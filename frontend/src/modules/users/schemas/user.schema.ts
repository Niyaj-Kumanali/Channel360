import { z } from 'zod';

export const createUserSchema = z.object({
  employeeId: z.string().min(1, 'Employee ID is required'),
  firstName: z.string().min(1, 'First name is required'),
  lastName: z.string().min(1, 'Last name is required'),
  email: z.string().email('Please enter a valid email'),
  mobileNumber: z.string().optional(),
  password: z.string().min(6, 'Password must be at least 6 characters'),
  roleIds: z.array(z.number()).min(1, 'At least one role is required'),
});

export const updateUserSchema = z.object({
  firstName: z.string().min(1, 'First name is required').optional(),
  lastName: z.string().min(1, 'Last name is required').optional(),
  email: z.string().email('Please enter a valid email').optional(),
  mobileNumber: z.string().optional(),
  roleIds: z.array(z.number()).optional(),
});

export const userFilterSchema = z.object({
  search: z.string().optional(),
  status: z.string().optional(),
  roleId: z.string().optional(),
});

export type CreateUserFormData = z.infer<typeof createUserSchema>;
export type UpdateUserFormData = z.infer<typeof updateUserSchema>;
