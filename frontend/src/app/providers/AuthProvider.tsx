import React, { createContext, useState, useEffect, type ReactNode } from 'react';
import type { User, AuthState } from '@/shared/types/auth.types';
import { apiService } from '@/shared/services/api.service';

interface AuthContextType extends AuthState {
  login: (email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  hasRole: (role: string) => boolean;
  hasAnyRole: (...roles: string[]) => boolean;
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [state, setState] = useState<AuthState>({
    user: null,
    isAuthenticated: false,
    isLoading: true,
  });

  useEffect(() => {
    const userJson = localStorage.getItem('user');
    const token = localStorage.getItem('access_token');
    if (userJson && token) {
      try {
        const user = JSON.parse(userJson) as User;
        setState({ user, isAuthenticated: true, isLoading: false });
      } catch {
        setState({ user: null, isAuthenticated: false, isLoading: false });
      }
    } else {
      setState({ user: null, isAuthenticated: false, isLoading: false });
    }
  }, []);

  const login = async (email: string, password: string) => {
    const response = await apiService.post<any>('/auth/login', { email, password });
    if (response.success) {
      apiService.setTokens(response.data.accessToken, response.data.refreshToken);
      const userResponse = await apiService.get<User>('/auth/me');
      if (userResponse.success) {
        localStorage.setItem('user', JSON.stringify(userResponse.data));
        setState({ user: userResponse.data, isAuthenticated: true, isLoading: false });
      }
    } else {
      throw new Error(response.message || 'Login failed');
    }
  };

  const logout = async () => {
    try {
      const refreshToken = localStorage.getItem('refresh_token');
      if (refreshToken) {
        await apiService.post('/auth/logout', { refreshToken });
      }
    } catch {
      // ignore
    } finally {
      apiService.clearTokens();
      setState({ user: null, isAuthenticated: false, isLoading: false });
    }
  };

  const hasRole = (role: string): boolean => {
    return state.user?.roles?.some(r => r.name === role || r.name === `ROLE_${role}`) ?? false;
  };

  const hasAnyRole = (...roles: string[]): boolean => {
    return roles.some(role => hasRole(role));
  };

  return (
    <AuthContext.Provider value={{ ...state, login, logout, hasRole, hasAnyRole }}>
      {children}
    </AuthContext.Provider>
  );
};
