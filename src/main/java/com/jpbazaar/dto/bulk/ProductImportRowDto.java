package com.jpbazaar.dto.bulk;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Parsed row representation from bulk product file")
public record ProductImportRowDto(
        @Schema(description = "Line / row number in source file", example = "2")
        int rowNumber,

        @Schema(description = "Product display title", example = "iPhone 15 Pro Max")
        String name,

        @Schema(description = "Category name", example = "Electronics")
        String categoryName,

        @Schema(description = "Brand name", example = "Apple")
        String brand,

        @Schema(description = "Stock Keeping Unit", example = "ELEC-APPL-001")
        String sku,

        @Schema(description = "Unit selling price", example = "1199.00")
        BigDecimal price,

        @Schema(description = "Original retail price / MRP", example = "1399.00")
        BigDecimal originalPrice,

        @Schema(description = "Available warehouse stock quantity", example = "50")
        Integer stockQuantity,

        @Schema(description = "Detailed product description")
        String description,

        @Schema(description = "Technical specifications or attributes")
        String specifications,

        @Schema(description = "Primary product image URL")
        String imageUrl,

        @Schema(description = "Validation status", example = "true")
        boolean valid,

        @Schema(description = "List of validation errors or warnings for this row")
        List<String> errors
) {
    public ProductImportRowDto {
        if (errors == null) {
            errors = List.of();
        }
    }
}
