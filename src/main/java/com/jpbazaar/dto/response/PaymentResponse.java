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

        @Schema(description = "Unique gateway transaction tracking ID", example = "TXN_1698500000000_A1B2")
        String transactionId,

        @Schema(description = "Processed payment amount", example = "399.98")
        BigDecimal amount,

        @Schema(description = "Payment method used", example = "CREDIT_CARD")
        PaymentMethod method,

        @Schema(description = "Gateway processing status (SUCCESS, FAILED, REFUNDED)", example = "SUCCESS")
        PaymentStatus status,

        @Schema(description = "Payment completion timestamp")
        OffsetDateTime createdAt
) {}
