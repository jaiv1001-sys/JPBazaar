package com.jpbazaar.controller;

import com.jpbazaar.dto.ApiResponse;
import com.jpbazaar.dto.request.AddToCartRequest;
import com.jpbazaar.dto.request.UpdateCartItemRequest;
import com.jpbazaar.dto.response.CartResponse;
import com.jpbazaar.service.CartService;
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
@RequestMapping("/api/cart")
@Tag(name = "Cart", description = "Endpoints for managing the active shopping cart")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    @Operation(summary = "Get current user's shopping cart")
    public ResponseEntity<ApiResponse<CartResponse>> getCart(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        CartResponse cart = cartService.getCart(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @PostMapping("/items")
    @Operation(summary = "Add a product to cart (updates quantity if product already in cart)")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddToCartRequest request
    ) {
        CartResponse cart = cartService.addToCart(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product added to cart", cart));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update quantity of a cart item")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        CartResponse cart = cartService.updateCartItemQuantity(userDetails.getUsername(), itemId, request);
        return ResponseEntity.ok(ApiResponse.success("Cart item updated", cart));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove an item from cart")
    public ResponseEntity<ApiResponse<CartResponse>> removeCartItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long itemId
    ) {
        CartResponse cart = cartService.removeCartItem(userDetails.getUsername(), itemId);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart", cart));
    }

    @DeleteMapping
    @Operation(summary = "Clear all items from shopping cart")
    public ResponseEntity<ApiResponse<CartResponse>> clearCart(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        CartResponse cart = cartService.clearCart(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Cart cleared successfully", cart));
    }
}
