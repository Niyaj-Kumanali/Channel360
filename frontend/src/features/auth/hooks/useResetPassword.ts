import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { ApiError } from '@/lib/api-error';
import { authApi } from '@/features/auth/api/auth.api';
import type { ResetPasswordFormData } from '@/features/auth/schemas/auth.schema';

export const useResetPassword = () => {
  const navigate = useNavigate();

  return useMutation({
    mutationFn: async (data: ResetPasswordFormData) => {
      const response = await authApi.resetPassword(data);
      if (!response.success) {
        throw new ApiError(response.message || 'Failed to reset password', { errors: (response as any).errors });
      }
    },
    onSuccess: () => {
      toast.success('Password reset successful');
      navigate('/login');
    },
  });
};
