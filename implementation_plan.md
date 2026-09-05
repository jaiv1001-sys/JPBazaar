# Implementation Plan — Phase 0: Project Planning

## Project: JPBazaar (Java 21 / Spring Boot 3 / PostgreSQL / Redis)

### Maven & Package Identification
* **Project Name**: JPBazaar
* **Group ID**: `com.jpbazaar`
* **Artifact ID**: `jpbazaar`
* **Base Package**: `com.jpbazaar`

---

### 1. Architectural Blueprint & System Overview

The system is designed as a **Modular Monolith** built with Java 21 and Spring Boot 3.x, optimized for high readability, strict domain separation, and performance.

#### High-Level System Architecture
```
                         React 18 + TypeScript Frontend
                                      │
                                      ▼ (HTTPS / JSON REST)
                   ┌──────────────────────────────────────┐
                   │        Spring Security Filter        │
                   │        (JWT Auth & CORS Guard)       │
                   └──────────────────┬───────────────────┘
                                      │
                                      ▼
                   ┌──────────────────────────────────────┐
                   │           Controller Layer           │
                   │        (REST APIs, DTO Validation)   │
                   └──────────────────┬───────────────────┘
                                      │
                                      ▼
                   ┌──────────────────────────────────────┐
                   │            Service Layer             │
                   │    (Business Rules, Transactions,    │
                   │    Concurrency Locks, Security Checks│
                   └──────────┬────────────────┬──────────┘
                              │                │
            ┌─────────────────┴─┐            ┌─┴────────────────┐
            ▼                   ▼            ▼                  ▼
     ┌─────────────┐     ┌────────────┐┌───────────┐   ┌─────────────────┐
     │ JPA Repos   │     │ Redis      ││ Payment   │   │ Image Storage   │
     │ (Spring Data│     │ Cache      ││ Simulator │   │ Adapter (S3/    │
     └──────┬──────┘     └────────────┘└───────────┘   │ Cloudinary Ready│
            │                                          └─────────────────┘
            ▼
     ┌─────────────┐
     │ PostgreSQL  │
     │ Database    │
     └─────────────┘
```

#### Layer Responsibilities
1. **Controller Layer (`com.jpbazaar.controller`)**: Accepts HTTP requests, handles DTO binding and Bean Validation (`@Valid`), delegates to Services, and returns HTTP response wrappers (`ResponseEntity<ApiResponse<T>>`).
2. **Service Layer (`com.jpbazaar.service`)**: Encapsulates core business rules, transactional boundaries (`@Transactional`), security authorization logic (IDOR prevention), and inventory concurrency controls.
3. **Repository Layer (`com.jpbazaar.repository`)**: Spring Data JPA repositories with custom JPQL queries, pagination (`Pageable`), and index-optimized lookups.
4. **Data & Cache Layer**: PostgreSQL 16 for ACID-compliant persistence; Redis for read-through caching of high-frequency product catalog queries and cache invalidation upon updates.

---

### 2. Feature & Module Breakdown

#### Feature Categories
* **Customer Features**:
  * User Registration & JWT Login
  * User Profile & Address Book (Multiple addresses with a single Default flag)
  * Product Catalog: Search, Multi-parameter Filter (Category, Price range, Brand, Rating), Sorting & Pagination
  * Shopping Cart: Quantity updates, Stock checks, Single-item/Bulk management, Duplicate auto-merge
  * Wishlist: Save for later items, move-to-cart operations
  * Checkout & Order Placement: Atomic transaction processing, address snapshotting, inventory reservation, order history
  * Order Tracking & Lifecycle Management: PLACED → CONFIRMED → PROCESSING → SHIPPED → OUT_FOR_DELIVERY → DELIVERED (or CANCELLED)
  * Product Reviews: Verified-buyer rating (1-5 stars) and review comment
  * Simulated Payment Gateways: Credit Card, UPI, Net Banking, COD options with transaction logs
* **Admin Features**:
  * Product CRUD & Inventory Stock Maintenance
  * Category CRUD & Tree management
  * Global Order Management & Status Transitions
  * User Access Controls & Role Management
  * Sales & Revenue Analytics Dashboard APIs

---

### 3. Database Entity List & Relationships

