package com.jpbazaar.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Comprehensive administrative dashboard statistics snapshot")
public record AdminDashboardStatsResponse(
        @Schema(description = "System-wide high-level KPI metrics")
        AdminOverviewResponse overview,

        @Schema(description = "Order lifecycle status distribution breakdown")
        List<OrderStatusCountResponse> ordersByStatus,

        @Schema(description = "Recent daily sales trend (default last 30 days)")
        List<DailySalesResponse> recentSalesTrend,

        @Schema(description = "Top selling products by volume and revenue")
        List<TopSellingProductResponse> topSellingProducts,

        @Schema(description = "Active products with inventory at or below threshold")
        List<LowStockProductResponse> lowStockProducts,

        @Schema(description = "Most recent customer orders")
        List<RecentOrderSummaryResponse> recentOrders,

        @Schema(description = "Product and revenue distribution across categories")
        List<CategorySalesResponse> categorySales
) {
}
