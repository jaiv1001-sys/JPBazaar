import { api } from './api';
import { AuthResponse, LoginRequest, RegisterRequest, User, UpdateProfileRequest, Address, AddressRequest } from '../types';

export const authService = {
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    return api.post('/api/auth/login', credentials);
  },

  async register(data: RegisterRequest): Promise<AuthResponse> {
    return api.post('/api/auth/register', data);
  },

  async getProfile(): Promise<User> {
    return api.get('/api/users/profile');
  },

  async updateProfile(data: UpdateProfileRequest): Promise<User> {
    return api.put('/api/users/profile', data);
  },

  async getAddresses(): Promise<Address[]> {
    return api.get('/api/addresses');
  },

  async addAddress(data: AddressRequest): Promise<Address> {
    return api.post('/api/addresses', data);
  },

  async updateAddress(id: number, data: AddressRequest): Promise<Address> {
    return api.put(`/api/addresses/${id}`, data);
  },

  async deleteAddress(id: number): Promise<void> {
    return api.delete(`/api/addresses/${id}`);
  },

  async setDefaultAddress(id: number): Promise<Address> {
    return api.put(`/api/addresses/${id}/default`);
  },
};
