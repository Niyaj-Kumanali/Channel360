import React, { useEffect, useState, useCallback } from 'react';
import toast from 'react-hot-toast';
import { roleApi } from '@/features/role/api/role.api';
import type { Role, Permission } from '@/features/auth/types/auth.types';
import { Loader } from '@/components/ui/Loader';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { cn } from '@/lib/utils';

export const PermissionMatrixPage: React.FC = () => {
  const { hasAnyRole } = useAuth();
  const canEdit = hasAnyRole('SUPER_ADMIN');

  const [roles, setRoles] = useState<Role[]>([]);
  const [permissions, setPermissions] = useState<Permission[]>([]);
  const [loading, setLoading] = useState(true);
  const [savingRoleId, setSavingRoleId] = useState<number | null>(null);

  const fetchData = async () => {
    try {
      const [rRes, pRes] = await Promise.all([
        roleApi.getAll(),
        roleApi.getAllPermissions(),
      ]);
      if (rRes.success) setRoles(rRes.data);
      if (pRes.success) setPermissions(pRes.data);
    } catch { toast.error('Failed to load permissions data'); }
    finally { setLoading(false); }
  };

  useEffect(() => { fetchData(); }, []);

  const isAssigned = useCallback((roleId: number, permId: number) => {
    const role = roles.find(r => r.id === roleId);
    return role?.permissionIds?.includes(permId) ?? false;
  }, [roles]);

  const handleToggle = async (roleId: number, permId: number, checked: boolean) => {
    const role = roles.find(r => r.id === roleId);
    if (!role) return;

    let newIds: number[];
    if (checked) {
      newIds = [...(role.permissionIds || []), permId];
    } else {
      newIds = (role.permissionIds || []).filter(id => id !== permId);
    }

    setRoles(prev => prev.map(r => r.id === roleId ? { ...r, permissionIds: newIds } : r));

    setSavingRoleId(roleId);
    try {
      const res = await roleApi.updatePermissions(roleId, newIds);
      if (!res.success) {
        fetchData();
        toast.error('Failed to update permission');
      }
    } catch {
      fetchData();
      toast.error('Failed to update permission');
    } finally {
      setSavingRoleId(null);
    }
  };

  const modules = [...new Set(permissions.map(p => p.module))].sort();

  if (loading) {
    return <div className="flex items-center justify-center min-h-[calc(100vh-10rem)]"><Loader size="lg" /></div>;
  }

  return (
    <div className="mx-auto max-w-6xl">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-foreground">Permissions Matrix</h1>
          <p className="text-sm text-muted-foreground mt-1">Manage role-permission assignments with a roles × permissions grid</p>
        </div>
      </div>

      <div className="rounded-xl border border-border bg-card overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-border bg-muted/50">
                <th className="text-left px-4 py-3 font-medium text-muted-foreground min-w-[200px] sticky left-0 bg-muted/50 z-10">Permission</th>
                {roles.map(role => (
                  <th key={role.id} className="text-center px-3 py-3 font-medium text-muted-foreground min-w-[120px]">
                    <div className="text-xs">{role.name.replace('ROLE_', '')}</div>
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {modules.map(module => (
                <React.Fragment key={module}>
                  <tr className="border-b border-border bg-accent/20">
                    <td className="px-4 py-2 text-xs font-semibold text-muted-foreground uppercase tracking-wider sticky left-0 bg-accent/20" colSpan={roles.length + 1}>
                      {module}
                    </td>
                  </tr>
                  {permissions.filter(p => p.module === module).map(perm => (
                    <tr key={perm.id} className="border-b border-border last:border-0 hover:bg-muted/30 transition-colors">
                      <td className="px-4 py-3 text-foreground sticky left-0 bg-card hover:bg-muted/30">
                        <div className="text-sm font-medium">{perm.name}</div>
                        {perm.description && <div className="text-xs text-muted-foreground">{perm.description}</div>}
                      </td>
                      {roles.map(role => {
                        const assigned = isAssigned(role.id, perm.id);
                        return (
                          <td key={role.id} className="text-center px-3 py-3">
                            <label className="inline-flex items-center justify-center cursor-pointer">
                              <input
                                type="checkbox"
                                checked={assigned}
                                disabled={!canEdit || savingRoleId === role.id}
                                onChange={e => handleToggle(role.id, perm.id, e.target.checked)}
                                className={cn(
                                  'h-4 w-4 rounded border-input',
                                  savingRoleId === role.id && 'opacity-50'
                                )}
                              />
                            </label>
                          </td>
                        );
                      })}
                    </tr>
                  ))}
                </React.Fragment>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
