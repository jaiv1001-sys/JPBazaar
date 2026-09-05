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

| Property | Environment Variable | Default Value | Description |
|---|---|---|---|
| Database URL | `DB_URL` | `jdbc:postgresql://localhost:5432/jpbazaar` | PostgreSQL JDBC connection URL |
| Database User | `DB_USERNAME` | `postgres` | Database username |
| Database Password | `DB_PASSWORD` | `postgres` | Database password |
| Redis Host | `REDIS_HOST` | `localhost` | Redis server hostname |
| Redis Port | `REDIS_PORT` | `6379` | Redis server port |
| JWT Secret | `JWT_SECRET` | *(64-char Hex Secret)* | HMAC-SHA secret for signing tokens |
| Active Profile | `SPRING_PROFILES_ACTIVE` | `dev` | Active Spring profile (`dev`, `test`, `prod`) |

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

## 📚 Completed Development Roadmap

- [x] **Phase 0**: Project Planning & System Architecture Blueprint
- [x] **Phase 1**: Database Architecture & PostgreSQL Schema (`sql/schema.sql`)
- [x] **Phase 2**: Spring Boot 3 Foundation & Environment Setup (`com.jpbazaar`)
- [ ] **Phase 3**: JPA Entity Layer & Annotations
- [ ] **Phase 4**: Spring Data JPA Repositories
- [ ] **Phase 5**: Authentication & Authorization (JWT)
- [ ] ... *(Phases 6 - 28)*
