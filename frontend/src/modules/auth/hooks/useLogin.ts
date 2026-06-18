import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { authApi } from '../api/auth.api';
import type { LoginFormData } from '../schemas/auth.schema';
import { useAuth } from '@/shared/hooks/useAuth';

export const useLogin = () => {
  const navigate = useNavigate();
  const { login } = useAuth();

  return useMutation({
    mutationFn: async (data: LoginFormData) => {
      await login(data.email, data.password);
    },
    onSuccess: () => {
      toast.success('Login successful');
      navigate('/dashboard');
    },
    onError: (error: Error) => {
      toast.error(error.message || 'Login failed');
    },
  });
};
