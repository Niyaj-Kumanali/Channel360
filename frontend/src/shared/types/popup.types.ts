export interface HomepagePopup {
  id: number;
  title: string;
  description: string;
  imageUrl: string;
  ctaButtonText: string;
  ctaUrl: string;
  priority: number;
  active: boolean;
  startDate: string;
  endDate: string;
}

export interface CreatePopupRequest {
  title: string;
  description?: string;
  imageUrl?: string;
  ctaButtonText?: string;
  ctaUrl?: string;
  priority: number;
  active?: boolean;
  startDate?: string;
  endDate?: string;
}

export interface UpdatePopupRequest extends Partial<CreatePopupRequest> {}
