import React from 'react';
import type { HomepageSection } from '@/features/cms/types/cms.types';
import { HeroBannerSection } from './HeroBannerSection';
import { ProductJourneySection } from './ProductJourneySection';
import { BusinessAreasSection } from './BusinessAreasSection';
import { BenefitsSection } from './BenefitsSection';
import { CtaSection } from './CtaSection';
import { AnnouncementSection } from './AnnouncementSection';
import { InfoBlockSection } from './InfoBlockSection';
import { PromotionSection } from './PromotionSection';
import { ImageCardSection } from './ImageCardSection';
import { RichContentSection } from './RichContentSection';

interface Props {
  section: HomepageSection;
}

const renderers: Record<string, React.FC<{ section: HomepageSection }>> = {
  hero_banner: HeroBannerSection,
  product_journey: ProductJourneySection,
  business_areas: BusinessAreasSection,
  benefits: BenefitsSection,
  cta: CtaSection,
  announcement: AnnouncementSection,
  info_block: InfoBlockSection,
  promotion: PromotionSection,
  image_card: ImageCardSection,
  rich_content: RichContentSection,
};

export const SectionRenderer: React.FC<Props> = ({ section }) => {
  const Component = renderers[section.sectionType];
  if (!Component) return null;
  return <Component section={section} />;
};
