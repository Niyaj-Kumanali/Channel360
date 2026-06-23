import { apiClient } from '@/lib/api-client';
import type { ApiResponse } from '@/features/auth/types/api.types';
import type { RegionApprover, RegionApproverRequest } from '@/features/region-approver/types/region-approver.types';

export const regionApproverApi = {
  getAll: () =>
    apiClient.get<ApiResponse<RegionApprover[]>>('/region-approvers'),

  getById: (id: number) =>
    apiClient.get<ApiResponse<RegionApprover>>(`/region-approvers/${id}`),

  create: (data: RegionApproverRequest) =>
    apiClient.post<ApiResponse<RegionApprover>>('/region-approvers', data),

  update: (id: number, data: RegionApproverRequest) =>
    apiClient.put<ApiResponse<RegionApprover>>(`/region-approvers/${id}`, data),

  deactivate: (id: number) =>
    apiClient.delete<ApiResponse<void>>(`/region-approvers/${id}`),
};
