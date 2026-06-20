import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import {
  LayoutDashboard,
  Users,
  FileText,
  Layout,
  Square,
  ChevronDown,
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
  FileText,
  Layout,
  Square,
};

interface SidebarProps {
  open: boolean;
  onClose: () => void;
}

const NavItem: React.FC<{ item: MenuItem; onNavigate: (path: string) => void; depth?: number }> = ({ item, onNavigate, depth = 0 }) => {
  const location = useLocation();
  const Icon = iconMap[item.icon] || LayoutDashboard;
  const isActive = location.pathname === item.path;

  return (
    <button
      onClick={() => onNavigate(item.path)}
      className={cn(
        'flex w-full items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium transition-colors',
        isActive
          ? 'bg-primary/10 text-primary'
          : 'text-muted-foreground hover:bg-accent hover:text-accent-foreground',
        depth > 0 && 'pl-9'
      )}
    >
      {depth === 0 && <Icon className="h-4 w-4 shrink-0" />}
      {item.label}
    </button>
  );
};

const NavGroup: React.FC<{ item: MenuItem; onNavigate: (path: string) => void }> = ({ item, onNavigate }) => {
  const [expanded, setExpanded] = useState(true);
  const location = useLocation();
  const Icon = iconMap[item.icon] || LayoutDashboard;
  const isChildActive = item.children?.some(c => location.pathname === c.path);

  return (
    <div>
      <button
        onClick={() => setExpanded(!expanded)}
        className={cn(
          'flex w-full items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium transition-colors',
          isChildActive
            ? 'text-primary'
            : 'text-muted-foreground hover:bg-accent hover:text-accent-foreground'
        )}
      >
        <Icon className="h-4 w-4 shrink-0" />
        <span className="flex-1 text-left">{item.label}</span>
        <ChevronDown className={cn('h-3.5 w-3.5 transition-transform', expanded && 'rotate-180')} />
      </button>
      {expanded && item.children && (
        <div className="mt-1 space-y-1 pl-3 border-l border-border ml-3">
          {item.children.map(child => (
            <NavItem key={child.path} item={child} onNavigate={onNavigate} depth={1} />
          ))}
        </div>
      )}
    </div>
  );
};

export const Sidebar: React.FC<SidebarProps> = ({ open, onClose }) => {
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
          {menu.map(item =>
            item.children && item.children.length > 0 ? (
              <NavGroup key={item.label} item={item} onNavigate={handleNavigate} />
            ) : (
              <NavItem key={item.path} item={item} onNavigate={handleNavigate} />
            )
          )}
        </nav>
        <div className="border-t border-border p-4">
          <p className="text-xs text-muted-foreground">Channel360 v1.0</p>
        </div>
      </aside>
    </>
  );
};
