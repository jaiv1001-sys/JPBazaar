package com.jpbazaar.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication response with JWT bearer token")
public record AuthResponse(
        @Schema(description = "JWT access token string", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token,

        @Schema(description = "Token type format", example = "Bearer")
        String tokenType,

        @Schema(description = "Authenticated user ID", example = "1")
        Long userId,

        @Schema(description = "User email address", example = "jane.doe@example.com")
        String email,

        @Schema(description = "User role authority", example = "ROLE_CUSTOMER")
        String role,

        @Schema(description = "Token expiration duration in milliseconds", example = "86400000")
        long expiresInMs
) {
    public static AuthResponse of(String token, Long userId, String email, String role, long expiresInMs) {
        return new AuthResponse(token, "Bearer", userId, email, role, expiresInMs);
    }
}
