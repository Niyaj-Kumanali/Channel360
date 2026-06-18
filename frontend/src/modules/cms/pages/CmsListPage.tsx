import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Plus, Edit, Trash2, ToggleLeft, ToggleRight, ArrowUp, ArrowDown, Eye } from 'lucide-react';
import { useSections, useDeleteSection, useToggleSection, useReorderSections } from '../hooks/useCms';
import { Button } from '@/shared/components/ui/Button';
import { Badge } from '@/shared/components/ui/Badge';
import { Card, CardContent, CardHeader } from '@/shared/components/ui/Card';
import { Modal } from '@/shared/components/ui/Modal';
import type { HomepageSection } from '@/shared/types/cms.types';

export const CmsListPage: React.FC = () => {
  const navigate = useNavigate();
  const { data, isLoading } = useSections();
  const deleteMutation = useDeleteSection();
  const toggleMutation = useToggleSection();
  const reorderMutation = useReorderSections();

  const sections = data?.data || [];

  const [deleteModal, setDeleteModal] = useState<{ open: boolean; sectionId: number | null }>({ open: false, sectionId: null });
  const [previewModal, setPreviewModal] = useState<{ open: boolean; section: HomepageSection | null }>({ open: false, section: null });

  const moveUp = (index: number) => {
    if (index === 0) return;
    const ids = sections.map(s => s.id);
    [ids[index - 1], ids[index]] = [ids[index], ids[index - 1]];
    reorderMutation.mutate(ids);
  };

  const moveDown = (index: number) => {
    if (index === sections.length - 1) return;
    const ids = sections.map(s => s.id);
    [ids[index], ids[index + 1]] = [ids[index + 1], ids[index]];
    reorderMutation.mutate(ids);
  };

  const sectionTypeLabels: Record<string, string> = {
    HERO_BANNER: 'Hero Banner',
    ANNOUNCEMENT: 'Announcement',
    INFO_BLOCK: 'Info Block',
    RICH_TEXT: 'Rich Text',
    IMAGE_CARD: 'Image Card',
    PROMOTION_BANNER: 'Promotion Banner',
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Homepage CMS</h1>
          <p className="text-gray-500 mt-1">Manage homepage sections</p>
        </div>
        <Link to="/cms/new">
          <Button>
            <Plus className="h-4 w-4 mr-2" />
            Add Section
          </Button>
        </Link>
      </div>

      <Card>
        <CardContent className="p-0">
          {isLoading ? (
            <div className="p-12 text-center">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto" />
            </div>
          ) : sections.length === 0 ? (
            <div className="p-12 text-center text-gray-500">
              No sections found. Create your first section to get started.
            </div>
          ) : (
            <div className="divide-y divide-gray-200">
              {sections.map((section, index) => (
                <div key={section.id} className="flex items-center gap-4 p-4 hover:bg-gray-50 transition-colors">
                  <div className="flex flex-col gap-1">
                    <button onClick={() => moveUp(index)} className="p-1 hover:bg-gray-200 rounded" disabled={index === 0}>
                      <ArrowUp className="h-3 w-3 text-gray-400" />
                    </button>
                    <button onClick={() => moveDown(index)} className="p-1 hover:bg-gray-200 rounded" disabled={index === sections.length - 1}>
                      <ArrowDown className="h-3 w-3 text-gray-400" />
                    </button>
                  </div>
                  <span className="text-xs text-gray-400 w-6">{section.displayOrder}</span>
                  {section.imageUrl && (
                    <img src={section.imageUrl} alt="" className="w-16 h-12 object-cover rounded-lg" />
                  )}
                  <div className="flex-1 min-w-0">
                    <h3 className="text-sm font-medium text-gray-900 truncate">{section.title}</h3>
                    <p className="text-xs text-gray-500">{sectionTypeLabels[section.sectionType] || section.sectionType}</p>
                  </div>
                  <Badge variant={section.active ? 'success' : 'default'}>
                    {section.active ? 'Active' : 'Inactive'}
                  </Badge>
                  <div className="flex items-center gap-1">
                    <Button variant="ghost" size="sm" onClick={() => setPreviewModal({ open: true, section })}>
                      <Eye className="h-4 w-4" />
                    </Button>
                    <Button variant="ghost" size="sm" onClick={() => navigate(`/cms/${section.id}/edit`)}>
                      <Edit className="h-4 w-4" />
                    </Button>
                    <Button variant="ghost" size="sm" onClick={() => toggleMutation.mutate(section.id)}>
                      {section.active ? <ToggleRight className="h-4 w-4 text-green-600" /> : <ToggleLeft className="h-4 w-4 text-gray-400" />}
                    </Button>
                    <Button variant="ghost" size="sm" onClick={() => setDeleteModal({ open: true, sectionId: section.id })}>
                      <Trash2 className="h-4 w-4 text-red-500" />
                    </Button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>

      <Modal isOpen={deleteModal.open} onClose={() => setDeleteModal({ open: false, sectionId: null })} title="Delete Section" size="sm">
        <p className="text-gray-600 mb-6">Are you sure you want to delete this section?</p>
        <div className="flex justify-end gap-3">
          <Button variant="outline" onClick={() => setDeleteModal({ open: false, sectionId: null })}>Cancel</Button>
          <Button variant="destructive" onClick={() => { if (deleteModal.sectionId) deleteMutation.mutate(deleteModal.sectionId); setDeleteModal({ open: false, sectionId: null }); }}>
            Delete
          </Button>
        </div>
      </Modal>

      <Modal isOpen={previewModal.open} onClose={() => setPreviewModal({ open: false, section: null })} title="Section Preview" size="lg">
        {previewModal.section && (
          <div className="space-y-4">
            {previewModal.section.imageUrl && (
              <img src={previewModal.section.imageUrl} alt="" className="w-full h-48 object-cover rounded-lg" />
            )}
            <h3 className="text-xl font-bold">{previewModal.section.title}</h3>
            {previewModal.section.subtitle && <h4 className="text-lg text-gray-600">{previewModal.section.subtitle}</h4>}
            {previewModal.section.description && <p className="text-gray-700">{previewModal.section.description}</p>}
            {previewModal.section.buttonText && (
              <Button>{previewModal.section.buttonText}</Button>
            )}
          </div>
        )}
      </Modal>
    </div>
  );
};
