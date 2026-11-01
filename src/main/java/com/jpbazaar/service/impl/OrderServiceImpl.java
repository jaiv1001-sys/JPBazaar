package com.jpbazaar.service.impl;

import com.jpbazaar.dto.request.CreateOrderRequest;
import com.jpbazaar.dto.request.UpdateOrderStatusRequest;
import com.jpbazaar.dto.response.*;
import com.jpbazaar.entity.*;
import com.jpbazaar.event.OrderPlacedEvent;
import com.jpbazaar.event.OrderStatusChangedEvent;
import com.jpbazaar.exception.*;
import com.jpbazaar.repository.*;
import com.jpbazaar.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

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
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final jakarta.persistence.EntityManager entityManager;
    private final CacheManager cacheManager;

    @Override
    @Transactional
    public OrderResponse placeOrder(String userEmail, CreateOrderRequest request) {
        log.info("Processing order placement for user: {}", userEmail);

        String cleanEmail = userEmail != null ? userEmail.trim() : "";
        User user = userRepository.findByEmailIgnoreCase(cleanEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + cleanEmail));

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
                .carrier("JPBazaar White-Glove Express")
                .totalAmount(BigDecimal.ZERO)
                .build();

        // Sort cart items by productId to enforce deterministic lock acquisition order and prevent deadlocks
        List<CartItem> sortedCartItems = cart.getItems().stream()
                .sorted(Comparator.comparing(item -> item.getProduct().getId()))
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

            order.addOrderItem(orderItem);
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        // Generate tracking number
        savedOrder.setTrackingNumber("JPB-" + savedOrder.getId() + "-" + System.currentTimeMillis() % 1000000);

        // Step 10: Create payment record
        PaymentMethod paymentMethod = request.paymentMethod() != null ? request.paymentMethod() : PaymentMethod.COD;
        String transactionId = "TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        Payment payment = Payment.builder()
                .order(savedOrder)
                .transactionId(transactionId)
                .amount(totalAmount)
                .method(paymentMethod)
                .status(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);
        savedOrder.setPayment(payment);

        // Record initial status history
        OrderStatusHistory initialHistory = OrderStatusHistory.builder()
                .order(savedOrder)
                .status(OrderStatus.PLACED)
                .notes("Acquisition order placed by patron")
                .updatedBy(user.getEmail())
                .build();
        savedOrder.addStatusHistory(initialHistory);

        savedOrder = orderRepository.save(savedOrder);

        // Step 11: Clear cart items atomically
        cartItemRepository.deleteByCartId(cart.getId());
        cart.clearItems();

        log.info("Order successfully placed with ID: {} for user: {}, total: {}, paymentMethod: {}",
                savedOrder.getId(), userEmail, totalAmount, paymentMethod);

        // Publish OrderPlacedEvent (decoupled, processed after commit)
        eventPublisher.publishEvent(new OrderPlacedEvent(savedOrder));

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
    public OrderTrackingResponse getOrderTracking(String userEmail, Long orderId, boolean isAdmin) {
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        if (!isAdmin && !order.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            throw new UnauthorizedException("You are not authorized to view tracking for this order");
        }

        List<OrderStatusHistory> historyEntities = orderStatusHistoryRepository.findByOrderIdOrderByCreatedAtAsc(orderId);
        List<OrderStatusHistoryResponse> historyResponses = historyEntities.stream()
                .map(OrderStatusHistoryResponse::fromEntity)
                .toList();

        List<TrackingCheckpointResponse> checkpoints = buildCheckpoints(order, historyEntities);

        List<OrderItemResponse> itemResponses = mapItemsToResponse(order.getItems());

        String formattedAddress = null;
        String recipientName = order.getUser() != null
                ? (order.getUser().getFirstName() + " " + order.getUser().getLastName()).trim()
                : null;
        if (order.getAddress() != null) {
            Address addr = order.getAddress();
            formattedAddress = addr.getAddressLine() + ", " + addr.getCity() + ", " +
                    addr.getState() + " " + addr.getPostalCode() + ", " + addr.getCountry();
        }

        return new OrderTrackingResponse(
                order.getId(),
                order.getStatus(),
                order.getCarrier() != null ? order.getCarrier() : "JPBazaar White-Glove Express",
                order.getTrackingNumber() != null ? order.getTrackingNumber() : "JPB-" + order.getId() + "-TRK",
                order.getEstimatedDeliveryDate(),
                order.getShippedAt(),
                order.getDeliveredAt(),
                order.getDeliveryNotes(),
                order.getTotalAmount(),
                recipientName,
                formattedAddress,
                itemResponses,
                checkpoints,
                historyResponses
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getUserOrders(String userEmail, OrderStatus status, Pageable pageable) {
        String cleanEmail = userEmail != null ? userEmail.trim() : "";
        User user = userRepository.findByEmailIgnoreCase(cleanEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + cleanEmail));

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
            orderPage = orderRepository.findAllWithDetails(pageable);
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

        // Update tracking attributes if provided
        if (request.carrier() != null && !request.carrier().isBlank()) {
            order.setCarrier(request.carrier().trim());
        }
        if (request.trackingNumber() != null && !request.trackingNumber().isBlank()) {
            order.setTrackingNumber(request.trackingNumber().trim());
        }
        if (request.estimatedDeliveryDate() != null) {
            order.setEstimatedDeliveryDate(request.estimatedDeliveryDate());
        }
        if (targetStatus == OrderStatus.SHIPPED && order.getShippedAt() == null) {
            order.setShippedAt(OffsetDateTime.now());
        }
        if (targetStatus == OrderStatus.DELIVERED && order.getDeliveredAt() == null) {
            order.setDeliveredAt(OffsetDateTime.now());
        }

        String note = request.notes() != null && !request.notes().isBlank()
                ? request.notes().trim()
                : "Status transitioned to " + targetStatus;

        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .status(targetStatus)
                .notes(note)
                .updatedBy("ADMIN")
                .build();
        order.addStatusHistory(history);

        Order updated = orderRepository.save(order);
        log.info("Order ID: {} status updated from {} to {}", orderId, currentStatus, targetStatus);

        // Publish OrderStatusChangedEvent
        eventPublisher.publishEvent(new OrderStatusChangedEvent(updated, currentStatus, targetStatus, note));

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
            payment.setTransactionId("REF-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());
            paymentRepository.save(payment);
            log.info("Order ID: {} payment refunded upon cancellation", orderId);
        }

        OrderStatus previousStatus = order.getStatus();
        order.setStatus(OrderStatus.CANCELLED);

        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .status(OrderStatus.CANCELLED)
                .notes("Order cancelled by " + (isAdmin ? "administrator" : "patron"))
                .updatedBy(userEmail)
                .build();
        order.addStatusHistory(history);

        Order updated = orderRepository.save(order);
        log.info("Order ID: {} cancelled by user: {}", orderId, userEmail);

        // Publish OrderStatusChangedEvent
        eventPublisher.publishEvent(new OrderStatusChangedEvent(updated, previousStatus, OrderStatus.CANCELLED, "Order cancelled"));

        return mapToOrderResponse(updated);
    }

    private List<TrackingCheckpointResponse> buildCheckpoints(Order order, List<OrderStatusHistory> history) {
        OrderStatus current = order.getStatus();
        Map<OrderStatus, OffsetDateTime> timestampMap = new EnumMap<>(OrderStatus.class);
        if (history != null) {
            for (OrderStatusHistory h : history) {
                timestampMap.putIfAbsent(h.getStatus(), h.getCreatedAt());
            }
        }

        if (current == OrderStatus.CANCELLED) {
            return List.of(
                    new TrackingCheckpointResponse(1, OrderStatus.PLACED, "Order Placed", "Order was placed", true, false, timestampMap.get(OrderStatus.PLACED)),
                    new TrackingCheckpointResponse(2, OrderStatus.CANCELLED, "Order Cancelled", "Order was cancelled", true, true, timestampMap.get(OrderStatus.CANCELLED))
            );
        }

        List<OrderStatus> standardFlow = List.of(
                OrderStatus.PLACED,
                OrderStatus.CONFIRMED,
                OrderStatus.PROCESSING,
                OrderStatus.SHIPPED,
                OrderStatus.OUT_FOR_DELIVERY,
                OrderStatus.DELIVERED
        );

        int currentIndex = standardFlow.indexOf(current);
        if (currentIndex < 0) {
            currentIndex = standardFlow.size() - 1;
        }

        List<TrackingCheckpointResponse> list = new ArrayList<>();
        for (int i = 0; i < standardFlow.size(); i++) {
            OrderStatus s = standardFlow.get(i);
            boolean completed = i <= currentIndex;
            boolean isCurrent = i == currentIndex;
            String label = getStepLabel(s);
            String desc = getStepDesc(s);
            OffsetDateTime ts = timestampMap.get(s);
            list.add(new TrackingCheckpointResponse(i + 1, s, label, desc, completed, isCurrent, ts));
        }
        return list;
    }

    private String getStepLabel(OrderStatus status) {
        return switch (status) {
            case PLACED -> "Order Placed";
            case CONFIRMED -> "Order Confirmed";
            case PROCESSING -> "Atelier Preparation";
            case SHIPPED -> "Dispatched";
            case OUT_FOR_DELIVERY -> "Out For Delivery";
            case DELIVERED -> "Delivered";
            case CANCELLED -> "Cancelled";
            case REFUNDED -> "Refunded";
        };
    }

    private String getStepDesc(OrderStatus status) {
        return switch (status) {
            case PLACED -> "Your order was successfully registered";
            case CONFIRMED -> "Payment verified and acquisition acknowledged";
            case PROCESSING -> "Item inspection and protective packaging";
            case SHIPPED -> "Package handed over to logistics carrier";
            case OUT_FOR_DELIVERY -> "Courier dispatched for arrival";
            case DELIVERED -> "Consignment delivered to client destination";
            case CANCELLED -> "Order has been cancelled";
            case REFUNDED -> "Settlement refunded to original payment method";
        };
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

    private List<OrderItemResponse> mapItemsToResponse(List<OrderItem> items) {
        if (items == null) {
            return Collections.emptyList();
        }
        return items.stream().map(item -> {
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
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = mapItemsToResponse(order.getItems());
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
