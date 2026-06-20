import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useSearchParams, useNavigate, Link } from 'react-router-dom';
import { useMutation } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { ArrowLeft } from 'lucide-react';
import { resetPasswordSchema, type ResetPasswordFormData } from '@/features/auth/schemas/auth.schema';
import { authApi } from '@/features/auth/api/auth.api';
import { Input } from '@/components/ui/Input';
import { Button } from '@/components/ui/Button';
import { Card, CardContent, CardHeader } from '@/components/ui/Card';

export const ResetPasswordPage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const token = searchParams.get('token') || '';

  const { register, handleSubmit, formState: { errors } } = useForm<ResetPasswordFormData>({
    resolver: zodResolver(resetPasswordSchema),
    defaultValues: { token, newPassword: '' },
  });

  const mutation = useMutation({
    mutationFn: async (data: ResetPasswordFormData) => {
      const response = await authApi.resetPassword(data);
      if (!response.success) {
        throw new Error(response.message || 'Failed to reset password');
      }
    },
    onSuccess: () => {
      toast.success('Password reset successful');
      navigate('/login');
    },
    onError: (error: Error) => {
      toast.error(error.message || 'Failed to reset password');
    },
  });

  return (
    <Card>
      <CardHeader>
        <h2 className="text-xl font-bold text-gray-900">Reset Password</h2>
        <p className="text-gray-500 text-sm mt-1">Enter your new password</p>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit((data) => mutation.mutate(data))} className="space-y-4">
          <input type="hidden" {...register('token')} />
          <Input
            id="newPassword"
            label="New Password"
            type="password"
            placeholder="Enter new password"
            error={errors.newPassword?.message}
            {...register('newPassword')}
          />
          <Button type="submit" className="w-full" isLoading={mutation.isPending}>
            Reset Password
          </Button>
        </form>
        <div className="mt-4 text-center">
          <Link to="/login" className="text-sm text-primary-600 hover:text-primary-700">
            <ArrowLeft className="h-4 w-4 inline mr-1" />
            Back to Login
          </Link>
        </div>
      </CardContent>
    </Card>
  );
};
