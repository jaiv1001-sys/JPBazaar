package com.jpbazaar.service.payment;

import com.jpbazaar.dto.request.RazorpayVerificationRequest;
import com.jpbazaar.dto.response.PaymentResponse;
import com.jpbazaar.dto.response.RazorpayOrderResponse;

public interface RazorpayService {

    /**
     * Initiates a Razorpay order for an existing customer order.
     * Prevents IDOR by validating user ownership.
     *
     * @param userEmail Email of the authenticated customer
     * @param orderId   Internal order ID
     * @return Razorpay order creation details for frontend modal
     */
    RazorpayOrderResponse createOrder(String userEmail, Long orderId);

    /**
     * Verifies the cryptographic HMAC-SHA256 signature returned by Razorpay Checkout.
     * Idempotently transitions Payment to SUCCESS and Order to CONFIRMED.
     *
     * @param userEmail Email of the authenticated customer
     * @param request   Verification payload containing paymentId, orderId, signature
     * @return Confirmed Payment receipt
     */
    PaymentResponse verifyPayment(String userEmail, RazorpayVerificationRequest request);

    /**
     * Processes asynchronous Razorpay webhook events (e.g. payment.captured, payment.failed).
     * Cryptographically validates X-Razorpay-Signature with the webhook secret.
     *
     * @param payload         Raw JSON payload
     * @param signatureHeader Value of X-Razorpay-Signature header
     */
    void handleWebhook(String payload, String signatureHeader);

    /**
     * Helper to compute HMAC-SHA256 signature for verification and testing.
     */
    String calculateHmacSha256(String data, String secret);
}
