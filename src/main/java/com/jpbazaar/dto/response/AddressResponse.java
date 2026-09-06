package com.jpbazaar.dto.response;

import java.time.OffsetDateTime;

public record AddressResponse(
        Long id,
        Long userId,
        String addressLine,
        String city,
        String state,
        String postalCode,
        String country,
        Boolean isDefault,
        OffsetDateTime createdAt
) {}
