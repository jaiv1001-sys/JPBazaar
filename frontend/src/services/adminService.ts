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
  Order,
  OrderStatus,
  UpdateOrderStatusRequest,
} from '../types';

export const adminService = {
  async getDashboardStats(): Promise<AdminDashboardStats> {
    return api.get('/api/admin/dashboard/stats');
  },

  async getOverview(): Promise<DashboardOverview> {
    return api.get('/api/admin/dashboard/overview');
  },

  async getSalesTrend(days = 30): Promise<DailySales[]> {
    return api.get('/api/admin/dashboard/sales-trend', { params: { days } });
  },

  async getCategorySales(): Promise<CategorySales[]> {
    return api.get('/api/admin/dashboard/category-sales');
  },

  async getOrdersByStatus(): Promise<OrderStatusCount[]> {
    return api.get('/api/admin/dashboard/orders-by-status');
  },

  async getRecentOrders(limit = 5): Promise<RecentOrderSummary[]> {
    return api.get('/api/admin/dashboard/recent-orders', { params: { limit } });
  },

  async getTopSellingProducts(limit = 5): Promise<TopSellingProduct[]> {
    return api.get('/api/admin/dashboard/top-products', { params: { limit } });
  },

  async getLowStockProducts(threshold = 10, page = 0, size = 10): Promise<PageResponse<LowStockProduct>> {
    return api.get('/api/admin/dashboard/low-stock', { params: { threshold, page, size } });
  },

  async getAllOrders(status?: string, page = 0, size = 20): Promise<PageResponse<Order>> {
    return api.get('/api/admin/orders', { params: { status: status || undefined, page, size } });
  },

  async updateOrderStatus(
    orderId: number,
    data: UpdateOrderStatusRequest | OrderStatus
  ): Promise<Order> {
    const payload = typeof data === 'string' ? { status: data } : data;
    return api.put(`/api/orders/${orderId}/status`, payload);
  },
};
