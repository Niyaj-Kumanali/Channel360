export interface HomepageSection {
  id: number;
  sectionName: string;
  sectionType: string;
  title: string;
  subtitle: string | null;
  description: string | null;
  imageUrl: string | null;
  buttonText: string | null;
  buttonUrl: string | null;
  displayOrder: number;
  active: boolean;
  startDate: string | null;
  endDate: string | null;
  createdBy: string | null;
  createdAt: string;
  updatedBy: string | null;
  updatedAt: string;
}

export interface HomepageSectionRequest {
  sectionName: string;
  sectionType: string;
  title: string;
  subtitle?: string | null;
  description?: string | null;
  imageUrl?: string | null;
  buttonText?: string | null;
  buttonUrl?: string | null;
  displayOrder: number;
  active?: boolean;
  startDate?: string | null;
  endDate?: string | null;
}

export interface HomepagePopup {
  id: number;
  title: string;
  description: string | null;
  imageUrl: string | null;
  ctaButtonText: string | null;
  ctaUrl: string | null;
  priority: number;
  active: boolean;
  startDate: string | null;
  endDate: string | null;
  createdBy: string | null;
  createdAt: string;
  updatedBy: string | null;
  updatedAt: string;
}

export interface HomepagePopupRequest {
  title: string;
  description?: string | null;
  imageUrl?: string | null;
  ctaButtonText?: string | null;
  ctaUrl?: string | null;
  priority: number;
  active?: boolean;
  startDate?: string | null;
  endDate?: string | null;
}

export const SECTION_TYPES = [
  { value: 'hero_banner', label: 'Hero Banner' },
  { value: 'product_journey', label: 'Product Journey' },
  { value: 'platform_capabilities', label: 'Platform Capabilities' },
  { value: 'benefits', label: 'Benefits' },
  { value: 'contact', label: 'Contact' },
  { value: 'footer', label: 'Footer' },
  { value: 'announcement', label: 'Announcement' },
  { value: 'info_block', label: 'Information Block' },
  { value: 'promotion', label: 'Promotion' },
  { value: 'image_card', label: 'Image Card' },
  { value: 'rich_content', label: 'Rich Content' },
] as const;
