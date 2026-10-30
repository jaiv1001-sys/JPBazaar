package com.jpbazaar.controller;

import com.jpbazaar.dto.ApiResponse;
import com.jpbazaar.dto.request.PaymentProcessRequest;
import com.jpbazaar.dto.response.PaymentResponse;
import com.jpbazaar.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Simulated payment gateway processing (Card, UPI, NetBanking, COD) and refunds")
@SecurityRequirement(name = "Bearer Authentication")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
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

    @GetMapping("/orders/{orderId}")
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
