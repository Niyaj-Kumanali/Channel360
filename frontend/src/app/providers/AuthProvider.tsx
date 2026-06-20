import React, { createContext, useState, useEffect, type ReactNode, useCallback } from 'react';
import { apiClient } from '@/lib/api-client';
import { authApi } from '@/features/auth/api/auth.api';
import type { User, AuthState } from '@/features/auth/types/auth.types';

interface AuthContextType extends AuthState {
  setUser: (user: User) => void;
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
    const init = async () => {
      const token = localStorage.getItem('access_token');
      if (!token) {
        setState({ user: null, isAuthenticated: false, isLoading: false });
        return;
      }
      try {
        const response = await authApi.getMe();
        if (response.success) {
          localStorage.setItem('user', JSON.stringify(response.data));
          setState({ user: response.data, isAuthenticated: true, isLoading: false });
        } else {
          apiClient.clearTokens();
          setState({ user: null, isAuthenticated: false, isLoading: false });
        }
      } catch {
        apiClient.clearTokens();
        setState({ user: null, isAuthenticated: false, isLoading: false });
      }
    };
    init();
  }, []);

  const setUser = useCallback((user: User) => {
    localStorage.setItem('user', JSON.stringify(user));
    setState({ user, isAuthenticated: true, isLoading: false });
  }, []);

  const logout = async () => {
    try {
      const refreshToken = localStorage.getItem('refresh_token');
      if (refreshToken) {
        await authApi.logout(refreshToken);
      }
    } catch {
      // ignore
    } finally {
      apiClient.clearTokens();
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
    <AuthContext.Provider value={{ ...state, setUser, logout, hasRole, hasAnyRole }}>
      {children}
    </AuthContext.Provider>
  );
};
