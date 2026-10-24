package com.jpbazaar.dto.response;

import com.jpbazaar.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Long userId,
        String userEmail,
        Long addressId,
        String shippingAddress,
        BigDecimal totalAmount,
        OrderStatus status,
        int totalItemCount,
        List<OrderItemResponse> items,
        PaymentResponse payment,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public OrderResponse(
            Long id,
            Long userId,
            String userEmail,
            Long addressId,
            String shippingAddress,
            BigDecimal totalAmount,
            OrderStatus status,
            int totalItemCount,
            List<OrderItemResponse> items,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this(id, userId, userEmail, addressId, shippingAddress, totalAmount, status, totalItemCount, items, null, createdAt, updatedAt);
    }
}
