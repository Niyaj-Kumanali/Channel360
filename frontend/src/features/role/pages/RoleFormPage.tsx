import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Save, ChevronDown } from 'lucide-react';
import toast from 'react-hot-toast';
import { roleApi } from '@/features/role/api/role.api';
import { menuApi } from '@/features/menu/api/menu.api';
import type { Permission, MenuItemResponse } from '@/features/auth/types/auth.types';
import { Loader } from '@/components/ui/Loader';
import { Button } from '@/components/ui/Button';
import { cn } from '@/lib/utils';

export const RoleFormPage: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = id && id !== 'new';

  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [selectedIds, setSelectedIds] = useState<number[]>([]);
  const [allPermissions, setAllPermissions] = useState<Permission[]>([]);
  const [menuItems, setMenuItems] = useState<MenuItemResponse[]>([]);
  const [selectedMenuItemIds, setSelectedMenuItemIds] = useState<number[]>([]);
  const [expanded, setExpanded] = useState<Record<string, boolean>>({});
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  const loadMenuItems = async (roleId?: number) => {
    try {
      const [menuRes, roleMenuRes] = await Promise.all([
        menuApi.getAll(),
        roleId ? roleApi.getRoleMenuItems(roleId) : Promise.resolve(null),
      ]);
      if (menuRes.success) setMenuItems(menuRes.data);
      if (roleMenuRes?.success) setSelectedMenuItemIds(roleMenuRes.data);
    } catch {
      // Menu data is non-critical; form can still work
    }
  };

  useEffect(() => {
    const init = async () => {
      try {
        const [permRes] = await Promise.all([
          roleApi.getAllPermissions(),
          isEdit ? roleApi.getById(Number(id)) : Promise.resolve(null),
        ]);

        if (permRes.success) {
          setAllPermissions(permRes.data);
        }

        if (isEdit) {
          const roleRes = await roleApi.getById(Number(id));
          if (roleRes.success) {
            setName(roleRes.data.name);
            setDescription(roleRes.data.description || '');
            setSelectedIds(roleRes.data.permissionIds);
            await loadMenuItems(Number(id));
          } else {
            toast.error('Failed to load role');
            navigate('/admin/roles');
          }
        } else {
          await loadMenuItems();
        }
      } catch {
        toast.error('Failed to load data');
        navigate('/admin/roles');
      } finally {
        setLoading(false);
      }
    };

    init();
  }, [id]);

  const togglePermission = (permId: number) => {
    setSelectedIds((prev) =>
      prev.includes(permId) ? prev.filter((p) => p !== permId) : [...prev, permId]
    );
  };

  const toggleGroup = (perms: Permission[]) => {
    const groupIds = perms.map(p => p.id);
    const allSelected = groupIds.every(gid => selectedIds.includes(gid));
    if (allSelected) {
      setSelectedIds((prev) => prev.filter(id => !groupIds.includes(id)));
    } else {
      setSelectedIds((prev) => [...new Set([...prev, ...groupIds])]);
    }
  };

  const toggleMenuItem = (itemId: number, childrenIds: number[]) => {
    setSelectedMenuItemIds((prev) => {
      const isSelected = prev.includes(itemId);
      if (isSelected) {
        return prev.filter(id => id !== itemId && !childrenIds.includes(id));
      }
      return [...new Set([...prev, itemId, ...childrenIds])];
    });
  };

  const toggleAllMenuItems = () => {
    const allIds = menuItems.map(m => m.id);
    const allSelected = allIds.every(id => selectedMenuItemIds.includes(id));
    if (allSelected) {
      setSelectedMenuItemIds([]);
    } else {
      setSelectedMenuItemIds(allIds);
    }
  };

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) {
      toast.error('Role name is required');
      return;
    }

    setSubmitting(true);
    try {
      const payload = { name: name.trim(), description: description.trim(), permissionIds: selectedIds };
      if (isEdit && id) {
        const res = await roleApi.update(Number(id), payload);
        if (res.success) {
          await roleApi.setRoleMenuItems(Number(id), selectedMenuItemIds);
          toast.success('Role updated');
        }
      } else {
        const res = await roleApi.create(payload);
        if (res.success) {
          await roleApi.setRoleMenuItems(res.data.id, selectedMenuItemIds);
          toast.success('Role created');
        }
      }
      navigate('/admin/roles');
    } catch {
      toast.error('Failed to save role');
    } finally {
      setSubmitting(false);
    }
  };

  const grouped = allPermissions.reduce<Record<string, Permission[]>>((acc, p) => {
    const group = p.module || 'General';
    if (!acc[group]) acc[group] = [];
    acc[group].push(p);
    return acc;
  }, {});

  const topLevelItems = menuItems.filter(m => m.parentId === null);
  const childItems = (parentId: number) => menuItems.filter(m => m.parentId === parentId);
  const allIds = menuItems.map(m => m.id);
  const allMenuSelected = allIds.length > 0 && allIds.every(id => selectedMenuItemIds.includes(id));

  if (loading) {
    return <div className="flex items-center justify-center min-h-[calc(100vh-10rem)]"><Loader size="lg" /></div>;
  }

  return (
    <div className="mx-auto max-w-3xl">
      <div className="flex items-center gap-4 mb-6">
        <button
          onClick={() => navigate('/admin/roles')}
          className="flex h-9 w-9 items-center justify-center rounded-lg text-muted-foreground hover:bg-accent hover:text-accent-foreground transition-colors"
        >
          <ArrowLeft className="h-4 w-4" />
        </button>
        <div>
          <h1 className="text-2xl font-bold text-foreground">{isEdit ? 'Edit Role' : 'New Role'}</h1>
          <p className="text-sm text-muted-foreground mt-1">
            {isEdit ? 'Update role details and permissions' : 'Create a new role with permissions'}
          </p>
        </div>
      </div>

      <form onSubmit={onSubmit} className="space-y-6" noValidate>
        <div className="rounded-xl border border-border bg-card p-6 space-y-5">
          <div className="grid gap-5 sm:grid-cols-2">
            <div className="space-y-1.5">
              <label className="text-sm font-medium text-foreground">
                Role Name <span className="text-red-500">*</span>
              </label>
              <input
                value={name}
                onChange={(e) => setName(e.target.value)}
                className="flex h-10 w-full rounded-lg border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
                placeholder="e.g. MANAGER"
              />
            </div>
            <div className="space-y-1.5">
              <label className="text-sm font-medium text-foreground">Description</label>
              <input
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                className="flex h-10 w-full rounded-lg border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
                placeholder="Optional description"
              />
            </div>
          </div>
        </div>

        <div className="rounded-xl border border-border bg-card p-6 space-y-5">
          <div>
            <h2 className="text-lg font-semibold text-foreground">Permissions</h2>
            <p className="text-sm text-muted-foreground mt-1">Select the permissions to assign to this role</p>
          </div>
          {allPermissions.length === 0 ? (
            <p className="text-sm text-muted-foreground">No permissions available.</p>
          ) : (
            Object.entries(grouped).map(([group, perms]) => {
              const groupIds = perms.map(p => p.id);
              const selectedCount = groupIds.filter(gid => selectedIds.includes(gid)).length;
              const allSelected = selectedCount === perms.length;
              return (
                <div key={group}>
                  <div className="flex items-center justify-between mb-2">
                    <h3 className="text-sm font-medium text-foreground capitalize">{group}</h3>
                    <button
                      type="button"
                      onClick={() => toggleGroup(perms)}
                      className="text-xs text-muted-foreground hover:text-foreground transition-colors"
                    >
                      {allSelected ? 'Deselect all' : `Select all (${selectedCount}/${perms.length})`}
                    </button>
                  </div>
                  <div className="grid gap-2 sm:grid-cols-2">
                    {perms.map((perm) => (
                      <label
                        key={perm.id}
                        className="flex items-center gap-3 rounded-lg border border-border p-3 cursor-pointer hover:bg-muted/30 transition-colors has-[:checked]:border-primary has-[:checked]:bg-primary/5"
                      >
                        <input
                          type="checkbox"
                          checked={selectedIds.includes(perm.id)}
                          onChange={() => togglePermission(perm.id)}
                          className="h-4 w-4 rounded border-border text-primary focus:ring-primary"
                        />
                        <div className="min-w-0">
                          <span className="text-sm font-medium text-foreground">{perm.name}</span>
                          {perm.description && (
                            <p className="text-xs text-muted-foreground truncate">{perm.description}</p>
                          )}
                        </div>
                      </label>
                    ))}
                  </div>
                </div>
              );
            })
          )}
        </div>

        <div className="rounded-xl border border-border bg-card p-6 space-y-5">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-lg font-semibold text-foreground">Menu Visibility</h2>
              <p className="text-sm text-muted-foreground mt-1">
                Select which sidebar menu items this role can see
              </p>
            </div>
            {menuItems.length > 0 && (
              <button
                type="button"
                onClick={toggleAllMenuItems}
                className="text-xs text-muted-foreground hover:text-foreground transition-colors"
              >
                {allMenuSelected
                  ? 'Deselect all'
                  : `Select all (${selectedMenuItemIds.length}/${allIds.length})`}
              </button>
            )}
          </div>
          {menuItems.length === 0 ? (
            <p className="text-sm text-muted-foreground">No menu items available.</p>
          ) : (
            <div className="space-y-1">
              {topLevelItems.map((item) => {
                const children = childItems(item.id);
                const hasChildren = children.length > 0;
                const isExpanded = expanded[item.label] ?? true;
                const childrenIds = children.map(c => c.id);
                const isChecked = selectedMenuItemIds.includes(item.id);

                return (
                  <div key={item.id}>
                    <div
                      className={cn(
                        'flex items-center gap-2 rounded-lg border border-border px-3 py-2 cursor-pointer hover:bg-muted/30 transition-colors',
                        isChecked && 'border-primary bg-primary/5'
                      )}
                    >
                      {hasChildren && (
                        <button
                          type="button"
                          onClick={() => setExpanded(prev => ({ ...prev, [item.label]: !isExpanded }))}
                          className="h-4 w-4 shrink-0 text-muted-foreground hover:text-foreground"
                        >
                          <ChevronDown className={cn('h-4 w-4 transition-transform', isExpanded && 'rotate-180')} />
                        </button>
                      )}
                      {!hasChildren && <div className="w-4" />}
                      <input
                        type="checkbox"
                        checked={isChecked}
                        onChange={() => toggleMenuItem(item.id, childrenIds)}
                        className="h-4 w-4 rounded border-border text-primary focus:ring-primary"
                      />
                      <span className="text-sm font-medium text-foreground">{item.label}</span>
                    </div>
                    {hasChildren && isExpanded && (
                      <div className="ml-7 mt-1 space-y-1 pl-4 border-l border-border">
                        {children.map((child) => (
                          <label
                            key={child.id}
                            className={cn(
                              'flex items-center gap-2 rounded-lg border border-border px-3 py-2 cursor-pointer hover:bg-muted/30 transition-colors',
                              selectedMenuItemIds.includes(child.id) && 'border-primary bg-primary/5'
                            )}
                          >
                            <input
                              type="checkbox"
                              checked={selectedMenuItemIds.includes(child.id)}
                              onChange={() => toggleMenuItem(child.id, [])}
                              className="h-4 w-4 rounded border-border text-primary focus:ring-primary"
                            />
                            <span className="text-sm text-foreground">{child.label}</span>
                          </label>
                        ))}
                      </div>
                    )}
                  </div>
                );
              })}
            </div>
          )}
        </div>

        <div className="flex items-center justify-end gap-3">
          <Button type="button" variant="outline" onClick={() => navigate('/admin/roles')}>
            Cancel
          </Button>
          <Button type="submit" isLoading={submitting} className="gap-2">
            <Save className="h-4 w-4" /> {isEdit ? 'Update' : 'Create'} Role
          </Button>
        </div>
      </form>
    </div>
  );
};
