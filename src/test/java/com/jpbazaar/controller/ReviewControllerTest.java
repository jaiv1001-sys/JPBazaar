package com.jpbazaar.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpbazaar.dto.request.AddToCartRequest;
import com.jpbazaar.dto.request.CreateOrderRequest;
import com.jpbazaar.dto.request.ReviewRequest;
import com.jpbazaar.dto.request.UpdateReviewRequest;
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
class ReviewControllerTest {

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
    private ReviewRepository reviewRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private String buyerEmail;
    private String buyerToken;
    private Long buyerAddressId;

    private String nonBuyerEmail;
    private String nonBuyerToken;

    private String adminToken;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        buyerEmail = "rev_buyer_" + System.nanoTime() + "@test.com";
        buyerToken = getOrCreateUserToken(buyerEmail, Role.CUSTOMER);
        User buyer = userRepository.findByEmail(buyerEmail).orElseThrow();
        Address addr = addressRepository.save(Address.builder()
                .user(buyer)
                .addressLine("123 Review Blvd")
                .city("Seattle")
                .state("WA")
                .postalCode("98101")
                .country("USA")
                .isDefault(true)
                .build());
        buyerAddressId = addr.getId();

        nonBuyerEmail = "rev_nonbuyer_" + System.nanoTime() + "@test.com";
        nonBuyerToken = getOrCreateUserToken(nonBuyerEmail, Role.CUSTOMER);

        String adminEmail = "rev_admin_" + System.nanoTime() + "@test.com";
        adminToken = getOrCreateUserToken(adminEmail, Role.ADMIN);

        Category category = categoryRepository.save(Category.builder()
                .name("ReviewCategory_" + System.nanoTime())
                .description("Category for review tests")
                .build());

