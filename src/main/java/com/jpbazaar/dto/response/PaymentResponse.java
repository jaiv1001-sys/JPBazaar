package com.jpbazaar.dto.response;

import com.jpbazaar.entity.PaymentMethod;
import com.jpbazaar.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PaymentResponse(
        Long id,
        Long orderId,
        String transactionId,
        BigDecimal amount,
        PaymentMethod method,
        PaymentStatus status,
        OffsetDateTime createdAt
) {}
