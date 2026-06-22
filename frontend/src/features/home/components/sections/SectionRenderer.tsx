import React from 'react';
import type { HomepageSection } from '@/features/cms/types/cms.types';
import { HeroBannerSection } from './HeroBannerSection';
import { ProductJourneySection } from './ProductJourneySection';
import { BenefitsSection } from './BenefitsSection';
import { PlatformCapabilitiesSection } from './PlatformCapabilitiesSection';
import { AnnouncementSection } from './AnnouncementSection';
import { InfoBlockSection } from './InfoBlockSection';
import { PromotionSection } from './PromotionSection';
import { ImageCardSection } from './ImageCardSection';
import { RichContentSection } from './RichContentSection';
import ContactSection from './ContactSection';
import { Footer } from '@/features/home/components/Footer';

interface Props {
  section: HomepageSection;
}

const renderers: Record<string, React.FC<{ section: HomepageSection }>> = {
  hero_banner: HeroBannerSection,
  product_journey: ProductJourneySection,
  benefits: BenefitsSection,
  platform_capabilities: PlatformCapabilitiesSection,
  contact: ContactSection,
  footer: ({ section }) => <Footer section={section} />,
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
