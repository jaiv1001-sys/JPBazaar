package com.jpbazaar.service;

import com.jpbazaar.dto.request.AddToCartRequest;
import com.jpbazaar.dto.request.UpdateCartItemRequest;
import com.jpbazaar.dto.response.CartResponse;

public interface CartService {

    CartResponse getCart(String userEmail);

    CartResponse addToCart(String userEmail, AddToCartRequest request);

    CartResponse updateCartItemQuantity(String userEmail, Long cartItemId, UpdateCartItemRequest request);

    CartResponse removeCartItem(String userEmail, Long cartItemId);

    CartResponse clearCart(String userEmail);
}
