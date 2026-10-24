package com.jpbazaar.concurrency;

import com.jpbazaar.dto.request.CreateOrderRequest;
import com.jpbazaar.entity.*;
import com.jpbazaar.repository.*;
import com.jpbazaar.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class InventoryConcurrencyTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = categoryRepository.save(Category.builder()
                .name("ConcurrencyCategory_" + System.nanoTime())
                .description("Category for concurrency tests")
                .build());
    }

    private User createTestUserWithAddressAndCart(String prefix, Product product, int quantity) {
        String email = prefix + "_" + System.nanoTime() + "@concurrency.com";
        User user = userRepository.save(User.builder()
                .firstName("Concurrent")
                .lastName("Buyer")
                .email(email)
                .password("securePassword123")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build());

        Address address = addressRepository.save(Address.builder()
                .user(user)
                .addressLine("101 Concurrency Way")
                .city("Dallas")
                .state("TX")
                .postalCode("75001")
                .country("USA")
                .isDefault(true)
                .build());

        Cart cart = cartRepository.save(Cart.builder()
                .user(user)
                .build());

        CartItem item = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(quantity)
                .build();
        cart.addItem(item);
        cartItemRepository.save(item);

        return user;
    }

    @Test
    void concurrentPurchase_TwoUsersForSingleStock_ExactlyOneSucceeds() throws Exception {
        // Arrange: Product with stock = 1
        Product product = productRepository.save(Product.builder()
                .name("Rare Collectible Item")
                .description("Only 1 available in stock")
                .price(new BigDecimal("999.00"))
                .stockQuantity(1)
                .sku("RARE-ITEM-" + System.nanoTime())
                .category(testCategory)
                .active(true)
                .build());

        User userA = createTestUserWithAddressAndCart("userA", product, 1);
        Address addressA = addressRepository.findByUserId(userA.getId()).get(0);

        User userB = createTestUserWithAddressAndCart("userB", product, 1);
        Address addressB = addressRepository.findByUserId(userB.getId()).get(0);

        long initialOrderCount = orderRepository.count();

        // Concurrency synchronization primitives
        int numberOfThreads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(numberOfThreads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // Thread 1: User A
        executor.submit(() -> {
            try {
                startSignal.await(); // wait for simultaneous trigger
                orderService.placeOrder(userA.getEmail(), new CreateOrderRequest(addressA.getId(), PaymentMethod.CARD));
                successCount.incrementAndGet();
            } catch (Exception e) {
                failureCount.incrementAndGet();
            } finally {
                doneSignal.countDown();
            }
        });

        // Thread 2: User B
        executor.submit(() -> {
            try {
                startSignal.await(); // wait for simultaneous trigger
                orderService.placeOrder(userB.getEmail(), new CreateOrderRequest(addressB.getId(), PaymentMethod.CARD));
                successCount.incrementAndGet();
            } catch (Exception e) {
                failureCount.incrementAndGet();
            } finally {
                doneSignal.countDown();
            }
        });

        // Act: Fire both requests simultaneously
        startSignal.countDown();
        boolean completed = doneSignal.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // Assert:
        assertThat(completed).isTrue();
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failureCount.get()).isEqualTo(1);

        // Verify remaining stock is exactly 0 (No overselling / no negative inventory)
        Product finalProduct = productRepository.findById(product.getId()).orElseThrow();
        assertThat(finalProduct.getStockQuantity()).isEqualTo(0);

        // Verify exactly 1 order was placed in total
        assertThat(orderRepository.count()).isEqualTo(initialOrderCount + 1);
    }

    @Test
    void flashSale_TenUsersCompeteForThreeItems_ExactlyThreeSucceed() throws Exception {
        // Arrange: Product with stock = 3
        int initialStock = 3;
        Product product = productRepository.save(Product.builder()
                .name("Flash Sale Earbuds")
                .description("Limited supply flash sale")
                .price(new BigDecimal("49.99"))
                .stockQuantity(initialStock)
                .sku("FLASH-EARBUDS-" + System.nanoTime())
                .category(testCategory)
                .active(true)
                .build());

        int totalBuyers = 10;
        List<User> buyers = new ArrayList<>();
        List<Address> addresses = new ArrayList<>();

        for (int i = 0; i < totalBuyers; i++) {
            User buyer = createTestUserWithAddressAndCart("buyer_" + i, product, 1);
            buyers.add(buyer);
            addresses.add(addressRepository.findByUserId(buyer.getId()).get(0));
        }

        long initialOrderCount = orderRepository.count();

        ExecutorService executor = Executors.newFixedThreadPool(totalBuyers);
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(totalBuyers);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < totalBuyers; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    startSignal.await();
                    orderService.placeOrder(
                            buyers.get(index).getEmail(),
                            new CreateOrderRequest(addresses.get(index).getId(), PaymentMethod.UPI)
                    );
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    doneSignal.countDown();
                }
            });
        }

        // Fire all 10 buyers at once
        startSignal.countDown();
        boolean completed = doneSignal.await(15, TimeUnit.SECONDS);
        executor.shutdown();

        // Assert
        assertThat(completed).isTrue();
        assertThat(successCount.get()).isEqualTo(3);
        assertThat(failureCount.get()).isEqualTo(7);

        // Stock drops to 0
        Product finalProduct = productRepository.findById(product.getId()).orElseThrow();
        assertThat(finalProduct.getStockQuantity()).isEqualTo(0);

        // Exactly 3 orders placed
        assertThat(orderRepository.count()).isEqualTo(initialOrderCount + 3);
    }
}
