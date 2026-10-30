import { api } from './api';
import { Payment, PaymentProcessRequest } from '../types';

export const paymentService = {
  async processPayment(data: PaymentProcessRequest): Promise<Payment> {
    return api.post('/api/payments/process', data);
  },

  async getPaymentByOrderId(orderId: number): Promise<Payment> {
    return api.get(`/api/payments/orders/${orderId}`);
  },

  async refundPayment(orderId: number): Promise<Payment> {
    return api.post(`/api/payments/orders/${orderId}/refund`);
  },
};
