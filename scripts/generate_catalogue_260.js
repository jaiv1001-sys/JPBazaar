const fs = require('fs');
const path = require('path');

// 1. ELECTRONICS (50 items)
const electronics = [
  {
    name: "Apple iPhone 15 Pro Max 256GB Titanium Blue",
    category: "Electronics",
    brand: "Apple",
    sku: "ELEC-APPL-001",
    price: 148900.00,
    originalPrice: 159900.00,
    stock: 45,
    description: "Forged in aerospace-grade titanium with the powerful A17 Pro chip and a versatile 48MP triple camera system.",
    specifications: JSON.stringify({ "Display": "6.7\" Super Retina XDR OLED", "Processor": "A17 Pro (3nm)", "Storage": "256GB NVMe", "Camera": "48MP + 12MP + 12MP", "Battery": "4441 mAh", "Weight": "221g" }),
    imageUrl: "https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=800"
  },
  {
    name: "Samsung Galaxy S24 Ultra 5G 512GB Titanium Gray",
    category: "Electronics",
    brand: "Samsung",
    sku: "ELEC-SMSG-002",
    price: 139999.00,
    originalPrice: 149999.00,
    stock: 50,
    description: "Meet Galaxy S24 Ultra with Galaxy AI, titanium shield, built-in S Pen, and 200MP Quad Telephoto camera.",
    specifications: JSON.stringify({ "Display": "6.8\" Dynamic AMOLED 2X 120Hz", "Processor": "Snapdragon 8 Gen 3", "RAM": "12GB LPDDR5X", "Storage": "512GB UFS 4.0", "Battery": "5000 mAh 45W", "Stylus": "Embedded S-Pen" }),
    imageUrl: "https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=800"
  },
  {
    name: "Apple MacBook Pro 16\" M3 Max 36GB 1TB Space Black",
    category: "Electronics",
    brand: "Apple",
    sku: "ELEC-APPL-003",
    price: 349900.00,
    originalPrice: 379900.00,
    stock: 20,
    description: "The most advanced Mac laptop ever with Apple M3 Max 16-core CPU, 40-core GPU, and Liquid Retina XDR display.",
    specifications: JSON.stringify({ "Display": "16.2\" Liquid Retina XDR 120Hz", "Processor": "Apple M3 Max (16-core CPU, 40-core GPU)", "Memory": "36GB Unified Memory", "Storage": "1TB PCIe Gen4 SSD", "Battery": "Up to 22 hours", "Ports": "3x Thunderbolt 4, HDMI, MagSafe 3" }),
    imageUrl: "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800"
  },
  {
    name: "Dell XPS 16 9640 Core Ultra 7 32GB RTX 4070",
    category: "Electronics",
    brand: "Dell",
    sku: "ELEC-DELL-004",
    price: 269990.00,
    originalPrice: 289990.00,
    stock: 25,
    description: "Sculpted in CNC machined aluminum and Gorilla Glass 3 with 4K OLED touch display and NVIDIA GeForce RTX 4070 graphics.",
    specifications: JSON.stringify({ "Display": "16.3\" 4K+ (3840x2400) OLED Touch", "Processor": "Intel Core Ultra 7 155H", "Graphics": "NVIDIA GeForce RTX 4070 8GB", "RAM": "32GB LPDDR5x", "Storage": "1TB NVMe SSD", "Weight": "2.13 kg" }),
    imageUrl: "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=800"
  },
  {
    name: "Sony Bravia XR 65\" 4K HDR QD-OLED Google TV",
    category: "Electronics",
    brand: "Sony",
    sku: "ELEC-SONY-005",
    price: 249900.00,
    originalPrice: 289900.00,
    stock: 15,
    description: "Cognitive Processor XR delivers unprecedented contrast and pure black levels with Quantum Dot OLED color technology.",
    specifications: JSON.stringify({ "Screen Size": "65 inches", "Panel Type": "QD-OLED 120Hz", "Processor": "Cognitive Processor XR", "Audio": "Acoustic Surface Audio+ (60W)", "Gaming": "HDMI 2.1 4K 120Hz VRR ALLM", "OS": "Google TV" }),
    imageUrl: "https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=800"
  },
  {
    name: "LG OLED evo C3 55\" 4K Smart TV",
    category: "Electronics",
    brand: "LG",
    sku: "ELEC-LG-006",
    price: 124990.00,
    originalPrice: 159990.00,
    stock: 30,
    description: "Self-lit OLED pixels paired with the alpha 9 AI Processor Gen6 for sensational picture quality and ultra-fast 0.1ms response gaming.",
    specifications: JSON.stringify({ "Screen Size": "55 inches", "Resolution": "4K Ultra HD (3840x2160)", "Refresh Rate": "120Hz Native", "HDR": "Dolby Vision, HDR10, HLG", "Gaming": "G-Sync, FreeSync, 0.1ms response", "OS": "webOS 23" }),
    imageUrl: "https://images.unsplash.com/photo-1577979749830-f1d742b96791?w=800"
  },
  {
    name: "Apple iPad Pro 12.9\" M2 Liquid Retina XDR 256GB Wi-Fi",
    category: "Electronics",
    brand: "Apple",
    sku: "ELEC-APPL-007",
    price: 112900.00,
    originalPrice: 122900.00,
    stock: 35,
    description: "Astounding performance with Apple M2 chip, 12.9-inch Liquid Retina XDR mini-LED display, and Apple Pencil hover technology.",
    specifications: JSON.stringify({ "Display": "12.9\" Liquid Retina XDR mini-LED (1600 nits peak)", "Chip": "Apple M2 (8-core CPU, 10-core GPU)", "Storage": "256GB", "Biometrics": "Face ID", "Connectivity": "Wi-Fi 6E, Thunderbolt 4", "Weight": "682g" }),
    imageUrl: "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=800"
  },
  {
    name: "Asus ROG Zephyrus G16 OLED 240Hz Intel Core Ultra 9",
    category: "Electronics",
    brand: "Asus",
    sku: "ELEC-ASUS-008",
    price: 219990.00,
    originalPrice: 249990.00,
    stock: 18,
    description: "Ultra-thin precision aluminum chassis with 2.5K 240Hz ROG Nebula OLED display, RTX 4080 graphics, and Intel Core Ultra 9 185H.",
    specifications: JSON.stringify({ "Display": "16\" 2.5K (2560x1600) 240Hz OLED", "Processor": "Intel Core Ultra 9 185H", "GPU": "NVIDIA GeForce RTX 4080 12GB", "RAM": "32GB LPDDR5X", "Storage": "1TB PCIe 4.0 SSD", "Weight": "1.85 kg" }),
    imageUrl: "https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=800"
  },
  {
    name: "HP Spectre x360 2-in-1 14\" Core Ultra 7 32GB 1TB",
    category: "Electronics",
    brand: "HP",
    sku: "ELEC-HP-009",
    price: 164990.00,
    originalPrice: 184990.00,
    stock: 22,
    description: "Convertible laptop featuring 2.8K OLED 120Hz IMAX Enhanced touch screen with 9MP AI camera and Poly Studio quad audio.",
    specifications: JSON.stringify({ "Display": "14\" 2.8K (2880x1800) OLED Touch 120Hz", "CPU": "Intel Core Ultra 7 155H", "RAM": "32GB LPDDR5x", "Storage": "1TB Gen4 NVMe SSD", "Audio": "Quad speakers tuned by Poly Studio", "Weight": "1.44 kg" }),
    imageUrl: "https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=800"
  },
  {
    name: "Lenovo ThinkPad X1 Carbon Gen 12 Ultrabook",
    category: "Electronics",
    brand: "Lenovo",
    sku: "ELEC-LENV-010",
    price: 189990.00,
    originalPrice: 209990.00,
    stock: 25,
    description: "Ultralight executive business flagship forged from carbon fiber and recycled magnesium with 2.8K OLED display.",
    specifications: JSON.stringify({ "Display": "14\" 2.8K OLED 120Hz 400 nits", "Processor": "Intel Core Ultra 7 155U vPro", "RAM": "32GB LPDDR5X", "Storage": "1TB PCIe Performance SSD", "Battery": "57Wh with Rapid Charge", "Weight": "1.09 kg" }),
    imageUrl: "https://images.unsplash.com/photo-1541807084-5c52b6b3adef?w=800"
  },
  {
    name: "OnePlus 12 5G 16GB 512GB Silky Black",
    category: "Electronics",
    brand: "OnePlus",
    sku: "ELEC-OPLS-011",
    price: 69999.00,
    originalPrice: 74999.00,
    stock: 60,
    description: "Flagship killer powered by Snapdragon 8 Gen 3, 4th Gen Hasselblad Camera System, and 100W SUPERVOOC charging.",
    specifications: JSON.stringify({ "Display": "6.82\" 2K ProXDR 120Hz LTPO (4500 nits peak)", "Processor": "Snapdragon 8 Gen 3", "RAM": "16GB LPDDR5X", "Storage": "512GB UFS 4.0", "Battery": "5400 mAh (100W Wired, 50W Wireless)" }),
    imageUrl: "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=800"
  },
  {
    name: "Google Pixel 8 Pro 256GB Obsidian",
    category: "Electronics",
    brand: "Google",
    sku: "ELEC-GOOG-012",
    price: 106999.00,
    originalPrice: 114999.00,
    stock: 35,
    description: "The AI flagship phone engineered by Google with Tensor G3 chip, fully upgraded triple camera system, and temperature sensor.",
    specifications: JSON.stringify({ "Display": "6.7\" Super Actua LTPO OLED 120Hz", "Chip": "Google Tensor G3 + Titan M2", "RAM": "12GB LPDDR5X", "Cameras": "50MP Main + 48MP Ultrawide + 48MP Telephoto 5x", "OS Support": "7 Years OS & Security updates" }),
    imageUrl: "https://images.unsplash.com/photo-1592899677977-9c10ca588bbd?w=800"
  },
  {
    name: "Sony PlayStation 5 Slim Console Disc Edition",
    category: "Electronics",
    brand: "Sony",
    sku: "ELEC-SONY-013",
    price: 54990.00,
    originalPrice: 59990.00,
    stock: 70,
    description: "PlayStation 5 Slim console packed with 1TB SSD storage, haptic feedback DualSense controller, and 4K 120Hz ray tracing graphics.",
    specifications: JSON.stringify({ "CPU": "AMD Zen 2 8-core 3.5GHz", "GPU": "AMD RDNA 2 10.3 TFLOPs", "Storage": "1TB Custom High-Speed NVMe SSD (5.5 GB/s)", "Optical Drive": "Ultra HD Blu-ray", "Video": "HDMI 2.1 4K 120Hz & 8K output" }),
    imageUrl: "https://images.unsplash.com/photo-1606813907291-d86efa9b94db?w=800"
  },
  {
    name: "Microsoft Xbox Series X 1TB Gaming Console",
    category: "Electronics",
    brand: "Microsoft",
    sku: "ELEC-MSFT-014",
    price: 52990.00,
    originalPrice: 57990.00,
    stock: 40,
    description: "The fastest, most powerful Xbox ever delivering true 4K gaming, 120 FPS, 12 teraflops of raw graphic processing power, and Quick Resume.",
    specifications: JSON.stringify({ "Processor": "Custom AMD 8-core Zen 2 3.8GHz", "Graphics": "12 TFLOPS 52 CUs @ 1.825 GHz RDNA 2", "Memory": "16GB GDDR6", "Storage": "1TB Custom NVMe SSD", "Drive": "4K UHD Blu-Ray" }),
    imageUrl: "https://images.unsplash.com/photo-1621259182978-fbf93132d53d?w=800"
  },
  {
    name: "Canon EOS R6 Mark II Mirrorless Camera Body",
    category: "Electronics",
    brand: "Canon",
    sku: "ELEC-CANN-015",
    price: 215995.00,
    originalPrice: 243995.00,
    stock: 12,
    description: "24.2MP full-frame CMOS sensor with up to 40 fps electronic shutter, 6K oversampled 4K 60p video, and in-body 8-stop image stabilization.",
    specifications: JSON.stringify({ "Sensor": "24.2MP Full-Frame CMOS", "Processor": "DIGIC X", "Continuous Shooting": "Up to 40 fps", "Video": "6K 60p RAW External, 4K 60p 10-Bit Internal", "Stabilization": "5-axis In-Body (up to 8 stops with IS lenses)" }),
    imageUrl: "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=800"
  },
  {
    name: "Sony Alpha 7 IV Full-Frame Hybrid Camera Body",
    category: "Electronics",
    brand: "Sony",
    sku: "ELEC-SONY-016",
    price: 219990.00,
    originalPrice: 242990.00,
    stock: 15,
    description: "Groundbreaking 33MP Exmor R back-illuminated sensor, BIONZ XR processing engine, 4K 60p 10-bit 4:2:2 recording, and Real-time Eye AF.",
    specifications: JSON.stringify({ "Sensor": "33MP Full-Frame Exmor R CMOS", "Autofocus": "759 phase-detection points (94% coverage)", "Video": "4K 60p 10-bit 4:2:2 S-Cinetone", "ISO Range": "100-51200 (expandable 50-204800)", "Card Slots": "Dual (CFexpress Type A / SD UHS-II)" }),
    imageUrl: "https://images.unsplash.com/photo-1502920917128-1aa500764cbd?w=800"
  },
  {
    name: "Fujifilm X-T5 Mirrorless Camera with 16-80mm Kit",
    category: "Electronics",
    brand: "Fujifilm",
    sku: "ELEC-FUJI-017",
    price: 199999.00,
    originalPrice: 219999.00,
    stock: 10,
    description: "Classic dial-based styling meets 40.2MP X-Trans CMOS 5 HR sensor with 5-axis 7.0-stop in-body stabilization and 6.2K video.",
    specifications: JSON.stringify({ "Sensor": "40.2MP X-Trans CMOS 5 HR", "Processor": "X-Processor 5", "Stabilization": "7.0 stops IBIS", "Video": "6.2K/30p 4:2:2 10-Bit Internal", "Film Simulations": "19 including Nostalgic Neg" }),
    imageUrl: "https://images.unsplash.com/photo-1512790182412-b19e6d62bc39?w=800"
  },
  {
    name: "DJI Mavic 3 Pro Cine Drone with RC Pro Controller",
    category: "Electronics",
    brand: "DJI",
    sku: "ELEC-DJI-018",
    price: 369900.00,
    originalPrice: 399900.00,
    stock: 8,
    description: "Triple-camera system featuring 4/3 CMOS Hasselblad camera, dual telephoto lenses, Apple ProRes encoding, and 43-min flight time.",
    specifications: JSON.stringify({ "Main Camera": "4/3 CMOS Hasselblad 20MP (5.1K/50fps)", "Telephoto": "70mm f/2.8 & 166mm f/3.4", "Video Codec": "Apple ProRes 422 HQ", "Max Flight Time": "43 minutes", "Transmission": "DJI O3+ up to 15 km" }),
    imageUrl: "https://images.unsplash.com/photo-1508614589041-895b88991e3e?w=800"
  },
  {
    name: "Samsung 49\" Odyssey OLED G9 Curved Gaming Monitor",
    category: "Electronics",
    brand: "Samsung",
    sku: "ELEC-SMSG-019",
    price: 149990.00,
    originalPrice: 179990.00,
    stock: 14,
    description: "Dual QHD 1800R curved gaming screen powered by Neo Quantum Processor Pro with 0.03ms response time and 240Hz refresh rate.",
    specifications: JSON.stringify({ "Screen Size": "49 inches 32:9", "Resolution": "Dual QHD (5120 x 1440)", "Panel": "OLED with Neo Quantum Processor Pro", "Refresh Rate": "240Hz", "Response Time": "0.03ms (GtG)", "Certification": "DisplayHDR True Black 400" }),
    imageUrl: "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=800"
  },
  {
    name: "LG 38\" UltraWide Curved Nano IPS Monitor",
    category: "Electronics",
    brand: "LG",
    sku: "ELEC-LG-020",
    price: 119999.00,
    originalPrice: 139999.00,
    stock: 16,
    description: "WQHD+ (3840x1600) 21:9 curved workspace monitor with Nano IPS panel, 98% DCI-P3 color gamut, and Thunderbolt 3 connectivity.",
    specifications: JSON.stringify({ "Screen Size": "37.5 inches", "Resolution": "WQHD+ (3840 x 1600)", "Panel": "Curved Nano IPS", "Color Space": "DCI-P3 98%", "Brightness": "450 nits (HDR 600)", "Ports": "Thunderbolt 3 (94W PD), 2x HDMI, DisplayPort" }),
    imageUrl: "https://images.unsplash.com/photo-1586210579191-33b45e38fa2c?w=800"
  }
];

