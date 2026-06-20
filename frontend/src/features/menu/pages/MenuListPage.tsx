import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Plus, Pencil, Trash2 } from 'lucide-react';
import toast from 'react-hot-toast';
import { menuApi } from '@/features/menu/api/menu.api';
import type { MenuItemResponse } from '@/features/auth/types/auth.types';
import { Button } from '@/components/ui/Button';
import { Loader } from '@/components/ui/Loader';

export const MenuListPage: React.FC = () => {
  const [items, setItems] = useState<MenuItemResponse[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchItems = async () => {
    try {
      const res = await menuApi.getAll();
      if (res.success) setItems(res.data);
    } catch {
      toast.error('Failed to load menu items');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchItems();
  }, []);

  const handleDelete = async (id: number, label: string) => {
    if (!window.confirm(`Delete menu item "${label}"?`)) return;
    try {
      const res = await menuApi.delete(id);
      if (res.success) {
        toast.success('Menu item deleted');
        fetchItems();
      }
    } catch {
      toast.error('Failed to delete menu item');
    }
  };

  const getParentLabel = (parentId: number | null): string => {
    if (parentId === null) return '-';
    const parent = items.find(i => i.id === parentId);
    return parent ? parent.label : '-';
  };

  if (loading) {
    return <div className="flex items-center justify-center min-h-[calc(100vh-10rem)]"><Loader size="lg" /></div>;
  }

  return (
    <div className="mx-auto max-w-6xl">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-foreground">Menu Items</h1>
          <p className="text-sm text-muted-foreground mt-1">Manage sidebar navigation menu items</p>
        </div>
        <Link to="/admin/menu/new">
          <Button className="gap-2">
            <Plus className="h-4 w-4" /> New Menu Item
          </Button>
        </Link>
      </div>

      <div className="rounded-xl border border-border bg-card overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-border bg-muted/50">
                <th className="text-left px-4 py-3 font-medium text-muted-foreground">Label</th>
                <th className="text-left px-4 py-3 font-medium text-muted-foreground">Path</th>
                <th className="text-center px-4 py-3 font-medium text-muted-foreground">Icon</th>
                <th className="text-center px-4 py-3 font-medium text-muted-foreground">Permission</th>
                <th className="text-center px-4 py-3 font-medium text-muted-foreground">Parent</th>
                <th className="text-center px-4 py-3 font-medium text-muted-foreground">Order</th>
                <th className="text-center px-4 py-3 font-medium text-muted-foreground">Status</th>
                <th className="text-right px-4 py-3 font-medium text-muted-foreground">Actions</th>
              </tr>
            </thead>
            <tbody>
              {items.length === 0 ? (
                <tr>
                  <td colSpan={8} className="px-4 py-12 text-center text-muted-foreground">
                    No menu items yet. Create your first menu item.
                  </td>
                </tr>
              ) : (
                items.map((item) => (
                  <tr key={item.id} className="border-b border-border last:border-0 hover:bg-muted/30 transition-colors">
                    <td className="px-4 py-3">
                      <div className="font-medium text-foreground">{item.label}</div>
                    </td>
                    <td className="px-4 py-3 text-muted-foreground max-w-[200px] truncate">{item.path}</td>
                    <td className="px-4 py-3 text-center text-muted-foreground">{item.icon || '-'}</td>
                    <td className="px-4 py-3 text-center">
                      {item.permissionName ? (
                        <span className="inline-flex items-center rounded-full bg-primary/10 px-2 py-0.5 text-xs font-medium text-primary">
                          {item.permissionName}
                        </span>
                      ) : (
                        <span className="text-muted-foreground text-xs">Public</span>
                      )}
                    </td>
                    <td className="px-4 py-3 text-center text-muted-foreground">{getParentLabel(item.parentId)}</td>
                    <td className="px-4 py-3 text-center text-muted-foreground">{item.displayOrder}</td>
                    <td className="px-4 py-3 text-center">
                      <span className={`inline-flex items-center gap-1 rounded-full px-2 py-0.5 text-xs font-medium ${
                        item.active ? 'bg-green-500/10 text-green-600' : 'bg-gray-500/10 text-muted-foreground'
                      }`}>
                        {item.active ? 'Active' : 'Inactive'}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-right">
                      <div className="flex items-center justify-end gap-1">
                        <Link
                          to={`/admin/menu/${item.id}`}
                          className="flex h-8 w-8 items-center justify-center rounded-lg text-muted-foreground hover:bg-accent hover:text-accent-foreground transition-colors"
                        >
                          <Pencil className="h-4 w-4" />
                        </Link>
                        <button
                          onClick={() => handleDelete(item.id, item.label)}
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
