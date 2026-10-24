package com.jpbazaar.service.payment;

import com.jpbazaar.entity.PaymentStatus;

public record PaymentGatewayResult(
        boolean success,
        String transactionId,
        PaymentStatus status,
        String message
) {
    public static PaymentGatewayResult success(String transactionId, PaymentStatus status) {
        return new PaymentGatewayResult(true, transactionId, status, "Payment processed successfully");
    }

    public static PaymentGatewayResult failure(String transactionId, String errorMessage) {
        return new PaymentGatewayResult(false, transactionId, PaymentStatus.FAILED, errorMessage);
    }
}
