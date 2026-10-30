package com.jpbazaar.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Add product to cart payload")
public record AddToCartRequest(
        @Schema(description = "Product ID to add", example = "1")
        @NotNull(message = "Product ID is required")
        Long productId,

        @Schema(description = "Quantity of items to add (min 1)", example = "2")
        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity
) {}
