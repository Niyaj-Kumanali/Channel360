import React, { useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Save } from 'lucide-react';
import { useForm } from 'react-hook-form';
import toast from 'react-hot-toast';
import { cmsApi } from '@/features/cms/api/cms.api';
import { SECTION_TYPES } from '@/features/cms/types/cms.types';
import type { HomepageSectionRequest } from '@/features/cms/types/cms.types';
import { Button } from '@/components/ui/Button';

export const SectionFormPage: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = id && id !== 'new';

  const { register, handleSubmit, reset, formState: { isSubmitting } } = useForm<HomepageSectionRequest>({
    defaultValues: {
      displayOrder: 0,
      active: true,
    },
  });

  useEffect(() => {
    if (isEdit) {
      cmsApi.getSection(Number(id)).then((res) => {
        if (res.success) {
          const { ...data } = res.data;
          reset(data);
        }
      }).catch(() => {
        toast.error('Failed to load section');
        navigate('/admin/sections');
      });
    }
  }, [id]);

  const onSubmit = async (data: HomepageSectionRequest) => {
    try {
      if (isEdit) {
        const res = await cmsApi.updateSection(Number(id), data);
        if (res.success) toast.success('Section updated');
      } else {
        const res = await cmsApi.createSection(data);
        if (res.success) toast.success('Section created');
      }
      navigate('/admin/sections');
    } catch {
      toast.error('Failed to save section');
    }
  };

  return (
    <div className="mx-auto max-w-3xl">
      <div className="flex items-center gap-4 mb-6">
        <button
          onClick={() => navigate('/admin/sections')}
          className="flex h-9 w-9 items-center justify-center rounded-lg text-muted-foreground hover:bg-accent hover:text-accent-foreground transition-colors"
        >
          <ArrowLeft className="h-4 w-4" />
        </button>
        <div>
          <h1 className="text-2xl font-bold text-foreground">{isEdit ? 'Edit Section' : 'New Section'}</h1>
          <p className="text-sm text-muted-foreground mt-1">
            {isEdit ? 'Update the homepage section details below' : 'Create a new section for the public homepage'}
          </p>
        </div>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-6" noValidate>
        <div className="rounded-xl border border-border bg-card p-6 space-y-5">
          <div className="grid gap-5 sm:grid-cols-2">
            <div className="space-y-1.5">
              <label className="text-sm font-medium text-foreground">
                Section Name <span className="text-red-500">*</span>
              </label>
              <input
                {...register('sectionName', { required: true })}
                className="flex h-10 w-full rounded-lg border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
                placeholder="e.g. Main Hero"
              />
            </div>
            <div className="space-y-1.5">
              <label className="text-sm font-medium text-foreground">
                Section Type <span className="text-red-500">*</span>
              </label>
              <select
                {...register('sectionType', { required: true })}
                className="flex h-10 w-full rounded-lg border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
              >
                <option value="">Select type...</option>
                {SECTION_TYPES.map(t => (
                  <option key={t.value} value={t.value}>{t.label}</option>
                ))}
              </select>
            </div>
          </div>

          <div className="space-y-1.5">
            <label className="text-sm font-medium text-foreground">
              Title <span className="text-red-500">*</span>
            </label>
            <input
              {...register('title', { required: true })}
              className="flex h-10 w-full rounded-lg border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
              placeholder="Section heading text"
            />
          </div>

          <div className="space-y-1.5">
            <label className="text-sm font-medium text-foreground">Subtitle</label>
            <input
              {...register('subtitle')}
              className="flex h-10 w-full rounded-lg border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
              placeholder="Optional subheading"
            />
          </div>

          <div className="space-y-1.5">
            <label className="text-sm font-medium text-foreground">Description</label>
            <textarea
              {...register('description')}
              rows={4}
              className="flex w-full rounded-lg border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 resize-y"
              placeholder="Optional description text"
            />
          </div>

          <div className="grid gap-5 sm:grid-cols-2">
            <div className="space-y-1.5">
              <label className="text-sm font-medium text-foreground">Image URL</label>
              <input
                {...register('imageUrl')}
                className="flex h-10 w-full rounded-lg border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
                placeholder="https://..."
              />
            </div>
            <div className="space-y-1.5">
              <label className="text-sm font-medium text-foreground">Display Order</label>
              <input
                type="number"
                {...register('displayOrder', { valueAsNumber: true })}
                className="flex h-10 w-full rounded-lg border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
              />
            </div>
          </div>

          <div className="grid gap-5 sm:grid-cols-2">
            <div className="space-y-1.5">
              <label className="text-sm font-medium text-foreground">Button Text</label>
              <input
                {...register('buttonText')}
                className="flex h-10 w-full rounded-lg border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
                placeholder="e.g. Learn More"
              />
            </div>
            <div className="space-y-1.5">
              <label className="text-sm font-medium text-foreground">Button URL</label>
              <input
                {...register('buttonUrl')}
                className="flex h-10 w-full rounded-lg border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
                placeholder="https://..."
              />
            </div>
          </div>

          <div className="grid gap-5 sm:grid-cols-2">
            <div className="space-y-1.5">
              <label className="text-sm font-medium text-foreground">Start Date</label>
              <input
                type="datetime-local"
                {...register('startDate')}
                className="flex h-10 w-full rounded-lg border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
              />
            </div>
            <div className="space-y-1.5">
              <label className="text-sm font-medium text-foreground">End Date</label>
              <input
                type="datetime-local"
                {...register('endDate')}
                className="flex h-10 w-full rounded-lg border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
              />
            </div>
          </div>

          <label className="flex items-center gap-3 cursor-pointer">
            <input
              type="checkbox"
              {...register('active')}
              className="h-4 w-4 rounded border-border text-primary focus:ring-primary"
            />
            <div>
              <span className="text-sm font-medium text-foreground">Active</span>
              <p className="text-xs text-muted-foreground">Section will be visible on the public homepage</p>
            </div>
          </label>
        </div>

        <div className="flex items-center justify-end gap-3">
          <Button type="button" variant="outline" onClick={() => navigate('/admin/sections')}>
            Cancel
          </Button>
          <Button type="submit" isLoading={isSubmitting} className="gap-2">
            <Save className="h-4 w-4" /> {isEdit ? 'Update' : 'Create'} Section
          </Button>
        </div>
      </form>
    </div>
  );
};
