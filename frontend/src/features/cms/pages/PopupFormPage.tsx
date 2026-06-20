import React, { useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Save } from 'lucide-react';
import { useForm } from 'react-hook-form';
import toast from 'react-hot-toast';
import { cmsApi } from '@/features/cms/api/cms.api';
import type { HomepagePopupRequest } from '@/features/cms/types/cms.types';
import { Button } from '@/components/ui/Button';

export const PopupFormPage: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = id && id !== 'new';

  const { register, handleSubmit, reset, formState: { isSubmitting } } = useForm<HomepagePopupRequest>({
    defaultValues: {
      priority: 0,
      active: true,
    },
  });

  useEffect(() => {
    if (isEdit) {
      cmsApi.getPopup(Number(id)).then((res) => {
        if (res.success) {
          const { ...data } = res.data;
          reset(data);
        }
      }).catch(() => {
        toast.error('Failed to load popup');
        navigate('/admin/popups');
      });
    }
  }, [id]);

  const onSubmit = async (data: HomepagePopupRequest) => {
    try {
      if (isEdit) {
        const res = await cmsApi.updatePopup(Number(id), data);
        if (res.success) toast.success('Popup updated');
      } else {
        const res = await cmsApi.createPopup(data);
        if (res.success) toast.success('Popup created');
      }
      navigate('/admin/popups');
    } catch {
      toast.error('Failed to save popup');
    }
  };

  return (
    <div className="mx-auto max-w-3xl">
      <div className="flex items-center gap-4 mb-6">
        <button
          onClick={() => navigate('/admin/popups')}
          className="flex h-9 w-9 items-center justify-center rounded-lg text-muted-foreground hover:bg-accent hover:text-accent-foreground transition-colors"
        >
          <ArrowLeft className="h-4 w-4" />
        </button>
        <div>
          <h1 className="text-2xl font-bold text-foreground">{isEdit ? 'Edit Popup' : 'New Popup'}</h1>
          <p className="text-sm text-muted-foreground mt-1">
            {isEdit ? 'Update the popup details below' : 'Create a new popup for the homepage'}
          </p>
        </div>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-6" noValidate>
        <div className="rounded-xl border border-border bg-card p-6 space-y-5">
          <div className="space-y-1.5">
            <label className="text-sm font-medium text-foreground">
              Title <span className="text-red-500">*</span>
            </label>
            <input
              {...register('title', { required: true })}
              className="flex h-10 w-full rounded-lg border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
              placeholder="e.g. Real-Time Analytics Now Available"
            />
            <p className="text-xs text-muted-foreground">Headline that appears at the top of the popup modal.</p>
          </div>

          <div className="space-y-1.5">
            <label className="text-sm font-medium text-foreground">Description</label>
            <textarea
              {...register('description')}
              rows={4}
              className="flex w-full rounded-lg border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 resize-y"
              placeholder="e.g. Track every product movement across your distribution network with our new interactive dashboards. Monitor channel performance and identify bottlenecks in real time."
            />
            <p className="text-xs text-muted-foreground">Body text shown below the title. Supports plain text only.</p>
          </div>

          <div className="space-y-1.5">
            <label className="text-sm font-medium text-foreground">Image URL</label>
            <input
              {...register('imageUrl')}
              className="flex h-10 w-full rounded-lg border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
              placeholder="https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=800"
            />
            <p className="text-xs text-muted-foreground">Optional image shown at the top of the popup. Use a publicly accessible URL.</p>
          </div>

          <div className="grid gap-5 sm:grid-cols-2">
            <div className="space-y-1.5">
              <label className="text-sm font-medium text-foreground">CTA Button Text</label>
              <input
                {...register('ctaButtonText')}
                className="flex h-10 w-full rounded-lg border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
                placeholder="e.g. Explore Dashboard"
              />
              <p className="text-xs text-muted-foreground">Text on the action button. Button shows only if text or a URL is provided.</p>
            </div>
            <div className="space-y-1.5">
              <label className="text-sm font-medium text-foreground">CTA URL</label>
              <input
                {...register('ctaUrl')}
                className="flex h-10 w-full rounded-lg border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
                placeholder="e.g. /dashboard or https://..."
              />
              <p className="text-xs text-muted-foreground">Where the button links to. Leave empty to just close the popup on click.</p>
            </div>
          </div>

          <div className="grid gap-5 sm:grid-cols-2">
            <div className="space-y-1.5">
              <label className="text-sm font-medium text-foreground">Priority</label>
              <input
                type="number"
                {...register('priority', { valueAsNumber: true })}
                className="flex h-10 w-full rounded-lg border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
              />
              <p className="text-xs text-muted-foreground">Higher number = shows first. Ties broken by newest first. Use 0 for default.</p>
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
              <p className="text-xs text-muted-foreground">Leave empty to show immediately. Popup hides until this time if set.</p>
            </div>
            <div className="space-y-1.5">
              <label className="text-sm font-medium text-foreground">End Date</label>
              <input
                type="datetime-local"
                {...register('endDate')}
                className="flex h-10 w-full rounded-lg border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
              />
              <p className="text-xs text-muted-foreground">Popup automatically hides after this time. Leave empty for no expiry.</p>
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
              <p className="text-xs text-muted-foreground">Popup will be shown on the homepage</p>
            </div>
          </label>
        </div>

        <div className="flex items-center justify-end gap-3">
          <Button type="button" variant="outline" onClick={() => navigate('/admin/popups')}>
            Cancel
          </Button>
          <Button type="submit" isLoading={isSubmitting} className="gap-2">
            <Save className="h-4 w-4" /> {isEdit ? 'Update' : 'Create'} Popup
          </Button>
        </div>
      </form>
    </div>
  );
};
