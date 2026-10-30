package com.jpbazaar.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Low stock alert information for replenishment management")
public record LowStockProductResponse(
        @Schema(description = "Product unique identifier", example = "105")
        Long id,

        @Schema(description = "Product name", example = "Wireless Noise-Canceling Headphones")
        String name,

        @Schema(description = "Product SKU", example = "AUDIO-HEADPHONE-NC")
        String sku,

        @Schema(description = "Brand name", example = "Sony")
        String brand,

        @Schema(description = "Associated category name", example = "Electronics")
        String categoryName,

        @Schema(description = "Current retail price", example = "14999.00")
        BigDecimal price,

        @Schema(description = "Current remaining inventory quantity", example = "3")
        Integer stockQuantity
) {
}
