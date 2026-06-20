import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { useAuth } from '@/features/auth/hooks/useAuth';
import type { LoginFormData } from '@/features/auth/schemas/auth.schema';

export const useLogin = () => {
  const navigate = useNavigate();
  const { login } = useAuth();

  return useMutation({
    mutationFn: async (data: LoginFormData) => {
      await login(data.email, data.password);
    },
    onSuccess: () => {
      toast.success('Login successful');
      navigate('/');
    },
    onError: (error: Error) => {
      toast.error(error.message || 'Login failed');
    },
  });
};