// Helper to fill remaining items in a category with realistic variations
function generateVariations(categoryName, brandList, prefix, baseIndex, totalNeeded, templates) {
  const result = [];
  for (let i = 0; i < totalNeeded; i++) {
    const t = templates[i % templates.length];
    const brand = brandList[i % brandList.length];
    const indexStr = String(baseIndex + i).padStart(3, '0');
    const sku = `${prefix}-${brand.substring(0, 4).toUpperCase()}-${indexStr}`;
    const priceVariance = ((i * 47) % 30) * 100;
    const price = Math.round(t.basePrice + priceVariance);
    const originalPrice = Math.round(price * (1 + (0.15 + ((i % 5) * 0.04))));
    
    result.push({
      name: `${brand} ${t.modelName} ${t.suffix || (2024 + (i % 2))}`,
      category: categoryName,
      brand: brand,
      sku: sku,
      price: price,
      originalPrice: originalPrice,
      stock: 15 + ((i * 7) % 65),
      description: t.desc.replace('{BRAND}', brand),
      specifications: JSON.stringify(t.specs),
      imageUrl: t.img
    });
  }
  return result;
}

// 30 more Electronics to reach 50
const extraElectronicsTemplates = [
  { modelName: "Ultra Slim 4K Smart Monitor", basePrice: 42999, desc: "Ultra-thin high precision 4K display engineered by {BRAND} with USB-C 90W PD charging.", specs: { "Resolution": "3840x2160", "Refresh Rate": "60Hz", "Panel": "IPS HDR400", "Ports": "USB-C, HDMI 2.1" }, img: "https://images.unsplash.com/photo-1547119957-637f8679db1e?w=800" },
  { modelName: "Studio Wireless ANC Headset", basePrice: 24990, desc: "Acoustically tuned studio headphones by {BRAND} with hybrid active noise cancellation.", specs: { "Drivers": "40mm Beryllium", "Battery": "35 Hours", "Bluetooth": "5.3 LDAC", "Weight": "260g" }, img: "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800" },
  { modelName: "Compact Creator Mirrorless Camera", basePrice: 89990, desc: "Lightweight creator camera by {BRAND} with 4K 60p 10-bit recording and subject tracking AF.", specs: { "Sensor": "26MP APS-C", "Autofocus": "AI Subject Detect", "Video": "4K 60p", "Audio": "Dual stereo mics" }, img: "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=800" },
  { modelName: "Pro Gaming Mechanical Keyboard", basePrice: 14999, desc: "Aircraft-grade aluminum chassis with custom hot-swappable optical mechanical switches by {BRAND}.", specs: { "Switches": "Linear Optical", "Polling Rate": "8000Hz", "Lighting": "Per-Key RGB", "Connectivity": "Tri-Mode Wireless" }, img: "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=800" },
  { modelName: "Wi-Fi 7 Tri-Band Mesh Router", basePrice: 32990, desc: "Ultra-fast Wi-Fi 7 mesh system by {BRAND} with multi-gigabit speeds and dedicated backhaul.", specs: { "Speed": "Up to 19 Gbps", "Bands": "2.4GHz + 5GHz + 6GHz", "Coverage": "Up to 6000 sq ft", "Ports": "10GbE WAN + 4x 2.5GbE" }, img: "https://images.unsplash.com/photo-1544197150-b99a580bb7a8?w=800" },
  { modelName: "True Wireless Earbuds Pro", basePrice: 18990, desc: "Spatial audio earbuds by {BRAND} with intelligent adaptive noise reduction and wireless case.", specs: { "Drivers": "11mm Dual Driver", "Battery": "8h + 24h case", "Water Resistance": "IP54", "Codec": "LHDC 5.0" }, img: "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=800" }
];
const eleBrands = ["Sony", "Samsung", "Apple", "Dell", "HP", "Lenovo", "Asus", "Bose", "Sennheiser", "LG"];
const remainingElectronics = generateVariations("Electronics", eleBrands, "ELEC", 21, 30, extraElectronicsTemplates);
const allElectronics = [...electronics, ...remainingElectronics]; // 50 items

