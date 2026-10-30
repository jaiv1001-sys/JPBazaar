# JPBazaar

> Production-grade, enterprise-scale modular monolithic e-commerce REST API backend built with **Java 21 LTS**, **Spring Boot 3.3.4**, **PostgreSQL 16**, **Redis 7**, **Spring Security 6 (JWT)**, **Spring Boot Actuator & Prometheus**, and **Docker**.

---

## 🌟 Executive Summary & Key Highlights

**JPBazaar** is designed from the ground up for high concurrency, zero overselling, audit-grade financial transactions, and enterprise security compliance:
- **Zero Overselling / Concurrency Control**: Uses PostgreSQL row-level pessimistic write locks (`SELECT ... FOR UPDATE`), JPA `@Version` optimistic locking, and deterministic lock ordering to mathematically eliminate lost updates, race conditions, and deadlocks during flash sales.
- **Query Performance & Optimization**: Eliminates N+1 queries across the entire domain using `LEFT JOIN FETCH` with explicit count queries for single-valued relationships and `@BatchSize(size = 25)` / `hibernate.default_batch_fetch_size: 25` for collection associations (cutting catalog query counts by ~95%).
- **Multi-Level Redis Caching**: Redis 7 distributed cache with Jackson polymorphic type handling, dynamic TTLs, and automated cache invalidation across products, categories, reviews, and inventory mutations.
- **Enterprise Security Hardening**: Spring Security 6 with stateless JWT authentication, dual-engine token bucket rate limiting (Redis distributed counter + thread-safe in-memory fallback), security response headers (CSP, HSTS, X-Content-Type-Options: nosniff, X-Frame-Options: DENY, Referrer-Policy, Permissions-Policy), hardened CORS, and sensitive data masking in logs.
- **Production Observability**: Spring Boot Actuator with Kubernetes liveness and readiness health probes (`/actuator/health/liveness`, `/actuator/health/readiness`), Micrometer Prometheus metric scraping (`/actuator/prometheus`), and 30-second graceful shutdown.
- **Cloud-Native Deployment**: Multi-stage `Dockerfile` with Spring Boot 3 `layertools`, unprivileged non-root user (`appuser:appgroup`), container-aware JVM flags, and single-command `docker compose` orchestration.
- **100% Test Coverage**: **149 / 149 passing tests** across 25 test classes covering domain unit logic, concurrency stress testing, Redis caching, database query optimization, security filters, and actuator endpoints.

---

## 🚀 Technology Stack

| Layer | Technology | Version | Purpose |
|---|---|---|---|
| **Language** | Java | 21 LTS | Core programming language (Virtual threads ready, records, pattern matching) |
| **Framework** | Spring Boot | 3.3.4 | Web, Data JPA, Security, Validation, Cache, Actuator |
| **Database** | PostgreSQL | 16 | Relational database with ACID transaction guarantees & B-tree indexes |
| **Cache & Queue** | Redis | 7 | Distributed cache, rate limiter counter, and session store |
| **Authentication** | JJWT | 0.12.6 | HMAC-SHA signed stateless JSON Web Tokens |
| **API Documentation**| Springdoc OpenAPI | 2.6.0 | Interactive Swagger UI and OpenAPI 3 schema definitions |
| **Observability** | Micrometer & Prometheus | Latest | Metric scraping, JVM monitoring, and container health probes |
| **Build Tool** | Apache Maven | 3.9+ | Dependency management, multi-stage packaging, Surefire test runner |
| **Containerization** | Docker & Docker Compose | Latest | Multi-stage image builds, layered caching, and multi-service orchestration |

---

## 📁 Package Architecture (`com.jpbazaar`)

```
com.jpbazaar
├── config          # Spring configuration (Security, Redis, OpenAPI, JPA, Actuator)
├── controller      # REST API Controllers (@RestController, DTO validation, HTTP mapping)
│   └── admin       # Administrative controllers (Dashboard analytics, Catalog management)
├── service         # Business logic layer (@Service, Transaction management, Concurrency)
│   ├── impl        # Production service implementations
│   ├── payment     # Pluggable PaymentGateway abstraction & implementations
│   └── storage     # Pluggable ImageStorageService (Local file storage, Cloud ready)
├── repository      # Spring Data JPA repositories with custom JPQL and pessimistic locks
├── entity          # JPA domain entities, @Version audit columns, and ORM mappings
├── dto             # Strongly-typed Java Record DTOs
│   ├── request     # Inbound client request records with Jakarta Bean Validation
│   └── response    # Outbound unified response records
├── mapper          # Entity-to-DTO conversion mappers
├── security        # Spring Security 6 filters, JWT token provider, and UserDetailsService
│   └── ratelimit   # Dual-engine token bucket rate limiter (Redis + in-memory fallback)
├── exception       # Centralized GlobalExceptionHandler (@RestControllerAdvice)
└── util            # Constants, diagnostic MaskingUtils, and helper functions
```

