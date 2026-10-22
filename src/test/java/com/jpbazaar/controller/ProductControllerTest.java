package com.jpbazaar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpbazaar.dto.request.CategoryRequest;
import com.jpbazaar.dto.request.ProductCreateRequest;
import com.jpbazaar.dto.request.ProductUpdateRequest;
import com.jpbazaar.entity.Role;
import com.jpbazaar.entity.User;
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

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerTest {

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

    private String adminToken;
    private String customerToken;
    private Long testCategoryId;

    @BeforeEach
    void setUp() throws Exception {
        adminToken = getOrCreateUserToken("admin_prod@test.com", Role.ADMIN);
        customerToken = getOrCreateUserToken("user_prod@test.com", Role.CUSTOMER);

        // Ensure category exists
        CategoryRequest catReq = new CategoryRequest("Computers_" + System.currentTimeMillis(), "Laptops and desktops");
        var res = mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(catReq)))
                .andExpect(status().isCreated())
                .andReturn();

        testCategoryId = objectMapper.readTree(res.getResponse().getContentAsString())
                .get("data").get("id").asLong();
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

    @Test
    void createProduct_AsAdmin_Success() throws Exception {
        ProductCreateRequest request = new ProductCreateRequest(
                testCategoryId,
                "ThinkPad X1 Carbon",
                "High performance ultrabook",
                new BigDecimal("1299.99"),
                20,
                "TP-X1-" + System.currentTimeMillis(),
                "Lenovo",
                true
        );

        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("ThinkPad X1 Carbon"))
                .andExpect(jsonPath("$.data.brand").value("Lenovo"))
                .andExpect(jsonPath("$.data.stockQuantity").value(20));
    }

    @Test
    void createProduct_ValidationFailure_ReturnsBadRequest() throws Exception {
        // Negative price and blank name
        ProductCreateRequest request = new ProductCreateRequest(
                testCategoryId,
                "",
                "Description",
                new BigDecimal("-5.00"),
                -1,
                "",
                "Brand",
                true
        );

        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void createProduct_AsCustomer_Forbidden() throws Exception {
        ProductCreateRequest request = new ProductCreateRequest(
                testCategoryId,
                "MacBook Pro",
                "Apple M3 Pro",
                new BigDecimal("1999.99"),
                10,
                "MBP-M3-" + System.currentTimeMillis(),
                "Apple",
                true
        );

        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void searchAndFilterProducts_Public_Success() throws Exception {
        // Create a product first
        String sku = "DELL-XPS-" + System.currentTimeMillis();
        ProductCreateRequest request = new ProductCreateRequest(
                testCategoryId,
                "Dell XPS 15",
                "Stunning display laptop",
                new BigDecimal("1499.00"),
                8,
                sku,
                "Dell",
                true
        );

        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Search by keyword and brand
        mockMvc.perform(get("/api/products")
                        .param("keyword", "XPS")
                        .param("brand", "Dell")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "price")
                        .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)));
    }

    @Test
    void updateAndDeleteProduct_AsAdmin_Success() throws Exception {
        // 1. Create product
        String sku = "LOGI-MX3-" + System.currentTimeMillis();
        ProductCreateRequest createReq = new ProductCreateRequest(
                testCategoryId,
                "MX Master 3",
                "Wireless Ergonomic Mouse",
                new BigDecimal("99.99"),
                50,
                sku,
                "Logitech",
                true
        );

        var createRes = mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Long prodId = objectMapper.readTree(createRes.getResponse().getContentAsString())
                .get("data").get("id").asLong();

        // 2. Update product
        ProductUpdateRequest updateReq = new ProductUpdateRequest(
                null,
                "MX Master 3S",
                "Quiet Click Ergonomic Mouse",
                new BigDecimal("109.99"),
                45,
                "Logitech",
                true
        );

        mockMvc.perform(put("/api/products/" + prodId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("MX Master 3S"))
                .andExpect(jsonPath("$.data.price").value(109.99));

        // 3. Delete product
        mockMvc.perform(delete("/api/products/" + prodId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product deleted successfully"));

        // 4. Verify 404 after deletion
        mockMvc.perform(get("/api/products/" + prodId))
                .andExpect(status().isNotFound());
    }
}
