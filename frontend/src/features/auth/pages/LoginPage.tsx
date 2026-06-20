import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Link } from 'react-router-dom';
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
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = (data: LoginFormData) => {
    loginMutation.mutate(data);
  };

  return (
    <div className="bg-white rounded-2xl shadow-sm border border-gray-100 border-t-4 border-primary p-8 animate-fade-in">
      <div className="text-center mb-8">
        <h1 className="text-2xl font-bold text-gray-900">Sign in</h1>
        <p className="text-gray-500 mt-1.5">Enter your credentials to access your account</p>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
        <FloatingLabelInput
          id="email"
          label="Email"
          type="email"
          placeholder="name@example.com"
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
            placeholder="Enter your password"
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
