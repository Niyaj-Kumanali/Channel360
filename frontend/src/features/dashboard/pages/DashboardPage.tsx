import React from 'react';
import { useAuth } from '@/features/auth/hooks/useAuth';

export const DashboardPage: React.FC = () => {
  const { user } = useAuth();

  return (
    <div className="mx-auto max-w-5xl">
      <div className="rounded-xl border border-border bg-card p-8">
        <h1 className="text-2xl font-bold text-foreground mb-2">
          Welcome, {user?.firstName}!
        </h1>
        <p className="text-muted-foreground">
          You are logged in as <strong>{user?.email}</strong>
          {user?.roles?.length ? (
            <span> with role(s): {user.roles.map(r => r.name).join(', ')}</span>
          ) : null}
        </p>
      </div>
    </div>
  );
};
