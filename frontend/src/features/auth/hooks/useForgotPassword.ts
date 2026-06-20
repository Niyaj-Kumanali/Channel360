import { useMutation } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { authApi } from '@/features/auth/api/auth.api';
import type { ForgotPasswordFormData } from '@/features/auth/schemas/auth.schema';

export const useForgotPassword = () => {
  return useMutation({
    mutationFn: async (data: ForgotPasswordFormData) => {
      const response = await authApi.forgotPassword(data);
      if (!response.success) {
        throw new Error(response.message || 'Failed to send reset link');
      }
    },
    onSuccess: () => {
      toast.success('Reset link sent to your email');
    },
    onError: (error: Error) => {
      toast.error(error.message || 'Failed to send reset link');
    },
  });
};
