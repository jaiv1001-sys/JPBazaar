package com.jpbazaar.dto.response;

public record AuthResponse(
        String token,
        String tokenType,
        Long userId,
        String email,
        String role,
        long expiresInMs
) {
    public static AuthResponse of(String token, Long userId, String email, String role, long expiresInMs) {
        return new AuthResponse(token, "Bearer", userId, email, role, expiresInMs);
    }
}
