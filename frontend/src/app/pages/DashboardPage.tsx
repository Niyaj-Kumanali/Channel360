import React from 'react';
import { useAuth } from '@/shared/hooks/useAuth';

export const DashboardPage: React.FC = () => {
  const { user } = useAuth();

  return (
    <div>
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
        <p className="text-gray-500 mt-1">
          Welcome back, {user?.firstName} {user?.lastName}
        </p>
      </div>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div className="bg-white rounded-xl border border-gray-200 p-6">
          <h3 className="text-sm font-medium text-gray-500">Total Users</h3>
          <p className="text-2xl font-bold text-gray-900 mt-2">--</p>
        </div>
        <div className="bg-white rounded-xl border border-gray-200 p-6">
          <h3 className="text-sm font-medium text-gray-500">Active Sections</h3>
          <p className="text-2xl font-bold text-gray-900 mt-2">--</p>
        </div>
        <div className="bg-white rounded-xl border border-gray-200 p-6">
          <h3 className="text-sm font-medium text-gray-500">Active Popups</h3>
          <p className="text-2xl font-bold text-gray-900 mt-2">--</p>
        </div>
        <div className="bg-white rounded-xl border border-gray-200 p-6">
          <h3 className="text-sm font-medium text-gray-500">System Status</h3>
          <p className="text-2xl font-bold text-green-600 mt-2">Online</p>
        </div>
      </div>
    </div>
  );
};
