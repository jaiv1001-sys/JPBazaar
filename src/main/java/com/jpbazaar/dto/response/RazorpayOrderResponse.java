package com.jpbazaar.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Details required by frontend Razorpay Checkout modal")
public record RazorpayOrderResponse(
        @Schema(description = "Razorpay-generated Order ID", example = "order_N4zW5kQ9m2P")
        String razorpayOrderId,

        @Schema(description = "Razorpay Public Key ID", example = "rzp_test_1234567890")
        String keyId,

        @Schema(description = "Amount in standard currency units (e.g., INR 499.00)", example = "499.00")
        BigDecimal amount,

        @Schema(description = "Amount in smallest currency unit (paise)", example = "49900")
        Long amountInPaise,

        @Schema(description = "ISO Currency code", example = "INR")
        String currency,

        @Schema(description = "Internal JPBazaar order ID", example = "101")
        Long orderId,

        @Schema(description = "Customer prefill name", example = "Alexander Vance")
        String customerName,

        @Schema(description = "Customer prefill email", example = "vance@luxury.com")
        String customerEmail,

        @Schema(description = "Customer prefill contact phone", example = "9999999999")
        String customerContact
) {}
