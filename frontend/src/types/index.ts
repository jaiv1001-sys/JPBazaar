// Standard Backend API Envelope
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

// User & Authentication
export type Role = 'CUSTOMER' | 'ADMIN';

export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  role: Role;
  enabled: boolean;
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  userId: number;
  email: string;
  firstName: string;
  role: Role;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  phone?: string;
  role?: Role;
}

export interface UpdateProfileRequest {
  firstName: string;
  lastName: string;
  phone?: string;
}

// Address
export interface Address {
  id: number;
  addressLine: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  isDefault: boolean;
  createdAt?: string;
}

export interface AddressRequest {
  addressLine: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  isDefault?: boolean;
}

// Category
export interface Category {
  id: number;
  name: string;
  description?: string;
  createdAt?: string;
}

export interface CategoryRequest {
  name: string;
  description?: string;
}

// Product & Images
export interface ProductImage {
  id: number;
  imageUrl: string;
  isPrimary: boolean;
}

export interface Product {
  id: number;
  name: string;
  description?: string;
  price: number;
  stockQuantity: number;
  sku: string;
  brand?: string;
  rating?: number;
  active: boolean;
  categoryId: number;
  categoryName?: string;
  imageUrls?: string[];
  images?: ProductImage[];
  createdAt: string;
  updatedAt: string;
}

export interface ProductCreateRequest {
  categoryId: number;
  name: string;
  description?: string;
  price: number;
  stockQuantity: number;
  sku: string;
  brand?: string;
  active?: boolean;
}

export interface ProductUpdateRequest {
  categoryId?: number;
  name?: string;
  description?: string;
  price?: number;
  stockQuantity?: number;
  sku?: string;
  brand?: string;
  active?: boolean;
}

// Shopping Cart
export interface CartItem {
  id: number;
  productId: number;
  productName: string;
  sku?: string;
  productSku?: string;
  price: number;
  unitPrice?: number;
  quantity: number;
  subtotal: number;
  imageUrl?: string;
}

export interface Cart {
  id: number;
  userId: number;
  items: CartItem[];
  totalAmount: number;
  totalItems: number;
}

export interface AddToCartRequest {
  productId: number;
  quantity: number;
}

export interface UpdateCartItemRequest {
  quantity: number;
}

// Wishlist
export interface WishlistItem {
  id: number;
  productId: number;
  productName: string;
  productSku?: string;
  price: number;
  rating?: number;
  imageUrl?: string;
  inStock: boolean;
  addedAt?: string;
  createdAt?: string;
}

export interface Wishlist {
  id: number;
  userId: number;
  items: WishlistItem[];
}

export interface AddToWishlistRequest {
  productId: number;
}

// Orders & Payments
export type OrderStatus =
  | 'PLACED'
  | 'CONFIRMED'
  | 'PROCESSING'
  | 'SHIPPED'
  | 'OUT_FOR_DELIVERY'
  | 'DELIVERED'
  | 'CANCELLED';

export type PaymentMethod = 'CARD' | 'UPI' | 'NET_BANKING' | 'COD';
export type PaymentStatus = 'PENDING' | 'SUCCESS' | 'FAILED' | 'REFUNDED';

export interface OrderItem {
  id: number;
  productId: number;
  productName: string;
  price: number;
  quantity: number;
  subtotal: number;
  imageUrl?: string;
}

export interface Order {
  id: number;
  userId: number;
  userEmail: string;
  addressId: number;
  shippingAddress: string;
  status: OrderStatus;
  totalAmount: number;
  totalItemCount?: number;
  paymentMethod?: PaymentMethod;
  payment?: Payment;
  notes?: string;
  items: OrderItem[];
  createdAt: string;
  updatedAt: string;
}

export interface CreateOrderRequest {
  addressId: number;
  paymentMethod?: PaymentMethod;
  notes?: string;
}

export interface UpdateOrderStatusRequest {
  status: OrderStatus;
}

export interface Payment {
  id: number;
  orderId: number;
  transactionId: string;
  amount: number;
  method: PaymentMethod;
  status: PaymentStatus;
  createdAt: string;
}

export interface PaymentProcessRequest {
  orderId: number;
  paymentMethod: PaymentMethod;
  simulateFailure?: boolean;
}

// Reviews
export interface Review {
  id: number;
  userId: number;
  userName: string;
  productId: number;
  rating: number;
  comment?: string;
  createdAt: string;
  updatedAt?: string;
}

export interface ReviewRequest {
  productId?: number;
  rating: number;
  comment?: string;
}

export interface ReviewCreateRequest {
  productId: number;
  rating: number;
  comment?: string;
}

export interface ProductReviewSummaryResponse {
  productId: number;
  averageRating: number;
  totalReviews: number;
  reviews: PageResponse<Review>;
}

// Admin Dashboard Analytics
export interface DashboardOverview {
  totalRevenue: number;
  totalOrders: number;
  totalProducts: number;
  totalUsers: number;
  pendingOrders: number;
}

export interface DailySales {
  date: string;
  revenue: number;
  orderCount: number;
}

export interface CategorySales {
  categoryId: number;
  categoryName: string;
  productCount: number;
  unitsSold: number;
  revenue: number;
}

export interface OrderStatusCount {
  status: OrderStatus;
  count: number;
}

export interface RecentOrderSummary {
  id: number;
  userEmail: string;
  totalAmount: number;
  status: OrderStatus;
  createdAt: string;
}

export interface TopSellingProduct {
  productId: number;
  productName: string;
  sku: string;
  unitsSold: number;
  totalRevenue: number;
}

export interface LowStockProduct {
  id: number;
  name: string;
  sku: string;
  stockQuantity: number;
  price: number;
}

export interface AdminDashboardStats {
  overview: DashboardOverview;
  dailySales?: DailySales[];
  recentSalesTrend?: DailySales[];
  categorySales?: CategorySales[];
  ordersByStatus?: OrderStatusCount[];
  orderStatusCounts?: OrderStatusCount[];
  recentOrders?: RecentOrderSummary[];
  topSellingProducts?: TopSellingProduct[];
  lowStockProducts?: LowStockProduct[];
}
