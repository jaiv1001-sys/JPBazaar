# PROJECT: Placement-Level E-Commerce Platform

You are the lead software architect and senior Java/Spring Boot developer.

Build a complete, production-style e-commerce platform suitable for a **BTech student portfolio, GitHub showcase, internships, and Java/Spring Boot placement interviews**.

The project must be built **PHASE BY PHASE**.

Do NOT generate the entire project blindly in one step.

The code must be real, runnable, maintainable, testable, and connected end-to-end.

The developer will use AI as a coding assistant but must understand the architecture, design decisions, business logic, database, APIs, security, concurrency, and implementation.

---

# 1. CORE OBJECTIVE

Build a full-stack e-commerce application with:

### Backend

* Java 21
* Spring Boot
* Spring Web
* Spring Data JPA
* Hibernate
* Spring Security
* JWT authentication
* PostgreSQL
* Redis
* Maven
* Bean Validation
* JUnit 5
* Mockito
* Integration testing
* OpenAPI / Swagger
* Docker

### Frontend

The frontend will be generated separately with AI.

Use:

* React
* TypeScript
* Tailwind CSS
* Responsive design

The backend must expose clean REST APIs that can easily connect to the frontend.

---

# 2. IMPORTANT DEVELOPMENT RULE

Build the project in phases.

NEVER skip phases.

NEVER generate fake placeholder implementations simply to make compilation pass.

NEVER use hardcoded data where a real database implementation is required.

NEVER create unnecessary complexity just to make the project look advanced.

Use professional architecture while keeping the code understandable to a student.

After completing each phase:

1. Explain what was implemented.
2. Explain WHY each major technology/design decision was used.
3. Explain HOW the implementation works.
4. Explain important classes and methods.
5. Explain database relationships.
6. Explain possible interview questions.
7. Explain how to test the phase.
8. Run/build/test the project if tools are available.
9. Fix errors before proceeding.
10. Wait for approval before moving to the next major phase.

---

# 3. DEVELOPMENT PHASES

Follow exactly this roadmap.

## PHASE 0 — Project Planning

Before writing implementation code:

Create:

* Complete system architecture
* Feature list
* Module list
* Database entity list
* Entity relationships
* API module list
* Security architecture
* Package structure
* Development roadmap
* Technology justification
* README initial structure

Do not implement the complete application yet.

---

# PHASE 1 — Database Architecture

Design the PostgreSQL database.

Entities:

### User

* id
* firstName
* lastName
* email
* password
* phone
* role
* enabled
* createdAt
* updatedAt

### Address

* id
* userId
* addressLine
* city
* state
* postalCode
* country
* isDefault
* createdAt

### Category

* id
* name
* description
* createdAt

### Product

* id
* categoryId
* name
* description
* price
* stockQuantity
* sku
* brand
* rating
* active
* createdAt
* updatedAt

### ProductImage

* id
* productId
* imageUrl
* isPrimary

### Cart

* id
* userId
* createdAt
* updatedAt

### CartItem

* id
* cartId
* productId
* quantity

### Wishlist

* id
* userId
* createdAt

### WishlistItem

* id
* wishlistId
* productId
* createdAt

### Order

* id
* userId
* addressId
* totalAmount
* status
* createdAt
* updatedAt

### OrderItem

* id
* orderId
* productId
* productName
* price
* quantity
* subtotal

Important:
Store productName and price in OrderItem as a historical snapshot so old orders do not change when the product changes later.

### Payment

* id
* orderId
* transactionId
* amount
* method
* status
* createdAt

### Review

* id
* userId
* productId
* rating
* comment
* createdAt
* updatedAt

Later modules may introduce:

* Inventory
* Coupon
* RefreshToken
* AuditLog

But do not add them unnecessarily during the initial database phase.

Create:

* ER diagram
* SQL schema
* Primary keys
* Foreign keys
* Unique constraints
* Indexes
* Check constraints
* Appropriate data types
* Cascade rules
* Timestamps
* Normalization decisions

---

# PHASE 2 — Spring Boot Foundation

Create the Maven Spring Boot project.

Use a clean package structure:

com.ecommerce

```
config
controller
service
repository
entity
dto
mapper
security
exception
util
```

Create:

* Main application class
* application.yml
* application-dev.yml
* application-test.yml if useful
* PostgreSQL configuration
* JPA configuration
* environment variable configuration
* global configuration structure

