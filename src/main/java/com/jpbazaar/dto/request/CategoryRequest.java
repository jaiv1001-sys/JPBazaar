package com.jpbazaar.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Product category creation and update payload")
public record CategoryRequest(
        @Schema(description = "Category name (must be unique)", example = "Electronics")
        @NotBlank(message = "Category name is required")
        @Size(max = 100, message = "Category name must not exceed 100 characters")
        String name,

        @Schema(description = "Detailed category description", example = "Consumer electronics, gadgets, and peripherals")
        @Size(max = 500, message = "Category description must not exceed 500 characters")
        String description
) {}
