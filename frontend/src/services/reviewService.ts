import { api } from './api';
import { Review, ReviewRequest, ReviewSummary, PageResponse } from '../types';

export const reviewService = {
  async getProductReviews(productId: number, page = 0, size = 10): Promise<PageResponse<Review>> {
    return api.get(`/api/products/${productId}/reviews`, { params: { page, size } });
  },

  async getProductReviewsSummary(productId: number): Promise<ReviewSummary> {
    return api.get(`/api/products/${productId}/reviews/summary`);
  },

  async addReview(productId: number, data: ReviewRequest): Promise<Review> {
    return api.post(`/api/products/${productId}/reviews`, data);
  },

  async updateReview(reviewId: number, data: ReviewRequest): Promise<Review> {
    return api.put(`/api/reviews/${reviewId}`, data);
  },

  async deleteReview(reviewId: number): Promise<void> {
    return api.delete(`/api/reviews/${reviewId}`);
  },
};
