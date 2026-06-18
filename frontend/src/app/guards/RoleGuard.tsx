import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '@/shared/hooks/useAuth';

interface RoleGuardProps {
  children: React.ReactNode;
  roles: string[];
}

export const RoleGuard: React.FC<RoleGuardProps> = ({ children, roles }) => {
  const { hasAnyRole } = useAuth();

  if (!hasAnyRole(...roles)) {
    return <Navigate to="/dashboard" replace />;
  }

  return <>{children}</>;
};
