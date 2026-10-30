/**
 * JPBazaar Storefront & Single Page Application Engine
 * Connects directly to Spring Boot 3 REST APIs
 */

// Application State
const state = {
  products: [],
  categories: [],
  selectedCategory: "ALL",
  searchQuery: "",
  sortBy: "featured",
  cart: [],
  wishlist: new Set(),
  user: null,
  token: localStorage.getItem("jpbazaar_token") || null,
  isAdmin: false,
};

// API Helper
const API = {
  async get(url, auth = false) {
    const headers = { "Content-Type": "application/json" };
    if (auth && state.token) headers["Authorization"] = `Bearer ${state.token}`;
    const res = await fetch(url, { headers });
    return handleResponse(res);
  },

  async post(url, data, auth = false) {
    const headers = { "Content-Type": "application/json" };
    if (auth && state.token) headers["Authorization"] = `Bearer ${state.token}`;
    const credentials = { method: "POST", headers, body: JSON.stringify(data) };
    const res = await fetch(url, credentials);
    return handleResponse(res);
  },

  async put(url, data, auth = false) {
    const headers = { "Content-Type": "application/json" };
    if (auth && state.token) headers["Authorization"] = `Bearer ${state.token}`;
    const res = await fetch(url, { method: "PUT", headers, body: JSON.stringify(data) });
    return handleResponse(res);
  },

  async delete(url, auth = false) {
    const headers = { "Content-Type": "application/json" };
    if (auth && state.token) headers["Authorization"] = `Bearer ${state.token}`;
    const res = await fetch(url, { method: "DELETE", headers });
    return handleResponse(res);
  }
};

async function handleResponse(res) {
  const json = await res.json().catch(() => ({}));
  if (!res.ok) {
    const errMsg = json.message || json.error || `HTTP error ${res.status}`;
    throw new Error(errMsg);
  }
  return json.data !== undefined ? json.data : json;
}

// Toast Notifications
function showToast(message, type = "info") {
  const container = document.getElementById("toastContainer");
  const toast = document.createElement("div");
  toast.className = `toast ${type}`;
  toast.innerHTML = `<span>${type === "success" ? "✓" : type === "error" ? "⚠" : "ℹ"}</span> <span>${message}</span>`;
  container.appendChild(toast);
  setTimeout(() => {
    toast.style.opacity = "0";
    toast.style.transform = "translateY(10px)";
    setTimeout(() => toast.remove(), 250);
  }, 3500);
}

// Initializer
document.addEventListener("DOMContentLoaded", async () => {
  initAuth();
  await loadCategories();
  await loadProducts();
  setupEventListeners();
  if (state.token) {
    await syncUserCart();
    await syncUserWishlist();
  }
});

function initAuth() {
  const savedUser = localStorage.getItem("jpbazaar_user");
  if (state.token && savedUser) {
    try {
      state.user = JSON.parse(savedUser);
      state.isAdmin = state.user.role === "ADMIN";
      renderAuthNav();
    } catch (e) {
      logout();
    }
  } else {
    renderAuthNav();
  }
}

function renderAuthNav() {
  const authContainer = document.getElementById("authNavContainer");
  const adminBtn = document.getElementById("btnAdminNav");

  if (state.user) {
    authContainer.innerHTML = `
      <button class="nav-btn" onclick="openProfileModal()">
        <span>👤 ${escapeHtml(state.user.firstName)}</span>
      </button>
      <button class="nav-btn" onclick="logout()" title="Logout">
        <span>🚪</span>
      </button>
    `;
    if (state.isAdmin) {
      adminBtn.style.display = "inline-flex";
    } else {
      adminBtn.style.display = "none";
    }
  } else {
    authContainer.innerHTML = `
      <button class="nav-btn primary" onclick="openAuthModal('login')">
        <span>Sign In</span>
      </button>
    `;
    adminBtn.style.display = "none";
  }
}

// Categories & Products Loading
async function loadCategories() {
  try {
    const data = await API.get("/api/categories");
    state.categories = Array.isArray(data) ? data : (data.content || []);
    renderCategoryPills();
  } catch (err) {
    console.error("Failed to load categories:", err);
  }
}

