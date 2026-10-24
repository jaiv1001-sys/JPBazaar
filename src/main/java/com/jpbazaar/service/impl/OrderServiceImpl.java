package com.jpbazaar.service.impl;

import com.jpbazaar.dto.request.CreateOrderRequest;
import com.jpbazaar.dto.request.UpdateOrderStatusRequest;
import com.jpbazaar.dto.response.OrderItemResponse;
import com.jpbazaar.dto.response.OrderResponse;
import com.jpbazaar.dto.response.PageResponse;
import com.jpbazaar.dto.response.PaymentResponse;
import com.jpbazaar.entity.*;
import com.jpbazaar.exception.*;
import com.jpbazaar.repository.*;
import com.jpbazaar.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;
    private final jakarta.persistence.EntityManager entityManager;
    private final CacheManager cacheManager;

    @Override
    @Transactional
    public OrderResponse placeOrder(String userEmail, CreateOrderRequest request) {
        log.info("Processing order placement for user: {}", userEmail);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        Address address = addressRepository.findByIdAndUserId(request.addressId(), user.getId())
                .orElseThrow(() -> new InvalidRequestException("Shipping address not found or does not belong to user"));

        Cart cart = cartRepository.findByUserEmailWithItems(userEmail)
                .orElse(null);

        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new InvalidRequestException("Cannot place order with an empty cart");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        Order order = Order.builder()
                .user(user)
                .address(address)
                .status(OrderStatus.PLACED)
                .totalAmount(BigDecimal.ZERO)
                .build();

        List<OrderItem> orderItems = new ArrayList<>();

        // Sort cart items by productId to enforce deterministic lock acquisition order and prevent deadlocks
        List<CartItem> sortedCartItems = cart.getItems().stream()
                .sorted(java.util.Comparator.comparing(item -> item.getProduct().getId()))
                .toList();

        for (CartItem cartItem : sortedCartItems) {
            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + cartItem.getProduct().getId()));

            // Refresh entity state while holding an exclusive row-level pessimistic write lock (SELECT ... FOR UPDATE)
            entityManager.refresh(product, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);

            if (!Boolean.TRUE.equals(product.getActive())) {
                throw new InvalidRequestException("Product '" + product.getName() + "' is no longer available for purchase");
            }

            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product '" + product.getName() +
                        "'. Available: " + product.getStockQuantity() + ", requested: " + cartItem.getQuantity());
            }

            // Decrement inventory
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
            if (cacheManager != null && cacheManager.getCache("products") != null) {
                cacheManager.getCache("products").evict(product.getId());
            }

            BigDecimal price = product.getPrice();
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .productName(product.getName())
                    .price(price)
                    .quantity(cartItem.getQuantity())
                    .subtotal(subtotal)
                    .build();

            orderItems.add(orderItem);
            order.addOrderItem(orderItem);
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        // Step 10: Create payment record
        PaymentMethod paymentMethod = request.paymentMethod() != null ? request.paymentMethod() : PaymentMethod.COD;
        String transactionId = "TXN-" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        Payment payment = Payment.builder()
                .order(savedOrder)
                .transactionId(transactionId)
                .amount(totalAmount)
                .method(paymentMethod)
                .status(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);
        savedOrder.setPayment(payment);

        // Step 11: Clear cart items atomically
        cartItemRepository.deleteByCartId(cart.getId());
        cart.clearItems();

        log.info("Order successfully placed with ID: {} for user: {}, total: {}, paymentMethod: {}",
                savedOrder.getId(), userEmail, totalAmount, paymentMethod);
        return mapToOrderResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(String userEmail, Long orderId, boolean isAdmin) {
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        if (!isAdmin && !order.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            throw new UnauthorizedException("You are not authorized to view this order");
        }

        return mapToOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getUserOrders(String userEmail, OrderStatus status, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        Page<Order> orderPage;
        if (status != null) {
            orderPage = orderRepository.findByUserIdAndStatus(user.getId(), status, pageable);
        } else {
            orderPage = orderRepository.findByUserId(user.getId(), pageable);
        }

        return PageResponse.from(orderPage, this::mapToOrderResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getAllOrders(OrderStatus status, Pageable pageable) {
        Page<Order> orderPage;
        if (status != null) {
            orderPage = orderRepository.findByStatus(status, pageable);
        } else {
            orderPage = orderRepository.findAll(pageable);
        }

        return PageResponse.from(orderPage, this::mapToOrderResponse);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        OrderStatus currentStatus = order.getStatus();
        OrderStatus targetStatus = request.status();

        if (!currentStatus.canTransitionTo(targetStatus)) {
            throw new InvalidOrderStateException("Cannot transition order status from " + currentStatus + " to " + targetStatus);
        }

        // If transitioning to CANCELLED, restore inventory
        if (targetStatus == OrderStatus.CANCELLED && currentStatus != OrderStatus.CANCELLED) {
            restoreInventory(order);
        }

        order.setStatus(targetStatus);
        Order updated = orderRepository.save(order);
        log.info("Order ID: {} status updated from {} to {}", orderId, currentStatus, targetStatus);

        return mapToOrderResponse(updated);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(String userEmail, Long orderId, boolean isAdmin) {
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        if (!isAdmin && !order.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            throw new UnauthorizedException("You are not authorized to cancel this order");
        }

        if (!order.getStatus().isCancellable()) {
            throw new InvalidOrderStateException("Order cannot be cancelled in status: " + order.getStatus() +
                    ". Cancellation is only allowed when status is PLACED, CONFIRMED, or PROCESSING.");
        }

        restoreInventory(order);

        // Refund payment if already successfully processed
        if (order.getPayment() != null && order.getPayment().getStatus() == PaymentStatus.SUCCESS) {
            Payment payment = order.getPayment();
            payment.setStatus(PaymentStatus.REFUNDED);
            payment.setTransactionId("REF-" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());
            paymentRepository.save(payment);
            log.info("Order ID: {} payment refunded upon cancellation", orderId);
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order updated = orderRepository.save(order);
        log.info("Order ID: {} cancelled by user: {}", orderId, userEmail);

        return mapToOrderResponse(updated);
    }

    private void restoreInventory(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            if (product != null) {
                product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                productRepository.save(product);
                if (cacheManager != null && cacheManager.getCache("products") != null) {
                    cacheManager.getCache("products").evict(product.getId());
                }
                log.info("Restored {} units of product ID: {} (new stock: {})",
                        item.getQuantity(), product.getId(), product.getStockQuantity());
            }
        }
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream().map(item -> {
            String imageUrl = null;
            Product product = item.getProduct();
            if (product != null && product.getImages() != null && !product.getImages().isEmpty()) {
                imageUrl = product.getImages().stream()
                        .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                        .findFirst()
                        .map(ProductImage::getImageUrl)
                        .orElse(product.getImages().get(0).getImageUrl());
            }
            return new OrderItemResponse(
                    item.getId(),
                    product != null ? product.getId() : null,
                    item.getProductName(),
                    item.getPrice(),
                    item.getQuantity(),
                    item.getSubtotal(),
                    imageUrl
            );
        }).toList();

        int totalItemCount = order.getItems().stream().mapToInt(OrderItem::getQuantity).sum();

        String formattedAddress = null;
        Long addressId = null;
        if (order.getAddress() != null) {
            Address addr = order.getAddress();
            addressId = addr.getId();
            formattedAddress = addr.getAddressLine() + ", " + addr.getCity() + ", " +
                    addr.getState() + " " + addr.getPostalCode() + ", " + addr.getCountry();
        }

        PaymentResponse paymentResponse = null;
        if (order.getPayment() != null) {
            Payment p = order.getPayment();
            paymentResponse = new PaymentResponse(
                    p.getId(),
                    order.getId(),
                    p.getTransactionId(),
                    p.getAmount(),
                    p.getMethod(),
                    p.getStatus(),
                    p.getCreatedAt()
            );
        }

        return new OrderResponse(
                order.getId(),
                order.getUser() != null ? order.getUser().getId() : null,
                order.getUser() != null ? order.getUser().getEmail() : null,
                addressId,
                formattedAddress,
                order.getTotalAmount(),
                order.getStatus(),
                totalItemCount,
                itemResponses,
                paymentResponse,
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
