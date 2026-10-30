import { api } from './api';
import {
  AdminDashboardStats,
  DashboardOverview,
  DailySales,
  CategorySales,
  OrderStatusCount,
  RecentOrderSummary,
  TopSellingProduct,
  LowStockProduct,
  PageResponse,
} from '../types';

export const adminService = {
  async getDashboardStats(): Promise<AdminDashboardStats> {
    return api.get('/api/admin/dashboard/stats');
  },

  async getOverview(): Promise<DashboardOverview> {
    return api.get('/api/admin/dashboard/overview');
  },

  async getDailySales(days = 30): Promise<DailySales[]> {
    return api.get('/api/admin/dashboard/sales/daily', { params: { days } });
  },

  async getCategorySales(): Promise<CategorySales[]> {
    return api.get('/api/admin/dashboard/sales/category');
  },

  async getOrderStatusCounts(): Promise<OrderStatusCount[]> {
    return api.get('/api/admin/dashboard/orders/status');
  },

  async getRecentOrders(limit = 5): Promise<RecentOrderSummary[]> {
    return api.get('/api/admin/dashboard/orders/recent', { params: { limit } });
  },

  async getTopSellingProducts(limit = 5): Promise<TopSellingProduct[]> {
    return api.get('/api/admin/dashboard/products/top-selling', { params: { limit } });
  },

  async getLowStockProducts(threshold = 10, page = 0, size = 10): Promise<PageResponse<LowStockProduct>> {
    return api.get('/api/admin/dashboard/products/low-stock', { params: { threshold, page, size } });
  },
};