function renderCategoryPills() {
  const container = document.getElementById("categoryPills");
  let html = `<button class="cat-pill ${state.selectedCategory === 'ALL' ? 'active' : ''}" onclick="selectCategory('ALL')">All Products</button>`;
  state.categories.forEach(cat => {
    const isActive = state.selectedCategory === String(cat.id);
    html += `<button class="cat-pill ${isActive ? 'active' : ''}" onclick="selectCategory('${cat.id}')">${escapeHtml(cat.name)}</button>`;
  });
  container.innerHTML = html;
}

function selectCategory(catId) {
  state.selectedCategory = catId;
  renderCategoryPills();
  renderProducts();
}

async function loadProducts() {
  try {
    const data = await API.get("/api/products?page=0&size=50");
    state.products = Array.isArray(data) ? data : (data.content || []);
    renderProducts();
  } catch (err) {
    showToast("Failed to load catalog: " + err.message, "error");
  }
}

function renderProducts() {
  const grid = document.getElementById("productGrid");
  let filtered = [...state.products];

  // Category Filter
  if (state.selectedCategory !== "ALL") {
    filtered = filtered.filter(p => p.categoryId && String(p.categoryId) === state.selectedCategory);
  }

  // Search Filter
  if (state.searchQuery.trim()) {
    const q = state.searchQuery.toLowerCase();
    filtered = filtered.filter(p =>
      p.name.toLowerCase().includes(q) ||
      (p.brand && p.brand.toLowerCase().includes(q)) ||
      (p.description && p.description.toLowerCase().includes(q))
    );
  }

  // Sort
  if (state.sortBy === "price-low") {
    filtered.sort((a, b) => a.price - b.price);
  } else if (state.sortBy === "price-high") {
    filtered.sort((a, b) => b.price - a.price);
  } else if (state.sortBy === "rating") {
    filtered.sort((a, b) => (b.rating || 0) - (a.rating || 0));
  }

  if (filtered.length === 0) {
    grid.innerHTML = `
      <div style="grid-column: 1/-1; text-align: center; padding: 64px 20px;">
        <h3 style="font-size: 1.25rem; font-weight: 700; color: var(--gray-700); margin-bottom: 8px;">No products match your criteria</h3>
        <p style="color: var(--gray-500); font-size: 0.9375rem;">Try adjusting your search terms or filters.</p>
      </div>
    `;
    return;
  }

  grid.innerHTML = filtered.map(product => {
    const primaryImg = (product.images && product.images.length > 0)
      ? (product.images.find(i => i.isPrimary) || product.images[0]).imageUrl
      : "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=600&q=80";

    const inStock = product.stockQuantity > 0;
    const isWished = state.wishlist.has(product.id);

    return `
      <div class="product-card">
        <div class="card-img-wrapper" onclick="openProductModal(${product.id})">
          <img class="card-img" src="${escapeHtml(primaryImg)}" alt="${escapeHtml(product.name)}" loading="lazy" />
          <span class="card-badge">${inStock ? (product.stockQuantity < 5 ? `Only ${product.stockQuantity} left` : 'In Stock') : 'Sold Out'}</span>
          <button class="card-wish-btn ${isWished ? 'active' : ''}" onclick="event.stopPropagation(); toggleWishlist(${product.id})" title="Wishlist">
            ${isWished ? '♥' : '♡'}
          </button>
        </div>
        <div class="card-content">
          <span class="card-category">${escapeHtml(product.categoryName || product.brand || 'JPBazaar')}</span>
          <h4 class="card-title" onclick="openProductModal(${product.id})">${escapeHtml(product.name)}</h4>
          <div class="card-rating">
            <span class="rating-stars">★</span>
            <span>${product.rating ? Number(product.rating).toFixed(1) : '5.0'}</span>
            <span style="color: var(--gray-300);">•</span>
            <span style="color: var(--gray-500); font-size: 0.75rem;">SKU: ${escapeHtml(product.sku)}</span>
          </div>
          <div class="card-footer">
            <span class="card-price">$${Number(product.price).toFixed(2)}</span>
            <button class="btn-add-cart" onclick="addToCart(${product.id})" ${!inStock ? 'disabled style="opacity: 0.5; cursor: not-allowed;"' : ''}>
              <span>+ Add to Cart</span>
            </button>
          </div>
        </div>
      </div>
    `;
  }).join("");
}

