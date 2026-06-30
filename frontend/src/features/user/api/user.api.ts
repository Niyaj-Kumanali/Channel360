import { apiClient } from '@/lib/api-client';
import type { ApiResponse } from '@/features/auth/types/api.types';
import type { UserDetail, CreateUserRequest, UpdateUserRequest, UserFilterParams, PageResponse } from '@/features/user/types/user.types';

export const userApi = {
  getAll: (params?: UserFilterParams) =>
    apiClient.get<PageResponse<UserDetail>>('/users', params as Record<string, string>),

  create: (data: CreateUserRequest) =>
    apiClient.post<ApiResponse<UserDetail>>('/users', data),

  update: (id: number, data: UpdateUserRequest) =>
    apiClient.put<ApiResponse<UserDetail>>(`/users/${id}`, data),

  delete: (id: number) =>
    apiClient.delete<ApiResponse<void>>(`/users/${id}`),

  activate: (id: number) =>
    apiClient.patch<ApiResponse<UserDetail>>(`/users/${id}/activate`),

  deactivate: (id: number) =>
    apiClient.patch<ApiResponse<UserDetail>>(`/users/${id}/deactivate`),

  resetPassword: (id: number) =>
    apiClient.post<ApiResponse<void>>(`/users/${id}/reset-password`),
};
