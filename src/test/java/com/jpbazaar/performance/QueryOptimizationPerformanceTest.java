package com.jpbazaar.performance;

import com.jpbazaar.dto.response.OrderResponse;
import com.jpbazaar.dto.response.PageResponse;
import com.jpbazaar.dto.response.ProductResponse;
import com.jpbazaar.dto.response.WishlistResponse;
import com.jpbazaar.entity.*;
import com.jpbazaar.repository.*;
import com.jpbazaar.service.OrderService;
import com.jpbazaar.service.ProductService;
import com.jpbazaar.service.WishlistService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class QueryOptimizationPerformanceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private EntityManager entityManager;

    private Category electronicsCategory;
    private User testUser;
    private Address testAddress;

    @BeforeEach
    void setUp() {
        electronicsCategory = categoryRepository.save(Category.builder()
                .name("Performance Tech " + System.nanoTime())
                .description("Hardware for high throughput")
                .build());

        testUser = userRepository.save(User.builder()
                .firstName("Perf")
                .lastName("Engineer")
                .email("perf_" + System.nanoTime() + "@jpbazaar.com")
                .password("securePassword123")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build());

        testAddress = addressRepository.save(Address.builder()
                .user(testUser)
                .addressLine("100 Performance Way")
                .city("Dallas")
                .state("TX")
                .postalCode("75001")
                .country("USA")
                .isDefault(true)
                .build());
    }

    @Test
    @Transactional
    @DisplayName("Catalog Search should fetch categories via JOIN FETCH and batch load images without N+1 queries")
    void testProductCatalogQueryOptimization() {
        // Arrange: Create 5 products each with 2 images
        for (int i = 1; i <= 5; i++) {
            Product product = Product.builder()
                    .category(electronicsCategory)
                    .name("Benchmark Server Node " + i)
                    .description("High efficiency server " + i)
                    .price(new BigDecimal("999.99"))
                    .stockQuantity(50)
                    .sku("NODE-SKU-" + i + "-" + System.nanoTime())
                    .brand("EnterpriseBrand")
                    .rating(new BigDecimal("4.80"))
                    .active(true)
                    .build();

            product.addImage(ProductImage.builder().imageUrl("https://cdn.jpbazaar.com/img1_" + i + ".png").isPrimary(true).build());
            product.addImage(ProductImage.builder().imageUrl("https://cdn.jpbazaar.com/img2_" + i + ".png").isPrimary(false).build());

            productRepository.save(product);
        }

        entityManager.flush();
        entityManager.clear();

        // Act: Fetch products via Service layer (executes searchAndFilterProducts with LEFT JOIN FETCH category)
        Pageable pageable = PageRequest.of(0, 10, Sort.by("price").descending());
        PageResponse<ProductResponse> response = productService.getProducts(
                electronicsCategory.getId(),
                "EnterpriseBrand",
                new BigDecimal("500.00"),
                new BigDecimal("2000.00"),
                true,
                "Benchmark",
                pageable
        );

        // Assert: 5 products retrieved with all category details and image URLs correctly resolved
        assertThat(response.content()).hasSize(5);
        for (ProductResponse p : response.content()) {
            assertThat(p.categoryName()).isEqualTo(electronicsCategory.getName());
            assertThat(p.imageUrls()).hasSize(2);
            assertThat(p.brand()).isEqualTo("EnterpriseBrand");
        }
    }

    @Test
    @Transactional
    @DisplayName("Order Listing should fetch Address and Payment via JOIN FETCH and batch load Items")
    void testOrderListingQueryOptimization() {
        // Arrange: Create 3 orders for testUser
        for (int i = 1; i <= 3; i++) {
            Product product = productRepository.save(Product.builder()
                    .category(electronicsCategory)
                    .name("Ordered Product " + i)
                    .price(new BigDecimal("100.00"))
                    .stockQuantity(20)
                    .sku("ORD-PROD-" + i + "-" + System.nanoTime())
                    .active(true)
                    .build());

            Order order = Order.builder()
                    .user(testUser)
                    .address(testAddress)
                    .status(OrderStatus.CONFIRMED)
                    .totalAmount(new BigDecimal("200.00"))
                    .build();

            OrderItem item1 = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .productName(product.getName())
                    .price(product.getPrice())
                    .quantity(2)
                    .subtotal(new BigDecimal("200.00"))
                    .build();
            order.addOrderItem(item1);

            Payment payment = Payment.builder()
                    .order(order)
                    .transactionId("TXN-PERF-" + i + "-" + System.nanoTime())
                    .amount(new BigDecimal("200.00"))
                    .method(PaymentMethod.CARD)
                    .status(PaymentStatus.SUCCESS)
                    .build();
            order.setPayment(payment);

            orderRepository.save(order);
        }

        entityManager.flush();
        entityManager.clear();

        // Act: Fetch user orders via Service layer
        PageResponse<OrderResponse> userOrders = orderService.getUserOrders(
                testUser.getEmail(),
                OrderStatus.CONFIRMED,
                PageRequest.of(0, 10)
        );

        // Assert: Orders returned with eager address and payment mapping, and batch loaded items
        assertThat(userOrders.content()).hasSize(3);
        for (OrderResponse o : userOrders.content()) {
            assertThat(o.shippingAddress()).contains("100 Performance Way");
            assertThat(o.payment()).isNotNull();
            assertThat(o.payment().status()).isEqualTo(PaymentStatus.SUCCESS);
            assertThat(o.items()).hasSize(1);
            assertThat(o.items().get(0).productName()).contains("Ordered Product");
        }
    }

    @Test
    @Transactional
    @DisplayName("Wishlist retrieval should batch load primary images across all wishlist items")
    void testWishlistBatchLoadingOptimization() {
        // Arrange: Create wishlist with 3 items
        Wishlist wishlist = wishlistRepository.save(Wishlist.builder()
                .user(testUser)
                .build());

        for (int i = 1; i <= 3; i++) {
            Product product = Product.builder()
                    .category(electronicsCategory)
                    .name("Wishlist Item " + i)
                    .price(new BigDecimal("49.99"))
                    .stockQuantity(15)
                    .sku("WISH-PROD-" + i + "-" + System.nanoTime())
                    .active(true)
                    .build();

            product.addImage(ProductImage.builder().imageUrl("https://cdn.jpbazaar.com/wishlist_primary_" + i + ".png").isPrimary(true).build());
            Product savedProduct = productRepository.save(product);

            WishlistItem wishlistItem = WishlistItem.builder()
                    .wishlist(wishlist)
                    .product(savedProduct)
                    .build();
            wishlist.addItem(wishlistItem);
        }

        wishlistRepository.save(wishlist);
        entityManager.flush();
        entityManager.clear();

        // Act: Retrieve wishlist via Service layer
        WishlistResponse response = wishlistService.getWishlist(testUser.getEmail());

        // Assert: All 3 items mapped with primary image URLs populated through batch fetching
        assertThat(response.totalItems()).isEqualTo(3);
        assertThat(response.items()).allMatch(item -> item.imageUrl() != null && item.imageUrl().contains("wishlist_primary_"));
    }

    @Test
    @Transactional
    @DisplayName("Low stock and brand index queries should execute cleanly with countQuery pagination")
    void testLowStockQueryOptimization() {
        // Arrange: Create 2 low-stock products (stock <= 5) and 2 normal stock products (stock > 5)
        for (int i = 1; i <= 4; i++) {
            Product product = Product.builder()
                    .category(electronicsCategory)
                    .name("Inventory Node " + i)
                    .price(new BigDecimal("150.00"))
                    .stockQuantity(i <= 2 ? 3 : 25)
                    .sku("LOW-STOCK-SKU-" + i + "-" + System.nanoTime())
                    .brand("StockBrand")
                    .active(true)
                    .build();
            productRepository.save(product);
        }

        entityManager.flush();
        entityManager.clear();

        // Act: Execute findLowStockProducts
        var lowStockPage = productRepository.findLowStockProducts(5, PageRequest.of(0, 10));

        // Assert: Only 2 low stock products returned, with category eagerly loaded
        assertThat(lowStockPage.getTotalElements()).isGreaterThanOrEqualTo(2);
        assertThat(lowStockPage.getContent()).allMatch(p -> p.getStockQuantity() <= 5);
        assertThat(lowStockPage.getContent().get(0).getCategory().getName()).isNotBlank();
    }
}
