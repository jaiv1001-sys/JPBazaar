package com.jpbazaar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpbazaar.dto.request.AddToWishlistRequest;
import com.jpbazaar.dto.response.WishlistResponse;
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
class WishlistControllerTest {

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
    private Long product1Id;
    private Long product2Id;

    @BeforeEach
    void setUp() {
        user1Token = getOrCreateUserToken("wishlist_user1_" + System.nanoTime() + "@test.com");
        user2Token = getOrCreateUserToken("wishlist_user2_" + System.nanoTime() + "@test.com");

        Category category = categoryRepository.save(
                Category.builder()
                        .name("WishlistCategory_" + System.nanoTime())
                        .description("Category for wishlist tests")
                        .build()
        );

        Product p1 = productRepository.save(
                Product.builder()
                        .name("Smartphone 15 Pro")
                        .description("Flagship smartphone")
                        .price(new BigDecimal("999.99"))
                        .stockQuantity(25)
                        .sku("PHONE-15-" + System.nanoTime())
                        .brand("TechBrand")
                        .category(category)
                        .active(true)
                        .build()
        );
        product1Id = p1.getId();

        Product p2 = productRepository.save(
                Product.builder()
                        .name("Wireless Noise Canceling Headphones")
                        .description("Premium sound")
                        .price(new BigDecimal("349.99"))
                        .stockQuantity(12)
                        .sku("HEADPHONE-" + System.nanoTime())
                        .brand("AudioBrand")
                        .category(category)
                        .active(true)
                        .build()
        );
        product2Id = p2.getId();
    }

    private String getOrCreateUserToken(String email) {
        User user = User.builder()
                .firstName("Wishlist")
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
    void getWishlist_InitialEmpty_ReturnsSuccess() throws Exception {
        mockMvc.perform(get("/api/wishlist")
                        .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalItems").value(0))
                .andExpect(jsonPath("$.data.items").isEmpty());
    }

    @Test
    void addToWishlist_Success() throws Exception {
        AddToWishlistRequest request = new AddToWishlistRequest(product1Id);

        mockMvc.perform(post("/api/wishlist/items")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalItems").value(1))
                .andExpect(jsonPath("$.data.items[0].productName").value("Smartphone 15 Pro"))
                .andExpect(jsonPath("$.data.items[0].inStock").value(true));
    }

    @Test
    void addToWishlist_DuplicateProduct_ReturnsConflict() throws Exception {
        AddToWishlistRequest request = new AddToWishlistRequest(product1Id);

        // First add -> success
        mockMvc.perform(post("/api/wishlist/items")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Second add -> duplicate conflict (409)
        mockMvc.perform(post("/api/wishlist/items")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("already in your wishlist")));
    }

    @Test
    void removeWishlistItem_ById_Success() throws Exception {
        // 1. Add product to wishlist
        AddToWishlistRequest addReq = new AddToWishlistRequest(product1Id);
        MvcResult addRes = mockMvc.perform(post("/api/wishlist/items")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addReq)))
                .andExpect(status().isCreated())
                .andReturn();

        WishlistResponse wishlist = objectMapper.readValue(
                objectMapper.readTree(addRes.getResponse().getContentAsString()).get("data").toString(),
                WishlistResponse.class
        );
        Long wishlistItemId = wishlist.items().get(0).id();

        // 2. Remove by item ID
        mockMvc.perform(delete("/api/wishlist/items/" + wishlistItemId)
                        .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalItems").value(0))
                .andExpect(jsonPath("$.data.items").isEmpty());
    }

    @Test
    void removeProductFromWishlist_ByProductId_Success() throws Exception {
        // 1. Add product to wishlist
        AddToWishlistRequest addReq = new AddToWishlistRequest(product2Id);
        mockMvc.perform(post("/api/wishlist/items")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addReq)))
                .andExpect(status().isCreated());

        // 2. Remove by product ID
        mockMvc.perform(delete("/api/wishlist/products/" + product2Id)
                        .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalItems").value(0));
    }

    @Test
    void clearWishlist_Success() throws Exception {
        // 1. Add product 1 and product 2
        mockMvc.perform(post("/api/wishlist/items")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddToWishlistRequest(product1Id))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/wishlist/items")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddToWishlistRequest(product2Id))))
                .andExpect(status().isCreated());

        // 2. Clear entire wishlist
        mockMvc.perform(delete("/api/wishlist")
                        .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalItems").value(0))
                .andExpect(jsonPath("$.data.items").isEmpty());
    }

    @Test
    void modifyOtherUserWishlistItem_IDORAttack_ReturnsForbidden() throws Exception {
        // 1. User 1 adds product to their wishlist
        AddToWishlistRequest addReq = new AddToWishlistRequest(product1Id);
        MvcResult addRes = mockMvc.perform(post("/api/wishlist/items")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addReq)))
                .andExpect(status().isCreated())
                .andReturn();

        WishlistResponse wishlist = objectMapper.readValue(
                objectMapper.readTree(addRes.getResponse().getContentAsString()).get("data").toString(),
                WishlistResponse.class
        );
        Long user1WishlistItemId = wishlist.items().get(0).id();

        // 2. User 2 attempts to delete User 1's wishlist item -> IDOR Attack!
        mockMvc.perform(delete("/api/wishlist/items/" + user1WishlistItemId)
                        .header("Authorization", "Bearer " + user2Token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }
}
