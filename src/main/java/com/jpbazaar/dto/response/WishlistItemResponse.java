package com.jpbazaar.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record WishlistItemResponse(
        Long id,
        Long productId,
        String productName,
        String productSku,
        BigDecimal price,
        Boolean inStock,
        String imageUrl,
        OffsetDateTime addedAt
) {}
