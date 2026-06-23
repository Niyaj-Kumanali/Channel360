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
import { FaqPage } from '@/features/faq/pages/FaqPage';
import { Loader } from '@/components/ui/Loader';
import { PopupListPage } from '@/features/cms/pages/PopupListPage';
import { PopupFormPage } from '@/features/cms/pages/PopupFormPage';
import { RoleListPage } from '@/features/role/pages/RoleListPage';
import { RoleFormPage } from '@/features/role/pages/RoleFormPage';
import { MenuListPage } from '@/features/menu/pages/MenuListPage';
import { RegionListPage } from '@/features/region/pages/RegionListPage';
import { WorkflowListPage } from '@/features/workflow/pages/WorkflowListPage';
import { RegionApproverListPage } from '@/features/region-approver/pages/RegionApproverListPage';
import { AuditLogListPage } from '@/features/audit/pages/AuditLogListPage';
import { ApprovalListPage } from '@/features/approval/pages/ApprovalListPage';
import { PermissionMatrixPage } from '@/features/permission/pages/PermissionMatrixPage';
import { UserListPage } from '@/features/user/pages/UserListPage';

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
      <Route path="/faq" element={<FaqPage />} />
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
          <Route path="/admin/menu" element={<MenuListPage />} />
          <Route path="/admin/menu/new" element={<Navigate to="/admin/menu" replace />} />
          <Route path="/admin/menu/:id" element={<Navigate to="/admin/menu" replace />} />
          <Route path="/admin/regions" element={<RegionListPage />} />
          <Route path="/admin/regions/new" element={<Navigate to="/admin/regions" replace />} />
          <Route path="/admin/regions/:id" element={<Navigate to="/admin/regions" replace />} />
          <Route path="/admin/workflows" element={<WorkflowListPage />} />
          <Route path="/admin/region-approvers" element={<RegionApproverListPage />} />
          <Route path="/admin/audit-logs" element={<AuditLogListPage />} />
          <Route path="/admin/approval-requests" element={<ApprovalListPage />} />
          <Route path="/admin/permissions" element={<PermissionMatrixPage />} />
          <Route path="/admin/users" element={<UserListPage />} />
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
