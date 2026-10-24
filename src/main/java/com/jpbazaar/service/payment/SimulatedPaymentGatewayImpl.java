package com.jpbazaar.service.payment;

import com.jpbazaar.entity.PaymentMethod;
import com.jpbazaar.entity.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
public class SimulatedPaymentGatewayImpl implements PaymentGateway {

    @Override
    public PaymentGatewayResult processPayment(PaymentGatewayRequest request) {
        String transactionId = "TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        log.info("Simulating payment gateway for order ID: {}, amount: {}, method: {}, txn: {}",
                request.orderId(), request.amount(), request.method(), transactionId);

        if (request.simulateFailure()) {
            log.warn("Payment simulation requested FAILURE for order ID: {}", request.orderId());
            return PaymentGatewayResult.failure(transactionId, "Simulated payment gateway declined the transaction");
        }

        if (request.method() == PaymentMethod.COD) {
            log.info("Payment method is COD. Setting payment status to PENDING for order ID: {}", request.orderId());
            return PaymentGatewayResult.success(transactionId, PaymentStatus.PENDING);
        }

        log.info("Payment simulation SUCCESS for order ID: {}, method: {}", request.orderId(), request.method());
        return PaymentGatewayResult.success(transactionId, PaymentStatus.SUCCESS);
    }

    @Override
    public PaymentGatewayResult refundPayment(String transactionId, BigDecimal amount) {
        String refundTxnId = "REF-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        log.info("Simulating refund for original txn: {}, amount: {}, refundTxn: {}", transactionId, amount, refundTxnId);
        return PaymentGatewayResult.success(refundTxnId, PaymentStatus.REFUNDED);
    }
}
