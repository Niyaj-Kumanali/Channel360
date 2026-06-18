import { apiService } from '@/shared/services/api.service';
import type { LoginRequest, LoginResponse } from '@/shared/types/auth.types';

export const authApi = {
  login: (data: LoginRequest) =>
    apiService.post<LoginResponse>('/auth/login', data),

  logout: (refreshToken: string) =>
    apiService.post('/auth/logout', { refreshToken }),

  getMe: () =>
    apiService.get<any>('/auth/me'),

  changePassword: (data: { oldPassword: string; newPassword: string }) =>
    apiService.post('/auth/change-password', data),

  forgotPassword: (email: string) =>
    apiService.post('/auth/forgot-password', { email }),

  resetPassword: (data: { token: string; newPassword: string }) =>
    apiService.post('/auth/reset-password', data),

  refresh: (refreshToken: string) =>
    apiService.post<LoginResponse>('/auth/refresh', { refreshToken }),
};