#### Schema Entity Summary
1. **`User`**: `id`, `first_name`, `last_name`, `email` (UNIQUE), `password` (BCrypt), `phone`, `role` (`CUSTOMER`, `ADMIN`), `enabled`, `created_at`, `updated_at`.
2. **`Address`**: `id`, `user_id` (FK), `address_line`, `city`, `state`, `postal_code`, `country`, `is_default`, `created_at`.
3. **`Category`**: `id`, `name` (UNIQUE), `description`, `created_at`.
4. **`Product`**: `id`, `category_id` (FK), `name`, `description`, `price` (`BigDecimal`), `stock_quantity`, `sku` (UNIQUE), `brand`, `rating` (`Double`), `active` (`Boolean`), `version` (`Long` for Optimistic Locking), `created_at`, `updated_at`.
5. **`ProductImage`**: `id`, `product_id` (FK), `image_url`, `is_primary` (`Boolean`).
6. **`Cart`**: `id`, `user_id` (FK, UNIQUE), `created_at`, `updated_at`.
7. **`CartItem`**: `id`, `cart_id` (FK), `product_id` (FK), `quantity` (`Integer`). Unique index on `(cart_id, product_id)`.
8. **`Wishlist`**: `id`, `user_id` (FK, UNIQUE), `created_at`.
9. **`WishlistItem`**: `id`, `wishlist_id` (FK), `product_id` (FK), `created_at`. Unique index on `(wishlist_id, product_id)`.
10. **`Order`**: `id`, `user_id` (FK), `address_id` (FK), `total_amount` (`BigDecimal`), `status` (`OrderStatus` ENUM), `created_at`, `updated_at`.
11. **`OrderItem`**: `id`, `order_id` (FK), `product_id` (FK), `product_name` (Snapshot), `price` (Snapshot `BigDecimal`), `quantity`, `subtotal` (`BigDecimal`).
12. **`Payment`**: `id`, `order_id` (FK, UNIQUE), `transaction_id` (UNIQUE), `amount`, `method` (`PaymentMethod` ENUM), `status` (`PaymentStatus` ENUM), `created_at`.
13. **`Review`**: `id`, `user_id` (FK), `product_id` (FK), `rating` (`Integer`), `comment`, `created_at`, `updated_at`. Unique index on `(user_id, product_id)`.

#### Relationship Map & Constraints
```
[User] 1 ──── N [Address]
[User] 1 ──── 1 [Cart] 1 ──── N [CartItem] N ──── 1 [Product]
[User] 1 ──── 1 [Wishlist] 1 ──── N [WishlistItem] N ──── 1 [Product]
[User] 1 ──── N [Order] 1 ──── N [OrderItem] N ──── 1 [Product]
[Order] 1 ──── 1 [Payment]
[Category] 1 ──── N [Product] 1 ──── N [ProductImage]
[User] 1 ──── N [Review] N ──── 1 [Product]
```

---

### 4. Package Structure

```
com.jpbazaar
├── config
│   ├── SecurityConfig.java
│   ├── RedisConfig.java
│   ├── JPAConfig.java
│   └── OpenAPIConfig.java
├── controller
│   ├── AuthController.java
│   ├── UserController.java
│   ├── AddressController.java
│   ├── CategoryController.java
│   ├── ProductController.java
│   ├── CartController.java
│   ├── WishlistController.java
│   ├── OrderController.java
│   ├── PaymentController.java
│   ├── ReviewController.java
│   └── AdminDashboardController.java
├── service
│   ├── AuthService.java
│   ├── UserService.java
│   ├── AddressService.java
│   ├── CategoryService.java
│   ├── ProductService.java
│   ├── CartService.java
│   ├── WishlistService.java
│   ├── OrderService.java
│   ├── InventoryService.java
│   ├── PaymentService.java
│   ├── ReviewService.java
│   └── AdminService.java
├── repository
│   ├── UserRepository.java
│   ├── AddressRepository.java
│   ├── CategoryRepository.java
│   ├── ProductRepository.java
│   ├── CartRepository.java
│   ├── CartItemRepository.java
│   ├── WishlistRepository.java
│   ├── WishlistItemRepository.java
│   ├── OrderRepository.java
│   ├── OrderItemRepository.java
│   ├── PaymentRepository.java
│   └── ReviewRepository.java
├── entity
│   ├── User.java
│   ├── Address.java
│   ├── Category.java
│   ├── Product.java
│   ├── ProductImage.java
│   ├── Cart.java
│   ├── CartItem.java
│   ├── Wishlist.java
│   ├── WishlistItem.java
│   ├── Order.java
│   ├── OrderItem.java
│   ├── Payment.java
│   └── Review.java
├── dto
│   ├── request
│   │   ├── RegisterRequest.java
│   │   ├── LoginRequest.java
│   │   ├── AddressRequest.java
│   │   ├── CategoryRequest.java
│   │   ├── ProductRequest.java
│   │   ├── CartItemRequest.java
│   │   ├── OrderCreateRequest.java
│   │   ├── PaymentProcessRequest.java
│   │   └── ReviewRequest.java
│   └── response
│       ├── AuthResponse.java
│       ├── UserResponse.java
│       ├── AddressResponse.java
│       ├── CategoryResponse.java
│       ├── ProductResponse.java
│       ├── CartResponse.java
│       ├── WishlistResponse.java
│       ├── OrderResponse.java
│       ├── PaymentResponse.java
│       ├── ReviewResponse.java
│       ├── AdminStatsResponse.java
│       └── ApiResponse.java
├── mapper
│   ├── UserMapper.java
│   ├── ProductMapper.java
│   ├── CartMapper.java
│   └── OrderMapper.java
├── security
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   ├── CustomUserDetailsService.java
│   └── SecurityUser.java
├── exception
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   ├── InsufficientStockException.java
│   ├── UnauthorizedException.java
│   ├── DuplicateResourceException.java
│   ├── InvalidOrderStateException.java
│   └── PaymentFailedException.java
└── util
    └── AppConstants.java
```

