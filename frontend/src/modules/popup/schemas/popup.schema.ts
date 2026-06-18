import { z } from 'zod';

export const createPopupSchema = z.object({
  title: z.string().min(1, 'Title is required'),
  description: z.string().optional(),
  imageUrl: z.string().optional(),
  ctaButtonText: z.string().optional(),
  ctaUrl: z.string().optional(),
  priority: z.coerce.number().min(0, 'Priority must be 0 or greater'),
  active: z.boolean().optional(),
  startDate: z.string().optional(),
  endDate: z.string().optional(),
});

export type CreatePopupFormData = z.infer<typeof createPopupSchema>;
