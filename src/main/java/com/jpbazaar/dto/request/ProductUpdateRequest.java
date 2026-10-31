package com.jpbazaar.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Product update payload (partial/patch updates allowed)")
public record ProductUpdateRequest(
        @Schema(description = "Category ID to update", example = "2")
        Long categoryId,

        @Schema(description = "Updated product name", example = "Wireless Noise-Cancelling Headphones Pro")
        @Size(max = 255, message = "Product name must not exceed 255 characters")
        String name,

        @Schema(description = "Updated product description", example = "Upgraded 40-hour battery life with ANC")
        @Size(max = 2000, message = "Product description must not exceed 2000 characters")
        String description,

        @Schema(description = "Updated unit selling price", example = "229.99")
        @DecimalMin(value = "0.0", inclusive = true, message = "Price must be non-negative")
        BigDecimal price,

        @Schema(description = "Updated original price / MRP", example = "279.99")
        @DecimalMin(value = "0.0", inclusive = true, message = "Original price must be non-negative")
        BigDecimal originalPrice,

        @Schema(description = "Updated technical specifications or attributes", example = "{\"Battery\": \"40h\", \"NoiseCancelling\": true}")
        String specifications,

        @Schema(description = "Updated warehouse stock quantity", example = "75")
        @Min(value = 0, message = "Stock quantity must be non-negative")
        Integer stockQuantity,

        @Schema(description = "Updated manufacturer brand name", example = "AudioPhile Pro")
        @Size(max = 100, message = "Brand must not exceed 100 characters")
        String brand,

        @Schema(description = "Active status flag", example = "true")
        Boolean active
) {
    public ProductUpdateRequest(Long categoryId, String name, String description, BigDecimal price, Integer stockQuantity, String brand, Boolean active) {
        this(categoryId, name, description, price, null, null, stockQuantity, brand, active);
    }
}
