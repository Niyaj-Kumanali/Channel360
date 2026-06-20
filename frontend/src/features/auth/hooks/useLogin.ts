import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { ApiError } from '@/lib/api-error';
import { apiClient } from '@/lib/api-client';
import { authApi } from '@/features/auth/api/auth.api';
import { useAuth } from '@/features/auth/hooks/useAuth';
import type { LoginFormData } from '@/features/auth/schemas/auth.schema';

export const useLogin = () => {
  const navigate = useNavigate();
  const { setUser } = useAuth();

  return useMutation({
    mutationFn: async (data: LoginFormData) => {
      const response = await authApi.login(data);
      if (!response.success) {
        throw new ApiError(response.message || 'Login failed', { errors: (response as any).errors });
      }
      apiClient.setTokens(response.data.accessToken, response.data.refreshToken);
      const userResponse = await authApi.getMe();
      if (!userResponse.success) {
        throw new ApiError(userResponse.message || 'Failed to get user info', { errors: (userResponse as any).errors });
      }
      return userResponse.data;
    },
    onSuccess: (user) => {
      setUser(user);
      toast.success('Login successful');
      navigate('/');
    },
  });
};
