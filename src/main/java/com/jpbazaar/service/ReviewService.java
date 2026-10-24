package com.jpbazaar.service;

import com.jpbazaar.dto.request.ReviewRequest;
import com.jpbazaar.dto.request.UpdateReviewRequest;
import com.jpbazaar.dto.response.PageResponse;
import com.jpbazaar.dto.response.ProductReviewSummaryResponse;
import com.jpbazaar.dto.response.ReviewResponse;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

    ReviewResponse addReview(String userEmail, ReviewRequest request);

    ReviewResponse updateReview(String userEmail, Long reviewId, UpdateReviewRequest request);

    void deleteReview(String userEmail, Long reviewId, boolean isAdmin);

    PageResponse<ReviewResponse> getProductReviews(Long productId, Pageable pageable);

    ProductReviewSummaryResponse getProductReviewSummary(Long productId, Pageable pageable);
}
