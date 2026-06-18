import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Link } from 'react-router-dom';
import { useMutation } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { ArrowLeft, Mail } from 'lucide-react';
import { forgotPasswordSchema, type ForgotPasswordFormData } from '../schemas/auth.schema';
import { authApi } from '../api/auth.api';
import { Input } from '@/shared/components/ui/Input';
import { Button } from '@/shared/components/ui/Button';
import { Card, CardContent, CardHeader } from '@/shared/components/ui/Card';

export const ForgotPasswordPage: React.FC = () => {
  const [sent, setSent] = React.useState(false);

  const { register, handleSubmit, formState: { errors } } = useForm<ForgotPasswordFormData>({
    resolver: zodResolver(forgotPasswordSchema),
  });

  const mutation = useMutation({
    mutationFn: (data: ForgotPasswordFormData) => authApi.forgotPassword(data.email),
    onSuccess: () => {
      setSent(true);
      toast.success('Reset link sent to your email');
    },
    onError: (error: Error) => {
      toast.error(error.message || 'Failed to send reset link');
    },
  });

  if (sent) {
    return (
      <Card>
        <CardContent className="text-center py-8">
          <Mail className="h-12 w-12 text-primary mx-auto mb-4" />
          <h3 className="text-lg font-semibold text-gray-900 mb-2">Check your email</h3>
          <p className="text-gray-500 mb-6">
            If an account exists with that email, we've sent a password reset link.
          </p>
          <Link to="/login">
            <Button variant="outline">
              <ArrowLeft className="h-4 w-4 mr-2" />
              Back to Login
            </Button>
          </Link>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader>
        <h2 className="text-xl font-bold text-gray-900">Forgot Password</h2>
        <p className="text-gray-500 text-sm mt-1">
          Enter your email and we'll send you a reset link
        </p>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit((data) => mutation.mutate(data))} className="space-y-4">
          <Input
            id="email"
            label="Email"
            type="email"
            placeholder="Enter your email"
            error={errors.email?.message}
            {...register('email')}
          />
          <Button type="submit" className="w-full" isLoading={mutation.isPending}>
            Send Reset Link
          </Button>
          <Link to="/login" className="block text-center text-sm text-primary-600 hover:text-primary-700">
            <ArrowLeft className="h-4 w-4 inline mr-1" />
            Back to Login
          </Link>
        </form>
      </CardContent>
    </Card>
  );
};
