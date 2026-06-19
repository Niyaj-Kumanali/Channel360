import { useQuery } from '@tanstack/react-query';
import { menuApi } from '@/modules/menu/api/menu.api';
import type { MenuItem } from '@/shared/types/menu.types';

export const useMenu = () => {
  return useQuery<MenuItem[]>({
    queryKey: ['menu'],
    queryFn: async () => {
      const response = await menuApi.getMenu();
      return response.data ?? [];
    },
    staleTime: 5 * 60 * 1000,
  });
};
