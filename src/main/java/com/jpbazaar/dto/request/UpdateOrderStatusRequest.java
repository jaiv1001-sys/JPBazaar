package com.jpbazaar.dto.request;

import com.jpbazaar.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record UpdateOrderStatusRequest(
        @NotNull(message = "Order status is required")
        OrderStatus status,
        String notes,
        String carrier,
        String trackingNumber,
        OffsetDateTime estimatedDeliveryDate
) {
    public UpdateOrderStatusRequest(OrderStatus status) {
        this(status, null, null, null, null);
    }
}
