import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { Link } from 'react-router-dom';
import toast from 'react-hot-toast';
import { ArrowLeft, Mail } from 'lucide-react';
import { ApiError } from '@/lib/api-error';
import { mapErrorToField } from '@/lib/error-utils';
import { forgotPasswordSchema, type ForgotPasswordFormData } from '@/features/auth/schemas/auth.schema';
import { useForgotPassword } from '@/features/auth/hooks/useForgotPassword';
import { FloatingLabelInput } from '@/components/ui/FloatingLabelInput';
import { Button } from '@/components/ui/Button';

export const ForgotPasswordPage: React.FC = () => {
  const [sent, setSent] = useState(false);
  const mutation = useForgotPassword();

  const { register, handleSubmit, setError, formState: { errors } } = useForm<ForgotPasswordFormData>({
    resolver: yupResolver(forgotPasswordSchema),
    mode: 'onBlur',
    reValidateMode: 'onBlur',
  });

  const onSubmit = (data: ForgotPasswordFormData) => {
    mutation.mutate(data, {
      onSuccess: () => setSent(true),
      onError: (error) => {
        if (error instanceof ApiError) {
          for (const msg of error.errors ?? []) {
            const field = mapErrorToField(msg);
            if (field) setError(field as keyof ForgotPasswordFormData, { message: msg });
          }
          toast.error(error.message);
        }
      },
    });
  };

  if (sent) {
    return (
      <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-8 animate-scale-in text-center">
        <div className="h-14 w-14 rounded-full bg-primary-50 flex items-center justify-center mx-auto mb-4">
          <Mail className="h-7 w-7 text-primary" />
        </div>
        <h2 className="text-xl font-bold text-gray-900 mb-2">Check your email</h2>
        <p className="text-gray-500 text-sm mb-6">
          If an account exists with that email, we've sent a password reset link.
        </p>
        <Link to="/login">
          <Button variant="outline">
            <ArrowLeft className="h-4 w-4 mr-2" />
            Back to Login
          </Button>
        </Link>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-8 animate-fade-in">
      <div className="text-center mb-8">
        <div className="h-12 w-12 rounded-full bg-primary-50 flex items-center justify-center mx-auto mb-4">
          <Mail className="h-6 w-6 text-primary" />
        </div>
        <h1 className="text-2xl font-bold text-gray-900">Forgot password?</h1>
        <p className="text-gray-500 mt-1">
          No worries. Enter your email and we'll send you a reset link.
        </p>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-5" noValidate>
        <FloatingLabelInput
          id="email"
          label="Email"
          type="email"
          placeholder="name@example.com"
          hint="e.g. name@example.com"
          error={errors.email?.message}
          required
          {...register('email')}
        />

        <Button type="submit" className="w-full h-11" isLoading={mutation.isPending}>
          Send Reset Link
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
