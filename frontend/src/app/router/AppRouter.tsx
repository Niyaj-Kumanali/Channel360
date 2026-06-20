import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { AuthLayout } from '@/app/layouts/AuthLayout';
import { HomePage } from '@/features/home/pages/HomePage';
import { LoginPage } from '@/features/auth/pages/LoginPage';
import { ForgotPasswordPage } from '@/features/auth/pages/ForgotPasswordPage';
import { ResetPasswordPage } from '@/features/auth/pages/ResetPasswordPage';
import { DashboardLayout } from '@/features/dashboard/components/DashboardLayout';
import { DashboardPage } from '@/features/dashboard/pages/DashboardPage';
import { SectionManagerPage } from '@/features/cms/pages/SectionManagerPage';
import { Loader } from '@/components/ui/Loader';
import { PopupListPage } from '@/features/cms/pages/PopupListPage';
import { PopupFormPage } from '@/features/cms/pages/PopupFormPage';
import { RoleListPage } from '@/features/role/pages/RoleListPage';
import { RoleFormPage } from '@/features/role/pages/RoleFormPage';

export const AppRouter: React.FC = () => {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-primary-950">
        <Loader size="lg" className="text-white" />
      </div>
    );
  }

  return (
    <Routes>
      {isAuthenticated ? (
        <Route element={<DashboardLayout />}>
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/admin/sections" element={<SectionManagerPage />} />
          <Route path="/admin/sections/new" element={<Navigate to="/admin/sections" replace />} />
          <Route path="/admin/sections/:id" element={<Navigate to="/admin/sections" replace />} />
          <Route path="/admin/popups" element={<PopupListPage />} />
          <Route path="/admin/popups/new" element={<PopupFormPage />} />
          <Route path="/admin/popups/:id" element={<PopupFormPage />} />
          <Route path="/admin/roles" element={<RoleListPage />} />
          <Route path="/admin/roles/new" element={<RoleFormPage />} />
          <Route path="/admin/roles/:id" element={<RoleFormPage />} />
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="/login" element={<Navigate to="/dashboard" replace />} />
          <Route path="/forgot-password" element={<Navigate to="/dashboard" replace />} />
          <Route path="/reset-password" element={<Navigate to="/dashboard" replace />} />
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Route>
      ) : (
        <>
          <Route path="/" element={<HomePage />} />
          <Route element={<AuthLayout />}>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/forgot-password" element={<ForgotPasswordPage />} />
            <Route path="/reset-password" element={<ResetPasswordPage />} />
          </Route>
          <Route path="/dashboard" element={<Navigate to="/login" replace />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </>
      )}
    </Routes>
  );
};
