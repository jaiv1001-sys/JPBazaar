package com.jpbazaar.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to verify Razorpay payment signature after successful checkout")
public record RazorpayVerificationRequest(
        @NotNull(message = "Order ID is mandatory")
        @Schema(description = "Internal JPBazaar order ID", example = "101")
        Long orderId,

        @NotBlank(message = "Razorpay Order ID is mandatory")
        @Schema(description = "Razorpay-generated Order ID", example = "order_N4zW5kQ9m2P")
        String razorpayOrderId,

        @NotBlank(message = "Razorpay Payment ID is mandatory")
        @Schema(description = "Razorpay-generated Payment ID", example = "pay_N4zX0lO2p9K")
        String razorpayPaymentId,

        @NotBlank(message = "Razorpay Signature is mandatory")
        @Schema(description = "HMAC SHA256 Signature returned by Razorpay Checkout modal", example = "9b646c28f...")
        String razorpaySignature
) {}
