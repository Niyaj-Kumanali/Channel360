import { apiService } from '@/shared/services/api.service';
import type { MenuItem } from '@/shared/types/menu.types';

export const menuApi = {
  getMenu: () =>
    apiService.get<MenuItem[]>('/auth/menu'),
};
