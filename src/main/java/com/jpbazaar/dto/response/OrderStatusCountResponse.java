package com.jpbazaar.dto.response;

import com.jpbazaar.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Aggregated count and percentage distribution for an order status")
public record OrderStatusCountResponse(
        @Schema(description = "Order lifecycle status", example = "DELIVERED")
        OrderStatus status,

        @Schema(description = "Total number of orders currently in this status", example = "4150")
        long count,

        @Schema(description = "Percentage share of total orders", example = "92.22")
        double percentage
) {
}
