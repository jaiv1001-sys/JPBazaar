package com.jpbazaar.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Add product to wishlist payload")
public record AddToWishlistRequest(
        @Schema(description = "Product ID to bookmark in wishlist", example = "1")
        @NotNull(message = "Product ID is required")
        Long productId
) {}
