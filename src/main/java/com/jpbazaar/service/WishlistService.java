package com.jpbazaar.service;

import com.jpbazaar.dto.request.AddToWishlistRequest;
import com.jpbazaar.dto.response.WishlistResponse;

public interface WishlistService {

    WishlistResponse getWishlist(String userEmail);

    WishlistResponse addToWishlist(String userEmail, AddToWishlistRequest request);

    WishlistResponse removeWishlistItem(String userEmail, Long wishlistItemId);

    WishlistResponse removeProductFromWishlist(String userEmail, Long productId);

    WishlistResponse clearWishlist(String userEmail);
}
