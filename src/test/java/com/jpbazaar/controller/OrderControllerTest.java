package com.jpbazaar.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpbazaar.dto.request.AddToCartRequest;
import com.jpbazaar.dto.request.CreateOrderRequest;
import com.jpbazaar.dto.request.UpdateOrderStatusRequest;
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
class OrderControllerTest {

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
    private Long customer2AddressId;

    private String adminToken;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        customer1Email = "cust1_" + System.nanoTime() + "@order-test.com";
        customer1Token = getOrCreateUserToken(customer1Email, Role.CUSTOMER);
        User user1 = userRepository.findByEmail(customer1Email).orElseThrow();
        Address addr1 = addressRepository.save(Address.builder()
                .user(user1)
                .addressLine("100 Tech Blvd")
                .city("San Jose")
                .state("CA")
                .postalCode("95110")
                .country("USA")
                .isDefault(true)
                .build());
        customer1AddressId = addr1.getId();

        customer2Email = "cust2_" + System.nanoTime() + "@order-test.com";
        customer2Token = getOrCreateUserToken(customer2Email, Role.CUSTOMER);
        User user2 = userRepository.findByEmail(customer2Email).orElseThrow();
        Address addr2 = addressRepository.save(Address.builder()
                .user(user2)
                .addressLine("200 Sunset Strip")
                .city("Los Angeles")
                .state("CA")
                .postalCode("90028")
                .country("USA")
                .isDefault(true)
                .build());
        customer2AddressId = addr2.getId();

        String adminEmail = "admin_" + System.nanoTime() + "@order-test.com";
        adminToken = getOrCreateUserToken(adminEmail, Role.ADMIN);

        Category category = categoryRepository.save(Category.builder()
                .name("OrderCategory_" + System.nanoTime())
                .description("Category for order tests")
                .build());

        testProduct = productRepository.save(Product.builder()
                .name("Test Gaming Laptop")
                .description("High-end specs")
                .price(new BigDecimal("1500.00"))
                .stockQuantity(10)
                .sku("LAP-TEST-" + System.nanoTime())
                .brand("PowerBrand")
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

    private void addItemToCart(String token, Long productId, int quantity) throws Exception {
        AddToCartRequest request = new AddToCartRequest(productId, quantity);
        mockMvc.perform(post("/api/cart/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void placeOrder_Success() throws Exception {
        // 1. Add 2 units of testProduct (stock: 10, price: 1500.00) to customer1 cart
        addItemToCart(customer1Token, testProduct.getId(), 2);

        // 2. Place order
        CreateOrderRequest orderRequest = new CreateOrderRequest(customer1AddressId, "Please leave at front door");
        MvcResult result = mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + customer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PLACED"))
                .andExpect(jsonPath("$.data.totalAmount").value(3000.00))
                .andExpect(jsonPath("$.data.totalItemCount").value(2))
                .andExpect(jsonPath("$.data.items[0].productName").value("Test Gaming Laptop"))
                .andExpect(jsonPath("$.data.items[0].price").value(1500.00))
                .andExpect(jsonPath("$.data.items[0].quantity").value(2))
                .andExpect(jsonPath("$.data.items[0].subtotal").value(3000.00))
                .andReturn();

        // 3. Verify stock quantity decremented from 10 to 8
        Product refreshedProduct = productRepository.findById(testProduct.getId()).orElseThrow();
        assertThat(refreshedProduct.getStockQuantity()).isEqualTo(8);

        // 4. Verify cart items cleared
        Cart cart = cartRepository.findByUserEmailWithItems(customer1Email).orElseThrow();
        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    void placeOrder_WhenCartIsEmpty_ShouldReturnBadRequest() throws Exception {
        CreateOrderRequest orderRequest = new CreateOrderRequest(customer1AddressId, "Empty cart checkout");
        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + customer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cannot place order with an empty cart"));
    }

    @Test
    void placeOrder_WhenInsufficientStock_ShouldReturnBadRequest() throws Exception {
        addItemToCart(customer1Token, testProduct.getId(), 3);

        // Simulate concurrent stock reduction to 2
        testProduct.setStockQuantity(2);
        productRepository.save(testProduct);

        CreateOrderRequest orderRequest = new CreateOrderRequest(customer1AddressId);
        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + customer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Insufficient stock")));
    }

    @Test
    void placeOrder_WhenAddressBelongsToAnotherUser_ShouldReturnBadRequest() throws Exception {
        addItemToCart(customer1Token, testProduct.getId(), 1);

        // customer1 tries to use customer2AddressId
        CreateOrderRequest orderRequest = new CreateOrderRequest(customer2AddressId);
        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + customer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Shipping address not found or does not belong to user"));
    }

    @Test
    void getOrderById_Success_And_IDOR_Protection() throws Exception {
        addItemToCart(customer1Token, testProduct.getId(), 1);
        CreateOrderRequest orderRequest = new CreateOrderRequest(customer1AddressId);
        MvcResult result = mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + customer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        long orderId = root.get("data").get("id").asLong();

        // 1. Owner can view order
        mockMvc.perform(get("/api/orders/" + orderId)
                        .header("Authorization", "Bearer " + customer1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(orderId))
                .andExpect(jsonPath("$.data.userEmail").value(customer1Email));

        // 2. IDOR Attack: customer2 cannot view customer1's order
        mockMvc.perform(get("/api/orders/" + orderId)
                        .header("Authorization", "Bearer " + customer2Token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You are not authorized to view this order"));

        // 3. Admin can view any order
        mockMvc.perform(get("/api/orders/" + orderId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(orderId));
    }

    @Test
    void getMyOrders_ShouldReturnPagedOrders() throws Exception {
        addItemToCart(customer1Token, testProduct.getId(), 1);
        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + customer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateOrderRequest(customer1AddressId))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/orders")
                        .header("Authorization", "Bearer " + customer1Token)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].items[0].productName").value("Test Gaming Laptop"));
    }

    @Test
    void cancelOrder_Success_ShouldRestoreStock() throws Exception {
        int initialStock = testProduct.getStockQuantity(); // 10
        addItemToCart(customer1Token, testProduct.getId(), 4);

        MvcResult result = mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + customer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateOrderRequest(customer1AddressId))))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        long orderId = root.get("data").get("id").asLong();

        // Verify stock dropped to 6
        assertThat(productRepository.findById(testProduct.getId()).orElseThrow().getStockQuantity()).isEqualTo(initialStock - 4);

        // Cancel order
        mockMvc.perform(put("/api/orders/" + orderId + "/cancel")
                        .header("Authorization", "Bearer " + customer1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));

        // Verify stock restored to initial 10
        assertThat(productRepository.findById(testProduct.getId()).orElseThrow().getStockQuantity()).isEqualTo(initialStock);
    }

    @Test
    void cancelOrder_BelongsToAnotherUser_ShouldReturnForbidden() throws Exception {
        addItemToCart(customer1Token, testProduct.getId(), 1);
        MvcResult result = mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + customer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateOrderRequest(customer1AddressId))))
                .andExpect(status().isCreated())
                .andReturn();

        long orderId = objectMapper.readTree(result.getResponse().getContentAsString()).get("data").get("id").asLong();

        // customer2 attempts to cancel customer1's order
        mockMvc.perform(put("/api/orders/" + orderId + "/cancel")
                        .header("Authorization", "Bearer " + customer2Token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You are not authorized to cancel this order"));
    }

    @Test
    void updateOrderStatus_AdminLifecycleTransitions_Success() throws Exception {
        addItemToCart(customer1Token, testProduct.getId(), 1);
        MvcResult result = mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + customer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateOrderRequest(customer1AddressId))))
                .andExpect(status().isCreated())
                .andReturn();

