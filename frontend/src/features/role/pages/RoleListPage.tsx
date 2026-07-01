import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Plus, Pencil, Trash2 } from 'lucide-react';
import toast from 'react-hot-toast';
import { roleApi } from '@/features/role/api/role.api';
import type { Role } from '@/features/auth/types/auth.types';
import { Loader } from '@/components/ui/Loader';
import { Button } from '@/components/ui/Button';

export const RoleListPage: React.FC = () => {
  const [roles, setRoles] = useState<Role[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchRoles = async () => {
    try {
      const res = await roleApi.getAll();
      if (res.success) setRoles(res.data);
    } catch {
      toast.error('Failed to load roles');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRoles();
  }, []);

  const handleDelete = async (id: number, name: string) => {
    if (!window.confirm(`Delete role "${name}"?`)) return;
    try {
      const res = await roleApi.delete(id);
      if (res.success) {
        toast.success('Role deleted');
        fetchRoles();
      }
    } catch {
      toast.error('Failed to delete role');
    }
  };

  if (loading) {
    return <div className="flex items-center justify-center min-h-[calc(100vh-10rem)]"><Loader size="lg" /></div>;
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
      <div>
        <h1 className="text-2xl font-bold text-foreground">Roles</h1>
        <p className="text-sm text-muted-foreground mt-1">Manage roles</p>
      </div>
      <Link to="/admin/roles/new">
        <Button className="gap-2">
          <Plus className="h-4 w-4" /> New Role
        </Button>
      </Link>
    </div>

    <div className="rounded-xl border border-border bg-card overflow-hidden">
      <div className="overflow-x-auto">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-border bg-muted/50">
              <th className="text-left px-4 py-3 font-medium text-muted-foreground">Name</th>
              <th className="text-left px-4 py-3 font-medium text-muted-foreground">Description</th>
              <th className="text-right px-4 py-3 font-medium text-muted-foreground">Actions</th>
            </tr>
          </thead>
          <tbody>
            {roles.length === 0 ? (
              <tr>
                <td colSpan={3} className="px-4 py-12 text-center text-muted-foreground">
                  No roles yet. Create your first role.
                </td>
              </tr>
            ) : (
              roles.map((role) => (
                <tr key={role.id} className="border-b border-border last:border-0 hover:bg-muted/30 transition-colors">
                  <td className="px-4 py-3">
                    <span className="font-medium text-foreground">{role.name}</span>
                  </td>
                  <td className="px-4 py-3 text-muted-foreground">{role.description || '-'}</td>
                  <td className="px-4 py-3 text-right">
                    <div className="flex items-center justify-end gap-1">
                      <Link
                        to={`/admin/roles/${role.id}`}
                        className="flex h-8 w-8 items-center justify-center rounded-lg text-muted-foreground hover:bg-accent hover:text-accent-foreground transition-colors"
                      >
                        <Pencil className="h-4 w-4" />
                      </Link>
                      <button
                        onClick={() => handleDelete(role.id, role.name)}
                        className="flex h-8 w-8 items-center justify-center rounded-lg text-muted-foreground hover:bg-destructive/10 hover:text-destructive transition-colors"
                      >
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
    </div>
  );
};
