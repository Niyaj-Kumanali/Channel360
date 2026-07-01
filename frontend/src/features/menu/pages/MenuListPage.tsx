import React, { useEffect, useState } from 'react';
import { Plus, Pencil, Trash2, Save, X, ChevronDown } from 'lucide-react';
import toast from 'react-hot-toast';
import { menuApi } from '@/features/menu/api/menu.api';
import { roleApi } from '@/features/role/api/role.api';
import type { MenuItemResponse, Permission } from '@/features/auth/types/auth.types';
import { Button } from '@/components/ui/Button';
import { Loader } from '@/components/ui/Loader';
import { Input } from '@/components/ui/Input';
import { SelectField } from '@/components/ui/SelectField';
import { iconMap, ICON_NAMES } from '@/lib/icon-map';
import { cn } from '@/lib/utils';

const iconOptions = ICON_NAMES.map(name => ({
  value: name,
  label: name,
  icon: React.createElement(iconMap[name], { className: 'h-4 w-4', key: name }),
}));

interface MenuFormData {
  id?: number;
  label: string;
  path: string;
  icon: string;
  parentId: number | '';
  displayOrder: number;
  active: boolean;
  permissionName: string;
}

const emptyForm: MenuFormData = {
  label: '',
  path: '',
  icon: '',
  parentId: '',
  displayOrder: 0,
  active: true,
  permissionName: '',
};

