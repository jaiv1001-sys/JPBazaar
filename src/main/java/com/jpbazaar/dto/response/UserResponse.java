package com.jpbazaar.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

@Schema(description = "Customer user profile response")
public record UserResponse(
        @Schema(description = "User unique ID", example = "1")
        Long id,

        @Schema(description = "First name", example = "Jane")
        String firstName,

        @Schema(description = "Last name", example = "Doe")
        String lastName,

        @Schema(description = "Registered email address", example = "jane.doe@example.com")
        String email,

        @Schema(description = "Phone number", example = "+1234567890")
        String phone,

        @Schema(description = "Assigned user role", example = "ROLE_CUSTOMER")
        String role,

        @Schema(description = "Account enabled status", example = "true")
        Boolean enabled,

        @Schema(description = "Account creation timestamp")
        OffsetDateTime createdAt
) {}
