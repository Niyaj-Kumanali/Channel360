import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Plus, Pencil, Trash2, ExternalLink } from 'lucide-react';
import toast from 'react-hot-toast';
import { cmsApi } from '@/features/cms/api/cms.api';
import type { HomepageSection } from '@/features/cms/types/cms.types';
import { SECTION_TYPES } from '@/features/cms/types/cms.types';
import { Loader } from '@/components/ui/Loader';
import { Button } from '@/components/ui/Button';

export const SectionListPage: React.FC = () => {
  const [sections, setSections] = useState<HomepageSection[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchSections = async () => {
    try {
      const res = await cmsApi.getAllSections();
      if (res.success) setSections(res.data);
    } catch {
      toast.error('Failed to load sections');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSections();
  }, []);

  const handleDelete = async (id: number, name: string) => {
    if (!window.confirm(`Delete section "${name}"?`)) return;
    try {
      const res = await cmsApi.deleteSection(id);
      if (res.success) {
        toast.success('Section deleted');
        fetchSections();
      }
    } catch {
      toast.error('Failed to delete section');
    }
  };

  const typeLabel = (type: string) => SECTION_TYPES.find(t => t.value === type)?.label || type;

  if (loading) {
    return <div className="flex items-center justify-center min-h-[calc(100vh-10rem)]"><Loader size="lg" /></div>;
  }

  return (
    <div className="mx-auto max-w-6xl">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-foreground">Homepage Sections</h1>
          <p className="text-sm text-muted-foreground mt-1">Manage sections displayed on the public homepage</p>
        </div>
        <Link to="/admin/sections/new">
          <Button className="gap-2">
            <Plus className="h-4 w-4" /> New Section
          </Button>
        </Link>
      </div>

      <div className="rounded-xl border border-border bg-card overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-border bg-muted/50">
                <th className="text-left px-4 py-3 font-medium text-muted-foreground">Name</th>
                <th className="text-left px-4 py-3 font-medium text-muted-foreground">Type</th>
                <th className="text-center px-4 py-3 font-medium text-muted-foreground">Order</th>
                <th className="text-center px-4 py-3 font-medium text-muted-foreground">Status</th>
                <th className="text-right px-4 py-3 font-medium text-muted-foreground">Actions</th>
              </tr>
            </thead>
            <tbody>
              {sections.length === 0 ? (
                <tr>
                  <td colSpan={5} className="px-4 py-12 text-center text-muted-foreground">
                    No sections yet. Create your first homepage section.
                  </td>
                </tr>
              ) : (
                sections.map((section) => (
                  <tr key={section.id} className="border-b border-border last:border-0 hover:bg-muted/30 transition-colors">
                    <td className="px-4 py-3">
                      <div className="font-medium text-foreground">{section.sectionName}</div>
                      <div className="text-xs text-muted-foreground mt-0.5 truncate max-w-xs">{section.title}</div>
                    </td>
                    <td className="px-4 py-3 text-muted-foreground">{typeLabel(section.sectionType)}</td>
                    <td className="px-4 py-3 text-center text-muted-foreground">{section.displayOrder}</td>
                    <td className="px-4 py-3 text-center">
                      <span className={`inline-flex items-center gap-1 rounded-full px-2 py-0.5 text-xs font-medium ${
                        section.active ? 'bg-green-500/10 text-green-600' : 'bg-gray-500/10 text-muted-foreground'
                      }`}>
                        {section.active ? 'Active' : 'Inactive'}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-right">
                      <div className="flex items-center justify-end gap-1">
                        <Link
                          to={`/admin/sections/${section.id}`}
                          className="flex h-8 w-8 items-center justify-center rounded-lg text-muted-foreground hover:bg-accent hover:text-accent-foreground transition-colors"
                        >
                          <Pencil className="h-4 w-4" />
                        </Link>
                        <button
                          onClick={() => handleDelete(section.id, section.sectionName)}
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
