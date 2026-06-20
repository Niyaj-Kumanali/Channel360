import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useSearchParams, Link } from 'react-router-dom';
import { ArrowLeft, Lock } from 'lucide-react';
import { resetPasswordSchema, type ResetPasswordFormData } from '@/features/auth/schemas/auth.schema';
import { useResetPassword } from '@/features/auth/hooks/useResetPassword';
import { PasswordInput } from '@/components/ui/PasswordInput';
import { Button } from '@/components/ui/Button';

export const ResetPasswordPage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token') || '';
  const mutation = useResetPassword();

  const { register, handleSubmit, formState: { errors } } = useForm<ResetPasswordFormData>({
    resolver: zodResolver(resetPasswordSchema),
    defaultValues: { token, newPassword: '' },
  });

  const onSubmit = (data: ResetPasswordFormData) => {
    mutation.mutate(data);
  };

  return (
    <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-8 animate-fade-in">
      <div className="text-center mb-8">
        <div className="h-12 w-12 rounded-full bg-primary-50 flex items-center justify-center mx-auto mb-4">
          <Lock className="h-6 w-6 text-primary" />
        </div>
        <h1 className="text-2xl font-bold text-gray-900">Reset password</h1>
        <p className="text-gray-500 mt-1">
          Enter your new password below.
        </p>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
        <input type="hidden" {...register('token')} />
        <PasswordInput
          id="newPassword"
          label="New Password"
          placeholder="Enter new password"
          error={errors.newPassword?.message}
          {...register('newPassword')}
        />
        <Button type="submit" className="w-full h-11" isLoading={mutation.isPending}>
          Reset Password
        </Button>

        <Link
          to="/login"
          className="flex items-center justify-center gap-1.5 text-sm text-gray-500 hover:text-gray-700 transition-colors"
        >
          <ArrowLeft className="h-4 w-4" />
          Back to Login
        </Link>
      </form>
    </div>
  );
};
