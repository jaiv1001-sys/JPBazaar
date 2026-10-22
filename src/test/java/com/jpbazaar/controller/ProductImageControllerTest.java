package com.jpbazaar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpbazaar.dto.request.ProductImageRequest;
import com.jpbazaar.dto.response.ProductImageResponse;
import com.jpbazaar.entity.Category;
import com.jpbazaar.entity.Product;
import com.jpbazaar.entity.Role;
import com.jpbazaar.entity.User;
import com.jpbazaar.repository.CategoryRepository;
import com.jpbazaar.repository.ProductImageRepository;
import com.jpbazaar.repository.ProductRepository;
import com.jpbazaar.repository.UserRepository;
import com.jpbazaar.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductImageControllerTest {

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

    @Autowired
    private ProductImageRepository productImageRepository;

    private String adminToken;
    private String customerToken;
    private Long testProductId;

    @BeforeEach
    void setUp() {
        adminToken = getOrCreateUserToken("admin_img_" + System.currentTimeMillis() + "@test.com", Role.ADMIN);
        customerToken = getOrCreateUserToken("cust_img_" + System.currentTimeMillis() + "@test.com", Role.CUSTOMER);

        Category category = categoryRepository.save(
                Category.builder()
                        .name("ImageTestCategory_" + System.nanoTime())
                        .description("Test description")
                        .build()
        );

        Product product = productRepository.save(
                Product.builder()
                        .name("Test Product With Images")
                        .description("High quality test product")
                        .price(new BigDecimal("199.99"))
                        .stockQuantity(15)
                        .sku("IMG-PROD-" + System.nanoTime())
                        .brand("PhotoBrand")
                        .category(category)
                        .active(true)
                        .build()
        );
        testProductId = product.getId();
    }

    private String getOrCreateUserToken(String email, Role role) {
        User user = User.builder()
                .firstName("Test")
                .lastName("User")
                .email(email)
                .password(passwordEncoder.encode("password123"))
                .role(role)
                .enabled(true)
                .build();
        userRepository.save(user);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, "password123")
        );
        return tokenProvider.generateToken(auth);
    }

    @Test
    void uploadImage_AsAdmin_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "sample-product.png",
                MediaType.IMAGE_PNG_VALUE,
                "fake image binary content".getBytes()
        );

        mockMvc.perform(multipart("/api/products/" + testProductId + "/images/upload")
                        .file(file)
                        .param("isPrimary", "true")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.isPrimary").value(true))
                .andExpect(jsonPath("$.data.imageUrl").isNotEmpty());
    }

    @Test
    void addImageUrl_AsAdmin_Success() throws Exception {
        ProductImageRequest request = new ProductImageRequest(
                "https://images.unsplash.com/photo-sample.jpg",
                false
        );

        mockMvc.perform(post("/api/products/" + testProductId + "/images")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.imageUrl").value("https://images.unsplash.com/photo-sample.jpg"))
                // Since it's the first image for this product, it automatically becomes primary
                .andExpect(jsonPath("$.data.isPrimary").value(true));
    }

    @Test
    void uploadImage_AsCustomer_Forbidden() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "unauthorized.png",
                MediaType.IMAGE_PNG_VALUE,
                "test binary".getBytes()
        );

        mockMvc.perform(multipart("/api/products/" + testProductId + "/images/upload")
                        .file(file)
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getProductImages_Public_Success() throws Exception {
        // Add two images via Admin
        ProductImageRequest req1 = new ProductImageRequest("https://cdn.example.com/img1.jpg", true);
        ProductImageRequest req2 = new ProductImageRequest("https://cdn.example.com/img2.jpg", false);

        mockMvc.perform(post("/api/products/" + testProductId + "/images")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/products/" + testProductId + "/images")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isCreated());

        // Public GET without any token
        mockMvc.perform(get("/api/products/" + testProductId + "/images"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].isPrimary").value(true));
    }

    @Test
    void setPrimaryImage_And_DeleteImage_Success() throws Exception {
        // 1. Add image 1 (first image -> primary)
        ProductImageRequest req1 = new ProductImageRequest("https://cdn.example.com/primary1.jpg", true);
        MvcResult res1 = mockMvc.perform(post("/api/products/" + testProductId + "/images")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isCreated())
                .andReturn();

        ProductImageResponse img1 = objectMapper.readValue(
                objectMapper.readTree(res1.getResponse().getContentAsString()).get("data").toString(),
                ProductImageResponse.class
        );

        // 2. Add image 2 (non-primary)
        ProductImageRequest req2 = new ProductImageRequest("https://cdn.example.com/secondary.jpg", false);
        MvcResult res2 = mockMvc.perform(post("/api/products/" + testProductId + "/images")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isCreated())
                .andReturn();

        ProductImageResponse img2 = objectMapper.readValue(
                objectMapper.readTree(res2.getResponse().getContentAsString()).get("data").toString(),
                ProductImageResponse.class
        );

        // 3. Set image 2 as primary
        mockMvc.perform(put("/api/products/" + testProductId + "/images/" + img2.id() + "/primary")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(img2.id()))
                .andExpect(jsonPath("$.data.isPrimary").value(true));

        // 4. Delete image 2
        mockMvc.perform(delete("/api/products/" + testProductId + "/images/" + img2.id())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 5. Verify only 1 image remains and image 1 is restored as primary
        mockMvc.perform(get("/api/products/" + testProductId + "/images"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(img1.id()))
                .andExpect(jsonPath("$.data[0].isPrimary").value(true));
    }

    @Test
    void serveImage_Public_Success() throws Exception {
        // Upload a file first
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "serve-test.png",
                MediaType.IMAGE_PNG_VALUE,
                "serve image binary content".getBytes()
        );

        MvcResult uploadResult = mockMvc.perform(multipart("/api/products/" + testProductId + "/images/upload")
                        .file(file)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andReturn();

        ProductImageResponse uploaded = objectMapper.readValue(
                objectMapper.readTree(uploadResult.getResponse().getContentAsString()).get("data").toString(),
                ProductImageResponse.class
        );

        // Public download/serve endpoint using the exact imageUrl returned
        mockMvc.perform(get(uploaded.imageUrl()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.IMAGE_PNG_VALUE));
    }
}
