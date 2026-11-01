package com.jpbazaar.dto.response;

import com.jpbazaar.entity.OrderStatus;

import java.time.OffsetDateTime;

public record TrackingCheckpointResponse(
        int step,
        OrderStatus status,
        String label,
        String description,
        boolean completed,
        boolean current,
        OffsetDateTime timestamp
) {
}
