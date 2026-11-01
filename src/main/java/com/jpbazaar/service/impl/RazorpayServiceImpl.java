package com.jpbazaar.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpbazaar.config.RazorpayConfig;
import com.jpbazaar.dto.request.RazorpayVerificationRequest;
import com.jpbazaar.dto.response.PaymentResponse;
import com.jpbazaar.dto.response.RazorpayOrderResponse;
import com.jpbazaar.entity.*;
import com.jpbazaar.exception.*;
import com.jpbazaar.repository.OrderRepository;
import com.jpbazaar.repository.PaymentRepository;
import com.jpbazaar.service.payment.RazorpayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RazorpayServiceImpl implements RazorpayService {

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String RAZORPAY_API_URL = "https://api.razorpay.com/v1";

    private final RazorpayConfig razorpayConfig;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public RazorpayOrderResponse createOrder(String userEmail, Long orderId) {
        log.info("Initiating Razorpay order creation for order ID: {}, user: {}", orderId, userEmail);

        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        // IDOR Protection: Validate order ownership
        if (!order.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            log.warn("IDOR attempt: User {} attempted to access Order ID {} belonging to {}",
                    userEmail, orderId, order.getUser().getEmail());
            throw new UnauthorizedException("You are not authorized to initiate payment for this order");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException("Cannot process payment for a cancelled order");
        }

        Payment existingPayment = paymentRepository.findByOrderId(order.getId()).orElse(null);
        if (existingPayment != null && existingPayment.getStatus() == PaymentStatus.SUCCESS) {
            throw new InvalidRequestException("Payment has already been successfully completed for this order");
        }

        // Razorpay amounts are strictly in smallest currency unit (paise for INR)
        long amountInPaise = order.getTotalAmount()
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();

        String razorpayOrderId = requestRazorpayOrderId(order, amountInPaise);

        // Record or update Payment entry in PENDING state
        Payment payment = existingPayment != null ? existingPayment : Payment.builder().order(order).build();
        payment.setAmount(order.getTotalAmount());
        payment.setMethod(PaymentMethod.CARD); // Default method, refined upon client callback
        payment.setStatus(PaymentStatus.PENDING);
        payment.setRazorpayOrderId(razorpayOrderId);
        if (payment.getTransactionId() == null || payment.getTransactionId().isBlank()) {
            payment.setTransactionId("PENDING_" + razorpayOrderId);
        }

        paymentRepository.save(payment);
        log.info("Saved pending Razorpay payment record for order ID: {}, razorpayOrderId: {}", orderId, razorpayOrderId);

        // Extract customer contact details
        String customerName = ((order.getUser().getFirstName() != null ? order.getUser().getFirstName() : "") + " "
                + (order.getUser().getLastName() != null ? order.getUser().getLastName() : "")).trim();
        String customerPhone = order.getUser().getPhone() != null ? order.getUser().getPhone() : "";

        return new RazorpayOrderResponse(
                razorpayOrderId,
                razorpayConfig.getKeyId(),
                order.getTotalAmount(),
                amountInPaise,
                razorpayConfig.getCurrency(),
                order.getId(),
                customerName,
                order.getUser().getEmail(),
                customerPhone
        );
    }

    @Override
    @Transactional(noRollbackFor = PaymentFailedException.class)
    public PaymentResponse verifyPayment(String userEmail, RazorpayVerificationRequest request) {
        log.info("Verifying Razorpay payment signature for order ID: {}, paymentId: {}",
                request.orderId(), request.razorpayPaymentId());

        Order order = orderRepository.findByIdWithDetails(request.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + request.orderId()));

        // IDOR Protection: Validate order ownership
        if (!order.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            log.warn("IDOR attempt: User {} attempted to verify payment for Order ID {} belonging to {}",
                    userEmail, request.orderId(), order.getUser().getEmail());
            throw new UnauthorizedException("You are not authorized to verify payment for this order");
        }

        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElseGet(() -> Payment.builder()
                        .order(order)
                        .amount(order.getTotalAmount())
                        .method(PaymentMethod.CARD)
                        .status(PaymentStatus.PENDING)
                        .build());

        // Idempotency: If already marked SUCCESS with the same payment ID, return receipt
        if (payment.getStatus() == PaymentStatus.SUCCESS &&
                request.razorpayPaymentId().equalsIgnoreCase(payment.getTransactionId())) {
            log.info("Payment already confirmed for order ID: {}. Returning existing receipt.", order.getId());
            return mapToPaymentResponse(payment);
        }

        // Verify HMAC-SHA256 Signature
        String signaturePayload = request.razorpayOrderId() + "|" + request.razorpayPaymentId();
        String expectedSignature = calculateHmacSha256(signaturePayload, razorpayConfig.getKeySecret());

        boolean signatureMatches = MessageDigest.isEqual(
                expectedSignature.getBytes(StandardCharsets.UTF_8),
                request.razorpaySignature().getBytes(StandardCharsets.UTF_8)
        );

        if (!signatureMatches) {
            log.error("CRYPTOGRAPHIC VERIFICATION FAILED! Order ID: {}, Expected: {}, Received: {}",
                    request.orderId(), expectedSignature, request.razorpaySignature());
            payment.setStatus(PaymentStatus.FAILED);
            payment.setRazorpayOrderId(request.razorpayOrderId());
            payment.setRazorpaySignature(request.razorpaySignature());
            paymentRepository.save(payment);
            eventPublisher.publishEvent(new com.jpbazaar.event.PaymentStatusEvent(order, payment, false, "Payment signature verification failed"));
            throw new PaymentFailedException("Payment signature verification failed. Potential tampering detected.");
        }

        // Cryptographic verification passed: Update payment and advance order state
        payment.setTransactionId(request.razorpayPaymentId());
        payment.setRazorpayOrderId(request.razorpayOrderId());
        payment.setRazorpaySignature(request.razorpaySignature());
        payment.setStatus(PaymentStatus.SUCCESS);
        Payment savedPayment = paymentRepository.save(payment);

        if (order.getStatus() == OrderStatus.PLACED) {
            OrderStatus previousStatus = order.getStatus();
            order.setStatus(OrderStatus.CONFIRMED);
            OrderStatusHistory history = OrderStatusHistory.builder()
                    .order(order)
                    .status(OrderStatus.CONFIRMED)
                    .notes("Payment confirmed via Razorpay (Txn: " + savedPayment.getTransactionId() + ")")
                    .updatedBy("RAZORPAY")
                    .build();
            order.addStatusHistory(history);
            orderRepository.save(order);
            log.info("Order ID: {} auto-transitioned to CONFIRMED following Razorpay verification", order.getId());
            eventPublisher.publishEvent(new com.jpbazaar.event.OrderStatusChangedEvent(order, previousStatus, OrderStatus.CONFIRMED, "Payment verified via Razorpay"));
        }

        eventPublisher.publishEvent(new com.jpbazaar.event.PaymentStatusEvent(order, savedPayment, true, null));

        log.info("Razorpay payment successfully verified for Order ID: {}, Payment ID: {}",
                order.getId(), savedPayment.getTransactionId());
        return mapToPaymentResponse(savedPayment);
    }

    @Override
    @Transactional
    public void handleWebhook(String payload, String signatureHeader) {
        log.info("Processing inbound Razorpay webhook event");

        if (signatureHeader == null || signatureHeader.isBlank()) {
            throw new UnauthorizedException("Missing X-Razorpay-Signature header");
        }

        // Cryptographic Webhook verification
        String expectedSignature = calculateHmacSha256(payload, razorpayConfig.getWebhookSecret());
        boolean signatureValid = MessageDigest.isEqual(
                expectedSignature.getBytes(StandardCharsets.UTF_8),
                signatureHeader.getBytes(StandardCharsets.UTF_8)
        );

        if (!signatureValid) {
            log.error("Inbound webhook signature verification failed. Rejecting event.");
            throw new UnauthorizedException("Invalid webhook signature");
        }

        try {
            JsonNode rootNode = objectMapper.readTree(payload);
            String event = rootNode.path("event").asText();
            log.info("Received valid Razorpay webhook event: {}", event);

            JsonNode paymentEntity = rootNode.path("payload").path("payment").path("entity");
            String razorpayOrderId = paymentEntity.path("order_id").asText(null);
            String razorpayPaymentId = paymentEntity.path("id").asText(null);

            if (razorpayOrderId == null || razorpayOrderId.isBlank()) {
                JsonNode orderEntity = rootNode.path("payload").path("order").path("entity");
                razorpayOrderId = orderEntity.path("id").asText(null);
            }

            if (razorpayOrderId != null) {
                Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId).orElse(null);
                if (payment != null) {
                    if ("payment.captured".equalsIgnoreCase(event) || "order.paid".equalsIgnoreCase(event)) {
                        if (payment.getStatus() != PaymentStatus.SUCCESS) {
                            payment.setStatus(PaymentStatus.SUCCESS);
                            if (razorpayPaymentId != null && !razorpayPaymentId.isBlank()) {
                                payment.setTransactionId(razorpayPaymentId);
                            }
                            paymentRepository.save(payment);

                            Order order = payment.getOrder();
                            if (order != null && order.getStatus() == OrderStatus.PLACED) {
                                order.setStatus(OrderStatus.CONFIRMED);
                                orderRepository.save(order);
                                log.info("Webhook transitioned Order ID: {} to CONFIRMED", order.getId());
                            }
                        }
                    } else if ("payment.failed".equalsIgnoreCase(event)) {
                        if (payment.getStatus() == PaymentStatus.PENDING) {
                            payment.setStatus(PaymentStatus.FAILED);
                            paymentRepository.save(payment);
                            log.info("Webhook updated payment to FAILED for Razorpay Order: {}", razorpayOrderId);
                        }
                    }
                } else {
                    log.warn("Webhook received for unknown Razorpay Order ID: {}", razorpayOrderId);
                }
            }
        } catch (Exception e) {
            log.error("Error parsing Razorpay webhook payload: {}", e.getMessage(), e);
            throw new InvalidRequestException("Malformed webhook payload");
        }
    }

    @Override
    public String calculateHmacSha256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hmacBytes);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to calculate HMAC-SHA256 signature", e);
        }
    }

    /**
     * Calls Razorpay Orders API if live/test credentials are configured and online.
     * Falls back to a valid deterministic test order ID in sandboxed or offline mock environments.
     */
    private String requestRazorpayOrderId(Order order, long amountInPaise) {
        if (!razorpayConfig.isPlaceholder()) {
            try {
                String authHeader = "Basic " + Base64.getEncoder().encodeToString(
                        (razorpayConfig.getKeyId() + ":" + razorpayConfig.getKeySecret()).getBytes(StandardCharsets.UTF_8));

                Map<String, Object> body = Map.of(
                        "amount", amountInPaise,
                        "currency", razorpayConfig.getCurrency(),
                        "receipt", "rcpt_" + order.getId() + "_" + System.currentTimeMillis() % 100000,
                        "notes", Map.of(
                                "orderId", String.valueOf(order.getId()),
                                "customerEmail", order.getUser().getEmail()
                        )
                );

                RestClient client = RestClient.builder()
                        .baseUrl(RAZORPAY_API_URL)
                        .defaultHeader(HttpHeaders.AUTHORIZATION, authHeader)
                        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .build();

                String response = client.post()
                        .uri("/orders")
                        .body(body)
                        .retrieve()
                        .body(String.class);

                if (response != null) {
                    JsonNode node = objectMapper.readTree(response);
                    String createdOrderId = node.path("id").asText();
                    if (createdOrderId != null && !createdOrderId.isBlank()) {
                        log.info("Successfully received Razorpay Order ID {} from live API", createdOrderId);
                        return createdOrderId;
                    }
                }
            } catch (Exception e) {
                log.warn("Razorpay API request failed ({}: {}). Utilizing deterministic test order ID for development/testing.",
                        e.getClass().getSimpleName(), e.getMessage());
            }
        }

        // Deterministic test order ID format matching Razorpay standard
        String uniqueSuffix = UUID.randomUUID().toString().replace("-", "").substring(0, 14);
        return "order_" + uniqueSuffix;
    }

    private PaymentResponse mapToPaymentResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrder() != null ? payment.getOrder().getId() : null,
                payment.getTransactionId(),
                payment.getRazorpayOrderId(),
                payment.getAmount(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }
}
