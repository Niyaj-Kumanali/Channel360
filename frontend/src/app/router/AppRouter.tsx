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
import { SectionListPage } from '@/features/cms/pages/SectionListPage';
import { SectionFormPage } from '@/features/cms/pages/SectionFormPage';
import { PopupListPage } from '@/features/cms/pages/PopupListPage';
import { PopupFormPage } from '@/features/cms/pages/PopupFormPage';

export const AppRouter: React.FC = () => {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-primary-950">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-white" />
      </div>
    );
  }

  return (
    <Routes>
      {isAuthenticated ? (
        <Route element={<DashboardLayout />}>
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/admin/sections" element={<SectionListPage />} />
          <Route path="/admin/sections/new" element={<SectionFormPage />} />
          <Route path="/admin/sections/:id" element={<SectionFormPage />} />
          <Route path="/admin/popups" element={<PopupListPage />} />
          <Route path="/admin/popups/new" element={<PopupFormPage />} />
          <Route path="/admin/popups/:id" element={<PopupFormPage />} />
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
