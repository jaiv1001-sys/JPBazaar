-- =============================================================================
-- JPBazaar: Seed 260 Products into PostgreSQL
-- =============================================================================

BEGIN;

-- 1. Ensure all 6 departments exist
INSERT INTO categories (name, description, created_at) VALUES ('Electronics', 'Cutting-edge laptops, smart devices, and high-performance computing.', CURRENT_TIMESTAMP) ON CONFLICT (name) DO UPDATE SET description = EXCLUDED.description;
INSERT INTO categories (name, description, created_at) VALUES ('Fashion', 'Designer apparel, tailored clothing, luxury footwear, and seasonal collections.', CURRENT_TIMESTAMP) ON CONFLICT (name) DO UPDATE SET description = EXCLUDED.description;
INSERT INTO categories (name, description, created_at) VALUES ('Home & Living', 'Smart home appliances, modern ergonomic furnishings, and culinary essentials.', CURRENT_TIMESTAMP) ON CONFLICT (name) DO UPDATE SET description = EXCLUDED.description;
INSERT INTO categories (name, description, created_at) VALUES ('Accessories', 'Premium watches, designer eyewear, audio gear, and lifestyle accessories.', CURRENT_TIMESTAMP) ON CONFLICT (name) DO UPDATE SET description = EXCLUDED.description;
INSERT INTO categories (name, description, created_at) VALUES ('Beauty', 'Dermatological skincare, luxury fragrances, cosmetics, and self-care.', CURRENT_TIMESTAMP) ON CONFLICT (name) DO UPDATE SET description = EXCLUDED.description;
INSERT INTO categories (name, description, created_at) VALUES ('Sports & Fitness', 'Athletic performance gear, gym equipment, sportswear, and outdoor fitness.', CURRENT_TIMESTAMP) ON CONFLICT (name) DO UPDATE SET description = EXCLUDED.description;

-- 2. Clear old demo products and reset image links
DELETE FROM product_images WHERE product_id IN (SELECT id FROM products);
DELETE FROM products;

