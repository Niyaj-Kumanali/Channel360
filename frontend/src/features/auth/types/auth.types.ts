export interface User {
  id: number;
  employeeId: string;
  firstName: string;
  lastName: string;
  email: string;
  mobileNumber: string;
  status: string;
  roles: Role[];
  lastLoginAt: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface Role {
  id: number;
  name: string;
  description: string;
  permissions: string[];
  permissionIds: number[];
}

export interface Permission {
  id: number;
  name: string;
  description: string;
  module: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

export interface MenuItem {
  path: string;
  label: string;
  icon: string;
  roles: string[];
  children?: MenuItem[];
}

export interface MenuItemResponse {
  id: number;
  parentId: number | null;
  label: string;
  path: string;
  icon: string | null;
  roleIds: number[];
  displayOrder: number;
  active: boolean;
  permissionName?: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
}
