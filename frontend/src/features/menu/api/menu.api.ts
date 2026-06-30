import { apiClient } from '@/lib/api-client';
import type { ApiResponse } from '@/features/auth/types/api.types';
import type { MenuItemResponse } from '@/features/auth/types/auth.types';

export const menuApi = {
  getAll: () =>
    apiClient.get<ApiResponse<MenuItemResponse[]>>('/menu-items'),

  create: (data: Partial<MenuItemResponse>) =>
    apiClient.post<ApiResponse<MenuItemResponse>>('/menu-items', data),

  update: (id: number, data: Partial<MenuItemResponse>) =>
    apiClient.put<ApiResponse<MenuItemResponse>>(`/menu-items/${id}`, data),

  delete: (id: number) =>
    apiClient.delete<ApiResponse<void>>(`/menu-items/${id}`),
};
