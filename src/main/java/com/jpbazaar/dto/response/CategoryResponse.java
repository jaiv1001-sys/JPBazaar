package com.jpbazaar.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Schema(description = "Category details")
public record CategoryResponse(
        @Schema(description = "Category unique identifier", example = "1")
        Long id,

        @Schema(description = "Category name", example = "Electronics")
        String name,

        @Schema(description = "Category description", example = "Consumer electronics, gadgets, and peripherals")
        String description,

        @Schema(description = "Creation timestamp")
        OffsetDateTime createdAt
) implements Serializable {}
