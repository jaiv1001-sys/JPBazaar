package com.jpbazaar.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Product catalog creation payload")
public record ProductCreateRequest(
        @Schema(description = "Category ID to which the product belongs", example = "1")
        @NotNull(message = "Category ID is required")
        Long categoryId,

        @Schema(description = "Product name/title", example = "Wireless Noise-Cancelling Headphones")
        @NotBlank(message = "Product name is required")
        @Size(max = 255, message = "Product name must not exceed 255 characters")
        String name,

        @Schema(description = "Detailed product description", example = "High fidelity audio with 30-hour battery life")
        @Size(max = 2000, message = "Product description must not exceed 2000 characters")
        String description,

        @Schema(description = "Unit selling price in standard currency", example = "199.99")
        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Price must be non-negative")
        BigDecimal price,

        @Schema(description = "Original retail price / MRP for discount calculations", example = "249.99")
        @DecimalMin(value = "0.0", inclusive = true, message = "Original price must be non-negative")
        BigDecimal originalPrice,

        @Schema(description = "Technical specifications or attributes (JSON or formatted text)", example = "{\"Battery\": \"30h\", \"Connectivity\": \"Bluetooth 5.2\"}")
        String specifications,

        @Schema(description = "Available warehouse stock quantity", example = "50")
        @NotNull(message = "Stock quantity is required")
        @Min(value = 0, message = "Stock quantity must be non-negative")
        Integer stockQuantity,

        @Schema(description = "Unique Stock Keeping Unit (SKU) identifier", example = "TECH-HP-001")
        @NotBlank(message = "SKU is required")
        @Size(max = 100, message = "SKU must not exceed 100 characters")
        String sku,

        @Schema(description = "Manufacturer brand name", example = "AudioPhile")
        @Size(max = 100, message = "Brand must not exceed 100 characters")
        String brand,

        @Schema(description = "Flag indicating if product is visible and active for purchase", example = "true")
        Boolean active
) {
    public ProductCreateRequest(Long categoryId, String name, String description, BigDecimal price, Integer stockQuantity, String sku, String brand, Boolean active) {
        this(categoryId, name, description, price, null, null, stockQuantity, sku, brand, active);
    }
}
