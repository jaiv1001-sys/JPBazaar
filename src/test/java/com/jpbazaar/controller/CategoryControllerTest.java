package com.jpbazaar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpbazaar.dto.request.CategoryRequest;
import com.jpbazaar.dto.request.ProductCreateRequest;
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

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CategoryControllerTest {

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

    private String adminToken;
    private String customerToken;

    @BeforeEach
    void setUp() {
        adminToken = getOrCreateUserToken("admin_cat@test.com", Role.ADMIN);
        customerToken = getOrCreateUserToken("user_cat@test.com", Role.CUSTOMER);
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
    void createCategory_AsAdmin_Success() throws Exception {
        CategoryRequest request = new CategoryRequest("Electronics", "Gadgets and tech accessories");

        mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Electronics"));
    }

    @Test
    void createCategory_AsCustomer_Forbidden() throws Exception {
        CategoryRequest request = new CategoryRequest("Books", "Printed and audio books");

        mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllCategories_Public_Success() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteCategory_WithAssociatedProducts_ThrowsBadRequest() throws Exception {
        // 1. Create Category
        CategoryRequest catReq = new CategoryRequest("Appliances", "Home appliances");
        var res = mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(catReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Long categoryId = objectMapper.readTree(res.getResponse().getContentAsString())
                .get("data").get("id").asLong();

        // 2. Create Product linked to Category
        ProductCreateRequest prodReq = new ProductCreateRequest(
                categoryId,
                "Microwave",
                "700W Countertop Microwave",
                new BigDecimal("89.99"),
                15,
                "MW-700W-" + System.currentTimeMillis(),
                "Panasonic",
                true
        );

        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(prodReq)))
                .andExpect(status().isCreated());

        // 3. Try to delete category -> should fail
        mockMvc.perform(delete("/api/categories/" + categoryId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Cannot delete category with associated products. Reassign or delete products first."));
    }
}
