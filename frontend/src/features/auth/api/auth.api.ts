import { apiClient } from '@/lib/api-client';
import type { ApiResponse } from '@/features/auth/types/api.types';
import type { LoginRequest, LoginResponse, User, MenuItem } from '@/features/auth/types/auth.types';

export const authApi = {
  login: (data: LoginRequest) =>
    apiClient.post<ApiResponse<LoginResponse>>('/auth/login', data),

  logout: (refreshToken: string) =>
    apiClient.post<ApiResponse<void>>('/auth/logout', { refreshToken }),

  getMe: () =>
    apiClient.get<ApiResponse<User>>('/auth/me'),

  changePassword: (data: { oldPassword: string; newPassword: string }) =>
    apiClient.post<ApiResponse<void>>('/auth/change-password', data),

  forgotPassword: (data: { email: string }) =>
    apiClient.post<ApiResponse<void>>('/auth/forgot-password', data),

  resetPassword: (data: { token: string; newPassword: string }) =>
    apiClient.post<ApiResponse<void>>('/auth/reset-password', data),

  refresh: (refreshToken: string) =>
    apiClient.post<ApiResponse<LoginResponse>>('/auth/refresh', { refreshToken }),

  getMenu: () =>
    apiClient.get<ApiResponse<MenuItem[]>>('/auth/menu'),
};
