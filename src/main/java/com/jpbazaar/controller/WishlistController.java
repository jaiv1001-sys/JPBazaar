package com.jpbazaar.controller;

import com.jpbazaar.dto.ApiResponse;
import com.jpbazaar.dto.request.AddToWishlistRequest;
import com.jpbazaar.dto.response.WishlistResponse;
import com.jpbazaar.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlist")
@Tag(name = "Wishlist", description = "Endpoints for managing user wishlist")
@SecurityRequirement(name = "bearerAuth")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    @Operation(summary = "Get current user's wishlist")
    public ResponseEntity<ApiResponse<WishlistResponse>> getWishlist(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        WishlistResponse response = wishlistService.getWishlist(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/items")
    @Operation(summary = "Add a product to wishlist")
    public ResponseEntity<ApiResponse<WishlistResponse>> addToWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddToWishlistRequest request
    ) {
        WishlistResponse response = wishlistService.addToWishlist(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product added to wishlist", response));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove item from wishlist by item ID")
    public ResponseEntity<ApiResponse<WishlistResponse>> removeWishlistItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long itemId
    ) {
        WishlistResponse response = wishlistService.removeWishlistItem(userDetails.getUsername(), itemId);
        return ResponseEntity.ok(ApiResponse.success("Item removed from wishlist", response));
    }

    @DeleteMapping("/products/{productId}")
    @Operation(summary = "Remove product from wishlist by product ID")
    public ResponseEntity<ApiResponse<WishlistResponse>> removeProductFromWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId
    ) {
        WishlistResponse response = wishlistService.removeProductFromWishlist(userDetails.getUsername(), productId);
        return ResponseEntity.ok(ApiResponse.success("Product removed from wishlist", response));
    }

    @DeleteMapping
    @Operation(summary = "Clear all items from wishlist")
    public ResponseEntity<ApiResponse<WishlistResponse>> clearWishlist(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        WishlistResponse response = wishlistService.clearWishlist(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Wishlist cleared successfully", response));
    }
}
