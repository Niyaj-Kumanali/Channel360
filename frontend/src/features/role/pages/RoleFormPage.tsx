import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Save } from 'lucide-react';
import toast from 'react-hot-toast';
import { roleApi } from '@/features/role/api/role.api';
import { Loader } from '@/components/ui/Loader';
import { Button } from '@/components/ui/Button';

export const RoleFormPage: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = id && id !== 'new';

  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    const init = async () => {
      try {
        if (isEdit) {
          const roleRes = await roleApi.getById(Number(id));
          if (roleRes.success) {
            setName(roleRes.data.name);
            setDescription(roleRes.data.description || '');
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

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) {
      toast.error('Role name is required');
      return;
    }

    setSubmitting(true);
    try {
      const payload = { name: name.trim(), description: description.trim() };
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
    <div>
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
          {isEdit ? 'Update role details' : 'Create a new role'}
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
