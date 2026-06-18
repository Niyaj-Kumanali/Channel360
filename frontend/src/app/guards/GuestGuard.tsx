import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '@/shared/hooks/useAuth';

export const GuestGuard: React.FC = () => {
  const { isAuthenticated } = useAuth();

  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />;
  }

  return <Outlet />;
};