-- 3. Insert 260 products and their primary images
WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Apple iPhone 15 Pro Max 256GB Titanium Blue',
    'Forged in aerospace-grade titanium with the powerful A17 Pro chip and a versatile 48MP triple camera system.',
    148900.00,
    159900.00,
    '{"Display":"6.7\" Super Retina XDR OLED","Processor":"A17 Pro (3nm)","Storage":"256GB NVMe","Camera":"48MP + 12MP + 12MP","Battery":"4441 mAh","Weight":"221g"}',
    45,
    'ELEC-APPL-001',
    'Apple',
    4.0,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Samsung Galaxy S24 Ultra 5G 512GB Titanium Gray',
    'Meet Galaxy S24 Ultra with Galaxy AI, titanium shield, built-in S Pen, and 200MP Quad Telephoto camera.',
    139999.00,
    149999.00,
    '{"Display":"6.8\" Dynamic AMOLED 2X 120Hz","Processor":"Snapdragon 8 Gen 3","RAM":"12GB LPDDR5X","Storage":"512GB UFS 4.0","Battery":"5000 mAh 45W","Stylus":"Embedded S-Pen"}',
    50,
    'ELEC-SMSG-002',
    'Samsung',
    4.1,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Apple MacBook Pro 16" M3 Max 36GB 1TB Space Black',
    'The most advanced Mac laptop ever with Apple M3 Max 16-core CPU, 40-core GPU, and Liquid Retina XDR display.',
    349900.00,
    379900.00,
    '{"Display":"16.2\" Liquid Retina XDR 120Hz","Processor":"Apple M3 Max (16-core CPU, 40-core GPU)","Memory":"36GB Unified Memory","Storage":"1TB PCIe Gen4 SSD","Battery":"Up to 22 hours","Ports":"3x Thunderbolt 4, HDMI, MagSafe 3"}',
    20,
    'ELEC-APPL-003',
    'Apple',
    4.2,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Dell XPS 16 9640 Core Ultra 7 32GB RTX 4070',
    'Sculpted in CNC machined aluminum and Gorilla Glass 3 with 4K OLED touch display and NVIDIA GeForce RTX 4070 graphics.',
    269990.00,
    289990.00,
    '{"Display":"16.3\" 4K+ (3840x2400) OLED Touch","Processor":"Intel Core Ultra 7 155H","Graphics":"NVIDIA GeForce RTX 4070 8GB","RAM":"32GB LPDDR5x","Storage":"1TB NVMe SSD","Weight":"2.13 kg"}',
    25,
    'ELEC-DELL-004',
    'Dell',
    4.3,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Sony Bravia XR 65" 4K HDR QD-OLED Google TV',
    'Cognitive Processor XR delivers unprecedented contrast and pure black levels with Quantum Dot OLED color technology.',
    249900.00,
    289900.00,
    '{"Screen Size":"65 inches","Panel Type":"QD-OLED 120Hz","Processor":"Cognitive Processor XR","Audio":"Acoustic Surface Audio+ (60W)","Gaming":"HDMI 2.1 4K 120Hz VRR ALLM","OS":"Google TV"}',
    15,
    'ELEC-SONY-005',
    'Sony',
    4.4,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'LG OLED evo C3 55" 4K Smart TV',
    'Self-lit OLED pixels paired with the alpha 9 AI Processor Gen6 for sensational picture quality and ultra-fast 0.1ms response gaming.',
    124990.00,
    159990.00,
    '{"Screen Size":"55 inches","Resolution":"4K Ultra HD (3840x2160)","Refresh Rate":"120Hz Native","HDR":"Dolby Vision, HDR10, HLG","Gaming":"G-Sync, FreeSync, 0.1ms response","OS":"webOS 23"}',
    30,
    'ELEC-LG-006',
    'LG',
    4.5,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1577979749830-f1d742b96791?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Apple iPad Pro 12.9" M2 Liquid Retina XDR 256GB Wi-Fi',
    'Astounding performance with Apple M2 chip, 12.9-inch Liquid Retina XDR mini-LED display, and Apple Pencil hover technology.',
    112900.00,
    122900.00,
    '{"Display":"12.9\" Liquid Retina XDR mini-LED (1600 nits peak)","Chip":"Apple M2 (8-core CPU, 10-core GPU)","Storage":"256GB","Biometrics":"Face ID","Connectivity":"Wi-Fi 6E, Thunderbolt 4","Weight":"682g"}',
    35,
    'ELEC-APPL-007',
    'Apple',
    4.6,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Asus ROG Zephyrus G16 OLED 240Hz Intel Core Ultra 9',
    'Ultra-thin precision aluminum chassis with 2.5K 240Hz ROG Nebula OLED display, RTX 4080 graphics, and Intel Core Ultra 9 185H.',
    219990.00,
    249990.00,
    '{"Display":"16\" 2.5K (2560x1600) 240Hz OLED","Processor":"Intel Core Ultra 9 185H","GPU":"NVIDIA GeForce RTX 4080 12GB","RAM":"32GB LPDDR5X","Storage":"1TB PCIe 4.0 SSD","Weight":"1.85 kg"}',
    18,
    'ELEC-ASUS-008',
    'Asus',
    4.7,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'HP Spectre x360 2-in-1 14" Core Ultra 7 32GB 1TB',
    'Convertible laptop featuring 2.8K OLED 120Hz IMAX Enhanced touch screen with 9MP AI camera and Poly Studio quad audio.',
    164990.00,
    184990.00,
    '{"Display":"14\" 2.8K (2880x1800) OLED Touch 120Hz","CPU":"Intel Core Ultra 7 155H","RAM":"32GB LPDDR5x","Storage":"1TB Gen4 NVMe SSD","Audio":"Quad speakers tuned by Poly Studio","Weight":"1.44 kg"}',
    22,
    'ELEC-HP-009',
    'HP',
    4.8,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Lenovo ThinkPad X1 Carbon Gen 12 Ultrabook',
    'Ultralight executive business flagship forged from carbon fiber and recycled magnesium with 2.8K OLED display.',
    189990.00,
    209990.00,
    '{"Display":"14\" 2.8K OLED 120Hz 400 nits","Processor":"Intel Core Ultra 7 155U vPro","RAM":"32GB LPDDR5X","Storage":"1TB PCIe Performance SSD","Battery":"57Wh with Rapid Charge","Weight":"1.09 kg"}',
    25,
    'ELEC-LENV-010',
    'Lenovo',
    4.9,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1541807084-5c52b6b3adef?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'OnePlus 12 5G 16GB 512GB Silky Black',
    'Flagship killer powered by Snapdragon 8 Gen 3, 4th Gen Hasselblad Camera System, and 100W SUPERVOOC charging.',
    69999.00,
    74999.00,
    '{"Display":"6.82\" 2K ProXDR 120Hz LTPO (4500 nits peak)","Processor":"Snapdragon 8 Gen 3","RAM":"16GB LPDDR5X","Storage":"512GB UFS 4.0","Battery":"5400 mAh (100W Wired, 50W Wireless)"}',
    60,
    'ELEC-OPLS-011',
    'OnePlus',
    4.0,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Google Pixel 8 Pro 256GB Obsidian',
    'The AI flagship phone engineered by Google with Tensor G3 chip, fully upgraded triple camera system, and temperature sensor.',
    106999.00,
    114999.00,
    '{"Display":"6.7\" Super Actua LTPO OLED 120Hz","Chip":"Google Tensor G3 + Titan M2","RAM":"12GB LPDDR5X","Cameras":"50MP Main + 48MP Ultrawide + 48MP Telephoto 5x","OS Support":"7 Years OS & Security updates"}',
    35,
    'ELEC-GOOG-012',
    'Google',
    4.1,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1592899677977-9c10ca588bbd?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Sony PlayStation 5 Slim Console Disc Edition',
    'PlayStation 5 Slim console packed with 1TB SSD storage, haptic feedback DualSense controller, and 4K 120Hz ray tracing graphics.',
    54990.00,
    59990.00,
    '{"CPU":"AMD Zen 2 8-core 3.5GHz","GPU":"AMD RDNA 2 10.3 TFLOPs","Storage":"1TB Custom High-Speed NVMe SSD (5.5 GB/s)","Optical Drive":"Ultra HD Blu-ray","Video":"HDMI 2.1 4K 120Hz & 8K output"}',
    70,
    'ELEC-SONY-013',
    'Sony',
    4.2,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1606813907291-d86efa9b94db?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Microsoft Xbox Series X 1TB Gaming Console',
    'The fastest, most powerful Xbox ever delivering true 4K gaming, 120 FPS, 12 teraflops of raw graphic processing power, and Quick Resume.',
    52990.00,
    57990.00,
    '{"Processor":"Custom AMD 8-core Zen 2 3.8GHz","Graphics":"12 TFLOPS 52 CUs @ 1.825 GHz RDNA 2","Memory":"16GB GDDR6","Storage":"1TB Custom NVMe SSD","Drive":"4K UHD Blu-Ray"}',
    40,
    'ELEC-MSFT-014',
    'Microsoft',
    4.3,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1621259182978-fbf93132d53d?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Canon EOS R6 Mark II Mirrorless Camera Body',
    '24.2MP full-frame CMOS sensor with up to 40 fps electronic shutter, 6K oversampled 4K 60p video, and in-body 8-stop image stabilization.',
    215995.00,
    243995.00,
    '{"Sensor":"24.2MP Full-Frame CMOS","Processor":"DIGIC X","Continuous Shooting":"Up to 40 fps","Video":"6K 60p RAW External, 4K 60p 10-Bit Internal","Stabilization":"5-axis In-Body (up to 8 stops with IS lenses)"}',
    12,
    'ELEC-CANN-015',
    'Canon',
    4.4,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Sony Alpha 7 IV Full-Frame Hybrid Camera Body',
    'Groundbreaking 33MP Exmor R back-illuminated sensor, BIONZ XR processing engine, 4K 60p 10-bit 4:2:2 recording, and Real-time Eye AF.',
    219990.00,
    242990.00,
    '{"Sensor":"33MP Full-Frame Exmor R CMOS","Autofocus":"759 phase-detection points (94% coverage)","Video":"4K 60p 10-bit 4:2:2 S-Cinetone","ISO Range":"100-51200 (expandable 50-204800)","Card Slots":"Dual (CFexpress Type A / SD UHS-II)"}',
    15,
    'ELEC-SONY-016',
    'Sony',
    4.5,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1502920917128-1aa500764cbd?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Fujifilm X-T5 Mirrorless Camera with 16-80mm Kit',
    'Classic dial-based styling meets 40.2MP X-Trans CMOS 5 HR sensor with 5-axis 7.0-stop in-body stabilization and 6.2K video.',
    199999.00,
    219999.00,
    '{"Sensor":"40.2MP X-Trans CMOS 5 HR","Processor":"X-Processor 5","Stabilization":"7.0 stops IBIS","Video":"6.2K/30p 4:2:2 10-Bit Internal","Film Simulations":"19 including Nostalgic Neg"}',
    10,
    'ELEC-FUJI-017',
    'Fujifilm',
    4.6,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1512790182412-b19e6d62bc39?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'DJI Mavic 3 Pro Cine Drone with RC Pro Controller',
    'Triple-camera system featuring 4/3 CMOS Hasselblad camera, dual telephoto lenses, Apple ProRes encoding, and 43-min flight time.',
    369900.00,
    399900.00,
    '{"Main Camera":"4/3 CMOS Hasselblad 20MP (5.1K/50fps)","Telephoto":"70mm f/2.8 & 166mm f/3.4","Video Codec":"Apple ProRes 422 HQ","Max Flight Time":"43 minutes","Transmission":"DJI O3+ up to 15 km"}',
    8,
    'ELEC-DJI-018',
    'DJI',
    4.7,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1508614589041-895b88991e3e?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Samsung 49" Odyssey OLED G9 Curved Gaming Monitor',
    'Dual QHD 1800R curved gaming screen powered by Neo Quantum Processor Pro with 0.03ms response time and 240Hz refresh rate.',
    149990.00,
    179990.00,
    '{"Screen Size":"49 inches 32:9","Resolution":"Dual QHD (5120 x 1440)","Panel":"OLED with Neo Quantum Processor Pro","Refresh Rate":"240Hz","Response Time":"0.03ms (GtG)","Certification":"DisplayHDR True Black 400"}',
    14,
    'ELEC-SMSG-019',
    'Samsung',
    4.8,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'LG 38" UltraWide Curved Nano IPS Monitor',
    'WQHD+ (3840x1600) 21:9 curved workspace monitor with Nano IPS panel, 98% DCI-P3 color gamut, and Thunderbolt 3 connectivity.',
    119999.00,
    139999.00,
    '{"Screen Size":"37.5 inches","Resolution":"WQHD+ (3840 x 1600)","Panel":"Curved Nano IPS","Color Space":"DCI-P3 98%","Brightness":"450 nits (HDR 600)","Ports":"Thunderbolt 3 (94W PD), 2x HDMI, DisplayPort"}',
    16,
    'ELEC-LG-020',
    'LG',
    4.9,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1586210579191-33b45e38fa2c?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Sony Ultra Slim 4K Smart Monitor 2024',
    'Ultra-thin high precision 4K display engineered by Sony with USB-C 90W PD charging.',
    42999.00,
    49449.00,
    '{"Resolution":"3840x2160","Refresh Rate":"60Hz","Panel":"IPS HDR400","Ports":"USB-C, HDMI 2.1"}',
    15,
    'ELEC-SONY-021',
    'Sony',
    4.0,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1547119957-637f8679db1e?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Samsung Studio Wireless ANC Headset 2025',
    'Acoustically tuned studio headphones by Samsung with hybrid active noise cancellation.',
    26690.00,
    31761.00,
    '{"Drivers":"40mm Beryllium","Battery":"35 Hours","Bluetooth":"5.3 LDAC","Weight":"260g"}',
    22,
    'ELEC-SAMS-022',
    'Samsung',
    4.1,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Apple Compact Creator Mirrorless Camera 2024',
    'Lightweight creator camera by Apple with 4K 60p 10-bit recording and subject tracking AF.',
    90390.00,
    111180.00,
    '{"Sensor":"26MP APS-C","Autofocus":"AI Subject Detect","Video":"4K 60p","Audio":"Dual stereo mics"}',
    29,
    'ELEC-APPL-023',
    'Apple',
    4.2,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Dell Pro Gaming Mechanical Keyboard 2025',
    'Aircraft-grade aluminum chassis with custom hot-swappable optical mechanical switches by Dell.',
    17099.00,
    21716.00,
    '{"Switches":"Linear Optical","Polling Rate":"8000Hz","Lighting":"Per-Key RGB","Connectivity":"Tri-Mode Wireless"}',
    36,
    'ELEC-DELL-024',
    'Dell',
    4.3,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'HP Wi-Fi 7 Tri-Band Mesh Router 2024',
    'Ultra-fast Wi-Fi 7 mesh system by HP with multi-gigabit speeds and dedicated backhaul.',
    33790.00,
    44265.00,
    '{"Speed":"Up to 19 Gbps","Bands":"2.4GHz + 5GHz + 6GHz","Coverage":"Up to 6000 sq ft","Ports":"10GbE WAN + 4x 2.5GbE"}',
    43,
    'ELEC-HP-025',
    'HP',
    4.4,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1544197150-b99a580bb7a8?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Lenovo True Wireless Earbuds Pro 2025',
    'Spatial audio earbuds by Lenovo with intelligent adaptive noise reduction and wireless case.',
    21490.00,
    24713.00,
    '{"Drivers":"11mm Dual Driver","Battery":"8h + 24h case","Water Resistance":"IP54","Codec":"LHDC 5.0"}',
    50,
    'ELEC-LENO-026',
    'Lenovo',
    4.5,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Asus Ultra Slim 4K Smart Monitor 2024',
    'Ultra-thin high precision 4K display engineered by Asus with USB-C 90W PD charging.',
    44199.00,
    52597.00,
    '{"Resolution":"3840x2160","Refresh Rate":"60Hz","Panel":"IPS HDR400","Ports":"USB-C, HDMI 2.1"}',
    57,
    'ELEC-ASUS-027',
    'Asus',
    4.6,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1547119957-637f8679db1e?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Bose Studio Wireless ANC Headset 2025',
    'Acoustically tuned studio headphones by Bose with hybrid active noise cancellation.',
    27890.00,
    34305.00,
    '{"Drivers":"40mm Beryllium","Battery":"35 Hours","Bluetooth":"5.3 LDAC","Weight":"260g"}',
    64,
    'ELEC-BOSE-028',
    'Bose',
    4.7,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Sennheiser Compact Creator Mirrorless Camera 2024',
    'Lightweight creator camera by Sennheiser with 4K 60p 10-bit recording and subject tracking AF.',
    91590.00,
    116319.00,
    '{"Sensor":"26MP APS-C","Autofocus":"AI Subject Detect","Video":"4K 60p","Audio":"Dual stereo mics"}',
    71,
    'ELEC-SENN-029',
    'Sennheiser',
    4.8,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'LG Pro Gaming Mechanical Keyboard 2025',
    'Aircraft-grade aluminum chassis with custom hot-swappable optical mechanical switches by LG.',
    15299.00,
    20042.00,
    '{"Switches":"Linear Optical","Polling Rate":"8000Hz","Lighting":"Per-Key RGB","Connectivity":"Tri-Mode Wireless"}',
    78,
    'ELEC-LG-030',
    'LG',
    4.9,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Sony Wi-Fi 7 Tri-Band Mesh Router 2024',
    'Ultra-fast Wi-Fi 7 mesh system by Sony with multi-gigabit speeds and dedicated backhaul.',
    34990.00,
    40239.00,
    '{"Speed":"Up to 19 Gbps","Bands":"2.4GHz + 5GHz + 6GHz","Coverage":"Up to 6000 sq ft","Ports":"10GbE WAN + 4x 2.5GbE"}',
    20,
    'ELEC-SONY-031',
    'Sony',
    4.0,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1544197150-b99a580bb7a8?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Samsung True Wireless Earbuds Pro 2025',
    'Spatial audio earbuds by Samsung with intelligent adaptive noise reduction and wireless case.',
    19690.00,
    23431.00,
    '{"Drivers":"11mm Dual Driver","Battery":"8h + 24h case","Water Resistance":"IP54","Codec":"LHDC 5.0"}',
    27,
    'ELEC-SAMS-032',
    'Samsung',
    4.1,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Apple Ultra Slim 4K Smart Monitor 2024',
    'Ultra-thin high precision 4K display engineered by Apple with USB-C 90W PD charging.',
    45399.00,
    55841.00,
    '{"Resolution":"3840x2160","Refresh Rate":"60Hz","Panel":"IPS HDR400","Ports":"USB-C, HDMI 2.1"}',
    34,
    'ELEC-APPL-033',
    'Apple',
    4.2,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1547119957-637f8679db1e?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Dell Studio Wireless ANC Headset 2025',
    'Acoustically tuned studio headphones by Dell with hybrid active noise cancellation.',
    26090.00,
    33134.00,
    '{"Drivers":"40mm Beryllium","Battery":"35 Hours","Bluetooth":"5.3 LDAC","Weight":"260g"}',
    41,
    'ELEC-DELL-034',
    'Dell',
    4.3,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'HP Compact Creator Mirrorless Camera 2024',
    'Lightweight creator camera by HP with 4K 60p 10-bit recording and subject tracking AF.',
    92790.00,
    121555.00,
    '{"Sensor":"26MP APS-C","Autofocus":"AI Subject Detect","Video":"4K 60p","Audio":"Dual stereo mics"}',
    48,
    'ELEC-HP-035',
    'HP',
    4.4,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Lenovo Pro Gaming Mechanical Keyboard 2025',
    'Aircraft-grade aluminum chassis with custom hot-swappable optical mechanical switches by Lenovo.',
    16499.00,
    18974.00,
    '{"Switches":"Linear Optical","Polling Rate":"8000Hz","Lighting":"Per-Key RGB","Connectivity":"Tri-Mode Wireless"}',
    55,
    'ELEC-LENO-036',
    'Lenovo',
    4.5,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Asus Wi-Fi 7 Tri-Band Mesh Router 2024',
    'Ultra-fast Wi-Fi 7 mesh system by Asus with multi-gigabit speeds and dedicated backhaul.',
    33190.00,
    39496.00,
    '{"Speed":"Up to 19 Gbps","Bands":"2.4GHz + 5GHz + 6GHz","Coverage":"Up to 6000 sq ft","Ports":"10GbE WAN + 4x 2.5GbE"}',
    62,
    'ELEC-ASUS-037',
    'Asus',
    4.6,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1544197150-b99a580bb7a8?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Bose True Wireless Earbuds Pro 2025',
    'Spatial audio earbuds by Bose with intelligent adaptive noise reduction and wireless case.',
    20890.00,
    25695.00,
    '{"Drivers":"11mm Dual Driver","Battery":"8h + 24h case","Water Resistance":"IP54","Codec":"LHDC 5.0"}',
    69,
    'ELEC-BOSE-038',
    'Bose',
    4.7,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Sennheiser Ultra Slim 4K Smart Monitor 2024',
    'Ultra-thin high precision 4K display engineered by Sennheiser with USB-C 90W PD charging.',
    43599.00,
    55371.00,
    '{"Resolution":"3840x2160","Refresh Rate":"60Hz","Panel":"IPS HDR400","Ports":"USB-C, HDMI 2.1"}',
    76,
    'ELEC-SENN-039',
    'Sennheiser',
    4.8,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1547119957-637f8679db1e?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'LG Studio Wireless ANC Headset 2025',
    'Acoustically tuned studio headphones by LG with hybrid active noise cancellation.',
    27290.00,
    35750.00,
    '{"Drivers":"40mm Beryllium","Battery":"35 Hours","Bluetooth":"5.3 LDAC","Weight":"260g"}',
    18,
    'ELEC-LG-040',
    'LG',
    4.9,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Sony Compact Creator Mirrorless Camera 2024',
    'Lightweight creator camera by Sony with 4K 60p 10-bit recording and subject tracking AF.',
    90990.00,
    104638.00,
    '{"Sensor":"26MP APS-C","Autofocus":"AI Subject Detect","Video":"4K 60p","Audio":"Dual stereo mics"}',
    25,
    'ELEC-SONY-041',
    'Sony',
    4.0,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Samsung Pro Gaming Mechanical Keyboard 2025',
    'Aircraft-grade aluminum chassis with custom hot-swappable optical mechanical switches by Samsung.',
    17699.00,
    21062.00,
    '{"Switches":"Linear Optical","Polling Rate":"8000Hz","Lighting":"Per-Key RGB","Connectivity":"Tri-Mode Wireless"}',
    32,
    'ELEC-SAMS-042',
    'Samsung',
    4.1,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Apple Wi-Fi 7 Tri-Band Mesh Router 2024',
    'Ultra-fast Wi-Fi 7 mesh system by Apple with multi-gigabit speeds and dedicated backhaul.',
    34390.00,
    42300.00,
    '{"Speed":"Up to 19 Gbps","Bands":"2.4GHz + 5GHz + 6GHz","Coverage":"Up to 6000 sq ft","Ports":"10GbE WAN + 4x 2.5GbE"}',
    39,
    'ELEC-APPL-043',
    'Apple',
    4.2,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1544197150-b99a580bb7a8?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Dell True Wireless Earbuds Pro 2025',
    'Spatial audio earbuds by Dell with intelligent adaptive noise reduction and wireless case.',
    19090.00,
    24244.00,
    '{"Drivers":"11mm Dual Driver","Battery":"8h + 24h case","Water Resistance":"IP54","Codec":"LHDC 5.0"}',
    46,
    'ELEC-DELL-044',
    'Dell',
    4.3,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'HP Ultra Slim 4K Smart Monitor 2024',
    'Ultra-thin high precision 4K display engineered by HP with USB-C 90W PD charging.',
    44799.00,
    58687.00,
    '{"Resolution":"3840x2160","Refresh Rate":"60Hz","Panel":"IPS HDR400","Ports":"USB-C, HDMI 2.1"}',
    53,
    'ELEC-HP-045',
    'HP',
    4.4,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1547119957-637f8679db1e?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Lenovo Studio Wireless ANC Headset 2025',
    'Acoustically tuned studio headphones by Lenovo with hybrid active noise cancellation.',
    25490.00,
    29313.00,
    '{"Drivers":"40mm Beryllium","Battery":"35 Hours","Bluetooth":"5.3 LDAC","Weight":"260g"}',
    60,
    'ELEC-LENO-046',
    'Lenovo',
    4.5,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Asus Compact Creator Mirrorless Camera 2024',
    'Lightweight creator camera by Asus with 4K 60p 10-bit recording and subject tracking AF.',
    92190.00,
    109706.00,
    '{"Sensor":"26MP APS-C","Autofocus":"AI Subject Detect","Video":"4K 60p","Audio":"Dual stereo mics"}',
    67,
    'ELEC-ASUS-047',
    'Asus',
    4.6,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Bose Pro Gaming Mechanical Keyboard 2025',
    'Aircraft-grade aluminum chassis with custom hot-swappable optical mechanical switches by Bose.',
    15899.00,
    19556.00,
    '{"Switches":"Linear Optical","Polling Rate":"8000Hz","Lighting":"Per-Key RGB","Connectivity":"Tri-Mode Wireless"}',
    74,
    'ELEC-BOSE-048',
    'Bose',
    4.7,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'Sennheiser Wi-Fi 7 Tri-Band Mesh Router 2024',
    'Ultra-fast Wi-Fi 7 mesh system by Sennheiser with multi-gigabit speeds and dedicated backhaul.',
    35590.00,
    45199.00,
    '{"Speed":"Up to 19 Gbps","Bands":"2.4GHz + 5GHz + 6GHz","Coverage":"Up to 6000 sq ft","Ports":"10GbE WAN + 4x 2.5GbE"}',
    16,
    'ELEC-SENN-049',
    'Sennheiser',
    4.8,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1544197150-b99a580bb7a8?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Electronics'),
    'LG True Wireless Earbuds Pro 2025',
    'Spatial audio earbuds by LG with intelligent adaptive noise reduction and wireless case.',
    20290.00,
    26580.00,
    '{"Drivers":"11mm Dual Driver","Battery":"8h + 24h case","Water Resistance":"IP54","Codec":"LHDC 5.0"}',
    23,
    'ELEC-LG-050',
    'LG',
    4.9,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Nike Tailored Slim Fit Wool Blazer 2024',
    'Expertly tailored premium Italian wool blazer crafted by Nike for formal and evening events.',
    14990.00,
    17239.00,
    '{"Material":"100% Virgin Wool","Lining":"Cupro Silk","Fit":"Tailored Slim","Closure":"Two-Button"}',
    15,
    'FASH-NIKE-001',
    'Nike',
    4.0,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1594938298603-c8148c4dae35?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Adidas Classic 501 Original Fit Denim 2025',
    'The iconic straight leg denim jeans by Adidas with signature copper rivets and button fly.',
    6699.00,
    7972.00,
    '{"Fabric":"100% Cotton Selvedge Denim","Rise":"Mid Rise","Leg":"Straight","Wash":"Dark Indigo Stone Wash"}',
    22,
    'FASH-ADID-002',
    'Adidas',
    4.1,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1542272604-780c96856592?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Puma Ultraboost Running Sneakers 2024',
    'High-performance energy return running shoes engineered by Puma with breathable knit upper.',
    17399.00,
    21401.00,
    '{"Cushioning":"Full-Length Boost Foam","Upper":"Primeknit+","Outsole":"Continental Natural Rubber","Weight":"290g"}',
    29,
    'FASH-PUMA-003',
    'Puma',
    4.2,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Levi''s Oversized Double-Breasted Trench Coat 2025',
    'Timeless weather-resistant gabardine trench coat by Levi''s featuring storm flap and belted waist.',
    14590.00,
    18529.00,
    '{"Fabric":"Water-Repellent Cotton Gabardine","Length":"Midi","Details":"Horn Buttons, Buckled Cuffs"}',
    36,
    'FASH-LEVI-004',
    'Levi''s',
    4.3,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1544441893-675973e31985?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'H&M Pure Cashmere Crewneck Sweater 2024',
    'Sumptuously soft grade-A Mongolian cashmere sweater by H&M offering weightless warmth.',
    12799.00,
    16767.00,
    '{"Material":"100% Pure Cashmere (2-Ply)","Neckline":"Ribbed Crewneck","Care":"Dry Clean or Hand Wash"}',
    43,
    'FASH-H&M-005',
    'H&M',
    4.4,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1576871337632-b9aef4c17ab9?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Zara Vintage Suede Leather Bomber Jacket 2025',
    'Hand-finished goat suede bomber jacket from Zara with satin lining and heavy gauge brass zippers.',
    25490.00,
    29313.00,
    '{"Shell":"100% Genuine Goat Suede","Lining":"100% Viscose","Trim":"Wool Ribbed Hem and Collar"}',
    50,
    'FASH-ZARA-006',
    'Zara',
    4.5,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1551028719-00167b16eac5?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Tommy Hilfiger Tech Fleece Full-Zip Windrunner Hoodie 2024',
    'Double-sided smooth spacer fleece engineered by Tommy Hilfiger for premium warmth without the bulk.',
    9195.00,
    10942.00,
    '{"Material":"66% Cotton, 34% Polyester Tech Fleece","Pockets":"Zippered Sleeve & Kangaroo Pockets","Fit":"Standard Athletic"}',
    57,
    'FASH-TOMM-007',
    'Tommy Hilfiger',
    4.6,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Calvin Klein Pure Mulberry Silk Slip Dress 2025',
    'Effortlessly elegant bias-cut mulberry silk slip dress by Calvin Klein with delicate adjustable straps.',
    12399.00,
    15251.00,
    '{"Fabric":"100% 19 Momme Mulberry Silk","Cut":"Bias Cut","Finish":"French Seams","Length":"Midi"}',
    64,
    'FASH-CALV-008',
    'Calvin Klein',
    4.7,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Under Armour Oxford Cotton Button-Down Shirt 2024',
    'Durable woven pinpoint Oxford cotton casual dress shirt from Under Armour with mother-of-pearl buttons.',
    5099.00,
    6476.00,
    '{"Weave":"100% Combed Oxford Cotton","Collar":"Button-Down Casual","Pattern":"Solid White & Sky Blue"}',
    71,
    'FASH-UNDE-009',
    'Under Armour',
    4.8,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Ralph Lauren Waterproof Winter Down Parka Jacket 2025',
    'Extreme cold weather winter parka by Ralph Lauren insulated with 700-fill power responsibly sourced down.',
    19290.00,
    25270.00,
    '{"Insulation":"700 Fill Power Goose Down","Shell":"2-Layer DryVent Waterproof","Hood":"Removable Faux Fur"}',
    78,
    'FASH-RALP-010',
    'Ralph Lauren',
    4.9,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1539533018447-63fcce667823?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Nike Tailored Slim Fit Wool Blazer 2024',
    'Expertly tailored premium Italian wool blazer crafted by Nike for formal and evening events.',
    16990.00,
    19539.00,
    '{"Material":"100% Virgin Wool","Lining":"Cupro Silk","Fit":"Tailored Slim","Closure":"Two-Button"}',
    20,
    'FASH-NIKE-011',
    'Nike',
    4.0,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1594938298603-c8148c4dae35?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Adidas Classic 501 Original Fit Denim 2025',
    'The iconic straight leg denim jeans by Adidas with signature copper rivets and button fly.',
    5699.00,
    6782.00,
    '{"Fabric":"100% Cotton Selvedge Denim","Rise":"Mid Rise","Leg":"Straight","Wash":"Dark Indigo Stone Wash"}',
    27,
    'FASH-ADID-012',
    'Adidas',
    4.1,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1542272604-780c96856592?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Puma Ultraboost Running Sneakers 2024',
    'High-performance energy return running shoes engineered by Puma with breathable knit upper.',
    19399.00,
    23861.00,
    '{"Cushioning":"Full-Length Boost Foam","Upper":"Primeknit+","Outsole":"Continental Natural Rubber","Weight":"290g"}',
    34,
    'FASH-PUMA-013',
    'Puma',
    4.2,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Levi''s Oversized Double-Breasted Trench Coat 2025',
    'Timeless weather-resistant gabardine trench coat by Levi''s featuring storm flap and belted waist.',
    13590.00,
    17259.00,
    '{"Fabric":"Water-Repellent Cotton Gabardine","Length":"Midi","Details":"Horn Buttons, Buckled Cuffs"}',
    41,
    'FASH-LEVI-014',
    'Levi''s',
    4.3,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1544441893-675973e31985?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'H&M Pure Cashmere Crewneck Sweater 2024',
    'Sumptuously soft grade-A Mongolian cashmere sweater by H&M offering weightless warmth.',
    14799.00,
    19387.00,
    '{"Material":"100% Pure Cashmere (2-Ply)","Neckline":"Ribbed Crewneck","Care":"Dry Clean or Hand Wash"}',
    48,
    'FASH-H&M-015',
    'H&M',
    4.4,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1576871337632-b9aef4c17ab9?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Zara Vintage Suede Leather Bomber Jacket 2025',
    'Hand-finished goat suede bomber jacket from Zara with satin lining and heavy gauge brass zippers.',
    24490.00,
    28163.00,
    '{"Shell":"100% Genuine Goat Suede","Lining":"100% Viscose","Trim":"Wool Ribbed Hem and Collar"}',
    55,
    'FASH-ZARA-016',
    'Zara',
    4.5,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1551028719-00167b16eac5?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Tommy Hilfiger Tech Fleece Full-Zip Windrunner Hoodie 2024',
    'Double-sided smooth spacer fleece engineered by Tommy Hilfiger for premium warmth without the bulk.',
    8195.00,
    9752.00,
    '{"Material":"66% Cotton, 34% Polyester Tech Fleece","Pockets":"Zippered Sleeve & Kangaroo Pockets","Fit":"Standard Athletic"}',
    62,
    'FASH-TOMM-017',
    'Tommy Hilfiger',
    4.6,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Calvin Klein Pure Mulberry Silk Slip Dress 2025',
    'Effortlessly elegant bias-cut mulberry silk slip dress by Calvin Klein with delicate adjustable straps.',
    11399.00,
    14021.00,
    '{"Fabric":"100% 19 Momme Mulberry Silk","Cut":"Bias Cut","Finish":"French Seams","Length":"Midi"}',
    69,
    'FASH-CALV-018',
    'Calvin Klein',
    4.7,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Under Armour Oxford Cotton Button-Down Shirt 2024',
    'Durable woven pinpoint Oxford cotton casual dress shirt from Under Armour with mother-of-pearl buttons.',
    4099.00,
    5206.00,
    '{"Weave":"100% Combed Oxford Cotton","Collar":"Button-Down Casual","Pattern":"Solid White & Sky Blue"}',
    76,
    'FASH-UNDE-019',
    'Under Armour',
    4.8,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Ralph Lauren Waterproof Winter Down Parka Jacket 2025',
    'Extreme cold weather winter parka by Ralph Lauren insulated with 700-fill power responsibly sourced down.',
    21290.00,
    27890.00,
    '{"Insulation":"700 Fill Power Goose Down","Shell":"2-Layer DryVent Waterproof","Hood":"Removable Faux Fur"}',
    18,
    'FASH-RALP-020',
    'Ralph Lauren',
    4.9,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1539533018447-63fcce667823?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Nike Tailored Slim Fit Wool Blazer 2024',
    'Expertly tailored premium Italian wool blazer crafted by Nike for formal and evening events.',
    15990.00,
    18389.00,
    '{"Material":"100% Virgin Wool","Lining":"Cupro Silk","Fit":"Tailored Slim","Closure":"Two-Button"}',
    25,
    'FASH-NIKE-021',
    'Nike',
    4.0,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1594938298603-c8148c4dae35?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Adidas Classic 501 Original Fit Denim 2025',
    'The iconic straight leg denim jeans by Adidas with signature copper rivets and button fly.',
    7699.00,
    9162.00,
    '{"Fabric":"100% Cotton Selvedge Denim","Rise":"Mid Rise","Leg":"Straight","Wash":"Dark Indigo Stone Wash"}',
    32,
    'FASH-ADID-022',
    'Adidas',
    4.1,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1542272604-780c96856592?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Puma Ultraboost Running Sneakers 2024',
    'High-performance energy return running shoes engineered by Puma with breathable knit upper.',
    18399.00,
    22631.00,
    '{"Cushioning":"Full-Length Boost Foam","Upper":"Primeknit+","Outsole":"Continental Natural Rubber","Weight":"290g"}',
    39,
    'FASH-PUMA-023',
    'Puma',
    4.2,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Levi''s Oversized Double-Breasted Trench Coat 2025',
    'Timeless weather-resistant gabardine trench coat by Levi''s featuring storm flap and belted waist.',
    12590.00,
    15989.00,
    '{"Fabric":"Water-Repellent Cotton Gabardine","Length":"Midi","Details":"Horn Buttons, Buckled Cuffs"}',
    46,
    'FASH-LEVI-024',
    'Levi''s',
    4.3,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1544441893-675973e31985?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'H&M Pure Cashmere Crewneck Sweater 2024',
    'Sumptuously soft grade-A Mongolian cashmere sweater by H&M offering weightless warmth.',
    13799.00,
    18077.00,
    '{"Material":"100% Pure Cashmere (2-Ply)","Neckline":"Ribbed Crewneck","Care":"Dry Clean or Hand Wash"}',
    53,
    'FASH-H&M-025',
    'H&M',
    4.4,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1576871337632-b9aef4c17ab9?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Zara Vintage Suede Leather Bomber Jacket 2025',
    'Hand-finished goat suede bomber jacket from Zara with satin lining and heavy gauge brass zippers.',
    23490.00,
    27013.00,
    '{"Shell":"100% Genuine Goat Suede","Lining":"100% Viscose","Trim":"Wool Ribbed Hem and Collar"}',
    60,
    'FASH-ZARA-026',
    'Zara',
    4.5,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1551028719-00167b16eac5?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Tommy Hilfiger Tech Fleece Full-Zip Windrunner Hoodie 2024',
    'Double-sided smooth spacer fleece engineered by Tommy Hilfiger for premium warmth without the bulk.',
    10195.00,
    12132.00,
    '{"Material":"66% Cotton, 34% Polyester Tech Fleece","Pockets":"Zippered Sleeve & Kangaroo Pockets","Fit":"Standard Athletic"}',
    67,
    'FASH-TOMM-027',
    'Tommy Hilfiger',
    4.6,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Calvin Klein Pure Mulberry Silk Slip Dress 2025',
    'Effortlessly elegant bias-cut mulberry silk slip dress by Calvin Klein with delicate adjustable straps.',
    10399.00,
    12791.00,
    '{"Fabric":"100% 19 Momme Mulberry Silk","Cut":"Bias Cut","Finish":"French Seams","Length":"Midi"}',
    74,
    'FASH-CALV-028',
    'Calvin Klein',
    4.7,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Under Armour Oxford Cotton Button-Down Shirt 2024',
    'Durable woven pinpoint Oxford cotton casual dress shirt from Under Armour with mother-of-pearl buttons.',
    6099.00,
    7746.00,
    '{"Weave":"100% Combed Oxford Cotton","Collar":"Button-Down Casual","Pattern":"Solid White & Sky Blue"}',
    16,
    'FASH-UNDE-029',
    'Under Armour',
    4.8,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Ralph Lauren Waterproof Winter Down Parka Jacket 2025',
    'Extreme cold weather winter parka by Ralph Lauren insulated with 700-fill power responsibly sourced down.',
    20290.00,
    26580.00,
    '{"Insulation":"700 Fill Power Goose Down","Shell":"2-Layer DryVent Waterproof","Hood":"Removable Faux Fur"}',
    23,
    'FASH-RALP-030',
    'Ralph Lauren',
    4.9,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1539533018447-63fcce667823?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Nike Tailored Slim Fit Wool Blazer 2024',
    'Expertly tailored premium Italian wool blazer crafted by Nike for formal and evening events.',
    14990.00,
    17239.00,
    '{"Material":"100% Virgin Wool","Lining":"Cupro Silk","Fit":"Tailored Slim","Closure":"Two-Button"}',
    30,
    'FASH-NIKE-031',
    'Nike',
    4.0,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1594938298603-c8148c4dae35?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Adidas Classic 501 Original Fit Denim 2025',
    'The iconic straight leg denim jeans by Adidas with signature copper rivets and button fly.',
    6699.00,
    7972.00,
    '{"Fabric":"100% Cotton Selvedge Denim","Rise":"Mid Rise","Leg":"Straight","Wash":"Dark Indigo Stone Wash"}',
    37,
    'FASH-ADID-032',
    'Adidas',
    4.1,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1542272604-780c96856592?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Puma Ultraboost Running Sneakers 2024',
    'High-performance energy return running shoes engineered by Puma with breathable knit upper.',
    17399.00,
    21401.00,
    '{"Cushioning":"Full-Length Boost Foam","Upper":"Primeknit+","Outsole":"Continental Natural Rubber","Weight":"290g"}',
    44,
    'FASH-PUMA-033',
    'Puma',
    4.2,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Levi''s Oversized Double-Breasted Trench Coat 2025',
    'Timeless weather-resistant gabardine trench coat by Levi''s featuring storm flap and belted waist.',
    14590.00,
    18529.00,
    '{"Fabric":"Water-Repellent Cotton Gabardine","Length":"Midi","Details":"Horn Buttons, Buckled Cuffs"}',
    51,
    'FASH-LEVI-034',
    'Levi''s',
    4.3,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1544441893-675973e31985?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'H&M Pure Cashmere Crewneck Sweater 2024',
    'Sumptuously soft grade-A Mongolian cashmere sweater by H&M offering weightless warmth.',
    12799.00,
    16767.00,
    '{"Material":"100% Pure Cashmere (2-Ply)","Neckline":"Ribbed Crewneck","Care":"Dry Clean or Hand Wash"}',
    58,
    'FASH-H&M-035',
    'H&M',
    4.4,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1576871337632-b9aef4c17ab9?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Zara Vintage Suede Leather Bomber Jacket 2025',
    'Hand-finished goat suede bomber jacket from Zara with satin lining and heavy gauge brass zippers.',
    25490.00,
    29313.00,
    '{"Shell":"100% Genuine Goat Suede","Lining":"100% Viscose","Trim":"Wool Ribbed Hem and Collar"}',
    65,
    'FASH-ZARA-036',
    'Zara',
    4.5,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1551028719-00167b16eac5?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Tommy Hilfiger Tech Fleece Full-Zip Windrunner Hoodie 2024',
    'Double-sided smooth spacer fleece engineered by Tommy Hilfiger for premium warmth without the bulk.',
    9195.00,
    10942.00,
    '{"Material":"66% Cotton, 34% Polyester Tech Fleece","Pockets":"Zippered Sleeve & Kangaroo Pockets","Fit":"Standard Athletic"}',
    72,
    'FASH-TOMM-037',
    'Tommy Hilfiger',
    4.6,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Calvin Klein Pure Mulberry Silk Slip Dress 2025',
    'Effortlessly elegant bias-cut mulberry silk slip dress by Calvin Klein with delicate adjustable straps.',
    12399.00,
    15251.00,
    '{"Fabric":"100% 19 Momme Mulberry Silk","Cut":"Bias Cut","Finish":"French Seams","Length":"Midi"}',
    79,
    'FASH-CALV-038',
    'Calvin Klein',
    4.7,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Under Armour Oxford Cotton Button-Down Shirt 2024',
    'Durable woven pinpoint Oxford cotton casual dress shirt from Under Armour with mother-of-pearl buttons.',
    5099.00,
    6476.00,
    '{"Weave":"100% Combed Oxford Cotton","Collar":"Button-Down Casual","Pattern":"Solid White & Sky Blue"}',
    21,
    'FASH-UNDE-039',
    'Under Armour',
    4.8,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Ralph Lauren Waterproof Winter Down Parka Jacket 2025',
    'Extreme cold weather winter parka by Ralph Lauren insulated with 700-fill power responsibly sourced down.',
    19290.00,
    25270.00,
    '{"Insulation":"700 Fill Power Goose Down","Shell":"2-Layer DryVent Waterproof","Hood":"Removable Faux Fur"}',
    28,
    'FASH-RALP-040',
    'Ralph Lauren',
    4.9,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1539533018447-63fcce667823?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Nike Tailored Slim Fit Wool Blazer 2024',
    'Expertly tailored premium Italian wool blazer crafted by Nike for formal and evening events.',
    16990.00,
    19539.00,
    '{"Material":"100% Virgin Wool","Lining":"Cupro Silk","Fit":"Tailored Slim","Closure":"Two-Button"}',
    35,
    'FASH-NIKE-041',
    'Nike',
    4.0,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1594938298603-c8148c4dae35?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Adidas Classic 501 Original Fit Denim 2025',
    'The iconic straight leg denim jeans by Adidas with signature copper rivets and button fly.',
    5699.00,
    6782.00,
    '{"Fabric":"100% Cotton Selvedge Denim","Rise":"Mid Rise","Leg":"Straight","Wash":"Dark Indigo Stone Wash"}',
    42,
    'FASH-ADID-042',
    'Adidas',
    4.1,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1542272604-780c96856592?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Puma Ultraboost Running Sneakers 2024',
    'High-performance energy return running shoes engineered by Puma with breathable knit upper.',
    19399.00,
    23861.00,
    '{"Cushioning":"Full-Length Boost Foam","Upper":"Primeknit+","Outsole":"Continental Natural Rubber","Weight":"290g"}',
    49,
    'FASH-PUMA-043',
    'Puma',
    4.2,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Levi''s Oversized Double-Breasted Trench Coat 2025',
    'Timeless weather-resistant gabardine trench coat by Levi''s featuring storm flap and belted waist.',
    13590.00,
    17259.00,
    '{"Fabric":"Water-Repellent Cotton Gabardine","Length":"Midi","Details":"Horn Buttons, Buckled Cuffs"}',
    56,
    'FASH-LEVI-044',
    'Levi''s',
    4.3,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1544441893-675973e31985?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'H&M Pure Cashmere Crewneck Sweater 2024',
    'Sumptuously soft grade-A Mongolian cashmere sweater by H&M offering weightless warmth.',
    14799.00,
    19387.00,
    '{"Material":"100% Pure Cashmere (2-Ply)","Neckline":"Ribbed Crewneck","Care":"Dry Clean or Hand Wash"}',
    63,
    'FASH-H&M-045',
    'H&M',
    4.4,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1576871337632-b9aef4c17ab9?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Zara Vintage Suede Leather Bomber Jacket 2025',
    'Hand-finished goat suede bomber jacket from Zara with satin lining and heavy gauge brass zippers.',
    24490.00,
    28163.00,
    '{"Shell":"100% Genuine Goat Suede","Lining":"100% Viscose","Trim":"Wool Ribbed Hem and Collar"}',
    70,
    'FASH-ZARA-046',
    'Zara',
    4.5,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1551028719-00167b16eac5?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Tommy Hilfiger Tech Fleece Full-Zip Windrunner Hoodie 2024',
    'Double-sided smooth spacer fleece engineered by Tommy Hilfiger for premium warmth without the bulk.',
    8195.00,
    9752.00,
    '{"Material":"66% Cotton, 34% Polyester Tech Fleece","Pockets":"Zippered Sleeve & Kangaroo Pockets","Fit":"Standard Athletic"}',
    77,
    'FASH-TOMM-047',
    'Tommy Hilfiger',
    4.6,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Calvin Klein Pure Mulberry Silk Slip Dress 2025',
    'Effortlessly elegant bias-cut mulberry silk slip dress by Calvin Klein with delicate adjustable straps.',
    11399.00,
    14021.00,
    '{"Fabric":"100% 19 Momme Mulberry Silk","Cut":"Bias Cut","Finish":"French Seams","Length":"Midi"}',
    19,
    'FASH-CALV-048',
    'Calvin Klein',
    4.7,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Under Armour Oxford Cotton Button-Down Shirt 2024',
    'Durable woven pinpoint Oxford cotton casual dress shirt from Under Armour with mother-of-pearl buttons.',
    4099.00,
    5206.00,
    '{"Weave":"100% Combed Oxford Cotton","Collar":"Button-Down Casual","Pattern":"Solid White & Sky Blue"}',
    26,
    'FASH-UNDE-049',
    'Under Armour',
    4.8,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Fashion'),
    'Ralph Lauren Waterproof Winter Down Parka Jacket 2025',
    'Extreme cold weather winter parka by Ralph Lauren insulated with 700-fill power responsibly sourced down.',
    21290.00,
    27890.00,
    '{"Insulation":"700 Fill Power Goose Down","Shell":"2-Layer DryVent Waterproof","Hood":"Removable Faux Fur"}',
    33,
    'FASH-RALP-050',
    'Ralph Lauren',
    4.9,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1539533018447-63fcce667823?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'IKEA Ergonomic High-Back Executive Mesh Chair 2024',
    'Ergonomic mesh chair from IKEA featuring adaptive lumbar support and 4D adjustable armrests.',
    16999.00,
    19549.00,
    '{"Mechanism":"Synchro-Tilt with Multi-Lock","Gas Lift":"Class 4 Heavy Duty","Weight Capacity":"150 kg"}',
    15,
    'HOME-IKEA-001',
    'IKEA',
    4.0,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1580481077195-c22ae4206583?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Philips Air Purifier with True HEPA H13 Filter 2025',
    'High-efficiency smart room air purifier by Philips filtering 99.97% of airborne pollutants down to 0.1 microns.',
    16199.00,
    19277.00,
    '{"CADR":"400 m3/h","Filter":"3-Stage True HEPA + Activated Carbon","Coverage":"Up to 550 sq ft","Noise":"24 dB Sleep Mode"}',
    22,
    'HOME-PHIL-002',
    'Philips',
    4.1,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1585771724684-38269d6639fd?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Havells Smart BLDC Inverter Ceiling Fan with Remote 2024',
    'Energy-saving brushless DC ceiling fan from Havells consuming 65% less power with whisper-quiet operation.',
    4699.00,
    5780.00,
    '{"Motor":"100% Copper BLDC Motor (28W)","Sweep":"1200 mm","Speed":"360 RPM","Control":"RF Remote + Smart App"}',
    29,
    'HOME-HAVE-003',
    'Havells',
    4.2,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1513506003901-1e6a229e2d15?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Prestige Orthopedic Memory Foam Mattress King Size 2025',
    'Pressure-relieving medium-firm orthopedic mattress engineered by Prestige with cooling gel infused memory foam.',
    20599.00,
    26161.00,
    '{"Size":"78x72x8 inches (King)","Construction":"Cooling Gel Memory Foam + High Resilience Base","Cover":"Breathable Bamboo Fabric"}',
    36,
    'HOME-PRES-004',
    'Prestige',
    4.3,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1584132967334-10e028bd69f7?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Wakefit Hard Anodized Non-Stick Cookware 5-Piece Set 2024',
    'Heavy-gauge hard-anodized cookware collection from Wakefit with durable multi-layer scratch-resistant coating.',
    6799.00,
    8907.00,
    '{"Pieces":"Kadhai, Fry Pan, Saucepan, Flat Tawa, Glass Lid","Compatibility":"Induction & Gas Stove","Coating":"PFOA-Free Ceramic Non-Stick"}',
    43,
    'HOME-WAKE-005',
    'Wakefit',
    4.4,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1584990347449-3990b71b8765?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Dyson Digital Hot Air Fryer 6.5L with Rapid Air Tech 2025',
    'Healthy oil-free cooking air fryer by Dyson with digital touchscreen and 12 one-touch cooking presets.',
    10499.00,
    12074.00,
    '{"Capacity":"6.5 Liters","Power":"1800W","Technology":"360 Rapid Hot Air Circulation","Temperature":"80C to 200C"}',
    50,
    'HOME-DYSO-006',
    'Dyson',
    4.5,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1585515320310-259814833e62?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Tefal Cordless Stick Vacuum Cleaner with Laser Detect 2024',
    'Powerful cordless vacuum from Tefal with laser illumination revealing hidden floor dust.',
    36190.00,
    43066.00,
    '{"Suction Power":"220 AW","Runtime":"Up to 60 Minutes","Filtration":"Fully-Sealed Whole-Machine HEPA","Weight":"2.4 kg"}',
    57,
    'HOME-TEFA-007',
    'Tefal',
    4.6,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1558317374-067fb5f30001?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Morphy Richards Smart Wi-Fi Robot Vacuum with Mopping 2025',
    'Autonomous robotic vacuum cleaner by Morphy Richards with LiDAR navigation, auto-recharge, and dual spinning mops.',
    31899.00,
    39236.00,
    '{"Suction":"5000 Pa","Navigation":"PreciSense LiDAR Mapping","Battery":"5200 mAh (180 min runtime)","App":"No-Go Zones & Multi-Floor Mapping"}',
    64,
    'HOME-MORP-008',
    'Morphy Richards',
    4.7,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1618220179428-22790b461013?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Bajaj Ergonomic High-Back Executive Mesh Chair 2024',
    'Ergonomic mesh chair from Bajaj featuring adaptive lumbar support and 4D adjustable armrests.',
    18599.00,
    23621.00,
    '{"Mechanism":"Synchro-Tilt with Multi-Lock","Gas Lift":"Class 4 Heavy Duty","Weight Capacity":"150 kg"}',
    71,
    'HOME-BAJA-009',
    'Bajaj',
    4.8,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1580481077195-c22ae4206583?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Milton Air Purifier with True HEPA H13 Filter 2025',
    'High-efficiency smart room air purifier by Milton filtering 99.97% of airborne pollutants down to 0.1 microns.',
    14799.00,
    19387.00,
    '{"CADR":"400 m3/h","Filter":"3-Stage True HEPA + Activated Carbon","Coverage":"Up to 550 sq ft","Noise":"24 dB Sleep Mode"}',
    78,
    'HOME-MILT-010',
    'Milton',
    4.9,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1585771724684-38269d6639fd?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'IKEA Smart BLDC Inverter Ceiling Fan with Remote 2024',
    'Energy-saving brushless DC ceiling fan from IKEA consuming 65% less power with whisper-quiet operation.',
    6299.00,
    7244.00,
    '{"Motor":"100% Copper BLDC Motor (28W)","Sweep":"1200 mm","Speed":"360 RPM","Control":"RF Remote + Smart App"}',
    20,
    'HOME-IKEA-011',
    'IKEA',
    4.0,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1513506003901-1e6a229e2d15?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Philips Orthopedic Memory Foam Mattress King Size 2025',
    'Pressure-relieving medium-firm orthopedic mattress engineered by Philips with cooling gel infused memory foam.',
    19199.00,
    22847.00,
    '{"Size":"78x72x8 inches (King)","Construction":"Cooling Gel Memory Foam + High Resilience Base","Cover":"Breathable Bamboo Fabric"}',
    27,
    'HOME-PHIL-012',
    'Philips',
    4.1,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1584132967334-10e028bd69f7?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Havells Hard Anodized Non-Stick Cookware 5-Piece Set 2024',
    'Heavy-gauge hard-anodized cookware collection from Havells with durable multi-layer scratch-resistant coating.',
    8399.00,
    10331.00,
    '{"Pieces":"Kadhai, Fry Pan, Saucepan, Flat Tawa, Glass Lid","Compatibility":"Induction & Gas Stove","Coating":"PFOA-Free Ceramic Non-Stick"}',
    34,
    'HOME-HAVE-013',
    'Havells',
    4.2,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1584990347449-3990b71b8765?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Prestige Digital Hot Air Fryer 6.5L with Rapid Air Tech 2025',
    'Healthy oil-free cooking air fryer by Prestige with digital touchscreen and 12 one-touch cooking presets.',
    9099.00,
    11556.00,
    '{"Capacity":"6.5 Liters","Power":"1800W","Technology":"360 Rapid Hot Air Circulation","Temperature":"80C to 200C"}',
    41,
    'HOME-PRES-014',
    'Prestige',
    4.3,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1585515320310-259814833e62?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Wakefit Cordless Stick Vacuum Cleaner with Laser Detect 2024',
    'Powerful cordless vacuum from Wakefit with laser illumination revealing hidden floor dust.',
    37790.00,
    49505.00,
    '{"Suction Power":"220 AW","Runtime":"Up to 60 Minutes","Filtration":"Fully-Sealed Whole-Machine HEPA","Weight":"2.4 kg"}',
    48,
    'HOME-WAKE-015',
    'Wakefit',
    4.4,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1558317374-067fb5f30001?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Dyson Smart Wi-Fi Robot Vacuum with Mopping 2025',
    'Autonomous robotic vacuum cleaner by Dyson with LiDAR navigation, auto-recharge, and dual spinning mops.',
    30499.00,
    35074.00,
    '{"Suction":"5000 Pa","Navigation":"PreciSense LiDAR Mapping","Battery":"5200 mAh (180 min runtime)","App":"No-Go Zones & Multi-Floor Mapping"}',
    55,
    'HOME-DYSO-016',
    'Dyson',
    4.5,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1618220179428-22790b461013?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Tefal Ergonomic High-Back Executive Mesh Chair 2024',
    'Ergonomic mesh chair from Tefal featuring adaptive lumbar support and 4D adjustable armrests.',
    17199.00,
    20467.00,
    '{"Mechanism":"Synchro-Tilt with Multi-Lock","Gas Lift":"Class 4 Heavy Duty","Weight Capacity":"150 kg"}',
    62,
    'HOME-TEFA-017',
    'Tefal',
    4.6,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1580481077195-c22ae4206583?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Morphy Richards Air Purifier with True HEPA H13 Filter 2025',
    'High-efficiency smart room air purifier by Morphy Richards filtering 99.97% of airborne pollutants down to 0.1 microns.',
    16399.00,
    20171.00,
    '{"CADR":"400 m3/h","Filter":"3-Stage True HEPA + Activated Carbon","Coverage":"Up to 550 sq ft","Noise":"24 dB Sleep Mode"}',
    69,
    'HOME-MORP-018',
    'Morphy Richards',
    4.7,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1585771724684-38269d6639fd?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Bajaj Smart BLDC Inverter Ceiling Fan with Remote 2024',
    'Energy-saving brushless DC ceiling fan from Bajaj consuming 65% less power with whisper-quiet operation.',
    4899.00,
    6222.00,
    '{"Motor":"100% Copper BLDC Motor (28W)","Sweep":"1200 mm","Speed":"360 RPM","Control":"RF Remote + Smart App"}',
    76,
    'HOME-BAJA-019',
    'Bajaj',
    4.8,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1513506003901-1e6a229e2d15?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Milton Orthopedic Memory Foam Mattress King Size 2025',
    'Pressure-relieving medium-firm orthopedic mattress engineered by Milton with cooling gel infused memory foam.',
    20799.00,
    27247.00,
    '{"Size":"78x72x8 inches (King)","Construction":"Cooling Gel Memory Foam + High Resilience Base","Cover":"Breathable Bamboo Fabric"}',
    18,
    'HOME-MILT-020',
    'Milton',
    4.9,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1584132967334-10e028bd69f7?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'IKEA Hard Anodized Non-Stick Cookware 5-Piece Set 2024',
    'Heavy-gauge hard-anodized cookware collection from IKEA with durable multi-layer scratch-resistant coating.',
    6999.00,
    8049.00,
    '{"Pieces":"Kadhai, Fry Pan, Saucepan, Flat Tawa, Glass Lid","Compatibility":"Induction & Gas Stove","Coating":"PFOA-Free Ceramic Non-Stick"}',
    25,
    'HOME-IKEA-021',
    'IKEA',
    4.0,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1584990347449-3990b71b8765?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Philips Digital Hot Air Fryer 6.5L with Rapid Air Tech 2025',
    'Healthy oil-free cooking air fryer by Philips with digital touchscreen and 12 one-touch cooking presets.',
    10699.00,
    12732.00,
    '{"Capacity":"6.5 Liters","Power":"1800W","Technology":"360 Rapid Hot Air Circulation","Temperature":"80C to 200C"}',
    32,
    'HOME-PHIL-022',
    'Philips',
    4.1,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1585515320310-259814833e62?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Havells Cordless Stick Vacuum Cleaner with Laser Detect 2024',
    'Powerful cordless vacuum from Havells with laser illumination revealing hidden floor dust.',
    36390.00,
    44760.00,
    '{"Suction Power":"220 AW","Runtime":"Up to 60 Minutes","Filtration":"Fully-Sealed Whole-Machine HEPA","Weight":"2.4 kg"}',
    39,
    'HOME-HAVE-023',
    'Havells',
    4.2,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1558317374-067fb5f30001?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Prestige Smart Wi-Fi Robot Vacuum with Mopping 2025',
    'Autonomous robotic vacuum cleaner by Prestige with LiDAR navigation, auto-recharge, and dual spinning mops.',
    29099.00,
    36956.00,
    '{"Suction":"5000 Pa","Navigation":"PreciSense LiDAR Mapping","Battery":"5200 mAh (180 min runtime)","App":"No-Go Zones & Multi-Floor Mapping"}',
    46,
    'HOME-PRES-024',
    'Prestige',
    4.3,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1618220179428-22790b461013?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Wakefit Ergonomic High-Back Executive Mesh Chair 2024',
    'Ergonomic mesh chair from Wakefit featuring adaptive lumbar support and 4D adjustable armrests.',
    18799.00,
    24627.00,
    '{"Mechanism":"Synchro-Tilt with Multi-Lock","Gas Lift":"Class 4 Heavy Duty","Weight Capacity":"150 kg"}',
    53,
    'HOME-WAKE-025',
    'Wakefit',
    4.4,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1580481077195-c22ae4206583?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Dyson Air Purifier with True HEPA H13 Filter 2025',
    'High-efficiency smart room air purifier by Dyson filtering 99.97% of airborne pollutants down to 0.1 microns.',
    14999.00,
    17249.00,
    '{"CADR":"400 m3/h","Filter":"3-Stage True HEPA + Activated Carbon","Coverage":"Up to 550 sq ft","Noise":"24 dB Sleep Mode"}',
    60,
    'HOME-DYSO-026',
    'Dyson',
    4.5,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1585771724684-38269d6639fd?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Tefal Smart BLDC Inverter Ceiling Fan with Remote 2024',
    'Energy-saving brushless DC ceiling fan from Tefal consuming 65% less power with whisper-quiet operation.',
    6499.00,
    7734.00,
    '{"Motor":"100% Copper BLDC Motor (28W)","Sweep":"1200 mm","Speed":"360 RPM","Control":"RF Remote + Smart App"}',
    67,
    'HOME-TEFA-027',
    'Tefal',
    4.6,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1513506003901-1e6a229e2d15?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Morphy Richards Orthopedic Memory Foam Mattress King Size 2025',
    'Pressure-relieving medium-firm orthopedic mattress engineered by Morphy Richards with cooling gel infused memory foam.',
    19399.00,
    23861.00,
    '{"Size":"78x72x8 inches (King)","Construction":"Cooling Gel Memory Foam + High Resilience Base","Cover":"Breathable Bamboo Fabric"}',
    74,
    'HOME-MORP-028',
    'Morphy Richards',
    4.7,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1584132967334-10e028bd69f7?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Bajaj Hard Anodized Non-Stick Cookware 5-Piece Set 2024',
    'Heavy-gauge hard-anodized cookware collection from Bajaj with durable multi-layer scratch-resistant coating.',
    8599.00,
    10921.00,
    '{"Pieces":"Kadhai, Fry Pan, Saucepan, Flat Tawa, Glass Lid","Compatibility":"Induction & Gas Stove","Coating":"PFOA-Free Ceramic Non-Stick"}',
    16,
    'HOME-BAJA-029',
    'Bajaj',
    4.8,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1584990347449-3990b71b8765?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Milton Digital Hot Air Fryer 6.5L with Rapid Air Tech 2025',
    'Healthy oil-free cooking air fryer by Milton with digital touchscreen and 12 one-touch cooking presets.',
    9299.00,
    12182.00,
    '{"Capacity":"6.5 Liters","Power":"1800W","Technology":"360 Rapid Hot Air Circulation","Temperature":"80C to 200C"}',
    23,
    'HOME-MILT-030',
    'Milton',
    4.9,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1585515320310-259814833e62?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'IKEA Cordless Stick Vacuum Cleaner with Laser Detect 2024',
    'Powerful cordless vacuum from IKEA with laser illumination revealing hidden floor dust.',
    34990.00,
    40239.00,
    '{"Suction Power":"220 AW","Runtime":"Up to 60 Minutes","Filtration":"Fully-Sealed Whole-Machine HEPA","Weight":"2.4 kg"}',
    30,
    'HOME-IKEA-031',
    'IKEA',
    4.0,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1558317374-067fb5f30001?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Philips Smart Wi-Fi Robot Vacuum with Mopping 2025',
    'Autonomous robotic vacuum cleaner by Philips with LiDAR navigation, auto-recharge, and dual spinning mops.',
    30699.00,
    36532.00,
    '{"Suction":"5000 Pa","Navigation":"PreciSense LiDAR Mapping","Battery":"5200 mAh (180 min runtime)","App":"No-Go Zones & Multi-Floor Mapping"}',
    37,
    'HOME-PHIL-032',
    'Philips',
    4.1,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1618220179428-22790b461013?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Havells Ergonomic High-Back Executive Mesh Chair 2024',
    'Ergonomic mesh chair from Havells featuring adaptive lumbar support and 4D adjustable armrests.',
    17399.00,
    21401.00,
    '{"Mechanism":"Synchro-Tilt with Multi-Lock","Gas Lift":"Class 4 Heavy Duty","Weight Capacity":"150 kg"}',
    44,
    'HOME-HAVE-033',
    'Havells',
    4.2,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1580481077195-c22ae4206583?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Prestige Air Purifier with True HEPA H13 Filter 2025',
    'High-efficiency smart room air purifier by Prestige filtering 99.97% of airborne pollutants down to 0.1 microns.',
    16599.00,
    21081.00,
    '{"CADR":"400 m3/h","Filter":"3-Stage True HEPA + Activated Carbon","Coverage":"Up to 550 sq ft","Noise":"24 dB Sleep Mode"}',
    51,
    'HOME-PRES-034',
    'Prestige',
    4.3,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1585771724684-38269d6639fd?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Wakefit Smart BLDC Inverter Ceiling Fan with Remote 2024',
    'Energy-saving brushless DC ceiling fan from Wakefit consuming 65% less power with whisper-quiet operation.',
    5099.00,
    6680.00,
    '{"Motor":"100% Copper BLDC Motor (28W)","Sweep":"1200 mm","Speed":"360 RPM","Control":"RF Remote + Smart App"}',
    58,
    'HOME-WAKE-035',
    'Wakefit',
    4.4,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1513506003901-1e6a229e2d15?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Dyson Orthopedic Memory Foam Mattress King Size 2025',
    'Pressure-relieving medium-firm orthopedic mattress engineered by Dyson with cooling gel infused memory foam.',
    20999.00,
    24149.00,
    '{"Size":"78x72x8 inches (King)","Construction":"Cooling Gel Memory Foam + High Resilience Base","Cover":"Breathable Bamboo Fabric"}',
    65,
    'HOME-DYSO-036',
    'Dyson',
    4.5,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1584132967334-10e028bd69f7?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Tefal Hard Anodized Non-Stick Cookware 5-Piece Set 2024',
    'Heavy-gauge hard-anodized cookware collection from Tefal with durable multi-layer scratch-resistant coating.',
    7199.00,
    8567.00,
    '{"Pieces":"Kadhai, Fry Pan, Saucepan, Flat Tawa, Glass Lid","Compatibility":"Induction & Gas Stove","Coating":"PFOA-Free Ceramic Non-Stick"}',
    72,
    'HOME-TEFA-037',
    'Tefal',
    4.6,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1584990347449-3990b71b8765?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Morphy Richards Digital Hot Air Fryer 6.5L with Rapid Air Tech 2025',
    'Healthy oil-free cooking air fryer by Morphy Richards with digital touchscreen and 12 one-touch cooking presets.',
    10899.00,
    13406.00,
    '{"Capacity":"6.5 Liters","Power":"1800W","Technology":"360 Rapid Hot Air Circulation","Temperature":"80C to 200C"}',
    79,
    'HOME-MORP-038',
    'Morphy Richards',
    4.7,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1585515320310-259814833e62?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Bajaj Cordless Stick Vacuum Cleaner with Laser Detect 2024',
    'Powerful cordless vacuum from Bajaj with laser illumination revealing hidden floor dust.',
    36590.00,
    46469.00,
    '{"Suction Power":"220 AW","Runtime":"Up to 60 Minutes","Filtration":"Fully-Sealed Whole-Machine HEPA","Weight":"2.4 kg"}',
    21,
    'HOME-BAJA-039',
    'Bajaj',
    4.8,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1558317374-067fb5f30001?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Home & Living'),
    'Milton Smart Wi-Fi Robot Vacuum with Mopping 2025',
    'Autonomous robotic vacuum cleaner by Milton with LiDAR navigation, auto-recharge, and dual spinning mops.',
    29299.00,
    38382.00,
    '{"Suction":"5000 Pa","Navigation":"PreciSense LiDAR Mapping","Battery":"5200 mAh (180 min runtime)","App":"No-Go Zones & Multi-Floor Mapping"}',
    28,
    'HOME-MILT-040',
    'Milton',
    4.9,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1618220179428-22790b461013?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'boAt Classic Aviator Polarized Sunglasses 2024',
    'Timeless teardrop frame sunglasses by boAt with crystal polarized lenses providing 100% UV400 protection.',
    8490.00,
    9764.00,
    '{"Frame":"Monel Metal Gold Tone","Lens":"G-15 Polarized Crystal Green","Bridge":"14mm","Temple":"135mm"}',
    15,
    'ACCS-BOAT-001',
    'boAt',
    4.0,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'JBL G-Shock Octagonal All-Black Watch 2025',
    'Ultra-rugged carbon core guard shock-resistant timepiece by JBL with 200m water resistance.',
    11695.00,
    13917.00,
    '{"Case":"Carbon Core Guard / Resin (48.5mm)","Water Resistance":"200 meters (20 Bar)","Glass":"Mineral Glass","Battery":"3 Years"}',
    22,
    'ACCS-JBL-002',
    'JBL',
    4.1,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Logitech Portable Waterproof Bluetooth Speaker IP67 2024',
    'Rugged high-fidelity portable outdoor speaker by Logitech delivering punchy bass and 12-hour playtime.',
    10399.00,
    12791.00,
    '{"Power":"30W RMS (Woofer + Tweeter)","Battery":"12 Hours","Waterproof Rating":"IP67 Dust & Water","Bluetooth":"5.2 Auracast"}',
    29,
    'ACCS-LOGI-003',
    'Logitech',
    4.2,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1545454675-3531b543be5d?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Anker Fast Charge 24000mAh Power Bank 140W 2025',
    'Ultra-high power output portable charger from Anker capable of fast charging two laptops simultaneously.',
    15099.00,
    19176.00,
    '{"Capacity":"24,000 mAh (86.4Wh)","Total Output":"140W Max (Power Delivery 3.1)","Display":"Smart Digital Color Screen","Ports":"2x USB-C + 1x USB-A"}',
    36,
    'ACCS-ANKE-004',
    'Anker',
    4.3,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1609592426815-562db496525a?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Noise Wireless Ergonomic Performance Mouse 2024',
    'Master-series ergonomic precision mouse by Noise with 8K DPI track-on-glass sensor and magnetic scroll wheel.',
    10295.00,
    13486.00,
    '{"Sensor":"Darkfield 8000 DPI","Scroll":"MagSpeed Electromagnetic","Battery":"70 Days on Full Charge","Multi-Device":"Pair up to 3 Devices"}',
    43,
    'ACCS-NOIS-005',
    'Noise',
    4.4,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Fossil Full Grain Leather Bi-Fold Wallet with RFID 2025',
    'Handcrafted vegetable-tanned full-grain leather wallet from Fossil with built-in RFID blocking shield.',
    5799.00,
    6669.00,
    '{"Leather":"100% Italian Vegetable Tanned Cowhide","Card Slots":"8 Slots + 2 Cash Compartments","Security":"RFID Blocking Mesh"}',
    50,
    'ACCS-FOSS-006',
    'Fossil',
    4.5,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1627123424574-724758594e93?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Ray-Ban Smart Fitness Watch AMOLED with Calling 2024',
    'Brilliant 1.43" AMOLED display smartwatch by Ray-Ban with Bluetooth voice calling and 100+ sports modes.',
    6199.00,
    7377.00,
    '{"Display":"1.43\" AMOLED (466x466, 1000 nits)","Sensors":"Heart Rate, SpO2, Sleep, Stress","Battery":"Up to 7 Days","Water Resistance":"IP68"}',
    57,
    'ACCS-RAY--007',
    'Ray-Ban',
    4.6,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Casio Wireless Charging Pad 3-in-1 Foldable 2025',
    'Sleek travel-ready 3-in-1 magnetic wireless charging station from Casio for phone, smartwatch, and earbuds.',
    7499.00,
    9224.00,
    '{"Output":"15W Phone + 5W Watch + 5W Earbuds","Technology":"Qi2 & MagSafe Compatible","Design":"Foldable Aluminum Body"}',
    64,
    'ACCS-CASI-008',
    'Casio',
    4.7,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1586105251261-72a756497a11?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Titan Classic Aviator Polarized Sunglasses 2024',
    'Timeless teardrop frame sunglasses by Titan with crystal polarized lenses providing 100% UV400 protection.',
    10090.00,
    12814.00,
    '{"Frame":"Monel Metal Gold Tone","Lens":"G-15 Polarized Crystal Green","Bridge":"14mm","Temple":"135mm"}',
    71,
    'ACCS-TITA-009',
    'Titan',
    4.8,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Sony G-Shock Octagonal All-Black Watch 2025',
    'Ultra-rugged carbon core guard shock-resistant timepiece by Sony with 200m water resistance.',
    10295.00,
    13486.00,
    '{"Case":"Carbon Core Guard / Resin (48.5mm)","Water Resistance":"200 meters (20 Bar)","Glass":"Mineral Glass","Battery":"3 Years"}',
    78,
    'ACCS-SONY-010',
    'Sony',
    4.9,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'boAt Portable Waterproof Bluetooth Speaker IP67 2024',
    'Rugged high-fidelity portable outdoor speaker by boAt delivering punchy bass and 12-hour playtime.',
    11999.00,
    13799.00,
    '{"Power":"30W RMS (Woofer + Tweeter)","Battery":"12 Hours","Waterproof Rating":"IP67 Dust & Water","Bluetooth":"5.2 Auracast"}',
    20,
    'ACCS-BOAT-011',
    'boAt',
    4.0,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1545454675-3531b543be5d?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'JBL Fast Charge 24000mAh Power Bank 140W 2025',
    'Ultra-high power output portable charger from JBL capable of fast charging two laptops simultaneously.',
    13699.00,
    16302.00,
    '{"Capacity":"24,000 mAh (86.4Wh)","Total Output":"140W Max (Power Delivery 3.1)","Display":"Smart Digital Color Screen","Ports":"2x USB-C + 1x USB-A"}',
    27,
    'ACCS-JBL-012',
    'JBL',
    4.1,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1609592426815-562db496525a?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Logitech Wireless Ergonomic Performance Mouse 2024',
    'Master-series ergonomic precision mouse by Logitech with 8K DPI track-on-glass sensor and magnetic scroll wheel.',
    11895.00,
    14631.00,
    '{"Sensor":"Darkfield 8000 DPI","Scroll":"MagSpeed Electromagnetic","Battery":"70 Days on Full Charge","Multi-Device":"Pair up to 3 Devices"}',
    34,
    'ACCS-LOGI-013',
    'Logitech',
    4.2,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Anker Full Grain Leather Bi-Fold Wallet with RFID 2025',
    'Handcrafted vegetable-tanned full-grain leather wallet from Anker with built-in RFID blocking shield.',
    4399.00,
    5587.00,
    '{"Leather":"100% Italian Vegetable Tanned Cowhide","Card Slots":"8 Slots + 2 Cash Compartments","Security":"RFID Blocking Mesh"}',
    41,
    'ACCS-ANKE-014',
    'Anker',
    4.3,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1627123424574-724758594e93?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Noise Smart Fitness Watch AMOLED with Calling 2024',
    'Brilliant 1.43" AMOLED display smartwatch by Noise with Bluetooth voice calling and 100+ sports modes.',
    7799.00,
    10217.00,
    '{"Display":"1.43\" AMOLED (466x466, 1000 nits)","Sensors":"Heart Rate, SpO2, Sleep, Stress","Battery":"Up to 7 Days","Water Resistance":"IP68"}',
    48,
    'ACCS-NOIS-015',
    'Noise',
    4.4,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Fossil Wireless Charging Pad 3-in-1 Foldable 2025',
    'Sleek travel-ready 3-in-1 magnetic wireless charging station from Fossil for phone, smartwatch, and earbuds.',
    6099.00,
    7014.00,
    '{"Output":"15W Phone + 5W Watch + 5W Earbuds","Technology":"Qi2 & MagSafe Compatible","Design":"Foldable Aluminum Body"}',
    55,
    'ACCS-FOSS-016',
    'Fossil',
    4.5,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1586105251261-72a756497a11?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Ray-Ban Classic Aviator Polarized Sunglasses 2024',
    'Timeless teardrop frame sunglasses by Ray-Ban with crystal polarized lenses providing 100% UV400 protection.',
    8690.00,
    10341.00,
    '{"Frame":"Monel Metal Gold Tone","Lens":"G-15 Polarized Crystal Green","Bridge":"14mm","Temple":"135mm"}',
    62,
    'ACCS-RAY--017',
    'Ray-Ban',
    4.6,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Casio G-Shock Octagonal All-Black Watch 2025',
    'Ultra-rugged carbon core guard shock-resistant timepiece by Casio with 200m water resistance.',
    11895.00,
    14631.00,
    '{"Case":"Carbon Core Guard / Resin (48.5mm)","Water Resistance":"200 meters (20 Bar)","Glass":"Mineral Glass","Battery":"3 Years"}',
    69,
    'ACCS-CASI-018',
    'Casio',
    4.7,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Titan Portable Waterproof Bluetooth Speaker IP67 2024',
    'Rugged high-fidelity portable outdoor speaker by Titan delivering punchy bass and 12-hour playtime.',
    10599.00,
    13461.00,
    '{"Power":"30W RMS (Woofer + Tweeter)","Battery":"12 Hours","Waterproof Rating":"IP67 Dust & Water","Bluetooth":"5.2 Auracast"}',
    76,
    'ACCS-TITA-019',
    'Titan',
    4.8,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1545454675-3531b543be5d?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Sony Fast Charge 24000mAh Power Bank 140W 2025',
    'Ultra-high power output portable charger from Sony capable of fast charging two laptops simultaneously.',
    15299.00,
    20042.00,
    '{"Capacity":"24,000 mAh (86.4Wh)","Total Output":"140W Max (Power Delivery 3.1)","Display":"Smart Digital Color Screen","Ports":"2x USB-C + 1x USB-A"}',
    18,
    'ACCS-SONY-020',
    'Sony',
    4.9,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1609592426815-562db496525a?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'boAt Wireless Ergonomic Performance Mouse 2024',
    'Master-series ergonomic precision mouse by boAt with 8K DPI track-on-glass sensor and magnetic scroll wheel.',
    10495.00,
    12069.00,
    '{"Sensor":"Darkfield 8000 DPI","Scroll":"MagSpeed Electromagnetic","Battery":"70 Days on Full Charge","Multi-Device":"Pair up to 3 Devices"}',
    25,
    'ACCS-BOAT-021',
    'boAt',
    4.0,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'JBL Full Grain Leather Bi-Fold Wallet with RFID 2025',
    'Handcrafted vegetable-tanned full-grain leather wallet from JBL with built-in RFID blocking shield.',
    5999.00,
    7139.00,
    '{"Leather":"100% Italian Vegetable Tanned Cowhide","Card Slots":"8 Slots + 2 Cash Compartments","Security":"RFID Blocking Mesh"}',
    32,
    'ACCS-JBL-022',
    'JBL',
    4.1,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1627123424574-724758594e93?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Logitech Smart Fitness Watch AMOLED with Calling 2024',
    'Brilliant 1.43" AMOLED display smartwatch by Logitech with Bluetooth voice calling and 100+ sports modes.',
    6399.00,
    7871.00,
    '{"Display":"1.43\" AMOLED (466x466, 1000 nits)","Sensors":"Heart Rate, SpO2, Sleep, Stress","Battery":"Up to 7 Days","Water Resistance":"IP68"}',
    39,
    'ACCS-LOGI-023',
    'Logitech',
    4.2,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Anker Wireless Charging Pad 3-in-1 Foldable 2025',
    'Sleek travel-ready 3-in-1 magnetic wireless charging station from Anker for phone, smartwatch, and earbuds.',
    4699.00,
    5968.00,
    '{"Output":"15W Phone + 5W Watch + 5W Earbuds","Technology":"Qi2 & MagSafe Compatible","Design":"Foldable Aluminum Body"}',
    46,
    'ACCS-ANKE-024',
    'Anker',
    4.3,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1586105251261-72a756497a11?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Noise Classic Aviator Polarized Sunglasses 2024',
    'Timeless teardrop frame sunglasses by Noise with crystal polarized lenses providing 100% UV400 protection.',
    10290.00,
    13480.00,
    '{"Frame":"Monel Metal Gold Tone","Lens":"G-15 Polarized Crystal Green","Bridge":"14mm","Temple":"135mm"}',
    53,
    'ACCS-NOIS-025',
    'Noise',
    4.4,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Fossil G-Shock Octagonal All-Black Watch 2025',
    'Ultra-rugged carbon core guard shock-resistant timepiece by Fossil with 200m water resistance.',
    10495.00,
    12069.00,
    '{"Case":"Carbon Core Guard / Resin (48.5mm)","Water Resistance":"200 meters (20 Bar)","Glass":"Mineral Glass","Battery":"3 Years"}',
    60,
    'ACCS-FOSS-026',
    'Fossil',
    4.5,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Ray-Ban Portable Waterproof Bluetooth Speaker IP67 2024',
    'Rugged high-fidelity portable outdoor speaker by Ray-Ban delivering punchy bass and 12-hour playtime.',
    12199.00,
    14517.00,
    '{"Power":"30W RMS (Woofer + Tweeter)","Battery":"12 Hours","Waterproof Rating":"IP67 Dust & Water","Bluetooth":"5.2 Auracast"}',
    67,
    'ACCS-RAY--027',
    'Ray-Ban',
    4.6,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1545454675-3531b543be5d?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Casio Fast Charge 24000mAh Power Bank 140W 2025',
    'Ultra-high power output portable charger from Casio capable of fast charging two laptops simultaneously.',
    13899.00,
    17096.00,
    '{"Capacity":"24,000 mAh (86.4Wh)","Total Output":"140W Max (Power Delivery 3.1)","Display":"Smart Digital Color Screen","Ports":"2x USB-C + 1x USB-A"}',
    74,
    'ACCS-CASI-028',
    'Casio',
    4.7,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1609592426815-562db496525a?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Titan Wireless Ergonomic Performance Mouse 2024',
    'Master-series ergonomic precision mouse by Titan with 8K DPI track-on-glass sensor and magnetic scroll wheel.',
    12095.00,
    15361.00,
    '{"Sensor":"Darkfield 8000 DPI","Scroll":"MagSpeed Electromagnetic","Battery":"70 Days on Full Charge","Multi-Device":"Pair up to 3 Devices"}',
    16,
    'ACCS-TITA-029',
    'Titan',
    4.8,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Sony Full Grain Leather Bi-Fold Wallet with RFID 2025',
    'Handcrafted vegetable-tanned full-grain leather wallet from Sony with built-in RFID blocking shield.',
    4599.00,
    6025.00,
    '{"Leather":"100% Italian Vegetable Tanned Cowhide","Card Slots":"8 Slots + 2 Cash Compartments","Security":"RFID Blocking Mesh"}',
    23,
    'ACCS-SONY-030',
    'Sony',
    4.9,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1627123424574-724758594e93?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'boAt Smart Fitness Watch AMOLED with Calling 2024',
    'Brilliant 1.43" AMOLED display smartwatch by boAt with Bluetooth voice calling and 100+ sports modes.',
    4999.00,
    5749.00,
    '{"Display":"1.43\" AMOLED (466x466, 1000 nits)","Sensors":"Heart Rate, SpO2, Sleep, Stress","Battery":"Up to 7 Days","Water Resistance":"IP68"}',
    30,
    'ACCS-BOAT-031',
    'boAt',
    4.0,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'JBL Wireless Charging Pad 3-in-1 Foldable 2025',
    'Sleek travel-ready 3-in-1 magnetic wireless charging station from JBL for phone, smartwatch, and earbuds.',
    6299.00,
    7496.00,
    '{"Output":"15W Phone + 5W Watch + 5W Earbuds","Technology":"Qi2 & MagSafe Compatible","Design":"Foldable Aluminum Body"}',
    37,
    'ACCS-JBL-032',
    'JBL',
    4.1,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1586105251261-72a756497a11?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Logitech Classic Aviator Polarized Sunglasses 2024',
    'Timeless teardrop frame sunglasses by Logitech with crystal polarized lenses providing 100% UV400 protection.',
    8890.00,
    10935.00,
    '{"Frame":"Monel Metal Gold Tone","Lens":"G-15 Polarized Crystal Green","Bridge":"14mm","Temple":"135mm"}',
    44,
    'ACCS-LOGI-033',
    'Logitech',
    4.2,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Anker G-Shock Octagonal All-Black Watch 2025',
    'Ultra-rugged carbon core guard shock-resistant timepiece by Anker with 200m water resistance.',
    12095.00,
    15361.00,
    '{"Case":"Carbon Core Guard / Resin (48.5mm)","Water Resistance":"200 meters (20 Bar)","Glass":"Mineral Glass","Battery":"3 Years"}',
    51,
    'ACCS-ANKE-034',
    'Anker',
    4.3,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Noise Portable Waterproof Bluetooth Speaker IP67 2024',
    'Rugged high-fidelity portable outdoor speaker by Noise delivering punchy bass and 12-hour playtime.',
    10799.00,
    14147.00,
    '{"Power":"30W RMS (Woofer + Tweeter)","Battery":"12 Hours","Waterproof Rating":"IP67 Dust & Water","Bluetooth":"5.2 Auracast"}',
    58,
    'ACCS-NOIS-035',
    'Noise',
    4.4,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1545454675-3531b543be5d?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Fossil Fast Charge 24000mAh Power Bank 140W 2025',
    'Ultra-high power output portable charger from Fossil capable of fast charging two laptops simultaneously.',
    15499.00,
    17824.00,
    '{"Capacity":"24,000 mAh (86.4Wh)","Total Output":"140W Max (Power Delivery 3.1)","Display":"Smart Digital Color Screen","Ports":"2x USB-C + 1x USB-A"}',
    65,
    'ACCS-FOSS-036',
    'Fossil',
    4.5,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1609592426815-562db496525a?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Ray-Ban Wireless Ergonomic Performance Mouse 2024',
    'Master-series ergonomic precision mouse by Ray-Ban with 8K DPI track-on-glass sensor and magnetic scroll wheel.',
    10695.00,
    12727.00,
    '{"Sensor":"Darkfield 8000 DPI","Scroll":"MagSpeed Electromagnetic","Battery":"70 Days on Full Charge","Multi-Device":"Pair up to 3 Devices"}',
    72,
    'ACCS-RAY--037',
    'Ray-Ban',
    4.6,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Casio Full Grain Leather Bi-Fold Wallet with RFID 2025',
    'Handcrafted vegetable-tanned full-grain leather wallet from Casio with built-in RFID blocking shield.',
    6199.00,
    7625.00,
    '{"Leather":"100% Italian Vegetable Tanned Cowhide","Card Slots":"8 Slots + 2 Cash Compartments","Security":"RFID Blocking Mesh"}',
    79,
    'ACCS-CASI-038',
    'Casio',
    4.7,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1627123424574-724758594e93?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Titan Smart Fitness Watch AMOLED with Calling 2024',
    'Brilliant 1.43" AMOLED display smartwatch by Titan with Bluetooth voice calling and 100+ sports modes.',
    6599.00,
    8381.00,
    '{"Display":"1.43\" AMOLED (466x466, 1000 nits)","Sensors":"Heart Rate, SpO2, Sleep, Stress","Battery":"Up to 7 Days","Water Resistance":"IP68"}',
    21,
    'ACCS-TITA-039',
    'Titan',
    4.8,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Accessories'),
    'Sony Wireless Charging Pad 3-in-1 Foldable 2025',
    'Sleek travel-ready 3-in-1 magnetic wireless charging station from Sony for phone, smartwatch, and earbuds.',
    4899.00,
    6418.00,
    '{"Output":"15W Phone + 5W Watch + 5W Earbuds","Technology":"Qi2 & MagSafe Compatible","Design":"Foldable Aluminum Body"}',
    28,
    'ACCS-SONY-040',
    'Sony',
    4.9,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1586105251261-72a756497a11?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Nivea 10% Niacinamide Face Serum with Zinc 2024',
    'Clean scientific formulation by Nivea clinically proven to fade dark spots and balance excess sebum.',
    599.00,
    689.00,
    '{"Volume":"30 ml","Active Ingredients":"10% Pure Niacinamide + 1% Zinc PCA","Skin Type":"Oily, Acne-Prone & Blemish-Prone","pH":"5.5 - 6.5"}',
    15,
    'BEAU-NIVE-001',
    'Nivea',
    4.0,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Cetaphil Gentle Skin Cleanser Hydrating Formula 2025',
    'Dermatologist-recommended creamy soap-free cleanser from Cetaphil that cleanses without stripping natural moisture.',
    2199.00,
    2617.00,
    '{"Volume":"250 ml","Formula":"Non-Comedogenic & Fragrance-Free","Key Actives":"Niacinamide, Panthenol, Hydrating Glycerin"}',
    22,
    'BEAU-CETA-002',
    'Cetaphil',
    4.1,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1556228720-195a672e8a03?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Minimalist Hydro Boost Hyaluronic Acid Water Gel 2024',
    'Oil-free gel moisturizer engineered by Minimalist providing 72-hour continuous hydration with purified hyaluronic acid.',
    1450.00,
    1784.00,
    '{"Net Weight":"50g","Key Ingredient":"Pure Hyaluronic Acid + Trehalose","Texture":"Lightweight Water Gel","Finish":"Dewy Non-Sticky"}',
    29,
    'BEAU-MINI-003',
    'Minimalist',
    4.2,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1598440947619-2c35fc9aa908?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'L''Oréal Revitalift 1.5% Pure Hyaluronic Acid Serum 2025',
    'Anti-wrinkle plumping serum from L''Oréal formulated with micro and macro hyaluronic acid molecules.',
    2999.00,
    3809.00,
    '{"Volume":"30 ml","Actives":"0.5% Macro HA + 1.0% Micro HA","Benefits":"Plumps skin and reduces fine lines by 47%","Formula":"Paraben-Free"}',
    36,
    'BEAU-L''OR-004',
    'L''Oréal',
    4.3,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1608248597359-593539829f8f?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Neutrogena Soundarya Radiance Luxury Night Cream 2024',
    'Ayurvedic rejuvenating night cream by Neutrogena infused with 24K Gold Bhasma and pure saffron extracts.',
    4695.00,
    6150.00,
    '{"Net Weight":"50g","Key Actives":"24 Karat Pure Gold Bhasma, Saffron, Ashwagandha","Benefits":"Enhances skin elasticity and golden glow"}',
    43,
    'BEAU-NEUT-005',
    'Neutrogena',
    4.4,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1570197788417-0e82375c9371?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Forest Essentials Vitamin C 16% Face Glow Serum 2025',
    'Potent antioxidant brightening serum from Forest Essentials with stabilized ethyl ascorbic acid and ferulic acid.',
    3199.00,
    3679.00,
    '{"Volume":"20 ml","Key Actives":"16% Vitamin C + 1% Ferulic Acid + Fullerene","Packaging":"UV-Protected Amber Glass Dropper"}',
    50,
    'BEAU-FORE-006',
    'Forest Essentials',
    4.5,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1617897903246-719242758050?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Maybelline Sunscreen SPF 50 PA++++ Invisible Gel 2024',
    'Broad-spectrum ultralight gel sunscreen by Maybelline leaving zero white cast with photostable modern UV filters.',
    1699.00,
    2022.00,
    '{"Protection":"SPF 50+ PA++++ (UVA/UVB)","Volume":"50g","Finish":"Invisible Matte, Water Resistant (80 min)"}',
    57,
    'BEAU-MAYB-007',
    'Maybelline',
    4.6,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1598440947619-2c35fc9aa908?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'The Body Shop Intense Repair Hair Mask with Argan Oil 2025',
    'Deep conditioning restorative hair treatment from The Body Shop infused with pure Moroccan Argan oil and keratin.',
    3699.00,
    4550.00,
    '{"Net Weight":"200 ml","Ingredients":"Cold-Pressed Moroccan Argan Oil, Hydrolyzed Keratin","Hair Type":"Dry, Damaged, Chemically Treated"}',
    64,
    'BEAU-THE -008',
    'The Body Shop',
    4.7,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Innisfree 10% Niacinamide Face Serum with Zinc 2024',
    'Clean scientific formulation by Innisfree clinically proven to fade dark spots and balance excess sebum.',
    2199.00,
    2793.00,
    '{"Volume":"30 ml","Active Ingredients":"10% Pure Niacinamide + 1% Zinc PCA","Skin Type":"Oily, Acne-Prone & Blemish-Prone","pH":"5.5 - 6.5"}',
    71,
    'BEAU-INNI-009',
    'Innisfree',
    4.8,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Kama Ayurveda Gentle Skin Cleanser Hydrating Formula 2025',
    'Dermatologist-recommended creamy soap-free cleanser from Kama Ayurveda that cleanses without stripping natural moisture.',
    799.00,
    1047.00,
    '{"Volume":"250 ml","Formula":"Non-Comedogenic & Fragrance-Free","Key Actives":"Niacinamide, Panthenol, Hydrating Glycerin"}',
    78,
    'BEAU-KAMA-010',
    'Kama Ayurveda',
    4.9,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1556228720-195a672e8a03?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Nivea Hydro Boost Hyaluronic Acid Water Gel 2024',
    'Oil-free gel moisturizer engineered by Nivea providing 72-hour continuous hydration with purified hyaluronic acid.',
    3050.00,
    3507.00,
    '{"Net Weight":"50g","Key Ingredient":"Pure Hyaluronic Acid + Trehalose","Texture":"Lightweight Water Gel","Finish":"Dewy Non-Sticky"}',
    20,
    'BEAU-NIVE-011',
    'Nivea',
    4.0,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1598440947619-2c35fc9aa908?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Cetaphil Revitalift 1.5% Pure Hyaluronic Acid Serum 2025',
    'Anti-wrinkle plumping serum from Cetaphil formulated with micro and macro hyaluronic acid molecules.',
    1599.00,
    1903.00,
    '{"Volume":"30 ml","Actives":"0.5% Macro HA + 1.0% Micro HA","Benefits":"Plumps skin and reduces fine lines by 47%","Formula":"Paraben-Free"}',
    27,
    'BEAU-CETA-012',
    'Cetaphil',
    4.1,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1608248597359-593539829f8f?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Minimalist Soundarya Radiance Luxury Night Cream 2024',
    'Ayurvedic rejuvenating night cream by Minimalist infused with 24K Gold Bhasma and pure saffron extracts.',
    6295.00,
    7743.00,
    '{"Net Weight":"50g","Key Actives":"24 Karat Pure Gold Bhasma, Saffron, Ashwagandha","Benefits":"Enhances skin elasticity and golden glow"}',
    34,
    'BEAU-MINI-013',
    'Minimalist',
    4.2,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1570197788417-0e82375c9371?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'L''Oréal Vitamin C 16% Face Glow Serum 2025',
    'Potent antioxidant brightening serum from L''Oréal with stabilized ethyl ascorbic acid and ferulic acid.',
    1799.00,
    2285.00,
    '{"Volume":"20 ml","Key Actives":"16% Vitamin C + 1% Ferulic Acid + Fullerene","Packaging":"UV-Protected Amber Glass Dropper"}',
    41,
    'BEAU-L''OR-014',
    'L''Oréal',
    4.3,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1617897903246-719242758050?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Neutrogena Sunscreen SPF 50 PA++++ Invisible Gel 2024',
    'Broad-spectrum ultralight gel sunscreen by Neutrogena leaving zero white cast with photostable modern UV filters.',
    3299.00,
    4322.00,
    '{"Protection":"SPF 50+ PA++++ (UVA/UVB)","Volume":"50g","Finish":"Invisible Matte, Water Resistant (80 min)"}',
    48,
    'BEAU-NEUT-015',
    'Neutrogena',
    4.4,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1598440947619-2c35fc9aa908?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Forest Essentials Intense Repair Hair Mask with Argan Oil 2025',
    'Deep conditioning restorative hair treatment from Forest Essentials infused with pure Moroccan Argan oil and keratin.',
    2299.00,
    2644.00,
    '{"Net Weight":"200 ml","Ingredients":"Cold-Pressed Moroccan Argan Oil, Hydrolyzed Keratin","Hair Type":"Dry, Damaged, Chemically Treated"}',
    55,
    'BEAU-FORE-016',
    'Forest Essentials',
    4.5,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Maybelline 10% Niacinamide Face Serum with Zinc 2024',
    'Clean scientific formulation by Maybelline clinically proven to fade dark spots and balance excess sebum.',
    799.00,
    951.00,
    '{"Volume":"30 ml","Active Ingredients":"10% Pure Niacinamide + 1% Zinc PCA","Skin Type":"Oily, Acne-Prone & Blemish-Prone","pH":"5.5 - 6.5"}',
    62,
    'BEAU-MAYB-017',
    'Maybelline',
    4.6,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'The Body Shop Gentle Skin Cleanser Hydrating Formula 2025',
    'Dermatologist-recommended creamy soap-free cleanser from The Body Shop that cleanses without stripping natural moisture.',
    2399.00,
    2951.00,
    '{"Volume":"250 ml","Formula":"Non-Comedogenic & Fragrance-Free","Key Actives":"Niacinamide, Panthenol, Hydrating Glycerin"}',
    69,
    'BEAU-THE -018',
    'The Body Shop',
    4.7,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1556228720-195a672e8a03?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Innisfree Hydro Boost Hyaluronic Acid Water Gel 2024',
    'Oil-free gel moisturizer engineered by Innisfree providing 72-hour continuous hydration with purified hyaluronic acid.',
    1650.00,
    2096.00,
    '{"Net Weight":"50g","Key Ingredient":"Pure Hyaluronic Acid + Trehalose","Texture":"Lightweight Water Gel","Finish":"Dewy Non-Sticky"}',
    76,
    'BEAU-INNI-019',
    'Innisfree',
    4.8,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1598440947619-2c35fc9aa908?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Kama Ayurveda Revitalift 1.5% Pure Hyaluronic Acid Serum 2025',
    'Anti-wrinkle plumping serum from Kama Ayurveda formulated with micro and macro hyaluronic acid molecules.',
    3199.00,
    4191.00,
    '{"Volume":"30 ml","Actives":"0.5% Macro HA + 1.0% Micro HA","Benefits":"Plumps skin and reduces fine lines by 47%","Formula":"Paraben-Free"}',
    18,
    'BEAU-KAMA-020',
    'Kama Ayurveda',
    4.9,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1608248597359-593539829f8f?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Nivea Soundarya Radiance Luxury Night Cream 2024',
    'Ayurvedic rejuvenating night cream by Nivea infused with 24K Gold Bhasma and pure saffron extracts.',
    4895.00,
    5629.00,
    '{"Net Weight":"50g","Key Actives":"24 Karat Pure Gold Bhasma, Saffron, Ashwagandha","Benefits":"Enhances skin elasticity and golden glow"}',
    25,
    'BEAU-NIVE-021',
    'Nivea',
    4.0,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1570197788417-0e82375c9371?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Cetaphil Vitamin C 16% Face Glow Serum 2025',
    'Potent antioxidant brightening serum from Cetaphil with stabilized ethyl ascorbic acid and ferulic acid.',
    3399.00,
    4045.00,
    '{"Volume":"20 ml","Key Actives":"16% Vitamin C + 1% Ferulic Acid + Fullerene","Packaging":"UV-Protected Amber Glass Dropper"}',
    32,
    'BEAU-CETA-022',
    'Cetaphil',
    4.1,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1617897903246-719242758050?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Minimalist Sunscreen SPF 50 PA++++ Invisible Gel 2024',
    'Broad-spectrum ultralight gel sunscreen by Minimalist leaving zero white cast with photostable modern UV filters.',
    1899.00,
    2336.00,
    '{"Protection":"SPF 50+ PA++++ (UVA/UVB)","Volume":"50g","Finish":"Invisible Matte, Water Resistant (80 min)"}',
    39,
    'BEAU-MINI-023',
    'Minimalist',
    4.2,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1598440947619-2c35fc9aa908?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'L''Oréal Intense Repair Hair Mask with Argan Oil 2025',
    'Deep conditioning restorative hair treatment from L''Oréal infused with pure Moroccan Argan oil and keratin.',
    899.00,
    1142.00,
    '{"Net Weight":"200 ml","Ingredients":"Cold-Pressed Moroccan Argan Oil, Hydrolyzed Keratin","Hair Type":"Dry, Damaged, Chemically Treated"}',
    46,
    'BEAU-L''OR-024',
    'L''Oréal',
    4.3,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Neutrogena 10% Niacinamide Face Serum with Zinc 2024',
    'Clean scientific formulation by Neutrogena clinically proven to fade dark spots and balance excess sebum.',
    2399.00,
    3143.00,
    '{"Volume":"30 ml","Active Ingredients":"10% Pure Niacinamide + 1% Zinc PCA","Skin Type":"Oily, Acne-Prone & Blemish-Prone","pH":"5.5 - 6.5"}',
    53,
    'BEAU-NEUT-025',
    'Neutrogena',
    4.4,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Forest Essentials Gentle Skin Cleanser Hydrating Formula 2025',
    'Dermatologist-recommended creamy soap-free cleanser from Forest Essentials that cleanses without stripping natural moisture.',
    999.00,
    1149.00,
    '{"Volume":"250 ml","Formula":"Non-Comedogenic & Fragrance-Free","Key Actives":"Niacinamide, Panthenol, Hydrating Glycerin"}',
    60,
    'BEAU-FORE-026',
    'Forest Essentials',
    4.5,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1556228720-195a672e8a03?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Maybelline Hydro Boost Hyaluronic Acid Water Gel 2024',
    'Oil-free gel moisturizer engineered by Maybelline providing 72-hour continuous hydration with purified hyaluronic acid.',
    3250.00,
    3868.00,
    '{"Net Weight":"50g","Key Ingredient":"Pure Hyaluronic Acid + Trehalose","Texture":"Lightweight Water Gel","Finish":"Dewy Non-Sticky"}',
    67,
    'BEAU-MAYB-027',
    'Maybelline',
    4.6,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1598440947619-2c35fc9aa908?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'The Body Shop Revitalift 1.5% Pure Hyaluronic Acid Serum 2025',
    'Anti-wrinkle plumping serum from The Body Shop formulated with micro and macro hyaluronic acid molecules.',
    1799.00,
    2213.00,
    '{"Volume":"30 ml","Actives":"0.5% Macro HA + 1.0% Micro HA","Benefits":"Plumps skin and reduces fine lines by 47%","Formula":"Paraben-Free"}',
    74,
    'BEAU-THE -028',
    'The Body Shop',
    4.7,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1608248597359-593539829f8f?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Innisfree Soundarya Radiance Luxury Night Cream 2024',
    'Ayurvedic rejuvenating night cream by Innisfree infused with 24K Gold Bhasma and pure saffron extracts.',
    6495.00,
    8249.00,
    '{"Net Weight":"50g","Key Actives":"24 Karat Pure Gold Bhasma, Saffron, Ashwagandha","Benefits":"Enhances skin elasticity and golden glow"}',
    16,
    'BEAU-INNI-029',
    'Innisfree',
    4.8,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1570197788417-0e82375c9371?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Kama Ayurveda Vitamin C 16% Face Glow Serum 2025',
    'Potent antioxidant brightening serum from Kama Ayurveda with stabilized ethyl ascorbic acid and ferulic acid.',
    1999.00,
    2619.00,
    '{"Volume":"20 ml","Key Actives":"16% Vitamin C + 1% Ferulic Acid + Fullerene","Packaging":"UV-Protected Amber Glass Dropper"}',
    23,
    'BEAU-KAMA-030',
    'Kama Ayurveda',
    4.9,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1617897903246-719242758050?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Nivea Sunscreen SPF 50 PA++++ Invisible Gel 2024',
    'Broad-spectrum ultralight gel sunscreen by Nivea leaving zero white cast with photostable modern UV filters.',
    499.00,
    574.00,
    '{"Protection":"SPF 50+ PA++++ (UVA/UVB)","Volume":"50g","Finish":"Invisible Matte, Water Resistant (80 min)"}',
    30,
    'BEAU-NIVE-031',
    'Nivea',
    4.0,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1598440947619-2c35fc9aa908?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Cetaphil Intense Repair Hair Mask with Argan Oil 2025',
    'Deep conditioning restorative hair treatment from Cetaphil infused with pure Moroccan Argan oil and keratin.',
    2499.00,
    2974.00,
    '{"Net Weight":"200 ml","Ingredients":"Cold-Pressed Moroccan Argan Oil, Hydrolyzed Keratin","Hair Type":"Dry, Damaged, Chemically Treated"}',
    37,
    'BEAU-CETA-032',
    'Cetaphil',
    4.1,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Minimalist 10% Niacinamide Face Serum with Zinc 2024',
    'Clean scientific formulation by Minimalist clinically proven to fade dark spots and balance excess sebum.',
    999.00,
    1229.00,
    '{"Volume":"30 ml","Active Ingredients":"10% Pure Niacinamide + 1% Zinc PCA","Skin Type":"Oily, Acne-Prone & Blemish-Prone","pH":"5.5 - 6.5"}',
    44,
    'BEAU-MINI-033',
    'Minimalist',
    4.2,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'L''Oréal Gentle Skin Cleanser Hydrating Formula 2025',
    'Dermatologist-recommended creamy soap-free cleanser from L''Oréal that cleanses without stripping natural moisture.',
    2599.00,
    3301.00,
    '{"Volume":"250 ml","Formula":"Non-Comedogenic & Fragrance-Free","Key Actives":"Niacinamide, Panthenol, Hydrating Glycerin"}',
    51,
    'BEAU-L''OR-034',
    'L''Oréal',
    4.3,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1556228720-195a672e8a03?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Neutrogena Hydro Boost Hyaluronic Acid Water Gel 2024',
    'Oil-free gel moisturizer engineered by Neutrogena providing 72-hour continuous hydration with purified hyaluronic acid.',
    1850.00,
    2424.00,
    '{"Net Weight":"50g","Key Ingredient":"Pure Hyaluronic Acid + Trehalose","Texture":"Lightweight Water Gel","Finish":"Dewy Non-Sticky"}',
    58,
    'BEAU-NEUT-035',
    'Neutrogena',
    4.4,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1598440947619-2c35fc9aa908?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Forest Essentials Revitalift 1.5% Pure Hyaluronic Acid Serum 2025',
    'Anti-wrinkle plumping serum from Forest Essentials formulated with micro and macro hyaluronic acid molecules.',
    3399.00,
    3909.00,
    '{"Volume":"30 ml","Actives":"0.5% Macro HA + 1.0% Micro HA","Benefits":"Plumps skin and reduces fine lines by 47%","Formula":"Paraben-Free"}',
    65,
    'BEAU-FORE-036',
    'Forest Essentials',
    4.5,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1608248597359-593539829f8f?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Maybelline Soundarya Radiance Luxury Night Cream 2024',
    'Ayurvedic rejuvenating night cream by Maybelline infused with 24K Gold Bhasma and pure saffron extracts.',
    5095.00,
    6063.00,
    '{"Net Weight":"50g","Key Actives":"24 Karat Pure Gold Bhasma, Saffron, Ashwagandha","Benefits":"Enhances skin elasticity and golden glow"}',
    72,
    'BEAU-MAYB-037',
    'Maybelline',
    4.6,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1570197788417-0e82375c9371?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'The Body Shop Vitamin C 16% Face Glow Serum 2025',
    'Potent antioxidant brightening serum from The Body Shop with stabilized ethyl ascorbic acid and ferulic acid.',
    3599.00,
    4427.00,
    '{"Volume":"20 ml","Key Actives":"16% Vitamin C + 1% Ferulic Acid + Fullerene","Packaging":"UV-Protected Amber Glass Dropper"}',
    79,
    'BEAU-THE -038',
    'The Body Shop',
    4.7,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1617897903246-719242758050?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Innisfree Sunscreen SPF 50 PA++++ Invisible Gel 2024',
    'Broad-spectrum ultralight gel sunscreen by Innisfree leaving zero white cast with photostable modern UV filters.',
    2099.00,
    2666.00,
    '{"Protection":"SPF 50+ PA++++ (UVA/UVB)","Volume":"50g","Finish":"Invisible Matte, Water Resistant (80 min)"}',
    21,
    'BEAU-INNI-039',
    'Innisfree',
    4.8,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1598440947619-2c35fc9aa908?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Beauty'),
    'Kama Ayurveda Intense Repair Hair Mask with Argan Oil 2025',
    'Deep conditioning restorative hair treatment from Kama Ayurveda infused with pure Moroccan Argan oil and keratin.',
    1099.00,
    1440.00,
    '{"Net Weight":"200 ml","Ingredients":"Cold-Pressed Moroccan Argan Oil, Hydrolyzed Keratin","Hair Type":"Dry, Damaged, Chemically Treated"}',
    28,
    'BEAU-KAMA-040',
    'Kama Ayurveda',
    4.9,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Decathlon Cast Iron Rubber Hex Dumbbell Set 10kg Pair 2024',
    'Professional heavy-duty rubber coated hex dumbbells by Decathlon with ergonomic knurled chrome handles.',
    3499.00,
    4024.00,
    '{"Weight":"2x 10kg (Total 20kg)","Material":"Solid Cast Iron Core + Low-Odor Rubber Coating","Handle":"Contoured Ergonomic Knurled Steel"}',
    15,
    'SPRT-DECA-001',
    'Decathlon',
    4.0,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Nike Astrox 99 Pro High-Performance Badminton Racket 2025',
    'Head-heavy tour-level badminton racket engineered by Nike with Namd graphite for explosive power smashes.',
    14690.00,
    17481.00,
    '{"Balance":"Head-Heavy Power","Frame":"HM Graphite + Namd + Volume Cut Resin","Weight / Grip":"4U (Avg. 83g) G5","String Tension":"20-28 lbs"}',
    22,
    'SPRT-NIKE-002',
    'Nike',
    4.1,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Adidas Dri-FIT Moisture-Wicking Training Shorts 2024',
    'Breathable athletic workout shorts from Adidas featuring sweat-wicking Dri-FIT fabric and side zip pockets.',
    2695.00,
    3315.00,
    '{"Fabric":"100% Recycled Polyester Mesh","Inseam":"7 Inches","Waistband":"Elastic with Internal Drawcord"}',
    29,
    'SPRT-ADID-003',
    'Adidas',
    4.2,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1539185441755-769473a23570?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Puma Pro Staff 97 v14 Precision Tennis Racket 2025',
    'Classic precision graphite tennis racket engineered by Puma with Braid 45 technology for ultimate control.',
    27099.00,
    34416.00,
    '{"Head Size":"97 sq in / 626 sq cm","Weight Unstrung":"315 g","String Pattern":"16x19","Balance":"31 cm / 10 pts HL"}',
    36,
    'SPRT-PUMA-004',
    'Puma',
    4.3,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1617083934555-ac7d4fed8889?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Yonex Fastskin Hyper Elite Competitive Racing Goggles 2024',
    'Hydrodynamic FINA-approved competitive swim goggles by Yonex with anti-fog mirrored panoramic lenses.',
    6799.00,
    8907.00,
    '{"Lens":"Polycarbonate Mirrored (Anti-Fog, UV400)","Nose Bridge":"3 Interchangeable Hydrodynamic Bridges","Certification":"FINA Approved"}',
    43,
    'SPRT-YONE-005',
    'Yonex',
    4.4,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1530549387789-4c1017266635?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Speedo High-Density Non-Slip Yoga Mat 8mm with Strap 2025',
    'Eco-friendly dual-texture TPE exercise mat from Speedo offering superior joint cushioning and ground grip.',
    4399.00,
    5059.00,
    '{"Thickness":"8 mm Cushioning","Material":"100% Recyclable TPE (Eco-Friendly)","Dimensions":"183 cm x 61 cm","Accessories":"Carry Strap Included"}',
    50,
    'SPRT-SPEE-006',
    'Speedo',
    4.5,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Wilson Heavy Duty Resistance Exercise Bands Set 2024',
    'Multi-level 5-piece stackable latex workout bands by Wilson with heavy carabiners, handles, and door anchor.',
    2499.00,
    2974.00,
    '{"Resistance Range":"10 lbs to 150 lbs Stackable","Material":"100% Natural Malaysian Latex","Included":"5 Bands, 2 Handles, Door Anchor, Ankle Straps, Pouch"}',
    57,
    'SPRT-WILS-007',
    'Wilson',
    4.6,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1598289431512-b97b0917affc?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Cosco FIFA Quality Pro Match Thermal Football Size 5 2025',
    'Thermally bonded seamless official match football from Cosco offering consistent flight and zero water intake.',
    6199.00,
    7625.00,
    '{"Construction":"Thermally Bonded 32-Panel Seamless","Bladder":"Butyl Bladder with High Air Retention","Certification":"FIFA Quality Pro Certified"}',
    64,
    'SPRT-COSC-008',
    'Cosco',
    4.7,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Under Armour Cast Iron Rubber Hex Dumbbell Set 10kg Pair 2024',
    'Professional heavy-duty rubber coated hex dumbbells by Under Armour with ergonomic knurled chrome handles.',
    5099.00,
    6476.00,
    '{"Weight":"2x 10kg (Total 20kg)","Material":"Solid Cast Iron Core + Low-Odor Rubber Coating","Handle":"Contoured Ergonomic Knurled Steel"}',
    71,
    'SPRT-UNDE-009',
    'Under Armour',
    4.8,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Reebok Astrox 99 Pro High-Performance Badminton Racket 2025',
    'Head-heavy tour-level badminton racket engineered by Reebok with Namd graphite for explosive power smashes.',
    13290.00,
    17410.00,
    '{"Balance":"Head-Heavy Power","Frame":"HM Graphite + Namd + Volume Cut Resin","Weight / Grip":"4U (Avg. 83g) G5","String Tension":"20-28 lbs"}',
    78,
    'SPRT-REEB-010',
    'Reebok',
    4.9,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Decathlon Dri-FIT Moisture-Wicking Training Shorts 2024',
    'Breathable athletic workout shorts from Decathlon featuring sweat-wicking Dri-FIT fabric and side zip pockets.',
    4295.00,
    4939.00,
    '{"Fabric":"100% Recycled Polyester Mesh","Inseam":"7 Inches","Waistband":"Elastic with Internal Drawcord"}',
    20,
    'SPRT-DECA-011',
    'Decathlon',
    4.0,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1539185441755-769473a23570?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Nike Pro Staff 97 v14 Precision Tennis Racket 2025',
    'Classic precision graphite tennis racket engineered by Nike with Braid 45 technology for ultimate control.',
    25699.00,
    30582.00,
    '{"Head Size":"97 sq in / 626 sq cm","Weight Unstrung":"315 g","String Pattern":"16x19","Balance":"31 cm / 10 pts HL"}',
    27,
    'SPRT-NIKE-012',
    'Nike',
    4.1,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1617083934555-ac7d4fed8889?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Adidas Fastskin Hyper Elite Competitive Racing Goggles 2024',
    'Hydrodynamic FINA-approved competitive swim goggles by Adidas with anti-fog mirrored panoramic lenses.',
    8399.00,
    10331.00,
    '{"Lens":"Polycarbonate Mirrored (Anti-Fog, UV400)","Nose Bridge":"3 Interchangeable Hydrodynamic Bridges","Certification":"FINA Approved"}',
    34,
    'SPRT-ADID-013',
    'Adidas',
    4.2,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1530549387789-4c1017266635?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Puma High-Density Non-Slip Yoga Mat 8mm with Strap 2025',
    'Eco-friendly dual-texture TPE exercise mat from Puma offering superior joint cushioning and ground grip.',
    2999.00,
    3809.00,
    '{"Thickness":"8 mm Cushioning","Material":"100% Recyclable TPE (Eco-Friendly)","Dimensions":"183 cm x 61 cm","Accessories":"Carry Strap Included"}',
    41,
    'SPRT-PUMA-014',
    'Puma',
    4.3,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Yonex Heavy Duty Resistance Exercise Bands Set 2024',
    'Multi-level 5-piece stackable latex workout bands by Yonex with heavy carabiners, handles, and door anchor.',
    4099.00,
    5370.00,
    '{"Resistance Range":"10 lbs to 150 lbs Stackable","Material":"100% Natural Malaysian Latex","Included":"5 Bands, 2 Handles, Door Anchor, Ankle Straps, Pouch"}',
    48,
    'SPRT-YONE-015',
    'Yonex',
    4.4,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1598289431512-b97b0917affc?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Speedo FIFA Quality Pro Match Thermal Football Size 5 2025',
    'Thermally bonded seamless official match football from Speedo offering consistent flight and zero water intake.',
    4799.00,
    5519.00,
    '{"Construction":"Thermally Bonded 32-Panel Seamless","Bladder":"Butyl Bladder with High Air Retention","Certification":"FIFA Quality Pro Certified"}',
    55,
    'SPRT-SPEE-016',
    'Speedo',
    4.5,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Wilson Cast Iron Rubber Hex Dumbbell Set 10kg Pair 2024',
    'Professional heavy-duty rubber coated hex dumbbells by Wilson with ergonomic knurled chrome handles.',
    3699.00,
    4402.00,
    '{"Weight":"2x 10kg (Total 20kg)","Material":"Solid Cast Iron Core + Low-Odor Rubber Coating","Handle":"Contoured Ergonomic Knurled Steel"}',
    62,
    'SPRT-WILS-017',
    'Wilson',
    4.6,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Cosco Astrox 99 Pro High-Performance Badminton Racket 2025',
    'Head-heavy tour-level badminton racket engineered by Cosco with Namd graphite for explosive power smashes.',
    14890.00,
    18315.00,
    '{"Balance":"Head-Heavy Power","Frame":"HM Graphite + Namd + Volume Cut Resin","Weight / Grip":"4U (Avg. 83g) G5","String Tension":"20-28 lbs"}',
    69,
    'SPRT-COSC-018',
    'Cosco',
    4.7,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Under Armour Dri-FIT Moisture-Wicking Training Shorts 2024',
    'Breathable athletic workout shorts from Under Armour featuring sweat-wicking Dri-FIT fabric and side zip pockets.',
    2895.00,
    3677.00,
    '{"Fabric":"100% Recycled Polyester Mesh","Inseam":"7 Inches","Waistband":"Elastic with Internal Drawcord"}',
    76,
    'SPRT-UNDE-019',
    'Under Armour',
    4.8,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1539185441755-769473a23570?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Reebok Pro Staff 97 v14 Precision Tennis Racket 2025',
    'Classic precision graphite tennis racket engineered by Reebok with Braid 45 technology for ultimate control.',
    27299.00,
    35762.00,
    '{"Head Size":"97 sq in / 626 sq cm","Weight Unstrung":"315 g","String Pattern":"16x19","Balance":"31 cm / 10 pts HL"}',
    18,
    'SPRT-REEB-020',
    'Reebok',
    4.9,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1617083934555-ac7d4fed8889?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Decathlon Fastskin Hyper Elite Competitive Racing Goggles 2024',
    'Hydrodynamic FINA-approved competitive swim goggles by Decathlon with anti-fog mirrored panoramic lenses.',
    6999.00,
    8049.00,
    '{"Lens":"Polycarbonate Mirrored (Anti-Fog, UV400)","Nose Bridge":"3 Interchangeable Hydrodynamic Bridges","Certification":"FINA Approved"}',
    25,
    'SPRT-DECA-021',
    'Decathlon',
    4.0,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1530549387789-4c1017266635?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Nike High-Density Non-Slip Yoga Mat 8mm with Strap 2025',
    'Eco-friendly dual-texture TPE exercise mat from Nike offering superior joint cushioning and ground grip.',
    4599.00,
    5473.00,
    '{"Thickness":"8 mm Cushioning","Material":"100% Recyclable TPE (Eco-Friendly)","Dimensions":"183 cm x 61 cm","Accessories":"Carry Strap Included"}',
    32,
    'SPRT-NIKE-022',
    'Nike',
    4.1,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Adidas Heavy Duty Resistance Exercise Bands Set 2024',
    'Multi-level 5-piece stackable latex workout bands by Adidas with heavy carabiners, handles, and door anchor.',
    2699.00,
    3320.00,
    '{"Resistance Range":"10 lbs to 150 lbs Stackable","Material":"100% Natural Malaysian Latex","Included":"5 Bands, 2 Handles, Door Anchor, Ankle Straps, Pouch"}',
    39,
    'SPRT-ADID-023',
    'Adidas',
    4.2,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1598289431512-b97b0917affc?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Puma FIFA Quality Pro Match Thermal Football Size 5 2025',
    'Thermally bonded seamless official match football from Puma offering consistent flight and zero water intake.',
    3399.00,
    4317.00,
    '{"Construction":"Thermally Bonded 32-Panel Seamless","Bladder":"Butyl Bladder with High Air Retention","Certification":"FIFA Quality Pro Certified"}',
    46,
    'SPRT-PUMA-024',
    'Puma',
    4.3,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Yonex Cast Iron Rubber Hex Dumbbell Set 10kg Pair 2024',
    'Professional heavy-duty rubber coated hex dumbbells by Yonex with ergonomic knurled chrome handles.',
    5299.00,
    6942.00,
    '{"Weight":"2x 10kg (Total 20kg)","Material":"Solid Cast Iron Core + Low-Odor Rubber Coating","Handle":"Contoured Ergonomic Knurled Steel"}',
    53,
    'SPRT-YONE-025',
    'Yonex',
    4.4,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Speedo Astrox 99 Pro High-Performance Badminton Racket 2025',
    'Head-heavy tour-level badminton racket engineered by Speedo with Namd graphite for explosive power smashes.',
    13490.00,
    15513.00,
    '{"Balance":"Head-Heavy Power","Frame":"HM Graphite + Namd + Volume Cut Resin","Weight / Grip":"4U (Avg. 83g) G5","String Tension":"20-28 lbs"}',
    60,
    'SPRT-SPEE-026',
    'Speedo',
    4.5,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Wilson Dri-FIT Moisture-Wicking Training Shorts 2024',
    'Breathable athletic workout shorts from Wilson featuring sweat-wicking Dri-FIT fabric and side zip pockets.',
    4495.00,
    5349.00,
    '{"Fabric":"100% Recycled Polyester Mesh","Inseam":"7 Inches","Waistband":"Elastic with Internal Drawcord"}',
    67,
    'SPRT-WILS-027',
    'Wilson',
    4.6,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1539185441755-769473a23570?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Cosco Pro Staff 97 v14 Precision Tennis Racket 2025',
    'Classic precision graphite tennis racket engineered by Cosco with Braid 45 technology for ultimate control.',
    25899.00,
    31856.00,
    '{"Head Size":"97 sq in / 626 sq cm","Weight Unstrung":"315 g","String Pattern":"16x19","Balance":"31 cm / 10 pts HL"}',
    74,
    'SPRT-COSC-028',
    'Cosco',
    4.7,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1617083934555-ac7d4fed8889?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Under Armour Fastskin Hyper Elite Competitive Racing Goggles 2024',
    'Hydrodynamic FINA-approved competitive swim goggles by Under Armour with anti-fog mirrored panoramic lenses.',
    8599.00,
    10921.00,
    '{"Lens":"Polycarbonate Mirrored (Anti-Fog, UV400)","Nose Bridge":"3 Interchangeable Hydrodynamic Bridges","Certification":"FINA Approved"}',
    16,
    'SPRT-UNDE-029',
    'Under Armour',
    4.8,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1530549387789-4c1017266635?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Reebok High-Density Non-Slip Yoga Mat 8mm with Strap 2025',
    'Eco-friendly dual-texture TPE exercise mat from Reebok offering superior joint cushioning and ground grip.',
    3199.00,
    4191.00,
    '{"Thickness":"8 mm Cushioning","Material":"100% Recyclable TPE (Eco-Friendly)","Dimensions":"183 cm x 61 cm","Accessories":"Carry Strap Included"}',
    23,
    'SPRT-REEB-030',
    'Reebok',
    4.9,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Decathlon Heavy Duty Resistance Exercise Bands Set 2024',
    'Multi-level 5-piece stackable latex workout bands by Decathlon with heavy carabiners, handles, and door anchor.',
    1299.00,
    1494.00,
    '{"Resistance Range":"10 lbs to 150 lbs Stackable","Material":"100% Natural Malaysian Latex","Included":"5 Bands, 2 Handles, Door Anchor, Ankle Straps, Pouch"}',
    30,
    'SPRT-DECA-031',
    'Decathlon',
    4.0,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1598289431512-b97b0917affc?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Nike FIFA Quality Pro Match Thermal Football Size 5 2025',
    'Thermally bonded seamless official match football from Nike offering consistent flight and zero water intake.',
    4999.00,
    5949.00,
    '{"Construction":"Thermally Bonded 32-Panel Seamless","Bladder":"Butyl Bladder with High Air Retention","Certification":"FIFA Quality Pro Certified"}',
    37,
    'SPRT-NIKE-032',
    'Nike',
    4.1,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Adidas Cast Iron Rubber Hex Dumbbell Set 10kg Pair 2024',
    'Professional heavy-duty rubber coated hex dumbbells by Adidas with ergonomic knurled chrome handles.',
    3899.00,
    4796.00,
    '{"Weight":"2x 10kg (Total 20kg)","Material":"Solid Cast Iron Core + Low-Odor Rubber Coating","Handle":"Contoured Ergonomic Knurled Steel"}',
    44,
    'SPRT-ADID-033',
    'Adidas',
    4.2,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Puma Astrox 99 Pro High-Performance Badminton Racket 2025',
    'Head-heavy tour-level badminton racket engineered by Puma with Namd graphite for explosive power smashes.',
    15090.00,
    19164.00,
    '{"Balance":"Head-Heavy Power","Frame":"HM Graphite + Namd + Volume Cut Resin","Weight / Grip":"4U (Avg. 83g) G5","String Tension":"20-28 lbs"}',
    51,
    'SPRT-PUMA-034',
    'Puma',
    4.3,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Yonex Dri-FIT Moisture-Wicking Training Shorts 2024',
    'Breathable athletic workout shorts from Yonex featuring sweat-wicking Dri-FIT fabric and side zip pockets.',
    3095.00,
    4054.00,
    '{"Fabric":"100% Recycled Polyester Mesh","Inseam":"7 Inches","Waistband":"Elastic with Internal Drawcord"}',
    58,
    'SPRT-YONE-035',
    'Yonex',
    4.4,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1539185441755-769473a23570?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Speedo Pro Staff 97 v14 Precision Tennis Racket 2025',
    'Classic precision graphite tennis racket engineered by Speedo with Braid 45 technology for ultimate control.',
    27499.00,
    31624.00,
    '{"Head Size":"97 sq in / 626 sq cm","Weight Unstrung":"315 g","String Pattern":"16x19","Balance":"31 cm / 10 pts HL"}',
    65,
    'SPRT-SPEE-036',
    'Speedo',
    4.5,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1617083934555-ac7d4fed8889?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Wilson Fastskin Hyper Elite Competitive Racing Goggles 2024',
    'Hydrodynamic FINA-approved competitive swim goggles by Wilson with anti-fog mirrored panoramic lenses.',
    7199.00,
    8567.00,
    '{"Lens":"Polycarbonate Mirrored (Anti-Fog, UV400)","Nose Bridge":"3 Interchangeable Hydrodynamic Bridges","Certification":"FINA Approved"}',
    72,
    'SPRT-WILS-037',
    'Wilson',
    4.6,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1530549387789-4c1017266635?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Cosco High-Density Non-Slip Yoga Mat 8mm with Strap 2025',
    'Eco-friendly dual-texture TPE exercise mat from Cosco offering superior joint cushioning and ground grip.',
    4799.00,
    5903.00,
    '{"Thickness":"8 mm Cushioning","Material":"100% Recyclable TPE (Eco-Friendly)","Dimensions":"183 cm x 61 cm","Accessories":"Carry Strap Included"}',
    79,
    'SPRT-COSC-038',
    'Cosco',
    4.7,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Under Armour Heavy Duty Resistance Exercise Bands Set 2024',
    'Multi-level 5-piece stackable latex workout bands by Under Armour with heavy carabiners, handles, and door anchor.',
    2899.00,
    3682.00,
    '{"Resistance Range":"10 lbs to 150 lbs Stackable","Material":"100% Natural Malaysian Latex","Included":"5 Bands, 2 Handles, Door Anchor, Ankle Straps, Pouch"}',
    21,
    'SPRT-UNDE-039',
    'Under Armour',
    4.8,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1598289431512-b97b0917affc?w=800', TRUE FROM ins_prod;

WITH ins_prod AS (
  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)
  VALUES (
    (SELECT id FROM categories WHERE name = 'Sports & Fitness'),
    'Reebok FIFA Quality Pro Match Thermal Football Size 5 2025',
    'Thermally bonded seamless official match football from Reebok offering consistent flight and zero water intake.',
    3599.00,
    4715.00,
    '{"Construction":"Thermally Bonded 32-Panel Seamless","Bladder":"Butyl Bladder with High Air Retention","Certification":"FIFA Quality Pro Certified"}',
    28,
    'SPRT-REEB-040',
    'Reebok',
    4.9,
    TRUE,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
  )
  RETURNING id
)
INSERT INTO product_images (product_id, image_url, is_primary)
SELECT id, 'https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=800', TRUE FROM ins_prod;

COMMIT;
