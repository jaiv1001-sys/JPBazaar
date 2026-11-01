import { api } from './api';
import { Category, CategoryRequest } from '../types';

let cachedCategories: Category[] | null = null;

export const categoryService = {
  async getCategories(forceRefresh = false): Promise<Category[]> {
    if (!cachedCategories || forceRefresh) {
      cachedCategories = await api.get('/api/categories');
    }
    return cachedCategories || [];
  },

  clearCache(): void {
    cachedCategories = null;
  },

  async getCategoryById(id: number): Promise<Category> {
    return api.get(`/api/categories/${id}`);
  },

  async createCategory(data: CategoryRequest): Promise<Category> {
    const created: Category = await api.post('/api/categories', data);
    this.clearCache();
    return created;
  },

  async updateCategory(id: number, data: CategoryRequest): Promise<Category> {
    const updated: Category = await api.put(`/api/categories/${id}`, data);
    this.clearCache();
    return updated;
  },

  async deleteCategory(id: number): Promise<void> {
    await api.delete(`/api/categories/${id}`);
    this.clearCache();
  },
};