// Cart Management
async function addToCart(productId) {
  const product = state.products.find(p => p.id === productId);
  if (!product) return;

  if (state.token) {
    try {
      await API.post("/api/cart/items", { productId, quantity: 1 }, true);
      await syncUserCart();
      showToast(`Added "${product.name}" to your cart!`, "success");
    } catch (err) {
      showToast(err.message, "error");
    }
  } else {
    // Local session cart
    const existing = state.cart.find(i => i.productId === productId);
    if (existing) {
      existing.quantity += 1;
    } else {
      const primaryImg = (product.images && product.images.length > 0)
        ? (product.images.find(i => i.isPrimary) || product.images[0]).imageUrl
        : "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=600&q=80";

      state.cart.push({
        id: Date.now(),
        productId: product.id,
        productName: product.name,
        price: product.price,
        quantity: 1,
        imageUrl: primaryImg,
      });
    }
    renderCart();
    showToast(`Added "${product.name}" to cart!`, "success");
  }
}

async function updateCartItemQty(itemId, newQty) {
  if (newQty <= 0) {
    return removeCartItem(itemId);
  }

  if (state.token) {
    try {
      await API.put(`/api/cart/items/${itemId}`, { quantity: newQty }, true);
      await syncUserCart();
    } catch (err) {
      showToast(err.message, "error");
    }
  } else {
    const item = state.cart.find(i => i.id === itemId);
    if (item) {
      item.quantity = newQty;
      renderCart();
    }
  }
}

async function removeCartItem(itemId) {
  if (state.token) {
    try {
      await API.delete(`/api/cart/items/${itemId}`, true);
      await syncUserCart();
      showToast("Item removed from cart", "info");
    } catch (err) {
      showToast(err.message, "error");
    }
  } else {
    state.cart = state.cart.filter(i => i.id !== itemId);
    renderCart();
    showToast("Item removed", "info");
  }
}

async function syncUserCart() {
  try {
    const data = await API.get("/api/cart", true);
    state.cart = data.items || [];
    renderCart();
  } catch (e) {
    console.error("Cart sync failed:", e);
  }
}

function renderCart() {
  const countBadge = document.getElementById("cartCountBadge");
  const cartBody = document.getElementById("cartItemsContainer");
  const subtotalEl = document.getElementById("cartSubtotal");
  const totalEl = document.getElementById("cartTotal");

  const totalItems = state.cart.reduce((sum, item) => sum + item.quantity, 0);
  countBadge.textContent = totalItems;

  const subtotal = state.cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  subtotalEl.textContent = `$${subtotal.toFixed(2)}`;
  totalEl.textContent = `$${subtotal.toFixed(2)}`;

  if (state.cart.length === 0) {
    cartBody.innerHTML = `
      <div style="text-align: center; padding: 48px 16px; color: var(--gray-500);">
        <p style="font-size: 2.5rem; margin-bottom: 8px;">🛒</p>
        <p style="font-weight: 700; color: var(--dark);">Your cart is empty</p>
        <p style="font-size: 0.875rem;">Discover our premium selection and add items!</p>
      </div>
    `;
    return;
  }

  cartBody.innerHTML = state.cart.map(item => `
    <div class="cart-item-row">
      <img class="cart-item-thumb" src="${escapeHtml(item.imageUrl || 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=600&q=80')}" alt="" />
      <div class="cart-item-info">
        <h5 class="cart-item-name">${escapeHtml(item.productName)}</h5>
        <p class="cart-item-price">$${Number(item.price).toFixed(2)}</p>
      </div>
      <div class="cart-qty-ctrl">
        <button class="qty-btn" onclick="updateCartItemQty(${item.id}, ${item.quantity - 1})">-</button>
        <span class="qty-val">${item.quantity}</span>
        <button class="qty-btn" onclick="updateCartItemQty(${item.id}, ${item.quantity + 1})">+</button>
      </div>
      <button class="cart-del-btn" onclick="removeCartItem(${item.id})" title="Remove">✕</button>
    </div>
  `).join("");
}

