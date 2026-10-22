# JPBazaar

> Production-grade, modular monolithic e-commerce backend built with **Java 21**, **Spring Boot 3**, **PostgreSQL**, **Redis**, **Spring Security (JWT)**, and **Docker**.

---

## 🚀 Technology Stack

| Layer | Technology |
|---|---|
| **Language** | Java 21 LTS |
| **Framework** | Spring Boot 3.3.4 (Spring Web, Spring Data JPA, Spring Security) |
| **Database** | PostgreSQL 16 (Relational DB with ACID guarantees) |
| **Cache** | Redis 7 (In-memory caching & eviction) |
| **Authentication** | JWT (JSON Web Token) with JJWT 0.12.6 |
| **API Docs** | OpenAPI 3 / Swagger UI (Springdoc 2.6.0) |
| **Build Tool** | Maven 3.9+ |
| **Containerization** | Docker & Docker Compose |

---

## 📁 Package Architecture (`com.jpbazaar`)

```
com.jpbazaar
├── config       # Spring configuration (Security, Redis, OpenAPI, JPA)
├── controller   # REST API Controllers (@RestController, DTO binding, Validation)
├── service      # Service layer (Business logic, Transactions, Concurrency control)
├── repository   # Spring Data JPA repositories & custom JPQL queries
├── entity       # JPA domain entities & ORM mappings
├── dto          # Request and Response DTO records
├── mapper       # Entity-DTO mapping utilities
├── security     # Security filters, JWT provider, UserDetailsService
├── exception    # Global exception handler (@RestControllerAdvice)
└── util         # Application constants and utility helpers
```

---

## 🛠️ Environment Configuration & Properties

Externalized properties in `application.yml` bound to environment variables:

| Property | Environment Variable | Config Placeholder | Description |
|---|---|---|---|
| Database URL | `DB_URL` | `${DB_URL}` | PostgreSQL JDBC connection URL |
| Database User | `DB_USERNAME` | `${DB_USERNAME}` | Database username |
| Database Password | `DB_PASSWORD` | `${DB_PASSWORD}` | Database password |
| Redis Host | `REDIS_HOST` | `${REDIS_HOST}` | Redis server hostname |
| Redis Port | `REDIS_PORT` | `${REDIS_PORT}` | Redis server port |
| JWT Secret | `JWT_SECRET` | `${JWT_SECRET}` | HMAC-SHA secret for signing tokens |
| Active Profile | `SPRING_PROFILES_ACTIVE` | `${SPRING_PROFILES_ACTIVE:dev}` | Active Spring profile (`dev`, `test`, `prod`) |

*Local development environment template provided in `.env.example`. Secret files (`.env`) are excluded from Git via `.gitignore`.*

---

## 🏃 Running the Application

### 1. Build and Test
```bash
mvn clean test
```

### 2. Run Locally with Maven
```bash
mvn spring-boot:run
```

### 3. API Documentation
Once running, open Swagger UI in your browser:
```
http://localhost:8080/swagger-ui.html
```

---

- [x] **Phase 0**: Project Planning & System Architecture Blueprint
- [x] **Phase 1**: Database Architecture & PostgreSQL Schema (`sql/schema.sql`)
- [x] **Phase 2**: Spring Boot 3 Foundation & Environment Setup (`com.jpbazaar`)
- [x] **Phase 3**: JPA Entity Layer & Annotations
- [x] **Phase 4**: Spring Data JPA Repositories
- [x] **Phase 5**: Authentication & Authorization (JWT)
- [x] **Phase 6**: User Module & Address Book (IDOR Protection)
- [x] **Phase 7**: Product and Category Module (Search, Filter, Pagination)
- [x] **Phase 8**: Product Images (Pluggable Storage Abstraction, Upload, Primary Designation)
- [x] **Phase 9**: Cart Module (Add, Update, Remove, Clear, Merge Duplicates, Stock & Inactive Validation)
- [x] **Phase 10**: Wishlist Module (Add, Remove, Clear, Duplicate Detection, N+1 Fetch Joins)
- [ ] **Phase 11**: Order Module
- [ ] **Phase 12**: Payment Module
- [ ] **Phase 13**: Product Review Module
- [ ] **Phase 14**: Admin Dashboard & Analytics Module
- [ ] **Phase 15**: Inventory Management Module
- [ ] **Phase 16**: Caching with Redis
- [ ] **Phase 17**: Global Exception Handling & Input Validation
- [ ] **Phase 18**: OpenAPI / Swagger Documentation
- [ ] **Phase 19**: Security Hardening & Rate Limiting
- [ ] **Phase 20**: Dockerization & Deployment
- [ ] **Phase 21**: Comprehensive Testing Suite
- [ ] **Phase 22**: Production Readiness & Documentation
