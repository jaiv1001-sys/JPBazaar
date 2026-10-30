# JPBazaar — Luxury Premium E-Commerce Platform
## Master Frontend Development & Backend Integration Prompt

### 1. YOUR ROLE

Act as a world-class team consisting of:

- Senior UI/UX Designer
- Luxury Brand Design Specialist
- Senior React Frontend Architect
- TypeScript Engineer
- Design System Engineer
- E-Commerce Product Designer
- Accessibility Specialist
- Frontend Performance Engineer

Your responsibility is to transform my existing JPBazaar project into a visually stunning, premium, user-friendly, fully functional e-commerce platform.

Do not create a generic e-commerce template.

Think like a team designing a world-class shopping experience that can compete with the usability, convenience, and functionality of Amazon and Flipkart while establishing its own distinctive luxury brand identity.

The result must look professionally designed, commercially credible, and portfolio-worthy.

---

# 2. PROJECT CONTEXT

Project Name: JPBazaar

Project Type: Full-Stack E-Commerce Application

This is an existing Java Spring Boot backend project. The frontend must integrate with the existing backend.

### Backend Technology Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- Spring Security
- JWT Authentication
- PostgreSQL
- Redis
- Maven
- REST APIs
- Bean Validation
- JUnit 5
- Mockito

### Frontend Technology Stack

Use:

- React
- TypeScript
- Tailwind CSS
- React Router
- Axios
- Lucide React icons

You may introduce additional lightweight libraries when there is a genuine requirement.

Do not unnecessarily introduce large libraries or complicated architecture.

### Critical Instruction

The backend already exists.

Before implementing anything:

1. Inspect the complete existing backend project.
2. Understand its API endpoints.
3. Understand authentication and authorization.
4. Inspect request and response DTOs.
5. Understand pagination and error response formats.
6. Understand the product, category, cart, wishlist, order, payment and review modules.
7. Identify the actual API contracts.
8. Understand the existing project directory structure.

Do not invent API endpoints.

Do not modify existing backend business logic just to accommodate frontend assumptions.

If an API is missing, document the missing functionality instead of silently creating fake data or pretending the integration works.

The frontend must communicate with the real Spring Boot backend.

---

# 3. BRAND IDENTITY — JPBazaar

Create a distinctive luxury shopping identity.

### Primary design philosophy

Luxury + Elegance + Simplicity + Trust + Convenience.

The website should communicate:

- Premium quality
- Exclusivity
- Trustworthiness
- Sophistication
- Modern technology
- Effortless shopping

### Primary colors

Black should be the dominant brand color.

- Primary Black: #080808
- Secondary Black: #111111
- Surface Black: #181818
- Elevated Surface: #222222
- Primary Gold: #D4AF37
- Shining Gold: #F5D76E
- Muted Gold: #A88932
- Premium White: #FFFFFF
- Soft White: #F5F3ED
- Muted Text: #A3A3A3

Use additional colors only when they improve usability.

For example:

- Emerald green for successful payments.
- Red for errors and destructive actions.
- Blue for informational messages.
- Amber for warnings.

### Color distribution

Black must remain the primary identity.

Gold should be used strategically for:

- Primary CTAs
- Important highlights
- Active navigation states
- Premium badges
- Borders and separators
- Selected interactive elements
- Small decorative accents

Do not make every element gold.

Avoid excessive gradients, glowing effects, and unnecessary animations.

The design should look expensive, not flashy.

### Typography

Use a sophisticated typography system.

Suggested combination:

- Headings: Playfair Display or a similar elegant serif.
- Body: Inter or a similar modern sans-serif.
- Product prices: Clean, readable sans-serif.

Maintain excellent readability.

---

# 4. DESIGN PHILOSOPHY

The website must follow these principles:

1. User-friendly navigation.
2. Minimal cognitive load.
3. Consistent visual hierarchy.
4. Beautiful product presentation.
5. Clear product information.
6. Fast shopping experience.
7. Responsive layouts.
8. Accessible interactions.
9. Consistent spacing and alignment.
10. Professional visual consistency.

Do not sacrifice usability for aesthetics.

A customer should be able to find a product, understand its details, add it to their cart, and complete checkout without confusion.

---

# 5. CUSTOMER-FACING WEBSITE

Create the following complete customer experience.

## A. Homepage

The homepage is the most important visual experience.

Design it like a premium international shopping brand.

### Header

Include:

- JPBazaar logo
- Home
- Shop
- Categories
- New Arrivals
- Best Sellers
- Search
- Wishlist
- Account
- Cart

Requirements:

- Sticky navigation.
- Responsive mobile navigation.
- Attractive hover interactions.
- Proper active navigation states.
- Search bar with a clean interface.
- Cart item count.
- Accessible dropdown menus.

