import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Plus, Edit, Trash2, ToggleLeft, ToggleRight } from 'lucide-react';
import { usePopups, useDeletePopup, useTogglePopup } from '../hooks/usePopups';
import { Button } from '@/shared/components/ui/Button';
import { Badge } from '@/shared/components/ui/Badge';
import { Card, CardContent } from '@/shared/components/ui/Card';
import { Modal } from '@/shared/components/ui/Modal';
import type { HomepagePopup } from '@/shared/types/popup.types';

export const PopupListPage: React.FC = () => {
  const navigate = useNavigate();
  const { data, isLoading } = usePopups();
  const deleteMutation = useDeletePopup();
  const toggleMutation = useTogglePopup();

  const popups = data?.data || [];
  const [deleteModal, setDeleteModal] = useState<{ open: boolean; popupId: number | null }>({ open: false, popupId: null });

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Popups</h1>
          <p className="text-gray-500 mt-1">Manage homepage popups</p>
        </div>
        <Link to="/popups/new">
          <Button>
            <Plus className="h-4 w-4 mr-2" />
            Add Popup
          </Button>
        </Link>
      </div>

      <Card>
        <CardContent className="p-0">
          {isLoading ? (
            <div className="p-12 text-center">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto" />
            </div>
          ) : popups.length === 0 ? (
            <div className="p-12 text-center text-gray-500">No popups found.</div>
          ) : (
            <div className="divide-y divide-gray-200">
              {popups.map((popup) => (
                <div key={popup.id} className="flex items-center gap-4 p-4 hover:bg-gray-50 transition-colors">
                  {popup.imageUrl && (
                    <img src={popup.imageUrl} alt="" className="w-16 h-12 object-cover rounded-lg" />
                  )}
                  <div className="flex-1 min-w-0">
                    <h3 className="text-sm font-medium text-gray-900 truncate">{popup.title}</h3>
                    <p className="text-xs text-gray-500">Priority: {popup.priority}</p>
                  </div>
                  <Badge variant={popup.active ? 'success' : 'default'}>
                    {popup.active ? 'Active' : 'Inactive'}
                  </Badge>
                  <div className="flex items-center gap-1">
                    <Button variant="ghost" size="sm" onClick={() => navigate(`/popups/${popup.id}/edit`)}>
                      <Edit className="h-4 w-4" />
                    </Button>
                    <Button variant="ghost" size="sm" onClick={() => toggleMutation.mutate(popup.id)}>
                      {popup.active ? <ToggleRight className="h-4 w-4 text-green-600" /> : <ToggleLeft className="h-4 w-4 text-gray-400" />}
                    </Button>
                    <Button variant="ghost" size="sm" onClick={() => setDeleteModal({ open: true, popupId: popup.id })}>
                      <Trash2 className="h-4 w-4 text-red-500" />
                    </Button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>

      <Modal isOpen={deleteModal.open} onClose={() => setDeleteModal({ open: false, popupId: null })} title="Delete Popup" size="sm">
        <p className="text-gray-600 mb-6">Are you sure?</p>
        <div className="flex justify-end gap-3">
          <Button variant="outline" onClick={() => setDeleteModal({ open: false, popupId: null })}>Cancel</Button>
          <Button variant="destructive" onClick={() => { if (deleteModal.popupId) deleteMutation.mutate(deleteModal.popupId); setDeleteModal({ open: false, popupId: null }); }}>
            Delete
          </Button>
        </div>
      </Modal>
    </div>
  );
};