Do not hardcode secrets.

Use environment variables for:

* Database username
* Database password
* JWT secret
* Redis configuration
* Other sensitive values

Create a clean README.

---

# PHASE 3 — Entity Layer

Create JPA entities corresponding to the database.

Use appropriate:

* @Entity
* @Table
* @Id
* @GeneratedValue
* @ManyToOne
* @OneToMany
* @OneToOne
* @JoinColumn
* constraints
* indexes

Be careful with bidirectional relationships.

Avoid infinite JSON serialization.

Do NOT expose JPA entities directly from every API.

Explain:

* Entity
* DTO
* Repository
* Service
* Controller

and why these responsibilities are separated.

---

# PHASE 4 — Repository Layer

Create repositories using Spring Data JPA.

Implement appropriate queries for:

* Product search
* Category filtering
* Price filtering
* Stock checking
* User lookup
* Order lookup
* User order history
* Reviews
* Cart
* Wishlist

Use:

* JpaRepository
* Derived query methods
* JPQL where appropriate
* Pagination
* Sorting

Do not use native SQL unless there is a genuine reason.

Explain database indexing and query performance.

---

# PHASE 5 — Authentication and Authorization

Implement secure authentication.

Features:

### Registration

POST /api/auth/register

### Login

POST /api/auth/login

Use:

* Spring Security
* BCrypt password hashing
* JWT
* AuthenticationManager
* UserDetailsService
* SecurityFilterChain
* JWT filter

Roles:

* CUSTOMER
* ADMIN

Rules:

CUSTOMER:

* Browse products
* Manage cart
* Manage wishlist
* Place orders
* View own orders
* Write reviews
* Manage own addresses

ADMIN:

* Product management
* Category management
* Inventory management
* Order management
* User management

Never store plaintext passwords.

Return appropriate HTTP status codes.

Explain the entire JWT request lifecycle.

---

# PHASE 6 — User Module

Implement:

* User profile
* Update profile
* Change password
* Address CRUD
* Default address

Important security requirement:

A customer must NEVER be able to access or modify another customer's private data simply by changing an ID in the URL.

Example:

/api/users/10/orders

must verify that the authenticated user is actually allowed to access user 10.

---

# PHASE 7 — Product and Category Module

Implement:

### Category

* Create
* Read
* Update
* Delete

### Product

* Create
* Read
* Update
* Delete
* Search
* Filter
* Sort
* Pagination

Product APIs should support:

* category
* brand
* minimum price
* maximum price
* active status
* keyword search

Use pagination.

Example:

GET /api/products?page=0&size=20

Do not load thousands of products into memory unnecessarily.

---

# PHASE 8 — Product Images

Implement product image support.

For the initial version, image URLs can be stored.

Do not store large binary images directly in PostgreSQL.

Design the code so it can later support:

* Cloudinary
* AWS S3
* another object storage service

without rewriting the entire Product module.

---

# PHASE 9 — Cart Module

Implement:

* Get cart
* Add product
* Update quantity
* Remove product
* Clear cart

Business rules:

* Quantity must be positive.
* Product must exist.
* Product must be active.
* Requested quantity must be valid.
* Do not allow invalid stock quantities.

Prevent duplicate product entries inside a cart.

If a product already exists in the cart, update its quantity instead of creating a duplicate CartItem.

---

# PHASE 10 — Wishlist Module

Implement:

* Get wishlist
* Add product
* Remove product
* Clear wishlist

Prevent duplicate wishlist items.

Only authenticated users can access their wishlist.

---

# PHASE 11 — Order Module

This is one of the most important modules.

Implement:

* Place order
* Get order by ID
* Get current user's orders
* Cancel order
* Admin order management
* Update order status

Order lifecycle:

PLACED
→ CONFIRMED
→ PROCESSING
→ SHIPPED
→ OUT_FOR_DELIVERY
→ DELIVERED

Cancellation must only be allowed in valid states.

Do not allow:

DELIVERED → CANCELLED

unless the business rules explicitly support returns.

---

# PHASE 12 — Transaction Management

Use Spring transactions correctly.

For order placement:

1. Validate user
2. Validate address
3. Read cart
4. Validate products
5. Validate stock
6. Calculate total
7. Create order
8. Create order items
9. Reduce inventory
10. Create payment record
11. Clear cart
12. Commit transaction