### Hero Section

Create a visually impressive hero section.

Include:

- High-quality lifestyle/product photography.
- Elegant headline.
- Short brand description.
- Strong shopping CTA.
- Secondary CTA.
- Premium typography.
- Carefully balanced negative space.

Suggested headline:

"Beyond Shopping. Beyond Ordinary."

Suggested supporting text:

"Discover a world of carefully selected products, exceptional quality, and an elevated shopping experience."

CTA:

"Explore Collection"

Do not use generic stock-looking banners or excessive text.

### Category Showcase

Create visually attractive category cards.

Each card should include:

- Category image
- Category name
- Short description
- Explore action

Use actual categories retrieved from the backend.

### Featured Products

Create sections for:

- Featured Collections
- New Arrivals
- Best Sellers
- Trending Products
- Recommended Products

Each product card should contain:

- Product image
- Product name
- Brand
- Price
- Original price when available
- Discount percentage when supported by backend data
- Rating
- Wishlist button
- Quick View
- Add to Cart
- Availability status

Use consistent product image ratios.

Product cards should feel premium and clean.

### Additional Homepage Sections

Include:

- Brand story
- Premium shopping benefits
- Customer reviews
- Newsletter subscription UI
- Promotional banner
- Recently viewed products where supported
- Professional footer

Never create fake customer testimonials.

---

## B. Product Listing Page

Create a premium product discovery experience.

Include:

- Breadcrumb navigation
- Page heading
- Category description
- Search
- Filter sidebar
- Sort dropdown
- Product grid
- Pagination

### Filters

Support actual backend capabilities:

- Category
- Brand
- Minimum price
- Maximum price
- Availability
- Rating, if supported

### Sorting

Support available backend sorting:

- Price: Low to High
- Price: High to Low
- Newest
- Name
- Rating, if supported

### Product Grid

Desktop: 4 columns where space permits.

Tablet: 2–3 columns.

Mobile: 2 columns where product information remains readable.

Include:

- Loading skeletons
- Empty states
- Error states
- Pagination controls

Filters and sorting must actually update backend requests.

---

## C. Product Details Page

This page must provide an excellent shopping experience.

Include:

- Product image gallery
- Primary image
- Thumbnail navigation
- Product name
- Brand
- Price
- Rating
- Description
- Stock availability
- Quantity selector
- Add to Cart
- Buy Now
- Wishlist
- Product specifications when available
- Customer reviews
- Related products

Use a premium two-column layout on desktop.

On mobile, stack the information naturally.

Never invent product specifications.

Only display information supplied by the backend.

---

## D. Authentication

Create beautiful authentication screens.

### Registration

Fields:

- First name
- Last name
- Email
- Phone
- Password
- Confirm password

Include proper validation.

### Login

Fields:

- Email
- Password
- Remember me only if the authentication architecture supports it.
- Forgot password only if a real recovery flow exists.

Integrate with the actual JWT authentication endpoints.

Show clear errors for:

- Invalid credentials
- Duplicate email
- Invalid input
- Server errors

Do not store passwords in browser storage.

Handle authentication tokens according to the backend's security contract.

---

## E. Shopping Cart

Create a premium cart interface.

Include:

- Product image
- Product name
- Unit price
- Quantity controls
- Subtotal
- Remove item
- Clear cart
- Cart total
- Checkout CTA

Important:

- Synchronize cart data with the backend.
- Update quantities using actual APIs.
- Validate stock.
- Display unavailable products clearly.
- Prevent duplicate cart entries.
- Refresh the displayed totals after changes.

Include a beautiful empty-cart state.

---

## F. Wishlist

Include:

- Saved products
- Product images
- Price
- Availability
- Remove from wishlist
- Move to cart

All operations must use the backend APIs.

---

## G. Checkout

Make checkout simple, trustworthy and easy.

Create a step-based checkout experience:

Step 1: Delivery Address

Step 2: Order Summary

Step 3: Payment Method

Step 4: Order Confirmation

Payment methods should reflect actual backend support:

- UPI
- Card
- Net Banking
- Cash on Delivery

The backend currently uses payment simulation. Clearly communicate that payments are simulated.

Never imply that a real financial transaction is happening.

Display:

- Product subtotal
- Delivery charge, if configured
- Discount, if supported
- Final amount
- Selected address
- Payment status

Prevent duplicate order submissions.

---

## H. My Account Dashboard

Create a professional customer account area.

Include:

- Profile
- Personal information
- Manage addresses
- Default address
- My orders
- Order details
- Order tracking
- Wishlist
- Change password

Use clean navigation and understandable information architecture.

Customers must only access their own information.

---

## I. Order Tracking

Create a visually clear order lifecycle.

Show:

