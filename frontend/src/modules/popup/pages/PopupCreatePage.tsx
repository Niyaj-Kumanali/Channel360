import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft } from 'lucide-react';
import { createPopupSchema, type CreatePopupFormData } from '../schemas/popup.schema';
import { useCreatePopup } from '../hooks/usePopups';
import { Input } from '@/shared/components/ui/Input';
import { Button } from '@/shared/components/ui/Button';
import { Card, CardContent, CardHeader } from '@/shared/components/ui/Card';

export const PopupCreatePage: React.FC = () => {
  const navigate = useNavigate();
  const createMutation = useCreatePopup();

  const { register, handleSubmit, formState: { errors } } = useForm<CreatePopupFormData>({
    resolver: zodResolver(createPopupSchema),
    defaultValues: { priority: 0, active: true },
  });

  const onSubmit = (data: CreatePopupFormData) => {
    createMutation.mutate(data, { onSuccess: () => navigate('/popups') });
  };

  return (
    <div>
      <div className="mb-6">
        <Button variant="ghost" onClick={() => navigate('/popups')}>
          <ArrowLeft className="h-4 w-4 mr-2" />
          Back to Popups
        </Button>
      </div>
      <Card className="max-w-2xl">
        <CardHeader>
          <h2 className="text-xl font-bold text-gray-900">Create Popup</h2>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <Input id="title" label="Title" error={errors.title?.message} {...register('title')} />
            <Input id="description" label="Description" {...register('description')} />
            <div className="grid grid-cols-2 gap-4">
              <Input id="imageUrl" label="Image URL" {...register('imageUrl')} />
              <Input id="priority" label="Priority" type="number" error={errors.priority?.message} {...register('priority')} />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <Input id="ctaButtonText" label="CTA Button Text" {...register('ctaButtonText')} />
              <Input id="ctaUrl" label="CTA URL" {...register('ctaUrl')} />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <Input id="startDate" label="Start Date" type="datetime-local" {...register('startDate')} />
              <Input id="endDate" label="End Date" type="datetime-local" {...register('endDate')} />
            </div>
            <div className="flex justify-end gap-3 pt-4">
              <Button variant="outline" onClick={() => navigate('/popups')}>Cancel</Button>
              <Button type="submit" isLoading={createMutation.isPending}>Create Popup</Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
};
