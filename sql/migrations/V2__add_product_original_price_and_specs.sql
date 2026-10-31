-- Migration: Add original_price and specifications to products table
ALTER TABLE products
    ADD COLUMN IF NOT EXISTS original_price NUMERIC(10, 2) CONSTRAINT chk_products_original_price CHECK (original_price IS NULL OR original_price >= 0),
    ADD COLUMN IF NOT EXISTS specifications TEXT;
