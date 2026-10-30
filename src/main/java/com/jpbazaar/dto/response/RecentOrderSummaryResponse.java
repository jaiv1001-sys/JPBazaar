package com.jpbazaar.dto.response;

import com.jpbazaar.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Schema(description = "Concise summary of a recent order for admin activity feed")
public record RecentOrderSummaryResponse(
        @Schema(description = "Order unique identifier", example = "1001")
        Long id,

        @Schema(description = "Customer user ID", example = "45")
        Long userId,

        @Schema(description = "Customer full name", example = "Alice Johnson")
        String customerName,

        @Schema(description = "Customer email address", example = "alice@example.com")
        String customerEmail,

        @Schema(description = "Order total monetary amount", example = "4999.00")
        BigDecimal totalAmount,

        @Schema(description = "Current lifecycle status of the order", example = "PLACED")
        OrderStatus status,

        @Schema(description = "Total number of line items in the order", example = "3")
        int itemCount,

        @Schema(description = "Timestamp when the order was submitted", example = "2026-10-30T10:15:30Z")
        OffsetDateTime createdAt
) {
}