- Order placed
- Confirmed
- Processing
- Shipped
- Out for delivery
- Delivered

Also show cancellation status when applicable.

Use the actual order status returned by the backend.

Do not display future milestones as completed.

---

## J. Reviews

Implement:

- Product reviews
- Star ratings
- Review comments
- Average rating
- Review submission
- Edit and delete actions when authorized

Respect the backend rule that only eligible customers who purchased a product can review it.

---

# 6. ADMIN DASHBOARD

Create a separate professional administration experience.

The admin dashboard should feel like a premium business management application.

### Dashboard overview

Display:

- Total users
- Total products
- Total orders
- Total revenue
- Pending orders
- Low-stock products
- Recent orders
- Sales statistics

Use real admin APIs.

### Product Management

Implement:

- Product listing
- Add product
- Edit product
- Delete product
- Product images
- Stock management
- Category assignment
- Search and filters

### Category Management

Implement:

- Create category
- Update category
- Delete category
- View categories

### Order Management

Implement:

- View orders
- View order details
- Update order status
- Manage cancellations

### User Management

Implement only the operations supported by the existing backend.

### Admin UX

Include:

- Sidebar navigation
- Dashboard cards
- Data tables
- Search
- Filters
- Pagination
- Charts
- Confirmation dialogs
- Toast notifications

Do not use fake dashboard statistics.

If a backend endpoint is unavailable, identify it clearly.

---

# 7. DESIGN SYSTEM

Create a reusable design system.

Define reusable components:

- Button
- Input
- Select
- Modal
- Dropdown
- Card
- ProductCard
- ProductGrid
- Badge
- Toast
- Loader
- Skeleton
- Pagination
- Breadcrumb
- Navbar
- Footer
- Sidebar
- DataTable
- EmptyState
- ErrorState

Establish:

- Color tokens
- Typography scale
- Spacing scale
- Border radius
- Shadows
- Breakpoints
- Animation rules
- Component variants

Maintain visual consistency throughout the application.

Do not duplicate styling across multiple components unnecessarily.

---

# 8. FRONTEND ARCHITECTURE

Use a clean and scalable directory structure.

Suggested structure:

src/
  assets/
  components/
    common/
    layout/
    product/
    cart/
    checkout/
    admin/
  pages/
    home/
    auth/
    products/
    cart/
    wishlist/
    checkout/
    account/
    orders/
    admin/
  services/
    api/
    auth/
    product/
    cart/
    order/
    payment/
  hooks/
  context/
  store/
  types/
  utils/
  routes/
  styles/

Adjust this structure when the existing project already has a sensible architecture.

Use:

- TypeScript interfaces/types.
- Reusable API service functions.
- Centralized Axios configuration.
- Proper route protection.
- Customer/admin role-based navigation.
- Reusable form validation.
- Consistent error handling.
- Proper loading states.

Avoid giant components.

Keep components focused and maintainable.

---

# 9. BACKEND INTEGRATION REQUIREMENTS

This is a critical requirement.

The frontend must use actual Spring Boot REST APIs.

### API integration

Create a centralized API client.

Configure:

- Base URL through environment variables.
- Request interceptors where appropriate.
- Authentication handling.
- Error handling.
- Response typing.
- Token refresh only if supported by the backend.

Example environment variable:

VITE_API_BASE_URL=http://localhost:8080

Do not hardcode API URLs throughout the application.

### Integration rules

1. Inspect existing backend endpoint definitions.
2. Match frontend requests to the actual endpoint paths.
3. Match request DTO structures.
4. Match response DTO structures.
5. Handle pagination correctly.
6. Handle backend validation errors.
7. Handle JWT authentication.
8. Handle expired sessions.
9. Handle unauthorized responses.
10. Handle server failures gracefully.

Never assume that the frontend and backend share the same data structure.

Create TypeScript types based on actual backend DTOs.

### Data integrity

The backend is the source of truth.

Never calculate critical order totals independently and trust the frontend result.

Never trust frontend stock validation alone.

Never trust frontend role checks alone.

---

# 10. RESPONSIVENESS

The application must work beautifully on:

- Desktop
- Laptop
- Tablet
- Mobile

Use mobile-first responsive design.

Pay particular attention to:

- Navigation
- Product grids
- Product details
- Cart
- Checkout
- Account dashboard
- Admin tables

No horizontal overflow.

No broken layouts.

No unreadable text.

No overlapping buttons.

Mobile shopping must feel like a first-class experience, not a compressed desktop website.

---

# 11. MICRO-INTERACTIONS AND ANIMATIONS

Use subtle, premium interactions.

Examples:

- Smooth hover effects
- Product image transitions
- Button feedback
- Cart update animation
- Wishlist heart animation
- Page transitions
- Dropdown transitions
- Skeleton loading
- Toast notifications

