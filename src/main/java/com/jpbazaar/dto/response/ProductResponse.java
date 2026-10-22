package com.jpbazaar.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record ProductResponse(
        Long id,
        Long categoryId,
        String categoryName,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        String sku,
        String brand,
        BigDecimal rating,
        Boolean active,
        List<String> imageUrls,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
