import { apiClient } from '@/lib/api-client';
import type { ApiResponse } from '@/features/auth/types/api.types';
import type { Workflow, WorkflowRequest, WorkflowStep, WorkflowStepRequest } from '@/features/workflow/types/workflow.types';

export const workflowApi = {
  getAll: () =>
    apiClient.get<ApiResponse<Workflow[]>>('/workflows'),

  getById: (id: number) =>
    apiClient.get<ApiResponse<Workflow>>(`/workflows/${id}`),

  create: (data: WorkflowRequest) =>
    apiClient.post<ApiResponse<Workflow>>('/workflows', data),

  update: (id: number, data: WorkflowRequest) =>
    apiClient.put<ApiResponse<Workflow>>(`/workflows/${id}`, data),

  delete: (id: number) =>
    apiClient.delete<ApiResponse<void>>(`/workflows/${id}`),

  addStep: (data: WorkflowStepRequest) =>
    apiClient.post<ApiResponse<WorkflowStep>>('/workflows/steps', data),

  updateStep: (stepId: number, data: WorkflowStepRequest) =>
    apiClient.put<ApiResponse<WorkflowStep>>(`/workflows/steps/${stepId}`, data),

  deleteStep: (stepId: number) =>
    apiClient.delete<ApiResponse<void>>(`/workflows/steps/${stepId}`),
};