---

## 🌐 Complete API Endpoint Reference

### 🔐 1. Authentication (`/api/auth/**`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/auth/register` | Public | Register new customer account (Auto-initializes cart & wishlist) |
| `POST` | `/api/auth/login` | Public | Authenticate with email/password; returns JWT bearer token |

### 👤 2. User & Address Book (`/api/users/**`, `/api/addresses/**`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/users/profile` | Customer / Admin | Retrieve authenticated user profile |
| `PUT` | `/api/users/profile` | Customer / Admin | Update first name, last name, or phone number |
| `GET` | `/api/addresses` | Customer / Admin | List all saved shipping addresses for authenticated user |
| `POST` | `/api/addresses` | Customer / Admin | Add new shipping address (Auto-manages default address flag) |
| `PUT` | `/api/addresses/{id}` | Customer / Admin | Update existing shipping address (IDOR-protected) |
| `DELETE`| `/api/addresses/{id}` | Customer / Admin | Delete shipping address (IDOR-protected) |

### 📦 3. Products & Categories (`/api/products/**`, `/api/categories/**`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/products` | Public | Filter, search, and paginate active products (Redis cached) |
| `GET` | `/api/products/{id}` | Public | Retrieve detailed product by ID (Includes category & images) |
| `GET` | `/api/categories` | Public | List all active product categories |
| `GET` | `/api/categories/{id}` | Public | Retrieve single category by ID |
| `POST` | `/api/admin/products` | Admin Only | Create new catalog product (Invalidates product cache) |
| `PUT` | `/api/admin/products/{id}` | Admin Only | Update existing product details & inventory |
| `DELETE`| `/api/admin/products/{id}` | Admin Only | Soft-delete product (Deactivates without corrupting historical orders) |
| `POST` | `/api/admin/categories` | Admin Only | Create new category |
| `PUT` | `/api/admin/categories/{id}` | Admin Only | Update category |
| `DELETE`| `/api/admin/categories/{id}` | Admin Only | Delete category |

### 🖼️ 4. Product Images (`/api/images/**`, `/api/admin/products/{id}/images`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/admin/products/{id}/images` | Admin Only | Upload image file (Validates MIME type, size, unique naming) |
| `PUT` | `/api/admin/products/{id}/images/{imgId}/primary` | Admin Only | Set primary catalog display image |
| `DELETE`| `/api/admin/products/{id}/images/{imgId}` | Admin Only | Remove image file and database record |
| `GET` | `/api/images/{filename}` | Public | Stream image file content with caching headers |

### 🛒 5. Shopping Cart (`/api/cart/**`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/cart` | Customer | Retrieve user cart with real-time stock checks and subtotal calculation |
| `POST` | `/api/cart/items` | Customer | Add product to cart (Validates stock and auto-merges duplicates) |
| `PUT` | `/api/cart/items/{itemId}` | Customer | Update cart item quantity |
| `DELETE`| `/api/cart/items/{itemId}` | Customer | Remove specific line item from cart |
| `DELETE`| `/api/cart` | Customer | Clear entire cart |

### 💖 6. Wishlist (`/api/wishlist/**`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/wishlist` | Customer | Retrieve wishlist items with batch-fetched product images |
| `POST` | `/api/wishlist/items` | Customer | Add product to wishlist (Duplicate-safe idempotent operation) |
| `DELETE`| `/api/wishlist/items/{productId}` | Customer | Remove product from wishlist |
| `DELETE`| `/api/wishlist` | Customer | Clear entire wishlist |

### 📋 7. Orders & Checkout (`/api/orders/**`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/orders` | Customer | Atomic checkout from cart (Pessimistic locking, stock deduction, snapshotting) |
| `GET` | `/api/orders` | Customer | List paginated customer order history |
| `GET` | `/api/orders/{id}` | Customer / Admin | Get order details (IDOR-protected: Customer must own order) |
| `PUT` | `/api/orders/{id}/cancel` | Customer / Admin | Cancel order (Restores inventory; triggers payment refund if paid) |
| `GET` | `/api/admin/orders` | Admin Only | List all platform orders with optional status filtering |
| `PUT` | `/api/orders/{id}/status` | Admin Only | Advance order lifecycle status machine (`PLACED` $\rightarrow$ `DELIVERED`) |

