import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { popupApi } from '../api/popup.api';

export const usePopups = () => {
  return useQuery({
    queryKey: ['popups'],
    queryFn: () => popupApi.getAll(),
  });
};

export const useActivePopups = () => {
  return useQuery({
    queryKey: ['popups', 'active'],
    queryFn: () => popupApi.getActive(),
  });
};

export const usePopup = (id: number) => {
  return useQuery({
    queryKey: ['popups', id],
    queryFn: () => popupApi.getById(id),
    enabled: !!id,
  });
};

export const useCreatePopup = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: any) => popupApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['popups'] });
      toast.success('Popup created');
    },
    onError: (error: Error) => {
      toast.error(error.message || 'Failed to create popup');
    },
  });
};

export const useUpdatePopup = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: any }) => popupApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['popups'] });
      toast.success('Popup updated');
    },
    onError: (error: Error) => {
      toast.error(error.message || 'Failed to update popup');
    },
  });
};

export const useDeletePopup = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => popupApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['popups'] });
      toast.success('Popup deleted');
    },
    onError: (error: Error) => {
      toast.error(error.message || 'Failed to delete popup');
    },
  });
};

export const useTogglePopup = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => popupApi.toggleActive(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['popups'] });
    },
  });
};
