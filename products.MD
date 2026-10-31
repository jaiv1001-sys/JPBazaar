 [localhost](file;ttp://localhost) # JPBazaar – Professional Product Catalogue & Bulk Import Implementation

## ROLE
Act as a Senior Full-Stack Engineer, E-commerce Architect, Database Engineer and UI/UX Designer.

Your task is to implement a production-quality Product Management System for my existing JPBazaar e-commerce application.

Do not create a new application. Work inside my existing project and extend its current architecture.

## 1. EXISTING PROJECT CONTEXT

JPBazaar is an e-commerce application with:

- Backend: Java + Spring Boot
- Database: PostgreSQL
- ORM: Spring Data JPA / Hibernate
- Frontend: Existing JPBazaar frontend
- Authentication: JWT-based authentication
- Authorization: CUSTOMER and ADMIN roles

The application already contains product-related entities, including:

- Product
- ProductImage
- Category

First inspect the complete project structure, existing entities, DTOs, repositories, services, controllers, security configuration, database schema and frontend components.

IMPORTANT:
- Reuse existing implementations wherever possible.
- Do not create duplicate entities or database tables.
- Do not break existing APIs or customer functionality.
- Do not change the existing authentication architecture unnecessarily.
- Follow existing naming conventions and coding standards.
- Never assume an implementation exists without inspecting it.

## 2. PRIMARY OBJECTIVE

Implement two professional product addition methods:

### Method A: Manual Product Management

Authorized administrators must be able to:

- Add new products.
- Edit existing products.
- Delete or deactivate products.
- Update prices.
- Update stock quantities.
- Manage product categories.
- Manage product specifications.
- Upload multiple product images.
- Select a primary product image.
- Manage product availability and publication status.

### Method B: Bulk Product Import

Administrators must be able to:

- Upload CSV files.
- Upload Excel (.xlsx) files.
- Download a sample import template.
- Preview imported product records.
- Validate every record before importing.
- Detect duplicate SKUs.
- Identify missing or invalid fields.
- Display row-level validation errors.
- Import valid records.
- Download an import error report.
- View import history and summary.

The system must not silently skip invalid records.

## 3. INITIAL PRODUCT CATALOGUE

Prepare an initial catalogue of 260 realistic products.

| Category | Product Count |
|---|---:|
| Electronics | 50 |
| Fashion | 50 |
| Home & Living | 40 |
| Accessories | 40 |
| Beauty | 40 |
| Sports & Fitness | 40 |
| TOTAL | 260 |

Use real-world brands and genuine product models wherever possible.

Examples:

Electronics:
- Apple
- Samsung
- Dell
- HP
- Lenovo
- Sony
- LG

Fashion:
- Nike
- Adidas
- Puma
- Levi's
- H&M
- Zara

Home & Living:
- IKEA
- Philips
- Havells
- Prestige
- Wakefit

Accessories:
- boAt
- JBL
- Logitech
- Anker
- Noise

Beauty:
- Nivea
- Cetaphil
- Minimalist
- L'Oréal
- Neutrogena

Sports & Fitness:
- Decathlon
- Nike
- Adidas
- Puma
- Yonex

These are examples, not a restriction on the catalogue.

IMPORTANT PRODUCT DATA RULES:

- Use realistic product names and descriptions.
- Use correct brand and category associations.
- Research product specifications from reliable sources.
- Use INR pricing.
- Do not invent product ratings or customer reviews.
- Do not invent sales figures.
- Do not claim an image represents an exact model unless verified.
- Do not use copyrighted or misleading product images.
- Use verified image sources or properly licensed product images.
- Do not generate random meaningless product records just to reach 260.
- Clearly identify any information that could not be verified.

## 4. PRODUCT DATA STRUCTURE

Inspect the existing Product entity and extend it only when necessary.

The product catalogue should support:

- Product ID
- Product name
- SKU
- Brand
- Category
- Short description
- Full description
- Selling price
- Original price
- Discount information
- Stock quantity
- Product specifications
- Product images
- Primary image
- Active/inactive status
- Publication status
- Creation timestamp
- Updated timestamp

Support product variants where appropriate:

- Size
- Colour
- Storage capacity
- Other category-specific attributes

Do not force every category to use identical specifications.

Avoid unnecessary database changes and maintain existing relationships.

## 5. ADMIN FRONTEND

Create a premium Admin Product Management experience.

Design requirements:

- Primary colour: Luxury black.
- Secondary colour: Shining gold.
- Supporting colours: White, charcoal, subtle grey and appropriate status colours.
- Premium typography.
- Consistent spacing.
- Responsive layout.
- Smooth interactions.
- Clear navigation.
- Professional data tables.
- Loading states.
- Empty states.
- Error states.
- Success notifications.

Create the following screens:

### A. Product Dashboard

Display:

- Total products
- Active products
- Inactive products
- Out-of-stock products
- Low-stock products
- Total categories

### B. Product Listing

Features:

- Product image
- Product name
- SKU
- Brand
- Category
- Price
- Stock
- Status
- Edit action
- Delete/deactivate action

Add:

- Search
- Category filters
- Brand filters
- Stock filters
- Status filters
- Sorting
- Pagination
- Bulk selection where appropriate

### C. Add/Edit Product

Create a professional product form with:

- Basic information
- Pricing
- Inventory
- Category
- Specifications
- Product variants
- Multiple image uploads
- Primary image selection

Provide proper frontend and backend validation.

### D. Bulk Import

Create a dedicated import page with:

1. Download CSV template.
2. Download Excel template.
3. Drag-and-drop upload.
4. File validation.
5. Import preview.
6. Row-level validation results.
7. Duplicate SKU warnings.
8. Valid and invalid record counts.
9. Import confirmation.
10. Import progress.
11. Final import summary.
12. Download error report.

Do not execute imports automatically immediately after uploading a file.

## 6. BACKEND IMPLEMENTATION

Follow the existing layered architecture.

Implement or extend:

- Entity layer
- DTO layer
- Repository layer
- Service layer
- Controller layer
- Validation
- Exception handling
- Security configuration

Use:

- DTOs instead of exposing JPA entities.
- Bean Validation.
- Transaction management.
- Pagination.
- Proper HTTP status codes.
- Centralized exception handling.
- PostgreSQL constraints.
- Existing JWT security.

Create suitable APIs for:

- Creating products
- Updating products
- Retrieving products
- Deactivating products
- Managing images
- Managing categories
- Bulk import preview
- Bulk import execution
- Import history
- Import error reports

All administrative endpoints must enforce ADMIN authorization on the backend.

Never rely exclusively on hiding admin buttons in the frontend.

## 7. BULK IMPORT ENGINE

Implement bulk import as a reliable workflow.

Recommended process:

Upload → Parse → Validate → Preview → Confirm → Import → Report

Requirements:

- Support CSV and XLSX.
- Validate file format and size.
- Validate required fields.
- Validate category references.
- Validate price and stock.
- Detect duplicate SKUs within the uploaded file.
- Detect duplicate SKUs in the database.
- Handle malformed files safely.
- Avoid duplicate insertion.
- Preserve database consistency.
- Provide row-level error messages.
- Use appropriate transaction boundaries.
- Prevent accidental repeated imports.

For large imports, use batch processing where appropriate.

Use an import tracking mechanism if necessary.

Do not introduce unnecessary microservices or complicated distributed architecture.

## 8. IMAGE MANAGEMENT

Implement secure product image management.

Requirements:

- Multiple images per product.
- Primary image selection.
- Image preview.
- Image removal.
- File type validation.
- File size validation.
- Secure upload handling.

Use suitable object storage or an existing image storage solution.

Store image URLs and metadata in PostgreSQL, not image binary data, unless the existing architecture explicitly requires otherwise.

## 9. TESTING

Write tests for:

- Product creation
- Product updates
- Product deactivation
- SKU uniqueness
- Invalid pricing
- Invalid stock
- Unauthorized access
- CSV parsing
- Excel parsing
- Duplicate records
- Invalid categories
- Partial import failures
- Repeated import requests
- Product image validation

Verify that existing customer-facing product functionality continues to work.

## 10. IMPLEMENTATION STRATEGY

Implement the project in the following phases:

Phase 1: Existing architecture analysis.

Phase 2: Database and product entity improvements.

Phase 3: Product management backend APIs.

Phase 4: Admin product management frontend.

Phase 5: Product image management.

Phase 6: CSV and Excel bulk import engine.

Phase 7: Import preview, validation and error reporting.

Phase 8: Prepare and import the 260-product catalogue.

Phase 9: Integration testing and final verification.

Complete and verify each phase before proceeding.

Do not skip testing.

## 11. STRICT ENGINEERING RULES

- Do not rewrite the entire application.
- Do not delete existing working functionality.
- Do not introduce duplicate APIs.
- Do not create unnecessary database tables.
- Do not hardcode product records inside frontend components.
- Do not bypass backend validation.
- Do not expose admin operations to customers.
- Do not use mock data as final production data.
- Do not leave unfinished TODO implementations.
- Do not claim success without testing.
- Preserve the existing JPBazaar premium black-and-gold visual identity.

## 12. FINAL DELIVERABLE

At completion, provide:

1. Summary of existing architecture discovered.
2. Files created and modified.
3. Database changes and migrations.
4. Backend API documentation.
5. Admin frontend functionality.
6. CSV and Excel templates.
7. Initial 260-product catalogue.
8. Product image storage approach.
9. Test results.
10. Instructions to run and verify the complete feature.

The final result must be a reliable, maintainable product management system suitable for a real-world e-commerce application.

Start with Phase 1: inspect the existing project thoroughly and report the implementation plan before modifying code.