package com.jpbazaar.service.impl;

import com.jpbazaar.dto.request.AddToWishlistRequest;
import com.jpbazaar.dto.response.WishlistItemResponse;
import com.jpbazaar.dto.response.WishlistResponse;
import com.jpbazaar.entity.*;
import com.jpbazaar.exception.DuplicateResourceException;
import com.jpbazaar.exception.ResourceNotFoundException;
import com.jpbazaar.exception.UnauthorizedException;
import com.jpbazaar.repository.ProductRepository;
import com.jpbazaar.repository.UserRepository;
import com.jpbazaar.repository.WishlistItemRepository;
import com.jpbazaar.repository.WishlistRepository;
import com.jpbazaar.service.WishlistService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public WishlistServiceImpl(
            WishlistRepository wishlistRepository,
            WishlistItemRepository wishlistItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository
    ) {
        this.wishlistRepository = wishlistRepository;
        this.wishlistItemRepository = wishlistItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public WishlistResponse getWishlist(String userEmail) {
        Wishlist wishlist = getOrCreateWishlist(userEmail);
        return mapToWishlistResponse(wishlist);
    }

    @Override
    public WishlistResponse addToWishlist(String userEmail, AddToWishlistRequest request) {
        Wishlist wishlist = getOrCreateWishlist(userEmail);

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + request.productId()));

        // Prevent duplicate wishlist items
        if (wishlistItemRepository.existsByWishlistIdAndProductId(wishlist.getId(), product.getId())) {
            throw new DuplicateResourceException("Product '" + product.getName() + "' is already in your wishlist");
        }

        WishlistItem item = WishlistItem.builder()
                .wishlist(wishlist)
                .product(product)
                .build();

        wishlist.addItem(item);
        wishlistItemRepository.save(item);

        return mapToWishlistResponse(wishlist);
    }

    @Override
    public WishlistResponse removeWishlistItem(String userEmail, Long wishlistItemId) {
        Wishlist wishlist = getOrCreateWishlist(userEmail);

        WishlistItem item = wishlistItemRepository.findByIdAndWishlistId(wishlistItemId, wishlist.getId())
                .orElseThrow(() -> new UnauthorizedException("Wishlist item with ID " + wishlistItemId + " does not belong to your wishlist or does not exist"));

        wishlist.removeItem(item);
        wishlistItemRepository.delete(item);

        return mapToWishlistResponse(wishlist);
    }

    @Override
    public WishlistResponse removeProductFromWishlist(String userEmail, Long productId) {
        Wishlist wishlist = getOrCreateWishlist(userEmail);

        WishlistItem item = wishlistItemRepository.findByWishlistIdAndProductId(wishlist.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + productId + " is not in your wishlist"));

        wishlist.removeItem(item);
        wishlistItemRepository.delete(item);

        return mapToWishlistResponse(wishlist);
    }

    @Override
    public WishlistResponse clearWishlist(String userEmail) {
        Wishlist wishlist = getOrCreateWishlist(userEmail);

        wishlistItemRepository.deleteByWishlistId(wishlist.getId());
        wishlist.clearItems();

        return mapToWishlistResponse(wishlist);
    }

    private Wishlist getOrCreateWishlist(String userEmail) {
        return wishlistRepository.findByUserEmailWithItems(userEmail)
                .orElseGet(() -> {
                    User user = userRepository.findByEmail(userEmail)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

                    Wishlist newWishlist = Wishlist.builder()
                            .user(user)
                            .build();
                    return wishlistRepository.save(newWishlist);
                });
    }

    private WishlistResponse mapToWishlistResponse(Wishlist wishlist) {
        List<WishlistItemResponse> items = new ArrayList<>();

        if (wishlist.getItems() != null) {
            for (WishlistItem item : wishlist.getItems()) {
                Product product = item.getProduct();

                String primaryImageUrl = null;
                if (product.getImages() != null && !product.getImages().isEmpty()) {
                    primaryImageUrl = product.getImages().stream()
                            .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                            .map(ProductImage::getImageUrl)
                            .findFirst()
                            .orElse(product.getImages().get(0).getImageUrl());
                }

                boolean inStock = product.getStockQuantity() != null
                        && product.getStockQuantity() > 0
                        && Boolean.TRUE.equals(product.getActive());

                items.add(new WishlistItemResponse(
                        item.getId(),
                        product.getId(),
                        product.getName(),
                        product.getSku(),
                        product.getPrice(),
                        inStock,
                        primaryImageUrl,
                        item.getCreatedAt()
                ));
            }
        }

        return new WishlistResponse(
                wishlist.getId(),
                wishlist.getUser().getId(),
                items,
                items.size()
        );
    }
}
