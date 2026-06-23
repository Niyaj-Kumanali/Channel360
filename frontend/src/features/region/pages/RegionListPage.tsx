import React, { useEffect, useState } from 'react';
import { Plus, Pencil, Trash2, ChevronRight, ChevronDown, MapPin } from 'lucide-react';
import toast from 'react-hot-toast';
import { regionApi } from '@/features/region/api/region.api';
import type { Region, RegionRequest } from '@/features/region/types/region.types';
import { Loader } from '@/components/ui/Loader';
import { Button } from '@/components/ui/Button';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { cn } from '@/lib/utils';

type TreeTypeFilter = 'all' | 'B2B' | 'B2C';

const levelColors: Record<string, string> = {
  Zone: 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-300',
  Region: 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300',
  State: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300',
  Territory: 'bg-amber-100 text-amber-700 dark:bg-amber-900/30 dark:text-amber-300',
};

const levelOrder = ['Zone', 'Region', 'State', 'Territory'];

const LevelBadge: React.FC<{ level: string }> = ({ level }) => (
  <span className={cn('inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium', levelColors[level] || 'bg-gray-100 text-gray-700')}>
    {level}
  </span>
);

const drawerDefaults: RegionRequest = { name: '', parentId: null, level: 'Zone', treeType: 'B2B' };

interface RegionTreeNodeProps {
  region: Region;
  allRegions: Region[];
  selectedId: number | null;
  onSelect: (r: Region) => void;
  depth?: number;
}

const RegionTreeNode: React.FC<RegionTreeNodeProps> = ({ region, allRegions, selectedId, onSelect, depth = 0 }) => {
  const children = allRegions.filter(r => r.parentId === region.id);
  const [expanded, setExpanded] = useState(depth < 2);

  return (
    <div>
      <div
        onClick={() => onSelect(region)}
        className={cn(
          'flex items-center gap-2 rounded-lg px-3 py-2 cursor-pointer transition-colors',
          selectedId === region.id ? 'bg-primary/10 text-primary' : 'hover:bg-accent/50 text-foreground'
        )}
        style={{ paddingLeft: `${depth * 1.25 + 0.75}rem` }}
      >
        {children.length > 0 ? (
          <button
            onClick={(e) => { e.stopPropagation(); setExpanded(!expanded); }}
            className="flex h-5 w-5 items-center justify-center rounded text-muted-foreground hover:bg-accent"
          >
            {expanded ? <ChevronDown className="h-3.5 w-3.5" /> : <ChevronRight className="h-3.5 w-3.5" />}
          </button>
        ) : (
          <div className="h-5 w-5 flex items-center justify-center">
            <div className="h-1.5 w-1.5 rounded-full bg-muted-foreground/40" />
          </div>
        )}
        <MapPin className="h-3.5 w-3.5 shrink-0" />
        <span className="flex-1 text-sm truncate">{region.name}</span>
        <LevelBadge level={region.level} />
      </div>
      {expanded && children.length > 0 && (
        <div className="border-l border-border ml-3">
          {children.sort((a, b) => a.name.localeCompare(b.name)).map(child => (
            <RegionTreeNode key={child.id} region={child} allRegions={allRegions} selectedId={selectedId} onSelect={onSelect} depth={depth + 1} />
          ))}
        </div>
      )}
    </div>
  );
};

