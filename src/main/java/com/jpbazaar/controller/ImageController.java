package com.jpbazaar.controller;

import com.jpbazaar.service.storage.ImageStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/images")
@Tag(name = "Media Storage", description = "Public static media resource serving")
public class ImageController {

    private final ImageStorageService imageStorageService;

    public ImageController(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    @GetMapping("/{*filePath}")
    @Operation(summary = "Serve uploaded image file", description = "Serves an uploaded image resource with client-side caching headers.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Image file retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Image file not found")
    })
    public ResponseEntity<Resource> serveImage(
            @Parameter(description = "Relative path of the image file", example = "products/1/thumbnail.jpg")
            @PathVariable String filePath
    ) {
        String cleanPath = (filePath != null && filePath.startsWith("/")) ? filePath.substring(1) : filePath;
        Resource file = imageStorageService.loadAsResource(cleanPath);
        String contentType = imageStorageService.getContentType(file.getFilename());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS).cachePublic())
                .body(file);
    }
}
