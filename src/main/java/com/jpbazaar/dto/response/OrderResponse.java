package com.jpbazaar.dto.response;

import com.jpbazaar.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Schema(description = "Customer order response details")
public record OrderResponse(
        @Schema(description = "Order ID", example = "101")
        Long id,

        @Schema(description = "Customer user ID", example = "1")
        Long userId,

        @Schema(description = "Customer email address", example = "jane.doe@example.com")
        String userEmail,

        @Schema(description = "Shipping address ID", example = "1")
        Long addressId,

        @Schema(description = "Formatted shipping address snapshot", example = "42 Silicon Avenue, Apt 7B, San Francisco, California, 94107, United States")
        String shippingAddress,

        @Schema(description = "Total monetary order amount", example = "399.98")
        BigDecimal totalAmount,

        @Schema(description = "Current lifecycle order status", example = "PENDING")
        OrderStatus status,

        @Schema(description = "Total number of purchased items", example = "2")
        int totalItemCount,

        @Schema(description = "List of line items in order")
        List<OrderItemResponse> items,

        @Schema(description = "Associated payment record")
        PaymentResponse payment,

        @Schema(description = "Order creation timestamp")
        OffsetDateTime createdAt,

        @Schema(description = "Order last status update timestamp")
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
