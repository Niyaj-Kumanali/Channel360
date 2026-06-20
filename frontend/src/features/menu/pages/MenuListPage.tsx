import React, { useEffect, useState } from 'react';
import { Plus, Pencil, Trash2, Save, X, ChevronDown } from 'lucide-react';
import toast from 'react-hot-toast';
import { menuApi } from '@/features/menu/api/menu.api';
import { roleApi } from '@/features/role/api/role.api';
import type { MenuItemResponse, Role } from '@/features/auth/types/auth.types';
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
}

const emptyForm: MenuFormData = {
  label: '',
  path: '',
  icon: '',
  parentId: '',
  displayOrder: 0,
  active: true,
};

export const MenuListPage: React.FC = () => {
  const [roles, setRoles] = useState<Role[]>([]);
  const [menuItems, setMenuItems] = useState<MenuItemResponse[]>([]);
  const [selectedRoleId, setSelectedRoleId] = useState<number | null>(null);
  const [checkedIds, setCheckedIds] = useState<number[]>([]);
  const [saving, setSaving] = useState(false);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState<MenuFormData>(emptyForm);

  const fetchAll = async () => {
    try {
      const [roleRes, menuRes] = await Promise.all([
        roleApi.getAll().catch(() => null),
        menuApi.getAll().catch(() => null),
      ]);
      if (roleRes?.success) {
        setRoles(roleRes.data);
        if (!selectedRoleId && roleRes.data.length > 0) {
          setSelectedRoleId(roleRes.data[0].id);
        }
      }
      if (menuRes?.success) setMenuItems(menuRes.data);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAll();
  }, []);

  useEffect(() => {
    if (!selectedRoleId) return;
    roleApi.getRoleMenuItems(selectedRoleId).then(res => {
      if (res.success) setCheckedIds(res.data);
    }).catch(() => {});
  }, [selectedRoleId]);

  const saveVisibility = async () => {
    if (!selectedRoleId) return;
    setSaving(true);
    try {
      const res = await roleApi.setRoleMenuItems(selectedRoleId, checkedIds);
      if (res.success) toast.success('Menu visibility saved');
    } catch {
      toast.error('Failed to save menu visibility');
    } finally {
      setSaving(false);
    }
  };

  const toggleItem = (itemId: number, childrenIds: number[]) => {
    setCheckedIds(prev => {
      const isChecked = prev.includes(itemId);
      if (isChecked) {
        return prev.filter(id => id !== itemId && !childrenIds.includes(id));
      }
      return [...new Set([...prev, itemId, ...childrenIds])];
    });
  };

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
      await fetchAll();
      if (selectedRoleId) {
        const roleRes = await roleApi.getRoleMenuItems(selectedRoleId);
        if (roleRes.success) setCheckedIds(roleRes.data);
      }
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
        await fetchAll();
        if (selectedRoleId) {
          const roleRes = await roleApi.getRoleMenuItems(selectedRoleId);
          if (roleRes.success) setCheckedIds(roleRes.data);
        }
      }
    } catch {
      toast.error('Failed to delete menu item');
    }
  };

  const topLevelItems = menuItems.filter(m => m.parentId == null);
  const childItems = (parentId: number) => menuItems.filter(m => m.parentId === parentId);

  if (loading) {
    return <div className="flex items-center justify-center min-h-[calc(100vh-10rem)]"><Loader size="lg" /></div>;
  }

  return (
    <div className="mx-auto max-w-4xl">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-foreground">Menu Management</h1>
          <p className="text-sm text-muted-foreground mt-1">
            Select a role to configure which menu items it can see
          </p>
        </div>
        <Button onClick={openAddModal} className="gap-2">
          <Plus className="h-4 w-4" /> Add Menu Item
        </Button>
      </div>

      {/* Role tabs */}
      <div className="flex gap-2 mb-6 flex-wrap">
        {roles.map(role => (
          <button
            key={role.id}
            type="button"
            onClick={() => setSelectedRoleId(role.id)}
            className={cn(
              'rounded-lg px-4 py-2 text-sm font-medium transition-colors',
              selectedRoleId === role.id
                ? 'bg-primary text-primary-foreground'
                : 'bg-card border border-border text-muted-foreground hover:bg-accent hover:text-accent-foreground'
            )}
          >
            {role.name}
          </button>
        ))}
      </div>

      {/* Menu tree */}
      {selectedRoleId && (
        <div className="rounded-xl border border-border bg-card p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-sm font-semibold text-foreground">
              Menu Items for {roles.find(r => r.id === selectedRoleId)?.name}
            </h2>
            <span className="text-xs text-muted-foreground">
              {checkedIds.length} / {menuItems.length} selected
            </span>
          </div>

          {topLevelItems.length === 0 ? (
            <p className="text-sm text-muted-foreground py-8 text-center">
              No menu items yet. Click "Add Menu Item" to create one.
            </p>
          ) : (
            <div className="space-y-1">
              {topLevelItems.map(item => {
                const children = childItems(item.id);
                const hasChildren = children.length > 0;
                const childrenIds = children.map(c => c.id);
                const isChecked = checkedIds.includes(item.id);

                return (
                  <div key={item.id}>
                    <div
                      className={cn(
                        'flex items-center gap-2 rounded-lg border px-3 py-2.5 transition-colors',
                        isChecked ? 'border-primary bg-primary/5' : 'border-border'
                      )}
                    >
                      <input
                        type="checkbox"
                        checked={isChecked}
                        onChange={() => toggleItem(item.id, childrenIds)}
                        className="h-4 w-4 rounded border-border text-primary focus:ring-primary"
                      />
                      <span className="flex-1 text-sm font-medium text-foreground">{item.label}</span>
                      <span className="text-xs text-muted-foreground hidden sm:inline">{item.icon}</span>
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
                            className={cn(
                              'flex items-center gap-2 rounded-lg border px-3 py-2 transition-colors',
                              checkedIds.includes(child.id) ? 'border-primary bg-primary/5' : 'border-border'
                            )}
                          >
                            <input
                              type="checkbox"
                              checked={checkedIds.includes(child.id)}
                              onChange={() => toggleItem(child.id, [])}
                              className="h-4 w-4 rounded border-border text-primary focus:ring-primary"
                            />
                            <span className="flex-1 text-sm text-foreground">{child.label}</span>
                            <span className="text-xs text-muted-foreground hidden sm:inline">{child.icon}</span>
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

          <div className="mt-6 flex justify-end">
            <Button onClick={saveVisibility} isLoading={saving} className="gap-2">
              <Save className="h-4 w-4" /> Save Visibility
            </Button>
          </div>
        </div>
      )}

      {/* Add/Edit Modal */}
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
