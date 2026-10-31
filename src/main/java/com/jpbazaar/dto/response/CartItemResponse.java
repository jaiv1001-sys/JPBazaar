package com.jpbazaar.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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
) {
    @JsonProperty("price")
    public BigDecimal getPrice() {
        return unitPrice;
    }
}
