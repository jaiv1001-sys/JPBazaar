import { api } from './api';
import { Address, AddressRequest } from '../types';

export const addressService = {
  async getAddresses(): Promise<Address[]> {
    return api.get('/api/addresses');
  },

  async getAddressById(id: number): Promise<Address> {
    return api.get(`/api/addresses/${id}`);
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
    return api.put(`/api/addresses/${id}/set-default`);
  },
};
