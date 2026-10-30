import { api } from './api';
import { Product, PageResponse, ProductCreateRequest, ProductUpdateRequest, ProductImage } from '../types';

export interface ProductFilterParams {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: 'asc' | 'desc';
  categoryId?: number;
  brand?: string;
  minPrice?: number;
  maxPrice?: number;
  active?: boolean;
  keyword?: string;
}

export const productService = {
  async getProducts(params?: ProductFilterParams): Promise<PageResponse<Product>> {
    return api.get('/api/products', { params });
  },

  async getProductById(id: number): Promise<Product> {
    return api.get(`/api/products/${id}`);
  },

  async createProduct(data: ProductCreateRequest): Promise<Product> {
    return api.post('/api/products', data);
  },

  async updateProduct(id: number, data: ProductUpdateRequest): Promise<Product> {
    return api.put(`/api/products/${id}`, data);
  },

  async deleteProduct(id: number): Promise<void> {
    return api.delete(`/api/products/${id}`);
  },

  async uploadImage(productId: number, file: File): Promise<ProductImage> {
    const formData = new FormData();
    formData.append('file', file);
    return api.post(`/api/products/${productId}/images`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },

  async setPrimaryImage(productId: number, imageId: number): Promise<ProductImage> {
    return api.put(`/api/products/${productId}/images/${imageId}/primary`);
  },

  async deleteImage(productId: number, imageId: number): Promise<void> {
    return api.delete(`/api/products/${productId}/images/${imageId}`);
  },
};
