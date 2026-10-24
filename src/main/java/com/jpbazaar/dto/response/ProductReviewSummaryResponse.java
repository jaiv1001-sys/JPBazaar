package com.jpbazaar.dto.response;

import java.math.BigDecimal;

public record ProductReviewSummaryResponse(
        Long productId,
        BigDecimal averageRating,
        long totalReviews,
        PageResponse<ReviewResponse> reviews
) {}
