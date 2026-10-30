package com.jpbazaar.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Customer authentication login credentials")
public record LoginRequest(
        @Schema(description = "Registered user email address", example = "jane.doe@example.com")
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @Schema(description = "Account password", example = "SecurePass123!")
        @NotBlank(message = "Password is required")
        String password
) {}
