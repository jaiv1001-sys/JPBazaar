package com.jpbazaar.controller;

import com.jpbazaar.dto.ApiResponse;
import com.jpbazaar.dto.request.PaymentProcessRequest;
import com.jpbazaar.dto.request.RazorpayOrderRequest;
import com.jpbazaar.dto.request.RazorpayVerificationRequest;
import com.jpbazaar.dto.response.PaymentResponse;
import com.jpbazaar.dto.response.RazorpayOrderResponse;
import com.jpbazaar.service.PaymentService;
import com.jpbazaar.service.payment.RazorpayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Simulated and Razorpay payment gateway integration, signatures, and webhooks")
public class PaymentController {

    private final PaymentService paymentService;
    private final RazorpayService razorpayService;

    @PostMapping("/process")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Process simulated payment", description = "Simulates payment gateway transaction for an order. On success, transitions order to CONFIRMED. Retains failed records for audit logging.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment processed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Payment failed or already completed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden (IDOR protection violation)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
            Authentication authentication,
            @Valid @RequestBody PaymentProcessRequest request
    ) {
        PaymentResponse response = paymentService.processPayment(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Payment processed successfully", response));
    }

    @PostMapping("/razorpay/create-order")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Create Razorpay payment order", description = "Generates a Razorpay Order ID for customer checkout. Amount converted to paise with IDOR protection.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Razorpay order initiated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid order state or order already settled"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden (IDOR violation)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<ApiResponse<RazorpayOrderResponse>> createRazorpayOrder(
            Authentication authentication,
            @Valid @RequestBody RazorpayOrderRequest request
    ) {
        RazorpayOrderResponse response = razorpayService.createOrder(authentication.getName(), request.orderId());
        return ResponseEntity.ok(ApiResponse.success("Razorpay order initiated successfully", response));
    }

    @PostMapping("/razorpay/verify")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Verify Razorpay payment signature", description = "Cryptographically verifies HMAC-SHA256 signature returned by Razorpay Checkout. Idempotently marks payment SUCCESS and confirms order.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment verified and order confirmed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid signature or tampering detected"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden (IDOR violation)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<ApiResponse<PaymentResponse>> verifyRazorpayPayment(
            Authentication authentication,
            @Valid @RequestBody RazorpayVerificationRequest request
    ) {
        PaymentResponse response = razorpayService.verifyPayment(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Payment verified and order confirmed", response));
    }

    @PostMapping("/webhook/razorpay")
    @Operation(summary = "Razorpay webhook listener", description = "Public endpoint for asynchronous Razorpay webhook notifications. Validates X-Razorpay-Signature with webhook secret.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Webhook acknowledged"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid webhook signature")
    })
    public ResponseEntity<ApiResponse<Void>> handleRazorpayWebhook(
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String signatureHeader,
            @RequestBody String payload
    ) {
        razorpayService.handleWebhook(payload, signatureHeader);
        return ResponseEntity.ok(ApiResponse.success("Webhook processed successfully", null));
    }

    @GetMapping("/orders/{orderId}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get payment details for order", description = "Retrieves payment transaction details for a specified order ID. Customers can only view their own payment details.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment details retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden (IDOR protection violation)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order or payment record not found")
    })
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByOrderId(
            @Parameter(description = "Order ID", example = "1") @PathVariable Long orderId,
            Authentication authentication
    ) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        PaymentResponse response = paymentService.getPaymentByOrderId(authentication.getName(), orderId, isAdmin);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/orders/{orderId}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Admin: Process payment refund", description = "Initiates a payment refund for an order. Generates unique REF- transaction reference. Requires ADMIN role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment refunded successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Payment cannot be refunded (not in SUCCESS status)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden (Admin role required)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<ApiResponse<PaymentResponse>> refundPayment(
            @Parameter(description = "Order ID to refund", example = "1") @PathVariable Long orderId,
            Authentication authentication
    ) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        PaymentResponse response = paymentService.refundPayment(authentication.getName(), orderId, isAdmin);
        return ResponseEntity.ok(ApiResponse.success("Payment refunded successfully", response));
    }
}
