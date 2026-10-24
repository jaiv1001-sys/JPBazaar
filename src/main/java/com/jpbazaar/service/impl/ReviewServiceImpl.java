package com.jpbazaar.service.impl;

import com.jpbazaar.dto.request.ReviewRequest;
import com.jpbazaar.dto.request.UpdateReviewRequest;
import com.jpbazaar.dto.response.PageResponse;
import com.jpbazaar.dto.response.ProductReviewSummaryResponse;
import com.jpbazaar.dto.response.ReviewResponse;
import com.jpbazaar.entity.Product;
import com.jpbazaar.entity.Review;
import com.jpbazaar.entity.User;
import com.jpbazaar.exception.DuplicateResourceException;
import com.jpbazaar.exception.InvalidRequestException;
import com.jpbazaar.exception.ResourceNotFoundException;
import com.jpbazaar.exception.UnauthorizedException;
import com.jpbazaar.repository.OrderItemRepository;
import com.jpbazaar.repository.ProductRepository;
import com.jpbazaar.repository.ReviewRepository;
import com.jpbazaar.repository.UserRepository;
import com.jpbazaar.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final CacheManager cacheManager;

    @Override
    @Transactional
    public ReviewResponse addReview(String userEmail, ReviewRequest request) {
        log.info("Adding review for product ID: {} by user: {}", request.productId(), userEmail);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + request.productId()));

        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new InvalidRequestException("Cannot review an inactive product");
        }

        // Verified Buyer Business Rule
        boolean hasPurchased = orderItemRepository.hasUserPurchasedProduct(user.getId(), product.getId());
        if (!hasPurchased) {
            log.warn("User {} attempted to review product {} without purchasing", userEmail, product.getId());
            throw new InvalidRequestException("Only verified buyers who have purchased this product can submit a review");
        }

        // One review per user per product rule
        if (reviewRepository.existsByUserIdAndProductId(user.getId(), product.getId())) {
            throw new DuplicateResourceException("You have already submitted a review for this product");
        }

        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(request.rating())
                .comment(request.comment())
                .build();

        Review savedReview = reviewRepository.save(review);
        reviewRepository.flush();

        // Update product average rating
        updateProductAverageRating(product);

        log.info("Review successfully created with ID: {} for product: {}", savedReview.getId(), product.getId());
        return mapToReviewResponse(savedReview);
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(String userEmail, Long reviewId, UpdateReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));

        if (!review.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            throw new UnauthorizedException("You are not authorized to update this review");
        }

        review.setRating(request.rating());
        review.setComment(request.comment());
        Review updated = reviewRepository.save(review);
        reviewRepository.flush();

        updateProductAverageRating(review.getProduct());
        log.info("Review ID: {} updated by author: {}", reviewId, userEmail);

        return mapToReviewResponse(updated);
    }

    @Override
    @Transactional
    public void deleteReview(String userEmail, Long reviewId, boolean isAdmin) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));

        if (!isAdmin && !review.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            throw new UnauthorizedException("You are not authorized to delete this review");
        }

        Product product = review.getProduct();
        reviewRepository.delete(review);
        reviewRepository.flush();

        updateProductAverageRating(product);
        log.info("Review ID: {} deleted by: {}", reviewId, userEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getProductReviews(Long productId, Pageable pageable) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with ID: " + productId);
        }

        Page<Review> page = reviewRepository.findByProductIdWithUser(productId, pageable);
        return PageResponse.from(page, this::mapToReviewResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductReviewSummaryResponse getProductReviewSummary(Long productId, Pageable pageable) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with ID: " + productId);
        }

        Page<Review> page = reviewRepository.findByProductIdWithUser(productId, pageable);
        Double avg = reviewRepository.calculateAverageRatingByProductId(productId);
        BigDecimal averageRating = avg != null
                ? BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        return new ProductReviewSummaryResponse(
                productId,
                averageRating,
                page.getTotalElements(),
                PageResponse.from(page, this::mapToReviewResponse)
        );
    }

    private void updateProductAverageRating(Product product) {
        Double avg = reviewRepository.calculateAverageRatingByProductId(product.getId());
        BigDecimal averageRating = avg != null
                ? BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        product.setRating(averageRating);
        productRepository.save(product);

        if (cacheManager != null && cacheManager.getCache("products") != null) {
            cacheManager.getCache("products").evict(product.getId());
        }

        log.info("Product ID: {} average rating updated to: {}", product.getId(), averageRating);
    }

    private ReviewResponse mapToReviewResponse(Review review) {
        String userName = "Anonymous";
        if (review.getUser() != null) {
            userName = review.getUser().getFirstName() + " " + review.getUser().getLastName();
        }

        return new ReviewResponse(
                review.getId(),
                review.getProduct().getId(),
                review.getUser() != null ? review.getUser().getId() : null,
                userName,
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
