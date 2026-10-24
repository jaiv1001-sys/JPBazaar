package com.jpbazaar.dto.response;

import java.io.Serializable;
import java.time.OffsetDateTime;

public record CategoryResponse(
        Long id,
        String name,
        String description,
        OffsetDateTime createdAt
) implements Serializable {}