If an important operation fails:

Rollback appropriately.

Explain:

* ACID
* transaction
* rollback
* commit
* @Transactional
* isolation
* race condition

---

# PHASE 13 — Concurrency and Inventory

This phase is critical for interviews.

Handle this scenario:

Product stock = 1

Two users simultaneously attempt to purchase it.

Do not allow:

User A → successful purchase
User B → successful purchase

when only one item exists.

Implement an appropriate concurrency strategy using database locking/optimistic or pessimistic locking where justified.

Explain:

* race condition
* lost update
* database locking
* optimistic locking
* pessimistic locking
* transaction isolation

Choose the simplest reliable approach and explain why.

---

# PHASE 14 — Payment Simulation

Do NOT integrate real financial services initially.

Create a payment simulation service.

Payment methods:

* CARD
* UPI
* NET_BANKING
* COD

Statuses:

* PENDING
* SUCCESS
* FAILED
* REFUNDED

Simulate success/failure safely.

Use transaction IDs.

Make payment logic replaceable so a real gateway could be integrated later.

Explain the difference between:

Payment Service
and
Payment Gateway.

---

# PHASE 15 — Review Module

Implement:

* Add review
* Update review
* Delete review
* Get product reviews
* Calculate average rating

Business rule:

Only a customer who purchased a product should be allowed to review it.

Prevent duplicate reviews if the business rule is one review per user per product.

Validate rating:

1–5 only.

---

# PHASE 16 — Exception Handling

Create custom exceptions such as:

* ResourceNotFoundException
* InvalidRequestException
* InsufficientStockException
* UnauthorizedException
* DuplicateResourceException
* InvalidOrderStateException
* PaymentFailedException

Create a global exception handler using:

@RestControllerAdvice

Return clean API error responses.

Example structure:

{
"timestamp": "...",
"status": 404,
"error": "PRODUCT_NOT_FOUND",
"message": "Product not found",
"path": "/api/products/100"
}

Never expose internal stack traces to users.

---

# PHASE 17 — Validation

Use Bean Validation.

Examples:

* @NotBlank
* @Email
* @Size
* @Min
* @Max
* @Positive
* @PositiveOrZero

Validate request DTOs.

Never trust frontend validation alone.

Backend validation is mandatory.

---

# PHASE 18 — DTO and Mapper Architecture

Create proper DTOs.

Do not expose sensitive entity fields.

For example, UserResponseDTO must NOT contain:

password

Separate:

* Request DTO
* Response DTO
* Entity

Use mapper classes or a clean mapping approach.

Explain why DTOs exist.

---

# PHASE 19 — Redis

Introduce Redis only where it provides real value.

Use Redis for appropriate caching, such as:

* Product details
* Categories
* Frequently requested product data

Implement cache:

* Cache hit
* Cache miss
* Cache eviction
* Cache update

Be careful with stale data.

When an admin updates a product, invalidate the relevant cache.

Explain:

* Why Redis?
* Why not query PostgreSQL every time?
* Cache hit
* Cache miss
* TTL
* Cache invalidation
* Cache consistency

---

# PHASE 20 — API Documentation

Integrate Swagger/OpenAPI.

Document:

* Authentication
* Products
* Categories
* Cart
* Wishlist
* Orders
* Payments
* Reviews
* Admin APIs

For each endpoint document:

* Request
* Response
* Parameters
* Authentication requirement
* Possible errors

---

# PHASE 21 — Testing

Create meaningful tests.

### Unit tests

Test:

* Services
* Business logic
* Order calculations
* Stock validation
* Authentication logic
* Order state transitions

Use:

* JUnit 5
* Mockito

### Integration tests

Test:

* REST endpoints
* Database interaction
* Security
* Transactions

Do not create meaningless tests just to increase coverage.

Focus on business-critical functionality.

---

# PHASE 22 — Logging and Monitoring

Add structured application logging.

Log important events:

* Login attempts
* Order creation
* Payment result
* Stock failures
* Admin operations
* Unexpected errors

Do not log:

* Passwords
* JWT secrets
* sensitive authentication information

Use appropriate log levels:

* DEBUG
* INFO
* WARN
* ERROR

---

# PHASE 23 — Docker

Containerize:

