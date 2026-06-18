import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { AuthGuard } from '@/app/guards/AuthGuard';
import { GuestGuard } from '@/app/guards/GuestGuard';
import { DashboardLayout } from '@/app/layouts/DashboardLayout';
import { AuthLayout } from '@/app/layouts/AuthLayout';
import { LoginPage } from '@/modules/auth/pages/LoginPage';
import { ForgotPasswordPage } from '@/modules/auth/pages/ForgotPasswordPage';
import { ResetPasswordPage } from '@/modules/auth/pages/ResetPasswordPage';
import { DashboardPage } from '@/app/pages/DashboardPage';
import { UserListPage } from '@/modules/users/pages/UserListPage';
import { UserCreatePage } from '@/modules/users/pages/UserCreatePage';
import { UserEditPage } from '@/modules/users/pages/UserEditPage';
import { CmsListPage } from '@/modules/cms/pages/CmsListPage';
import { CmsCreatePage } from '@/modules/cms/pages/CmsCreatePage';
import { CmsEditPage } from '@/modules/cms/pages/CmsEditPage';
import { PopupListPage } from '@/modules/popup/pages/PopupListPage';
import { PopupCreatePage } from '@/modules/popup/pages/PopupCreatePage';
import { PopupEditPage } from '@/modules/popup/pages/PopupEditPage';

export const AppRouter: React.FC = () => {
  return (
    <Routes>
      <Route element={<GuestGuard />}>
        <Route element={<AuthLayout />}>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/forgot-password" element={<ForgotPasswordPage />} />
          <Route path="/reset-password" element={<ResetPasswordPage />} />
        </Route>
      </Route>
      <Route element={<AuthGuard />}>
        <Route element={<DashboardLayout />}>
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/users" element={<UserListPage />} />
          <Route path="/users/new" element={<UserCreatePage />} />
          <Route path="/users/:id/edit" element={<UserEditPage />} />
          <Route path="/cms" element={<CmsListPage />} />
          <Route path="/cms/new" element={<CmsCreatePage />} />
          <Route path="/cms/:id/edit" element={<CmsEditPage />} />
          <Route path="/popups" element={<PopupListPage />} />
          <Route path="/popups/new" element={<PopupCreatePage />} />
          <Route path="/popups/:id/edit" element={<PopupEditPage />} />
        </Route>
      </Route>
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
};