// 2. FASHION (50 items)
const fashBrands = ["Nike", "Adidas", "Puma", "Levi's", "H&M", "Zara", "Tommy Hilfiger", "Calvin Klein", "Under Armour", "Ralph Lauren"];
const fashionTemplates = [
  { modelName: "Tailored Slim Fit Wool Blazer", basePrice: 14990, desc: "Expertly tailored premium Italian wool blazer crafted by {BRAND} for formal and evening events.", specs: { "Material": "100% Virgin Wool", "Lining": "Cupro Silk", "Fit": "Tailored Slim", "Closure": "Two-Button" }, img: "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?w=800" },
  { modelName: "Classic 501 Original Fit Denim", basePrice: 4999, desc: "The iconic straight leg denim jeans by {BRAND} with signature copper rivets and button fly.", specs: { "Fabric": "100% Cotton Selvedge Denim", "Rise": "Mid Rise", "Leg": "Straight", "Wash": "Dark Indigo Stone Wash" }, img: "https://images.unsplash.com/photo-1542272604-780c96856592?w=800" },
  { modelName: "Ultraboost Running Sneakers", basePrice: 16999, desc: "High-performance energy return running shoes engineered by {BRAND} with breathable knit upper.", specs: { "Cushioning": "Full-Length Boost Foam", "Upper": "Primeknit+", "Outsole": "Continental Natural Rubber", "Weight": "290g" }, img: "https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=800" },
  { modelName: "Oversized Double-Breasted Trench Coat", basePrice: 12490, desc: "Timeless weather-resistant gabardine trench coat by {BRAND} featuring storm flap and belted waist.", specs: { "Fabric": "Water-Repellent Cotton Gabardine", "Length": "Midi", "Details": "Horn Buttons, Buckled Cuffs" }, img: "https://images.unsplash.com/photo-1544441893-675973e31985?w=800" },
  { modelName: "Pure Cashmere Crewneck Sweater", basePrice: 11999, desc: "Sumptuously soft grade-A Mongolian cashmere sweater by {BRAND} offering weightless warmth.", specs: { "Material": "100% Pure Cashmere (2-Ply)", "Neckline": "Ribbed Crewneck", "Care": "Dry Clean or Hand Wash" }, img: "https://images.unsplash.com/photo-1576871337632-b9aef4c17ab9?w=800" },
  { modelName: "Vintage Suede Leather Bomber Jacket", basePrice: 22990, desc: "Hand-finished goat suede bomber jacket from {BRAND} with satin lining and heavy gauge brass zippers.", specs: { "Shell": "100% Genuine Goat Suede", "Lining": "100% Viscose", "Trim": "Wool Ribbed Hem and Collar" }, img: "https://images.unsplash.com/photo-1551028719-00167b16eac5?w=800" },
  { modelName: "Tech Fleece Full-Zip Windrunner Hoodie", basePrice: 7995, desc: "Double-sided smooth spacer fleece engineered by {BRAND} for premium warmth without the bulk.", specs: { "Material": "66% Cotton, 34% Polyester Tech Fleece", "Pockets": "Zippered Sleeve & Kangaroo Pockets", "Fit": "Standard Athletic" }, img: "https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=800" },
  { modelName: "Pure Mulberry Silk Slip Dress", basePrice: 9499, desc: "Effortlessly elegant bias-cut mulberry silk slip dress by {BRAND} with delicate adjustable straps.", specs: { "Fabric": "100% 19 Momme Mulberry Silk", "Cut": "Bias Cut", "Finish": "French Seams", "Length": "Midi" }, img: "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=800" },
  { modelName: "Oxford Cotton Button-Down Shirt", basePrice: 3499, desc: "Durable woven pinpoint Oxford cotton casual dress shirt from {BRAND} with mother-of-pearl buttons.", specs: { "Weave": "100% Combed Oxford Cotton", "Collar": "Button-Down Casual", "Pattern": "Solid White & Sky Blue" }, img: "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=800" },
  { modelName: "Waterproof Winter Down Parka Jacket", basePrice: 18990, desc: "Extreme cold weather winter parka by {BRAND} insulated with 700-fill power responsibly sourced down.", specs: { "Insulation": "700 Fill Power Goose Down", "Shell": "2-Layer DryVent Waterproof", "Hood": "Removable Faux Fur" }, img: "https://images.unsplash.com/photo-1539533018447-63fcce667823?w=800" }
];
const allFashion = generateVariations("Fashion", fashBrands, "FASH", 1, 50, fashionTemplates); // 50 items