### 💳 8. Payments (`/api/payments/**`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/payments/process` | Customer | Execute payment (`CARD`, `UPI`, `NET_BANKING`, `COD`) via Pluggable Gateway |
| `GET` | `/api/payments/orders/{orderId}` | Customer / Admin | View payment status & transaction ID |
| `POST` | `/api/payments/orders/{orderId}/refund` | Admin Only | Execute manual payment refund |

### ⭐ 9. Reviews & Ratings (`/api/reviews/**`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/reviews` | Customer | Submit verified buyer product review (Verified Buyer Guard enforced) |
| `GET` | `/api/reviews/product/{productId}` | Public | List paginated reviews and aggregate rating summary for product |
| `PUT` | `/api/reviews/{id}` | Customer | Edit customer review (Auto-recalculates product average rating) |
| `DELETE`| `/api/reviews/{id}` | Customer / Admin | Delete review (Auto-recalculates product average rating) |

### 📊 10. Admin Analytics Dashboard (`/api/admin/dashboard/**`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/admin/dashboard/overview` | Admin Only | Consolidated executive dashboard metrics |
| `GET` | `/api/admin/dashboard/stats` | Admin Only | High-level platform KPIs (Revenue, Orders, Customers, Low Stock) |
| `GET` | `/api/admin/dashboard/sales-trend` | Admin Only | Chronological daily sales and revenue timeline |
| `GET` | `/api/admin/dashboard/category-sales` | Admin Only | Category revenue and unit sales breakdown |
| `GET` | `/api/admin/dashboard/order-status-distribution` | Admin Only | Order count distribution across lifecycle statuses |
| `GET` | `/api/admin/dashboard/top-selling` | Admin Only | Top 5 best-selling products by quantity and revenue |
| `GET` | `/api/admin/dashboard/low-stock` | Admin Only | Alert list of products below inventory threshold |
| `GET` | `/api/admin/dashboard/recent-orders` | Admin Only | Quick-view of 5 most recent platform orders |

### 🩺 11. Health & Production Observability (`/actuator/**`, `/api/v1/health`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/v1/health` | Public | Lightweight health check endpoint |
| `GET` | `/actuator/health` | Public | Comprehensive health indicator (Database, Redis, Disk space) |
| `GET` | `/actuator/health/liveness` | Public | Kubernetes Liveness probe |
| `GET` | `/actuator/health/readiness` | Public | Kubernetes Readiness probe |
| `GET` | `/actuator/info` | Public | Application build and runtime environment metadata |
| `GET` | `/actuator/metrics` | Admin Only | Real-time JVM memory, GC, HTTP request metrics |
| `GET` | `/actuator/prometheus` | Admin Only | Prometheus format metrics scraper endpoint |

---

## 🛠️ Environment Configuration Reference

Configuration properties in `application.yml` are externalized and bound to environment variables:

| Property | Environment Variable | Default Value | Description |
|---|---|---|---|
| Database Connection URL | `DB_URL` | `jdbc:postgresql://localhost:5432/jpbazaar` | PostgreSQL JDBC connection URL |
| Database Username | `DB_USERNAME` | `postgres` | Database authentication username |
| Database Password | `DB_PASSWORD` | `postgres` | Database authentication password |
| Hibernate DDL Mode | `JPA_DDL_AUTO` | `validate` | DDL validation mode (`validate`, `update`, `none`) |
| Cache Provider Type | `CACHE_TYPE` | `redis` | Caching provider (`redis`, `simple`, `none`) |
| Redis Hostname | `REDIS_HOST` | `localhost` | Redis server hostname |
| Redis Port | `REDIS_PORT` | `6379` | Redis server port |
| Redis Password | `REDIS_PASSWORD` | `""` | Redis authentication password |
| Application Port | `SERVER_PORT` | `8080` | HTTP port on which the web server listens |
| Active Spring Profile | `SPRING_PROFILES_ACTIVE` | `dev` | Active environment profile (`dev`, `test`, `prod`) |
| JWT Secret Key | `JWT_SECRET` | *(Required 256-bit)* | HMAC-SHA secret for cryptographically signing tokens |
| JWT Expiration (ms) | `JWT_EXPIRATION_MS` | `86400000` | Token validity duration in milliseconds (24 hours) |
| Image Upload Directory | `UPLOAD_DIR` | `uploads/images` | Filesystem directory for persisted product images |
| Rate Limiter Enabled | `RATE_LIMIT_ENABLED` | `true` | Globally toggle token bucket rate limiting |
| Rate Limiter Use Redis | `RATE_LIMIT_USE_REDIS` | `false` | Enable distributed Redis rate limiter counter |
| CORS Allowed Origins | `CORS_ALLOWED_ORIGINS` | `http://localhost:3000,...` | Comma-separated list of allowed frontend origins |

