package com.jpbazaar.service.impl;

import com.jpbazaar.dto.request.PaymentProcessRequest;
import com.jpbazaar.dto.response.PaymentResponse;
import com.jpbazaar.entity.*;
import com.jpbazaar.event.OrderStatusChangedEvent;
import com.jpbazaar.event.PaymentStatusEvent;
import com.jpbazaar.event.RefundCompletedEvent;
import com.jpbazaar.exception.*;
import com.jpbazaar.repository.OrderRepository;
import com.jpbazaar.repository.PaymentRepository;
import com.jpbazaar.service.PaymentService;
import com.jpbazaar.service.payment.PaymentGateway;
import com.jpbazaar.service.payment.PaymentGatewayRequest;
import com.jpbazaar.service.payment.PaymentGatewayResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentGateway paymentGateway;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(noRollbackFor = PaymentFailedException.class)
    public PaymentResponse processPayment(String userEmail, PaymentProcessRequest request) {
        log.info("Processing payment for order ID: {}, user: {}", request.orderId(), userEmail);

        Order order = orderRepository.findByIdWithDetails(request.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + request.orderId()));

        if (!order.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            throw new UnauthorizedException("You are not authorized to process payment for this order");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException("Cannot process payment for a cancelled order");
        }

        Payment existingPayment = paymentRepository.findByOrderId(order.getId()).orElse(null);
        if (existingPayment != null && existingPayment.getStatus() == PaymentStatus.SUCCESS) {
            throw new InvalidRequestException("Payment has already been successfully completed for this order");
        }

        boolean simulateFailure = Boolean.TRUE.equals(request.simulateFailure());
        PaymentGatewayRequest gatewayRequest = new PaymentGatewayRequest(
                order.getId(),
                order.getTotalAmount(),
                request.paymentMethod(),
                simulateFailure
        );

        PaymentGatewayResult result = paymentGateway.processPayment(gatewayRequest);

        Payment payment = existingPayment != null ? existingPayment : Payment.builder().order(order).build();
        payment.setAmount(order.getTotalAmount());
        payment.setMethod(request.paymentMethod());
        payment.setTransactionId(result.transactionId());
        payment.setStatus(result.status());

        Payment savedPayment = paymentRepository.save(payment);
        order.setPayment(savedPayment);

        if (!result.success()) {
            log.warn("Payment failed for order ID: {}. Reason: {}", order.getId(), result.message());
            eventPublisher.publishEvent(new PaymentStatusEvent(order, savedPayment, false, result.message()));
            throw new PaymentFailedException(result.message());
        }

        // If payment succeeded and order is in PLACED state, advance order to CONFIRMED
        if (result.status() == PaymentStatus.SUCCESS && order.getStatus() == OrderStatus.PLACED) {
            OrderStatus previousStatus = order.getStatus();
            order.setStatus(OrderStatus.CONFIRMED);
            OrderStatusHistory history = OrderStatusHistory.builder()
                    .order(order)
                    .status(OrderStatus.CONFIRMED)
                    .notes("Payment completed via " + savedPayment.getMethod() + " (Txn: " + savedPayment.getTransactionId() + ")")
                    .updatedBy("PAYMENT_GATEWAY")
                    .build();
            order.addStatusHistory(history);
            orderRepository.save(order);
            log.info("Order ID: {} auto-transitioned to CONFIRMED following successful payment", order.getId());
            eventPublisher.publishEvent(new OrderStatusChangedEvent(order, previousStatus, OrderStatus.CONFIRMED, "Payment completed"));
        }

        eventPublisher.publishEvent(new PaymentStatusEvent(order, savedPayment, true, null));

        log.info("Payment successfully processed for order ID: {}, txn: {}, status: {}",
                order.getId(), savedPayment.getTransactionId(), savedPayment.getStatus());
        return mapToPaymentResponse(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(String userEmail, Long orderId, boolean isAdmin) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found for order ID: " + orderId));

        if (!isAdmin && !payment.getOrder().getUser().getEmail().equalsIgnoreCase(userEmail)) {
            throw new UnauthorizedException("You are not authorized to view payment details for this order");
        }

        return mapToPaymentResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse refundPayment(String userEmail, Long orderId, boolean isAdmin) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found for order ID: " + orderId));

        if (!isAdmin && !payment.getOrder().getUser().getEmail().equalsIgnoreCase(userEmail)) {
            throw new UnauthorizedException("You are not authorized to refund payment for this order");
        }

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new InvalidRequestException("Only successful payments can be refunded. Current status: " + payment.getStatus());
        }

        PaymentGatewayResult result = paymentGateway.refundPayment(payment.getTransactionId(), payment.getAmount());
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setTransactionId(result.transactionId());
        Payment updated = paymentRepository.save(payment);

        Order order = payment.getOrder();
        if (order != null) {
            OrderStatus previousStatus = order.getStatus();
            if (previousStatus.canTransitionTo(OrderStatus.REFUNDED)) {
                order.setStatus(OrderStatus.REFUNDED);
                OrderStatusHistory history = OrderStatusHistory.builder()
                        .order(order)
                        .status(OrderStatus.REFUNDED)
                        .notes("Refund processed: " + updated.getTransactionId())
                        .updatedBy(userEmail)
                        .build();
                order.addStatusHistory(history);
                orderRepository.save(order);
            }
            eventPublisher.publishEvent(new RefundCompletedEvent(order, updated));
        }

        log.info("Payment refunded for order ID: {}, new txn: {}", orderId, updated.getTransactionId());
        return mapToPaymentResponse(updated);
    }

    private PaymentResponse mapToPaymentResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrder() != null ? payment.getOrder().getId() : null,
                payment.getTransactionId(),
                payment.getRazorpayOrderId(),
                payment.getAmount(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }
}