export const MenuListPage: React.FC = () => {
  const [menuItems, setMenuItems] = useState<MenuItemResponse[]>([]);
  const [permissions, setPermissions] = useState<Permission[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState<MenuFormData>(emptyForm);

  const fetchData = async () => {
    try {
      const [menuRes, permRes] = await Promise.all([
        menuApi.getAll().catch(() => null),
        roleApi.getAllPermissions().catch(() => null),
      ]);
      if (menuRes?.success) setMenuItems(menuRes.data);
      if (permRes?.success) setPermissions(permRes.data);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchData(); }, []);

  const openAddModal = () => {
    setFormData(emptyForm);
    setShowModal(true);
  };

  const openEditModal = (item: MenuItemResponse) => {
    setFormData({
      id: item.id,
      label: item.label,
      path: item.path,
      icon: item.icon || '',
      parentId: item.parentId ?? '',
      displayOrder: item.displayOrder,
      active: item.active,
      permissionName: item.permissionName || '',
    });
    setShowModal(true);
  };

  const saveMenuItem = async () => {
    if (!formData.label.trim()) { toast.error('Label is required'); return; }
    if (!formData.path.trim()) { toast.error('Path is required'); return; }

    const payload: Partial<MenuItemResponse> = {
      label: formData.label.trim(),
      path: formData.path.trim(),
      parentId: formData.parentId === '' ? null : formData.parentId,
      displayOrder: formData.displayOrder,
      active: formData.active,
    };
    if (formData.icon.trim()) payload.icon = formData.icon.trim();
    if (formData.permissionName.trim()) payload.permissionName = formData.permissionName.trim();

    try {
      if (formData.id) {
        const res = await menuApi.update(formData.id, payload);
        if (!res.success) { toast.error('Failed to update'); return; }
        toast.success('Menu item updated');
      } else {
        const res = await menuApi.create(payload);
        if (!res.success) { toast.error('Failed to create'); return; }
        toast.success('Menu item created');
      }
      setShowModal(false);
      await fetchData();
    } catch {
      toast.error('Failed to save menu item');
    }
  };

  const deleteItem = async (id: number, label: string) => {
    if (!window.confirm(`Delete menu item "${label}"?`)) return;
    try {
      const res = await menuApi.delete(id);
      if (res.success) {
        toast.success('Menu item deleted');
        await fetchData();
      }
    } catch {
      toast.error('Failed to delete menu item');
    }
  };

  const topLevelItems = menuItems.filter(m => m.parentId == null);
  const childItems = (parentId: number) => menuItems.filter(m => m.parentId === parentId);
  const permissionOptions = permissions.map(p => ({ value: p.name, label: `${p.name} (${p.module})` }));

  if (loading) {
    return <div className="flex items-center justify-center min-h-[calc(100vh-10rem)]"><Loader size="lg" /></div>;
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
      <div>
        <h1 className="text-2xl font-bold text-foreground">Menu Structure</h1>
        <p className="text-sm text-muted-foreground mt-1">
          Manage the master sidebar menu structure. This defines what menu items exist — <strong>not</strong> who can see them.
        </p>
        <p className="text-xs text-muted-foreground mt-1.5 bg-muted/50 rounded-lg px-3 py-2 border border-border max-w-xl">
          <strong>Instructions:</strong> Create parent items (e.g. Content) and nest child items under them. The <strong>Required Permission</strong> field links a menu item to a permission — roles that have that permission will see the menu. Parent menus auto-appear when any child has a granted permission. Leave it blank for always-visible items. Role-to-permission assignment is done on the <strong>Role Management</strong> page.
        </p>
      </div>
      <Button onClick={openAddModal} className="gap-2">
        <Plus className="h-4 w-4" /> Add Menu Item
      </Button>
    </div>

    <div className="rounded-xl border border-border bg-card p-6">
      {topLevelItems.length === 0 ? (
        <p className="text-sm text-muted-foreground py-8 text-center">
          No menu items yet. Click "Add Menu Item" to create one.
        </p>
      ) : (
        <div className="space-y-1">
          {topLevelItems.map(item => {
            const children = childItems(item.id);
            const hasChildren = children.length > 0;

            return (
              <div key={item.id}>
                <div className="flex items-center gap-2 rounded-lg border border-border px-3 py-2.5 transition-colors">
                  <span className="flex-1 text-sm font-medium text-foreground">{item.label}</span>
                  <span className="text-xs text-muted-foreground hidden sm:inline">{item.path}</span>
                  <span className="text-xs text-muted-foreground hidden md:inline">{item.icon}</span>
                  {item.permissionName && (
                    <span className="text-xs rounded-full bg-primary/10 px-2 py-0.5 text-primary">{item.permissionName}</span>
                  )}
                  <button
                    type="button"
                    onClick={() => openEditModal(item)}
                    className="flex h-7 w-7 items-center justify-center rounded-md text-muted-foreground hover:bg-accent hover:text-accent-foreground transition-colors"
                  >
                    <Pencil className="h-3.5 w-3.5" />
                  </button>
                  <button
                    type="button"
                    onClick={() => deleteItem(item.id, item.label)}
                    className="flex h-7 w-7 items-center justify-center rounded-md text-muted-foreground hover:bg-destructive/10 hover:text-destructive transition-colors"
                  >
                    <Trash2 className="h-3.5 w-3.5" />
                  </button>
                </div>
                {hasChildren && (
                  <div className="ml-6 mt-1 space-y-1 pl-4 border-l border-border">
                    {children.map(child => (
                      <div
                        key={child.id}
                        className="flex items-center gap-2 rounded-lg border border-border px-3 py-2 transition-colors"
                      >
                        <span className="flex-1 text-sm text-foreground">{child.label}</span>
                        <span className="text-xs text-muted-foreground hidden sm:inline">{child.path}</span>
                        <span className="text-xs text-muted-foreground hidden md:inline">{child.icon}</span>
                        {child.permissionName && (
                          <span className="text-xs rounded-full bg-primary/10 px-2 py-0.5 text-primary">{child.permissionName}</span>
                        )}
                        <button
                          type="button"
                          onClick={() => openEditModal(child)}
                          className="flex h-7 w-7 items-center justify-center rounded-md text-muted-foreground hover:bg-accent hover:text-accent-foreground transition-colors"
                        >
                          <Pencil className="h-3.5 w-3.5" />
                        </button>
                        <button
                          type="button"
                          onClick={() => deleteItem(child.id, child.label)}
                          className="flex h-7 w-7 items-center justify-center rounded-md text-muted-foreground hover:bg-destructive/10 hover:text-destructive transition-colors"
                        >
                          <Trash2 className="h-3.5 w-3.5" />
                        </button>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            );
          })}
        </div>
      )}
    </div>

    {showModal && (
      <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40" onClick={() => setShowModal(false)}>
        <div className="w-full max-w-lg rounded-xl border border-border bg-card p-6 shadow-lg" onClick={e => e.stopPropagation()}>
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-semibold text-foreground">
              {formData.id ? 'Edit Menu Item' : 'New Menu Item'}
            </h3>
            <button
              type="button"
              onClick={() => setShowModal(false)}
              className="flex h-8 w-8 items-center justify-center rounded-lg text-muted-foreground hover:bg-accent hover:text-accent-foreground transition-colors"
            >
              <X className="h-4 w-4" />
            </button>
          </div>
          <div className="space-y-4">
            <div className="grid gap-4 sm:grid-cols-2">
              <Input
                id="modal-label"
                label="Label *"
                value={formData.label}
                onChange={e => setFormData(prev => ({ ...prev, label: e.target.value }))}
                placeholder="e.g. Dashboard"
              />
              <Input
                id="modal-path"
                label="Path *"
                value={formData.path}
                onChange={e => setFormData(prev => ({ ...prev, path: e.target.value }))}
                placeholder="e.g. /dashboard"
              />
            </div>
            <div className="grid gap-4 sm:grid-cols-2">
              <SelectField
                id="modal-icon"
                label="Icon"
                value={formData.icon}
                onChange={v => setFormData(prev => ({ ...prev, icon: v }))}
                placeholder="-- No icon --"
                options={iconOptions}
              />
              <Input
                id="modal-order"
                label="Display Order"
                type="number"
                value={formData.displayOrder}
                onChange={e => setFormData(prev => ({ ...prev, displayOrder: Number(e.target.value) }))}
              />
            </div>
            <SelectField
              id="modal-parent"
              label="Parent Item"
              value={String(formData.parentId)}
              onChange={v => setFormData(prev => ({ ...prev, parentId: v === '' ? '' : Number(v) }))}
              placeholder="-- Top Level --"
              options={menuItems
                .filter(m => m.parentId == null && m.id !== formData.id)
                .map(m => ({ value: String(m.id), label: m.label }))}
            />
            <SelectField
              id="modal-permission"
              label="Required Permission"
              value={formData.permissionName}
              onChange={v => setFormData(prev => ({ ...prev, permissionName: v }))}
              placeholder="-- No permission --"
              options={permissionOptions}
            />
            <label className="flex items-center gap-3 cursor-pointer">
              <input
                type="checkbox"
                checked={formData.active}
                onChange={e => setFormData(prev => ({ ...prev, active: e.target.checked }))}
                className="h-4 w-4 rounded border-border text-primary focus:ring-primary"
              />
              <span className="text-sm font-medium text-foreground">Active</span>
            </label>
          </div>
          <div className="flex items-center justify-end gap-3 mt-6">
            <Button type="button" variant="outline" onClick={() => setShowModal(false)}>
              Cancel
            </Button>
            <Button type="button" onClick={saveMenuItem} className="gap-2">
              <Save className="h-4 w-4" /> {formData.id ? 'Update' : 'Create'}
            </Button>
          </div>
        </div>
      </div>
    )}
    </div>
  );
};
