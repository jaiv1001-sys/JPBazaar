import { api } from './api';
import { Category, CategoryRequest } from '../types';

export const categoryService = {
  async getCategories(): Promise<Category[]> {
    return api.get('/api/categories');
  },

  async getCategoryById(id: number): Promise<Category> {
    return api.get(`/api/categories/${id}`);
  },

  async createCategory(data: CategoryRequest): Promise<Category> {
    return api.post('/api/categories', data);
  },

  async updateCategory(id: number, data: CategoryRequest): Promise<Category> {
    return api.put(`/api/categories/${id}`, data);
  },

  async deleteCategory(id: number): Promise<void> {
    return api.delete(`/api/categories/${id}`);
  },
};