// 3. HOME & LIVING (40 items)
const homeBrands = ["IKEA", "Philips", "Havells", "Prestige", "Wakefit", "Dyson", "Tefal", "Morphy Richards", "Bajaj", "Milton"];
const homeTemplates = [
  { modelName: "Ergonomic High-Back Executive Mesh Chair", basePrice: 16999, desc: "Ergonomic mesh chair from {BRAND} featuring adaptive lumbar support and 4D adjustable armrests.", specs: { "Mechanism": "Synchro-Tilt with Multi-Lock", "Gas Lift": "Class 4 Heavy Duty", "Weight Capacity": "150 kg" }, img: "https://images.unsplash.com/photo-1580481077195-c22ae4206583?w=800" },
  { modelName: "Air Purifier with True HEPA H13 Filter", basePrice: 14499, desc: "High-efficiency smart room air purifier by {BRAND} filtering 99.97% of airborne pollutants down to 0.1 microns.", specs: { "CADR": "400 m3/h", "Filter": "3-Stage True HEPA + Activated Carbon", "Coverage": "Up to 550 sq ft", "Noise": "24 dB Sleep Mode" }, img: "https://images.unsplash.com/photo-1585771724684-38269d6639fd?w=800" },
  { modelName: "Smart BLDC Inverter Ceiling Fan with Remote", basePrice: 4299, desc: "Energy-saving brushless DC ceiling fan from {BRAND} consuming 65% less power with whisper-quiet operation.", specs: { "Motor": "100% Copper BLDC Motor (28W)", "Sweep": "1200 mm", "Speed": "360 RPM", "Control": "RF Remote + Smart App" }, img: "https://images.unsplash.com/photo-1513506003901-1e6a229e2d15?w=800" },
  { modelName: "Orthopedic Memory Foam Mattress King Size", basePrice: 18499, desc: "Pressure-relieving medium-firm orthopedic mattress engineered by {BRAND} with cooling gel infused memory foam.", specs: { "Size": "78x72x8 inches (King)", "Construction": "Cooling Gel Memory Foam + High Resilience Base", "Cover": "Breathable Bamboo Fabric" }, img: "https://images.unsplash.com/photo-1584132967334-10e028bd69f7?w=800" },
  { modelName: "Hard Anodized Non-Stick Cookware 5-Piece Set", basePrice: 5999, desc: "Heavy-gauge hard-anodized cookware collection from {BRAND} with durable multi-layer scratch-resistant coating.", specs: { "Pieces": "Kadhai, Fry Pan, Saucepan, Flat Tawa, Glass Lid", "Compatibility": "Induction & Gas Stove", "Coating": "PFOA-Free Ceramic Non-Stick" }, img: "https://images.unsplash.com/photo-1584990347449-3990b71b8765?w=800" },
  { modelName: "Digital Hot Air Fryer 6.5L with Rapid Air Tech", basePrice: 7999, desc: "Healthy oil-free cooking air fryer by {BRAND} with digital touchscreen and 12 one-touch cooking presets.", specs: { "Capacity": "6.5 Liters", "Power": "1800W", "Technology": "360 Rapid Hot Air Circulation", "Temperature": "80C to 200C" }, img: "https://images.unsplash.com/photo-1585515320310-259814833e62?w=800" },
  { modelName: "Cordless Stick Vacuum Cleaner with Laser Detect", basePrice: 34990, desc: "Powerful cordless vacuum from {BRAND} with laser illumination revealing hidden floor dust.", specs: { "Suction Power": "220 AW", "Runtime": "Up to 60 Minutes", "Filtration": "Fully-Sealed Whole-Machine HEPA", "Weight": "2.4 kg" }, img: "https://images.unsplash.com/photo-1558317374-067fb5f30001?w=800" },
  { modelName: "Smart Wi-Fi Robot Vacuum with Mopping", basePrice: 28999, desc: "Autonomous robotic vacuum cleaner by {BRAND} with LiDAR navigation, auto-recharge, and dual spinning mops.", specs: { "Suction": "5000 Pa", "Navigation": "PreciSense LiDAR Mapping", "Battery": "5200 mAh (180 min runtime)", "App": "No-Go Zones & Multi-Floor Mapping" }, img: "https://images.unsplash.com/photo-1618220179428-22790b461013?w=800" }
];
const allHome = generateVariations("Home & Living", homeBrands, "HOME", 1, 40, homeTemplates); // 40 items

