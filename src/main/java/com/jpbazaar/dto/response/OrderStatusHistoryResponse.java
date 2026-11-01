package com.jpbazaar.dto.response;

import com.jpbazaar.entity.OrderStatus;
import com.jpbazaar.entity.OrderStatusHistory;

import java.time.OffsetDateTime;

public record OrderStatusHistoryResponse(
        Long id,
        OrderStatus status,
        String notes,
        String updatedBy,
        OffsetDateTime createdAt
) {
    public static OrderStatusHistoryResponse fromEntity(OrderStatusHistory history) {
        return new OrderStatusHistoryResponse(
                history.getId(),
                history.getStatus(),
                history.getNotes(),
                history.getUpdatedBy(),
                history.getCreatedAt()
        );
    }
}
