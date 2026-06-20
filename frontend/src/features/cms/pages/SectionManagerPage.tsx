import React, { useEffect, useState, useCallback } from 'react';
import {
  Plus,
  GripVertical,
  Trash2,
  Eye,
  EyeOff,
  PanelRightOpen,
  X,
  Save,
} from 'lucide-react';
import {
  DndContext,
  DragOverlay,
  closestCenter,
  PointerSensor,
  useSensor,
  useSensors,
  type DragEndEvent,
  type DragStartEvent,
} from '@dnd-kit/core';
import {
  SortableContext,
  useSortable,
  verticalListSortingStrategy,
  arrayMove,
} from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import toast from 'react-hot-toast';
import { cmsApi } from '@/features/cms/api/cms.api';
import { SectionRenderer } from '@/features/home/components/sections/SectionRenderer';
import { Button } from '@/components/ui/Button';
import type { HomepageSection } from '@/features/cms/types/cms.types';
import { SECTION_TYPES } from '@/features/cms/types/cms.types';

const typeIcon: Record<string, string> = {
  hero_banner: '🎯',
  stats_bar: '📊',
  product_journey: '🔄',
  business_areas: '🏢',
  benefits: '⭐',
  cta: '📢',
  announcement: '📣',
  info_block: 'ℹ️',
  promotion: '🏷️',
  image_card: '🖼️',
  rich_content: '📝',
};

const typeLabel = (type: string) =>
  SECTION_TYPES.find(t => t.value === type)?.label || type;

const SortableSectionCard: React.FC<{
  section: HomepageSection;
  isDragging?: boolean;
  onEdit: () => void;
  onDelete: (id: number, name: string) => void;
  onToggleActive: (id: number, active: boolean) => void;
}> = ({ section, isDragging, onEdit, onDelete, onToggleActive }) => {
  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
    isDragging: isSortDragging,
  } = useSortable({ id: section.id });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
  };

  return (
    <div
      ref={setNodeRef}
      style={style}
      className={`group flex items-center gap-3 rounded-xl border bg-card px-3 py-3 transition-all ${
        isSortDragging || isDragging
          ? 'border-primary/50 shadow-lg shadow-primary/5 z-10 opacity-90'
          : 'border-border hover:border-primary/30 hover:shadow-sm'
      }`}
    >
      <button
        className="flex cursor-grab touch-none items-center text-muted-foreground/40 hover:text-muted-foreground transition-colors"
        {...attributes}
        {...listeners}
      >
        <GripVertical className="h-4 w-4" />
      </button>

      <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-lg bg-muted text-sm">
        {typeIcon[section.sectionType] || '📄'}
      </div>

      <div className="min-w-0 flex-1 cursor-pointer" onClick={onEdit}>
        <div className="flex items-center gap-2">
          <span className="truncate text-sm font-medium text-foreground">
            {section.sectionName}
          </span>
          <span className="shrink-0 rounded bg-muted px-1.5 py-0.5 text-[10px] text-muted-foreground">
            {typeLabel(section.sectionType)}
          </span>
        </div>
        <p className="truncate text-xs text-muted-foreground">{section.title}</p>
      </div>

      <button
        onClick={() => onToggleActive(section.id, !section.active)}
        className={`flex h-7 w-7 items-center justify-center rounded-lg transition-colors ${
          section.active
            ? 'text-green-500 hover:bg-green-500/10'
            : 'text-muted-foreground/40 hover:text-muted-foreground hover:bg-accent'
        }`}
        title={section.active ? 'Active' : 'Inactive'}
      >
        {section.active ? <Eye className="h-3.5 w-3.5" /> : <EyeOff className="h-3.5 w-3.5" />}
      </button>

      <button
        onClick={(e) => { e.stopPropagation(); onEdit(); }}
        className="flex h-7 w-7 items-center justify-center rounded-lg text-muted-foreground/40 hover:text-primary hover:bg-accent transition-colors"
        title="Edit"
      >
        <PanelRightOpen className="h-3.5 w-3.5" />
      </button>

      <button
        onClick={() => onDelete(section.id, section.sectionName)}
        className="flex h-7 w-7 items-center justify-center rounded-lg text-muted-foreground/40 hover:text-destructive hover:bg-destructive/10 transition-colors"
        title="Delete"
      >
        <Trash2 className="h-3.5 w-3.5" />
      </button>
    </div>
  );
};