---

### 5. API Module Roadmap

| Module | Method | Endpoint | Access | Purpose |
|---|---|---|---|---|
| **Auth** | POST | `/api/auth/register` | Public | Register new customer account |
| **Auth** | POST | `/api/auth/login` | Public | Authenticate user & issue JWT |
| **User** | GET | `/api/users/me` | Authenticated | Get current profile |
| **User** | PUT | `/api/users/me` | Authenticated | Update profile |
| **Address** | POST | `/api/addresses` | Authenticated | Add address |
| **Address** | GET | `/api/addresses` | Authenticated | Get user addresses |
| **Product** | GET | `/api/products` | Public | List products (paged, filtered, sorted) |
| **Product** | GET | `/api/products/{id}` | Public | Get product details by ID |
| **Product** | POST | `/api/products` | ADMIN | Create new product |
| **Product** | PUT | `/api/products/{id}` | ADMIN | Update product details/stock |
| **Cart** | GET | `/api/cart` | Customer | Fetch current active cart |
| **Cart** | POST | `/api/cart/items` | Customer | Add product to cart |
| **Cart** | PUT | `/api/cart/items/{itemId}` | Customer | Update item quantity |
| **Cart** | DELETE | `/api/cart/items/{itemId}` | Customer | Remove item from cart |
| **Order** | POST | `/api/orders` | Customer | Place order from cart |
| **Order** | GET | `/api/orders/{id}` | Customer / Admin | Get order details |
| **Order** | PUT | `/api/orders/{id}/status` | ADMIN | Advance order lifecycle status |
| **Payment**| POST | `/api/payments/process` | Customer | Execute simulated payment |
| **Review** | POST | `/api/reviews` | Verified Buyer| Leave product review |
| **Admin**  | GET | `/api/admin/dashboard/stats` | ADMIN | Fetch revenue & system stats |

---

### 6. Security Architecture & Threat Mitigation

1. **Stateless JWT Security**:
   * Token generated upon login signed with Secret Key.
   * `JwtAuthenticationFilter` intercepts requests, validates signature, extracts claims/authorities, and populates `SecurityContextHolder`.
2. **Role-Based Access Control (RBAC)**:
   * Endpoints secured via `@PreAuthorize("hasRole('ADMIN')")` or Spring Security Filter Chain URL matchers (`/api/admin/**` restricted to `ADMIN`).
3. **IDOR (Insecure Direct Object Reference) Protection**:
   * Customer endpoints (e.g. `/api/orders/{id}`) cross-verify `Order.userId == SecurityContextHolder.getPrincipal().getId()`. If mismatched, throw `UnauthorizedAccessException` (HTTP 403).
4. **Data Hashing & Input Sanitation**:
   * BCrypt (strength 12) for user passwords.
   * Bean Validation (`@Valid`, `@Pattern`, `@Size`) prevents injection attack vectors.

---

### 7. Technology Justification & Interview Value

