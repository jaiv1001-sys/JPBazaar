package com.jpbazaar.service.impl;

import com.jpbazaar.dto.response.*;
import com.jpbazaar.entity.OrderStatus;
import com.jpbazaar.entity.Product;
import com.jpbazaar.entity.Role;
import com.jpbazaar.repository.CategoryRepository;
import com.jpbazaar.repository.OrderItemRepository;
import com.jpbazaar.repository.OrderRepository;
import com.jpbazaar.repository.ProductRepository;
import com.jpbazaar.repository.UserRepository;
import com.jpbazaar.service.AdminDashboardService;
import com.jpbazaar.util.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public AdminDashboardStatsResponse getDashboardStats() {
        log.info("Aggregating consolidated administrative dashboard metrics");
        AdminOverviewResponse overview = getOverview();
        List<OrderStatusCountResponse> ordersByStatus = getOrdersByStatus();
        List<DailySalesResponse> recentSalesTrend = getSalesTrend(AppConstants.DEFAULT_SALES_TREND_DAYS);
        List<TopSellingProductResponse> topSellingProducts = getTopSellingProducts(AppConstants.DEFAULT_DASHBOARD_RECENT_LIMIT);
        PageResponse<LowStockProductResponse> lowStockPage = getLowStockProducts(
                AppConstants.DEFAULT_LOW_STOCK_THRESHOLD,
                PageRequest.of(0, AppConstants.DEFAULT_DASHBOARD_RECENT_LIMIT)
        );
        List<RecentOrderSummaryResponse> recentOrders = getRecentOrders(AppConstants.DEFAULT_DASHBOARD_RECENT_LIMIT);
        List<CategorySalesResponse> categorySales = getCategorySales();

        return new AdminDashboardStatsResponse(
                overview,
                ordersByStatus,
                recentSalesTrend,
                topSellingProducts,
                lowStockPage.content(),
                recentOrders,
                categorySales
        );
    }

    @Override
    public AdminOverviewResponse getOverview() {
        log.debug("Calculating administrative KPI overview metrics");
        long totalUsers = userRepository.count();
        long totalCustomers = userRepository.countByRole(Role.CUSTOMER);
        long totalAdmins = userRepository.countByRole(Role.ADMIN);

        long totalProducts = productRepository.count();
        long totalActiveProducts = productRepository.countByActiveTrue();

        long totalOrders = orderRepository.count();
        BigDecimal totalRevenue = orderRepository.calculateTotalRevenue().setScale(2, RoundingMode.HALF_UP);
        long pendingOrders = orderRepository.countPendingOrders();
        long deliveredOrders = orderRepository.countByStatus(OrderStatus.DELIVERED);
        long cancelledOrders = orderRepository.countByStatus(OrderStatus.CANCELLED);
        long lowStockCount = productRepository.countByStockQuantityLessThanEqualAndActiveTrue(AppConstants.DEFAULT_LOW_STOCK_THRESHOLD);

        OffsetDateTime startOfToday = OffsetDateTime.of(LocalDate.now().atStartOfDay(), OffsetDateTime.now().getOffset());
        long todayOrders = orderRepository.countByCreatedAtAfter(startOfToday);
        BigDecimal todayRevenue = orderRepository.calculateRevenueSince(startOfToday).setScale(2, RoundingMode.HALF_UP);

        return new AdminOverviewResponse(
                totalUsers,
                totalCustomers,
                totalAdmins,
                totalProducts,
                totalActiveProducts,
                totalOrders,
                totalRevenue,
                pendingOrders,
                deliveredOrders,
                cancelledOrders,
                lowStockCount,
                todayOrders,
                todayRevenue
        );
    }

    @Override
    public List<OrderStatusCountResponse> getOrdersByStatus() {
        log.debug("Aggregating order lifecycle distribution by status");
        long totalOrders = orderRepository.count();
        List<Object[]> results = orderRepository.countOrdersByStatusGroup();

        Map<OrderStatus, Long> countsMap = new EnumMap<>(OrderStatus.class);
        for (OrderStatus status : OrderStatus.values()) {
            countsMap.put(status, 0L);
        }

        for (Object[] row : results) {
            if (row[0] instanceof OrderStatus status) {
                long count = toLong(row[1]);
                countsMap.put(status, count);
            } else if (row[0] instanceof String statusStr) {
                try {
                    OrderStatus status = OrderStatus.valueOf(statusStr);
                    long count = toLong(row[1]);
                    countsMap.put(status, count);
                } catch (IllegalArgumentException ignored) {
                }
            }
        }

        List<OrderStatusCountResponse> responseList = new ArrayList<>();
        for (OrderStatus status : OrderStatus.values()) {
            long count = countsMap.get(status);
            double percentage = totalOrders > 0
                    ? BigDecimal.valueOf((double) count * 100.0 / totalOrders)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue()
                    : 0.0;
            responseList.add(new OrderStatusCountResponse(status, count, percentage));
        }

        return responseList;
    }

    @Override
    public List<DailySalesResponse> getSalesTrend(int days) {
        int normalizedDays = Math.max(1, Math.min(days, 365));
        log.debug("Calculating sales trend for past {} days", normalizedDays);

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(normalizedDays - 1);
        OffsetDateTime startDateTime = OffsetDateTime.of(startDate.atStartOfDay(), OffsetDateTime.now().getOffset());

        List<Object[]> results = orderRepository.getDailySalesTrend(startDateTime);

        // Pre-fill continuous timeline map to avoid gaps in chart visualization
        Map<LocalDate, DailySalesResponse> trendMap = new LinkedHashMap<>();
        for (LocalDate d = startDate; !d.isAfter(today); d = d.plusDays(1)) {
            trendMap.put(d, new DailySalesResponse(d, 0L, BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)));
        }

        for (Object[] row : results) {
            LocalDate date = toLocalDate(row[0]);
            if (date != null && trendMap.containsKey(date)) {
                long orderCount = toLong(row[1]);
                BigDecimal revenue = toBigDecimal(row[2]).setScale(2, RoundingMode.HALF_UP);
                trendMap.put(date, new DailySalesResponse(date, orderCount, revenue));
            }
        }

        return new ArrayList<>(trendMap.values());
    }

    @Override
    public List<TopSellingProductResponse> getTopSellingProducts(int limit) {
        int normalizedLimit = Math.max(1, Math.min(limit, 100));
        log.debug("Fetching top {} selling products", normalizedLimit);

        List<Object[]> results = orderItemRepository.findTopSellingProducts(PageRequest.of(0, normalizedLimit));
        List<TopSellingProductResponse> topProducts = new ArrayList<>();

        for (Object[] row : results) {
            Long productId = toLong(row[0]);
            String productName = (String) row[1];
            String sku = (String) row[2];
            long unitsSold = toLong(row[3]);
            BigDecimal revenue = toBigDecimal(row[4]).setScale(2, RoundingMode.HALF_UP);

            topProducts.add(new TopSellingProductResponse(productId, productName, sku, unitsSold, revenue));
        }

        return topProducts;
    }

    @Override
    public PageResponse<LowStockProductResponse> getLowStockProducts(int threshold, Pageable pageable) {
        log.debug("Fetching paginated low stock products with threshold <= {}", threshold);
        Page<Product> page = productRepository.findLowStockProducts(threshold, pageable);

        Page<LowStockProductResponse> responsePage = page.map(p -> new LowStockProductResponse(
                p.getId(),
                p.getName(),
                p.getSku(),
                p.getBrand(),
                p.getCategory() != null ? p.getCategory().getName() : null,
                p.getPrice(),
                p.getStockQuantity()
        ));

        return PageResponse.from(responsePage);
    }

    @Override
    public List<RecentOrderSummaryResponse> getRecentOrders(int limit) {
        int normalizedLimit = Math.max(1, Math.min(limit, 100));
        log.debug("Fetching {} most recent orders", normalizedLimit);

        List<Object[]> results = orderRepository.findRecentOrdersSummary(PageRequest.of(0, normalizedLimit));
        List<RecentOrderSummaryResponse> recentOrders = new ArrayList<>();

        for (Object[] row : results) {
            Long orderId = toLong(row[0]);
            Long userId = toLong(row[1]);
            String firstName = (String) row[2];
            String lastName = (String) row[3];
            String customerName = ((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "")).trim();
            String customerEmail = (String) row[4];
            BigDecimal totalAmount = toBigDecimal(row[5]).setScale(2, RoundingMode.HALF_UP);
            OrderStatus status = (OrderStatus) row[6];
            int itemCount = (int) toLong(row[7]);
            OffsetDateTime createdAt = (OffsetDateTime) row[8];

            recentOrders.add(new RecentOrderSummaryResponse(
                    orderId,
                    userId,
                    customerName,
                    customerEmail,
                    totalAmount,
                    status,
                    itemCount,
                    createdAt
            ));
        }

        return recentOrders;
    }

    @Override
    public List<CategorySalesResponse> getCategorySales() {
        log.debug("Aggregating sales and product distribution by category");
        List<Object[]> results = categoryRepository.getCategorySalesStatistics();
        List<CategorySalesResponse> categoryStats = new ArrayList<>();

        for (Object[] row : results) {
            Long categoryId = toLong(row[0]);
            String categoryName = (String) row[1];
            long totalProducts = toLong(row[2]);
            long unitsSold = toLong(row[3]);
            BigDecimal totalRevenue = toBigDecimal(row[4]).setScale(2, RoundingMode.HALF_UP);

            categoryStats.add(new CategorySalesResponse(
                    categoryId,
                    categoryName,
                    totalProducts,
                    unitsSold,
                    totalRevenue
            ));
        }

        return categoryStats;
    }

    private long toLong(Object value) {
        if (value instanceof Number n) {
            return n.longValue();
        }
        return 0L;
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value instanceof BigDecimal bd) {
            return bd;
        }
        if (value instanceof Number n) {
            return BigDecimal.valueOf(n.doubleValue());
        }
        return BigDecimal.ZERO;
    }

    private LocalDate toLocalDate(Object value) {
        if (value instanceof LocalDate ld) {
            return ld;
        }
        if (value instanceof java.sql.Date d) {
            return d.toLocalDate();
        }
        if (value instanceof java.util.Date d) {
            return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return null;
    }
}
