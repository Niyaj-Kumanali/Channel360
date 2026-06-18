import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { cmsApi } from '../api/cms.api';

export const useSections = () => {
  return useQuery({
    queryKey: ['cms-sections'],
    queryFn: () => cmsApi.getAll(),
  });
};

export const useActiveSections = () => {
  return useQuery({
    queryKey: ['cms-sections', 'active'],
    queryFn: () => cmsApi.getActive(),
  });
};

export const useSection = (id: number) => {
  return useQuery({
    queryKey: ['cms-sections', id],
    queryFn: () => cmsApi.getById(id),
    enabled: !!id,
  });
};

export const useCreateSection = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: any) => cmsApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cms-sections'] });
      toast.success('Section created');
    },
    onError: (error: Error) => {
      toast.error(error.message || 'Failed to create section');
    },
  });
};

export const useUpdateSection = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: any }) => cmsApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cms-sections'] });
      toast.success('Section updated');
    },
    onError: (error: Error) => {
      toast.error(error.message || 'Failed to update section');
    },
  });
};

export const useDeleteSection = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => cmsApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cms-sections'] });
      toast.success('Section deleted');
    },
    onError: (error: Error) => {
      toast.error(error.message || 'Failed to delete section');
    },
  });
};

export const useReorderSections = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (sectionIds: number[]) => cmsApi.reorder(sectionIds),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cms-sections'] });
      toast.success('Order updated');
    },
  });
};

export const useToggleSection = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => cmsApi.toggleActive(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cms-sections'] });
    },
  });
};
