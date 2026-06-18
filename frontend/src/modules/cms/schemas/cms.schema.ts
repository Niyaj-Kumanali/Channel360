import { z } from 'zod';

export const createSectionSchema = z.object({
  sectionName: z.string().min(1, 'Section name is required'),
  sectionType: z.string().min(1, 'Section type is required'),
  title: z.string().min(1, 'Title is required'),
  subtitle: z.string().optional(),
  description: z.string().optional(),
  imageUrl: z.string().optional(),
  buttonText: z.string().optional(),
  buttonUrl: z.string().optional(),
  displayOrder: z.coerce.number().min(0, 'Display order must be 0 or greater'),
  active: z.boolean().optional(),
  startDate: z.string().optional(),
  endDate: z.string().optional(),
});

export type CreateSectionFormData = z.infer<typeof createSectionSchema>;
