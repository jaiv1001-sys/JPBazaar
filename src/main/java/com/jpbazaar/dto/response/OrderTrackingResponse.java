package com.jpbazaar.dto.response;

import com.jpbazaar.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record OrderTrackingResponse(
        Long orderId,
        OrderStatus currentStatus,
        String carrier,
        String trackingNumber,
        OffsetDateTime estimatedDeliveryDate,
        OffsetDateTime shippedAt,
        OffsetDateTime deliveredAt,
        String deliveryNotes,
        BigDecimal totalAmount,
        String recipientName,
        String deliveryAddress,
        List<OrderItemResponse> items,
        List<TrackingCheckpointResponse> checkpoints,
        List<OrderStatusHistoryResponse> history
) {
}
