package com.jpbazaar.service.impl;

import com.jpbazaar.dto.request.AddToCartRequest;
import com.jpbazaar.dto.request.UpdateCartItemRequest;
import com.jpbazaar.dto.response.CartItemResponse;
import com.jpbazaar.dto.response.CartResponse;
import com.jpbazaar.entity.*;
import com.jpbazaar.exception.InsufficientStockException;
import com.jpbazaar.exception.InvalidRequestException;
import com.jpbazaar.exception.ResourceNotFoundException;
import com.jpbazaar.exception.UnauthorizedException;
import com.jpbazaar.repository.CartItemRepository;
import com.jpbazaar.repository.CartRepository;
import com.jpbazaar.repository.ProductRepository;
import com.jpbazaar.repository.UserRepository;
import com.jpbazaar.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(String userEmail) {
        Cart cart = getOrCreateCart(userEmail);
        return mapToCartResponse(cart);
    }

    @Override
    public CartResponse addToCart(String userEmail, AddToCartRequest request) {
        Cart cart = getOrCreateCart(userEmail);

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + request.productId()));

        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new InvalidRequestException("Product is inactive and cannot be added to cart: " + product.getName());
        }

        // Check if product is already in the cart to prevent duplicate entries
        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            int newTotalQuantity = existingItem.getQuantity() + request.quantity();

            if (newTotalQuantity > product.getStockQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product '" + product.getName()
                        + "'. Available: " + product.getStockQuantity() + ", Total in cart would be: " + newTotalQuantity);
            }

            existingItem.setQuantity(newTotalQuantity);
            cartItemRepository.save(existingItem);
        } else {
            if (request.quantity() > product.getStockQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product '" + product.getName()
                        + "'. Available: " + product.getStockQuantity() + ", Requested: " + request.quantity());
            }

            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.quantity())
                    .build();

            cart.addItem(newItem);
            cartItemRepository.save(newItem);
        }

        return mapToCartResponse(cart);
    }

    @Override
    public CartResponse updateCartItemQuantity(String userEmail, Long cartItemId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateCart(userEmail);

        CartItem item = cartItemRepository.findByIdAndCartId(cartItemId, cart.getId())
                .orElseThrow(() -> new UnauthorizedException("Cart item with ID " + cartItemId + " does not belong to your cart or does not exist"));

        Product product = item.getProduct();
        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new InvalidRequestException("Product is inactive: " + product.getName());
        }

        if (request.quantity() > product.getStockQuantity()) {
            throw new InsufficientStockException("Insufficient stock for product '" + product.getName()
                    + "'. Available: " + product.getStockQuantity() + ", Requested: " + request.quantity());
        }

        item.setQuantity(request.quantity());
        cartItemRepository.save(item);

        return mapToCartResponse(cart);
    }

    @Override
    public CartResponse removeCartItem(String userEmail, Long cartItemId) {
        Cart cart = getOrCreateCart(userEmail);

        CartItem item = cartItemRepository.findByIdAndCartId(cartItemId, cart.getId())
                .orElseThrow(() -> new UnauthorizedException("Cart item with ID " + cartItemId + " does not belong to your cart or does not exist"));

        cart.removeItem(item);
        cartItemRepository.delete(item);

        return mapToCartResponse(cart);
    }

    @Override
    public CartResponse clearCart(String userEmail) {
        Cart cart = getOrCreateCart(userEmail);

        cartItemRepository.deleteByCartId(cart.getId());
        cart.clearItems();

        return mapToCartResponse(cart);
    }

    private Cart getOrCreateCart(String userEmail) {
        return cartRepository.findByUserEmailWithItems(userEmail)
                .orElseGet(() -> {
                    User user = userRepository.findByEmail(userEmail)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    private CartResponse mapToCartResponse(Cart cart) {
        List<CartItemResponse> itemResponses = new ArrayList<>();
        int totalItemCount = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        if (cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                Product product = item.getProduct();
                BigDecimal unitPrice = product.getPrice();
                BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

                String primaryImageUrl = null;
                if (product.getImages() != null && !product.getImages().isEmpty()) {
                    primaryImageUrl = product.getImages().stream()
                            .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                            .map(ProductImage::getImageUrl)
                            .findFirst()
                            .orElse(product.getImages().get(0).getImageUrl());
                }

                itemResponses.add(new CartItemResponse(
                        item.getId(),
                        product.getId(),
                        product.getName(),
                        product.getSku(),
                        unitPrice,
                        item.getQuantity(),
                        subtotal,
                        primaryImageUrl
                ));

                totalItemCount += item.getQuantity();
                totalAmount = totalAmount.add(subtotal);
            }
        }

        return new CartResponse(
                cart.getId(),
                cart.getUser().getId(),
                itemResponses,
                totalItemCount,
                totalAmount
        );
    }
}
