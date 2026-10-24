package com.jpbazaar.service;

import com.jpbazaar.dto.request.PaymentProcessRequest;
import com.jpbazaar.dto.response.PaymentResponse;

public interface PaymentService {

    PaymentResponse processPayment(String userEmail, PaymentProcessRequest request);

    PaymentResponse getPaymentByOrderId(String userEmail, Long orderId, boolean isAdmin);

    PaymentResponse refundPayment(String userEmail, Long orderId, boolean isAdmin);
}
