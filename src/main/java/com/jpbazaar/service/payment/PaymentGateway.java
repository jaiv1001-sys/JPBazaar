package com.jpbazaar.service.payment;

import java.math.BigDecimal;

/**
 * Pluggable Payment Gateway abstraction.
 * Allows seamless switching between simulated gateways and real gateways (e.g. Stripe, Razorpay).
 */
public interface PaymentGateway {

    PaymentGatewayResult processPayment(PaymentGatewayRequest request);

    PaymentGatewayResult refundPayment(String transactionId, BigDecimal amount);
}
