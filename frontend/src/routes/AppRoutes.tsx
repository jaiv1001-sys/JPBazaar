import React, { Suspense } from 'react';
import { Routes, Route } from 'react-router-dom';
import { MainLayout } from '../components/layout/MainLayout';
import { ProtectedRoute } from './ProtectedRoute';
import { AdminRoute } from './AdminRoute';

// Lazy-loaded route components for optimized code-splitting and faster initial bundle loading
const HomePage = React.lazy(() => import('../pages/home/HomePage').then(m => ({ default: m.HomePage })));
const ProductsPage = React.lazy(() => import('../pages/products/ProductsPage').then(m => ({ default: m.ProductsPage })));
const ProductDetailPage = React.lazy(() => import('../pages/products/ProductDetailPage').then(m => ({ default: m.ProductDetailPage })));
const CartPage = React.lazy(() => import('../pages/cart/CartPage').then(m => ({ default: m.CartPage })));
const WishlistPage = React.lazy(() => import('../pages/wishlist/WishlistPage').then(m => ({ default: m.WishlistPage })));
const AuthPage = React.lazy(() => import('../pages/auth/AuthPage').then(m => ({ default: m.AuthPage })));
const CheckoutPage = React.lazy(() => import('../pages/checkout/CheckoutPage').then(m => ({ default: m.CheckoutPage })));
const AccountPage = React.lazy(() => import('../pages/account/AccountPage').then(m => ({ default: m.AccountPage })));
const OrdersPage = React.lazy(() => import('../pages/account/OrdersPage').then(m => ({ default: m.OrdersPage })));
const OrderTrackingPage = React.lazy(() => import('../pages/orders/OrderTrackingPage').then(m => ({ default: m.OrderTrackingPage })));
const AdminDashboardPage = React.lazy(() => import('../pages/admin/AdminDashboardPage').then(m => ({ default: m.AdminDashboardPage })));
const DesignShowcasePage = React.lazy(() => import('../pages/showcase/DesignShowcasePage').then(m => ({ default: m.DesignShowcasePage })));
const NotFoundPage = React.lazy(() => import('../pages/notfound/NotFoundPage').then(m => ({ default: m.NotFoundPage })));

const PageLoadingFallback: React.FC = () => (
  <div className="min-h-[60vh] flex flex-col items-center justify-center bg-luxury-black">
    <div className="w-10 h-10 border-2 border-luxury-gold/30 border-t-luxury-gold rounded-full animate-spin mb-4" />
    <span className="font-serif text-sm tracking-luxury-widest text-luxury-gold uppercase">JPBazaar</span>
  </div>
);

export const AppRoutes: React.FC = () => {
  return (
    <Suspense fallback={<PageLoadingFallback />}>
      <Routes>
        <Route element={<MainLayout />}>
          {/* Public Storefront Routes */}
          <Route path="/" element={<HomePage />} />
          <Route path="/products" element={<ProductsPage />} />
          <Route path="/products/:id" element={<ProductDetailPage />} />
          <Route path="/cart" element={<CartPage />} />
          <Route path="/wishlist" element={<WishlistPage />} />
          <Route path="/auth" element={<AuthPage />} />
          <Route path="/design-system" element={<DesignShowcasePage />} />

          {/* Protected Customer Routes */}
          <Route element={<ProtectedRoute />}>
            <Route path="/checkout" element={<CheckoutPage />} />
            <Route path="/account" element={<AccountPage />} />
            <Route path="/account/orders" element={<OrdersPage />} />
            <Route path="/account/orders/:id/track" element={<OrderTrackingPage />} />
          </Route>

          {/* Protected Admin Routes */}
          <Route element={<AdminRoute />}>
            <Route path="/admin" element={<AdminDashboardPage />} />
          </Route>

          {/* 404 Catch-All */}
          <Route path="*" element={<NotFoundPage />} />
        </Route>
      </Routes>
    </Suspense>
  );
};
