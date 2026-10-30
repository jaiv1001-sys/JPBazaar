import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import { ApiResponse } from '../types';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '';

export const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor: Attach JWT Token
api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('jpbazaar_token');
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor: Extract data payload & handle errors
api.interceptors.response.use(
  (response) => {
    // If response matches ApiResponse<T> format, return payload data
    if (response.data && typeof response.data === 'object' && 'data' in response.data) {
      return response.data.data;
    }
    return response.data;
  },
  (error: AxiosError<ApiResponse<unknown>>) => {
    if (error.response) {
      const status = error.response.status;

      // Handle 401 Unauthorized
      if (status === 401) {
        localStorage.removeItem('jpbazaar_token');
        localStorage.removeItem('jpbazaar_user');
        window.dispatchEvent(new CustomEvent('jpbazaar:unauthorized'));
      }

      // Handle 429 Rate Limit
      if (status === 429) {
        const retryAfter = error.response.headers['retry-after'];
        const msg = retryAfter
          ? `Rate limit exceeded. Please wait ${retryAfter} seconds before retrying.`
          : 'Too many requests. Please slow down.';
        return Promise.reject(new Error(msg));
      }

      const serverMessage =
        error.response.data?.message ||
        (typeof error.response.data === 'string' ? error.response.data : undefined) ||
        error.message;

      return Promise.reject(new Error(serverMessage));
    }

    return Promise.reject(new Error(error.message || 'Network error occurred. Please check your connection.'));
  }
);