* Spring Boot backend
* PostgreSQL
* Redis

Create:

Dockerfile

docker-compose.yml

The application should be able to start using Docker Compose.

Do not hardcode local machine paths.

---

# PHASE 24 — Frontend API Integration Contract

Prepare the backend for the AI-generated React frontend.

Provide:

* Complete endpoint list
* Request examples
* Response examples
* Authentication flow
* Error response format
* Pagination format
* Product JSON structure
* Cart JSON structure
* Order JSON structure

The frontend should not need to guess the API structure.

---

# PHASE 25 — Admin Dashboard APIs

Provide backend APIs for:

* Total users
* Total products
* Total orders
* Total revenue
* Pending orders
* Low stock products
* Recent orders
* Sales summary

Use efficient database queries.

Do not fetch every order/product and calculate everything in Java memory if SQL aggregation can do it efficiently.

---

# PHASE 26 — Performance Optimization

Review the project for:

* N+1 query problems
* Missing indexes
* Excessive database queries
* Large result sets
* Unnecessary API calls
* Inefficient loops
* Cache opportunities

Use:

* Pagination
* Indexes
* Appropriate fetch strategies
* Projections where useful
* Redis caching where justified

Document before/after reasoning where possible.

---

# PHASE 27 — Security Audit

Perform a security review.

Check:

* Password hashing
* JWT validation
* Role-based access
* Authorization
* IDOR vulnerabilities
* SQL injection
* Input validation
* Sensitive data exposure
* CORS
* Error leakage
* Authentication endpoints
* Admin endpoints

Never rely only on frontend security.

---

# PHASE 28 — Final Integration

Verify:

React frontend
↓
REST APIs
↓
Spring Security
↓
Services
↓
Repositories
↓
PostgreSQL / Redis

Verify the major user journeys:

### Customer

Register
→ Login
→ Browse products
→ Search
→ View product
→ Add to cart
→ Checkout
→ Payment
→ Order
→ Track order
→ Review product

### Admin

Login
→ Dashboard
→ Add product
→ Update product
→ Manage inventory
→ View orders
→ Update order status
→ View sales statistics

---

# 4. CODING STANDARDS

Follow these rules:

* Clean Java naming
* Meaningful variable names
* Small focused methods
* Single Responsibility Principle
* Avoid unnecessary inheritance
* Prefer composition where appropriate
* Avoid giant classes
* Avoid giant methods
* Avoid duplicated business logic
* Use interfaces where they provide meaningful abstraction
* Use enums for fixed states
* Use BigDecimal for monetary values
* Use appropriate database indexes
* Use UTC timestamps where appropriate
* Avoid exposing entities directly through APIs
* Never hardcode secrets
* Never commit passwords or API keys
* Never use System.out.println for application logging
* Use proper logging
* Use meaningful comments only where necessary

---

# 5. JAVA CONCEPTS TO DEMONSTRATE

The project should naturally demonstrate:

* OOP
* Encapsulation
* Abstraction
* Inheritance where appropriate
* Polymorphism
* Interfaces
* Collections
* Generics
* Optional
* Streams
* Lambda expressions
* Exception handling
* Enums
* Records where appropriate
* BigDecimal
* Date/time API
* Concurrency concepts
* Thread safety
* Immutability where appropriate

Do not force Java features into the project just for demonstration.

---

# 6. INTERVIEW PREPARATION REQUIREMENT

For every major module, provide an interview section.

For example:

## Product Module — Interview Questions

Explain:

1. Why did you use JPA?
2. Why PostgreSQL?
3. Why DTOs?
4. Why service layer?
5. Why pagination?
6. How does product search work?
7. What indexes did you create?
8. How would you handle 1 million products?
9. What happens if two admins update the same product?
10. What is the N+1 problem?

For every major feature, explain:

### WHAT

What is it?

### WHY

Why did we use it?

### HOW

How does it work?

### WHEN

When should it be used?

### ALTERNATIVE

What alternatives exist?

### INTERVIEW

What can an interviewer ask?

---

# 7. LEARNING MODE

The developer is using this project to learn Java and backend development.

Therefore, when generating important code:

Do not simply dump code.

Explain the important code before or after generating it.

For example, if generating:

@Transactional
public Order placeOrder(...)

explain:

