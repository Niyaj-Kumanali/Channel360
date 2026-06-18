import React, { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useNavigate, useParams } from 'react-router-dom';
import { ArrowLeft } from 'lucide-react';
import { createSectionSchema, type CreateSectionFormData } from '../schemas/cms.schema';
import { useSection, useUpdateSection } from '../hooks/useCms';
import { SECTION_TYPES } from '@/shared/constants';
import { Input } from '@/shared/components/ui/Input';
import { Select } from '@/shared/components/ui/Select';
import { Button } from '@/shared/components/ui/Button';
import { Card, CardContent, CardHeader } from '@/shared/components/ui/Card';

export const CmsEditPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const sectionId = Number(id);
  const { data: sectionData, isLoading } = useSection(sectionId);
  const updateMutation = useUpdateSection();

  const { register, handleSubmit, formState: { errors }, reset } = useForm<CreateSectionFormData>({
    resolver: zodResolver(createSectionSchema),
  });

  useEffect(() => {
    if (sectionData?.data) {
      const s = sectionData.data;
      reset({
        sectionName: s.sectionName,
        sectionType: s.sectionType,
        title: s.title,
        subtitle: s.subtitle || '',
        description: s.description || '',
        imageUrl: s.imageUrl || '',
        buttonText: s.buttonText || '',
        buttonUrl: s.buttonUrl || '',
        displayOrder: s.displayOrder,
        active: s.active,
        startDate: s.startDate || '',
        endDate: s.endDate || '',
      });
    }
  }, [sectionData, reset]);

  const onSubmit = (data: CreateSectionFormData) => {
    updateMutation.mutate({ id: sectionId, data }, {
      onSuccess: () => navigate('/cms'),
    });
  };

  if (isLoading) {
    return <div className="flex justify-center py-12"><div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary" /></div>;
  }

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
          <h2 className="text-xl font-bold text-gray-900">Edit Section</h2>
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
              <Input id="displayOrder" label="Display Order" type="number" {...register('displayOrder')} />
            </div>
            <Input id="buttonText" label="Button Text" {...register('buttonText')} />
            <Input id="buttonUrl" label="Button URL" {...register('buttonUrl')} />
            <div className="grid grid-cols-2 gap-4">
              <Input id="startDate" label="Start Date" type="datetime-local" {...register('startDate')} />
              <Input id="endDate" label="End Date" type="datetime-local" {...register('endDate')} />
            </div>
            <div className="flex justify-end gap-3 pt-4">
              <Button variant="outline" onClick={() => navigate('/cms')}>Cancel</Button>
              <Button type="submit" isLoading={updateMutation.isPending}>Save Changes</Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
};