export const SectionManagerPage: React.FC = () => {
  const [sections, setSections] = useState<HomepageSection[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeId, setActiveId] = useState<number | null>(null);
  const [editingSection, setEditingSection] = useState<HomepageSection | null>(null);
  const [panelOpen, setPanelOpen] = useState(false);
  const [isNew, setIsNew] = useState(false);

  const sensors = useSensors(
    useSensor(PointerSensor, { activationConstraint: { distance: 6 } })
  );

  const fetchSections = useCallback(async () => {
    try {
      const res = await cmsApi.getAllSections();
      if (res.success) setSections(res.data);
    } catch {
      toast.error('Failed to load sections');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { fetchSections(); }, [fetchSections]);

  const handleDragStart = (event: DragStartEvent) => {
    setActiveId(event.active.id as number);
  };

  const handleDragEnd = async (event: DragEndEvent) => {
    setActiveId(null);
    const { active, over } = event;
    if (!over || active.id === over.id) return;

    const oldIndex = sections.findIndex(s => s.id === active.id);
    const newIndex = sections.findIndex(s => s.id === over.id);
    if (oldIndex === -1 || newIndex === -1) return;

    const reordered = arrayMove(sections, oldIndex, newIndex).map((s, i) => ({
      ...s,
      displayOrder: i + 1,
    }));
    setSections(reordered);

    try {
      await cmsApi.reorderSections(
        reordered.map(s => ({ id: s.id, displayOrder: s.displayOrder }))
      );
    } catch {
      toast.error('Failed to save order');
      fetchSections();
    }
  };

  const handleDelete = async (id: number, name: string) => {
    if (!window.confirm(`Delete section "${name}"?`)) return;
    try {
      await cmsApi.deleteSection(id);
      toast.success('Section deleted');
      fetchSections();
    } catch {
      toast.error('Failed to delete section');
    }
  };

  const handleToggleActive = async (id: number, active: boolean) => {
    const section = sections.find(s => s.id === id);
    if (!section) return;
    try {
      await cmsApi.updateSection(id, {
        sectionName: section.sectionName,
        sectionType: section.sectionType,
        title: section.title,
        subtitle: section.subtitle,
        description: section.description,
        imageUrl: section.imageUrl,
        buttonText: section.buttonText,
        buttonUrl: section.buttonUrl,
        displayOrder: section.displayOrder,
        active,
        startDate: section.startDate,
        endDate: section.endDate,
      });
      setSections(prev => prev.map(s => s.id === id ? { ...s, active } : s));
      toast.success(active ? 'Section activated' : 'Section deactivated');
    } catch {
      toast.error('Failed to update section');
    }
  };

  const openEditor = (section: HomepageSection | null) => {
    setIsNew(!section);
    setEditingSection(section ? { ...section } : null);
    setPanelOpen(true);
  };

  const handleSave = async () => {
    if (!editingSection) return;
    try {
      if (isNew) {
        const res = await cmsApi.createSection({
          sectionName: editingSection.sectionName,
          sectionType: editingSection.sectionType,
          title: editingSection.title,
          subtitle: editingSection.subtitle,
          description: editingSection.description,
          imageUrl: editingSection.imageUrl,
          buttonText: editingSection.buttonText,
          buttonUrl: editingSection.buttonUrl,
          displayOrder: sections.length + 1,
          active: editingSection.active,
          startDate: editingSection.startDate,
          endDate: editingSection.endDate,
        });
        if (res.success) {
          toast.success('Section created');
          setPanelOpen(false);
          fetchSections();
        }
      } else {
        await cmsApi.updateSection(editingSection.id, {
          sectionName: editingSection.sectionName,
          sectionType: editingSection.sectionType,
          title: editingSection.title,
          subtitle: editingSection.subtitle,
          description: editingSection.description,
          imageUrl: editingSection.imageUrl,
          buttonText: editingSection.buttonText,
          buttonUrl: editingSection.buttonUrl,
          displayOrder: editingSection.displayOrder,
          active: editingSection.active,
          startDate: editingSection.startDate,
          endDate: editingSection.endDate,
        });
        toast.success('Section updated');
        setPanelOpen(false);
        fetchSections();
      }
    } catch {
      toast.error('Failed to save section');
    }
  };

  const activeSection = activeId ? sections.find(s => s.id === activeId) : null;

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64 text-muted-foreground text-sm">
        Loading...
      </div>
    );
  }

  return (
    <div className="flex h-full flex-col">
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-foreground">Section Manager</h1>
          <p className="text-sm text-muted-foreground mt-1">
            Drag to reorder sections • Click to edit • Preview updates live
          </p>
        </div>
        <Button onClick={() => openEditor(null)} className="gap-2">
          <Plus className="h-4 w-4" /> Add Section
        </Button>
      </div>

      <div className="flex flex-1 gap-6 overflow-hidden">
        {/* Left: Draggable Section List */}
        <div className="w-[380px] shrink-0 overflow-y-auto pr-2">
          {sections.length === 0 ? (
            <div className="flex flex-col items-center justify-center rounded-xl border-2 border-dashed border-border p-12 text-center">
              <p className="text-sm text-muted-foreground mb-3">
                No sections yet. Create your first homepage section.
              </p>
              <Button variant="outline" onClick={() => openEditor(null)} className="gap-2">
                <Plus className="h-4 w-4" /> Create Section
              </Button>
            </div>
          ) : (
            <DndContext
              sensors={sensors}
              collisionDetection={closestCenter}
              onDragStart={handleDragStart}
              onDragEnd={handleDragEnd}
            >
              <SortableContext
                items={sections.map(s => s.id)}
                strategy={verticalListSortingStrategy}
              >
                <div className="space-y-2">
                  {sections.map(section => (
                    <SortableSectionCard
                      key={section.id}
                      section={section}
                      isDragging={activeId === section.id}
                      onEdit={() => openEditor(section)}
                      onDelete={handleDelete}
                      onToggleActive={handleToggleActive}
                    />
                  ))}
                </div>
              </SortableContext>
              <DragOverlay>
                {activeSection && (
                  <div className="rounded-xl border border-primary/50 bg-card px-3 py-3 shadow-lg opacity-90">
                    <div className="flex items-center gap-3">
                      <GripVertical className="h-4 w-4 text-muted-foreground" />
                      <span className="text-sm font-medium">{activeSection.sectionName}</span>
                    </div>
                  </div>
                )}
              </DragOverlay>
            </DndContext>
          )}
        </div>

        {/* Right: Live Preview */}
        <div className="flex-1 overflow-hidden rounded-xl border border-border bg-background">
          <div className="flex items-center gap-2 border-b border-border px-4 py-2 bg-muted/30">
            <div className="flex gap-1.5">
              <div className="h-2.5 w-2.5 rounded-full bg-red-400" />
              <div className="h-2.5 w-2.5 rounded-full bg-yellow-400" />
              <div className="h-2.5 w-2.5 rounded-full bg-green-400" />
            </div>
            <span className="text-xs text-muted-foreground ml-2">
              {window.location.origin} — Live Preview
            </span>
          </div>
          <div className="overflow-y-auto" style={{ maxHeight: 'calc(100vh - 260px)' }}>
            {sections.length === 0 ? (
              <div className="flex items-center justify-center h-64 text-sm text-muted-foreground">
                Add sections to see a live preview
              </div>
            ) : (
              sections
                .filter(s => s.active)
                .map(section => (
                  <SectionRenderer key={section.id} section={section} />
                ))
            )}
          </div>
        </div>
      </div>

      {/* Edit Panel (slide-over) */}
      {panelOpen && (
        <div className="fixed inset-0 z-50 flex justify-end">
          <div className="fixed inset-0 bg-black/40" onClick={() => setPanelOpen(false)} />
          <div className="relative w-full max-w-lg bg-background border-l border-border shadow-2xl overflow-y-auto">
            <div className="sticky top-0 z-10 flex items-center justify-between border-b border-border bg-background px-6 py-4">
              <h2 className="text-lg font-semibold text-foreground">
                {isNew ? 'New Section' : 'Edit Section'}
              </h2>
              <button
                onClick={() => setPanelOpen(false)}
                className="flex h-8 w-8 items-center justify-center rounded-lg text-muted-foreground hover:bg-accent hover:text-accent-foreground transition-colors"
              >
                <X className="h-4 w-4" />
              </button>
            </div>

            <div className="p-6 space-y-5">
              {isNew && (
                <div>
                  <label className="block text-sm font-medium text-foreground mb-1.5">Section Type</label>
                  <select
                    value={editingSection?.sectionType || ''}
                    onChange={(e) => setEditingSection(prev => prev ? { ...prev, sectionType: e.target.value } : null)}
                    className="w-full rounded-lg border border-border bg-background px-3 py-2 text-sm text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
                  >
                    <option value="">Select type...</option>
                    {SECTION_TYPES.map(t => (
                      <option key={t.value} value={t.value}>{t.label}</option>
                    ))}
                  </select>
                  <p className="text-xs text-muted-foreground mt-1">Determines how the section looks and what data it expects. Cannot be changed after creation.</p>
                </div>
              )}

              <div>
                <label className="block text-sm font-medium text-foreground mb-1.5">Section Name</label>
                <input
                  value={editingSection?.sectionName || ''}
                  onChange={(e) => setEditingSection(prev => prev ? { ...prev, sectionName: e.target.value } : null)}
                  className="w-full rounded-lg border border-border bg-background px-3 py-2 text-sm text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
                  placeholder="e.g. Hero Banner"
                />
                <p className="text-xs text-muted-foreground mt-1">Internal label used to identify this section in the admin panel.</p>
              </div>

              <div>
                <label className="block text-sm font-medium text-foreground mb-1.5">Title</label>
                <input
                  value={editingSection?.title || ''}
                  onChange={(e) => setEditingSection(prev => prev ? { ...prev, title: e.target.value } : null)}
                  className="w-full rounded-lg border border-border bg-background px-3 py-2 text-sm text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
                  placeholder="e.g. Complete Visibility Across Your Channel Ecosystem"
                />
                <p className="text-xs text-muted-foreground mt-1">Main heading displayed on the section. Usually the largest text.</p>
              </div>

              <div>
                <label className="block text-sm font-medium text-foreground mb-1.5">Subtitle</label>
                <input
                  value={editingSection?.subtitle || ''}
                  onChange={(e) => setEditingSection(prev => prev ? { ...prev, subtitle: e.target.value } : null)}
                  className="w-full rounded-lg border border-border bg-background px-3 py-2 text-sm text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
                  placeholder="e.g. Enterprise Channel Management Platform"
                />
                <p className="text-xs text-muted-foreground mt-1">Smaller text below the title. Used as a tagline or badge label.</p>
              </div>

              <div>
                <label className="block text-sm font-medium text-foreground mb-1.5">Description</label>
                <textarea
                  value={editingSection?.description || ''}
                  onChange={(e) => setEditingSection(prev => prev ? { ...prev, description: e.target.value } : null)}
                  rows={3}
                  className="w-full rounded-lg border border-border bg-background px-3 py-2 text-sm text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50 resize-none"
                  placeholder="e.g. Track the complete lifecycle of products across your distribution network..."
                />
                <p className="text-xs text-muted-foreground mt-1">For basic sections: paragraph text. For Stats/Journey/Areas/Benefits: JSON array of items.</p>
              </div>

              <div>
                <label className="block text-sm font-medium text-foreground mb-1.5">Image URL</label>
                <input
                  value={editingSection?.imageUrl || ''}
                  onChange={(e) => setEditingSection(prev => prev ? { ...prev, imageUrl: e.target.value } : null)}
                  className="w-full rounded-lg border border-border bg-background px-3 py-2 text-sm text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
                  placeholder="https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=800"
                />
                <p className="text-xs text-muted-foreground mt-1">Background or featured image. Uses a publicly accessible URL.</p>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-foreground mb-1.5">Button Text</label>
                  <input
                    value={editingSection?.buttonText || ''}
                    onChange={(e) => setEditingSection(prev => prev ? { ...prev, buttonText: e.target.value } : null)}
                    className="w-full rounded-lg border border-border bg-background px-3 py-2 text-sm text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
                    placeholder="e.g. Access Platform"
                  />
                  <p className="text-xs text-muted-foreground mt-1">Label on the call-to-action button.</p>
                </div>
                <div>
                  <label className="block text-sm font-medium text-foreground mb-1.5">Button URL</label>
                  <input
                    value={editingSection?.buttonUrl || ''}
                    onChange={(e) => setEditingSection(prev => prev ? { ...prev, buttonUrl: e.target.value } : null)}
                    className="w-full rounded-lg border border-border bg-background px-3 py-2 text-sm text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
                    placeholder="e.g. /login or https://..."
                  />
                  <p className="text-xs text-muted-foreground mt-1">Where the CTA button links to. Internal paths or full URLs.</p>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-foreground mb-1.5">Start Date</label>
                  <input
                    type="datetime-local"
                    value={editingSection?.startDate ? editingSection.startDate.slice(0, 16) : ''}
                    onChange={(e) => setEditingSection(prev => prev ? { ...prev, startDate: e.target.value ? e.target.value + ':00' : null } : null)}
                    className="w-full rounded-lg border border-border bg-background px-3 py-2 text-sm text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
                  />
                  <p className="text-xs text-muted-foreground mt-1">Leave empty to show immediately. Section hides until this time if set.</p>
                </div>
                <div>
                  <label className="block text-sm font-medium text-foreground mb-1.5">End Date</label>
                  <input
                    type="datetime-local"
                    value={editingSection?.endDate ? editingSection.endDate.slice(0, 16) : ''}
                    onChange={(e) => setEditingSection(prev => prev ? { ...prev, endDate: e.target.value ? e.target.value + ':00' : null } : null)}
                    className="w-full rounded-lg border border-border bg-background px-3 py-2 text-sm text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
                  />
                  <p className="text-xs text-muted-foreground mt-1">Section automatically hides after this time. Leave empty for no expiry.</p>
                </div>
              </div>

              <div className="flex items-center gap-3">
                <label className="relative inline-flex h-6 w-11 cursor-pointer items-center">
                  <input
                    type="checkbox"
                    checked={editingSection?.active ?? true}
                    onChange={(e) => setEditingSection(prev => prev ? { ...prev, active: e.target.checked } : null)}
                    className="peer sr-only"
                  />
                  <span className="absolute inset-0 rounded-full bg-muted peer-checked:bg-primary transition-colors" />
                  <span className="absolute left-0.5 top-0.5 h-5 w-5 rounded-full bg-white shadow peer-checked:translate-x-5 transition-transform" />
                </label>
                <span className="text-sm text-foreground">Active</span>
              </div>
              <p className="text-xs text-muted-foreground -mt-3">When inactive, the section won't appear on the public homepage.</p>
            </div>

            <div className="sticky bottom-0 flex items-center justify-end gap-3 border-t border-border bg-background px-6 py-4">
              <Button variant="outline" onClick={() => setPanelOpen(false)}>Cancel</Button>
              <Button
                onClick={handleSave}
                className="gap-2"
                disabled={!editingSection?.sectionName || (!isNew && !editingSection?.sectionType)}
              >
                <Save className="h-4 w-4" /> {isNew ? 'Create' : 'Save'}
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
