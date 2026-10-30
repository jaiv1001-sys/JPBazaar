package com.jpbazaar.service;

import com.jpbazaar.dto.response.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for administrative analytics, business intelligence metrics,
 * sales forecasting trends, and inventory health tracking.
 */
public interface AdminDashboardService {

    /**
     * Retrieves the consolidated administrative dashboard snapshot.
     */
    AdminDashboardStatsResponse getDashboardStats();

    /**
     * Retrieves high-level KPI overview metrics.
     */
    AdminOverviewResponse getOverview();

    /**
     * Retrieves aggregated order counts and percentage share by status.
     */
    List<OrderStatusCountResponse> getOrdersByStatus();

    /**
     * Retrieves daily sales trend for the past N days with complete daily date filling.
     *
     * @param days number of days to analyze (e.g. 7, 30)
     */
    List<DailySalesResponse> getSalesTrend(int days);

    /**
     * Retrieves top-selling products ranked by volume and revenue.
     *
     * @param limit maximum number of products to return
     */
    List<TopSellingProductResponse> getTopSellingProducts(int limit);

    /**
     * Retrieves paginated low stock product alerts at or below the warning threshold.
     *
     * @param threshold stock quantity threshold
     * @param pageable  pagination parameters
     */
    PageResponse<LowStockProductResponse> getLowStockProducts(int threshold, Pageable pageable);

    /**
     * Retrieves recent customer orders.
     *
     * @param limit maximum number of orders to return
     */
    List<RecentOrderSummaryResponse> getRecentOrders(int limit);

    /**
     * Retrieves sales performance and product distribution grouped by category.
     */
    List<CategorySalesResponse> getCategorySales();
}
