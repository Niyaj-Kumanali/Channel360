import * as yup from 'yup';

export const loginSchema = yup.object({
  email: yup.string().email('Please enter a valid email').required('Email is required'),
  password: yup.string().min(6, 'Password must be at least 6 characters').required('Password is required'),
});

export const forgotPasswordSchema = yup.object({
  email: yup.string().email('Please enter a valid email').required('Email is required'),
});

export const resetPasswordSchema = yup.object({
  token: yup.string().required('Token is required'),
  newPassword: yup.string().min(6, 'Password must be at least 6 characters').required('New password is required'),
});

export const changePasswordSchema = yup.object({
  oldPassword: yup.string().required('Current password is required'),
  newPassword: yup.string().min(6, 'Password must be at least 6 characters').required('New password is required'),
  confirmPassword: yup.string().required('Please confirm your password').oneOf([yup.ref('newPassword')], 'Passwords do not match'),
});

export type LoginFormData = yup.InferType<typeof loginSchema>;
export type ForgotPasswordFormData = yup.InferType<typeof forgotPasswordSchema>;
export type ResetPasswordFormData = yup.InferType<typeof resetPasswordSchema>;
export type ChangePasswordFormData = yup.InferType<typeof changePasswordSchema>;
