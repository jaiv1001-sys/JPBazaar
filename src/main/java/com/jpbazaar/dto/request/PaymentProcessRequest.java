package com.jpbazaar.dto.request;

import com.jpbazaar.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record PaymentProcessRequest(
        @NotNull(message = "Order ID is required")
        Long orderId,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,

        Boolean simulateFailure
) {
    public PaymentProcessRequest(Long orderId, PaymentMethod paymentMethod) {
        this(orderId, paymentMethod, false);
    }
}
