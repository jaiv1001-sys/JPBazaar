import { api } from './api';
import { Review, ReviewCreateRequest, ProductReviewSummaryResponse } from '../types';

export const reviewService = {
  async getProductReviews(productId: number, page = 0, size = 10): Promise<ProductReviewSummaryResponse> {
    return api.get(`/api/products/${productId}/reviews`, { params: { page, size } });
  },

  async addReview(data: ReviewCreateRequest): Promise<Review> {
    return api.post('/api/reviews', data);
  },

  async updateReview(reviewId: number, data: { rating: number; comment?: string }): Promise<Review> {
    return api.put(`/api/reviews/${reviewId}`, data);
  },

  async deleteReview(reviewId: number): Promise<void> {
    return api.delete(`/api/reviews/${reviewId}`);
  },
};
