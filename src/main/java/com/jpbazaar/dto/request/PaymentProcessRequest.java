package com.jpbazaar.dto.request;

import com.jpbazaar.entity.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Payment gateway processing request payload")
public record PaymentProcessRequest(
        @Schema(description = "ID of the order to process payment for", example = "101")
        @NotNull(message = "Order ID is required")
        Long orderId,

        @Schema(description = "Payment method used for the transaction", example = "UPI")
        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,

        @Schema(description = "Simulate payment gateway failure for testing error recovery", example = "false")
        Boolean simulateFailure
) {
    public PaymentProcessRequest(Long orderId, PaymentMethod paymentMethod) {
        this(orderId, paymentMethod, false);
    }
}
