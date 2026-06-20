import { apiService } from '@/shared/services/api.service';
import type { PageResponse } from '@/shared/types/api.types';
import type { ApiResponse } from '@/shared/types/api.types';

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

const API_BASE_URL = 'http://localhost:5000/api/v1';

export const usersApi = {
  getAll: async (params?: UserFilterParams): Promise<PageResponse<UserDto>> => {
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
    const token = localStorage.getItem('access_token');
    const headers: Record<string, string> = { 'Content-Type': 'application/json' };
    if (token) headers['Authorization'] = `Bearer ${token}`;
    let url = `${API_BASE_URL}/users`;
    if (Object.keys(queryParams).length > 0) {
      url += '?' + new URLSearchParams(queryParams).toString();
    }
    const response = await fetch(url, { headers });
    return response.json();
  },

  getById: (id: number): Promise<ApiResponse<UserDto>> =>
    apiService.get<UserDto>(`/users/${id}`),

  create: (data: CreateUserRequest): Promise<ApiResponse<UserDto>> =>
    apiService.post<UserDto>('/users', data),

  update: (id: number, data: UpdateUserRequest): Promise<ApiResponse<UserDto>> =>
    apiService.put<UserDto>(`/users/${id}`, data),

  delete: (id: number): Promise<ApiResponse<void>> =>
    apiService.delete(`/users/${id}`),

  activate: (id: number): Promise<ApiResponse<UserDto>> =>
    apiService.patch<UserDto>(`/users/${id}/activate`),

  deactivate: (id: number): Promise<ApiResponse<UserDto>> =>
    apiService.patch<UserDto>(`/users/${id}/deactivate`),

  assignRoles: (id: number, roleIds: number[]): Promise<ApiResponse<UserDto>> =>
    apiService.put<UserDto>(`/users/${id}/roles`, { roleIds }),

  resetPassword: (id: number): Promise<ApiResponse<void>> =>
    apiService.post(`/users/${id}/reset-password`),
};
