import { apiClient } from '@/lib/api-client';
import type { ApiResponse } from '@/features/auth/types/api.types';
import type { Role, Permission, MenuWithPermissions } from '@/features/auth/types/auth.types';

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

  getMenusWithPermissions: () =>
    apiClient.get<ApiResponse<MenuWithPermissions[]>>('/menu-items/with-permissions'),

  updatePermissions: (roleId: number, permissionIds: number[]) =>
    apiClient.put<ApiResponse<void>>(`/roles/${roleId}/permissions`, permissionIds),
};
