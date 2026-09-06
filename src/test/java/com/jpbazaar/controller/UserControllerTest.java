package com.jpbazaar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpbazaar.dto.request.ChangePasswordRequest;
import com.jpbazaar.dto.request.RegisterRequest;
import com.jpbazaar.dto.request.UpdateProfileRequest;
import com.jpbazaar.dto.response.AuthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "Alice",
                "Smith",
                "alice.smith@example.com",
                "password123",
                "1112223333"
        );

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andReturn();

        if (result.getResponse().getStatus() == 201) {
            String content = result.getResponse().getContentAsString();
            AuthResponse authResponse = objectMapper.readValue(
                    objectMapper.readTree(content).get("data").toString(),
                    AuthResponse.class
            );
            jwtToken = authResponse.token();
        }
    }

    @Test
    void getCurrentProfile_Success() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("alice.smith@example.com"))
                .andExpect(jsonPath("$.data.firstName").value("Alice"));
    }

    @Test
    void updateProfile_Success() throws Exception {
        UpdateProfileRequest updateRequest = new UpdateProfileRequest("AliceUpdated", "SmithUpdated", "9998887777");

        mockMvc.perform(put("/api/users/me")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("AliceUpdated"))
                .andExpect(jsonPath("$.data.phone").value("9998887777"));
    }

    @Test
    void changePassword_Success() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("password123", "newPassword123");

        mockMvc.perform(put("/api/users/change-password")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password changed successfully"));
    }

    private static org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder post(String url) {
        return org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(url);
    }
}