function toggleCartDrawer() {
  const drawer = document.getElementById("cartDrawerBackdrop");
  drawer.classList.toggle("active");
}

// Wishlist
async function toggleWishlist(productId) {
  if (!state.token) {
    showToast("Please sign in to save items to your wishlist", "info");
    openAuthModal("login");
    return;
  }

  try {
    if (state.wishlist.has(productId)) {
      await API.delete(`/api/wishlist/items/${productId}`, true);
      state.wishlist.delete(productId);
      showToast("Removed from wishlist", "info");
    } else {
      await API.post("/api/wishlist/items", { productId }, true);
      state.wishlist.add(productId);
      showToast("Added to wishlist!", "success");
    }
    updateWishlistBadge();
    renderProducts();
  } catch (err) {
    showToast(err.message, "error");
  }
}

async function syncUserWishlist() {
  try {
    const data = await API.get("/api/wishlist", true);
    state.wishlist.clear();
    if (data.items) {
      data.items.forEach(i => state.wishlist.add(i.productId));
    }
    updateWishlistBadge();
  } catch (e) {
    console.error("Wishlist sync failed:", e);
  }
}

function updateWishlistBadge() {
  document.getElementById("wishlistCountBadge").textContent = state.wishlist.size;
}

// Product Details Modal
async function openProductModal(productId) {
  const product = state.products.find(p => p.id === productId);
  if (!product) return;

  const modal = document.getElementById("productModalBackdrop");
  const modalBody = document.getElementById("productModalBody");

  const primaryImg = (product.images && product.images.length > 0)
    ? (product.images.find(i => i.isPrimary) || product.images[0]).imageUrl
    : "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=600&q=80";

  modalBody.innerHTML = `
    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 24px;">
      <div>
        <img src="${escapeHtml(primaryImg)}" alt="${escapeHtml(product.name)}" style="width: 100%; border-radius: var(--radius-md); object-fit: cover;" />
      </div>
      <div>
        <span style="font-size: 0.75rem; font-weight: 700; color: var(--primary); text-transform: uppercase;">${escapeHtml(product.brand || 'JPBazaar')}</span>
        <h3 style="font-size: 1.4rem; font-weight: 800; color: var(--dark); margin: 4px 0 8px;">${escapeHtml(product.name)}</h3>
        <p style="font-size: 1.5rem; font-weight: 800; color: var(--dark); margin-bottom: 12px;">$${Number(product.price).toFixed(2)}</p>
        <p style="color: var(--gray-700); font-size: 0.875rem; line-height: 1.6; margin-bottom: 20px;">${escapeHtml(product.description || 'No description provided.')}</p>
        
        <div style="background: var(--gray-50); padding: 12px; border-radius: var(--radius-md); margin-bottom: 20px; font-size: 0.8125rem;">
          <p><strong>SKU:</strong> ${escapeHtml(product.sku)}</p>
          <p><strong>Stock Level:</strong> ${product.stockQuantity} units available</p>
          <p><strong>Customer Rating:</strong> ⭐ ${product.rating ? Number(product.rating).toFixed(1) : '5.0'} / 5.0</p>
        </div>

        <button class="btn-checkout" onclick="addToCart(${product.id}); closeAllModals();">
          <span>+ Add to Shopping Cart</span>
        </button>
      </div>
    </div>
  `;

  modal.classList.add("active");
}

// Checkout & Orders
function proceedToCheckout() {
  if (state.cart.length === 0) {
    showToast("Your cart is empty!", "error");
    return;
  }

  if (!state.token) {
    showToast("Please sign in to complete your checkout", "info");
    toggleCartDrawer();
    openAuthModal("login");
    return;
  }

  toggleCartDrawer();
  const modal = document.getElementById("checkoutModalBackdrop");
  modal.classList.add("active");
}

