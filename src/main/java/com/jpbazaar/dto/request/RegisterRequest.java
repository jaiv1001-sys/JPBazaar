package com.jpbazaar.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Customer account registration payload")
public record RegisterRequest(
        @Schema(description = "Customer first name", example = "Jane")
        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name must not exceed 50 characters")
        String firstName,

        @Schema(description = "Customer last name", example = "Doe")
        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name must not exceed 50 characters")
        String lastName,

        @Schema(description = "Customer email address used for login", example = "jane.doe@example.com")
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Size(max = 100, message = "Email must not exceed 100 characters")
        String email,

        @Schema(description = "Account password (min 6 characters)", example = "SecurePass123!")
        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
        String password,

        @Schema(description = "Customer contact phone number", example = "+1234567890")
        @Size(max = 20, message = "Phone number must not exceed 20 characters")
        String phone
) {}
