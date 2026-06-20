import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useSearchParams, Link } from 'react-router-dom';
import { ArrowLeft } from 'lucide-react';
import { resetPasswordSchema, type ResetPasswordFormData } from '@/features/auth/schemas/auth.schema';
import { useResetPassword } from '@/features/auth/hooks/useResetPassword';
import { Input } from '@/components/ui/Input';
import { Button } from '@/components/ui/Button';
import { Card, CardContent, CardHeader } from '@/components/ui/Card';

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
    <Card>
      <CardHeader>
        <h2 className="text-xl font-bold text-gray-900">Reset Password</h2>
        <p className="text-gray-500 text-sm mt-1">Enter your new password</p>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
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