        testProduct = productRepository.save(Product.builder()
                .name("Wireless Noise Cancelling Headphones")
                .description("Studio audio quality")
                .price(new BigDecimal("350.00"))
                .stockQuantity(20)
                .sku("HEADPHONES-" + System.nanoTime())
                .brand("AcousticBrand")
                .category(category)
                .active(true)
                .build());
    }

    private String getOrCreateUserToken(String email, Role role) {
        if (!userRepository.existsByEmail(email)) {
            User user = User.builder()
                    .firstName("Review")
                    .lastName("Tester")
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

    private void purchaseProductForBuyer() throws Exception {
        // Add to cart
        mockMvc.perform(post("/api/cart/items")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddToCartRequest(testProduct.getId(), 1))))
                .andExpect(status().isCreated());

        // Checkout order
        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateOrderRequest(buyerAddressId, PaymentMethod.COD))))
                .andExpect(status().isCreated());
    }

    @Test
    void addReview_VerifiedBuyer_Success_UpdatesProductAverage() throws Exception {
        purchaseProductForBuyer();

        ReviewRequest request = new ReviewRequest(testProduct.getId(), 5, "Exceptional sound quality and battery life!");
        mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.productId").value(testProduct.getId()))
                .andExpect(jsonPath("$.data.rating").value(5))
                .andExpect(jsonPath("$.data.comment").value("Exceptional sound quality and battery life!"));

        // Verify product average rating updated to 5.00
        Product updatedProduct = productRepository.findById(testProduct.getId()).orElseThrow();
        assertThat(updatedProduct.getRating()).isEqualTo(new BigDecimal("5.00"));
    }

    @Test
    void addReview_WhenNotPurchased_ShouldReturnBadRequest() throws Exception {
        // nonBuyer has NOT purchased testProduct
        ReviewRequest request = new ReviewRequest(testProduct.getId(), 4, "Looks good from the pictures!");
        mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + nonBuyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Only verified buyers who have purchased this product can submit a review"));
    }

    @Test
    void addReview_DuplicateReview_ShouldReturnConflict() throws Exception {
        purchaseProductForBuyer();

        // 1st review succeeds
        ReviewRequest request1 = new ReviewRequest(testProduct.getId(), 5, "Great product!");
        mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        // 2nd review by same buyer rejected with 409 Conflict
        ReviewRequest request2 = new ReviewRequest(testProduct.getId(), 4, "Submitting another review");
        mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("You have already submitted a review for this product"));
    }

    @Test
    void addReview_InvalidRating_ShouldReturnBadRequest() throws Exception {
        purchaseProductForBuyer();

        // Rating > 5
        ReviewRequest requestOver = new ReviewRequest(testProduct.getId(), 6, "Invalid 6 stars");
        mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestOver)))
                .andExpect(status().isBadRequest());

        // Rating < 1
        ReviewRequest requestUnder = new ReviewRequest(testProduct.getId(), 0, "Invalid 0 stars");
        mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUnder)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateReview_AuthorSuccess_RecalculatesAverage() throws Exception {
        purchaseProductForBuyer();

        // Create initial 5-star review
        MvcResult result = mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReviewRequest(testProduct.getId(), 5, "Initial 5 stars"))))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        long reviewId = root.get("data").get("id").asLong();

        // Author updates to 3 stars
        UpdateReviewRequest updateRequest = new UpdateReviewRequest(3, "Downgraded to 3 stars after a week");
        mockMvc.perform(put("/api/reviews/" + reviewId)
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.rating").value(3))
                .andExpect(jsonPath("$.data.comment").value("Downgraded to 3 stars after a week"));

        // Product average rating recalculated to 3.00
        Product updatedProduct = productRepository.findById(testProduct.getId()).orElseThrow();
        assertThat(updatedProduct.getRating()).isEqualTo(new BigDecimal("3.00"));
    }

    @Test
    void updateReview_WhenNotAuthor_ShouldReturnForbidden() throws Exception {
        purchaseProductForBuyer();

        MvcResult result = mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReviewRequest(testProduct.getId(), 5, "Buyer review"))))
                .andExpect(status().isCreated())
                .andReturn();

        long reviewId = objectMapper.readTree(result.getResponse().getContentAsString()).get("data").get("id").asLong();

        // nonBuyer attempts to edit buyer's review
        UpdateReviewRequest updateRequest = new UpdateReviewRequest(1, "Hacked review");
        mockMvc.perform(put("/api/reviews/" + reviewId)
                        .header("Authorization", "Bearer " + nonBuyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You are not authorized to update this review"));
    }

    @Test
    void deleteReview_AuthorSuccess_ResetsAverageRating() throws Exception {
        purchaseProductForBuyer();

        MvcResult result = mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReviewRequest(testProduct.getId(), 4, "Good"))))
                .andExpect(status().isCreated())
                .andReturn();

        long reviewId = objectMapper.readTree(result.getResponse().getContentAsString()).get("data").get("id").asLong();

        // Author deletes review
        mockMvc.perform(delete("/api/reviews/" + reviewId)
                        .header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk());

        // Average rating resets to 0.00
        Product updatedProduct = productRepository.findById(testProduct.getId()).orElseThrow();
        assertThat(updatedProduct.getRating()).isEqualTo(new BigDecimal("0.00"));
    }

    @Test
    void deleteReview_AdminCanDeleteAnyReview() throws Exception {
        purchaseProductForBuyer();

        MvcResult result = mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReviewRequest(testProduct.getId(), 4, "Good"))))
                .andExpect(status().isCreated())
                .andReturn();

        long reviewId = objectMapper.readTree(result.getResponse().getContentAsString()).get("data").get("id").asLong();

        // Admin deletes review
        mockMvc.perform(delete("/api/reviews/" + reviewId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        assertThat(reviewRepository.existsById(reviewId)).isFalse();
    }

    @Test
    void deleteReview_WhenNotAuthorAndNotAdmin_ShouldReturnForbidden() throws Exception {
        purchaseProductForBuyer();

        MvcResult result = mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReviewRequest(testProduct.getId(), 4, "Good"))))
                .andExpect(status().isCreated())
                .andReturn();

        long reviewId = objectMapper.readTree(result.getResponse().getContentAsString()).get("data").get("id").asLong();

        // nonBuyer attempts to delete buyer's review
        mockMvc.perform(delete("/api/reviews/" + reviewId)
                        .header("Authorization", "Bearer " + nonBuyerToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You are not authorized to delete this review"));
    }

    @Test
    void getProductReviews_PublicAccess_ReturnsSummaryAndPaging() throws Exception {
        purchaseProductForBuyer();

        mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReviewRequest(testProduct.getId(), 5, "Public review test"))))
                .andExpect(status().isCreated());

        // Public unauthenticated request
        mockMvc.perform(get("/api/products/" + testProduct.getId() + "/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productId").value(testProduct.getId()))
                .andExpect(jsonPath("$.data.averageRating").value(5.00))
                .andExpect(jsonPath("$.data.totalReviews").value(1))
                .andExpect(jsonPath("$.data.reviews.content[0].rating").value(5))
                .andExpect(jsonPath("$.data.reviews.content[0].comment").value("Public review test"));
    }
}
