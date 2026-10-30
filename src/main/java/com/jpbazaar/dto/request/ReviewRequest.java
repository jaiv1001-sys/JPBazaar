package com.jpbazaar.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Product review submission payload")
public record ReviewRequest(
        @Schema(description = "ID of the product being reviewed", example = "1")
        @NotNull(message = "Product ID is required")
        Long productId,

        @Schema(description = "Rating score from 1 to 5 stars", example = "5")
        @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Rating must be between 1 and 5")
        @Max(value = 5, message = "Rating must be between 1 and 5")
        Integer rating,

        @Schema(description = "Customer review text and feedback", example = "Outstanding sound quality and super comfortable for all-day wear!")
        @Size(max = 2000, message = "Comment cannot exceed 2000 characters")
        String comment
) {}
