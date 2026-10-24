package com.jpbazaar.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductUpdateRequest(
        Long categoryId,

        @Size(max = 255, message = "Product name must not exceed 255 characters")
        String name,

        @Size(max = 2000, message = "Product description must not exceed 2000 characters")
        String description,

        @DecimalMin(value = "0.0", inclusive = true, message = "Price must be non-negative")
        BigDecimal price,

        @Min(value = 0, message = "Stock quantity must be non-negative")
        Integer stockQuantity,

        @Size(max = 100, message = "Brand must not exceed 100 characters")
        String brand,

        Boolean active
) {}
