import { apiClient } from '@/lib/api-client';
import type { ApiResponse } from '@/features/auth/types/api.types';
import type { AuditLog } from '@/features/audit/types/audit.types';

export const auditApi = {
  getAll: (params?: { module?: string; action?: string; userId?: number }) =>
    apiClient.get<ApiResponse<AuditLog[]>>('/audit-logs', params as Record<string, string>),
};
