import { apiClient } from '@/lib/api-client';
import type { ApiResponse } from '@/features/auth/types/api.types';
import type { ApprovalRequestResponse, ApprovalRequestCreate, ApprovalActionRequest, ApprovalTaskResponse } from '@/features/approval/types/approval.types';

export const approvalApi = {
  getAll: () => apiClient.get<ApiResponse<ApprovalRequestResponse[]>>('/approval-requests'),

  getById: (id: number) => apiClient.get<ApiResponse<ApprovalRequestResponse>>(`/approval-requests/${id}`),

  getMyRequests: (userId: number) =>
    apiClient.get<ApiResponse<ApprovalRequestResponse[]>>(`/approval-requests/my/${userId}`),

  create: (data: ApprovalRequestCreate) =>
    apiClient.post<ApiResponse<ApprovalRequestResponse>>('/approval-requests', data),

  approveTask: (taskId: number, data: ApprovalActionRequest) =>
    apiClient.post<ApiResponse<ApprovalTaskResponse>>(`/approval-requests/${taskId}/approve`, data),

  rejectTask: (taskId: number, data: ApprovalActionRequest) =>
    apiClient.post<ApiResponse<ApprovalTaskResponse>>(`/approval-requests/${taskId}/reject`, data),
};
