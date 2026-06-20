import { apiClient } from '@/lib/api-client';
import type { ApiResponse } from '@/features/auth/types/api.types';
import type { HomepageSection, HomepagePopup } from '@/features/cms/types/cms.types';

export const homeApi = {
  getPublishedSections: () =>
    apiClient.get<ApiResponse<HomepageSection[]>>('/homepage/sections'),

  getActivePopups: () =>
    apiClient.get<ApiResponse<HomepagePopup[]>>('/homepage/popups'),
};
