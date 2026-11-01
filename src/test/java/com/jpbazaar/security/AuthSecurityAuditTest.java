package com.jpbazaar.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpbazaar.dto.request.AddressRequest;
import com.jpbazaar.dto.response.AddressResponse;
import com.jpbazaar.entity.Address;
import com.jpbazaar.entity.Role;
import com.jpbazaar.entity.User;
import com.jpbazaar.repository.AddressRepository;
import com.jpbazaar.repository.UserRepository;
import com.jpbazaar.service.AddressService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthSecurityAuditTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AddressService addressService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private User customerA;
    private User customerB;
    private User adminUser;
    private User disabledUser;

    private String tokenCustomerA;
    private String tokenCustomerB;
    private String tokenAdmin;

    @BeforeEach
    void setUp() {
        long timestamp = System.nanoTime();

        customerA = userRepository.save(User.builder()
                .firstName("Customer")
                .lastName("Alice")
                .email("alice_" + timestamp + "@audit.jpbazaar.com")
                .password(passwordEncoder.encode("PassAlice123!"))
                .role(Role.CUSTOMER)
                .enabled(true)
                .build());

        customerB = userRepository.save(User.builder()
                .firstName("Customer")
                .lastName("Bob")
                .email("bob_" + timestamp + "@audit.jpbazaar.com")
                .password(passwordEncoder.encode("PassBob123!"))
                .role(Role.CUSTOMER)
                .enabled(true)
                .build());

        adminUser = userRepository.save(User.builder()
                .firstName("Super")
                .lastName("Admin")
                .email("admin_" + timestamp + "@audit.jpbazaar.com")
                .password(passwordEncoder.encode("AdminPass123!"))
                .role(Role.ADMIN)
                .enabled(true)
                .build());

        disabledUser = userRepository.save(User.builder()
                .firstName("Disabled")
                .lastName("User")
                .email("disabled_" + timestamp + "@audit.jpbazaar.com")
                .password(passwordEncoder.encode("DisabledPass123!"))
                .role(Role.CUSTOMER)
                .enabled(false)
                .build());

        tokenCustomerA = generateTokenForUser(customerA);
        tokenCustomerB = generateTokenForUser(customerB);
        tokenAdmin = generateTokenForUser(adminUser);
    }

    private String generateTokenForUser(User user) {
        SecurityUser secUser = new SecurityUser(user);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(secUser, null, secUser.getAuthorities());
        return jwtTokenProvider.generateToken(auth);
    }

    @Test
    @DisplayName("Should reject malformed or manipulated JWT tokens")
    void testMalformedTokenRejected() throws Exception {
        mockMvc.perform(get("/api/users/profile")
                        .header("Authorization", "Bearer invalid.malformed.token"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should reject expired JWT tokens")
    void testExpiredTokenRejected() throws Exception {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Date now = new Date(System.currentTimeMillis() - 100000);
        Date expired = new Date(System.currentTimeMillis() - 1000);

        String expiredToken = Jwts.builder()
                .subject(customerA.getEmail())
                .claim("userId", customerA.getId())
                .claim("role", customerA.getRole().name())
                .issuedAt(now)
                .expiration(expired)
                .signWith(key)
                .compact();

        mockMvc.perform(get("/api/users/profile")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should reject authentication for disabled customer even with signed token")
    void testDisabledUserAuthenticationRejected() throws Exception {
        String tokenForDisabled = generateTokenForUser(disabledUser);

        mockMvc.perform(get("/api/users/profile")
                        .header("Authorization", "Bearer " + tokenForDisabled))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should deny CUSTOMER role access to administrative endpoints (HTTP 403)")
    void testCustomerForbiddenFromAdminEndpoints() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard/stats")
                        .header("Authorization", "Bearer " + tokenCustomerA))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/admin/orders")
                        .header("Authorization", "Bearer " + tokenCustomerA))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should permit ADMIN role access to administrative endpoints")
    void testAdminAllowedOnAdminEndpoints() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard/stats")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("IDOR Prevention: Customer B cannot read Customer A's address")
    void testAddressIdorReadPrevention() throws Exception {
        Address addressA = addressRepository.save(Address.builder()
                .user(customerA)
                .addressLine("101 Luxury Blvd")
                .city("Manhattan")
                .state("NY")
                .postalCode("10001")
                .country("USA")
                .isDefault(true)
                .build());

        // Alice (owner) can access
        mockMvc.perform(get("/api/addresses/" + addressA.getId())
                        .header("Authorization", "Bearer " + tokenCustomerA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.addressLine").value("101 Luxury Blvd"));

        // Bob (attacker) MUST receive 403 Forbidden
        mockMvc.perform(get("/api/addresses/" + addressA.getId())
                        .header("Authorization", "Bearer " + tokenCustomerB))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("IDOR Prevention: Customer B cannot delete Customer A's address")
    void testAddressIdorDeletePrevention() throws Exception {
        Address addressA = addressRepository.save(Address.builder()
                .user(customerA)
                .addressLine("102 Luxury Blvd")
                .city("Manhattan")
                .state("NY")
                .postalCode("10001")
                .country("USA")
                .isDefault(false)
                .build());

        // Bob attempts to delete Alice's address
        mockMvc.perform(delete("/api/addresses/" + addressA.getId())
                        .header("Authorization", "Bearer " + tokenCustomerB))
                .andExpect(status().isForbidden());

        // Verify address was NOT deleted
        assertThat(addressRepository.existsById(addressA.getId())).isTrue();
    }

    @Test
    @DisplayName("Password Protection: Password hash must never be serialized in JSON")
    void testPasswordNeverSerialized() throws Exception {
        String json = objectMapper.writeValueAsString(customerA);
        assertThat(json).doesNotContain("password");
        assertThat(json).doesNotContain("PassAlice123!");
    }
}
