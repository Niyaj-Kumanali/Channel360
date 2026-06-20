import { apiClient } from '@/lib/api-client';
import type { ApiResponse } from '@/features/auth/types/api.types';
import type { HomepageSection, HomepageSectionRequest, HomepagePopup, HomepagePopupRequest } from '@/features/cms/types/cms.types';

export const cmsApi = {
  // Sections
  getAllSections: () =>
    apiClient.get<ApiResponse<HomepageSection[]>>('/homepage/sections/admin'),

  getSection: (id: number) =>
    apiClient.get<ApiResponse<HomepageSection>>(`/homepage/sections/${id}`),

  createSection: (data: HomepageSectionRequest) =>
    apiClient.post<ApiResponse<HomepageSection>>('/homepage/sections', data),

  updateSection: (id: number, data: HomepageSectionRequest) =>
    apiClient.put<ApiResponse<HomepageSection>>(`/homepage/sections/${id}`, data),

  deleteSection: (id: number) =>
    apiClient.delete<ApiResponse<void>>(`/homepage/sections/${id}`),

  // Popups
  getAllPopups: () =>
    apiClient.get<ApiResponse<HomepagePopup[]>>('/homepage/popups/admin'),

  getPopup: (id: number) =>
    apiClient.get<ApiResponse<HomepagePopup>>(`/homepage/popups/${id}`),

  createPopup: (data: HomepagePopupRequest) =>
    apiClient.post<ApiResponse<HomepagePopup>>('/homepage/popups', data),

  updatePopup: (id: number, data: HomepagePopupRequest) =>
    apiClient.put<ApiResponse<HomepagePopup>>(`/homepage/popups/${id}`, data),

  deletePopup: (id: number) =>
    apiClient.delete<ApiResponse<void>>(`/homepage/popups/${id}`),
};
