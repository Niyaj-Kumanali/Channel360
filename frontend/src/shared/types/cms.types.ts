export interface HomepageSection {
  id: number;
  sectionName: string;
  sectionType: string;
  title: string;
  subtitle: string;
  description: string;
  imageUrl: string;
  buttonText: string;
  buttonUrl: string;
  displayOrder: number;
  active: boolean;
  startDate: string;
  endDate: string;
}

export interface CreateSectionRequest {
  sectionName: string;
  sectionType: string;
  title: string;
  subtitle?: string;
  description?: string;
  imageUrl?: string;
  buttonText?: string;
  buttonUrl?: string;
  displayOrder: number;
  active?: boolean;
  startDate?: string;
  endDate?: string;
}

export interface UpdateSectionRequest extends Partial<CreateSectionRequest> {}
