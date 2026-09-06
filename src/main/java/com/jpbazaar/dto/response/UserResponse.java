package com.jpbazaar.dto.response;

import java.time.OffsetDateTime;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String role,
        Boolean enabled,
        OffsetDateTime createdAt
) {}
