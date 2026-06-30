import type { Role } from '@/features/auth/types/auth.types';

export interface UserDetail {
  id: number;
  employeeId: string;
  firstName: string;
  lastName: string;
  email: string;
  mobileNumber: string | null;
  status: string;
  lastLoginAt: string | null;
  roles: Role[];
  createdAt: string;
  updatedAt: string;
}

export interface CreateUserRequest {
  employeeId: string;
  firstName: string;
  lastName: string;
  email: string;
  mobileNumber?: string;
  roleIds: number[];
}

export interface UpdateUserRequest {
  firstName?: string;
  lastName?: string;
  mobileNumber?: string;
  roleIds?: number[];
}

export interface UserFilterParams {
  search?: string;
  status?: string;
  roleId?: number;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}
