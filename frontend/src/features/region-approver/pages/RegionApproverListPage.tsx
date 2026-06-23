import React, { useEffect, useState } from 'react';
import { Plus, Pencil, Trash2 } from 'lucide-react';
import toast from 'react-hot-toast';
import { regionApproverApi } from '@/features/region-approver/api/region-approver.api';
import { regionApi } from '@/features/region/api/region.api';
import type { RegionApprover, RegionApproverRequest } from '@/features/region-approver/types/region-approver.types';
import type { Region } from '@/features/region/types/region.types';
import type { Role } from '@/features/auth/types/auth.types';
import type { UserDetail } from '@/features/user/types/user.types';
import { roleApi } from '@/features/role/api/role.api';
import { userApi } from '@/features/user/api/user.api';
import { Loader } from '@/components/ui/Loader';
import { Button } from '@/components/ui/Button';
import { useAuth } from '@/features/auth/hooks/useAuth';

const drawerDefaults: RegionApproverRequest = { regionId: 0, roleId: 0, userId: 0 };

export const RegionApproverListPage: React.FC = () => {
  const { hasAnyRole } = useAuth();
  const canEdit = hasAnyRole('SUPER_ADMIN');
  const [approvers, setApprovers] = useState<RegionApprover[]>([]);
  const [regions, setRegions] = useState<Region[]>([]);
  const [roles, setRoles] = useState<Role[]>([]);
  const [users, setUsers] = useState<UserDetail[]>([]);
  const [loading, setLoading] = useState(true);
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [editId, setEditId] = useState<number | null>(null);
  const [form, setForm] = useState<RegionApproverRequest>({ ...drawerDefaults });
  const [saving, setSaving] = useState(false);

  const fetchData = async () => {
    try {
      const [aRes, regRes, rolRes, uRes] = await Promise.all([
        regionApproverApi.getAll(),
        regionApi.getAll(),
        roleApi.getAll(),
        userApi.getAll({ size: 999 }),
      ]);
      if (aRes.success) setApprovers(aRes.data);
      if (regRes.success) setRegions(regRes.data);
      if (rolRes.success) setRoles(rolRes.data);
      setUsers(uRes.content);
    } catch {
      toast.error('Failed to load data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchData(); }, []);

  const openCreate = () => {
    setForm({ ...drawerDefaults });
    setEditId(null);
    setDrawerOpen(true);
  };

  const openEdit = (a: RegionApprover) => {
    setForm({ regionId: a.regionId, roleId: a.roleId, userId: a.userId });
    setEditId(a.id);
    setDrawerOpen(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.regionId || !form.roleId || !form.userId) {
      toast.error('All fields are required');
      return;
    }
    setSaving(true);
    try {
      if (editId) {
        const res = await regionApproverApi.update(editId, form);
        if (res.success) { toast.success('Approver updated'); setDrawerOpen(false); fetchData(); }
      } else {
        const res = await regionApproverApi.create(form);
        if (res.success) { toast.success('Approver created'); setDrawerOpen(false); fetchData(); }
      }
    } catch {
      toast.error(editId ? 'Failed to update' : 'Failed to create');
    } finally { setSaving(false); }
  };

  const handleDeactivate = async (a: RegionApprover) => {
    if (!window.confirm(`Deactivate approver "${a.userName}" for ${a.regionName}?`)) return;
    try {
      const res = await regionApproverApi.deactivate(a.id);
      if (res.success) { toast.success('Approver deactivated'); fetchData(); }
    } catch { toast.error('Failed to deactivate'); }
  };

  if (loading) {
    return <div className="flex items-center justify-center min-h-[calc(100vh-10rem)]"><Loader size="lg" /></div>;
  }

  return (
    <div className="mx-auto max-w-6xl">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-foreground">Region Approvers</h1>
          <p className="text-sm text-muted-foreground mt-1">Map specific users as approvers for region + role combinations</p>
        </div>
        {canEdit && (
          <Button onClick={openCreate} className="gap-2"><Plus className="h-4 w-4" /> Add Approver</Button>
        )}
      </div>

      <div className="rounded-xl border border-border bg-card overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-border bg-muted/50">
                <th className="text-left px-4 py-3 font-medium text-muted-foreground">Region</th>
                <th className="text-left px-4 py-3 font-medium text-muted-foreground">Role</th>
                <th className="text-left px-4 py-3 font-medium text-muted-foreground">Approver</th>
                <th className="text-left px-4 py-3 font-medium text-muted-foreground">Status</th>
                <th className="text-right px-4 py-3 font-medium text-muted-foreground">Actions</th>
              </tr>
            </thead>
            <tbody>
              {approvers.length === 0 ? (
                <tr><td colSpan={5} className="px-4 py-12 text-center text-muted-foreground">No approvers configured.</td></tr>
              ) : (
                approvers.map(a => (
                  <tr key={a.id} className="border-b border-border last:border-0 hover:bg-muted/30 transition-colors">
                    <td className="px-4 py-3">
                      <span className="font-medium text-foreground">{a.regionName}</span>
                      <span className="text-xs text-muted-foreground ml-2">{a.regionPath}</span>
                    </td>
                    <td className="px-4 py-3"><span className="text-foreground">{a.roleName}</span></td>
                    <td className="px-4 py-3">
                      <span className="text-foreground">{a.userName}</span>
                      <span className="text-xs text-muted-foreground ml-2">{a.userEmail}</span>
                    </td>
                    <td className="px-4 py-3">
                      <span className="inline-flex items-center rounded-full bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300 px-2 py-0.5 text-xs font-medium">Active</span>
                    </td>
                    <td className="px-4 py-3 text-right">
                      <div className="flex items-center justify-end gap-1">
                        <button onClick={() => openEdit(a)}
                          className="flex h-8 w-8 items-center justify-center rounded-lg text-muted-foreground hover:bg-accent hover:text-accent-foreground transition-colors">
                          <Pencil className="h-4 w-4" />
                        </button>
                        <button onClick={() => handleDeactivate(a)}
                          className="flex h-8 w-8 items-center justify-center rounded-lg text-muted-foreground hover:bg-destructive/10 hover:text-destructive transition-colors">
                          <Trash2 className="h-4 w-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {drawerOpen && (
        <div className="fixed inset-0 z-50 flex justify-end">
          <div className="fixed inset-0 bg-black/40" onClick={() => setDrawerOpen(false)} />
          <div className="relative w-full max-w-lg bg-background border-l border-border shadow-xl overflow-y-auto">
            <div className="flex items-center justify-between px-6 py-4 border-b border-border">
              <h2 className="text-lg font-semibold text-foreground">{editId ? 'Edit Approver' : 'Add Approver'}</h2>
              <button onClick={() => setDrawerOpen(false)} className="text-muted-foreground hover:text-foreground text-xl">&times;</button>
            </div>
            <form onSubmit={handleSubmit} className="p-6 space-y-4">
              <div>
                <label className="block text-sm font-medium text-foreground mb-1">Region</label>
                <select value={form.regionId} onChange={e => setForm({ ...form, regionId: parseInt(e.target.value) || 0 })}
                  className="w-full rounded-lg border border-input bg-background px-3 py-2 text-sm outline-none focus:border-ring focus:ring-1 focus:ring-ring">
                  <option value={0}>Select region...</option>
                  {regions.map(r => <option key={r.id} value={r.id}>{r.path}</option>)}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-foreground mb-1">Role</label>
                <select value={form.roleId} onChange={e => setForm({ ...form, roleId: parseInt(e.target.value) || 0 })}
                  className="w-full rounded-lg border border-input bg-background px-3 py-2 text-sm outline-none focus:border-ring focus:ring-1 focus:ring-ring">
                  <option value={0}>Select role...</option>
                  {roles.map(r => <option key={r.id} value={r.id}>{r.name}</option>)}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-foreground mb-1">User</label>
                <select value={form.userId} onChange={e => setForm({ ...form, userId: parseInt(e.target.value) || 0 })}
                  className="w-full rounded-lg border border-input bg-background px-3 py-2 text-sm outline-none focus:border-ring focus:ring-1 focus:ring-ring">
                  <option value={0}>Select user...</option>
                  {users.map(u => <option key={u.id} value={u.id}>{u.firstName} {u.lastName} - {u.employeeId}</option>)}
                </select>
              </div>
              <div className="flex gap-3 pt-4">
                <Button type="submit" isLoading={saving} className="flex-1">{editId ? 'Update' : 'Create'}</Button>
                <Button type="button" variant="outline" onClick={() => setDrawerOpen(false)}>Cancel</Button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
