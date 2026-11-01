package com.jpbazaar.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpbazaar.config.RazorpayConfig;
import com.jpbazaar.dto.request.RazorpayOrderRequest;
import com.jpbazaar.dto.request.RazorpayVerificationRequest;
import com.jpbazaar.entity.*;
import com.jpbazaar.repository.*;
import com.jpbazaar.service.payment.RazorpayService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RazorpayIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RazorpayService razorpayService;

    @Autowired
    private RazorpayConfig razorpayConfig;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private User customer1;
    private User customer2;
    private String customer1Token;
    private String customer2Token;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        customer1 = userRepository.save(User.builder()
                .firstName("Alexander")
                .lastName("Vance")
                .email("vance@luxury.com")
                .password(passwordEncoder.encode("Password123!"))
                .phone("+1234567890")
                .role(Role.CUSTOMER)
                .build());

        customer2 = userRepository.save(User.builder()
                .firstName("Marcus")
                .lastName("Sterling")
                .email("marcus@luxury.com")
                .password(passwordEncoder.encode("Password123!"))
                .phone("+1987654321")
                .role(Role.CUSTOMER)
                .build());

        customer1Token = generateToken(customer1.getEmail(), "ROLE_CUSTOMER");
        customer2Token = generateToken(customer2.getEmail(), "ROLE_CUSTOMER");

        Address address = addressRepository.save(Address.builder()
                .user(customer1)
                .addressLine("742 Evergreen Terrace")
                .city("Springfield")
                .state("OR")
                .postalCode("97477")
                .country("USA")
                .isDefault(true)
                .build());

        testOrder = orderRepository.save(Order.builder()
                .user(customer1)
                .address(address)
                .totalAmount(new BigDecimal("299.98"))
                .status(OrderStatus.PLACED)
                .build());
    }

    private String generateToken(String email, String role) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key)
                .compact();
    }

    @Test
    @DisplayName("Should successfully initiate a Razorpay order with converted amount in paise")
    void testCreateRazorpayOrder_Success() throws Exception {
        RazorpayOrderRequest request = new RazorpayOrderRequest(testOrder.getId());

        mockMvc.perform(post("/api/payments/razorpay/create-order")
                        .header("Authorization", "Bearer " + customer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderId").value(testOrder.getId()))
                .andExpect(jsonPath("$.data.amountInPaise").value(29998))
                .andExpect(jsonPath("$.data.currency").value("INR"))
                .andExpect(jsonPath("$.data.razorpayOrderId").isNotEmpty())
                .andExpect(jsonPath("$.data.customerName").value("Alexander Vance"))
                .andExpect(jsonPath("$.data.customerEmail").value("vance@luxury.com"));

        Payment payment = paymentRepository.findByOrderId(testOrder.getId()).orElse(null);
        assertThat(payment).isNotNull();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.getRazorpayOrderId()).isNotEmpty();
    }

    @Test
    @DisplayName("IDOR: Customer 2 cannot initiate payment order for Customer 1's order")
    void testCreateRazorpayOrder_IdorProtection() throws Exception {
        RazorpayOrderRequest request = new RazorpayOrderRequest(testOrder.getId());

        mockMvc.perform(post("/api/payments/razorpay/create-order")
                        .header("Authorization", "Bearer " + customer2Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("You are not authorized to initiate payment for this order"));
    }

    @Test
    @DisplayName("Should cryptographically verify valid signature and auto-confirm order")
    void testVerifyRazorpayPayment_ValidSignature_ConfirmsOrder() throws Exception {
        // Step 1: Create order first
        razorpayService.createOrder(customer1.getEmail(), testOrder.getId());
        Payment pendingPayment = paymentRepository.findByOrderId(testOrder.getId()).orElseThrow();
        String razorpayOrderId = pendingPayment.getRazorpayOrderId();

        // Step 2: Compute valid HMAC-SHA256
        String razorpayPaymentId = "pay_test_verified999";
        String payloadToSign = razorpayOrderId + "|" + razorpayPaymentId;
        String validSignature = razorpayService.calculateHmacSha256(payloadToSign, razorpayConfig.getKeySecret());

        RazorpayVerificationRequest request = new RazorpayVerificationRequest(
                testOrder.getId(),
                razorpayOrderId,
                razorpayPaymentId,
                validSignature
        );

        // Step 3: Verify payment
        mockMvc.perform(post("/api/payments/razorpay/verify")
                        .header("Authorization", "Bearer " + customer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.transactionId").value(razorpayPaymentId))
                .andExpect(jsonPath("$.data.razorpayOrderId").value(razorpayOrderId));

        // Step 4: Verify Database state
        Payment updatedPayment = paymentRepository.findByOrderId(testOrder.getId()).orElseThrow();
        assertThat(updatedPayment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(updatedPayment.getTransactionId()).isEqualTo(razorpayPaymentId);

        Order updatedOrder = orderRepository.findById(testOrder.getId()).orElseThrow();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    @DisplayName("Tampered signature should be rejected and payment marked FAILED")
    void testVerifyRazorpayPayment_TamperedSignature_Rejected() throws Exception {
        razorpayService.createOrder(customer1.getEmail(), testOrder.getId());
        Payment pendingPayment = paymentRepository.findByOrderId(testOrder.getId()).orElseThrow();
        String razorpayOrderId = pendingPayment.getRazorpayOrderId();

        RazorpayVerificationRequest request = new RazorpayVerificationRequest(
                testOrder.getId(),
                razorpayOrderId,
                "pay_test_tampered123",
                "invalid_tampered_signature_hex_value"
        );

        mockMvc.perform(post("/api/payments/razorpay/verify")
                        .header("Authorization", "Bearer " + customer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        Payment failedPayment = paymentRepository.findByOrderId(testOrder.getId()).orElseThrow();
        assertThat(failedPayment.getStatus()).isEqualTo(PaymentStatus.FAILED);

        Order unalteredOrder = orderRepository.findById(testOrder.getId()).orElseThrow();
        assertThat(unalteredOrder.getStatus()).isEqualTo(OrderStatus.PLACED);
    }

    @Test
    @DisplayName("Idempotency: Re-verifying successful payment returns receipt safely")
    void testVerifyRazorpayPayment_Idempotent() throws Exception {
        razorpayService.createOrder(customer1.getEmail(), testOrder.getId());
        Payment pendingPayment = paymentRepository.findByOrderId(testOrder.getId()).orElseThrow();
        String razorpayOrderId = pendingPayment.getRazorpayOrderId();

        String razorpayPaymentId = "pay_test_idempotent_123";
        String validSignature = razorpayService.calculateHmacSha256(
                razorpayOrderId + "|" + razorpayPaymentId, razorpayConfig.getKeySecret());

        RazorpayVerificationRequest request = new RazorpayVerificationRequest(
                testOrder.getId(),
                razorpayOrderId,
                razorpayPaymentId,
                validSignature
        );

        // First verification
        mockMvc.perform(post("/api/payments/razorpay/verify")
                        .header("Authorization", "Bearer " + customer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Repeated verification (idempotent call)
        mockMvc.perform(post("/api/payments/razorpay/verify")
                        .header("Authorization", "Bearer " + customer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.transactionId").value(razorpayPaymentId));
    }

    @Test
    @DisplayName("Webhook: Valid signature event successfully captures payment and confirms order")
    void testWebhook_ValidSignature_CapturedEvent() throws Exception {
        razorpayService.createOrder(customer1.getEmail(), testOrder.getId());
        Payment pendingPayment = paymentRepository.findByOrderId(testOrder.getId()).orElseThrow();
        String razorpayOrderId = pendingPayment.getRazorpayOrderId();

        String webhookJson = """
                {
                  "event": "payment.captured",
                  "payload": {
                    "payment": {
                      "entity": {
                        "id": "pay_webhook_captured_555",
                        "order_id": "%s",
                        "status": "captured",
                        "amount": 29998
                      }
                    }
                  }
                }
                """.formatted(razorpayOrderId);

        String validWebhookSignature = razorpayService.calculateHmacSha256(
                webhookJson, razorpayConfig.getWebhookSecret());

        mockMvc.perform(post("/api/payments/webhook/razorpay")
                        .header("X-Razorpay-Signature", validWebhookSignature)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(webhookJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        Payment capturedPayment = paymentRepository.findByOrderId(testOrder.getId()).orElseThrow();
        assertThat(capturedPayment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(capturedPayment.getTransactionId()).isEqualTo("pay_webhook_captured_555");

        Order confirmedOrder = orderRepository.findById(testOrder.getId()).orElseThrow();
        assertThat(confirmedOrder.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    @DisplayName("Webhook: Invalid signature should be rejected with 403 Forbidden")
    void testWebhook_InvalidSignature_Rejected() throws Exception {
        String webhookJson = "{\"event\": \"payment.captured\"}";

        mockMvc.perform(post("/api/payments/webhook/razorpay")
                        .header("X-Razorpay-Signature", "invalid_forged_signature")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(webhookJson))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid webhook signature"));
    }
}
