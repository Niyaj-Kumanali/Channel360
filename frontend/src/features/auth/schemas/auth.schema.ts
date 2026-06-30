import * as yup from 'yup';

export const loginSchema = yup.object({
  email: yup.string().email('Please enter a valid email').required('Email is required'),
  password: yup.string().required('Password is required'),
});

export const forgotPasswordSchema = yup.object({
  email: yup.string().email('Please enter a valid email').required('Email is required'),
});

export const resetPasswordSchema = yup.object({
  token: yup.string().required('Token is required'),
  newPassword: yup.string().required('New password is required'),
});

export type LoginFormData = yup.InferType<typeof loginSchema>;
export type ForgotPasswordFormData = yup.InferType<typeof forgotPasswordSchema>;
export type ResetPasswordFormData = yup.InferType<typeof resetPasswordSchema>;
