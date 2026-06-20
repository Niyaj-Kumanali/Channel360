import { apiClient } from '@/lib/api-client';
import type { ApiResponse } from '@/features/auth/types/api.types';
import type { Role, Permission } from '@/features/auth/types/auth.types';

export const roleApi = {
  getAll: () =>
    apiClient.get<ApiResponse<Role[]>>('/roles'),

  getById: (id: number) =>
    apiClient.get<ApiResponse<Role>>(`/roles/${id}`),

  create: (data: Partial<Role>) =>
    apiClient.post<ApiResponse<Role>>('/roles', data),

  update: (id: number, data: Partial<Role>) =>
    apiClient.put<ApiResponse<Role>>(`/roles/${id}`, data),

  delete: (id: number) =>
    apiClient.delete<ApiResponse<void>>(`/roles/${id}`),

  getAllPermissions: () =>
    apiClient.get<ApiResponse<Permission[]>>('/permissions'),

  getRoleMenuItems: (roleId: number) =>
    apiClient.get<ApiResponse<number[]>>(`/menu-items/roles/${roleId}`),

  setRoleMenuItems: (roleId: number, menuItemIds: number[]) =>
    apiClient.put<ApiResponse<void>>(`/menu-items/roles/${roleId}`, { menuItemIds }),
};