---

## 🏃 Running the Application

### Option A: Single-Command Docker Compose (Recommended)
Orchestrates PostgreSQL 16, Redis 7, and the JPBazaar Spring Boot 3 application with healthcheck dependencies:
```bash
docker compose --env-file .env.docker up --build -d
```
Verify container health status:
```bash
docker compose ps
```
Stream unified application logs:
```bash
docker compose logs -f app
```
Stop and remove containers:
```bash
docker compose down
```

### Option B: Local Development with Maven
1. Start PostgreSQL (port 5432) and Redis (port 6379).
2. Execute database schema:
   ```bash
   psql -U postgres -d jpbazaar -f sql/schema.sql
   ```
3. Copy environment template and configure secrets:
   ```bash
   cp .env.example .env
   ```
4. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

### Option C: Running the Full Automated Test Suite
Executes all 149 integration, unit, concurrency, caching, and security tests:
```bash
./mvnw test
```

---

## 📖 API Documentation & Swagger UI

Once the application is running, open the interactive OpenAPI 3 Swagger UI in your browser:
```
http://localhost:8080/swagger-ui.html
```
Raw OpenAPI 3 JSON specification:
```
http://localhost:8080/v3/api-docs
```

---

## 📋 Complete Project Milestone History

- [x] **Phase 0**: Project Planning & System Architecture Blueprint
- [x] **Phase 1**: Database Architecture & PostgreSQL Schema (`sql/schema.sql`)
- [x] **Phase 2**: Spring Boot 3 Foundation & Environment Setup (`com.jpbazaar`)
- [x] **Phase 3**: JPA Entity Layer & Annotations
- [x] **Phase 4**: Spring Data JPA Repositories
- [x] **Phase 5**: Authentication & Authorization (JWT with JJWT 0.12.6)
- [x] **Phase 6**: User Module & Address Book (IDOR Protection)
- [x] **Phase 7**: Product and Category Module (Search, Filter, Pagination)
- [x] **Phase 8**: Product Images (Pluggable Storage Abstraction, Upload, Primary Designation)
- [x] **Phase 9**: Cart Module (Add, Update, Remove, Clear, Merge Duplicates, Stock & Inactive Validation)
- [x] **Phase 10**: Wishlist Module (Add, Remove, Clear, Duplicate Detection, N+1 Fetch Joins)
- [x] **Phase 11**: Order Module (Checkout, Lifecycle State Machine, Historical Price Snapshotting, Stock Management, IDOR Defense)
- [x] **Phase 12**: Payment Module & Transaction Management (ACID Rollback Pipeline, Pluggable PaymentGateway, Card/UPI/NetBanking/COD, Refunds)
- [x] **Phase 13**: Concurrency and Inventory Control (Pessimistic Locking `SELECT ... FOR UPDATE`, Deadlock Prevention, Multi-Threaded Stress Testing)
- [x] **Phase 14**: Verified Buyer Review & Rating System (Verified Buyer Guard, One Review Per Product, Auto Average Recalculation, IDOR Defense)
- [x] **Phase 15**: Global Exception Handling & Input Validation (Standardized `ApiResponse`, Custom Exception Hierarchy, Bean Validation Audit, SLF4J Diagnostic Logging, Zero Stack Trace Leakage)
- [x] **Phase 16**: Caching with Redis & Cache Invalidation (Spring Cache Abstraction, RedisTemplate, Jackson JSON Serialization, Dynamic TTLs, Automated Invalidation across Products, Categories, Images, Reviews, and Inventory Deductions)
- [x] **Phase 17**: OpenAPI / Swagger Documentation (Springdoc OpenAPI 3.0, Interactive Swagger UI, Operation Summaries)
- [x] **Phase 18**: Admin Analytics Dashboard (Consolidated KPIs, Sales Trends, Category Revenue, Status Distribution, Top Selling, Low Stock Alerts)
- [x] **Phase 19**: Performance Optimization (Indexes, Query Tuning, N+1 Elimination via `LEFT JOIN FETCH` with Count Queries & `@BatchSize`)
- [x] **Phase 20**: Security Hardening & Rate Limiting (Dual-Engine Token Bucket, Security Headers CSP/HSTS/Frame-Options, CORS, Masking)
- [x] **Phase 21**: Dockerization & Multi-Container Deployment (Multi-Stage Dockerfile, Layertools Caching, Non-Root User, Docker Compose)
- [x] **Phase 22**: Production Readiness & Documentation (Spring Boot Actuator, Prometheus Metrics, Kubernetes Probes, Graceful Shutdown, Comprehensive Documentation)
