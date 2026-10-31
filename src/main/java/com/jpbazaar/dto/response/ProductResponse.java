package com.jpbazaar.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Schema(description = "Product catalog response details")
public record ProductResponse(
        @Schema(description = "Product unique identifier", example = "1")
        Long id,

        @Schema(description = "Category ID", example = "1")
        Long categoryId,

        @Schema(description = "Category display name", example = "Electronics")
        String categoryName,

        @Schema(description = "Product name", example = "Wireless Noise-Cancelling Headphones")
        String name,

        @Schema(description = "Product description", example = "High fidelity audio with 30-hour battery life")
        String description,

        @Schema(description = "Unit selling price in standard currency", example = "199.99")
        BigDecimal price,

        @Schema(description = "Original retail price / MRP", example = "249.99")
        BigDecimal originalPrice,

        @Schema(description = "Discount percentage when originalPrice > price", example = "20")
        Integer discountPercentage,

        @Schema(description = "Technical specifications or attributes (JSON/text)", example = "{\"Battery\": \"30h\"}")
        String specifications,

        @Schema(description = "Available warehouse stock quantity", example = "50")
        Integer stockQuantity,

        @Schema(description = "Stock Keeping Unit (SKU) code", example = "TECH-HP-001")
        String sku,

        @Schema(description = "Brand name", example = "AudioPhile")
        String brand,

        @Schema(description = "Average customer review rating (1.00 - 5.00)", example = "4.85")
        BigDecimal rating,

        @Schema(description = "Active catalog status", example = "true")
        Boolean active,

        @Schema(description = "List of product image URLs")
        List<String> imageUrls,

        @Schema(description = "Product creation timestamp")
        OffsetDateTime createdAt,

        @Schema(description = "Product last updated timestamp")
        OffsetDateTime updatedAt
) implements Serializable {

    public ProductResponse(
            Long id,
            Long categoryId,
            String categoryName,
            String name,
            String description,
            BigDecimal price,
            Integer stockQuantity,
            String sku,
            String brand,
            BigDecimal rating,
            Boolean active,
            List<String> imageUrls,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this(id, categoryId, categoryName, name, description, price, null, null, null,
             stockQuantity, sku, brand, rating, active, imageUrls, createdAt, updatedAt);
    }
}
