package com.jpbazaar.dto.response;

import java.math.BigDecimal;

public record CartItemResponse(
        Long id,
        Long productId,
        String productName,
        String productSku,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal subtotal,
        String imageUrl
) {}
