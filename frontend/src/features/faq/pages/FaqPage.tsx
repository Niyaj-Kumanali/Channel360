import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { ArrowLeft } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { FaqSection } from '@/features/home/components/sections/FaqSection';
import { homeApi } from '@/features/home/api/home.api';
import type { HomepageSection } from '@/features/cms/types/cms.types';

const fallbackSection: HomepageSection = {
  id: 0, sectionName: 'FAQ', sectionType: 'faq',
  title: 'Frequently Asked Questions',
  subtitle: null,
  description: '<h3>How does onboarding work?</h3><p>Our team guides you through every step, from data migration to partner training.</p><h3>Can I integrate with my existing ERP?</h3><p>Yes, Channel360 supports integrations with major ERP and CRM systems.</p><h3>What kind of support is available?</h3><p>24/7 technical support with dedicated account managers for enterprise plans.</p><h3>Is my data secure on the platform?</h3><p>We use enterprise-grade encryption, SOC 2 compliance, and role-based access controls to keep your data safe.</p><h3>Can I customize the platform for my workflow?</h3><p>Yes, Channel360 supports custom fields, workflows, and modules tailored to your distribution model.</p><h3>What does pricing look like for enterprises?</h3><p>We offer tiered pricing based on partner count and transaction volume. Contact our sales team for a custom quote.</p>',
  imageUrl: null, buttonText: null, buttonUrl: null,
  displayOrder: 0, active: true, startDate: null, endDate: null,
  createdBy: null, createdAt: '', updatedBy: null, updatedAt: '',
};

export const FaqPage: React.FC = () => {
  const [faqSection, setFaqSection] = useState<HomepageSection>(fallbackSection);

  useEffect(() => {
    homeApi.getPublishedSections().then((res) => {
      if (res.success && res.data.length > 0) {
        const found = res.data.find((s) => s.sectionType === 'faq');
        if (found) setFaqSection(found);
      }
    }).catch(() => {});
  }, []);

  return (
    <div>
      <div className="relative border-b border-border">
        <div className="mx-auto flex max-w-7xl items-center px-4 py-4 sm:px-6 lg:px-8">
          <Link to="/">
            <Button variant="ghost" size="sm" className="gap-2">
              <ArrowLeft className="h-4 w-4" />
              Back to Home
            </Button>
          </Link>
        </div>
      </div>
      <FaqSection section={faqSection} isFullPage />
    </div>
  );
};
