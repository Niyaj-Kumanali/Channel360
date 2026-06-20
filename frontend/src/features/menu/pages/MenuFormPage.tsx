import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Save } from 'lucide-react';
import toast from 'react-hot-toast';
import { menuApi } from '@/features/menu/api/menu.api';
import { roleApi } from '@/features/role/api/role.api';
import type { Permission } from '@/features/auth/types/auth.types';
import type { MenuItemResponse } from '@/features/auth/types/auth.types';
import { Loader } from '@/components/ui/Loader';
import { Button } from '@/components/ui/Button';

export const MenuFormPage: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = id && id !== 'new';

  const [label, setLabel] = useState('');
  const [path, setPath] = useState('');
  const [icon, setIcon] = useState('');
  const [permissionName, setPermissionName] = useState('');
  const [parentId, setParentId] = useState<number | ''>('');
  const [displayOrder, setDisplayOrder] = useState(0);
  const [active, setActive] = useState(true);

  const [allPermissions, setAllPermissions] = useState<Permission[]>([]);
  const [parentItems, setParentItems] = useState<MenuItemResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    const init = async () => {
      try {
        const [permRes, menuRes] = await Promise.all([
          roleApi.getAllPermissions(),
          menuApi.getAll(),
        ]);

        if (permRes.success) setAllPermissions(permRes.data);
        if (menuRes.success) setParentItems(menuRes.data.filter(m => m.parentId === null && (!isEdit || m.id !== Number(id))));

        if (isEdit && id) {
          const itemRes = await menuApi.getById(Number(id));
          if (itemRes.success) {
            const item = itemRes.data;
            setLabel(item.label);
            setPath(item.path);
            setIcon(item.icon || '');
            setPermissionName(item.permissionName || '');
            setParentId(item.parentId ?? '');
            setDisplayOrder(item.displayOrder);
            setActive(item.active);
          } else {
            toast.error('Failed to load menu item');
            navigate('/admin/menu');
          }
        }
      } catch {
        toast.error('Failed to load data');
        navigate('/admin/menu');
      } finally {
        setLoading(false);
      }
    };

    init();
  }, [id]);

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!label.trim()) {
      toast.error('Label is required');
      return;
    }
    if (!path.trim()) {
      toast.error('Path is required');
      return;
    }

    setSubmitting(true);
    try {
      const payload: Partial<MenuItemResponse> = {
        label: label.trim(),
        path: path.trim(),
        permissionName: permissionName || null,
        parentId: parentId === '' ? null : parentId,
        displayOrder,
        active,
      };
      if (icon.trim()) payload.icon = icon.trim();

      if (isEdit && id) {
        const res = await menuApi.update(Number(id), payload);
        if (res.success) toast.success('Menu item updated');
      } else {
        const res = await menuApi.create(payload);
        if (res.success) toast.success('Menu item created');
      }
      navigate('/admin/menu');
    } catch {
      toast.error('Failed to save menu item');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return <div className="flex items-center justify-center min-h-[calc(100vh-10rem)]"><Loader size="lg" /></div>;
  }

  return (
    <div className="mx-auto max-w-3xl">
      <div className="flex items-center gap-4 mb-6">
        <button
          onClick={() => navigate('/admin/menu')}
          className="flex h-9 w-9 items-center justify-center rounded-lg text-muted-foreground hover:bg-accent hover:text-accent-foreground transition-colors"
        >
          <ArrowLeft className="h-4 w-4" />
        </button>
        <div>
          <h1 className="text-2xl font-bold text-foreground">{isEdit ? 'Edit Menu Item' : 'New Menu Item'}</h1>
          <p className="text-sm text-muted-foreground mt-1">
            {isEdit ? 'Update the menu item details below' : 'Create a new sidebar navigation menu item'}
          </p>
        </div>
      </div>

      <form onSubmit={onSubmit} className="space-y-6" noValidate>
        <div className="rounded-xl border border-border bg-card p-6 space-y-5">
          <div className="grid gap-5 sm:grid-cols-2">
            <div className="space-y-1.5">
              <label className="text-sm font-medium text-foreground">
                Label <span className="text-red-500">*</span>
              </label>
              <input
                value={label}
                onChange={(e) => setLabel(e.target.value)}
                className="flex h-10 w-full rounded-lg border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
                placeholder="e.g. Dashboard"
              />
              <p className="text-xs text-muted-foreground">Display text shown in the sidebar.</p>
            </div>
            <div className="space-y-1.5">
              <label className="text-sm font-medium text-foreground">
                Path <span className="text-red-500">*</span>
              </label>
              <input
                value={path}
                onChange={(e) => setPath(e.target.value)}
                className="flex h-10 w-full rounded-lg border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
                placeholder="e.g. /dashboard or # for parent groups"
              />
              <p className="text-xs text-muted-foreground">Route path. Use '#' for group headers with children.</p>
            </div>
          </div>

          <div className="grid gap-5 sm:grid-cols-2">
            <div className="space-y-1.5">
              <label className="text-sm font-medium text-foreground">Icon Name</label>
              <input
                value={icon}
                onChange={(e) => setIcon(e.target.value)}
                className="flex h-10 w-full rounded-lg border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
                placeholder="e.g. LayoutDashboard, Shield, Users"
              />
              <p className="text-xs text-muted-foreground">Lucide icon name (PascalCase). Must match the sidebar icon map.</p>
            </div>
            <div className="space-y-1.5">
              <label className="text-sm font-medium text-foreground">Display Order</label>
              <input
                type="number"
                value={displayOrder}
                onChange={(e) => setDisplayOrder(Number(e.target.value))}
                className="flex h-10 w-full rounded-lg border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
              />
              <p className="text-xs text-muted-foreground">Lower numbers appear first in the sidebar.</p>
            </div>
          </div>

          <div className="grid gap-5 sm:grid-cols-2">
            <div className="space-y-1.5">
              <label className="text-sm font-medium text-foreground">Required Permission</label>
              <select
                value={permissionName}
                onChange={(e) => setPermissionName(e.target.value)}
                className="flex h-10 w-full rounded-lg border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
              >
                <option value="">-- Public (no permission required) --</option>
                {allPermissions.map((p) => (
                  <option key={p.id} value={p.name}>{p.name} - {p.description}</option>
                ))}
              </select>
              <p className="text-xs text-muted-foreground">Users need this permission to see the menu item. Leave empty for public items.</p>
            </div>
            <div className="space-y-1.5">
              <label className="text-sm font-medium text-foreground">Parent Item</label>
              <select
                value={parentId}
                onChange={(e) => setParentId(e.target.value === '' ? '' : Number(e.target.value))}
                className="flex h-10 w-full rounded-lg border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
              >
                <option value="">-- Top Level --</option>
                {parentItems.map((p) => (
                  <option key={p.id} value={p.id}>{p.label}</option>
                ))}
              </select>
              <p className="text-xs text-muted-foreground">Make this item a child of another menu item. Leave empty for top-level items.</p>
            </div>
          </div>

          <label className="flex items-center gap-3 cursor-pointer">
            <input
              type="checkbox"
              checked={active}
              onChange={(e) => setActive(e.target.checked)}
              className="h-4 w-4 rounded border-border text-primary focus:ring-primary"
            />
            <div>
              <span className="text-sm font-medium text-foreground">Active</span>
              <p className="text-xs text-muted-foreground">Menu item appears in the sidebar</p>
            </div>
          </label>
        </div>

        <div className="flex items-center justify-end gap-3">
          <Button type="button" variant="outline" onClick={() => navigate('/admin/menu')}>
            Cancel
          </Button>
          <Button type="submit" isLoading={submitting} className="gap-2">
            <Save className="h-4 w-4" /> {isEdit ? 'Update' : 'Create'} Menu Item
          </Button>
        </div>
      </form>
    </div>
  );
};