// 4. ACCESSORIES (40 items)
const accBrands = ["boAt", "JBL", "Logitech", "Anker", "Noise", "Fossil", "Ray-Ban", "Casio", "Titan", "Sony"];
const accTemplates = [
  { modelName: "Classic Aviator Polarized Sunglasses", basePrice: 8490, desc: "Timeless teardrop frame sunglasses by {BRAND} with crystal polarized lenses providing 100% UV400 protection.", specs: { "Frame": "Monel Metal Gold Tone", "Lens": "G-15 Polarized Crystal Green", "Bridge": "14mm", "Temple": "135mm" }, img: "https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=800" },
  { modelName: "G-Shock Octagonal All-Black Watch", basePrice: 9995, desc: "Ultra-rugged carbon core guard shock-resistant timepiece by {BRAND} with 200m water resistance.", specs: { "Case": "Carbon Core Guard / Resin (48.5mm)", "Water Resistance": "200 meters (20 Bar)", "Glass": "Mineral Glass", "Battery": "3 Years" }, img: "https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=800" },
  { modelName: "Portable Waterproof Bluetooth Speaker IP67", basePrice: 9999, desc: "Rugged high-fidelity portable outdoor speaker by {BRAND} delivering punchy bass and 12-hour playtime.", specs: { "Power": "30W RMS (Woofer + Tweeter)", "Battery": "12 Hours", "Waterproof Rating": "IP67 Dust & Water", "Bluetooth": "5.2 Auracast" }, img: "https://images.unsplash.com/photo-1545454675-3531b543be5d?w=800" },
  { modelName: "Fast Charge 24000mAh Power Bank 140W", basePrice: 12999, desc: "Ultra-high power output portable charger from {BRAND} capable of fast charging two laptops simultaneously.", specs: { "Capacity": "24,000 mAh (86.4Wh)", "Total Output": "140W Max (Power Delivery 3.1)", "Display": "Smart Digital Color Screen", "Ports": "2x USB-C + 1x USB-A" }, img: "https://images.unsplash.com/photo-1609592426815-562db496525a?w=800" },
  { modelName: "Wireless Ergonomic Performance Mouse", basePrice: 9495, desc: "Master-series ergonomic precision mouse by {BRAND} with 8K DPI track-on-glass sensor and magnetic scroll wheel.", specs: { "Sensor": "Darkfield 8000 DPI", "Scroll": "MagSpeed Electromagnetic", "Battery": "70 Days on Full Charge", "Multi-Device": "Pair up to 3 Devices" }, img: "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=800" },
  { modelName: "Full Grain Leather Bi-Fold Wallet with RFID", basePrice: 3299, desc: "Handcrafted vegetable-tanned full-grain leather wallet from {BRAND} with built-in RFID blocking shield.", specs: { "Leather": "100% Italian Vegetable Tanned Cowhide", "Card Slots": "8 Slots + 2 Cash Compartments", "Security": "RFID Blocking Mesh" }, img: "https://images.unsplash.com/photo-1627123424574-724758594e93?w=800" },
  { modelName: "Smart Fitness Watch AMOLED with Calling", basePrice: 4999, desc: "Brilliant 1.43\" AMOLED display smartwatch by {BRAND} with Bluetooth voice calling and 100+ sports modes.", specs: { "Display": "1.43\" AMOLED (466x466, 1000 nits)", "Sensors": "Heart Rate, SpO2, Sleep, Stress", "Battery": "Up to 7 Days", "Water Resistance": "IP68" }, img: "https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=800" },
  { modelName: "Wireless Charging Pad 3-in-1 Foldable", basePrice: 4599, desc: "Sleek travel-ready 3-in-1 magnetic wireless charging station from {BRAND} for phone, smartwatch, and earbuds.", specs: { "Output": "15W Phone + 5W Watch + 5W Earbuds", "Technology": "Qi2 & MagSafe Compatible", "Design": "Foldable Aluminum Body" }, img: "https://images.unsplash.com/photo-1586105251261-72a756497a11?w=800" }
];
const allAccessories = generateVariations("Accessories", accBrands, "ACCS", 1, 40, accTemplates); // 40 items

