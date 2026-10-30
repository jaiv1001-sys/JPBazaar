package com.jpbazaar.controller;

import com.jpbazaar.dto.ApiResponse;
import com.jpbazaar.dto.request.AddToWishlistRequest;
import com.jpbazaar.dto.response.WishlistResponse;
import com.jpbazaar.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Wishlist", description = "Customer product bookmarking and wishlist item management")
@SecurityRequirement(name = "Bearer Authentication")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    @Operation(summary = "Get current user's wishlist", description = "Retrieves all wishlisted products with real-time stock availability and thumbnail image URLs. N+1 query safe via fetch joins.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Wishlist retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<WishlistResponse>> getWishlist(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        WishlistResponse response = wishlistService.getWishlist(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/items")
    @Operation(summary = "Add product to wishlist", description = "Bookmarks a product in user's wishlist. Returns HTTP 409 Conflict if product is already present in wishlist.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Product added to wishlist"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Product is already in wishlist")
    })
    public ResponseEntity<ApiResponse<WishlistResponse>> addToWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddToWishlistRequest request
    ) {
        WishlistResponse response = wishlistService.addToWishlist(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product added to wishlist", response));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove item from wishlist by item ID", description = "Deletes a specific wishlist entry. Validates that the item belongs to the authenticated user's wishlist (IDOR protected).")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Item removed from wishlist"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden (item does not belong to user wishlist)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Wishlist item not found")
    })
    public ResponseEntity<ApiResponse<WishlistResponse>> removeWishlistItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Wishlist Item ID", example = "1") @PathVariable Long itemId
    ) {
        WishlistResponse response = wishlistService.removeWishlistItem(userDetails.getUsername(), itemId);
        return ResponseEntity.ok(ApiResponse.success("Item removed from wishlist", response));
    }

    @DeleteMapping("/products/{productId}")
    @Operation(summary = "Remove product from wishlist by product ID", description = "Directly removes a product from user's wishlist using product ID. Convenient for toggle buttons on catalog cards.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product removed from wishlist"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not in wishlist")
    })
    public ResponseEntity<ApiResponse<WishlistResponse>> removeProductFromWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Product ID", example = "1") @PathVariable Long productId
    ) {
        WishlistResponse response = wishlistService.removeProductFromWishlist(userDetails.getUsername(), productId);
        return ResponseEntity.ok(ApiResponse.success("Product removed from wishlist", response));
    }

    @DeleteMapping
    @Operation(summary = "Clear entire wishlist", description = "Removes all bookmarked items from the user's wishlist.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Wishlist cleared successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<WishlistResponse>> clearWishlist(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        WishlistResponse response = wishlistService.clearWishlist(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Wishlist cleared successfully", response));
    }
}
