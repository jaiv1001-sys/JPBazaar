package com.jpbazaar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpbazaar.dto.request.AddToCartRequest;
import com.jpbazaar.dto.request.UpdateCartItemRequest;
import com.jpbazaar.dto.response.CartResponse;
import com.jpbazaar.entity.Category;
import com.jpbazaar.entity.Product;
import com.jpbazaar.entity.Role;
import com.jpbazaar.entity.User;
import com.jpbazaar.repository.CategoryRepository;
import com.jpbazaar.repository.ProductRepository;
import com.jpbazaar.repository.UserRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    private String user1Token;
    private String user2Token;
    private Long activeProductId;
    private Long inactiveProductId;

    @BeforeEach
    void setUp() {
        user1Token = getOrCreateUserToken("cart_user1_" + System.nanoTime() + "@test.com");
        user2Token = getOrCreateUserToken("cart_user2_" + System.nanoTime() + "@test.com");

        Category category = categoryRepository.save(
                Category.builder()
                        .name("CartCategory_" + System.nanoTime())
                        .description("Category for cart tests")
                        .build()
        );

        Product activeProduct = productRepository.save(
                Product.builder()
                        .name("Active Laptop")
                        .description("High-end laptop")
                        .price(new BigDecimal("1000.00"))
                        .stockQuantity(10)
                        .sku("ACT-LAP-" + System.nanoTime())
                        .brand("TechBrand")
                        .category(category)
                        .active(true)
                        .build()
        );
        activeProductId = activeProduct.getId();

        Product inactiveProduct = productRepository.save(
                Product.builder()
                        .name("Discontinued Item")
                        .description("Out of production")
                        .price(new BigDecimal("50.00"))
                        .stockQuantity(5)
                        .sku("DISC-ITEM-" + System.nanoTime())
                        .brand("OldBrand")
                        .category(category)
                        .active(false)
                        .build()
        );
        inactiveProductId = inactiveProduct.getId();
    }

    private String getOrCreateUserToken(String email) {
        User user = User.builder()
                .firstName("Cart")
                .lastName("Tester")
                .email(email)
                .password(passwordEncoder.encode("password123"))
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();
        userRepository.save(user);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, "password123")
        );
        return tokenProvider.generateToken(auth);
    }

    @Test
    void getCart_InitialEmptyCart_ReturnsSuccess() throws Exception {
        mockMvc.perform(get("/api/cart")
                        .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalItemCount").value(0))
                .andExpect(jsonPath("$.data.totalAmount").value(0))
                .andExpect(jsonPath("$.data.items").isEmpty());
    }

    @Test
    void addToCart_Success() throws Exception {
        AddToCartRequest request = new AddToCartRequest(activeProductId, 2);

        mockMvc.perform(post("/api/cart/items")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalItemCount").value(2))
                .andExpect(jsonPath("$.data.totalAmount").value(2000.00))
                .andExpect(jsonPath("$.data.items[0].productName").value("Active Laptop"))
                .andExpect(jsonPath("$.data.items[0].quantity").value(2))
                .andExpect(jsonPath("$.data.items[0].subtotal").value(2000.00));
    }

    @Test
    void addToCart_DuplicateProduct_IncrementsExistingQuantity() throws Exception {
        AddToCartRequest request1 = new AddToCartRequest(activeProductId, 2);
        AddToCartRequest request2 = new AddToCartRequest(activeProductId, 3);

        mockMvc.perform(post("/api/cart/items")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/cart/items")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.items.length()").value(1))
                .andExpect(jsonPath("$.data.totalItemCount").value(5))
                .andExpect(jsonPath("$.data.totalAmount").value(5000.00))
                .andExpect(jsonPath("$.data.items[0].quantity").value(5));
    }

    @Test
    void addToCart_ExceedsStock_ReturnsBadRequest() throws Exception {
        // Active product only has stock = 10, requesting 15
        AddToCartRequest request = new AddToCartRequest(activeProductId, 15);

        mockMvc.perform(post("/api/cart/items")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Insufficient stock")));
    }

    @Test
    void addToCart_InactiveProduct_ReturnsBadRequest() throws Exception {
        AddToCartRequest request = new AddToCartRequest(inactiveProductId, 1);

        mockMvc.perform(post("/api/cart/items")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("inactive")));
    }

    @Test
    void updateCartItemQuantity_And_RemoveItem_Success() throws Exception {
        // 1. Add item with qty 2
        AddToCartRequest addReq = new AddToCartRequest(activeProductId, 2);
        MvcResult addRes = mockMvc.perform(post("/api/cart/items")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addReq)))
                .andExpect(status().isCreated())
                .andReturn();

        CartResponse cart = objectMapper.readValue(
                objectMapper.readTree(addRes.getResponse().getContentAsString()).get("data").toString(),
                CartResponse.class
        );
        Long cartItemId = cart.items().get(0).id();

        // 2. Update quantity to 4
        UpdateCartItemRequest updateReq = new UpdateCartItemRequest(4);
        mockMvc.perform(put("/api/cart/items/" + cartItemId)
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].quantity").value(4))
                .andExpect(jsonPath("$.data.totalAmount").value(4000.00));

        // 3. Remove item
        mockMvc.perform(delete("/api/cart/items/" + cartItemId)
                        .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items").isEmpty())
                .andExpect(jsonPath("$.data.totalItemCount").value(0));
    }

    @Test
    void clearCart_Success() throws Exception {
        // Add item
        AddToCartRequest addReq = new AddToCartRequest(activeProductId, 3);
        mockMvc.perform(post("/api/cart/items")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addReq)))
                .andExpect(status().isCreated());

        // Clear cart
        mockMvc.perform(delete("/api/cart")
                        .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items").isEmpty())
                .andExpect(jsonPath("$.data.totalItemCount").value(0))
                .andExpect(jsonPath("$.data.totalAmount").value(0));
    }

    @Test
    void modifyOtherUserCartItem_IDORAttack_ReturnsForbidden() throws Exception {
        // 1. User 1 adds product to their cart
        AddToCartRequest addReq = new AddToCartRequest(activeProductId, 1);
        MvcResult addRes = mockMvc.perform(post("/api/cart/items")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addReq)))
                .andExpect(status().isCreated())
                .andReturn();

        CartResponse cart = objectMapper.readValue(
                objectMapper.readTree(addRes.getResponse().getContentAsString()).get("data").toString(),
                CartResponse.class
        );
        Long user1CartItemId = cart.items().get(0).id();

        // 2. User 2 attempts to delete User 1's cart item -> IDOR Attack!
        mockMvc.perform(delete("/api/cart/items/" + user1CartItemId)
                        .header("Authorization", "Bearer " + user2Token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));

        // 3. User 2 attempts to update User 1's cart item quantity -> IDOR Attack!
        UpdateCartItemRequest updateReq = new UpdateCartItemRequest(5);
        mockMvc.perform(put("/api/cart/items/" + user1CartItemId)
                        .header("Authorization", "Bearer " + user2Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }
}
