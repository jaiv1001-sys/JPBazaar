package com.jpbazaar.controller;

import com.jpbazaar.dto.ApiResponse;
import com.jpbazaar.dto.request.ReviewRequest;
import com.jpbazaar.dto.request.UpdateReviewRequest;
import com.jpbazaar.dto.response.ProductReviewSummaryResponse;
import com.jpbazaar.dto.response.ReviewResponse;
import com.jpbazaar.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Verified buyer product rating and review management")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/api/reviews")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Submit product review", description = "Allows a verified buyer (who has purchased and received/confirmed the item) to submit a rating (1-5) and comment. Enforces one review per user per product.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Review submitted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "User has not purchased product (Verified Buyer rule) or product inactive"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "User already submitted a review for this product")
    })
    public ResponseEntity<ApiResponse<ReviewResponse>> addReview(
            Authentication authentication,
            @Valid @RequestBody ReviewRequest request
    ) {
        ReviewResponse response = reviewService.addReview(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review submitted successfully", response));
    }

    @PutMapping("/api/reviews/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update review", description = "Updates rating and comment for an existing review. Author-only IDOR guard. Recalculates product average rating.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Review updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden (user is not review author)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Review not found")
    })
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @Parameter(description = "Review ID", example = "1") @PathVariable Long id,
            Authentication authentication,
            @Valid @RequestBody UpdateReviewRequest request
    ) {
        ReviewResponse response = reviewService.updateReview(authentication.getName(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Review updated successfully", response));
    }

    @DeleteMapping("/api/reviews/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Delete review", description = "Deletes a review. Allowed by author or Admin. Automatically recalculates product average rating.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Review deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden (user is neither author nor admin)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Review not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @Parameter(description = "Review ID", example = "1") @PathVariable Long id,
            Authentication authentication
    ) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        reviewService.deleteReview(authentication.getName(), id, isAdmin);
        return ResponseEntity.ok(ApiResponse.success("Review deleted successfully", null));
    }

    @GetMapping({"/api/products/{productId}/reviews", "/api/reviews/product/{productId}"})
    @Operation(summary = "Get product reviews and rating summary", description = "Public endpoint. Returns overall average rating, review count, and paginated review list with author names.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reviews retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ApiResponse<ProductReviewSummaryResponse>> getProductReviews(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long productId,
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort property", example = "createdAt") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "desc") @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        ProductReviewSummaryResponse summary = reviewService.getProductReviewSummary(productId, pageable);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }
}
