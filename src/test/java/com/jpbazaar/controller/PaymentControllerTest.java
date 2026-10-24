package com.jpbazaar.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpbazaar.dto.request.AddToCartRequest;
import com.jpbazaar.dto.request.CreateOrderRequest;
import com.jpbazaar.dto.request.PaymentProcessRequest;
import com.jpbazaar.entity.*;
import com.jpbazaar.repository.*;
import com.jpbazaar.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PaymentControllerTest {

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
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private String customer1Email;
    private String customer1Token;
    private Long customer1AddressId;

    private String customer2Email;
    private String customer2Token;

    private String adminToken;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        customer1Email = "pay_cust1_" + System.nanoTime() + "@test.com";
        customer1Token = getOrCreateUserToken(customer1Email, Role.CUSTOMER);
        User user1 = userRepository.findByEmail(customer1Email).orElseThrow();
        Address addr1 = addressRepository.save(Address.builder()
                .user(user1)
                .addressLine("456 Market St")
                .city("San Francisco")
                .state("CA")
                .postalCode("94105")
                .country("USA")
                .isDefault(true)
                .build());
        customer1AddressId = addr1.getId();

        customer2Email = "pay_cust2_" + System.nanoTime() + "@test.com";
        customer2Token = getOrCreateUserToken(customer2Email, Role.CUSTOMER);

        String adminEmail = "pay_admin_" + System.nanoTime() + "@test.com";
        adminToken = getOrCreateUserToken(adminEmail, Role.ADMIN);

        Category category = categoryRepository.save(Category.builder()
                .name("PaymentCategory_" + System.nanoTime())
                .description("Category for payment tests")
                .build());

        testProduct = productRepository.save(Product.builder()
                .name("Smartphone Pro")
                .description("Flagship mobile device")
                .price(new BigDecimal("800.00"))
                .stockQuantity(15)
                .sku("PHONE-PRO-" + System.nanoTime())
                .brand("OmniTech")
                .category(category)
                .active(true)
                .build());
    }

    private String getOrCreateUserToken(String email, Role role) {
        if (!userRepository.existsByEmail(email)) {
            User user = User.builder()
                    .firstName("Test")
                    .lastName("User")
                    .email(email)
                    .password(passwordEncoder.encode("password123"))
                    .role(role)
                    .enabled(true)
                    .build();
            userRepository.save(user);
        }

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, "password123")
        );
        return tokenProvider.generateToken(auth);
    }

    private Long createOrderForCustomer1(PaymentMethod method) throws Exception {
        AddToCartRequest cartRequest = new AddToCartRequest(testProduct.getId(), 1);
        mockMvc.perform(post("/api/cart/items")
                        .header("Authorization", "Bearer " + customer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartRequest)))
                .andExpect(status().isCreated());

        CreateOrderRequest orderRequest = new CreateOrderRequest(customer1AddressId, method, "Payment test order");
        MvcResult result = mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + customer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.get("data").get("id").asLong();
    }

    @Test
    void processPayment_CardSuccess_ShouldTransitionOrderToConfirmed() throws Exception {
        Long orderId = createOrderForCustomer1(PaymentMethod.CARD);

        PaymentProcessRequest paymentRequest = new PaymentProcessRequest(orderId, PaymentMethod.CARD, false);
        mockMvc.perform(post("/api/payments/process")
                        .header("Authorization", "Bearer " + customer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderId").value(orderId))
                .andExpect(jsonPath("$.data.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.method").value("CARD"))
                .andExpect(jsonPath("$.data.transactionId").value(org.hamcrest.Matchers.startsWith("TXN-")));

        // Order should auto-transition from PLACED to CONFIRMED
        Order updatedOrder = orderRepository.findById(orderId).orElseThrow();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    void processPayment_UpiSuccess() throws Exception {
        Long orderId = createOrderForCustomer1(PaymentMethod.UPI);

        PaymentProcessRequest paymentRequest = new PaymentProcessRequest(orderId, PaymentMethod.UPI, false);
        mockMvc.perform(post("/api/payments/process")
                        .header("Authorization", "Bearer " + customer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.method").value("UPI"));
    }

    @Test
    void processPayment_SimulatedFailure_ShouldReturnBadRequest() throws Exception {
        Long orderId = createOrderForCustomer1(PaymentMethod.CARD);

        PaymentProcessRequest paymentRequest = new PaymentProcessRequest(orderId, PaymentMethod.CARD, true);
        mockMvc.perform(post("/api/payments/process")
                        .header("Authorization", "Bearer " + customer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("declined the transaction")));

        // Payment record in DB should record FAILED
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }

    @Test
    void processPayment_AlreadyCompleted_ShouldReturnBadRequest() throws Exception {
        Long orderId = createOrderForCustomer1(PaymentMethod.CARD);

        // First payment succeeds
        PaymentProcessRequest paymentRequest = new PaymentProcessRequest(orderId, PaymentMethod.CARD, false);
        mockMvc.perform(post("/api/payments/process")
                        .header("Authorization", "Bearer " + customer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk());

        // Second payment attempt rejected
        mockMvc.perform(post("/api/payments/process")
                        .header("Authorization", "Bearer " + customer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Payment has already been successfully completed for this order"));
    }

    @Test
    void getPaymentByOrderId_Success_And_IDOR_Protection() throws Exception {
        Long orderId = createOrderForCustomer1(PaymentMethod.CARD);

        // Process payment
        mockMvc.perform(post("/api/payments/process")
                        .header("Authorization", "Bearer " + customer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PaymentProcessRequest(orderId, PaymentMethod.CARD))))
                .andExpect(status().isOk());

        // 1. Owner can view payment
        mockMvc.perform(get("/api/payments/orders/" + orderId)
                        .header("Authorization", "Bearer " + customer1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderId").value(orderId))
                .andExpect(jsonPath("$.data.status").value("SUCCESS"));

        // 2. IDOR Attack: customer2 cannot view customer1's payment
        mockMvc.perform(get("/api/payments/orders/" + orderId)
                        .header("Authorization", "Bearer " + customer2Token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You are not authorized to view payment details for this order"));

        // 3. Admin can view payment
        mockMvc.perform(get("/api/payments/orders/" + orderId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderId").value(orderId));
    }

    @Test
    void refundPayment_AdminSuccess() throws Exception {
        Long orderId = createOrderForCustomer1(PaymentMethod.CARD);

        // Process payment first
        mockMvc.perform(post("/api/payments/process")
                        .header("Authorization", "Bearer " + customer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PaymentProcessRequest(orderId, PaymentMethod.CARD))))
                .andExpect(status().isOk());

        // Admin refunds payment
        mockMvc.perform(post("/api/payments/orders/" + orderId + "/refund")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("REFUNDED"))
                .andExpect(jsonPath("$.data.transactionId").value(org.hamcrest.Matchers.startsWith("REF-")));
    }

    @Test
    void refundPayment_WhenNonAdmin_ShouldReturnForbidden() throws Exception {
        Long orderId = createOrderForCustomer1(PaymentMethod.CARD);

        mockMvc.perform(post("/api/payments/orders/" + orderId + "/refund")
                        .header("Authorization", "Bearer " + customer1Token))
                .andExpect(status().isForbidden());
    }

    @Test
    void cancelOrder_AutomaticallyRefundsPayment() throws Exception {
        Long orderId = createOrderForCustomer1(PaymentMethod.CARD);

        // Process payment
        mockMvc.perform(post("/api/payments/process")
                        .header("Authorization", "Bearer " + customer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PaymentProcessRequest(orderId, PaymentMethod.CARD))))
                .andExpect(status().isOk());

        // Customer cancels order
        mockMvc.perform(put("/api/orders/" + orderId + "/cancel")
                        .header("Authorization", "Bearer " + customer1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));

        // Payment record in DB should be REFUNDED
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
        assertThat(payment.getTransactionId()).startsWith("REF-");
    }
}