// 5. BEAUTY (40 items)
const beauBrands = ["Nivea", "Cetaphil", "Minimalist", "L'Oréal", "Neutrogena", "Forest Essentials", "Maybelline", "The Body Shop", "Innisfree", "Kama Ayurveda"];
const beauTemplates = [
  { modelName: "10% Niacinamide Face Serum with Zinc", basePrice: 599, desc: "Clean scientific formulation by {BRAND} clinically proven to fade dark spots and balance excess sebum.", specs: { "Volume": "30 ml", "Active Ingredients": "10% Pure Niacinamide + 1% Zinc PCA", "Skin Type": "Oily, Acne-Prone & Blemish-Prone", "pH": "5.5 - 6.5" }, img: "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=800" },
  { modelName: "Gentle Skin Cleanser Hydrating Formula", basePrice: 499, desc: "Dermatologist-recommended creamy soap-free cleanser from {BRAND} that cleanses without stripping natural moisture.", specs: { "Volume": "250 ml", "Formula": "Non-Comedogenic & Fragrance-Free", "Key Actives": "Niacinamide, Panthenol, Hydrating Glycerin" }, img: "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=800" },
  { modelName: "Hydro Boost Hyaluronic Acid Water Gel", basePrice: 1050, desc: "Oil-free gel moisturizer engineered by {BRAND} providing 72-hour continuous hydration with purified hyaluronic acid.", specs: { "Net Weight": "50g", "Key Ingredient": "Pure Hyaluronic Acid + Trehalose", "Texture": "Lightweight Water Gel", "Finish": "Dewy Non-Sticky" }, img: "https://images.unsplash.com/photo-1598440947619-2c35fc9aa908?w=800" },
  { modelName: "Revitalift 1.5% Pure Hyaluronic Acid Serum", basePrice: 899, desc: "Anti-wrinkle plumping serum from {BRAND} formulated with micro and macro hyaluronic acid molecules.", specs: { "Volume": "30 ml", "Actives": "0.5% Macro HA + 1.0% Micro HA", "Benefits": "Plumps skin and reduces fine lines by 47%", "Formula": "Paraben-Free" }, img: "https://images.unsplash.com/photo-1608248597359-593539829f8f?w=800" },
  { modelName: "Soundarya Radiance Luxury Night Cream", basePrice: 3895, desc: "Ayurvedic rejuvenating night cream by {BRAND} infused with 24K Gold Bhasma and pure saffron extracts.", specs: { "Net Weight": "50g", "Key Actives": "24 Karat Pure Gold Bhasma, Saffron, Ashwagandha", "Benefits": "Enhances skin elasticity and golden glow" }, img: "https://images.unsplash.com/photo-1570197788417-0e82375c9371?w=800" },
  { modelName: "Vitamin C 16% Face Glow Serum", basePrice: 699, desc: "Potent antioxidant brightening serum from {BRAND} with stabilized ethyl ascorbic acid and ferulic acid.", specs: { "Volume": "20 ml", "Key Actives": "16% Vitamin C + 1% Ferulic Acid + Fullerene", "Packaging": "UV-Protected Amber Glass Dropper" }, img: "https://images.unsplash.com/photo-1617897903246-719242758050?w=800" },
  { modelName: "Sunscreen SPF 50 PA++++ Invisible Gel", basePrice: 499, desc: "Broad-spectrum ultralight gel sunscreen by {BRAND} leaving zero white cast with photostable modern UV filters.", specs: { "Protection": "SPF 50+ PA++++ (UVA/UVB)", "Volume": "50g", "Finish": "Invisible Matte, Water Resistant (80 min)" }, img: "https://images.unsplash.com/photo-1598440947619-2c35fc9aa908?w=800" },
  { modelName: "Intense Repair Hair Mask with Argan Oil", basePrice: 799, desc: "Deep conditioning restorative hair treatment from {BRAND} infused with pure Moroccan Argan oil and keratin.", specs: { "Net Weight": "200 ml", "Ingredients": "Cold-Pressed Moroccan Argan Oil, Hydrolyzed Keratin", "Hair Type": "Dry, Damaged, Chemically Treated" }, img: "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?w=800" }
];
const allBeauty = generateVariations("Beauty", beauBrands, "BEAU", 1, 40, beauTemplates); // 40 items

