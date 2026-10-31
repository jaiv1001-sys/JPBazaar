package com.jpbazaar;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpbazaar.dto.request.*;
import com.jpbazaar.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jpbazaar.entity.User;
import com.jpbazaar.entity.Role;
import com.jpbazaar.entity.Product;
import com.jpbazaar.entity.OrderStatus;
import com.jpbazaar.entity.PaymentStatus;
import com.jpbazaar.entity.PaymentMethod;
import com.jpbazaar.entity.Payment;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@TestPropertySource(properties = {
        "app.security.rate-limit.enabled=false"
})
@Tag("live-e2e")
@EnabledIfSystemProperty(named = "liveE2E", matches = "true")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomerJourneyLiveE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private WishlistRepository wishlistRepository;

    private static String customerAEmail;
    private static String customerAPassword;
    private static String customerAToken;
    private static Long customerAId;

    private static String customerBEmail;
    private static String customerBPassword;
    private static String customerBToken;
    private static Long customerBId;

    private static Long targetProductId;
    private static BigDecimal targetProductPrice;
    private static int targetProductOriginalStock;

    private static Long customerAAddressId;
    private static Long customerAOrderId;

    @BeforeAll
    static void initCredentials() {
        String uid = UUID.randomUUID().toString().substring(0, 8);
        customerAEmail = "journey_alpha_" + uid + "@jpbazaar.test";
        customerAPassword = "Password@123";

        customerBEmail = "journey_beta_" + uid + "@jpbazaar.test";
        customerBPassword = "Password@456";
    }

    // Helper method to extract "data" field from ApiResponse
    private JsonNode extractData(MvcResult result) throws Exception {
        String json = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(json);
        assertThat(root.has("data")).isTrue();
        return root.get("data");
    }

    @Test
    @Order(1)
    @DisplayName("REQ 1: Customer Authentication, Token Issuance, and RBAC Boundaries")
    void test01_AuthenticationAndRbac() throws Exception {
        System.out.println("\n=======================================================");
        System.out.println(">>> 1. TESTING CUSTOMER AUTHENTICATION & RBAC BOUNDARIES");
        System.out.println("=======================================================");

        // 1.1 Customer Registration
        RegisterRequest registerReq = new RegisterRequest(
                "Alexander", "Vance", customerAEmail, customerAPassword, "+1555123456"
        );

        MvcResult regResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode regData = extractData(regResult);
        customerAToken = regData.get("token").asText();
        customerAId = regData.get("userId").asLong();
        assertThat(customerAToken).isNotBlank();
        assertThat(regData.get("role").asText()).isEqualTo("CUSTOMER");
        System.out.println("✓ Customer A registered successfully: " + customerAEmail + " (ID: " + customerAId + ")");

        // Verify PostgreSQL persistence for registered user
        User userInDb = userRepository.findByEmail(customerAEmail).orElse(null);
        assertThat(userInDb).isNotNull();
        assertThat(userInDb.getEnabled()).isTrue();
        assertThat(userInDb.getRole()).isEqualTo(Role.CUSTOMER);
        System.out.println("✓ PostgreSQL user persistence verified (enabled=true, role=CUSTOMER)");

        // 1.2 Customer Login with valid credentials
        LoginRequest loginReq = new LoginRequest(customerAEmail, customerAPassword);
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode loginData = extractData(loginResult);
        assertThat(loginData.get("token").asText()).isNotBlank();
        System.out.println("✓ Customer A login successful, JWT token acquired");

        // 1.3 Invalid credentials validation
        LoginRequest invalidLogin = new LoginRequest(customerAEmail, "WrongPassword@999");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLogin)))
                .andExpect(status().isUnauthorized());
        System.out.println("✓ Invalid password correctly rejected with 401 Unauthorized");

        // 1.4 Profile access with valid JWT token
        MvcResult profileResult = mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + customerAToken))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode profileData = extractData(profileResult);
        assertThat(profileData.get("email").asText()).isEqualTo(customerAEmail);
        System.out.println("✓ Protected profile access verified with Bearer JWT");

        // 1.5 Protected API access without token
        mockMvc.perform(get("/api/cart"))
                .andExpect(result -> assertThat(result.getResponse().getStatus()).isIn(401, 403));
        System.out.println("✓ Unauthenticated access to /api/cart safely rejected (401/403)");

        // 1.6 Role-based authorization: Customer cannot access Admin endpoints
        mockMvc.perform(get("/api/admin/orders")
                        .header("Authorization", "Bearer " + customerAToken))
                .andExpect(status().isForbidden());
        System.out.println("✓ RBAC confirmed: Customer token blocked from /api/admin/orders (403 Forbidden)");

        // Register Customer B for multi-user IDOR isolation tests
        RegisterRequest regB = new RegisterRequest(
                "Elena", "Rostova", customerBEmail, customerBPassword, "+1555987654"
        );
        MvcResult regBResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regB)))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode bData = extractData(regBResult);
        customerBToken = bData.get("token").asText();
        customerBId = bData.get("userId").asLong();
        System.out.println("✓ Customer B registered for IDOR security tests: " + customerBEmail);
    }

    @Test
    @Order(2)
    @DisplayName("REQ 2: Product Discovery, Multi-parameter Search, Filters, and Availability")
    void test02_ProductDiscovery() throws Exception {
        System.out.println("\n=======================================================");
        System.out.println(">>> 2. TESTING PRODUCT DISCOVERY & AVAILABILITY");
        System.out.println("=======================================================");

        // 2.1 Full Catalogue Listing
        MvcResult catResult = mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "10")
                        .param("active", "true"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode catData = extractData(catResult);
        assertThat(catData.get("totalElements").asInt()).isEqualTo(260);
        System.out.println("✓ Catalog retrieval verified: 260 total live products");

        // Choose a stable target product with healthy stock
        JsonNode firstProd = catData.get("content").get(0);
        targetProductId = firstProd.get("id").asLong();
        targetProductPrice = new BigDecimal(firstProd.get("price").asText());
        targetProductOriginalStock = firstProd.get("stockQuantity").asInt();

        System.out.println("✓ Selected target product for journey testing:");
        System.out.println("  ID: " + targetProductId + ", Name: '" + firstProd.get("name").asText()
                + "', Price: $" + targetProductPrice + ", Stock: " + targetProductOriginalStock);
        assertThat(targetProductOriginalStock).isGreaterThanOrEqualTo(5);

        // 2.2 Category Filtering (Electronics: categoryId=1)
        MvcResult filterResult = mockMvc.perform(get("/api/products")
                        .param("categoryId", "1"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode filterData = extractData(filterResult);
        for (JsonNode prod : filterData.get("content")) {
            assertThat(prod.get("categoryId").asLong()).isEqualTo(1L);
        }
        System.out.println("✓ Category filtering verified: all returned items belong to category 1");

        // 2.3 Keyword Search
        MvcResult searchResult = mockMvc.perform(get("/api/products")
                        .param("keyword", "Wireless"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode searchData = extractData(searchResult);
        assertThat(searchData.get("totalElements").asInt()).isGreaterThan(0);
        System.out.println("✓ Search verified: " + searchData.get("totalElements").asInt() + " items match 'Wireless'");

        // 2.4 Price Sorting
        MvcResult sortResult = mockMvc.perform(get("/api/products")
                        .param("sortBy", "price")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode sortData = extractData(sortResult);
        double prev = -1.0;
        for (JsonNode p : sortData.get("content")) {
            double cur = p.get("price").asDouble();
            assertThat(cur).isGreaterThanOrEqualTo(prev);
            prev = cur;
        }
        System.out.println("✓ Sorting verified: strictly ascending price verified");

        // 2.5 Product details endpoint
        MvcResult detailResult = mockMvc.perform(get("/api/products/" + targetProductId))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode detailData = extractData(detailResult);
        assertThat(detailData.get("id").asLong()).isEqualTo(targetProductId);
        assertThat(detailData.has("description")).isTrue();
        assertThat(detailData.has("imageUrls")).isTrue();
        System.out.println("✓ Product details endpoint verified with complete specification attributes");
    }

    @Test
    @Order(3)
    @DisplayName("REQ 3: Wishlist Operations, Persistence, and Duplicate Prevention")
    void test03_WishlistOperations() throws Exception {
        System.out.println("\n=======================================================");
        System.out.println(">>> 3. TESTING WISHLIST LIFECYCLE & PERSISTENCE");
        System.out.println("=======================================================");

        // 3.1 Add product to Wishlist
        AddToWishlistRequest wishReq = new AddToWishlistRequest(targetProductId);
        MvcResult addResult = mockMvc.perform(post("/api/wishlist/items")
                        .header("Authorization", "Bearer " + customerAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wishReq)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode addData = extractData(addResult);
        assertThat(addData.get("totalItems").asInt()).isEqualTo(1);
        System.out.println("✓ Product added to Customer A wishlist");

        // 3.2 Duplicate prevention (409 Conflict)
        mockMvc.perform(post("/api/wishlist/items")
                        .header("Authorization", "Bearer " + customerAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wishReq)))
                .andExpect(status().isConflict());
        System.out.println("✓ Duplicate wishlist entry prevented (409 Conflict)");

        // 3.3 Verify Wishlist retrieval & persistence
        MvcResult getResult = mockMvc.perform(get("/api/wishlist")
                        .header("Authorization", "Bearer " + customerAToken))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode getData = extractData(getResult);
        assertThat(getData.get("totalItems").asInt()).isEqualTo(1);
        assertThat(getData.get("items").get(0).get("productId").asLong()).isEqualTo(targetProductId);
        System.out.println("✓ Wishlist persistence confirmed in PostgreSQL");

        // 3.4 Remove product from Wishlist
        mockMvc.perform(delete("/api/wishlist/products/" + targetProductId)
                        .header("Authorization", "Bearer " + customerAToken))
                .andExpect(status().isOk());

        MvcResult emptyResult = mockMvc.perform(get("/api/wishlist")
                        .header("Authorization", "Bearer " + customerAToken))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(extractData(emptyResult).get("totalItems").asInt()).isEqualTo(0);
        System.out.println("✓ Product removed from wishlist, wishlist empty");
    }

    @Test
    @Order(4)
    @DisplayName("REQ 4: Address Management, Default Address, and IDOR Protection")
    void test04_AddressManagementAndIdor() throws Exception {
        System.out.println("\n=======================================================");
        System.out.println(">>> 4. TESTING ADDRESS MANAGEMENT & IDOR SECURITY");
        System.out.println("=======================================================");

        // 4.1 Add Address for Customer A
        AddressRequest addrReq = new AddressRequest(
                "742 Evergreen Terrace", "Springfield", "Oregon", "97477", "United States", false
        );

        MvcResult addResult = mockMvc.perform(post("/api/addresses")
                        .header("Authorization", "Bearer " + customerAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addrReq)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode addrData = extractData(addResult);
        customerAAddressId = addrData.get("id").asLong();
        assertThat(customerAAddressId).isNotNull();
        System.out.println("✓ Address created for Customer A (ID: " + customerAAddressId + ")");

        // 4.2 Set as default address
        MvcResult defaultResult = mockMvc.perform(put("/api/addresses/" + customerAAddressId + "/set-default")
                        .header("Authorization", "Bearer " + customerAToken))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(extractData(defaultResult).get("isDefault").asBoolean()).isTrue();
        System.out.println("✓ Address designated as default shipping address");

        // 4.3 Update address
        AddressRequest updateReq = new AddressRequest(
                "742 Evergreen Terrace Suite 200", "Springfield", "Oregon", "97477", "United States", true
        );
        MvcResult updateResult = mockMvc.perform(put("/api/addresses/" + customerAAddressId)
                        .header("Authorization", "Bearer " + customerAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(extractData(updateResult).get("addressLine").asText()).contains("Suite 200");
        System.out.println("✓ Address successfully updated and persisted");

        // 4.4 IDOR Security: Customer B attempts to access Customer A's address
        mockMvc.perform(get("/api/addresses/" + customerAAddressId)
                        .header("Authorization", "Bearer " + customerBToken))
                .andExpect(status().isForbidden());
        System.out.println("✓ IDOR Guard: Customer B blocked from viewing Customer A's address (403 Forbidden)");

        // Customer B attempts to update Customer A's address
        mockMvc.perform(put("/api/addresses/" + customerAAddressId)
                        .header("Authorization", "Bearer " + customerBToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isForbidden());
        System.out.println("✓ IDOR Guard: Customer B blocked from updating Customer A's address (403 Forbidden)");

        // Customer B attempts to delete Customer A's address
        mockMvc.perform(delete("/api/addresses/" + customerAAddressId)
                        .header("Authorization", "Bearer " + customerBToken))
                .andExpect(status().isForbidden());
        System.out.println("✓ IDOR Guard: Customer B blocked from deleting Customer A's address (403 Forbidden)");
    }

    @Test
    @Order(5)
    @DisplayName("REQ 5: Cart Management, Merging, Quantities, and Price Calculations")
    void test05_CartOperationsAndCalculations() throws Exception {
        System.out.println("\n=======================================================");
        System.out.println(">>> 5. TESTING CART OPERATIONS & PRICE CALCULATIONS");
        System.out.println("=======================================================");

        // 5.1 Add product to cart (quantity 2)
        AddToCartRequest addReq1 = new AddToCartRequest(targetProductId, 2);
        MvcResult addRes1 = mockMvc.perform(post("/api/cart/items")
                        .header("Authorization", "Bearer " + customerAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addReq1)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode cartData1 = extractData(addRes1);
        assertThat(cartData1.get("items").size()).isEqualTo(1);
        assertThat(cartData1.get("items").get(0).get("quantity").asInt()).isEqualTo(2);
        System.out.println("✓ Added 2 units of product " + targetProductId + " to cart");

        // 5.2 Add same product again (quantity 1) -> Verify auto-merging into 3 units
        AddToCartRequest addReq2 = new AddToCartRequest(targetProductId, 1);
        MvcResult addRes2 = mockMvc.perform(post("/api/cart/items")
                        .header("Authorization", "Bearer " + customerAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addReq2)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode cartData2 = extractData(addRes2);
        assertThat(cartData2.get("items").size()).isEqualTo(1);
        int mergedQuantity = cartData2.get("items").get(0).get("quantity").asInt();
        assertThat(mergedQuantity).isEqualTo(3);
        System.out.println("✓ Duplicate item auto-merged: total cart quantity is 3");

        // 5.3 Validate Price consistency & Cart Total
        Long cartItemId = cartData2.get("items").get(0).get("id").asLong();
        BigDecimal expectedSubtotal = targetProductPrice.multiply(BigDecimal.valueOf(3));
        BigDecimal actualSubtotal = new BigDecimal(cartData2.get("items").get(0).get("subtotal").asText());
        BigDecimal actualTotal = new BigDecimal(cartData2.get("totalAmount").asText());

        assertThat(actualSubtotal).isEqualByComparingTo(expectedSubtotal);
        assertThat(actualTotal).isEqualByComparingTo(expectedSubtotal);
        System.out.println("✓ Price consistency verified: Subtotal ($" + actualSubtotal + ") == Total ($" + actualTotal + ")");

        // 5.4 Update quantity to 2
        UpdateCartItemRequest updateQtyReq = new UpdateCartItemRequest(2);
        MvcResult updateRes = mockMvc.perform(put("/api/cart/items/" + cartItemId)
                        .header("Authorization", "Bearer " + customerAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateQtyReq)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode cartData3 = extractData(updateRes);
        assertThat(cartData3.get("items").get(0).get("quantity").asInt()).isEqualTo(2);
        BigDecimal newTotal = targetProductPrice.multiply(BigDecimal.valueOf(2));
        assertThat(new BigDecimal(cartData3.get("totalAmount").asText())).isEqualByComparingTo(newTotal);
        System.out.println("✓ Quantity updated to 2; total recalculated to $" + newTotal);

        // 5.5 Out of stock / excessive quantity validation
        UpdateCartItemRequest excessiveReq = new UpdateCartItemRequest(999999);
        mockMvc.perform(put("/api/cart/items/" + cartItemId)
                        .header("Authorization", "Bearer " + customerAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(excessiveReq)))
                .andExpect(status().isBadRequest());
        System.out.println("✓ Excessive quantity rejected with 400 Bad Request (Insufficient Stock)");

        // 5.6 Cart IDOR protection: Customer B cannot modify Customer A's cart item
        mockMvc.perform(delete("/api/cart/items/" + cartItemId)
                        .header("Authorization", "Bearer " + customerBToken))
                .andExpect(status().isForbidden());
        System.out.println("✓ IDOR Guard: Customer B blocked from deleting Customer A's cart item (403 Forbidden)");
    }

    @Test
    @Order(6)
    @DisplayName("REQ 6 & 8: Checkout, Order Placement, Item Snapshots, and Stock Deduction")
    void test06_CheckoutAndOrderPlacement() throws Exception {
        System.out.println("\n=======================================================");
        System.out.println(">>> 6. TESTING CHECKOUT, ORDER ATOMICITY & STOCK DEDUCTION");
        System.out.println("=======================================================");

        // 6.1 Empty cart order rejection
        CreateOrderRequest emptyCartOrderReq = new CreateOrderRequest(customerAAddressId, PaymentMethod.CARD, "Empty cart test");
        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + customerBToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyCartOrderReq)))
                .andExpect(status().isBadRequest());
        System.out.println("✓ Empty cart checkout rejected with 400 Bad Request");

        // Record stock in PostgreSQL prior to order placement
        Product prodBefore = productRepository.findById(targetProductId).orElseThrow();
        int stockBefore = prodBefore.getStockQuantity();
        System.out.println("Current stock before order placement: " + stockBefore);

        // 6.2 Customer A places order (currently has 2 units in cart)
        CreateOrderRequest orderReq = new CreateOrderRequest(
                customerAAddressId, PaymentMethod.CARD, "Priority White-Glove Courier Delivery"
        );

        MvcResult orderResult = mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + customerAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderReq)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode orderData = extractData(orderResult);
        customerAOrderId = orderData.get("id").asLong();
        assertThat(customerAOrderId).isNotNull();
        assertThat(orderData.get("status").asText()).isEqualTo("PLACED");

        BigDecimal expectedTotal = targetProductPrice.multiply(BigDecimal.valueOf(2));
        BigDecimal orderTotal = new BigDecimal(orderData.get("totalAmount").asText());
        assertThat(orderTotal).isEqualByComparingTo(expectedTotal);
        System.out.println("✓ Order placed successfully with ID: " + customerAOrderId + ", Total: $" + orderTotal);

        // 6.3 Verify Order Item Snapshot integrity
        JsonNode itemsNode = orderData.get("items");
        assertThat(itemsNode.size()).isEqualTo(1);
        JsonNode itemSnapshot = itemsNode.get(0);
        assertThat(itemSnapshot.get("productId").asLong()).isEqualTo(targetProductId);
        assertThat(itemSnapshot.get("quantity").asInt()).isEqualTo(2);
        assertThat(new BigDecimal(itemSnapshot.get("price").asText())).isEqualByComparingTo(targetProductPrice);
        assertThat(new BigDecimal(itemSnapshot.get("subtotal").asText())).isEqualByComparingTo(expectedTotal);
        System.out.println("✓ Order item historical price & quantity snapshot verified");

        // 6.4 Verify Shopping Cart is now completely empty
        MvcResult cartResult = mockMvc.perform(get("/api/cart")
                        .header("Authorization", "Bearer " + customerAToken))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(extractData(cartResult).get("items").size()).isEqualTo(0);
        System.out.println("✓ Cart emptied automatically upon order placement");

        // 6.5 Verify Stock Deduction in PostgreSQL
        Product prodAfter = productRepository.findById(targetProductId).orElseThrow();
        int stockAfter = prodAfter.getStockQuantity();
        assertThat(stockAfter).isEqualTo(stockBefore - 2);
        System.out.println("✓ Stock deduction confirmed in PostgreSQL: " + stockBefore + " -> " + stockAfter + " (decremented by 2)");

        // 6.6 Verify Initial Payment record created with status PENDING
        com.jpbazaar.entity.Order orderInDb = orderRepository.findByIdWithDetails(customerAOrderId).orElseThrow();
        assertThat(orderInDb.getPayment()).isNotNull();
        assertThat(orderInDb.getPayment().getStatus()).isEqualTo(PaymentStatus.PENDING);
        System.out.println("✓ Associated Payment record initialized in PostgreSQL (status: PENDING)");

        // 6.7 Order IDOR Protection: Customer B cannot view Customer A's order
        mockMvc.perform(get("/api/orders/" + customerAOrderId)
                        .header("Authorization", "Bearer " + customerBToken))
                .andExpect(status().isForbidden());
        System.out.println("✓ IDOR Guard: Customer B blocked from viewing Customer A's order (403 Forbidden)");
    }

    @Test
    @Order(7)
    @DisplayName("REQ 7: Simulated Payment Processing, Failure Handling, and Duplicate Prevention")
    void test07_PaymentProcessing() throws Exception {
        System.out.println("\n=======================================================");
        System.out.println(">>> 7. TESTING PAYMENT PROCESSING & TRANSACTION SAFETY");
        System.out.println("=======================================================");

        // 7.1 Failed Payment Simulation (simulateFailure = true)
        PaymentProcessRequest failedPayReq = new PaymentProcessRequest(customerAOrderId, PaymentMethod.CARD, true);
        mockMvc.perform(post("/api/payments/process")
                        .header("Authorization", "Bearer " + customerAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(failedPayReq)))
                .andExpect(status().isBadRequest());

        // Verify order remains in PLACED state after failed payment attempt
        com.jpbazaar.entity.Order orderAfterFailure = orderRepository.findById(customerAOrderId).orElseThrow();
        assertThat(orderAfterFailure.getStatus()).isEqualTo(OrderStatus.PLACED);
        System.out.println("✓ Failed payment handled safely: Order remains PLACED, error reported cleanly");

        // 7.2 Successful Payment Simulation (simulateFailure = false)
        PaymentProcessRequest successPayReq = new PaymentProcessRequest(customerAOrderId, PaymentMethod.CARD, false);
        MvcResult payResult = mockMvc.perform(post("/api/payments/process")
                        .header("Authorization", "Bearer " + customerAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(successPayReq)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode payData = extractData(payResult);
        assertThat(payData.get("status").asText()).isEqualTo("SUCCESS");
        String txnId = payData.get("transactionId").asText();
        assertThat(txnId).startsWith("TXN-");
        System.out.println("✓ Payment processed successfully! Transaction ID: " + txnId);

        // Verify order auto-transitioned to CONFIRMED
        com.jpbazaar.entity.Order orderAfterSuccess = orderRepository.findById(customerAOrderId).orElseThrow();
        assertThat(orderAfterSuccess.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        System.out.println("✓ Order auto-transitioned to CONFIRMED following successful payment");

        // 7.3 Duplicate Payment Prevention
        mockMvc.perform(post("/api/payments/process")
                        .header("Authorization", "Bearer " + customerAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(successPayReq)))
                .andExpect(status().isBadRequest());
        System.out.println("✓ Duplicate payment rejected with 400 Bad Request");

        // 7.4 Payment IDOR Protection: Customer B cannot view Customer A's payment receipt
        mockMvc.perform(get("/api/payments/orders/" + customerAOrderId)
                        .header("Authorization", "Bearer " + customerBToken))
                .andExpect(status().isForbidden());
        System.out.println("✓ IDOR Guard: Customer B blocked from viewing Customer A's payment (403 Forbidden)");
    }

    @Test
    @Order(8)
    @DisplayName("REQ 8: Order History, Details, and State Machine Lifecycle Transitions")
    void test08_OrderLifecycleAndTransitions() throws Exception {
        System.out.println("\n=======================================================");
        System.out.println(">>> 8. TESTING ORDER HISTORY & STATE MACHINE TRANSITIONS");
        System.out.println("=======================================================");

        // 8.1 View order history
        MvcResult historyResult = mockMvc.perform(get("/api/orders")
                        .header("Authorization", "Bearer " + customerAToken))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode historyData = extractData(historyResult);
        assertThat(historyData.get("totalElements").asInt()).isGreaterThanOrEqualTo(1);
        System.out.println("✓ Order history retrieved: Customer A sees order " + customerAOrderId);

        // 8.2 View order details
        MvcResult detailResult = mockMvc.perform(get("/api/orders/" + customerAOrderId)
                        .header("Authorization", "Bearer " + customerAToken))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode detailData = extractData(detailResult);
        assertThat(detailData.get("id").asLong()).isEqualTo(customerAOrderId);
        assertThat(detailData.get("status").asText()).isEqualTo("CONFIRMED");
        System.out.println("✓ Order details verified with payment and status confirmed");

        // 8.3 Admin status transitions: CONFIRMED -> PROCESSING -> SHIPPED -> DELIVERED
        LoginRequest adminLogin = new LoginRequest("admin@jpbazaar.com", "Admin@123");
        MvcResult adminAuthResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andReturn();
        String adminToken = extractData(adminAuthResult).get("token").asText();
        System.out.println("✓ Admin authenticated for lifecycle management");

        // Step to PROCESSING
        UpdateOrderStatusRequest toProcessing = new UpdateOrderStatusRequest(OrderStatus.PROCESSING);
        mockMvc.perform(put("/api/orders/" + customerAOrderId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toProcessing)))
                .andExpect(status().isOk());
        System.out.println("✓ Transitioned order to PROCESSING");

        // Step to SHIPPED
        UpdateOrderStatusRequest toShipped = new UpdateOrderStatusRequest(OrderStatus.SHIPPED);
        mockMvc.perform(put("/api/orders/" + customerAOrderId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toShipped)))
                .andExpect(status().isOk());
        System.out.println("✓ Transitioned order to SHIPPED");

        // Step to OUT_FOR_DELIVERY
        UpdateOrderStatusRequest toOutForDelivery = new UpdateOrderStatusRequest(OrderStatus.OUT_FOR_DELIVERY);
        mockMvc.perform(put("/api/orders/" + customerAOrderId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toOutForDelivery)))
                .andExpect(status().isOk());
        System.out.println("✓ Transitioned order to OUT_FOR_DELIVERY");

        // Step to DELIVERED
        UpdateOrderStatusRequest toDelivered = new UpdateOrderStatusRequest(OrderStatus.DELIVERED);
        mockMvc.perform(put("/api/orders/" + customerAOrderId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toDelivered)))
                .andExpect(status().isOk());
        System.out.println("✓ Transitioned order to DELIVERED");

        // 8.4 Invalid status transition: DELIVERED -> CANCELLED
        UpdateOrderStatusRequest invalidTransition = new UpdateOrderStatusRequest(OrderStatus.CANCELLED);
        mockMvc.perform(put("/api/orders/" + customerAOrderId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTransition)))
                .andExpect(status().isBadRequest());
        System.out.println("✓ Invalid state machine transition (DELIVERED -> CANCELLED) rejected with 400 Bad Request");
    }

    @Test
    @Order(9)
    @DisplayName("REQ 9: Verified Buyer Reviews, Order Cancellation, and Stock Restoration")
    void test09_ReviewsAndStockRestoration() throws Exception {
        System.out.println("\n=======================================================");
        System.out.println(">>> 9. TESTING REVIEWS & INVENTORY RESTORATION RULES");
        System.out.println("=======================================================");

        // 9.1 Non-buyer review rejection
        ReviewRequest nonBuyerReviewReq = new ReviewRequest(targetProductId, 5, "I never bought this but it looks nice!");
        mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + customerBToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonBuyerReviewReq)))
                .andExpect(status().isBadRequest());
        System.out.println("✓ Non-buyer review prevented with 400 Bad Request (Verified Buyer rule enforced)");

        // 9.2 Verified buyer review submission (Customer A purchased and received targetProductId)
        ReviewRequest buyerReviewReq = new ReviewRequest(
                targetProductId, 5, "Exquisite craftsmanship, extraordinary acoustic fidelity!"
        );
        MvcResult reviewResult = mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + customerAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buyerReviewReq)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode reviewData = extractData(reviewResult);
        assertThat(reviewData.get("rating").asInt()).isEqualTo(5);
        assertThat(reviewData.get("comment").asText()).contains("Exquisite craftsmanship");
        System.out.println("✓ Verified buyer review created successfully");

        // 9.3 Duplicate review prevention (409 Conflict)
        mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + customerAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buyerReviewReq)))
                .andExpect(status().isConflict());
        System.out.println("✓ Duplicate review by same buyer prevented (409 Conflict)");

        // 9.4 Order Cancellation and Inventory Stock Restoration
        System.out.println("\n--- Testing Order Cancellation & Inventory Restoration ---");
        // Customer A adds 1 unit to cart
        mockMvc.perform(post("/api/cart/items")
                        .header("Authorization", "Bearer " + customerAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddToCartRequest(targetProductId, 1))))
                .andExpect(status().isCreated());

        Product prodBeforeCancelOrder = productRepository.findById(targetProductId).orElseThrow();
        int stockBaseline = prodBeforeCancelOrder.getStockQuantity();
        System.out.println("Stock baseline before new order: " + stockBaseline);

        // Place new order (status = PLACED)
        CreateOrderRequest cancelTestOrderReq = new CreateOrderRequest(customerAAddressId, PaymentMethod.CARD, "Cancellation test");
        MvcResult newOrderRes = mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + customerAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelTestOrderReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Long cancelOrderId = extractData(newOrderRes).get("id").asLong();

        // Verify stock decremented by 1
        Product prodAfterPlace = productRepository.findById(targetProductId).orElseThrow();
        assertThat(prodAfterPlace.getStockQuantity()).isEqualTo(stockBaseline - 1);
        System.out.println("Stock after placement: " + prodAfterPlace.getStockQuantity() + " (decremented by 1)");

        // Process successful payment so we can also test refund generation upon cancellation
        mockMvc.perform(post("/api/payments/process")
                        .header("Authorization", "Bearer " + customerAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PaymentProcessRequest(cancelOrderId, PaymentMethod.CARD, false))))
                .andExpect(status().isOk());

        // Cancel order
        MvcResult cancelResult = mockMvc.perform(put("/api/orders/" + cancelOrderId + "/cancel")
                        .header("Authorization", "Bearer " + customerAToken))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode cancelData = extractData(cancelResult);
        assertThat(cancelData.get("status").asText()).isEqualTo("CANCELLED");
        System.out.println("✓ Order " + cancelOrderId + " cancelled successfully");

        // Verify Stock Restoration in PostgreSQL
        Product prodAfterCancel = productRepository.findById(targetProductId).orElseThrow();
        assertThat(prodAfterCancel.getStockQuantity()).isEqualTo(stockBaseline);
        System.out.println("✓ INVENTORY RESTORATION CONFIRMED: Stock restored back to baseline (" + stockBaseline + ")");

        // Verify Payment Refund in PostgreSQL
        Payment paymentInDb = paymentRepository.findByOrderId(cancelOrderId).orElseThrow();
        assertThat(paymentInDb.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
        assertThat(paymentInDb.getTransactionId()).startsWith("REF-");
        System.out.println("✓ PAYMENT REFUND CONFIRMED: Payment status is REFUNDED with ID " + paymentInDb.getTransactionId());
        System.out.println("\n=======================================================");
        System.out.println(">>> ALL 9 CUSTOMER JOURNEY REQUIREMENTS VERIFIED 100%");
        System.out.println("=======================================================");
    }
}
