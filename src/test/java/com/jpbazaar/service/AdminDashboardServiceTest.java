package com.jpbazaar.service;

import com.jpbazaar.dto.response.*;
import com.jpbazaar.entity.Category;
import com.jpbazaar.entity.OrderStatus;
import com.jpbazaar.entity.Product;
import com.jpbazaar.entity.Role;
import com.jpbazaar.repository.*;
import com.jpbazaar.service.impl.AdminDashboardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminDashboardServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private AdminDashboardServiceImpl adminDashboardService;

    private OffsetDateTime testTimestamp;

    @BeforeEach
    void setUp() {
        testTimestamp = OffsetDateTime.of(2026, 10, 30, 10, 0, 0, 0, ZoneOffset.UTC);
    }

    @Test
    @DisplayName("Should calculate KPI overview metrics accurately from database counts and sums")
    void shouldCalculateOverviewMetrics() {
        when(userRepository.count()).thenReturn(100L);
        when(userRepository.countByRole(Role.CUSTOMER)).thenReturn(95L);
        when(userRepository.countByRole(Role.ADMIN)).thenReturn(5L);

        when(productRepository.count()).thenReturn(50L);
        when(productRepository.countByActiveTrue()).thenReturn(45L);
        when(productRepository.countByStockQuantityLessThanEqualAndActiveTrue(10)).thenReturn(4L);

        when(orderRepository.count()).thenReturn(200L);
        when(orderRepository.calculateTotalRevenue()).thenReturn(new BigDecimal("150000.00"));
        when(orderRepository.countPendingOrders()).thenReturn(15L);
        when(orderRepository.countByStatus(OrderStatus.DELIVERED)).thenReturn(170L);
        when(orderRepository.countByStatus(OrderStatus.CANCELLED)).thenReturn(15L);

        when(orderRepository.countByCreatedAtAfter(any())).thenReturn(10L);
        when(orderRepository.calculateRevenueSince(any())).thenReturn(new BigDecimal("5000.00"));

        AdminOverviewResponse overview = adminDashboardService.getOverview();

        assertThat(overview).isNotNull();
        assertThat(overview.totalUsers()).isEqualTo(100L);
        assertThat(overview.totalCustomers()).isEqualTo(95L);
        assertThat(overview.totalAdmins()).isEqualTo(5L);
        assertThat(overview.totalProducts()).isEqualTo(50L);
        assertThat(overview.totalActiveProducts()).isEqualTo(45L);
        assertThat(overview.totalOrders()).isEqualTo(200L);
        assertThat(overview.totalRevenue()).isEqualByComparingTo("150000.00");
        assertThat(overview.pendingOrders()).isEqualTo(15L);
        assertThat(overview.deliveredOrders()).isEqualTo(170L);
        assertThat(overview.cancelledOrders()).isEqualTo(15L);
        assertThat(overview.lowStockCount()).isEqualTo(4L);
        assertThat(overview.todayOrders()).isEqualTo(10L);
        assertThat(overview.todayRevenue()).isEqualByComparingTo("5000.00");
    }

    @Test
    @DisplayName("Should aggregate order status distribution with accurate counts and percentages")
    void shouldAggregateOrderStatusDistribution() {
        when(orderRepository.count()).thenReturn(100L);

        List<Object[]> queryResults = new ArrayList<>();
        queryResults.add(new Object[]{OrderStatus.PLACED, 10L});
        queryResults.add(new Object[]{OrderStatus.DELIVERED, 80L});
        queryResults.add(new Object[]{OrderStatus.CANCELLED, 10L});

        when(orderRepository.countOrdersByStatusGroup()).thenReturn(queryResults);

        List<OrderStatusCountResponse> statusDistribution = adminDashboardService.getOrdersByStatus();

        assertThat(statusDistribution).hasSize(OrderStatus.values().length);

        OrderStatusCountResponse deliveredStat = statusDistribution.stream()
                .filter(s -> s.status() == OrderStatus.DELIVERED)
                .findFirst()
                .orElseThrow();
        assertThat(deliveredStat.count()).isEqualTo(80L);
        assertThat(deliveredStat.percentage()).isEqualTo(80.00);

        OrderStatusCountResponse placedStat = statusDistribution.stream()
                .filter(s -> s.status() == OrderStatus.PLACED)
                .findFirst()
                .orElseThrow();
        assertThat(placedStat.count()).isEqualTo(10L);
        assertThat(placedStat.percentage()).isEqualTo(10.00);

        OrderStatusCountResponse shippedStat = statusDistribution.stream()
                .filter(s -> s.status() == OrderStatus.SHIPPED)
                .findFirst()
                .orElseThrow();
        assertThat(shippedStat.count()).isEqualTo(0L);
        assertThat(shippedStat.percentage()).isEqualTo(0.00);
    }

    @Test
    @DisplayName("Should generate continuous daily sales trend without gaps for the requested days")
    void shouldGenerateContinuousSalesTrend() {
        LocalDate today = LocalDate.now();
        List<Object[]> queryResults = List.of(
                new Object[]{today, 5L, new BigDecimal("2500.00")},
                new Object[]{today.minusDays(2), 3L, new BigDecimal("1200.00")}
        );

        when(orderRepository.getDailySalesTrend(any())).thenReturn(queryResults);

        List<DailySalesResponse> trend = adminDashboardService.getSalesTrend(7);

        assertThat(trend).hasSize(7);
        assertThat(trend.get(trend.size() - 1).date()).isEqualTo(today);
        assertThat(trend.get(trend.size() - 1).orderCount()).isEqualTo(5L);
        assertThat(trend.get(trend.size() - 1).revenue()).isEqualByComparingTo("2500.00");

        // Day without orders in query results should be filled with 0
        DailySalesResponse yesterdayStat = trend.get(trend.size() - 2);
        assertThat(yesterdayStat.date()).isEqualTo(today.minusDays(1));
        assertThat(yesterdayStat.orderCount()).isEqualTo(0L);
        assertThat(yesterdayStat.revenue()).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("Should retrieve top selling products with proper ranking and mapped DTO values")
    void shouldRetrieveTopSellingProducts() {
        List<Object[]> queryResults = List.of(
                new Object[]{101L, "Smartphone Pro", "PHONE-PRO-128", 50L, new BigDecimal("2500000.00")},
                new Object[]{102L, "Bluetooth Speaker", "AUDIO-SPK-01", 30L, new BigDecimal("90000.00")}
        );

        when(orderItemRepository.findTopSellingProducts(any())).thenReturn(queryResults);

        List<TopSellingProductResponse> topProducts = adminDashboardService.getTopSellingProducts(5);

        assertThat(topProducts).hasSize(2);
        TopSellingProductResponse top1 = topProducts.get(0);
        assertThat(top1.productId()).isEqualTo(101L);
        assertThat(top1.productName()).isEqualTo("Smartphone Pro");
        assertThat(top1.sku()).isEqualTo("PHONE-PRO-128");
        assertThat(top1.totalUnitsSold()).isEqualTo(50L);
        assertThat(top1.totalRevenue()).isEqualByComparingTo("2500000.00");
    }

    @Test
    @DisplayName("Should retrieve paginated low stock product alerts")
    void shouldRetrieveLowStockProducts() {
        Category category = Category.builder().id(1L).name("Electronics").build();
        Product product = Product.builder()
                .id(201L)
                .name("Budget Wireless Earbuds")
                .sku("EARBUDS-LITE")
                .brand("SoundMax")
                .category(category)
                .price(new BigDecimal("999.00"))
                .stockQuantity(2)
                .active(true)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findLowStockProducts(eq(5), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(product), pageable, 1));

        PageResponse<LowStockProductResponse> response = adminDashboardService.getLowStockProducts(5, pageable);

        assertThat(response).isNotNull();
        assertThat(response.totalElements()).isEqualTo(1);
        LowStockProductResponse item = response.content().get(0);
        assertThat(item.id()).isEqualTo(201L);
        assertThat(item.name()).isEqualTo("Budget Wireless Earbuds");
        assertThat(item.sku()).isEqualTo("EARBUDS-LITE");
        assertThat(item.categoryName()).isEqualTo("Electronics");
        assertThat(item.stockQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should retrieve recent customer orders formatted with customer names and items count")
    void shouldRetrieveRecentOrders() {
        List<Object[]> queryResults = new ArrayList<>();
        queryResults.add(new Object[]{
                501L,
                10L,
                "John",
                "Doe",
                "john.doe@example.com",
                new BigDecimal("12499.00"),
                OrderStatus.PLACED,
                3,
                testTimestamp
        });

        when(orderRepository.findRecentOrdersSummary(any())).thenReturn(queryResults);

        List<RecentOrderSummaryResponse> recentOrders = adminDashboardService.getRecentOrders(5);

        assertThat(recentOrders).hasSize(1);
        RecentOrderSummaryResponse order = recentOrders.get(0);
        assertThat(order.id()).isEqualTo(501L);
        assertThat(order.userId()).isEqualTo(10L);
        assertThat(order.customerName()).isEqualTo("John Doe");
        assertThat(order.customerEmail()).isEqualTo("john.doe@example.com");
        assertThat(order.totalAmount()).isEqualByComparingTo("12499.00");
        assertThat(order.status()).isEqualTo(OrderStatus.PLACED);
        assertThat(order.itemCount()).isEqualTo(3);
        assertThat(order.createdAt()).isEqualTo(testTimestamp);
    }

    @Test
    @DisplayName("Should aggregate category sales breakdown including categories with zero sales")
    void shouldAggregateCategorySales() {
        List<Object[]> queryResults = List.of(
                new Object[]{1L, "Electronics", 25L, 120L, new BigDecimal("600000.00")},
                new Object[]{2L, "Books", 10L, 0L, BigDecimal.ZERO}
        );

        when(categoryRepository.getCategorySalesStatistics()).thenReturn(queryResults);

        List<CategorySalesResponse> categorySales = adminDashboardService.getCategorySales();

        assertThat(categorySales).hasSize(2);
        CategorySalesResponse cat1 = categorySales.get(0);
        assertThat(cat1.categoryId()).isEqualTo(1L);
        assertThat(cat1.categoryName()).isEqualTo("Electronics");
        assertThat(cat1.totalProducts()).isEqualTo(25L);
        assertThat(cat1.unitsSold()).isEqualTo(120L);
        assertThat(cat1.totalRevenue()).isEqualByComparingTo("600000.00");

        CategorySalesResponse cat2 = categorySales.get(1);
        assertThat(cat2.categoryId()).isEqualTo(2L);
        assertThat(cat2.unitsSold()).isEqualTo(0L);
        assertThat(cat2.totalRevenue()).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("Should assemble comprehensive consolidated dashboard snapshot")
    void shouldAssembleDashboardStats() {
        when(userRepository.count()).thenReturn(10L);
        when(userRepository.countByRole(Role.CUSTOMER)).thenReturn(9L);
        when(userRepository.countByRole(Role.ADMIN)).thenReturn(1L);
        when(productRepository.count()).thenReturn(5L);
        when(productRepository.countByActiveTrue()).thenReturn(5L);
        when(productRepository.countByStockQuantityLessThanEqualAndActiveTrue(any())).thenReturn(1L);
        when(orderRepository.count()).thenReturn(20L);
        when(orderRepository.calculateTotalRevenue()).thenReturn(new BigDecimal("10000.00"));
        when(orderRepository.countPendingOrders()).thenReturn(2L);
        when(orderRepository.countByStatus(any())).thenReturn(5L);
        when(orderRepository.countByCreatedAtAfter(any())).thenReturn(2L);
        when(orderRepository.calculateRevenueSince(any())).thenReturn(new BigDecimal("1000.00"));

        when(orderRepository.countOrdersByStatusGroup()).thenReturn(List.of());
        when(orderRepository.getDailySalesTrend(any())).thenReturn(List.of());
        when(orderItemRepository.findTopSellingProducts(any())).thenReturn(List.of());
        when(productRepository.findLowStockProducts(any(), any())).thenReturn(new PageImpl<>(List.of()));
        when(orderRepository.findRecentOrdersSummary(any())).thenReturn(List.of());
        when(categoryRepository.getCategorySalesStatistics()).thenReturn(List.of());

        AdminDashboardStatsResponse stats = adminDashboardService.getDashboardStats();

        assertThat(stats).isNotNull();
        assertThat(stats.overview()).isNotNull();
        assertThat(stats.ordersByStatus()).isNotNull();
        assertThat(stats.recentSalesTrend()).isNotNull();
        assertThat(stats.topSellingProducts()).isNotNull();
        assertThat(stats.lowStockProducts()).isNotNull();
        assertThat(stats.recentOrders()).isNotNull();
        assertThat(stats.categorySales()).isNotNull();
    }
}
