import { api } from './api';
import { Order, CreateOrderRequest, UpdateOrderStatusRequest, PageResponse, OrderTrackingResponse } from '../types';

export const orderService = {
  async placeOrder(data: CreateOrderRequest): Promise<Order> {
    return api.post('/api/orders', data);
  },

  async getMyOrders(page = 0, size = 20, status?: string): Promise<PageResponse<Order>> {
    return api.get('/api/orders', { params: { page, size, status } });
  },

  async getOrderById(id: number): Promise<Order> {
    return api.get(`/api/orders/${id}`);
  },

  async getOrderTracking(id: number): Promise<OrderTrackingResponse> {
    return api.get(`/api/orders/${id}/tracking`);
  },

  async cancelOrder(id: number): Promise<Order> {
    return api.put(`/api/orders/${id}/cancel`);
  },

  async updateOrderStatus(id: number, data: UpdateOrderStatusRequest): Promise<Order> {
    return api.put(`/api/orders/${id}/status`, data);
  },
};
