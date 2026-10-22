package com.jpbazaar.controller;

import com.jpbazaar.dto.ApiResponse;
import com.jpbazaar.dto.request.ProductImageRequest;
import com.jpbazaar.dto.response.ProductImageResponse;
import com.jpbazaar.service.ProductImageService;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Product Images", description = "Endpoints for managing product catalog images")
public class ProductImageController {

    private final ProductImageService productImageService;

    public ProductImageController(ProductImageService productImageService) {
        this.productImageService = productImageService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Add image URL to product (Admin only)")
    public ResponseEntity<ApiResponse<ProductImageResponse>> addImage(
            @PathVariable Long productId,
            @Valid @RequestBody ProductImageRequest request
    ) {
        ProductImageResponse response = productImageService.addImage(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product image added successfully", response));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Upload image file for product (Admin only)")
    public ResponseEntity<ApiResponse<ProductImageResponse>> uploadImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isPrimary", defaultValue = "false") boolean isPrimary
    ) {
        ProductImageResponse response = productImageService.uploadImage(productId, file, isPrimary);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product image uploaded successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all images for a product (Public)")
    public ResponseEntity<ApiResponse<List<ProductImageResponse>>> getProductImages(
            @PathVariable Long productId
    ) {
        List<ProductImageResponse> images = productImageService.getProductImages(productId);
        return ResponseEntity.ok(ApiResponse.success(images));
    }

    @PutMapping("/{imageId}/primary")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Set image as primary thumbnail (Admin only)")
    public ResponseEntity<ApiResponse<ProductImageResponse>> setPrimaryImage(
            @PathVariable Long productId,
            @PathVariable Long imageId
    ) {
        ProductImageResponse response = productImageService.setPrimaryImage(productId, imageId);
        return ResponseEntity.ok(ApiResponse.success("Primary image updated successfully", response));
    }

    @DeleteMapping("/{imageId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete product image (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteProductImage(
            @PathVariable Long productId,
            @PathVariable Long imageId
    ) {
        productImageService.deleteProductImage(productId, imageId);
        return ResponseEntity.ok(ApiResponse.success("Product image deleted successfully", null));
    }
}
