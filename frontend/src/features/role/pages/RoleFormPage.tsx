import React, { useEffect, useMemo, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Save, ChevronDown } from 'lucide-react';
import toast from 'react-hot-toast';
import { roleApi } from '@/features/role/api/role.api';
import type { MenuWithPermissions } from '@/features/auth/types/auth.types';
import { Loader } from '@/components/ui/Loader';
import { Button } from '@/components/ui/Button';
import { cn } from '@/lib/utils';

export const RoleFormPage: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = id && id !== 'new';

  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [selectedPermissionIds, setSelectedPermissionIds] = useState<number[]>([]);
  const [menus, setMenus] = useState<MenuWithPermissions[]>([]);
  const [expanded, setExpanded] = useState<Record<string, boolean>>({});
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    const init = async () => {
      try {
        const [menuRes] = await Promise.all([
          roleApi.getMenusWithPermissions(),
          isEdit ? roleApi.getById(Number(id)) : Promise.resolve(null),
        ]);

        if (menuRes.success) {
          setMenus(menuRes.data);
          const expandedMap: Record<string, boolean> = {};
          menuRes.data.forEach(m => { expandedMap[m.label] = true; });
          setExpanded(expandedMap);
        }

        if (isEdit) {
          const roleRes = await roleApi.getById(Number(id));
          if (roleRes.success) {
            setName(roleRes.data.name);
            setDescription(roleRes.data.description || '');
            setSelectedPermissionIds(roleRes.data.permissionIds);
          } else {
            toast.error('Failed to load role');
            navigate('/admin/roles');
          }
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
    setSelectedPermissionIds(prev =>
      prev.includes(permId) ? prev.filter(p => p !== permId) : [...prev, permId]
    );
  };

  const handleViewToggle = (menu: MenuWithPermissions) => {
    if (!menu.permissionName) return;
    const viewPerm = menu.permissions.find(p => p.name === menu.permissionName);
    if (!viewPerm) return;

    const isViewSelected = selectedPermissionIds.includes(viewPerm.id);
    if (isViewSelected) {
      const childIds = menu.permissions.filter(p => p.id !== viewPerm.id).map(p => p.id);
      setSelectedPermissionIds(prev => prev.filter(id => id !== viewPerm.id && !childIds.includes(id)));
    } else {
      setSelectedPermissionIds(prev => [...prev, viewPerm.id]);
    }
  };

  const handleChildToggle = (menu: MenuWithPermissions, permId: number) => {
    const newIds = selectedPermissionIds.includes(permId)
      ? selectedPermissionIds.filter(id => id !== permId)
      : [...selectedPermissionIds, permId];

    const viewPerm = menu.permissionName
      ? menu.permissions.find(p => p.name === menu.permissionName)
      : null;

    if (viewPerm && !newIds.includes(viewPerm.id)) {
      newIds.push(viewPerm.id);
    }

    setSelectedPermissionIds(newIds);
  };

  const topLevelMenus = useMemo(() => menus.filter(m => m.parentId == null), [menus]);
  const childMenus = useMemo(() => {
    const map: Record<number, MenuWithPermissions[]> = {};
    menus.forEach(m => {
      if (m.parentId != null) {
        if (!map[m.parentId]) map[m.parentId] = [];
        map[m.parentId].push(m);
      }
    });
    return map;
  }, [menus]);

  const isViewChecked = (menu: MenuWithPermissions) => {
    if (!menu.permissionName) return true;
    const viewPerm = menu.permissions.find(p => p.name === menu.permissionName);
    return viewPerm ? selectedPermissionIds.includes(viewPerm.id) : true;
  };

  const isViewDisabled = (menu: MenuWithPermissions) => {
    if (!menu.permissionName) return true;
    const viewPerm = menu.permissions.find(p => p.name === menu.permissionName);
    if (!viewPerm) return true;
    const childPerms = menu.permissions.filter(p => p.id !== viewPerm.id);
    return childPerms.some(p => selectedPermissionIds.includes(p.id));
  };

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) {
      toast.error('Role name is required');
      return;
    }

    setSubmitting(true);
    try {
      const payload = { name: name.trim(), description: description.trim(), permissionIds: selectedPermissionIds };
      if (isEdit && id) {
        const res = await roleApi.update(Number(id), payload);
        if (res.success) toast.success('Role updated');
        else { toast.error('Failed to update role'); return; }
      } else {
        const res = await roleApi.create(payload);
        if (res.success) toast.success('Role created');
        else { toast.error('Failed to create role'); return; }
      }
      navigate('/admin/roles');
    } catch {
      toast.error('Failed to save role');
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
            <h2 className="text-lg font-semibold text-foreground">Permissions & Menu Access</h2>
            <p className="text-sm text-muted-foreground mt-1">
              Check the permissions you want this role to have. Each sidebar menu item (like Dashboard, Roles, Content) is shown with its linked permissions below it.
            </p>
            <ul className="text-xs text-muted-foreground mt-2 space-y-1.5 bg-muted/50 rounded-lg px-3 py-2.5 border border-border list-disc list-inside">
              <li><strong>View</strong> checkbox — checking this lets the role <em>see</em> that menu item in the sidebar. If it has children (like Content → Homepage Sections), the children are greyed out until View is checked.</li>
              <li><strong>Create / Edit / Delete</strong> checkboxes — these appear below a menu. Checking any of them <strong>automatically checks</strong> the View checkbox too (since you need access to the page to perform actions on it).</li>
              <li><strong>Greyed-out / locked</strong> checkboxes — when a child checkbox is checked, the parent View becomes locked (you can't uncheck it while a child depends on it). Uncheck all children first, then you can uncheck View.</li>
              <li><strong>Parent menus</strong> (like Content) appear in the sidebar automatically when any child menu has a granted permission.</li>
            </ul>
          </div>

          {topLevelMenus.length === 0 ? (
            <p className="text-sm text-muted-foreground">No menus available.</p>
          ) : (
            <div className="space-y-2">
              {topLevelMenus.map(menu => {
                const children = childMenus[menu.id] || [];
                const hasChildren = children.length > 0;
                const isExpanded = expanded[menu.label] ?? true;
                const viewChecked = isViewChecked(menu);
                const viewDisabled = isViewDisabled(menu);

                return (
                  <div key={menu.id}>
                    <div className="rounded-lg border border-border bg-muted/10 px-3 py-2">
                      <div className="flex items-center gap-2">
                        {hasChildren && (
                          <button
                            type="button"
                            onClick={() => setExpanded(prev => ({ ...prev, [menu.label]: !isExpanded }))}
                            className="h-4 w-4 shrink-0 text-muted-foreground hover:text-foreground"
                          >
                            <ChevronDown className={cn('h-4 w-4 transition-transform', isExpanded && 'rotate-180')} />
                          </button>
                        )}
                        {!hasChildren && <div className="w-4" />}
                        <span className="text-sm font-semibold text-foreground">{menu.label}</span>

                        {menu.permissions.map(p => p.name === menu.permissionName).filter(Boolean).length > 0 && (
                          <label
                            className={cn(
                              'flex items-center gap-1.5 ml-auto cursor-pointer',
                              viewDisabled && 'opacity-50'
                            )}
                          >
                            <input
                              type="checkbox"
                              checked={viewChecked}
                              disabled={viewDisabled}
                              onChange={() => handleViewToggle(menu)}
                              className="h-4 w-4 rounded border-border text-primary focus:ring-primary"
                            />
                            <span className="text-xs text-muted-foreground">
                              {menu.permissionName ? menu.permissionName.split('.').pop() : 'View'}
                            </span>
                          </label>
                        )}
                      </div>
                    </div>

                    {hasChildren && isExpanded && (
                      <div className="ml-6 mt-1 space-y-1 pl-4 border-l border-border">
                        {children.map(child => {
                          const childViewPerm = child.permissionName
                            ? child.permissions.find(p => p.name === child.permissionName)
                            : null;
                          const childViewChecked = childViewPerm
                            ? selectedPermissionIds.includes(childViewPerm.id)
                            : true;
                          const childViewDisabled = childViewPerm
                            ? child.permissions.some(
                                p => p.id !== childViewPerm.id && selectedPermissionIds.includes(p.id)
                              )
                            : true;

                          return (
                            <div key={child.id}>
                              <div className="rounded-lg border border-border px-3 py-2">
                                <div className="flex items-center gap-2">
                                  <span className="text-sm font-medium text-foreground">{child.label}</span>
                                  {childViewPerm && (
                                    <label className={cn('flex items-center gap-1.5 ml-auto', childViewDisabled && 'opacity-50')}>
                                      <input
                                        type="checkbox"
                                        checked={childViewChecked}
                                        disabled={childViewDisabled}
                                        onChange={() => {
                                          const newIds = childViewChecked
                                            ? selectedPermissionIds.filter(id => id !== childViewPerm.id)
                                            : [...selectedPermissionIds, childViewPerm.id];
                                          if (!childViewChecked) {
                                            const childPermIds = child.permissions
                                              .filter(p => p.id !== childViewPerm.id)
                                              .map(p => p.id);
                                            childPermIds.forEach(cpId => {
                                              if (!newIds.includes(cpId)) newIds.push(cpId);
                                            });
                                          }
                                          setSelectedPermissionIds(newIds);
                                        }}
                                        className="h-4 w-4 rounded border-border text-primary focus:ring-primary"
                                      />
                                      <span className="text-xs text-muted-foreground">
                                        {child.permissionName ? child.permissionName.split('.').pop() : 'View'}
                                      </span>
                                    </label>
                                  )}
                                </div>
                              </div>

                              {child.permissions.length > 0 && (
                                <div className="ml-6 mt-1 space-y-1">
                                  {child.permissions
                                    .filter(p => child.permissionName ? p.name !== child.permissionName : true)
                                    .map(perm => (
                                      <label
                                        key={perm.id}
                                        className={cn(
                                          'flex items-center gap-3 rounded-lg border border-border p-2.5 cursor-pointer hover:bg-muted/30 transition-colors has-[:checked]:border-primary has-[:checked]:bg-primary/5',
                                          !childViewChecked && 'opacity-40 pointer-events-none'
                                        )}
                                      >
                                        <input
                                          type="checkbox"
                                          checked={selectedPermissionIds.includes(perm.id)}
                                          onChange={() => handleChildToggle(child, perm.id)}
                                          className="h-4 w-4 rounded border-border text-primary focus:ring-primary"
                                        />
                                        <div className="min-w-0">
                                          <span className="text-sm text-foreground">
                                            {perm.name.includes('.') ? perm.name.split('.').pop() : perm.name}
                                          </span>
                                          {perm.description && (
                                            <p className="text-xs text-muted-foreground truncate">{perm.description}</p>
                                          )}
                                        </div>
                                      </label>
                                    ))}
                                </div>
                              )}
                            </div>
                          );
                        })}
                      </div>
                    )}

                    {menu.permissions.length > 0 && isExpanded && (
                      <div className="ml-6 mt-1 space-y-1 pl-4">
                        {menu.permissions
                          .filter(p => menu.permissionName ? p.name !== menu.permissionName : true)
                          .map(perm => (
                            <label
                              key={perm.id}
                              className={cn(
                                'flex items-center gap-3 rounded-lg border border-border p-2.5 cursor-pointer hover:bg-muted/30 transition-colors has-[:checked]:border-primary has-[:checked]:bg-primary/5',
                                !viewChecked && 'opacity-40 pointer-events-none'
                              )}
                            >
                              <input
                                type="checkbox"
                                checked={selectedPermissionIds.includes(perm.id)}
                                onChange={() => handleChildToggle(menu, perm.id)}
                                className="h-4 w-4 rounded border-border text-primary focus:ring-primary"
                              />
                              <div className="min-w-0">
                                <span className="text-sm text-foreground">
                                  {perm.name.includes('.') ? perm.name.split('.').pop() : perm.name}
                                </span>
                                {perm.description && (
                                  <p className="text-xs text-muted-foreground truncate">{perm.description}</p>
                                )}
                              </div>
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
