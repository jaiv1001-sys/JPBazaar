package com.jpbazaar.controller;

import com.jpbazaar.dto.ApiResponse;
import com.jpbazaar.dto.request.ProductImageRequest;
import com.jpbazaar.dto.response.ProductImageResponse;
import com.jpbazaar.service.ProductImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/images")
@Tag(name = "Product Images", description = "Image gallery upload, storage management, and primary thumbnail assignment")
public class ProductImageController {

    private final ProductImageService productImageService;

    public ProductImageController(ProductImageService productImageService) {
        this.productImageService = productImageService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Add image URL to product (Admin only)", description = "Associates an external image URL with a product. Automatically assigns as primary if first image or if requested.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Product image added successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden (Admin role required)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ApiResponse<ProductImageResponse>> addImage(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long productId,
            @Valid @RequestBody ProductImageRequest request
    ) {
        ProductImageResponse response = productImageService.addImage(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product image added successfully", response));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Upload image file for product (Admin only)", description = "Uploads a multipart image file (JPG, PNG, WebP) to local/cloud storage and records the image metadata.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Product image uploaded successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid or empty image file"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden (Admin role required)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ApiResponse<ProductImageResponse>> uploadImage(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long productId,
            @Parameter(description = "Image file to upload") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Set as primary thumbnail") @RequestParam(value = "isPrimary", defaultValue = "false") boolean isPrimary
    ) {
        ProductImageResponse response = productImageService.uploadImage(productId, file, isPrimary);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product image uploaded successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all images for a product (Public)", description = "Retrieves the full image gallery for a product ordered with primary image first.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Images retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ApiResponse<List<ProductImageResponse>>> getProductImages(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long productId
    ) {
        List<ProductImageResponse> images = productImageService.getProductImages(productId);
        return ResponseEntity.ok(ApiResponse.success(images));
    }

    @PutMapping("/{imageId}/primary")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Set image as primary thumbnail (Admin only)", description = "Designates an existing image as the primary catalog thumbnail and unsets previous primary image.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Primary image updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden (Admin role required)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product or image not found")
    })
    public ResponseEntity<ApiResponse<ProductImageResponse>> setPrimaryImage(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long productId,
            @Parameter(description = "Image ID", example = "1") @PathVariable Long imageId
    ) {
        ProductImageResponse response = productImageService.setPrimaryImage(productId, imageId);
        return ResponseEntity.ok(ApiResponse.success("Primary image updated successfully", response));
    }

    @DeleteMapping("/{imageId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Delete product image (Admin only)", description = "Deletes image file from storage and removes database entity. Re-assigns new primary thumbnail if needed.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product image deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden (Admin role required)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product or image not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteProductImage(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long productId,
            @Parameter(description = "Image ID", example = "1") @PathVariable Long imageId
    ) {
        productImageService.deleteProductImage(productId, imageId);
        return ResponseEntity.ok(ApiResponse.success("Product image deleted successfully", null));
    }
}
