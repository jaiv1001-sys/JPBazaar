package com.jpbazaar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.jpbazaar.dto.request.LoginRequest;
import com.jpbazaar.dto.request.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Scenario 1 & 2: Register newly registered user and log in successfully")
    void registerAndLoginCustomer_Success() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "John",
                "Doe",
                "john.doe@example.com",
                "securePassword123",
                "1234567890"
        );

        // 1. Register customer
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.data.role").value("CUSTOMER"));

        // 2. Login customer
        LoginRequest loginRequest = new LoginRequest("john.doe@example.com", "securePassword123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"));
    }

    @Test
    @DisplayName("Scenario: Case-insensitive email login succeeds")
    void login_CaseInsensitiveEmail_Success() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "Case",
                "Sensitive",
                "case.test@example.com",
                "securePassword123",
                "1234567890"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // Login with uppercase/mixed case email
        LoginRequest loginUpper = new LoginRequest("CASE.TEST@EXAMPLE.COM", "securePassword123");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUpper)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("case.test@example.com"));
    }

    @Test
    @DisplayName("Scenario 3: Incorrect password returns 401 with appropriate message")
    void login_IncorrectPassword_Returns401() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "Alice",
                "Smith",
                "alice.smith@example.com",
                "correctPassword123",
                "1112223333"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        LoginRequest wrongPasswordRequest = new LoginRequest("alice.smith@example.com", "WrongPassword999!");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongPasswordRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    @DisplayName("Scenario 4: Non-existent email returns 401 with appropriate message")
    void login_NonExistentEmail_Returns401() throws Exception {
        LoginRequest nonExistentRequest = new LoginRequest("nonexistent.user@example.com", "somePassword123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistentRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    @DisplayName("Scenario 5: Empty email/password rejected with 400 Bad Request")
    void login_EmptyCredentials_Returns400() throws Exception {
        String invalidPayload = "{\"email\":\"\", \"password\":\"\"}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Scenario 6 & 7: Successful login generates valid JWT granting access to protected endpoints")
    void login_GeneratesValidJwt_GrantsProtectedAccess() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "Protected",
                "User",
                "protected.user@example.com",
                "securePassword123",
                "5554443333"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest("protected.user@example.com", "securePassword123");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").exists())
                .andReturn();

        String token = JsonPath.read(loginResult.getResponse().getContentAsString(), "$.data.token");

        // Access protected /api/users/me with token
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("protected.user@example.com"));

        // Access protected /api/users/profile with token
        mockMvc.perform(get("/api/users/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("protected.user@example.com"));
    }

    @Test
    @DisplayName("Scenario 8: Missing or invalid JWT is rejected")
    void protectedEndpoint_InvalidOrMissingJwt_Rejected() throws Exception {
        // Missing token
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isForbidden());

        // Invalid token
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer invalid.jwt.token.here"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Scenario: Duplicate email registration returns 409 Conflict")
    void register_DuplicateEmail_ReturnsConflict() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "Jane",
                "Doe",
                "jane.doe@example.com",
                "password123",
                "0987654321"
        );

        // Register first time
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // Register duplicate time
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }
}
