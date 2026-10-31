package com.jpbazaar.config;

import com.jpbazaar.entity.Address;
import com.jpbazaar.entity.Category;
import com.jpbazaar.entity.Product;
import com.jpbazaar.entity.ProductImage;
import com.jpbazaar.entity.Role;
import com.jpbazaar.entity.User;
import com.jpbazaar.repository.AddressRepository;
import com.jpbazaar.repository.CategoryRepository;
import com.jpbazaar.repository.ProductImageRepository;
import com.jpbazaar.repository.ProductRepository;
import com.jpbazaar.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@Profile("!test")
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.jpbazaar.service.BulkImportService bulkImportService;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0 && productRepository.count() > 0) {
            log.info("JPBazaar data already present. Skipping demo catalog seeding.");
            return;
        }

        log.info("Seeding initial demo data for JPBazaar Storefront...");

        // 1. Seed Users
        User admin = userRepository.findByEmail("admin@jpbazaar.com").orElseGet(() ->
                userRepository.save(User.builder()
                        .firstName("Admin")
                        .lastName("Director")
                        .email("admin@jpbazaar.com")
                        .password(passwordEncoder.encode("Admin@123"))
                        .role(Role.ADMIN)
                        .phone("+1-555-0199")
                        .enabled(true)
                        .build())
        );

        User customer = userRepository.findByEmail("customer@jpbazaar.com").orElseGet(() -> {
            User c = userRepository.save(User.builder()
                    .firstName("Sarah")
                    .lastName("Jenkins")
                    .email("customer@jpbazaar.com")
                    .password(passwordEncoder.encode("Customer@123"))
                    .role(Role.CUSTOMER)
                    .phone("+1-555-0144")
                    .enabled(true)
                    .build());

            addressRepository.save(Address.builder()
                    .user(c)
                    .addressLine("742 Evergreen Terrace")
                    .city("Springfield")
                    .state("Oregon")
                    .postalCode("97477")
                    .country("United States")
                    .isDefault(true)
                    .build());

            return c;
        });

        // 2. Seed Categories (Ensure all 6 catalog departments exist)
        Category electronics = categoryRepository.findByName("Electronics").orElseGet(() ->
                categoryRepository.save(Category.builder()
                        .name("Electronics")
                        .description("Cutting-edge laptops, smart devices, and high-performance computing.")
                        .build())
        );

        Category fashion = categoryRepository.findByName("Fashion").orElseGet(() ->
                categoryRepository.save(Category.builder()
                        .name("Fashion")
                        .description("Designer apparel, tailored clothing, luxury footwear, and seasonal collections.")
                        .build())
        );

        Category home = categoryRepository.findByName("Home & Living").orElseGet(() ->
                categoryRepository.save(Category.builder()
                        .name("Home & Living")
                        .description("Smart home appliances, modern ergonomic furnishings, and culinary essentials.")
                        .build())
        );

        Category accessories = categoryRepository.findByName("Accessories").orElseGet(() ->
                categoryRepository.save(Category.builder()
                        .name("Accessories")
                        .description("Premium watches, designer eyewear, audio gear, and lifestyle accessories.")
                        .build())
        );

        Category beauty = categoryRepository.findByName("Beauty").orElseGet(() ->
                categoryRepository.save(Category.builder()
                        .name("Beauty")
                        .description("Dermatological skincare, luxury fragrances, cosmetics, and self-care.")
                        .build())
        );

        Category sports = categoryRepository.findByName("Sports & Fitness").orElseGet(() ->
                categoryRepository.save(Category.builder()
                        .name("Sports & Fitness")
                        .description("Athletic performance gear, gym equipment, sportswear, and outdoor fitness.")
                        .build())
        );

        // 3. Seed Products if empty
        if (productRepository.count() == 0) {
            java.io.InputStream catalogStream = getClass().getResourceAsStream("/data/catalogue_260_products.csv");
            if (catalogStream != null) {
                log.info("Importing comprehensive 260-product catalogue from classpath resource...");
                try {
                    var result = bulkImportService.executeImport(catalogStream, "catalogue_260_products.csv");
                    log.info("260-product catalogue seeded: {} imported, {} failed.",
                            result.successfullyImported(), result.failedRows());
                } catch (Exception ex) {
                    log.error("Failed to seed 260 catalogue from CSV, falling back to basic demo seeds: {}", ex.getMessage());
                    seedDefaultDemoProducts(electronics, fashion, home, accessories);
                }
            } else {
                seedDefaultDemoProducts(electronics, fashion, home, accessories);
            }
        }

        log.info("JPBazaar demo storefront data successfully seeded!");
    }

    private void seedDefaultDemoProducts(Category electronics, Category fashion, Category home, Category accessories) {
        seedProduct("Apple MacBook Pro 16\" M3 Max",
                "Apple M3 Max chip with 16-core CPU, 40-core GPU, 36GB Unified Memory, and 1TB SSD Storage. Liquid Retina XDR display with ProMotion.",
                new BigDecimal("2499.00"), 15, "APPL-MBP16-M3", "Apple", new BigDecimal("4.9"), electronics,
                "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?auto=format&fit=crop&w=800&q=80");

        seedProduct("Sony WH-1000XM5 Noise-Cancelling Headphones",
                "Industry-leading noise cancellation optimized with two processors and 8 microphones. Up to 30-hour battery life with ultra-comfortable lightweight design.",
                new BigDecimal("398.00"), 24, "SONY-WH1000XM5-BLK", "Sony", new BigDecimal("4.8"), accessories,
                "https://images.unsplash.com/photo-1546435770-a3e426bf472b?auto=format&fit=crop&w=800&q=80");

        seedProduct("iPhone 15 Pro Max - Titanium Blue",
                "Forged in titanium with industry-leading A17 Pro chip, custom Action button, and the most versatile 5x optical telephoto camera system.",
                new BigDecimal("1199.00"), 30, "APPL-IP15PM-256", "Apple", new BigDecimal("4.9"), electronics,
                "https://images.unsplash.com/photo-1695048133142-1a20484d2569?auto=format&fit=crop&w=800&q=80");

        seedProduct("Apple Watch Ultra 2 GPS + Cellular",
                "Rugged 49mm titanium case, precision dual-frequency GPS, up to 36 hours of battery life, and the brightest Apple display ever made.",
                new BigDecimal("799.00"), 18, "APPL-WAT-ULT2", "Apple", new BigDecimal("4.8"), accessories,
                "https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?auto=format&fit=crop&w=800&q=80");

        seedProduct("Samsung Odyssey Neo G9 Curved Gaming Monitor",
                "49\" Dual QHD (5120x1440) Mini-LED display with 240Hz refresh rate, 1ms response time, and Quantum HDR2000 for unmatched immersion.",
                new BigDecimal("1399.00"), 8, "SMSG-NEO-G9-49", "Samsung", new BigDecimal("4.7"), electronics,
                "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?auto=format&fit=crop&w=800&q=80");

        seedProduct("Nike Air Max 270 React Sneakers",
                "Iconic lifestyle sneaker combining Nike's biggest heel Air unit with lightweight, durable Nike React foam for super smooth cushioning.",
                new BigDecimal("160.00"), 45, "NIKE-AM270-WHT", "Nike", new BigDecimal("4.6"), fashion,
                "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=800&q=80");

        seedProduct("Levi's Vintage Sherpa Trucker Jacket",
                "Classic denim jacket lined with plush sherpa for warmth and comfort. Features signature metal shank buttons and chest flap pockets.",
                new BigDecimal("128.00"), 22, "LEVI-SHERPA-TRK", "Levi's", new BigDecimal("4.7"), fashion,
                "https://images.unsplash.com/photo-1576995853123-5a10305d93c0?auto=format&fit=crop&w=800&q=80");

        seedProduct("De'Longhi Magnifica S Automatic Espresso Machine",
                "Bean-to-cup espresso and cappuccino machine with manual frother, 13 grind settings, and rapid heating thermoblock technology.",
                new BigDecimal("649.00"), 12, "DLNG-MAGNIFICA-S", "De'Longhi", new BigDecimal("4.8"), home,
                "https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?auto=format&fit=crop&w=800&q=80");

        seedProduct("Dyson V15 Detect Cordless Vacuum Cleaner",
                "Laser reveals invisible microscopic dust on hard floors. Acoustic piezo sensor counts and measures the size of dust particles.",
                new BigDecimal("749.00"), 14, "DYSN-V15-DETECT", "Dyson", new BigDecimal("4.9"), home,
                "https://images.unsplash.com/photo-1558317374-067fb5f30001?auto=format&fit=crop&w=800&q=80");

        seedProduct("Logitech MX Master 3S Wireless Mouse",
                "Quiet clicks with 8000 DPI track-on-glass sensor and MagSpeed electromagnetic scrolling for supreme precision and tactile control.",
                new BigDecimal("99.99"), 50, "LOGI-MXM3S-PLT", "Logitech", new BigDecimal("4.9"), electronics,
                "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?auto=format&fit=crop&w=800&q=80");
    }

    private void seedProduct(String name, String desc, BigDecimal price, int stock, String sku, String brand,
                             BigDecimal rating, Category category, String imageUrl) {
        Product product = productRepository.save(Product.builder()
                .name(name)
                .description(desc)
                .price(price)
                .stockQuantity(stock)
                .sku(sku)
                .brand(brand)
                .rating(rating)
                .active(true)
                .category(category)
                .build());

        productImageRepository.save(ProductImage.builder()
                .product(product)
                .imageUrl(imageUrl)
                .isPrimary(true)
                .build());
    }
}
