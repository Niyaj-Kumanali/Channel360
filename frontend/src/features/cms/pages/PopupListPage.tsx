import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Plus, Pencil, Trash2 } from 'lucide-react';
import toast from 'react-hot-toast';
import { cmsApi } from '@/features/cms/api/cms.api';
import type { HomepagePopup } from '@/features/cms/types/cms.types';
import { Button } from '@/components/ui/Button';
import { Loader } from '@/components/ui/Loader';

export const PopupListPage: React.FC = () => {
  const [popups, setPopups] = useState<HomepagePopup[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchPopups = async () => {
    try {
      const res = await cmsApi.getAllPopups();
      if (res.success) setPopups(res.data);
    } catch {
      toast.error('Failed to load popups');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPopups();
  }, []);

  const handleDelete = async (id: number, title: string) => {
    if (!window.confirm(`Delete popup "${title}"?`)) return;
    try {
      const res = await cmsApi.deletePopup(id);
      if (res.success) {
        toast.success('Popup deleted');
        fetchPopups();
      }
    } catch {
      toast.error('Failed to delete popup');
    }
  };

  if (loading) {
    return <div className="flex items-center justify-center min-h-[calc(100vh-10rem)]"><Loader size="lg" /></div>;
  }

  return (
    <div className="mx-auto max-w-6xl">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-foreground">Popups</h1>
          <p className="text-sm text-muted-foreground mt-1">Manage popup notifications displayed on the homepage</p>
        </div>
        <Link to="/admin/popups/new">
          <Button className="gap-2">
            <Plus className="h-4 w-4" /> New Popup
          </Button>
        </Link>
      </div>

      <div className="rounded-xl border border-border bg-card overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-border bg-muted/50">
                <th className="text-left px-4 py-3 font-medium text-muted-foreground">Title</th>
                <th className="text-center px-4 py-3 font-medium text-muted-foreground">Priority</th>
                <th className="text-center px-4 py-3 font-medium text-muted-foreground">Status</th>
                <th className="text-right px-4 py-3 font-medium text-muted-foreground">Actions</th>
              </tr>
            </thead>
            <tbody>
              {popups.length === 0 ? (
                <tr>
                  <td colSpan={4} className="px-4 py-12 text-center text-muted-foreground">
                    No popups yet. Create your first popup.
                  </td>
                </tr>
              ) : (
                popups.map((popup) => (
                  <tr key={popup.id} className="border-b border-border last:border-0 hover:bg-muted/30 transition-colors">
                    <td className="px-4 py-3">
                      <div className="font-medium text-foreground">{popup.title}</div>
                      {popup.description && (
                        <div className="text-xs text-muted-foreground mt-0.5 truncate max-w-xs">{popup.description}</div>
                      )}
                    </td>
                    <td className="px-4 py-3 text-center text-muted-foreground">{popup.priority}</td>
                    <td className="px-4 py-3 text-center">
                      <span className={`inline-flex items-center gap-1 rounded-full px-2 py-0.5 text-xs font-medium ${
                        popup.active ? 'bg-green-500/10 text-green-600' : 'bg-gray-500/10 text-muted-foreground'
                      }`}>
                        {popup.active ? 'Active' : 'Inactive'}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-right">
                      <div className="flex items-center justify-end gap-1">
                        <Link
                          to={`/admin/popups/${popup.id}`}
                          className="flex h-8 w-8 items-center justify-center rounded-lg text-muted-foreground hover:bg-accent hover:text-accent-foreground transition-colors"
                        >
                          <Pencil className="h-4 w-4" />
                        </Link>
                        <button
                          onClick={() => handleDelete(popup.id, popup.title)}
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
