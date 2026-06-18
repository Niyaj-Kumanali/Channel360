import { apiService } from '@/shared/services/api.service';
import type { PageResponse } from '@/shared/types/api.types';

export interface UserDto {
  id: number;
  employeeId: string;
  firstName: string;
  lastName: string;
  email: string;
  mobileNumber: string;
  status: string;
  roles: { id: number; name: string; description: string }[];
  lastLoginAt: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CreateUserRequest {
  employeeId: string;
  firstName: string;
  lastName: string;
  email: string;
  mobileNumber?: string;
  password: string;
  roleIds: number[];
}

export interface UpdateUserRequest {
  firstName?: string;
  lastName?: string;
  email?: string;
  mobileNumber?: string;
  roleIds?: number[];
}

export interface UserFilterParams {
  search?: string;
  status?: string;
  roleId?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: string;
}

export const usersApi = {
  getAll: (params?: UserFilterParams) => {
    const queryParams: Record<string, string> = {};
    if (params) {
      if (params.search) queryParams.search = params.search;
      if (params.status) queryParams.status = params.status;
      if (params.roleId) queryParams.roleId = params.roleId;
      if (params.page !== undefined) queryParams.page = String(params.page);
      if (params.size !== undefined) queryParams.size = String(params.size);
      if (params.sortBy) queryParams.sortBy = params.sortBy;
      if (params.sortDir) queryParams.sortDir = params.sortDir;
    }
    return apiService.get<PageResponse<UserDto>>('/users', queryParams);
  },

  getById: (id: number) =>
    apiService.get<UserDto>(`/users/${id}`),

  create: (data: CreateUserRequest) =>
    apiService.post<UserDto>('/users', data),

  update: (id: number, data: UpdateUserRequest) =>
    apiService.put<UserDto>(`/users/${id}`, data),

  delete: (id: number) =>
    apiService.delete(`/users/${id}`),

  activate: (id: number) =>
    apiService.patch<UserDto>(`/users/${id}/activate`),

  deactivate: (id: number) =>
    apiService.patch<UserDto>(`/users/${id}/deactivate`),

  assignRoles: (id: number, roleIds: number[]) =>
    apiService.put<UserDto>(`/users/${id}/roles`, { roleIds }),

  resetPassword: (id: number) =>
    apiService.post(`/users/${id}/reset-password`),
};