Animations should be smooth and restrained.

Respect reduced-motion accessibility preferences.

Do not sacrifice performance for animations.

---

# 12. ACCESSIBILITY AND USABILITY

Follow accessibility best practices.

Include:

- Semantic HTML
- Proper labels
- Keyboard navigation
- Visible focus states
- Accessible dialogs
- Appropriate contrast
- Alt text for images
- Screen-reader-friendly controls
- Clear validation messages

Use WCAG 2.2 AA as the accessibility target.

Ensure gold text on black has sufficient contrast.

---

# 13. PERFORMANCE

Optimize the application.

Implement:

- Lazy loading
- Image optimization
- Code splitting
- Route-based loading
- Efficient state management
- Pagination
- Avoid unnecessary re-renders
- Proper API request handling
- Appropriate caching

Do not download every product at once.

Do not introduce unnecessary dependencies.

---

# 14. DEVELOPMENT PROCESS — FOLLOW STRICTLY

Do not generate the entire frontend blindly in one step.

Work in phases.

### Phase A — Existing Project Audit

Inspect the backend and current frontend.

Deliver:

- Existing project structure
- API endpoint inventory
- DTO mapping
- Authentication flow
- Missing API requirements
- Integration risks
- Frontend implementation plan

Do not start major implementation until this audit is complete.

### Phase B — Brand and Design System

Create:

- Brand identity
- Color system
- Typography
- Spacing
- Reusable components
- Responsive layout foundations

### Phase C — Application Foundation

Implement:

- React application structure
- Routing
- Axios client
- TypeScript types
- Global state
- Authentication foundation
- Error handling

### Phase D — Customer Experience

Implement:

- Homepage
- Product listing
- Product details
- Search
- Cart
- Wishlist

### Phase E — Checkout and Account

Implement:

- Authentication pages
- Address management
- Checkout
- Payment simulation integration
- Orders
- Order tracking
- Reviews

### Phase F — Admin Dashboard

Implement:

- Dashboard
- Products
- Categories
- Inventory
- Orders
- Users
- Analytics

### Phase G — Final Integration and Quality

Verify:

- Every API integration
- Every major customer journey
- Every admin workflow
- Authentication
- Authorization
- Responsive design
- Accessibility
- Error states
- Performance

For each phase:

1. Explain what will be built.
2. Explain important architectural decisions.
3. Implement the phase.
4. Run the application.
5. Fix errors.
6. Verify functionality.
7. Report completed work.
8. List any limitations.
9. Wait for my approval before proceeding.

---

# 15. STRICT DESIGN RULES

NEVER:

- Create a generic e-commerce template.
- Copy Amazon or Flipkart branding.
- Use excessive gold everywhere.
- Use too many gradients.
- Use random colors.
- Use emoji as primary interface icons.
- Use low-quality product images.
- Use inconsistent spacing.
- Create fake product information.
- Create fake reviews.
- Create fake dashboard statistics.
- Use placeholder functionality and call it complete.
- Leave buttons non-functional.
- Ignore mobile responsiveness.
- Ignore backend integration.
- Expose sensitive information.
- Break existing backend functionality.
- Rewrite working backend modules unnecessarily.

Every visible interactive element must perform its intended action.

If a feature cannot be implemented because the backend does not support it, report that limitation clearly.

---

# 16. FINAL EXPECTATION

The finished JPBazaar application should feel like a real luxury e-commerce brand.

The experience should combine:

- The discovery and convenience of leading e-commerce platforms.
- The elegant presentation of luxury retail brands.
- The usability of modern consumer applications.
- The functionality of a complete e-commerce system.
- The engineering quality of a professional software project.

The final result must be:

- Visually distinctive
- Fully responsive
- Easy to navigate
- Functionally complete
- Backend-integrated
- Secure
- Accessible
- Performant
- Maintainable
- Portfolio-ready

Most importantly, prioritize real functionality over visual demonstrations.

---

# 17. FIRST ACTION

Start ONLY with Phase A: Existing Project Audit.

Inspect my existing JPBazaar backend and frontend.

Understand the actual implementation before making changes.

Return a detailed audit and proposed implementation plan.

Do not generate the entire frontend immediately.

Wait for my approval before beginning Phase B.

Remember:

**We are not building just another shopping website. We are building JPBazaar — a distinctive luxury shopping experience powered by professional backend engineering.**
My additional design recommendations
I would make three decisions before implementation


1. Use a luxury editorial homepage

Large product photography, elegant typography, black backgrounds and carefully placed gold accents. Avoid filling the entire screen with product cards.


2. Use light surfaces where shopping needs clarity

My suggestion is to keep the brand identity black and gold, but allow selected product listing and product detail areas to use soft ivory backgrounds. This improves product visibility and readability.