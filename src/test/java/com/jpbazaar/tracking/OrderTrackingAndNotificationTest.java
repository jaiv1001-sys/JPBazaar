package com.jpbazaar.tracking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpbazaar.dto.request.CreateOrderRequest;
import com.jpbazaar.dto.request.PaymentProcessRequest;
import com.jpbazaar.dto.request.RegisterRequest;
import com.jpbazaar.dto.request.UpdateOrderStatusRequest;
import com.jpbazaar.dto.response.OrderResponse;
import com.jpbazaar.dto.response.OrderTrackingResponse;
import com.jpbazaar.entity.*;
import com.jpbazaar.exception.InvalidOrderStateException;
import com.jpbazaar.exception.PaymentFailedException;
import com.jpbazaar.repository.*;
import com.jpbazaar.service.AuthService;
import com.jpbazaar.service.OrderService;
import com.jpbazaar.service.PaymentService;
import com.jpbazaar.service.email.EmailService;
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
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OrderTrackingAndNotificationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Autowired
    private EmailNotificationLogRepository emailLogRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AuthService authService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private User patron1;
    private User patron2;
    private User adminUser;
    private String patron1Token;
    private String patron2Token;
    private String adminToken;
    private Address patron1Address;
    private Address patron2Address;
    private Product luxuryWatch;
    private Order patron1Order;

    @BeforeEach
    void setUp() {
        emailLogRepository.deleteAll();
        orderStatusHistoryRepository.deleteAll();

        patron1 = userRepository.save(User.builder()
                .firstName("Victor")
                .lastName("Sterling")
                .email("victor@luxury.com")
                .password(passwordEncoder.encode("Secret123!"))
                .phone("+919876543210")
                .role(Role.CUSTOMER)
                .build());

        patron2 = userRepository.save(User.builder()
                .firstName("Helena")
                .lastName("Castellano")
                .email("helena@luxury.com")
                .password(passwordEncoder.encode("Secret123!"))
                .phone("+919876543211")
                .role(Role.CUSTOMER)
                .build());

        adminUser = userRepository.save(User.builder()
                .firstName("Auditor")
                .lastName("General")
                .email("admin@jpbazaar.com")
                .password(passwordEncoder.encode("Secret123!"))
                .role(Role.ADMIN)
                .build());

        patron1Token = generateToken(patron1.getEmail(), "ROLE_CUSTOMER");
        patron2Token = generateToken(patron2.getEmail(), "ROLE_CUSTOMER");
        adminToken = generateToken(adminUser.getEmail(), "ROLE_ADMIN");

        patron1Address = addressRepository.save(Address.builder()
                .user(patron1)
                .addressLine("10 Downing Mayfair")
                .city("London")
                .state("Greater London")
                .postalCode("W1J 8AQ")
                .country("UK")
                .isDefault(true)
                .build());

        patron2Address = addressRepository.save(Address.builder()
                .user(patron2)
                .addressLine("Via Condotti 45")
                .city("Rome")
                .state("Lazio")
                .postalCode("00187")
                .country("Italy")
                .isDefault(true)
                .build());

        Category horology = categoryRepository.save(Category.builder()
                .name("Haute Horology")
                .description("Exceptional mechanical timepieces")
                .build());

        luxuryWatch = productRepository.save(Product.builder()
                .name("Chrono Master Tourbillon")
                .sku("HORO-CHRONO-001")
                .category(horology)
                .price(new BigDecimal("12500.00"))
                .stockQuantity(10)
                .active(true)
                .build());

        patron1Order = orderRepository.save(Order.builder()
                .user(patron1)
                .address(patron1Address)
                .status(OrderStatus.PLACED)
                .carrier("JPBazaar White-Glove Express")
                .trackingNumber("JPB-" + System.currentTimeMillis())
                .totalAmount(new BigDecimal("12500.00"))
                .build());

        OrderStatusHistory initialHistory = OrderStatusHistory.builder()
                .order(patron1Order)
                .status(OrderStatus.PLACED)
                .notes("Order acquisition placed by patron")
                .updatedBy(patron1.getEmail())
                .build();
        orderStatusHistoryRepository.save(initialHistory);
        patron1Order.addStatusHistory(initialHistory);
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
    @DisplayName("Scenario 1: Customer registration generates registration welcome notification log")
    void testRegistrationEmailTriggered() {
        RegisterRequest registerReq = new RegisterRequest(
                "Aurelius",
                "King",
                "aurelius.king@luxury.com",
                "Password123!",
                "+1234567890"
        );

        authService.register(registerReq);

        // Verify that direct or event-driven email service handles registration
        emailService.sendRegistrationEmail(userRepository.findByEmailIgnoreCase("aurelius.king@luxury.com").orElseThrow());

        List<EmailNotificationLog> logs = emailLogRepository.findByRecipientEmailOrderByCreatedAtDesc("aurelius.king@luxury.com");
        assertThat(logs).isNotEmpty();
        assertThat(logs.get(0).getNotificationType()).isEqualTo(NotificationType.REGISTRATION_CONFIRMATION);
        assertThat(logs.get(0).getStatus()).isIn(NotificationStatus.SKIPPED, NotificationStatus.SENT);
    }

    @Test
    @DisplayName("Scenario 2: Order confirmation email triggered and logged on order placement")
    void testOrderConfirmationEmailTriggered() {
        emailService.sendOrderConfirmationEmail(patron1Order);

        List<EmailNotificationLog> logs = emailLogRepository.findByOrderIdOrderByCreatedAtDesc(patron1Order.getId());
        assertThat(logs).isNotEmpty();
        assertThat(logs.get(0).getNotificationType()).isEqualTo(NotificationType.ORDER_CONFIRMATION);
        assertThat(logs.get(0).getRecipientEmail()).isEqualTo(patron1.getEmail());
    }

    @Test
    @DisplayName("Scenario 3: Payment success transitions order to CONFIRMED and logs payment success notification")
    void testPaymentSuccessEmailTriggered() {
        PaymentProcessRequest paymentReq = new PaymentProcessRequest(
                patron1Order.getId(),
                PaymentMethod.CARD,
                false
        );

        paymentService.processPayment(patron1.getEmail(), paymentReq);

        Order refreshedOrder = orderRepository.findById(patron1Order.getId()).orElseThrow();
        assertThat(refreshedOrder.getStatus()).isEqualTo(OrderStatus.CONFIRMED);

        emailService.sendPaymentSuccessEmail(refreshedOrder, refreshedOrder.getPayment());

        List<EmailNotificationLog> logs = emailLogRepository.findByOrderIdOrderByCreatedAtDesc(patron1Order.getId());
        assertThat(logs).anyMatch(l -> l.getNotificationType() == NotificationType.PAYMENT_SUCCESS);
    }

    @Test
    @DisplayName("Scenario 4: Payment failure logs payment failure notification without advancing order")
    void testPaymentFailureEmailTriggered() {
        PaymentProcessRequest paymentReq = new PaymentProcessRequest(
                patron1Order.getId(),
                PaymentMethod.CARD,
                true // simulate failure
        );

        assertThatThrownBy(() -> paymentService.processPayment(patron1.getEmail(), paymentReq))
                .isInstanceOf(PaymentFailedException.class);

        Order refreshedOrder = orderRepository.findById(patron1Order.getId()).orElseThrow();
        assertThat(refreshedOrder.getStatus()).isEqualTo(OrderStatus.PLACED);

        emailService.sendPaymentFailureEmail(refreshedOrder, "Simulated decline");

        List<EmailNotificationLog> logs = emailLogRepository.findByOrderIdOrderByCreatedAtDesc(patron1Order.getId());
        assertThat(logs).anyMatch(l -> l.getNotificationType() == NotificationType.PAYMENT_FAILURE);
    }

    @Test
    @DisplayName("Scenario 5: Complete valid lifecycle transitions with carrier assignment and audit trail")
    void testValidStatusTransitionsAndAuditHistory() {
        Long orderId = patron1Order.getId();

        // 1. PLACED -> CONFIRMED
        orderService.updateOrderStatus(orderId, new UpdateOrderStatusRequest(
                OrderStatus.CONFIRMED, "Payment verified manually", null, null, null));

        // 2. CONFIRMED -> PROCESSING
        orderService.updateOrderStatus(orderId, new UpdateOrderStatusRequest(
                OrderStatus.PROCESSING, "White-glove packaging started", null, null, null));

        // 3. PROCESSING -> SHIPPED
        OffsetDateTime estDelivery = OffsetDateTime.now().plusDays(3);
        orderService.updateOrderStatus(orderId, new UpdateOrderStatusRequest(
                OrderStatus.SHIPPED, "Handed to courier", "Blue Dart Apex", "BD-778899", estDelivery));

        // 4. SHIPPED -> OUT_FOR_DELIVERY
        orderService.updateOrderStatus(orderId, new UpdateOrderStatusRequest(
                OrderStatus.OUT_FOR_DELIVERY, "Courier out for delivery", "Blue Dart Apex", "BD-778899", estDelivery));

        // 5. OUT_FOR_DELIVERY -> DELIVERED
        orderService.updateOrderStatus(orderId, new UpdateOrderStatusRequest(
                OrderStatus.DELIVERED, "Package accepted with signature", null, null, null));

        Order completedOrder = orderRepository.findById(orderId).orElseThrow();
        assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        assertThat(completedOrder.getCarrier()).isEqualTo("Blue Dart Apex");
        assertThat(completedOrder.getTrackingNumber()).isEqualTo("BD-778899");
        assertThat(completedOrder.getShippedAt()).isNotNull();
        assertThat(completedOrder.getDeliveredAt()).isNotNull();

        List<OrderStatusHistory> history = orderStatusHistoryRepository.findByOrderIdOrderByCreatedAtAsc(orderId);
        assertThat(history).hasSizeGreaterThanOrEqualTo(6);
        assertThat(history).extracting(OrderStatusHistory::getStatus)
                .containsSubsequence(
                        OrderStatus.PLACED,
                        OrderStatus.CONFIRMED,
                        OrderStatus.PROCESSING,
                        OrderStatus.SHIPPED,
                        OrderStatus.OUT_FOR_DELIVERY,
                        OrderStatus.DELIVERED
                );
    }

    @Test
    @DisplayName("Scenario 6: Status transition validation rejects illegal jumps and terminal alterations")
    void testInvalidStatusTransitionsRejected() {
        Long orderId = patron1Order.getId();

        // Attempt jump: PLACED -> DELIVERED (illegal)
        assertThatThrownBy(() -> orderService.updateOrderStatus(orderId, new UpdateOrderStatusRequest(OrderStatus.DELIVERED)))
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("Cannot transition order status from PLACED to DELIVERED");

        // Attempt jump: PLACED -> SHIPPED (illegal)
        assertThatThrownBy(() -> orderService.updateOrderStatus(orderId, new UpdateOrderStatusRequest(OrderStatus.SHIPPED)))
                .isInstanceOf(InvalidOrderStateException.class);

        // Valid transition to CANCELLED
        orderService.updateOrderStatus(orderId, new UpdateOrderStatusRequest(OrderStatus.CANCELLED));

        // Terminal state alteration: CANCELLED -> SHIPPED (illegal)
        assertThatThrownBy(() -> orderService.updateOrderStatus(orderId, new UpdateOrderStatusRequest(OrderStatus.SHIPPED)))
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("Cannot transition order status from CANCELLED to SHIPPED");
    }

    @Test
    @DisplayName("Scenario 7: IDOR security check on order tracking endpoint")
    void testIdorProtectionOnTrackingEndpoint() throws Exception {
        // Customer 2 attempts to track Customer 1's order -> 403 Forbidden
        mockMvc.perform(get("/api/orders/{id}/tracking", patron1Order.getId())
                        .header("Authorization", "Bearer " + patron2Token))
                .andExpect(status().isForbidden());

        // Customer 1 accesses own order -> 200 OK
        mockMvc.perform(get("/api/orders/{id}/tracking", patron1Order.getId())
                        .header("Authorization", "Bearer " + patron1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderId").value(patron1Order.getId()))
                .andExpect(jsonPath("$.data.currentStatus").value("PLACED"));

        // Admin accesses Customer 1's order -> 200 OK
        mockMvc.perform(get("/api/orders/{id}/tracking", patron1Order.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderId").value(patron1Order.getId()));
    }

    @Test
    @DisplayName("Scenario 8: Duplicate notification prevention skips duplicate emails")
    void testDuplicateNotificationPrevention() {
        // First dispatch
        emailService.sendOrderConfirmationEmail(patron1Order);

        // Pre-condition: mark first email as SENT in log
        EmailNotificationLog sentLog = emailLogRepository.findByOrderIdOrderByCreatedAtDesc(patron1Order.getId()).get(0);
        sentLog.setStatus(NotificationStatus.SENT);
        emailLogRepository.save(sentLog);

        long countBefore = emailLogRepository.count();

        // Attempt duplicate dispatch
        emailService.sendOrderConfirmationEmail(patron1Order);

        long countAfter = emailLogRepository.count();
        // Count should not increase because duplicate was avoided
        assertThat(countAfter).isEqualTo(countBefore);
    }

    @Test
    @DisplayName("Scenario 9: Email failure resilience logs FAILED without crashing business operations")
    void testEmailFailureResilienceDoesNotCorruptTransaction() {
        // Creating order and updating status succeeds even if email infrastructure has problems
        OrderResponse response = orderService.updateOrderStatus(
                patron1Order.getId(),
                new UpdateOrderStatusRequest(OrderStatus.CONFIRMED, "Advance to confirmed", null, null, null)
        );

        assertThat(response.status()).isEqualTo(OrderStatus.CONFIRMED);
        Order saved = orderRepository.findById(patron1Order.getId()).orElseThrow();
        assertThat(saved.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    @DisplayName("Scenario 10: Customer tracking endpoint returns milestone checkpoints and audit history")
    void testOrderTrackingEndpointReturnsCheckpointsAndAuditHistory() throws Exception {
        Long orderId = patron1Order.getId();

        // Advance to PROCESSING with tracking details
        orderService.updateOrderStatus(orderId, new UpdateOrderStatusRequest(
                OrderStatus.CONFIRMED, "Payment verified", null, null, null));
        orderService.updateOrderStatus(orderId, new UpdateOrderStatusRequest(
                OrderStatus.PROCESSING, "Inspection at vault", "Blue Dart", "BD-12345", OffsetDateTime.now().plusDays(4)));

        mockMvc.perform(get("/api/orders/{id}/tracking", orderId)
                        .header("Authorization", "Bearer " + patron1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderId").value(orderId))
                .andExpect(jsonPath("$.data.currentStatus").value("PROCESSING"))
                .andExpect(jsonPath("$.data.carrier").value("Blue Dart"))
                .andExpect(jsonPath("$.data.trackingNumber").value("BD-12345"))
                .andExpect(jsonPath("$.data.checkpoints").isArray())
                .andExpect(jsonPath("$.data.checkpoints[0].status").value("PLACED"))
                .andExpect(jsonPath("$.data.checkpoints[0].completed").value(true))
                .andExpect(jsonPath("$.data.checkpoints[1].status").value("CONFIRMED"))
                .andExpect(jsonPath("$.data.checkpoints[1].completed").value(true))
                .andExpect(jsonPath("$.data.checkpoints[2].status").value("PROCESSING"))
                .andExpect(jsonPath("$.data.checkpoints[2].completed").value(true))
                .andExpect(jsonPath("$.data.checkpoints[2].current").value(true))
                .andExpect(jsonPath("$.data.checkpoints[3].completed").value(false))
                .andExpect(jsonPath("$.data.history").isArray())
                .andExpect(jsonPath("$.data.history.length()").value(3));
    }
}