        long orderId = objectMapper.readTree(result.getResponse().getContentAsString()).get("data").get("id").asLong();

        // PLACED -> CONFIRMED
        mockMvc.perform(put("/api/orders/" + orderId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateOrderStatusRequest(OrderStatus.CONFIRMED))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"));

        // CONFIRMED -> PROCESSING
        mockMvc.perform(put("/api/orders/" + orderId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateOrderStatusRequest(OrderStatus.PROCESSING))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PROCESSING"));

        // PROCESSING -> SHIPPED
        mockMvc.perform(put("/api/orders/" + orderId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateOrderStatusRequest(OrderStatus.SHIPPED))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("SHIPPED"));

        // SHIPPED -> OUT_FOR_DELIVERY
        mockMvc.perform(put("/api/orders/" + orderId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateOrderStatusRequest(OrderStatus.OUT_FOR_DELIVERY))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("OUT_FOR_DELIVERY"));

        // OUT_FOR_DELIVERY -> DELIVERED
        mockMvc.perform(put("/api/orders/" + orderId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateOrderStatusRequest(OrderStatus.DELIVERED))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("DELIVERED"));
    }

    @Test
    void updateOrderStatus_InvalidTransition_ShouldReturnBadRequest() throws Exception {
        addItemToCart(customer1Token, testProduct.getId(), 1);
        MvcResult result = mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + customer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateOrderRequest(customer1AddressId))))
                .andExpect(status().isCreated())
                .andReturn();

        long orderId = objectMapper.readTree(result.getResponse().getContentAsString()).get("data").get("id").asLong();

        // Advance to DELIVERED
        mockMvc.perform(put("/api/orders/" + orderId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateOrderStatusRequest(OrderStatus.CONFIRMED))));
        mockMvc.perform(put("/api/orders/" + orderId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateOrderStatusRequest(OrderStatus.PROCESSING))));
        mockMvc.perform(put("/api/orders/" + orderId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateOrderStatusRequest(OrderStatus.SHIPPED))));
        mockMvc.perform(put("/api/orders/" + orderId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateOrderStatusRequest(OrderStatus.OUT_FOR_DELIVERY))));
        mockMvc.perform(put("/api/orders/" + orderId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateOrderStatusRequest(OrderStatus.DELIVERED))));

        // Attempt DELIVERED -> CANCELLED (Strictly forbidden by business rules)
        mockMvc.perform(put("/api/orders/" + orderId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateOrderStatusRequest(OrderStatus.CANCELLED))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Cannot transition order status from DELIVERED to CANCELLED")));

        // Customer cannot cancel an already DELIVERED order
        mockMvc.perform(put("/api/orders/" + orderId + "/cancel")
                        .header("Authorization", "Bearer " + customer1Token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Order cannot be cancelled in status: DELIVERED")));
    }

    @Test
    void adminEndpoints_WhenAccessedByCustomer_ShouldReturnForbidden() throws Exception {
        // Customer accessing /api/admin/orders
        mockMvc.perform(get("/api/admin/orders")
                        .header("Authorization", "Bearer " + customer1Token))
                .andExpect(status().isForbidden());

        // Customer attempting to update status
        mockMvc.perform(put("/api/orders/1/status")
                        .header("Authorization", "Bearer " + customer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateOrderStatusRequest(OrderStatus.CONFIRMED))))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllOrders_AdminSuccess() throws Exception {
        addItemToCart(customer1Token, testProduct.getId(), 1);
        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + customer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateOrderRequest(customer1AddressId))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/admin/orders")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)));
    }
}
