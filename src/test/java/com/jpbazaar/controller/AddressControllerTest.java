package com.jpbazaar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpbazaar.dto.request.AddressRequest;
import com.jpbazaar.dto.request.RegisterRequest;
import com.jpbazaar.dto.response.AddressResponse;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String user1Token;
    private String user2Token;

    @BeforeEach
    void setUp() throws Exception {
        user1Token = registerAndGetToken("User1", "One", "user1@example.com", "password123");
        user2Token = registerAndGetToken("User2", "Two", "user2@example.com", "password123");
    }

    private String registerAndGetToken(String firstName, String lastName, String email, String password) throws Exception {
        RegisterRequest request = new RegisterRequest(firstName, lastName, email, password, "1234567890");
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(
                objectMapper.readTree(content).get("data").toString(),
                AuthResponse.class
        );
        return authResponse.token();
    }

    @Test
    void addAddressAndSetDefault_Success() throws Exception {
        AddressRequest request = new AddressRequest(
                "123 Main St",
                "Metropolis",
                "NY",
                "10001",
                "USA",
                true
        );

        mockMvc.perform(post("/api/addresses")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.city").value("Metropolis"))
                .andExpect(jsonPath("$.data.isDefault").value(true));
    }

    @Test
    void accessOtherUserAddress_IDORAttack_ReturnsForbidden() throws Exception {
        // 1. User 1 adds an address
        AddressRequest request = new AddressRequest(
                "456 Private Ave",
                "Gotham",
                "NJ",
                "07001",
                "USA",
                true
        );

        MvcResult result = mockMvc.perform(post("/api/addresses")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        AddressResponse addressResponse = objectMapper.readValue(
                objectMapper.readTree(content).get("data").toString(),
                AddressResponse.class
        );
        Long user1AddressId = addressResponse.id();

        // 2. User 2 attempts to fetch User 1's address ID -> IDOR Attack!
        mockMvc.perform(get("/api/addresses/" + user1AddressId)
                        .header("Authorization", "Bearer " + user2Token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Access denied: You do not own address ID " + user1AddressId));
    }
}