* **Java 21**: Introduces Records for DTOs, Pattern Matching, and virtual threads readiness; showcases adoption of modern LTS standard.
* **Spring Boot 3.x**: De facto industry standard for Java enterprise microservices/monoliths with spring-boot-starter-web, JPA, Security.
* **PostgreSQL 16**: Relational integrity, check constraints, composite indexing, strict transactions.
* **Redis**: Cache read-intensive catalog data (`GET /api/products`), preventing DB overload and demonstrating real-world caching patterns (Cache Hit/Miss/Eviction).
* **Docker Compose**: Containerizes PostgreSQL, Redis, and Spring Boot app for seamless environment reproducibility.

---

### 8. Key Architectural & Concurrency Decisions

#### 1. Inventory Concurrency Control
* **Problem**: Two users buying the last stock item simultaneously leads to negative inventory (lost update).
* **Solution**: Combine `@Version` Optimistic Locking on `Product` entity with retry mechanisms for high throughput, OR Pessimistic Lock (`SELECT ... FOR UPDATE`) during the stock-checking order placement transaction.

#### 2. Order Historical Snapshotting
* **Problem**: If a product price drops from $100 to $50 next week, historical order total values change.
* **Solution**: `OrderItem` stores `productName` and `price` as explicit value fields captured at checkout time, completely decoupled from future `Product` entity modifications.

#### 3. Database Indexing Strategy
* Indexes added on high-cardinality search/filter columns: `Product(category_id, active, price)`, `Product(sku)`, `Order(user_id)`, `Review(product_id)`.

---

### 9. Step-by-Step Phase Roadmap (Phase 0 to Phase 28)

* **Phase 0**: Project Planning & System Design *(Current)*
* **Phase 1**: PostgreSQL Schema Design & ER Modeling
* **Phase 2**: Spring Boot Maven Project Initialization & Config Setup
* **Phase 3**: JPA Entity Layer & Annotations
* **Phase 4**: Spring Data JPA Repositories & JPQL Custom Queries
* **Phase 5**: Authentication & Authorization (Spring Security + JWT)
* **Phase 6**: User & Address Management Module
* **Phase 7**: Category & Product Catalog Module (Filter, Search, Paging)
* **Phase 8**: Product Image Management Strategy
* **Phase 9**: Shopping Cart Module
* **Phase 10**: Wishlist Module
* **Phase 11**: Order Management & Lifecycle Module
* **Phase 12**: Spring `@Transactional` Management & Rollback Rules
* **Phase 13**: Inventory Concurrency Control (Optimistic / Pessimistic Locking)
* **Phase 14**: Payment Simulation Engine
* **Phase 15**: Verified Buyer Review & Rating System
* **Phase 16**: Global Exception Handling (`@RestControllerAdvice`)
* **Phase 17**: Request DTO Validation (`jakarta.validation`)
* **Phase 18**: DTO Mapping & Layer Isolation
* **Phase 19**: Redis Caching Layer Integration & Eviction Rules
* **Phase 20**: OpenAPI / Swagger API Documentation Setup
* **Phase 21**: Unit & Integration Testing Strategy (JUnit 5, Mockito)
* **Phase 22**: Structured Logging & Operational Audit Logs
* **Phase 23**: Dockerization & Multi-Container Docker Compose
* **Phase 24**: Frontend API Contract Specification
* **Phase 25**: Admin Analytics Dashboard APIs
* **Phase 26**: Performance Optimization (N+1 Query Resolution, Indexes)
* **Phase 27**: Backend Security Audit & OWASP Top 10 Check
* **Phase 28**: End-to-End System Integration & User Journey Verification

---

### 10. Initial README Structure Outline

The `README.md` will contain:
1. System Architecture & Tech Stack Matrix
2. Database ER Diagram & Domain Schema
3. Prerequisites & Local Environment Setup (Docker Compose commands)
4. Application Configuration & Environment Variables
5. Key Design Patterns & Concurrency Mechanics (With Interview FAQs)
6. OpenAPI/Swagger Documentation Links & API Curl Snippets
7. Verification & Test Execution Guide

---

## Verification & Next Steps

### Verification Plan
- Phase 0 output reviewed against all requirements in `IDEA.md` and user branding updates.
- Architecture and domain model approved before creating database scripts or Java files.

### Action Items
- Wait for user approval on **Phase 0 — Project Planning** for project **JPBazaar**.
- Upon approval, proceed to **Phase 1 — Database Architecture**.
