import React from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { Link } from 'react-router-dom';
import toast from 'react-hot-toast';
import { ApiError } from '@/lib/api-error';
import { mapErrorToField } from '@/lib/error-utils';
import { loginSchema, type LoginFormData } from '@/features/auth/schemas/auth.schema';
import { useLogin } from '@/features/auth/hooks/useLogin';
import { FloatingLabelInput } from '@/components/ui/FloatingLabelInput';
import { PasswordInput } from '@/components/ui/PasswordInput';
import { Button } from '@/components/ui/Button';

export const LoginPage: React.FC = () => {
  const loginMutation = useLogin();

  const {
    register,
    handleSubmit,
    setError,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: yupResolver(loginSchema),
    mode: 'onBlur',
    reValidateMode: 'onBlur',
  });

  const onSubmit = (data: LoginFormData) => {
    loginMutation.mutate(data, {
      onError: (error) => {
        if (error instanceof ApiError) {
          for (const msg of error.errors ?? []) {
            const field = mapErrorToField(msg);
            if (field) setError(field as keyof LoginFormData, { message: msg });
          }
          toast.error(error.message);
        }
      },
    });
  };

  return (
    <div className="bg-white rounded-2xl shadow-sm border border-gray-100 border-t-4 border-primary p-8 animate-fade-in">
      <div className="text-center mb-8">
        <h1 className="text-2xl font-bold text-gray-900">Sign in</h1>
        <p className="text-gray-500 mt-1.5">Enter your credentials to access your account</p>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-5" noValidate>
        <FloatingLabelInput
          id="email"
          label="Email"
          type="email"
          placeholder="name@example.com"
          hint="e.g. name@example.com"
          error={errors.email?.message}
          {...register('email')}
        />

        <div>
          <div className="flex items-center justify-between mb-1">
            <span />
            <Link
              to="/forgot-password"
              className="text-xs text-gray-400 hover:text-primary-600 transition-colors"
            >
              Forgot password?
            </Link>
          </div>
          <PasswordInput
            id="password"
            label="Password"
            placeholder="e.g. MyP@ss123"
            hint="At least 6 characters"
            error={errors.password?.message}
            {...register('password')}
          />
        </div>

        <Button type="submit" className="w-full h-11" isLoading={loginMutation.isPending}>
          Sign In
        </Button>
      </form>

      <p className="mt-6 text-center text-xs text-gray-400">
        Need an account? Contact your system administrator.
      </p>
    </div>
  );
};
