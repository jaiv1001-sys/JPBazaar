import { api } from './api';
import { Wishlist, AddToWishlistRequest } from '../types';

export const wishlistService = {
  async getWishlist(): Promise<Wishlist> {
    return api.get('/api/wishlist');
  },

  async addToWishlist(data: AddToWishlistRequest): Promise<Wishlist> {
    return api.post('/api/wishlist/items', data);
  },

  async removeFromWishlist(productId: number): Promise<Wishlist> {
    return api.delete(`/api/wishlist/items/${productId}`);
  },

  async clearWishlist(): Promise<void> {
    return api.delete('/api/wishlist');
  },
};
