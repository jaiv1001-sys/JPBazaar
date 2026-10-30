package com.jpbazaar.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpbazaar.dto.request.LoginRequest;
import com.jpbazaar.security.ratelimit.RateLimiterService;
import com.jpbazaar.util.MaskingUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityHardeningIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RateLimiterService rateLimiterService;

    @BeforeEach
    void setUp() {
        // Reset test IP rate limit quota before each test case
        rateLimiterService.reset("ip:127.0.0.1:AUTH");
        rateLimiterService.reset("ip:127.0.0.1:GENERAL");
        rateLimiterService.reset("ip:203.0.113.195:AUTH");
        rateLimiterService.reset("ip:203.0.113.195:GENERAL");
    }

    @Test
    @DisplayName("Should return hardened HTTP security headers on API responses")
    void shouldReturnHardenedSecurityHeaders() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().string("Referrer-Policy", "strict-origin-when-cross-origin"))
                .andExpect(header().string("Permissions-Policy", "geolocation=(), camera=(), microphone=()"))
                .andExpect(header().string("Content-Security-Policy", containsString("default-src 'self'")));
    }

    @Test
    @DisplayName("Should include rate limit headers on allowed requests")
    void shouldIncludeRateLimitHeadersOnAllowedRequests() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-RateLimit-Limit", "100"))
                .andExpect(header().exists("X-RateLimit-Remaining"))
                .andExpect(header().exists("X-RateLimit-Reset"));
    }

    @Test
    @DisplayName("Should trigger 429 Too Many Requests when rate limit threshold is exceeded")
    void shouldTrigger429WhenRateLimitExceeded() throws Exception {
        String testIp = "203.0.113.195";
        // Use an invalid request payload to fail validation instantly (<1ms) without triggering expensive BCrypt
        String invalidPayload = "{}";

        // Exhaust the 10 token capacity on AUTH tier (/api/auth/**)
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(post("/api/auth/login")
                            .header("X-Forwarded-For", testIp)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidPayload))
                    .andExpect(header().string("X-RateLimit-Limit", "10"))
                    .andExpect(status().isBadRequest()); // Validation error, but allowed through rate limiter
        }

        // The 11th request MUST be rejected with HTTP 429 Too Many Requests
        mockMvc.perform(post("/api/auth/login")
                        .header("X-Forwarded-For", testIp)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string("X-RateLimit-Limit", "10"))
                .andExpect(header().string("X-RateLimit-Remaining", "0"))
                .andExpect(header().exists("Retry-After"))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message", containsString("Rate limit exceeded")));
    }

    @Test
    @DisplayName("Should handle CORS preflight request with correct headers")
    void shouldHandleCorsPreflightRequest() throws Exception {
        mockMvc.perform(options("/api/products")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"))
                .andExpect(header().string("Access-Control-Expose-Headers", containsString("X-RateLimit-Limit")))
                .andExpect(header().string("Access-Control-Expose-Headers", containsString("Retry-After")));
    }

    @Test
    @DisplayName("Should mask sensitive data correctly to protect against logging leakage")
    void shouldMaskSensitiveDataCorrectly() {
        // Email masking
        assertThat(MaskingUtils.maskEmail("john.doe@example.com")).isEqualTo("j***e@example.com");
        assertThat(MaskingUtils.maskEmail("ab@domain.com")).isEqualTo("a***@domain.com");

        // Card masking
        assertThat(MaskingUtils.maskCardNumber("4111 1111 1111 1234")).isEqualTo("****-****-****-1234");
        assertThat(MaskingUtils.maskCardNumber("9876543210")).isEqualTo("****-****-****-3210");

        // Secret & Token masking
        assertThat(MaskingUtils.maskSecret("MySuperSecretPassword#123")).isEqualTo("[PROTECTED]");
        assertThat(MaskingUtils.maskToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.header.payload.signature"))
                .startsWith("eyJhbG...")
                .endsWith("ture");
    }
}