async function handlePlaceOrder(e) {
  e.preventDefault();
  const addressLine = document.getElementById("chkAddress").value;
  const city = document.getElementById("chkCity").value;
  const stateVal = document.getElementById("chkState").value;
  const postalCode = document.getElementById("chkZip").value;
  const country = document.getElementById("chkCountry").value;
  const paymentMethod = document.getElementById("chkPaymentMethod").value;

  try {
    // 1. Create or ensure delivery address
    const address = await API.post("/api/addresses", {
      addressLine,
      city,
      state: stateVal,
      postalCode,
      country,
      isDefault: true,
    }, true);

    // 2. Create Order
    const order = await API.post("/api/orders", {
      addressId: address.id,
      paymentMethod,
    }, true);

    // 3. Process Payment
    await API.post("/api/payments/process", {
      orderId: order.id,
      paymentMethod,
      cardNumber: paymentMethod === "CARD" ? "4111222233334444" : null,
      expiryMonth: paymentMethod === "CARD" ? "12" : null,
      expiryYear: paymentMethod === "CARD" ? "2028" : null,
      cvv: paymentMethod === "CARD" ? "123" : null,
    }, true);

    closeAllModals();
    await syncUserCart();

    showToast(`Order #${order.id} placed and confirmed successfully! 🎉`, "success");
    openOrderSuccessModal(order.id, order.totalAmount);
  } catch (err) {
    showToast(err.message, "error");
  }
}

function openOrderSuccessModal(orderId, amount) {
  const modal = document.getElementById("successModalBackdrop");
  document.getElementById("successOrderNum").textContent = `#${orderId}`;
  document.getElementById("successOrderTotal").textContent = `$${Number(amount).toFixed(2)}`;
  modal.classList.add("active");
}

// User Profile & Order History
async function openProfileModal() {
  const modal = document.getElementById("profileModalBackdrop");
  document.getElementById("profileUserName").textContent = `${state.user.firstName} ${state.user.lastName}`;
  document.getElementById("profileUserEmail").textContent = state.user.email;
  document.getElementById("profileUserRole").textContent = state.user.role;

  try {
    const orders = await API.get("/api/orders/my-orders", true);
    const container = document.getElementById("profileOrdersContainer");
    if (!orders || orders.length === 0) {
      container.innerHTML = `<p style="color: var(--gray-500); font-size: 0.875rem;">No past orders found.</p>`;
    } else {
      container.innerHTML = orders.map(o => `
        <div style="background: var(--gray-50); border: 1px solid var(--gray-200); padding: 12px; border-radius: var(--radius-md); margin-bottom: 10px;">
          <div style="display: flex; justify-content: space-between; font-weight: 700; margin-bottom: 4px;">
            <span>Order #${o.id}</span>
            <span style="color: var(--primary);">$${Number(o.totalAmount).toFixed(2)}</span>
          </div>
          <div style="display: flex; justify-content: space-between; font-size: 0.8125rem; color: var(--gray-600);">
            <span>Status: <strong style="color: var(--dark);">${o.status}</strong></span>
            <span>${new Date(o.createdAt).toLocaleDateString()}</span>
          </div>
        </div>
      `).join("");
    }
  } catch (err) {
    console.error("Order history fetch error:", err);
  }

  modal.classList.add("active");
}

// Admin Dashboard Modal
async function openAdminModal() {
  if (!state.isAdmin) return;
  const modal = document.getElementById("adminModalBackdrop");

  try {
    const stats = await API.get("/api/admin/dashboard/stats", true);
    const overview = stats.overview || {};
    document.getElementById("admTotalRev").textContent = `$${Number(overview.totalRevenue || 0).toFixed(2)}`;
    document.getElementById("admTotalOrders").textContent = overview.totalOrders || 0;
    document.getElementById("admActiveUsers").textContent = overview.totalUsers || 0;
    document.getElementById("admLowStock").textContent = (stats.lowStockProducts || []).length;
  } catch (err) {
    showToast("Failed to fetch admin stats: " + err.message, "error");
  }

  modal.classList.add("active");
}

