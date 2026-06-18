import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft } from 'lucide-react';
import { createSectionSchema, type CreateSectionFormData } from '../schemas/cms.schema';
import { useCreateSection } from '../hooks/useCms';
import { SECTION_TYPES } from '@/shared/constants';
import { Input } from '@/shared/components/ui/Input';
import { Select } from '@/shared/components/ui/Select';
import { Button } from '@/shared/components/ui/Button';
import { Card, CardContent, CardHeader } from '@/shared/components/ui/Card';

export const CmsCreatePage: React.FC = () => {
  const navigate = useNavigate();
  const createMutation = useCreateSection();

  const { register, handleSubmit, formState: { errors } } = useForm<CreateSectionFormData>({
    resolver: zodResolver(createSectionSchema),
    defaultValues: { displayOrder: 0, active: true },
  });

  const onSubmit = (data: CreateSectionFormData) => {
    createMutation.mutate(data, {
      onSuccess: () => navigate('/cms'),
    });
  };

  return (
    <div>
      <div className="mb-6">
        <Button variant="ghost" onClick={() => navigate('/cms')}>
          <ArrowLeft className="h-4 w-4 mr-2" />
          Back to CMS
        </Button>
      </div>
      <Card className="max-w-2xl">
        <CardHeader>
          <h2 className="text-xl font-bold text-gray-900">Create Section</h2>
          <p className="text-gray-500 text-sm mt-1">Add a new homepage section</p>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <Input id="sectionName" label="Section Name" error={errors.sectionName?.message} {...register('sectionName')} />
              <Select
                id="sectionType"
                label="Section Type"
                placeholder="Select type"
                options={Object.values(SECTION_TYPES).map(t => ({ value: t, label: t.replace(/_/g, ' ') }))}
                {...register('sectionType')}
                error={errors.sectionType?.message}
              />
            </div>
            <Input id="title" label="Title" error={errors.title?.message} {...register('title')} />
            <Input id="subtitle" label="Subtitle" {...register('subtitle')} />
            <div className="grid grid-cols-2 gap-4">
              <Input id="imageUrl" label="Image URL" {...register('imageUrl')} />
              <Input id="displayOrder" label="Display Order" type="number" error={errors.displayOrder?.message} {...register('displayOrder')} />
            </div>
            <Input id="buttonText" label="Button Text" {...register('buttonText')} />
            <Input id="buttonUrl" label="Button URL" {...register('buttonUrl')} />
            <div className="grid grid-cols-2 gap-4">
              <Input id="startDate" label="Start Date" type="datetime-local" {...register('startDate')} />
              <Input id="endDate" label="End Date" type="datetime-local" {...register('endDate')} />
            </div>
            <div className="flex justify-end gap-3 pt-4">
              <Button variant="outline" onClick={() => navigate('/cms')}>Cancel</Button>
              <Button type="submit" isLoading={createMutation.isPending}>Create Section</Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
};
