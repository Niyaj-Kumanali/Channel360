import { apiService } from '@/shared/services/api.service';
import type { HomepageSection, CreateSectionRequest, UpdateSectionRequest } from '@/shared/types/cms.types';

export const cmsApi = {
  getAll: () =>
    apiService.get<HomepageSection[]>('/cms/sections'),

  getActive: () =>
    apiService.get<HomepageSection[]>('/cms/sections/active'),

  getById: (id: number) =>
    apiService.get<HomepageSection>(`/cms/sections/${id}`),

  create: (data: CreateSectionRequest) =>
    apiService.post<HomepageSection>('/cms/sections', data),

  update: (id: number, data: UpdateSectionRequest) =>
    apiService.put<HomepageSection>(`/cms/sections/${id}`, data),

  delete: (id: number) =>
    apiService.delete(`/cms/sections/${id}`),

  reorder: (sectionIds: number[]) =>
    apiService.put('/cms/sections/reorder', { sectionIds }),

  toggleActive: (id: number) =>
    apiService.patch<HomepageSection>(`/cms/sections/${id}/toggle`),
};
