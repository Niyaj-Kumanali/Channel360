import React, { useEffect, useState } from 'react';
import { Plus, Pencil, Trash2, Key, ToggleLeft, ToggleRight, Search, ChevronLeft, ChevronRight } from 'lucide-react';
import toast from 'react-hot-toast';
import { userApi } from '@/features/user/api/user.api';
import { roleApi } from '@/features/role/api/role.api';
import type { UserDetail, CreateUserRequest, UpdateUserRequest } from '@/features/user/types/user.types';
import type { Role } from '@/features/auth/types/auth.types';
import { Loader } from '@/components/ui/Loader';
import { Button } from '@/components/ui/Button';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { cn } from '@/lib/utils';

const statusColors: Record<string, string> = {
  ACTIVE: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300',
  INACTIVE: 'bg-gray-100 text-gray-600 dark:bg-gray-800 dark:text-gray-400',
  SUSPENDED: 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-300',
};

const drawerDefaults: CreateUserRequest = {
  employeeId: '', firstName: '', lastName: '', email: '', mobileNumber: '', roleIds: [],
};

export const UserListPage: React.FC = () => {
  const { hasAnyRole } = useAuth();
  const canEdit = hasAnyRole('SUPER_ADMIN');

  const [users, setUsers] = useState<UserDetail[]>([]);
  const [roles, setRoles] = useState<Role[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('');

  const [drawerOpen, setDrawerOpen] = useState(false);
  const [editId, setEditId] = useState<number | null>(null);
  const [form, setForm] = useState<CreateUserRequest>({ ...drawerDefaults });
  const [saving, setSaving] = useState(false);

  const size = 10;

  const fetchUsers = async (p?: number) => {
    try {
      const params: Record<string, string> = { page: String(p ?? page), size: String(size) };
      if (search) params.search = search;
      if (statusFilter) params.status = statusFilter;
      const res = await userApi.getAll(params as any);
      setUsers(res.content);
      setTotalPages(res.totalPages);
      setTotalElements(res.totalElements);
      setPage(res.page);
    } catch { toast.error('Failed to load users'); }
    finally { setLoading(false); }
  };

  const fetchRoles = async () => {
    try {
      const res = await roleApi.getAll();
      if (res.success) setRoles(res.data);
    } catch { toast.error('Failed to load roles'); }
  };

  useEffect(() => { fetchUsers(); fetchRoles(); }, []);

  useEffect(() => {
    fetchUsers(0);
  }, [search, statusFilter]);

  const openCreate = () => {
    setForm({ ...drawerDefaults });
    setEditId(null);
    setDrawerOpen(true);
  };

  const openEdit = (u: UserDetail) => {
    setForm({
      employeeId: u.employeeId,
      firstName: u.firstName,
      lastName: u.lastName,
      email: u.email,
      mobileNumber: u.mobileNumber || '',
      roleIds: u.roles?.map(r => r.id) || [],
    });
    setEditId(u.id);
    setDrawerOpen(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.firstName.trim() || !form.lastName.trim() || !form.email.trim() || !form.employeeId.trim()) {
      toast.error('Name, email, and employee ID are required');
      return;
    }
    setSaving(true);
    try {
      if (editId) {
        const data: UpdateUserRequest = {
          firstName: form.firstName,
          lastName: form.lastName,
          mobileNumber: form.mobileNumber || undefined,
          roleIds: form.roleIds,
        };
        const res = await userApi.update(editId, data);
        if (res.success) { toast.success('User updated'); setDrawerOpen(false); fetchUsers(); }
      } else {
        const res = await userApi.create(form);
        if (res.success) { toast.success('User created'); setDrawerOpen(false); fetchUsers(); }
      }
    } catch {
      toast.error(editId ? 'Failed to update user' : 'Failed to create user');
    } finally { setSaving(false); }
  };

  const handleToggleStatus = async (u: UserDetail) => {
    try {
      if (u.status === 'ACTIVE') {
        const res = await userApi.deactivate(u.id);
        if (res.success) { toast.success('User deactivated'); fetchUsers(); }
      } else {
        const res = await userApi.activate(u.id);
        if (res.success) { toast.success('User activated'); fetchUsers(); }
      }
    } catch { toast.error('Failed to update status'); }
  };

  const handleResetPassword = async (u: UserDetail) => {
    if (!window.confirm(`Reset password for ${u.firstName} ${u.lastName}?`)) return;
    try {
      const res = await userApi.resetPassword(u.id);
      if (res.success) toast.success('Password reset email sent');
    } catch { toast.error('Failed to reset password'); }
  };

  const handleDelete = async (u: UserDetail) => {
    if (!window.confirm(`Delete user "${u.firstName} ${u.lastName}"? This cannot be undone.`)) return;
    try {
      const res = await userApi.delete(u.id);
      if (res.success) { toast.success('User deleted'); fetchUsers(); }
    } catch { toast.error('Failed to delete user'); }
  };

  if (loading) {
    return <div className="flex items-center justify-center min-h-[calc(100vh-10rem)]"><Loader size="lg" /></div>;
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
      <div>
        <h1 className="text-2xl font-bold text-foreground">User Management</h1>
        <p className="text-sm text-muted-foreground mt-1">Manage platform users, roles, and account status</p>
      </div>
      {canEdit && (
        <Button onClick={openCreate} className="gap-2"><Plus className="h-4 w-4" /> New User</Button>
      )}
    </div>

    <div className="flex flex-wrap gap-3 mb-6">
      <div className="relative flex-1 min-w-[200px] max-w-xs">
        <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
        <input type="text" value={search} onChange={e => setSearch(e.target.value)}
          placeholder="Search by name, email, or employee ID..."
          className="w-full rounded-lg border border-input bg-background pl-9 pr-3 py-2 text-sm outline-none focus:border-ring focus:ring-1 focus:ring-ring"
        />
      </div>
      <select value={statusFilter} onChange={e => setStatusFilter(e.target.value)}
        className="rounded-lg border border-input bg-background px-3 py-2 text-sm outline-none focus:border-ring focus:ring-1 focus:ring-ring">
        <option value="">All Status</option>
        <option value="ACTIVE">Active</option>
        <option value="INACTIVE">Inactive</option>
        <option value="SUSPENDED">Suspended</option>
      </select>
      <span className="text-sm text-muted-foreground self-center">{totalElements} users</span>
    </div>

    <div className="rounded-xl border border-border bg-card overflow-hidden">
      <div className="overflow-x-auto">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-border bg-muted/50">
              <th className="text-left px-4 py-3 font-medium text-muted-foreground">Employee</th>
              <th className="text-left px-4 py-3 font-medium text-muted-foreground">Name</th>
              <th className="text-left px-4 py-3 font-medium text-muted-foreground">Email</th>
              <th className="text-left px-4 py-3 font-medium text-muted-foreground">Roles</th>
              <th className="text-left px-4 py-3 font-medium text-muted-foreground">Status</th>
              <th className="text-right px-4 py-3 font-medium text-muted-foreground">Actions</th>
            </tr>
          </thead>
          <tbody>
            {users.length === 0 ? (
              <tr><td colSpan={6} className="px-4 py-12 text-center text-muted-foreground">No users found.</td></tr>
            ) : (
              users.map(u => (
                <tr key={u.id} className="border-b border-border last:border-0 hover:bg-muted/30 transition-colors">
                  <td className="px-4 py-3 text-muted-foreground">{u.employeeId}</td>
                  <td className="px-4 py-3">
                    <span className="font-medium text-foreground">{u.firstName} {u.lastName}</span>
                  </td>
                  <td className="px-4 py-3 text-muted-foreground">{u.email}</td>
                  <td className="px-4 py-3">
                    <div className="flex flex-wrap gap-1">
                      {u.roles?.map(r => (
                        <span key={r.id} className="inline-flex items-center rounded-full bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300 px-2 py-0.5 text-xs font-medium">
                          {r.name.replace('ROLE_', '')}
                        </span>
                      ))}
                    </div>
                  </td>
                  <td className="px-4 py-3">
                    <span className={cn('inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium', statusColors[u.status] || 'bg-gray-100 text-gray-600')}>
                      {u.status}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-right">
                    {canEdit && (
                      <div className="flex items-center justify-end gap-1">
                        <button onClick={() => openEdit(u)}
                          className="flex h-8 w-8 items-center justify-center rounded-lg text-muted-foreground hover:bg-accent hover:text-accent-foreground transition-colors"
                          title="Edit">
                          <Pencil className="h-4 w-4" />
                        </button>
                        <button onClick={() => handleToggleStatus(u)}
                          className="flex h-8 w-8 items-center justify-center rounded-lg text-muted-foreground hover:bg-accent hover:text-accent-foreground transition-colors"
                          title={u.status === 'ACTIVE' ? 'Deactivate' : 'Activate'}>
                          {u.status === 'ACTIVE' ? <ToggleRight className="h-4 w-4" /> : <ToggleLeft className="h-4 w-4" />}
                        </button>
                        <button onClick={() => handleResetPassword(u)}
                          className="flex h-8 w-8 items-center justify-center rounded-lg text-muted-foreground hover:bg-accent hover:text-accent-foreground transition-colors"
                          title="Reset Password">
                          <Key className="h-4 w-4" />
                        </button>
                        <button onClick={() => handleDelete(u)}
                          className="flex h-8 w-8 items-center justify-center rounded-lg text-muted-foreground hover:bg-destructive/10 hover:text-destructive transition-colors"
                          title="Delete">
                          <Trash2 className="h-4 w-4" />
                        </button>
                      </div>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>

    {totalPages > 1 && (
      <div className="flex items-center justify-between mt-4">
        <span className="text-sm text-muted-foreground">Page {page + 1} of {totalPages}</span>
        <div className="flex gap-2">
          <button onClick={() => fetchUsers(page - 1)} disabled={page === 0}
            className="flex h-8 w-8 items-center justify-center rounded-lg border border-input bg-background text-muted-foreground hover:bg-accent hover:text-accent-foreground disabled:opacity-30 disabled:cursor-not-allowed">
            <ChevronLeft className="h-4 w-4" />
          </button>
          <button onClick={() => fetchUsers(page + 1)} disabled={page >= totalPages - 1}
            className="flex h-8 w-8 items-center justify-center rounded-lg border border-input bg-background text-muted-foreground hover:bg-accent hover:text-accent-foreground disabled:opacity-30 disabled:cursor-not-allowed">
            <ChevronRight className="h-4 w-4" />
          </button>
        </div>
      </div>
    )}

    {drawerOpen && (
      <div className="fixed inset-0 z-50 flex justify-end">
        <div className="fixed inset-0 bg-black/40" onClick={() => setDrawerOpen(false)} />
        <div className="relative w-full max-w-lg bg-background border-l border-border shadow-xl overflow-y-auto">
          <div className="flex items-center justify-between px-6 py-4 border-b border-border">
            <h2 className="text-lg font-semibold text-foreground">{editId ? 'Edit User' : 'New User'}</h2>
            <button onClick={() => setDrawerOpen(false)} className="text-muted-foreground hover:text-foreground text-xl">&times;</button>
          </div>
          <form onSubmit={handleSubmit} className="p-6 space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-foreground mb-1">Employee ID</label>
                <input type="text" value={form.employeeId} onChange={e => setForm({ ...form, employeeId: e.target.value })}
                  placeholder="EMP001" disabled={!!editId}
                  className="w-full rounded-lg border border-input bg-background px-3 py-2 text-sm outline-none focus:border-ring focus:ring-1 focus:ring-ring disabled:opacity-50" />
              </div>
              <div />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-foreground mb-1">First Name</label>
                <input type="text" value={form.firstName} onChange={e => setForm({ ...form, firstName: e.target.value })}
                  className="w-full rounded-lg border border-input bg-background px-3 py-2 text-sm outline-none focus:border-ring focus:ring-1 focus:ring-ring" />
              </div>
              <div>
                <label className="block text-sm font-medium text-foreground mb-1">Last Name</label>
                <input type="text" value={form.lastName} onChange={e => setForm({ ...form, lastName: e.target.value })}
                  className="w-full rounded-lg border border-input bg-background px-3 py-2 text-sm outline-none focus:border-ring focus:ring-1 focus:ring-ring" />
              </div>
            </div>
            <div>
              <label className="block text-sm font-medium text-foreground mb-1">Email</label>
              <input type="email" value={form.email} onChange={e => setForm({ ...form, email: e.target.value })}
                disabled={!!editId}
                className="w-full rounded-lg border border-input bg-background px-3 py-2 text-sm outline-none focus:border-ring focus:ring-1 focus:ring-ring disabled:opacity-50" />
            </div>
            <div>
              <label className="block text-sm font-medium text-foreground mb-1">Mobile Number</label>
              <input type="text" value={form.mobileNumber || ''} onChange={e => setForm({ ...form, mobileNumber: e.target.value })}
                placeholder="Optional"
                className="w-full rounded-lg border border-input bg-background px-3 py-2 text-sm outline-none focus:border-ring focus:ring-1 focus:ring-ring" />
            </div>
            <div>
              <label className="block text-sm font-medium text-foreground mb-1">Roles</label>
              <div className="space-y-2 max-h-48 overflow-y-auto rounded-lg border border-input bg-background p-3">
                {roles.map(role => (
                  <label key={role.id} className="flex items-center gap-2 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={form.roleIds.includes(role.id)}
                      onChange={e => {
                        if (e.target.checked) setForm({ ...form, roleIds: [...form.roleIds, role.id] });
                        else setForm({ ...form, roleIds: form.roleIds.filter(id => id !== role.id) });
                      }}
                      className="rounded border-input"
                    />
                    <span className="text-sm text-foreground">{role.name.replace('ROLE_', '')}</span>
                  </label>
                ))}
                {roles.length === 0 && <p className="text-sm text-muted-foreground">No roles available</p>}
              </div>
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