// 6. SPORTS & FITNESS (40 items)
const sportBrands = ["Decathlon", "Nike", "Adidas", "Puma", "Yonex", "Speedo", "Wilson", "Cosco", "Under Armour", "Reebok"];
const sportTemplates = [
  { modelName: "Cast Iron Rubber Hex Dumbbell Set 10kg Pair", basePrice: 3499, desc: "Professional heavy-duty rubber coated hex dumbbells by {BRAND} with ergonomic knurled chrome handles.", specs: { "Weight": "2x 10kg (Total 20kg)", "Material": "Solid Cast Iron Core + Low-Odor Rubber Coating", "Handle": "Contoured Ergonomic Knurled Steel" }, img: "https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=800" },
  { modelName: "Astrox 99 Pro High-Performance Badminton Racket", basePrice: 12990, desc: "Head-heavy tour-level badminton racket engineered by {BRAND} with Namd graphite for explosive power smashes.", specs: { "Balance": "Head-Heavy Power", "Frame": "HM Graphite + Namd + Volume Cut Resin", "Weight / Grip": "4U (Avg. 83g) G5", "String Tension": "20-28 lbs" }, img: "https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=800" },
  { modelName: "Dri-FIT Moisture-Wicking Training Shorts", basePrice: 2295, desc: "Breathable athletic workout shorts from {BRAND} featuring sweat-wicking Dri-FIT fabric and side zip pockets.", specs: { "Fabric": "100% Recycled Polyester Mesh", "Inseam": "7 Inches", "Waistband": "Elastic with Internal Drawcord" }, img: "https://images.unsplash.com/photo-1539185441755-769473a23570?w=800" },
  { modelName: "Pro Staff 97 v14 Precision Tennis Racket", basePrice: 24999, desc: "Classic precision graphite tennis racket engineered by {BRAND} with Braid 45 technology for ultimate control.", specs: { "Head Size": "97 sq in / 626 sq cm", "Weight Unstrung": "315 g", "String Pattern": "16x19", "Balance": "31 cm / 10 pts HL" }, img: "https://images.unsplash.com/photo-1617083934555-ac7d4fed8889?w=800" },
  { modelName: "Fastskin Hyper Elite Competitive Racing Goggles", basePrice: 5999, desc: "Hydrodynamic FINA-approved competitive swim goggles by {BRAND} with anti-fog mirrored panoramic lenses.", specs: { "Lens": "Polycarbonate Mirrored (Anti-Fog, UV400)", "Nose Bridge": "3 Interchangeable Hydrodynamic Bridges", "Certification": "FINA Approved" }, img: "https://images.unsplash.com/photo-1530549387789-4c1017266635?w=800" },
  { modelName: "High-Density Non-Slip Yoga Mat 8mm with Strap", basePrice: 1899, desc: "Eco-friendly dual-texture TPE exercise mat from {BRAND} offering superior joint cushioning and ground grip.", specs: { "Thickness": "8 mm Cushioning", "Material": "100% Recyclable TPE (Eco-Friendly)", "Dimensions": "183 cm x 61 cm", "Accessories": "Carry Strap Included" }, img: "https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=800" },
  { modelName: "Heavy Duty Resistance Exercise Bands Set", basePrice: 1299, desc: "Multi-level 5-piece stackable latex workout bands by {BRAND} with heavy carabiners, handles, and door anchor.", specs: { "Resistance Range": "10 lbs to 150 lbs Stackable", "Material": "100% Natural Malaysian Latex", "Included": "5 Bands, 2 Handles, Door Anchor, Ankle Straps, Pouch" }, img: "https://images.unsplash.com/photo-1598289431512-b97b0917affc?w=800" },
  { modelName: "FIFA Quality Pro Match Thermal Football Size 5", basePrice: 3299, desc: "Thermally bonded seamless official match football from {BRAND} offering consistent flight and zero water intake.", specs: { "Construction": "Thermally Bonded 32-Panel Seamless", "Bladder": "Butyl Bladder with High Air Retention", "Certification": "FIFA Quality Pro Certified" }, img: "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=800" }
];
const allSports = generateVariations("Sports & Fitness", sportBrands, "SPRT", 1, 40, sportTemplates); // 40 items

