-- =============================================================================
-- JPBazaar - PostgreSQL Database Schema
-- Database Architecture & DDL Definitions
-- =============================================================================

-- Drop tables if they exist (in reverse dependency order)
DROP TABLE IF EXISTS reviews CASCADE;
DROP TABLE IF EXISTS payments CASCADE;
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS wishlist_items CASCADE;
DROP TABLE IF EXISTS wishlists CASCADE;
DROP TABLE IF EXISTS cart_items CASCADE;
DROP TABLE IF EXISTS carts CASCADE;
DROP TABLE IF EXISTS product_images CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS addresses CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- -----------------------------------------------------------------------------
-- 1. USERS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL CONSTRAINT uk_users_email UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER' CONSTRAINT chk_users_role CHECK (role IN ('CUSTOMER', 'ADMIN')),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- -----------------------------------------------------------------------------
-- 2. ADDRESSES TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE addresses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL CONSTRAINT fk_addresses_user REFERENCES users(id) ON DELETE CASCADE,
    address_line VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    country VARCHAR(100) NOT NULL,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- -----------------------------------------------------------------------------
-- 3. CATEGORIES TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL CONSTRAINT uk_categories_name UNIQUE,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- -----------------------------------------------------------------------------
-- 4. PRODUCTS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT NOT NULL CONSTRAINT fk_products_category REFERENCES categories(id) ON DELETE RESTRICT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price NUMERIC(10, 2) NOT NULL CONSTRAINT chk_products_price CHECK (price >= 0),
    stock_quantity INT NOT NULL DEFAULT 0 CONSTRAINT chk_products_stock CHECK (stock_quantity >= 0),
    sku VARCHAR(100) NOT NULL CONSTRAINT uk_products_sku UNIQUE,
    brand VARCHAR(100),
    rating NUMERIC(3, 2) DEFAULT 0.00 CONSTRAINT chk_products_rating CHECK (rating >= 0 AND rating <= 5),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- -----------------------------------------------------------------------------
-- 5. PRODUCT IMAGES TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE product_images (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL CONSTRAINT fk_product_images_product REFERENCES products(id) ON DELETE CASCADE,
    image_url VARCHAR(512) NOT NULL,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE
);

-- -----------------------------------------------------------------------------
-- 6. CARTS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE carts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL CONSTRAINT uk_carts_user UNIQUE CONSTRAINT fk_carts_user REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- -----------------------------------------------------------------------------
-- 7. CART ITEMS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT NOT NULL CONSTRAINT fk_cart_items_cart REFERENCES carts(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL CONSTRAINT fk_cart_items_product REFERENCES products(id) ON DELETE CASCADE,
    quantity INT NOT NULL CONSTRAINT chk_cart_items_quantity CHECK (quantity > 0),
    CONSTRAINT uk_cart_product UNIQUE (cart_id, product_id)
);

-- -----------------------------------------------------------------------------
-- 8. WISHLISTS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE wishlists (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL CONSTRAINT uk_wishlists_user UNIQUE CONSTRAINT fk_wishlists_user REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- -----------------------------------------------------------------------------
-- 9. WISHLIST ITEMS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE wishlist_items (
    id BIGSERIAL PRIMARY KEY,
    wishlist_id BIGINT NOT NULL CONSTRAINT fk_wishlist_items_wishlist REFERENCES wishlists(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL CONSTRAINT fk_wishlist_items_product REFERENCES products(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uk_wishlist_product UNIQUE (wishlist_id, product_id)
);

-- -----------------------------------------------------------------------------
-- 10. ORDERS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL CONSTRAINT fk_orders_user REFERENCES users(id) ON DELETE RESTRICT,
    address_id BIGINT NOT NULL CONSTRAINT fk_orders_address REFERENCES addresses(id) ON DELETE RESTRICT,
    total_amount NUMERIC(12, 2) NOT NULL CONSTRAINT chk_orders_total CHECK (total_amount >= 0),
    status VARCHAR(30) NOT NULL DEFAULT 'PLACED' CONSTRAINT chk_orders_status CHECK (status IN ('PLACED', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'OUT_FOR_DELIVERY', 'DELIVERED', 'CANCELLED')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- -----------------------------------------------------------------------------
-- 11. ORDER ITEMS TABLE (Historical Snapshot)
-- -----------------------------------------------------------------------------
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL CONSTRAINT fk_order_items_order REFERENCES orders(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL CONSTRAINT fk_order_items_product REFERENCES products(id) ON DELETE RESTRICT,
    product_name VARCHAR(255) NOT NULL,
    price NUMERIC(10, 2) NOT NULL CONSTRAINT chk_order_items_price CHECK (price >= 0),
    quantity INT NOT NULL CONSTRAINT chk_order_items_quantity CHECK (quantity > 0),
    subtotal NUMERIC(12, 2) NOT NULL CONSTRAINT chk_order_items_subtotal CHECK (subtotal >= 0)
);

-- -----------------------------------------------------------------------------
-- 12. PAYMENTS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL CONSTRAINT uk_payments_order UNIQUE CONSTRAINT fk_payments_order REFERENCES orders(id) ON DELETE RESTRICT,
    transaction_id VARCHAR(100) NOT NULL CONSTRAINT uk_payments_transaction UNIQUE,
    amount NUMERIC(12, 2) NOT NULL CONSTRAINT chk_payments_amount CHECK (amount >= 0),
    method VARCHAR(30) NOT NULL CONSTRAINT chk_payments_method CHECK (method IN ('CARD', 'UPI', 'NET_BANKING', 'COD')),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING' CONSTRAINT chk_payments_status CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- -----------------------------------------------------------------------------
-- 13. REVIEWS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL CONSTRAINT fk_reviews_user REFERENCES users(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL CONSTRAINT fk_reviews_product REFERENCES products(id) ON DELETE CASCADE,
    rating INT NOT NULL CONSTRAINT chk_reviews_rating CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uk_user_product_review UNIQUE (user_id, product_id)
);

-- =============================================================================
-- INDEXES FOR QUERY OPTIMIZATION
-- =============================================================================

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_addresses_user_id ON addresses(user_id);
CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_products_active_price ON products(active, price);
CREATE INDEX idx_products_sku ON products(sku);
CREATE INDEX idx_product_images_product_id ON product_images(product_id);
CREATE INDEX idx_cart_items_cart_id ON cart_items(cart_id);
CREATE INDEX idx_wishlist_items_wishlist_id ON wishlist_items(wishlist_id);
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_payments_order_id ON payments(order_id);
CREATE INDEX idx_payments_transaction_id ON payments(transaction_id);
CREATE INDEX idx_reviews_product_id ON reviews(product_id);
CREATE INDEX idx_reviews_user_id ON reviews(user_id);
