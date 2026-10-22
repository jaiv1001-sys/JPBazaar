package com.jpbazaar.controller;

import com.jpbazaar.service.storage.ImageStorageService;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Images", description = "Endpoints for serving uploaded images")
public class ImageController {

    private final ImageStorageService imageStorageService;

    public ImageController(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    @GetMapping("/{*filePath}")
    @Operation(summary = "Serve uploaded image file (Public)")
    public ResponseEntity<Resource> serveImage(@PathVariable String filePath) {
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
