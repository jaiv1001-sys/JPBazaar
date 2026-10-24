package com.jpbazaar.service.payment;

import com.jpbazaar.entity.PaymentMethod;

import java.math.BigDecimal;

public record PaymentGatewayRequest(
        Long orderId,
        BigDecimal amount,
        PaymentMethod method,
        boolean simulateFailure
) {}
