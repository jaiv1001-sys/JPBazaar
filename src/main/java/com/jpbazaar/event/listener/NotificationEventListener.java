package com.jpbazaar.event.listener;

import com.jpbazaar.entity.OrderStatus;
import com.jpbazaar.event.*;
import com.jpbazaar.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationEventListener {

    private final EmailService emailService;

    @Async("emailTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("Processing UserRegisteredEvent for {}", event.user().getEmail());
        emailService.sendRegistrationEmail(event.user());
    }

    @Async("emailTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("Processing OrderPlacedEvent for order #{}", event.order().getId());
        emailService.sendOrderConfirmationEmail(event.order());
    }

    @Async("emailTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handlePaymentStatus(PaymentStatusEvent event) {
        log.info("Processing PaymentStatusEvent for order #{}, success={}", event.order().getId(), event.isSuccess());
        if (event.isSuccess()) {
            emailService.sendPaymentSuccessEmail(event.order(), event.payment());
        } else {
            emailService.sendPaymentFailureEmail(event.order(), event.failureReason());
        }
    }

    @Async("emailTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handleOrderStatusChanged(OrderStatusChangedEvent event) {
        log.info("Processing OrderStatusChangedEvent for order #{}: {} -> {}",
                event.order().getId(), event.previousStatus(), event.newStatus());

        OrderStatus newStatus = event.newStatus();
        switch (newStatus) {
            case PROCESSING -> emailService.sendOrderProcessingEmail(event.order());
            case SHIPPED -> emailService.sendOrderShippedEmail(event.order());
            case OUT_FOR_DELIVERY -> emailService.sendOutForDeliveryEmail(event.order());
            case DELIVERED -> emailService.sendOrderDeliveredEmail(event.order());
            case CANCELLED -> emailService.sendOrderCancelledEmail(event.order(), event.notes());
            case REFUNDED -> emailService.sendRefundCompletedEmail(event.order(), null);
            default -> log.debug("No distinct email trigger for status {}", newStatus);
        }
    }

    @Async("emailTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handleRefundCompleted(RefundCompletedEvent event) {
        log.info("Processing RefundCompletedEvent for order #{}", event.order().getId());
        emailService.sendRefundCompletedEmail(event.order(), event.payment());
    }
}
