import { apiClient } from '@/lib/api-client';
import type { ApiResponse } from '@/features/auth/types/api.types';
import type { Region, RegionRequest } from '@/features/region/types/region.types';

export const regionApi = {
  getAll: (treeType?: string) =>
    apiClient.get<ApiResponse<Region[]>>('/regions', treeType ? { treeType } : undefined),

  getById: (id: number) =>
    apiClient.get<ApiResponse<Region>>(`/regions/${id}`),

  create: (data: RegionRequest) =>
    apiClient.post<ApiResponse<Region>>('/regions', data),

  update: (id: number, data: RegionRequest) =>
    apiClient.put<ApiResponse<Region>>(`/regions/${id}`, data),

  delete: (id: number) =>
    apiClient.delete<ApiResponse<void>>(`/regions/${id}`),
};