export const RegionListPage: React.FC = () => {
  const { hasAnyRole } = useAuth();
  const canEdit = hasAnyRole('SUPER_ADMIN');
  const [regions, setRegions] = useState<Region[]>([]);
  const [loading, setLoading] = useState(true);
  const [treeType, setTreeType] = useState<TreeTypeFilter>('all');
  const [selectedRegion, setSelectedRegion] = useState<Region | null>(null);
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [form, setForm] = useState<RegionRequest>({ ...drawerDefaults });
  const [editingId, setEditingId] = useState<number | null>(null);
  const [saving, setSaving] = useState(false);

  const fetchRegions = async () => {
    try {
      const params = treeType === 'all' ? undefined : treeType;
      const res = await regionApi.getAll(params);
      if (res.success) setRegions(res.data);
    } catch { toast.error('Failed to load regions'); }
    finally { setLoading(false); }
  };

  useEffect(() => { setLoading(true); fetchRegions(); }, [treeType]);

  const rootRegions = regions.filter(r => r.parentId === null).sort((a, b) => a.name.localeCompare(b.name));

  const openCreate = () => {
    setForm({ ...drawerDefaults, treeType: treeType === 'all' ? 'B2B' : treeType });
    setEditingId(null);
    setDrawerOpen(true);
  };

  const openEdit = (region: Region) => {
    setForm({ name: region.name, parentId: region.parentId, level: region.level, treeType: region.treeType });
    setEditingId(region.id);
    setDrawerOpen(true);
  };

  const openAddChild = (parent: Region) => {
    const idx = levelOrder.indexOf(parent.level);
    if (idx === -1 || idx >= levelOrder.length - 1) { toast.error('Cannot add child to this level'); return; }
    setForm({ name: '', parentId: parent.id, level: levelOrder[idx + 1], treeType: parent.treeType });
    setEditingId(null);
    setDrawerOpen(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.name.trim()) { toast.error('Name is required'); return; }
    setSaving(true);
    try {
      if (editingId) {
        const res = await regionApi.update(editingId, form);
        if (res.success) { toast.success('Region updated'); setDrawerOpen(false); fetchRegions(); }
      } else {
        const res = await regionApi.create(form);
        if (res.success) { toast.success('Region created'); setDrawerOpen(false); fetchRegions(); }
      }
    } catch { toast.error(editingId ? 'Failed to update' : 'Failed to create'); }
    finally { setSaving(false); }
  };

  const handleDelete = async (region: Region) => {
    if (!window.confirm(`Delete region "${region.name}"? This may affect child regions.`)) return;
    try { const res = await regionApi.delete(region.id); if (res.success) { toast.success('Region deleted'); if (selectedRegion?.id === region.id) setSelectedRegion(null); fetchRegions(); } }
    catch { toast.error('Failed to delete'); }
  };

  const children = regions.filter(r => r.parentId === selectedRegion?.id);
  const siblingRegions = selectedRegion ? regions.filter(r => r.parentId === selectedRegion.parentId && r.id !== selectedRegion.id) : [];

  if (loading) {
    return <div className="flex items-center justify-center min-h-[calc(100vh-10rem)]"><Loader size="lg" /></div>;
  }

  return (
    <div className="mx-auto max-w-6xl">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-foreground">Regions</h1>
          <p className="text-sm text-muted-foreground mt-1">Manage B2B and B2C region hierarchies</p>
        </div>
        <div className="flex items-center gap-3">
          {(['all', 'B2B', 'B2C'] as const).map(t => (
            <button key={t} onClick={() => setTreeType(t)}
              className={cn('rounded-lg px-3 py-1.5 text-sm font-medium transition-colors', treeType === t ? 'bg-primary text-primary-foreground' : 'text-muted-foreground hover:bg-accent hover:text-accent-foreground')}>
              {t === 'all' ? 'All' : t}
            </button>
          ))}
          {canEdit && (
            <Button onClick={openCreate} size="sm" className="gap-1"><Plus className="h-3.5 w-3.5" /> New Region</Button>
          )}
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-1">
          <div className="rounded-xl border border-border bg-card overflow-hidden">
            {rootRegions.length === 0 ? (
              <div className="px-4 py-12 text-center text-sm text-muted-foreground">No regions found.</div>
            ) : (
              rootRegions.map(region => (
                <RegionTreeNode key={region.id} region={region} allRegions={regions} selectedId={selectedRegion?.id ?? null} onSelect={setSelectedRegion} />
              ))
            )}
          </div>
        </div>

        <div className="lg:col-span-2">
          {selectedRegion ? (
            <div className="rounded-xl border border-border bg-card">
              <div className="flex items-center justify-between px-5 py-4 border-b border-border">
                <div className="flex items-center gap-3">
                  <MapPin className="h-5 w-5 text-primary" />
                  <div>
                    <h2 className="text-lg font-semibold text-foreground">{selectedRegion.name}</h2>
                    <p className="text-xs text-muted-foreground">{selectedRegion.path}</p>
                  </div>
                </div>
                {canEdit && (
                  <div className="flex gap-1">
                    <button onClick={() => openAddChild(selectedRegion)}
                      className="flex h-8 w-8 items-center justify-center rounded-lg text-muted-foreground hover:bg-accent hover:text-accent-foreground" title="Add child">
                      <Plus className="h-4 w-4" />
                    </button>
                    <button onClick={() => openEdit(selectedRegion)}
                      className="flex h-8 w-8 items-center justify-center rounded-lg text-muted-foreground hover:bg-accent hover:text-accent-foreground" title="Edit">
                      <Pencil className="h-4 w-4" />
                    </button>
                    <button onClick={() => handleDelete(selectedRegion)}
                      className="flex h-8 w-8 items-center justify-center rounded-lg text-muted-foreground hover:bg-destructive/10 hover:text-destructive" title="Delete">
                      <Trash2 className="h-4 w-4" />
                    </button>
                  </div>
                )}
              </div>
              <div className="p-5 space-y-4">
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="text-xs text-muted-foreground">Level</label>
                    <p className="text-sm font-medium text-foreground mt-0.5"><LevelBadge level={selectedRegion.level} /></p>
                  </div>
                  <div>
                    <label className="text-xs text-muted-foreground">Tree Type</label>
                    <p className="text-sm font-medium text-foreground mt-0.5">{selectedRegion.treeType}</p>
                  </div>
                  <div>
                    <label className="text-xs text-muted-foreground">Created By</label>
                    <p className="text-sm text-foreground mt-0.5">{selectedRegion.createdBy || '-'}</p>
                  </div>
                  <div>
                    <label className="text-xs text-muted-foreground">Updated By</label>
                    <p className="text-sm text-foreground mt-0.5">{selectedRegion.updatedBy || '-'}</p>
                  </div>
                </div>

                {children.length > 0 && (
                  <div>
                    <label className="text-xs text-muted-foreground uppercase tracking-wider font-semibold">Child Regions ({children.length})</label>
                    <div className="mt-2 space-y-1">
                      {children.sort((a, b) => a.name.localeCompare(b.name)).map(child => (
                        <button key={child.id} onClick={() => setSelectedRegion(child)}
                          className="flex items-center gap-2 w-full rounded-lg px-3 py-2 text-sm hover:bg-accent/50 transition-colors text-left">
                          <MapPin className="h-3.5 w-3.5 shrink-0 text-muted-foreground" />
                          <span className="flex-1 text-foreground">{child.name}</span>
                          <LevelBadge level={child.level} />
                        </button>
                      ))}
                    </div>
                  </div>
                )}

                {siblingRegions.length > 0 && (
                  <div>
                    <label className="text-xs text-muted-foreground uppercase tracking-wider font-semibold">Sibling Regions</label>
                    <div className="mt-2 space-y-1">
                      {siblingRegions.sort((a, b) => a.name.localeCompare(b.name)).map(sibling => (
                        <button key={sibling.id} onClick={() => setSelectedRegion(sibling)}
                          className="flex items-center gap-2 w-full rounded-lg px-3 py-2 text-sm hover:bg-accent/50 transition-colors text-left">
                          <MapPin className="h-3.5 w-3.5 shrink-0 text-muted-foreground" />
                          <span className="flex-1 text-foreground">{sibling.name}</span>
                          <LevelBadge level={sibling.level} />
                        </button>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            </div>
          ) : (
            <div className="rounded-xl border border-border bg-card flex items-center justify-center min-h-[300px]">
              <p className="text-muted-foreground">Select a region from the tree to view details</p>
            </div>
          )}
        </div>
      </div>

      {drawerOpen && (
        <div className="fixed inset-0 z-50 flex justify-end">
          <div className="fixed inset-0 bg-black/40" onClick={() => setDrawerOpen(false)} />
          <div className="relative w-full max-w-lg bg-background border-l border-border shadow-xl overflow-y-auto">
            <div className="flex items-center justify-between px-6 py-4 border-b border-border">
              <h2 className="text-lg font-semibold text-foreground">{editingId ? 'Edit Region' : form.parentId ? 'Add Child Region' : 'New Region'}</h2>
              <button onClick={() => setDrawerOpen(false)} className="text-muted-foreground hover:text-foreground text-xl">&times;</button>
            </div>
            <form onSubmit={handleSubmit} className="p-6 space-y-4">
              <div>
                <label className="block text-sm font-medium text-foreground mb-1">Name</label>
                <input type="text" value={form.name} onChange={e => setForm({ ...form, name: e.target.value })}
                  placeholder="Region name" className="w-full rounded-lg border border-input bg-background px-3 py-2 text-sm outline-none focus:border-ring focus:ring-1 focus:ring-ring" />
              </div>
              <div>
                <label className="block text-sm font-medium text-foreground mb-1">Level</label>
                <select value={form.level} onChange={e => setForm({ ...form, level: e.target.value })}
                  className="w-full rounded-lg border border-input bg-background px-3 py-2 text-sm outline-none focus:border-ring focus:ring-1 focus:ring-ring">
                  {levelOrder.map(l => <option key={l} value={l}>{l}</option>)}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-foreground mb-1">Tree Type</label>
                <select value={form.treeType} onChange={e => setForm({ ...form, treeType: e.target.value })}
                  className="w-full rounded-lg border border-input bg-background px-3 py-2 text-sm outline-none focus:border-ring focus:ring-1 focus:ring-ring">
                  <option value="B2B">B2B</option>
                  <option value="B2C">B2C</option>
                </select>
              </div>
              <div className="flex gap-3 pt-4">
                <Button type="submit" isLoading={saving} className="flex-1">{editingId ? 'Update' : 'Create'}</Button>
                <Button type="button" variant="outline" onClick={() => setDrawerOpen(false)}>Cancel</Button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
