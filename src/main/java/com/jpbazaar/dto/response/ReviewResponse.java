package com.jpbazaar.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

@Schema(description = "Customer product review response")
public record ReviewResponse(
        @Schema(description = "Review ID", example = "1")
        Long id,

        @Schema(description = "Reviewed product ID", example = "1")
        Long productId,

        @Schema(description = "Review author user ID", example = "1")
        Long userId,

        @Schema(description = "Author display name", example = "Jane Doe")
        String userName,

        @Schema(description = "Rating score (1 to 5)", example = "5")
        Integer rating,

        @Schema(description = "Review commentary", example = "Outstanding sound quality and comfort.")
        String comment,

        @Schema(description = "Review timestamp")
        OffsetDateTime createdAt,

        @Schema(description = "Last update timestamp")
        OffsetDateTime updatedAt
) {}