// Authentication (Login / Register)
function openAuthModal(tab = "login") {
  const modal = document.getElementById("authModalBackdrop");
  switchAuthTab(tab);
  modal.classList.add("active");
}

function switchAuthTab(tab) {
  const loginForm = document.getElementById("loginForm");
  const registerForm = document.getElementById("registerForm");
  const tabLogin = document.getElementById("tabLogin");
  const tabReg = document.getElementById("tabRegister");

  if (tab === "login") {
    loginForm.style.display = "block";
    registerForm.style.display = "none";
    tabLogin.classList.add("active");
    tabReg.classList.remove("active");
  } else {
    loginForm.style.display = "none";
    registerForm.style.display = "block";
    tabReg.classList.add("active");
    tabLogin.classList.remove("active");
  }
}

async function handleLogin(e) {
  e.preventDefault();
  const email = document.getElementById("loginEmail").value;
  const password = document.getElementById("loginPassword").value;

  try {
    const res = await API.post("/api/auth/login", { email, password });
    handleAuthSuccess(res);
    closeAllModals();
    showToast("Signed in successfully! Welcome back.", "success");
  } catch (err) {
    showToast(err.message, "error");
  }
}

async function handleRegister(e) {
  e.preventDefault();
  const firstName = document.getElementById("regFirstName").value;
  const lastName = document.getElementById("regLastName").value;
  const email = document.getElementById("regEmail").value;
  const password = document.getElementById("regPassword").value;
  const phone = document.getElementById("regPhone").value;

  try {
    const res = await API.post("/api/auth/register", {
      firstName,
      lastName,
      email,
      password,
      phone,
      role: "CUSTOMER",
    });
    handleAuthSuccess(res);
    closeAllModals();
    showToast("Registration successful! Welcome to JPBazaar.", "success");
  } catch (err) {
    showToast(err.message, "error");
  }
}

function handleAuthSuccess(res) {
  state.token = res.token || res.accessToken;
  state.user = {
    id: res.userId,
    email: res.email,
    firstName: res.firstName || res.email.split('@')[0],
    lastName: res.lastName || '',
    role: res.role,
  };
  state.isAdmin = res.role === "ADMIN";

  localStorage.setItem("jpbazaar_token", state.token);
  localStorage.setItem("jpbazaar_user", JSON.stringify(state.user));

  renderAuthNav();
  syncUserCart();
  syncUserWishlist();
}

function logout() {
  state.token = null;
  state.user = null;
  state.isAdmin = false;
  state.cart = [];
  state.wishlist.clear();

  localStorage.removeItem("jpbazaar_token");
  localStorage.removeItem("jpbazaar_user");

  renderAuthNav();
  renderCart();
  updateWishlistBadge();
  closeAllModals();
  showToast("You have been signed out.", "info");
}

function quickFillDemo(role) {
  if (role === 'admin') {
    document.getElementById("loginEmail").value = "admin@jpbazaar.com";
    document.getElementById("loginPassword").value = "Admin@123";
  } else {
    document.getElementById("loginEmail").value = "customer@jpbazaar.com";
    document.getElementById("loginPassword").value = "Customer@123";
  }
}

// Modal Utilities
function closeAllModals() {
  document.querySelectorAll(".modal-backdrop, .drawer-backdrop").forEach(m => m.classList.remove("active"));
}

function setupEventListeners() {
  // Search debouncing
  let debounceTimer;
  document.getElementById("navSearchInput").addEventListener("input", (e) => {
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(() => {
      state.searchQuery = e.target.value;
      renderProducts();
    }, 250);
  });

  // Sort change
  document.getElementById("catalogSortSelect").addEventListener("change", (e) => {
    state.sortBy = e.target.value;
    renderProducts();
  });

  // Forms
  document.getElementById("loginForm").addEventListener("submit", handleLogin);
  document.getElementById("registerForm").addEventListener("submit", handleRegister);
  document.getElementById("checkoutForm").addEventListener("submit", handlePlaceOrder);

  // Close modals on Escape key
  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape") closeAllModals();
  });
}

function escapeHtml(str) {
  if (!str) return "";
  return String(str)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}