* What @Transactional means
* Why it is required here
* What happens when the method starts
* What happens when it succeeds
* What happens when it fails
* What gets rolled back
* What problems happen without it

Do NOT explain every obvious getter/setter individually.

Focus explanations on architecture, business logic, Java concepts, Spring concepts, database behavior, security, and interview-relevant decisions.

---

# 8. TESTING MINDSET

For every important feature, think about:

### Happy path

What should happen normally?

### Invalid input

What if the user sends invalid data?

### Missing resource

What if the product doesn't exist?

### Unauthorized access

What if another user tries to access the resource?

### Concurrent request

What if two users perform the operation simultaneously?

### Database failure

What if the database operation fails?

### Transaction failure

What if step 5 of a 7-step operation fails?

### Edge cases

Always consider edge cases.

---

# 9. GIT STRUCTURE

Create a professional Git history.

Recommended commits:

feat: initialize spring boot project

feat: add database entities

feat: implement user module

feat: implement authentication

feat: implement product module

feat: implement cart

feat: implement wishlist

feat: implement order processing

feat: add inventory concurrency control

feat: add payment simulation

feat: add review system

feat: add redis caching

test: add service tests

test: add integration tests

chore: dockerize application

docs: update api documentation

Do not create one giant commit containing the entire application.

---

# 10. README REQUIREMENT

Final README must contain:

# E-Commerce Platform

## Overview

## Features

## Architecture

## Technology Stack

## Database Design

## ER Diagram

## API Documentation

## Authentication

## Authorization

## Project Structure

## Order Lifecycle

## Concurrency Handling

## Redis Caching

## Testing

## Docker Setup

## Environment Variables

## Running Locally

## API Examples

## Screenshots

## Future Improvements

## Interview Topics Demonstrated

## Lessons Learned

---

# 11. IMPORTANT ANTI-PATTERNS TO AVOID

Do NOT:

* Put business logic in controllers
* Put SQL logic in controllers
* Return entities everywhere
* Store plaintext passwords
* Hardcode JWT secrets
* Hardcode database passwords
* Trust frontend authorization
* Ignore transaction boundaries
* Ignore concurrency
* Fetch all records unnecessarily
* Create unnecessary microservices
* Use MongoDB just because it is popular
* Add Kafka/RabbitMQ without a real use case
* Add Kubernetes without a real deployment need
* Add dozens of design patterns unnecessarily
* Create fake payment integrations
* Use fake APIs pretending they are real
* Generate placeholder TODO implementations in core functionality

Start as a **modular monolithic Spring Boot application**.

Only introduce additional infrastructure when there is a clear reason.

---

# 12. FINAL PROJECT ARCHITECTURE

The intended final architecture is:

```
                     React
                       │
                       ▼
                 REST API
                       │
                       ▼
             Spring Security
                       │
                       ▼
                  Controller
                       │
                       ▼
                     DTO
                       │
                       ▼
                   Service
                       │
         ┌─────────────┼─────────────┐
         │             │             │
         ▼             ▼             ▼
    Repository      Redis        External
         │           Cache         Services
         │
         ▼
     PostgreSQL
```

Supporting:

* JWT
* Validation
* Global Exception Handling
* Logging
* Testing
* Docker
* Swagger/OpenAPI

---

# 13. FINAL QUALITY BAR

The finished project must be:

* Runnable
* Testable
* Secure
* Maintainable
* Properly documented
* GitHub-ready
* Portfolio-ready
* Interview-ready

The project should demonstrate genuine understanding of:

Java
Spring Boot
REST APIs
SQL
PostgreSQL
JPA/Hibernate
Security
JWT
Transactions
Concurrency
Caching
Testing
Docker
System design

---

# 14. MOST IMPORTANT INSTRUCTION

DO NOT START BY GENERATING EVERYTHING.

Start with:

## PHASE 0 — PROJECT PLANNING

Give me:

1. Architecture
2. Complete module list
3. Database entities
4. Entity relationships
5. Package structure
6. API roadmap
7. Security architecture
8. Development roadmap
9. Technology justification
10. Risks and important design decisions

Then STOP.

Wait for approval before beginning Phase 1.

For every subsequent phase, finish the phase completely, verify it, explain it, and then wait for approval before continuing.

The goal is not merely to generate code.

The goal is to produce a project that the developer can confidently explain in a Java/Spring Boot placement interview.