// Combined 260 products
const catalogue = [
  ...allElectronics,    // 50
  ...allFashion,        // 50
  ...allHome,           // 40
  ...allAccessories,    // 40
  ...allBeauty,         // 40
  ...allSports          // 40
];

console.log(`Generated exactly ${catalogue.length} products:`);
console.log(`- Electronics: ${allElectronics.length}`);
console.log(`- Fashion: ${allFashion.length}`);
console.log(`- Home & Living: ${allHome.length}`);
console.log(`- Accessories: ${allAccessories.length}`);
console.log(`- Beauty: ${allBeauty.length}`);
console.log(`- Sports & Fitness: ${allSports.length}`);

// Escape CSV field
function escapeCsv(val) {
  if (val === null || val === undefined) return '';
  const str = String(val);
  if (str.includes(',') || str.includes('"') || str.includes('\n') || str.includes('\r')) {
    return `"${str.replace(/"/g, '""')}"`;
  }
  return str;
}

// Write CSV
const dataDir = path.join(__dirname, '..', 'data');
if (!fs.existsSync(dataDir)) {
  fs.mkdirSync(dataDir, { recursive: true });
}

const csvHeaders = ["Name", "Category", "Brand", "SKU", "Price", "OriginalPrice", "Stock", "Description", "Specifications", "ImageUrl"];
const csvLines = [csvHeaders.join(',')];

catalogue.forEach(p => {
  const row = [
    escapeCsv(p.name),
    escapeCsv(p.category),
    escapeCsv(p.brand),
    escapeCsv(p.sku),
    escapeCsv(p.price.toFixed(2)),
    escapeCsv(p.originalPrice ? p.originalPrice.toFixed(2) : ''),
    escapeCsv(p.stock),
    escapeCsv(p.description),
    escapeCsv(p.specifications),
    escapeCsv(p.imageUrl)
  ];
  csvLines.push(row.join(','));
});

const csvPath = path.join(dataDir, 'catalogue_260_products.csv');
fs.writeFileSync(csvPath, csvLines.join('\n'), 'utf8');
console.log(`Wrote CSV catalogue file: ${csvPath} (${(fs.statSync(csvPath).size / 1024).toFixed(1)} KB)`);
