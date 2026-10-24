package com.jpbazaar.service;

import com.jpbazaar.dto.request.CreateOrderRequest;
import com.jpbazaar.entity.*;
import com.jpbazaar.exception.InsufficientStockException;
import com.jpbazaar.exception.InvalidRequestException;
import com.jpbazaar.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class OrderTransactionManagementTest {

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

    @Autowired
    private PaymentRepository paymentRepository;

    private User testUser;
    private Address testAddress;
    private Product testProduct;
    private Cart userCart;

    @BeforeEach
    void setUp() {
        String email = "txn_test_" + System.nanoTime() + "@jpbazaar.com";
        testUser = userRepository.save(User.builder()
                .firstName("Transaction")
                .lastName("Tester")
                .email(email)
                .password("securePassword123")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build());

        testAddress = addressRepository.save(Address.builder()
                .user(testUser)
                .addressLine("789 Database Way")
                .city("Austin")
                .state("TX")
                .postalCode("78701")
                .country("USA")
                .isDefault(true)
                .build());

        Category category = categoryRepository.save(Category.builder()
                .name("TxnCategory_" + System.nanoTime())
                .description("Transaction test category")
                .build());

        testProduct = productRepository.save(Product.builder()
                .name("ACID Test Item")
                .description("Demonstrates atomicity")
                .price(new BigDecimal("250.00"))
                .stockQuantity(3)
                .sku("ACID-ITEM-" + System.nanoTime())
                .category(category)
                .active(true)
                .build());

        userCart = cartRepository.save(Cart.builder()
                .user(testUser)
                .build());
    }

    @Test
    void placeOrder_WhenStockInsufficient_ShouldRollbackAllOperationsAtomically() {
        // Arrange: Cart has quantity 5, but product only has stock 3
        CartItem cartItem = CartItem.builder()
                .cart(userCart)
                .product(testProduct)
                .quantity(5)
                .build();
        userCart.addItem(cartItem);
        cartItemRepository.save(cartItem);

        long initialOrderCount = orderRepository.count();
        long initialPaymentCount = paymentRepository.count();

        // Act & Assert: Order placement fails due to insufficient stock
        CreateOrderRequest request = new CreateOrderRequest(testAddress.getId(), PaymentMethod.CARD);
        assertThatThrownBy(() -> orderService.placeOrder(testUser.getEmail(), request))
                .isInstanceOf(InsufficientStockException.class);

        // Verification of Atomicity (Rollback):
        // 1. Stock quantity must remain un-decremented (3)
        Product uncommittedProduct = productRepository.findById(testProduct.getId()).orElseThrow();
        assertThat(uncommittedProduct.getStockQuantity()).isEqualTo(3);

        // 2. Cart items must NOT be cleared (must still contain the line item)
        Cart uncommittedCart = cartRepository.findByUserEmailWithItems(testUser.getEmail()).orElseThrow();
        assertThat(uncommittedCart.getItems()).hasSize(1);

        // 3. No order record must be committed
        assertThat(orderRepository.count()).isEqualTo(initialOrderCount);

        // 4. No payment record must be committed
        assertThat(paymentRepository.count()).isEqualTo(initialPaymentCount);
    }

    @Test
    void placeOrder_WhenAddressNotFound_ShouldRollbackImmediately() {
        CartItem cartItem = CartItem.builder()
                .cart(userCart)
                .product(testProduct)
                .quantity(1)
                .build();
        userCart.addItem(cartItem);
        cartItemRepository.save(cartItem);

        // Non-existent address ID 999999L
        CreateOrderRequest request = new CreateOrderRequest(999999L, PaymentMethod.COD);
        assertThatThrownBy(() -> orderService.placeOrder(testUser.getEmail(), request))
                .isInstanceOf(InvalidRequestException.class);

        // Product stock unchanged
        Product uncommittedProduct = productRepository.findById(testProduct.getId()).orElseThrow();
        assertThat(uncommittedProduct.getStockQuantity()).isEqualTo(3);

        // Cart items preserved
        Cart uncommittedCart = cartRepository.findByUserEmailWithItems(testUser.getEmail()).orElseThrow();
        assertThat(uncommittedCart.getItems()).hasSize(1);
    }
}
