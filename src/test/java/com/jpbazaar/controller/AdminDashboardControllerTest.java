package com.jpbazaar.controller;

import com.jpbazaar.dto.response.*;
import com.jpbazaar.entity.OrderStatus;
import com.jpbazaar.service.AdminDashboardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminDashboardService adminDashboardService;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/admin/dashboard/stats - Admin should retrieve full consolidated dashboard snapshot")
    void getDashboardStats_asAdmin_shouldReturn200AndStats() throws Exception {
        AdminOverviewResponse overview = new AdminOverviewResponse(
                500L, 480L, 20L, 100L, 90L, 1200L,
                new BigDecimal("450000.00"), 25L, 1100L, 75L,
                5L, 12L, new BigDecimal("8500.00")
        );

        AdminDashboardStatsResponse mockResponse = new AdminDashboardStatsResponse(
                overview,
                List.of(new OrderStatusCountResponse(OrderStatus.DELIVERED, 1100L, 91.67)),
                List.of(new DailySalesResponse(LocalDate.now(), 12L, new BigDecimal("8500.00"))),
                List.of(new TopSellingProductResponse(1L, "Laptop Pro", "LAPTOP-01", 45L, new BigDecimal("450000.00"))),
                List.of(new LowStockProductResponse(2L, "Mouse", "MOU-01", "Logi", "Electronics", new BigDecimal("500.00"), 2)),
                List.of(new RecentOrderSummaryResponse(101L, 5L, "Bob Smith", "bob@example.com", new BigDecimal("4500.00"), OrderStatus.PLACED, 2, OffsetDateTime.now())),
                List.of(new CategorySalesResponse(1L, "Electronics", 50L, 400L, new BigDecimal("400000.00")))
        );

        when(adminDashboardService.getDashboardStats()).thenReturn(mockResponse);

        mockMvc.perform(get("/api/admin/dashboard/stats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Admin dashboard statistics retrieved successfully"))
                .andExpect(jsonPath("$.data.overview.totalUsers").value(500))
                .andExpect(jsonPath("$.data.overview.totalRevenue").value(450000.00))
                .andExpect(jsonPath("$.data.ordersByStatus[0].status").value("DELIVERED"))
                .andExpect(jsonPath("$.data.topSellingProducts[0].productName").value("Laptop Pro"))
                .andExpect(jsonPath("$.data.categorySales[0].categoryName").value("Electronics"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/admin/dashboard/overview - Admin should retrieve system KPI overview")
    void getOverview_asAdmin_shouldReturn200() throws Exception {
        AdminOverviewResponse overview = new AdminOverviewResponse(
                100L, 95L, 5L, 30L, 28L, 250L,
                new BigDecimal("85000.00"), 10L, 220L, 20L,
                2L, 4L, new BigDecimal("3200.00")
        );

        when(adminDashboardService.getOverview()).thenReturn(overview);

        mockMvc.perform(get("/api/admin/dashboard/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalOrders").value(250))
                .andExpect(jsonPath("$.data.pendingOrders").value(10))
                .andExpect(jsonPath("$.data.totalRevenue").value(85000.00));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/admin/dashboard/orders-by-status - Admin should retrieve order breakdown")
    void getOrdersByStatus_asAdmin_shouldReturn200() throws Exception {
        List<OrderStatusCountResponse> mockList = List.of(
                new OrderStatusCountResponse(OrderStatus.PLACED, 5L, 10.0),
                new OrderStatusCountResponse(OrderStatus.DELIVERED, 40L, 80.0),
                new OrderStatusCountResponse(OrderStatus.CANCELLED, 5L, 10.0)
        );

        when(adminDashboardService.getOrdersByStatus()).thenReturn(mockList);

        mockMvc.perform(get("/api/admin/dashboard/orders-by-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.data[0].status").value("PLACED"))
                .andExpect(jsonPath("$.data[1].count").value(40));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/admin/dashboard/sales-trend - Admin should retrieve daily sales trend")
    void getSalesTrend_asAdmin_shouldReturn200() throws Exception {
        List<DailySalesResponse> trend = List.of(
                new DailySalesResponse(LocalDate.now().minusDays(1), 3L, new BigDecimal("1500.00")),
                new DailySalesResponse(LocalDate.now(), 5L, new BigDecimal("3500.00"))
        );

        when(adminDashboardService.getSalesTrend(eq(7))).thenReturn(trend);

        mockMvc.perform(get("/api/admin/dashboard/sales-trend?days=7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[1].orderCount").value(5));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/admin/dashboard/top-products - Admin should retrieve top selling products")
    void getTopProducts_asAdmin_shouldReturn200() throws Exception {
        List<TopSellingProductResponse> topProducts = List.of(
                new TopSellingProductResponse(10L, "Smartphone", "SP-01", 100L, new BigDecimal("5000000.00"))
        );

        when(adminDashboardService.getTopSellingProducts(eq(5))).thenReturn(topProducts);

        mockMvc.perform(get("/api/admin/dashboard/top-products?limit=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].productName").value("Smartphone"))
                .andExpect(jsonPath("$.data[0].totalUnitsSold").value(100));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/admin/dashboard/low-stock - Admin should retrieve paginated low stock products")
    void getLowStock_asAdmin_shouldReturn200() throws Exception {
        LowStockProductResponse lowStockItem = new LowStockProductResponse(
                12L, "USB Cable", "USB-01", "Anker", "Accessories", new BigDecimal("299.00"), 3
        );
        PageResponse<LowStockProductResponse> pageResponse = PageResponse.from(
                new PageImpl<>(List.of(lowStockItem))
        );

        when(adminDashboardService.getLowStockProducts(eq(10), any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/admin/dashboard/low-stock?threshold=10&page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].name").value("USB Cable"))
                .andExpect(jsonPath("$.data.content[0].stockQuantity").value(3));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/admin/dashboard/recent-orders - Admin should retrieve recent orders")
    void getRecentOrders_asAdmin_shouldReturn200() throws Exception {
        List<RecentOrderSummaryResponse> recentOrders = List.of(
                new RecentOrderSummaryResponse(99L, 1L, "Alice Smith", "alice@example.com", new BigDecimal("1299.00"), OrderStatus.PLACED, 1, OffsetDateTime.now())
        );

        when(adminDashboardService.getRecentOrders(eq(5))).thenReturn(recentOrders);

        mockMvc.perform(get("/api/admin/dashboard/recent-orders?limit=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].customerName").value("Alice Smith"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/admin/dashboard/category-sales - Admin should retrieve category breakdown")
    void getCategorySales_asAdmin_shouldReturn200() throws Exception {
        List<CategorySalesResponse> categorySales = List.of(
                new CategorySalesResponse(1L, "Footwear", 30L, 85L, new BigDecimal("250000.00"))
        );

        when(adminDashboardService.getCategorySales()).thenReturn(categorySales);

        mockMvc.perform(get("/api/admin/dashboard/category-sales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].categoryName").value("Footwear"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    @DisplayName("GET /api/admin/dashboard/stats - CUSTOMER role should be forbidden with 403")
    void getDashboardStats_asCustomer_shouldReturn403Forbidden() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard/stats"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/admin/dashboard/stats - Unauthenticated user should be rejected with 403 Forbidden")
    void getDashboardStats_unauthenticated_shouldBeRejected() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard/stats"))
                .andExpect(status().isForbidden());
    }
}
