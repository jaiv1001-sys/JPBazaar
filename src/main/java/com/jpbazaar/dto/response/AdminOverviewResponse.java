package com.jpbazaar.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "High-level administrative system metrics and KPIs")
public record AdminOverviewResponse(
        @Schema(description = "Total registered users across the platform", example = "1250")
        long totalUsers,

        @Schema(description = "Total registered customers", example = "1240")
        long totalCustomers,

        @Schema(description = "Total registered administrators", example = "10")
        long totalAdmins,

        @Schema(description = "Total products cataloged", example = "350")
        long totalProducts,

        @Schema(description = "Total actively listed products", example = "320")
        long totalActiveProducts,

        @Schema(description = "Total orders placed lifetime", example = "4500")
        long totalOrders,

        @Schema(description = "Total lifetime revenue from non-cancelled orders", example = "1875420.50")
        BigDecimal totalRevenue,

        @Schema(description = "Total orders currently awaiting fulfillment (PLACED, CONFIRMED, PROCESSING)", example = "42")
        long pendingOrders,

        @Schema(description = "Total completed and delivered orders", example = "4150")
        long deliveredOrders,

        @Schema(description = "Total cancelled orders", example = "308")
        long cancelledOrders,

        @Schema(description = "Total products with inventory stock at or below warning threshold", example = "8")
        long lowStockCount,

        @Schema(description = "Total orders placed today", example = "28")
        long todayOrders,

        @Schema(description = "Total revenue generated today", example = "15420.00")
        BigDecimal todayRevenue
) {
}
