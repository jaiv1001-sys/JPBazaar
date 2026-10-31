const fs = require('fs');
const path = require('path');
const { catalogue } = require('./generate_catalogue_260.js');

console.log(`Loaded ${catalogue.length} products directly from generator.`);

function escapeSql(str) {
  if (str === null || str === undefined) return 'NULL';
  return "'" + String(str).replace(/'/g, "''") + "'";
}

let sql = `-- =============================================================================\n`;
sql += `-- JPBazaar: Seed 260 Products into PostgreSQL\n`;
sql += `-- =============================================================================\n\n`;
sql += `BEGIN;\n\n`;

// 1. Ensure categories exist
sql += `-- 1. Ensure all 6 departments exist\n`;
const categories = [
  { name: 'Electronics', desc: 'Cutting-edge laptops, smart devices, and high-performance computing.' },
  { name: 'Fashion', desc: 'Designer apparel, tailored clothing, luxury footwear, and seasonal collections.' },
  { name: 'Home & Living', desc: 'Smart home appliances, modern ergonomic furnishings, and culinary essentials.' },
  { name: 'Accessories', desc: 'Premium watches, designer eyewear, audio gear, and lifestyle accessories.' },
  { name: 'Beauty', desc: 'Dermatological skincare, luxury fragrances, cosmetics, and self-care.' },
  { name: 'Sports & Fitness', desc: 'Athletic performance gear, gym equipment, sportswear, and outdoor fitness.' }
];

categories.forEach(cat => {
  sql += `INSERT INTO categories (name, description, created_at) VALUES (${escapeSql(cat.name)}, ${escapeSql(cat.desc)}, CURRENT_TIMESTAMP) ON CONFLICT (name) DO UPDATE SET description = EXCLUDED.description;\n`;
});

sql += `\n-- 2. Clear old demo products and reset image links\n`;
sql += `DELETE FROM product_images WHERE product_id IN (SELECT id FROM products);\n`;
sql += `DELETE FROM products;\n\n`;

sql += `-- 3. Insert 260 products and their primary images\n`;
catalogue.forEach((p, idx) => {
  const stock = p.stock || 50;
  const originalPrice = p.originalPrice ? p.originalPrice : p.price;
  const rating = (4.0 + ((idx % 10) * 0.1)).toFixed(1);

  sql += `WITH ins_prod AS (\n`;
  sql += `  INSERT INTO products (category_id, name, description, price, original_price, specifications, stock_quantity, sku, brand, rating, active, version, created_at, updated_at)\n`;
  sql += `  VALUES (\n`;
  sql += `    (SELECT id FROM categories WHERE name = ${escapeSql(p.category)}),\n`;
  sql += `    ${escapeSql(p.name)},\n`;
  sql += `    ${escapeSql(p.description)},\n`;
  sql += `    ${p.price.toFixed(2)},\n`;
  sql += `    ${originalPrice.toFixed(2)},\n`;
  sql += `    ${escapeSql(p.specifications)},\n`;
  sql += `    ${stock},\n`;
  sql += `    ${escapeSql(p.sku)},\n`;
  sql += `    ${escapeSql(p.brand)},\n`;
  sql += `    ${rating},\n`;
  sql += `    TRUE,\n`;
  sql += `    0,\n`;
  sql += `    CURRENT_TIMESTAMP,\n`;
  sql += `    CURRENT_TIMESTAMP\n`;
  sql += `  )\n`;
  sql += `  RETURNING id\n`;
  sql += `)\n`;
  sql += `INSERT INTO product_images (product_id, image_url, is_primary)\n`;
  sql += `SELECT id, ${escapeSql(p.imageUrl)}, TRUE FROM ins_prod;\n\n`;
});

sql += `COMMIT;\n`;

const outPath = path.join(__dirname, '..', 'sql', 'seed_260_catalogue.sql');
fs.writeFileSync(outPath, sql, 'utf8');
console.log(`Wrote ${outPath} successfully with ${catalogue.length} products.`);
