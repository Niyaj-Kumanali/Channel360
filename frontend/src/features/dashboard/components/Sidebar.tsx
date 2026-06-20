import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import {
  LayoutDashboard,
  Users,
  type LucideIcon,
} from 'lucide-react';
import { authApi } from '@/features/auth/api/auth.api';
import type { MenuItem } from '@/features/auth/types/auth.types';
import { cn } from '@/lib/utils';
import { Logo } from '@/components/ui/Logo';
import { useTheme } from '@/app/hooks/useTheme';

const iconMap: Record<string, LucideIcon> = {
  LayoutDashboard,
  Users,
};

interface SidebarProps {
  open: boolean;
  onClose: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ open, onClose }) => {
  const location = useLocation();
  const navigate = useNavigate();
  const { theme } = useTheme();
  const [menu, setMenu] = useState<MenuItem[]>([]);

  useEffect(() => {
    authApi.getMenu().then((res) => {
      if (res.success) setMenu(res.data);
    }).catch(() => {});
  }, []);

  const handleNavigate = (path: string) => {
    navigate(path);
    onClose();
  };

  const renderItem = (item: MenuItem) => {
    const Icon = iconMap[item.icon] || LayoutDashboard;
    const isActive = location.pathname === item.path;

    return (
      <button
        key={item.path}
        onClick={() => handleNavigate(item.path)}
        className={cn(
          'flex w-full items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium transition-colors',
          isActive
            ? 'bg-primary/10 text-primary'
            : 'text-muted-foreground hover:bg-accent hover:text-accent-foreground'
        )}
      >
        <Icon className="h-4 w-4 shrink-0" />
        {item.label}
      </button>
    );
  };

  return (
    <>
      {open && (
        <div
          className="fixed inset-0 z-30 bg-black/40 lg:hidden"
          onClick={onClose}
        />
      )}
      <aside
        className={cn(
          'fixed top-0 left-0 z-40 flex h-full w-64 flex-col border-r border-border bg-background transition-transform duration-200 lg:static lg:translate-x-0',
          open ? 'translate-x-0' : '-translate-x-full'
        )}
      >
        <div className="flex h-16 items-center px-6 border-b border-border">
          <Logo variant={theme === 'dark' ? 'light' : 'dark'} size="sm" />
        </div>
        <nav className="flex-1 overflow-y-auto p-4 space-y-1">
          {menu.map(renderItem)}
        </nav>
        <div className="border-t border-border p-4">
          <p className="text-xs text-muted-foreground">Channel360 v1.0</p>
        </div>
      </aside>
    </>
  );
};
