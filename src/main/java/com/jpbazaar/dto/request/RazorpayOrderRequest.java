package com.jpbazaar.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to initiate a Razorpay order")
public record RazorpayOrderRequest(
        @NotNull(message = "Order ID is mandatory")
        @Schema(description = "Internal Order ID to settle via Razorpay", example = "101")
        Long orderId
) {}
