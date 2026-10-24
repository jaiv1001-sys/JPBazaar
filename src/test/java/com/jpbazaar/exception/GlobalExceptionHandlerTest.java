package com.jpbazaar.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpbazaar.dto.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(GlobalExceptionHandlerTest.TestExceptionController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    public record SampleDto(
            @NotBlank(message = "Username cannot be blank")
            String username,

            @NotBlank(message = "Email cannot be blank")
            @Email(message = "Invalid email format")
            String email,

            @NotNull(message = "Age is required")
            @Positive(message = "Age must be positive")
            Integer age
    ) {}

    @RestController
    @RequestMapping("/test/exceptions")
    static class TestExceptionController {

        @GetMapping("/not-found")
        public void throwNotFound() {
            throw new ResourceNotFoundException("Test entity not found");
        }

        @GetMapping("/duplicate")
        public void throwDuplicate() {
            throw new DuplicateResourceException("Entity already exists with key");
        }

        @GetMapping("/invalid-request")
        public void throwInvalidRequest() {
            throw new InvalidRequestException("Invalid request data supplied");
        }

        @GetMapping("/insufficient-stock")
        public void throwInsufficientStock() {
            throw new InsufficientStockException("Insufficient stock available");
        }

        @GetMapping("/invalid-order-state")
        public void throwInvalidOrderState() {
            throw new InvalidOrderStateException("Order cannot transition to requested state");
        }

        @GetMapping("/payment-failed")
        public void throwPaymentFailed() {
            throw new PaymentFailedException("Payment processing failed at gateway");
        }

        @GetMapping("/unauthorized")
        public void throwUnauthorized() {
            throw new UnauthorizedException("You do not own this resource");
        }

        @GetMapping("/access-denied")
        public void throwAccessDenied() {
            throw new AccessDeniedException("Access denied by security constraint");
        }

        @GetMapping("/bad-credentials")
        public void throwBadCredentials() {
            throw new BadCredentialsException("Bad credentials provided");
        }

        @PostMapping("/validate")
        public ApiResponse<String> validateBody(@Valid @RequestBody SampleDto dto) {
            return ApiResponse.success("Valid", dto.username());
        }

        @GetMapping("/type-mismatch/{id}")
        public ApiResponse<Long> typeMismatch(@PathVariable Long id) {
            return ApiResponse.success(id);
        }

        @GetMapping("/required-param")
        public ApiResponse<String> requiredParam(@RequestParam String code) {
            return ApiResponse.success(code);
        }

        @GetMapping("/unhandled-server-error")
        public void throwUnhandledError() {
            throw new RuntimeException("Unexpected runtime error simulation");
        }
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 404 NOT_FOUND for ResourceNotFoundException")
    void shouldHandleResourceNotFound() throws Exception {
        mockMvc.perform(get("/test/exceptions/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Test entity not found"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 409 CONFLICT for DuplicateResourceException")
    void shouldHandleDuplicateResource() throws Exception {
        mockMvc.perform(get("/test/exceptions/duplicate"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Entity already exists with key"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 BAD_REQUEST for InvalidRequestException")
    void shouldHandleInvalidRequest() throws Exception {
        mockMvc.perform(get("/test/exceptions/invalid-request"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid request data supplied"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 BAD_REQUEST for InsufficientStockException")
    void shouldHandleInsufficientStock() throws Exception {
        mockMvc.perform(get("/test/exceptions/insufficient-stock"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Insufficient stock available"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 BAD_REQUEST for InvalidOrderStateException")
    void shouldHandleInvalidOrderState() throws Exception {
        mockMvc.perform(get("/test/exceptions/invalid-order-state"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Order cannot transition to requested state"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 BAD_REQUEST for PaymentFailedException")
    void shouldHandlePaymentFailed() throws Exception {
        mockMvc.perform(get("/test/exceptions/payment-failed"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Payment processing failed at gateway"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 403 FORBIDDEN for UnauthorizedException")
    void shouldHandleUnauthorizedException() throws Exception {
        mockMvc.perform(get("/test/exceptions/unauthorized"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("You do not own this resource"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 403 FORBIDDEN for AccessDeniedException")
    void shouldHandleAccessDeniedException() throws Exception {
        mockMvc.perform(get("/test/exceptions/access-d" + "enied"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Access denied: You do not have permission to access this resource"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 401 UNAUTHORIZED for BadCredentialsException")
    void shouldHandleBadCredentials() throws Exception {
        mockMvc.perform(get("/test/exceptions/bad-credentials"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid email or password"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 BAD_REQUEST with field errors for MethodArgumentNotValidException")
    void shouldHandleValidationErrors() throws Exception {
        SampleDto invalidDto = new SampleDto("", "not-an-email", -5);

        mockMvc.perform(post("/test/exceptions/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.data.username").value("Username cannot be blank"))
                .andExpect(jsonPath("$.data.email").value("Invalid email format"))
                .andExpect(jsonPath("$.data.age").value("Age must be positive"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 BAD_REQUEST for HttpMessageNotReadableException on malformed JSON")
    void shouldHandleMalformedJson() throws Exception {
        mockMvc.perform(post("/test/exceptions/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"username\": \"user\", \"invalid_json..."))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Malformed JSON request or invalid field format"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 BAD_REQUEST for MethodArgumentTypeMismatchException")
    void shouldHandleTypeMismatch() throws Exception {
        mockMvc.perform(get("/test/exceptions/type-mismatch/not-a-number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("Invalid value 'not-a-number' for parameter 'id'")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 BAD_REQUEST for MissingServletRequestParameterException")
    void shouldHandleMissingParameter() throws Exception {
        mockMvc.perform(get("/test/exceptions/required-param"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Required query parameter 'code' is missing"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 405 METHOD_NOT_ALLOWED for HttpRequestMethodNotSupportedException")
    void shouldHandleMethodNotSupported() throws Exception {
        mockMvc.perform(delete("/test/exceptions/not-found"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("HTTP method 'DELETE' is not supported")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 500 INTERNAL_SERVER_ERROR without exposing internal details for unhandled Exception")
    void shouldHandleUnhandledExceptionSafely() throws Exception {
        mockMvc.perform(get("/test/exceptions/unhandled-server-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("An unexpected internal server error occurred. Please contact support or try again later."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Should return 400 BAD_REQUEST with field error map when RegisterRequest validation fails")
    void shouldValidateRegisterRequestOnAuthEndpoint() throws Exception {
        String invalidRegisterJson = """
                {
                    "firstName": "",
                    "lastName": "",
                    "email": "invalid-email-format",
                    "password": "123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRegisterJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.data.firstName").value("First name is required"))
                .andExpect(jsonPath("$.data.lastName").value("Last name is required"))
                .andExpect(jsonPath("$.data.email").value("Invalid email format"))
                .andExpect(jsonPath("$.data.password").value("Password must be between 6 and 100 characters"));
    }
}
