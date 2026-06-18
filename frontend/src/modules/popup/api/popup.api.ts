import { apiService } from '@/shared/services/api.service';
import type { HomepagePopup, CreatePopupRequest, UpdatePopupRequest } from '@/shared/types/popup.types';

export const popupApi = {
  getAll: () =>
    apiService.get<HomepagePopup[]>('/popups'),

  getActive: () =>
    apiService.get<HomepagePopup[]>('/popups/active'),

  getById: (id: number) =>
    apiService.get<HomepagePopup>(`/popups/${id}`),

  create: (data: CreatePopupRequest) =>
    apiService.post<HomepagePopup>('/popups', data),

  update: (id: number, data: UpdatePopupRequest) =>
    apiService.put<HomepagePopup>(`/popups/${id}`, data),

  delete: (id: number) =>
    apiService.delete(`/popups/${id}`),

  toggleActive: (id: number) =>
    apiService.patch<HomepagePopup>(`/popups/${id}/toggle`),
};
