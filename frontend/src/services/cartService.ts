import { api } from './api';
import { Cart, AddToCartRequest, UpdateCartItemRequest } from '../types';

export const cartService = {
  async getCart(): Promise<Cart> {
    return api.get('/api/cart');
  },

  async addToCart(data: AddToCartRequest): Promise<Cart> {
    return api.post('/api/cart/items', data);
  },

  async updateItemQuantity(itemId: number, data: UpdateCartItemRequest): Promise<Cart> {
    return api.put(`/api/cart/items/${itemId}`, data);
  },

  async removeItem(itemId: number): Promise<Cart> {
    return api.delete(`/api/cart/items/${itemId}`);
  },

  async clearCart(): Promise<void> {
    return api.delete('/api/cart');
  },
};
