package com.jpbazaar.repository;

import com.jpbazaar.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AdminDashboardRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category electronicsCategory;
    private Category clothingCategory;
    private Product phone;
    private Product laptop;
    private Product tshirt;
    private User customer;
    private Address address;

    @BeforeEach
    void setUp() {
        customer = entityManager.persist(User.builder()
                .firstName("Test")
                .lastName("Customer")
                .email("customer@jpbazaar.com")
                .password("hashed_password")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build());

        entityManager.persist(User.builder()
                .firstName("Admin")
                .lastName("User")
                .email("admin@jpbazaar.com")
                .password("hashed_password")
                .role(Role.ADMIN)
                .enabled(true)
                .build());

        address = entityManager.persist(Address.builder()
                .user(customer)
                .addressLine("123 Tech Park")
                .city("Bengaluru")
                .state("Karnataka")
                .postalCode("560001")
                .country("India")
                .isDefault(true)
                .build());

        electronicsCategory = entityManager.persist(Category.builder()
                .name("Electronics")
                .description("Gadgets and electronics")
                .build());

        clothingCategory = entityManager.persist(Category.builder()
                .name("Clothing")
                .description("Apparel and fashion")
                .build());

        phone = entityManager.persist(Product.builder()
                .name("Smartphone Neo")
                .sku("PHONE-NEO")
                .brand("NeoBrand")
                .price(new BigDecimal("20000.00"))
                .stockQuantity(15)
                .active(true)
                .category(electronicsCategory)
                .build());

        laptop = entityManager.persist(Product.builder()
                .name("Ultra Laptop")
                .sku("LAPTOP-ULTRA")
                .brand("TechCorp")
                .price(new BigDecimal("60000.00"))
                .stockQuantity(3)
                .active(true)
                .category(electronicsCategory)
                .build());

        tshirt = entityManager.persist(Product.builder()
                .name("Cotton T-Shirt")
                .sku("TSHIRT-COTTON")
                .brand("UrbanStyle")
                .price(new BigDecimal("500.00"))
                .stockQuantity(2)
                .active(true)
                .category(clothingCategory)
                .build());

        // Order 1: DELIVERED (Total = 80,000)
        Order order1 = Order.builder()
                .user(customer)
                .address(address)
                .totalAmount(new BigDecimal("80000.00"))
                .status(OrderStatus.DELIVERED)
                .build();
        order1 = entityManager.persist(order1);

        OrderItem item1 = OrderItem.builder()
                .order(order1)
                .product(phone)
                .productName(phone.getName())
                .price(phone.getPrice())
                .quantity(1)
                .subtotal(new BigDecimal("20000.00"))
                .build();
        entityManager.persist(item1);

        OrderItem item2 = OrderItem.builder()
                .order(order1)
                .product(laptop)
                .productName(laptop.getName())
                .price(laptop.getPrice())
                .quantity(1)
                .subtotal(new BigDecimal("60000.00"))
                .build();
        entityManager.persist(item2);

        // Order 2: PLACED (Total = 20,000)
        Order order2 = Order.builder()
                .user(customer)
                .address(address)
                .totalAmount(new BigDecimal("20000.00"))
                .status(OrderStatus.PLACED)
                .build();
        order2 = entityManager.persist(order2);

        OrderItem item3 = OrderItem.builder()
                .order(order2)
                .product(phone)
                .productName(phone.getName())
                .price(phone.getPrice())
                .quantity(1)
                .subtotal(new BigDecimal("20000.00"))
                .build();
        entityManager.persist(item3);

        // Order 3: CANCELLED (Total = 500) - Should NOT count toward revenue!
        Order order3 = Order.builder()
                .user(customer)
                .address(address)
                .totalAmount(new BigDecimal("500.00"))
                .status(OrderStatus.CANCELLED)
                .build();
        order3 = entityManager.persist(order3);

        OrderItem item4 = OrderItem.builder()
                .order(order3)
                .product(tshirt)
                .productName(tshirt.getName())
                .price(tshirt.getPrice())
                .quantity(1)
                .subtotal(new BigDecimal("500.00"))
                .build();
        entityManager.persist(item4);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("calculateTotalRevenue - Should sum only non-cancelled orders")
    void calculateTotalRevenue_shouldExcludeCancelledOrders() {
        BigDecimal revenue = orderRepository.calculateTotalRevenue();

        // 80,000 + 20,000 = 100,000 (Order 3 of 500 is CANCELLED and excluded)
        assertThat(revenue).isEqualByComparingTo("100000.00");
    }

    @Test
    @DisplayName("countPendingOrders - Should count orders in PLACED/CONFIRMED/PROCESSING")
    void countPendingOrders_shouldCountOnlyPendingOrders() {
        long pending = orderRepository.countPendingOrders();

        // Order 2 is PLACED
        assertThat(pending).isEqualTo(1L);
    }

    @Test
    @DisplayName("countOrdersByStatusGroup - Should aggregate count grouped by status")
    void countOrdersByStatusGroup_shouldGroupProperly() {
        List<Object[]> results = orderRepository.countOrdersByStatusGroup();

        assertThat(results).isNotEmpty();
        long deliveredCount = results.stream()
                .filter(row -> row[0] == OrderStatus.DELIVERED)
                .mapToLong(row -> ((Number) row[1]).longValue())
                .findFirst()
                .orElse(0L);
        assertThat(deliveredCount).isEqualTo(1L);
    }

    @Test
    @DisplayName("findTopSellingProducts - Should order products by units sold in non-cancelled orders")
    void findTopSellingProducts_shouldRankBySoldUnits() {
        List<Object[]> results = orderItemRepository.findTopSellingProducts(PageRequest.of(0, 5));

        assertThat(results).isNotEmpty();
        // Phone was bought in Order 1 and Order 2 -> 2 units sold
        Object[] topRow = results.get(0);
        assertThat(topRow[1]).isEqualTo("Smartphone Neo");
        assertThat(((Number) topRow[3]).longValue()).isEqualTo(2L);
        assertThat((BigDecimal) topRow[4]).isEqualByComparingTo("40000.00");
    }

    @Test
    @DisplayName("findLowStockProducts - Should return active products with stock <= threshold")
    void findLowStockProducts_shouldReturnLowStockItems() {
        Page<Product> lowStock = productRepository.findLowStockProducts(5, PageRequest.of(0, 10));

        // Laptop has stock 3, T-Shirt has stock 2 -> both <= 5
        assertThat(lowStock.getTotalElements()).isEqualTo(2);
        assertThat(lowStock.getContent()).extracting(Product::getSku)
                .containsExactlyInAnyOrder("LAPTOP-ULTRA", "TSHIRT-COTTON");
    }

    @Test
    @DisplayName("getCategorySalesStatistics - Should aggregate sales per category and include all categories")
    void getCategorySalesStatistics_shouldAggregateCategoryBreakdown() {
        List<Object[]> results = categoryRepository.getCategorySalesStatistics();

        assertThat(results).isNotEmpty();
        // Electronics has sales from Phone (2 units, 40,000) + Laptop (1 unit, 60,000) = 100,000
        Object[] electronics = results.stream()
                .filter(row -> "Electronics".equals(row[1]))
                .findFirst()
                .orElseThrow();
        assertThat(((Number) electronics[2]).longValue()).isEqualTo(2L); // 2 products
        assertThat(((Number) electronics[3]).longValue()).isEqualTo(3L); // 3 units sold
        assertThat((BigDecimal) electronics[4]).isEqualByComparingTo("100000.00");

        // Clothing only had a CANCELLED order, so revenue is 0.00
        Object[] clothing = results.stream()
                .filter(row -> "Clothing".equals(row[1]))
                .findFirst()
                .orElseThrow();
        assertThat(((Number) clothing[3]).longValue()).isEqualTo(0L);
        assertThat((BigDecimal) clothing[4]).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("findRecentOrdersSummary - Should return recent orders with customer name and item counts")
    void findRecentOrdersSummary_shouldReturnOrderSummaries() {
        List<Object[]> summaries = orderRepository.findRecentOrdersSummary(PageRequest.of(0, 5));

        assertThat(summaries).hasSize(3);
        // Each row: [id, userId, firstName, lastName, email, totalAmount, status, itemCount, createdAt]
        Object[] row = summaries.get(0);
        assertThat(row[0]).isNotNull();
        assertThat(row[2]).isEqualTo("Test");
        assertThat(row[3]).isEqualTo("Customer");
        assertThat(row[4]).isEqualTo("customer@jpbazaar.com");
    }

    @Test
    @DisplayName("getDailySalesTrend - Should group daily totals correctly")
    void getDailySalesTrend_shouldGroupSales() {
        OffsetDateTime oneWeekAgo = OffsetDateTime.now().minusDays(7);
        List<Object[]> trend = orderRepository.getDailySalesTrend(oneWeekAgo);

        assertThat(trend).isNotEmpty();
        Object[] todayStat = trend.get(0);
        assertThat(todayStat[0]).isNotNull(); // date
        assertThat(((Number) todayStat[1]).longValue()).isGreaterThanOrEqualTo(2L); // order 1 & order 2
        assertThat((BigDecimal) todayStat[2]).isEqualByComparingTo("100000.00");
    }
}
