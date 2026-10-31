import { api } from './api';
import { BulkImportPreviewResponse, BulkImportResultResponse } from '../types';

export const bulkImportService = {
  async downloadCsvTemplate(): Promise<void> {
    const blob = (await api.get('/api/admin/products/import/template/csv', {
      responseType: 'blob',
    })) as unknown as Blob;

    const url = window.URL.createObjectURL(new Blob([blob], { type: 'text/csv;charset=utf-8;' }));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', 'jpbazaar_products_template.csv');
    document.body.appendChild(link);
    link.click();
    link.parentNode?.removeChild(link);
    window.URL.revokeObjectURL(url);
  },

  async downloadExcelTemplate(): Promise<void> {
    const blob = (await api.get('/api/admin/products/import/template/excel', {
      responseType: 'blob',
    })) as unknown as Blob;

    const url = window.URL.createObjectURL(
      new Blob([blob], {
        type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      })
    );
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', 'jpbazaar_products_template.xlsx');
    document.body.appendChild(link);
    link.click();
    link.parentNode?.removeChild(link);
    window.URL.revokeObjectURL(url);
  },

  async previewImport(file: File): Promise<BulkImportPreviewResponse> {
    const formData = new FormData();
    formData.append('file', file);
    return api.post('/api/admin/products/import/preview', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },

  async executeImport(file: File): Promise<BulkImportResultResponse> {
    const formData = new FormData();
    formData.append('file', file);
    return api.post('/api/admin/products/import/confirm', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
};
