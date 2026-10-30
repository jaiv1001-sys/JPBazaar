package com.jpbazaar.controller;

import com.jpbazaar.dto.ApiResponse;
import com.jpbazaar.dto.response.*;
import com.jpbazaar.service.AdminDashboardService;
import com.jpbazaar.util.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Admin Analytics", description = "Administrative analytics dashboard, sales trends, KPIs, and inventory health metrics")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @Operation(summary = "Get consolidated dashboard metrics", description = "Retrieves complete administrative dashboard snapshot including KPIs, order status distribution, sales trend, top products, low stock alerts, and category performance.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Dashboard snapshot retrieved successfully",
                    content = @Content(schema = @Schema(implementation = AdminDashboardStatsResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Valid JWT token required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AdminDashboardStatsResponse>> getDashboardStats() {
        AdminDashboardStatsResponse stats = adminDashboardService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("Admin dashboard statistics retrieved successfully", stats));
    }

    @Operation(summary = "Get system KPI overview", description = "Retrieves high-level summary counts for users, products, orders, revenue, pending orders, and today's velocity.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "KPI metrics retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<AdminOverviewResponse>> getOverview() {
        AdminOverviewResponse overview = adminDashboardService.getOverview();
        return ResponseEntity.ok(ApiResponse.success("Admin overview metrics retrieved successfully", overview));
    }

    @Operation(summary = "Get order status distribution", description = "Retrieves order counts and percentage distribution across all lifecycle states.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order status distribution retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    @GetMapping("/orders-by-status")
    public ResponseEntity<ApiResponse<List<OrderStatusCountResponse>>> getOrdersByStatus() {
        List<OrderStatusCountResponse> statusDistribution = adminDashboardService.getOrdersByStatus();
        return ResponseEntity.ok(ApiResponse.success("Order status distribution retrieved successfully", statusDistribution));
    }

    @Operation(summary = "Get daily sales trend", description = "Retrieves continuous daily sales and order volume trend for the specified number of past days.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sales trend data retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    @GetMapping("/sales-trend")
    public ResponseEntity<ApiResponse<List<DailySalesResponse>>> getSalesTrend(
            @Parameter(description = "Number of past days to analyze (default 30, max 365)", example = "30")
            @RequestParam(defaultValue = "30") int days
    ) {
        List<DailySalesResponse> trend = adminDashboardService.getSalesTrend(days);
        return ResponseEntity.ok(ApiResponse.success("Sales trend retrieved successfully", trend));
    }

    @Operation(summary = "Get top selling products", description = "Retrieves top products ranked by total volume sold and gross revenue.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Top selling products retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    @GetMapping("/top-products")
    public ResponseEntity<ApiResponse<List<TopSellingProductResponse>>> getTopSellingProducts(
            @Parameter(description = "Maximum number of top products to return (default 5, max 100)", example = "5")
            @RequestParam(defaultValue = "5") int limit
    ) {
        List<TopSellingProductResponse> topProducts = adminDashboardService.getTopSellingProducts(limit);
        return ResponseEntity.ok(ApiResponse.success("Top selling products retrieved successfully", topProducts));
    }

    @Operation(summary = "Get low stock products", description = "Retrieves paginated list of active products with inventory quantity at or below the warning threshold.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Low stock product alerts retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<PageResponse<LowStockProductResponse>>> getLowStockProducts(
            @Parameter(description = "Inventory quantity warning threshold (default 10)", example = "10")
            @RequestParam(defaultValue = "10") int threshold,
            @Parameter(description = "Page index (0-based)", example = "0")
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "stockQuantity"));
        PageResponse<LowStockProductResponse> lowStockPage = adminDashboardService.getLowStockProducts(threshold, pageable);
        return ResponseEntity.ok(ApiResponse.success("Low stock products retrieved successfully", lowStockPage));
    }

    @Operation(summary = "Get recent orders", description = "Retrieves latest customer orders for real-time monitoring.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Recent orders retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    @GetMapping("/recent-orders")
    public ResponseEntity<ApiResponse<List<RecentOrderSummaryResponse>>> getRecentOrders(
            @Parameter(description = "Maximum number of recent orders to return (default 5, max 100)", example = "5")
            @RequestParam(defaultValue = "5") int limit
    ) {
        List<RecentOrderSummaryResponse> recentOrders = adminDashboardService.getRecentOrders(limit);
        return ResponseEntity.ok(ApiResponse.success("Recent orders retrieved successfully", recentOrders));
    }

    @Operation(summary = "Get category sales statistics", description = "Retrieves sales volume, revenue, and product counts grouped by category.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category sales statistics retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    @GetMapping("/category-sales")
    public ResponseEntity<ApiResponse<List<CategorySalesResponse>>> getCategorySales() {
        List<CategorySalesResponse> categoryStats = adminDashboardService.getCategorySales();
        return ResponseEntity.ok(ApiResponse.success("Category sales statistics retrieved successfully", categoryStats));
    }
}
