package com.jpbazaar.dto.response;

import com.jpbazaar.entity.PaymentMethod;
import com.jpbazaar.entity.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Schema(description = "Payment transaction receipt details")
public record PaymentResponse(
        @Schema(description = "Payment ID", example = "501")
        Long id,

        @Schema(description = "Associated order ID", example = "101")
        Long orderId,

        @Schema(description = "Unique gateway transaction tracking ID", example = "pay_N4zX0lO2p9K")
        String transactionId,

        @Schema(description = "Razorpay order reference ID", example = "order_N4zW5kQ9m2P")
        String razorpayOrderId,

        @Schema(description = "Processed payment amount", example = "399.98")
        BigDecimal amount,

        @Schema(description = "Payment method used", example = "CARD")
        PaymentMethod method,

        @Schema(description = "Gateway processing status (PENDING, SUCCESS, FAILED, REFUNDED)", example = "SUCCESS")
        PaymentStatus status,

        @Schema(description = "Payment completion timestamp")
        OffsetDateTime createdAt
) {
    public PaymentResponse(
            Long id,
            Long orderId,
            String transactionId,
            BigDecimal amount,
            PaymentMethod method,
            PaymentStatus status,
            OffsetDateTime createdAt
    ) {
        this(id, orderId, transactionId, null, amount, method, status, createdAt);
    }
}
