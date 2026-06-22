import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/Button';
import { Logo } from '@/components/ui/Logo';
import { useTheme } from '@/app/hooks/useTheme';
import { homeApi } from '@/features/home/api/home.api';
import { SectionRenderer } from '@/features/home/components/sections/SectionRenderer';
import { PopupModal } from '@/features/home/components/PopupModal';
import type { HomepageSection, HomepagePopup } from '@/features/cms/types/cms.types';

const staticSections: HomepageSection[] = [
  {
    id: 0, sectionName: 'Hero Banner', sectionType: 'hero_banner',
    title: 'Complete Visibility Across Your Channel Ecosystem',
    subtitle: null,
    description: 'Track the complete lifecycle of products across your distribution network, from channel entry to end-customer engagement and activation.',
    imageUrl: null, buttonText: 'Access Platform', buttonUrl: '/login',
    displayOrder: 1, active: true, startDate: null, endDate: null,
    createdBy: null, createdAt: '', updatedBy: null, updatedAt: '',
  },
  {
    id: 0, sectionName: 'Product Journey', sectionType: 'product_journey',
    title: 'The Product Journey',
    subtitle: 'Follow every product from manufacturer through distribution to end-customer activation.',
    description: '[{"title":"Manufacturer","description":"Products enter the channel network from manufacturers and suppliers."},{"title":"Distributor","description":"Distributors receive and forward products to channel partners."},{"title":"Channel Partner","description":"Partners sell products to end customers and manage local inventory."},{"title":"End Customer","description":"Customers purchase products through the partner network."},{"title":"Activation","description":"Products are activated and linked back to their channel journey."}]',
    imageUrl: null, buttonText: null, buttonUrl: null,
    displayOrder: 2, active: true, startDate: null, endDate: null,
    createdBy: null, createdAt: '', updatedBy: null, updatedAt: '',
  },
  {
    id: 0, sectionName: 'Platform Capabilities', sectionType: 'platform_capabilities',
    title: 'Platform Capabilities',
    subtitle: 'Powerful modules purpose-built for end-to-end channel ecosystem management.',
    description: '[{"title":"Channel Analytics","description":"Real-time dashboards, reports, and actionable insights across the entire channel network."},{"title":"Partner Lifecycle","description":"Streamlined onboarding, performance tracking, and relationship management for every partner."},{"title":"Claims & Incentives","description":"Automated rebate, claim, and incentive program management with real-time tracking."},{"title":"Compliance Management","description":"Automated compliance checks, audit trails, and regulatory reporting across markets."},{"title":"Data Integration Hub","description":"Centralized data ingestion from ERP, CRM, and external partner systems."},{"title":"Smart Notifications","description":"Configurable alerts for inventory thresholds, claim status, and partner activity."}]',
    imageUrl: null, buttonText: null, buttonUrl: null,
    displayOrder: 3, active: true, startDate: null, endDate: null,
    createdBy: null, createdAt: '', updatedBy: null, updatedAt: '',
  },
  {
    id: 0, sectionName: 'Benefits', sectionType: 'benefits',
    title: 'Why Channel360?', subtitle: null,
    description: '[{"title":"Complete Lifecycle Visibility","description":"Track every product from manufacturer to end customer with full activation visibility."},{"title":"Centralized Operations","description":"Manage users, content, announcements, and partner communications in one place."},{"title":"Dynamic Content Control","description":"Update homepage, promotions, and announcements without code deployments."},{"title":"Secure by Design","description":"Role-based access control ensures users see only what they need."},{"title":"Scalable Foundation","description":"Built for enterprise growth with a modular architecture ready for analytics and reporting."}]',
    imageUrl: null, buttonText: null, buttonUrl: null,
    displayOrder: 4, active: true, startDate: null, endDate: null,
    createdBy: null, createdAt: '', updatedBy: null, updatedAt: '',
  },
  {
    id: 0, sectionName: 'Contact', sectionType: 'contact',
    title: 'Partner with Channel360',
    subtitle: "Let's build the next big thing together. Tell us about your channel goals and we'll map out a path forward.",
    description: '[{"type":"email","label":"Email","value":"niyajkumanali@gmail.com"},{"type":"phone","label":"Phone","value":"+91 8217097121"},{"type":"address","label":"Office","value":"Nipani, Belagavi, Karnataka, India, 591237"},{"type":"linkedin","label":"LinkedIn","value":"linkedin.com/in/niyaj-kumanali"},{"type":"github","label":"GitHub","value":"github.com/Niyaj-Kumanali"},{"type":"website","label":"Portfolio","value":"niyazdev.vercel.app"}]',
    imageUrl: null, buttonText: 'Access Platform', buttonUrl: '/login',
    displayOrder: 5, active: true, startDate: null, endDate: null,
    createdBy: null, createdAt: '', updatedBy: null, updatedAt: '',
  },
  {
    id: 0, sectionName: 'Footer', sectionType: 'footer',
    title: 'Footer', subtitle: null,
    description: '{"tagline":"End-to-end visibility across your entire channel ecosystem — from manufacturer to end-customer activation.","groups":[{"title":"Platform","links":[{"label":"Home","url":"/"},{"label":"Features","url":"#"},{"label":"Pricing","url":"#"},{"label":"FAQ","url":"#"}]},{"title":"Resources","links":[{"label":"Documentation","url":"#"},{"label":"API Reference","url":"#"},{"label":"Changelog","url":"#"},{"label":"Status","url":"#"}]},{"title":"Company","links":[{"label":"About","url":"#"},{"label":"Blog","url":"#"},{"label":"Careers","url":"#"},{"label":"Contact","url":"#"}]}],"socialLinks":[{"label":"Email","url":"mailto:niyajkumanali@gmail.com","icon":"email"},{"label":"LinkedIn","url":"https://linkedin.com/in/niyaj-kumanali","icon":"linkedin"},{"label":"GitHub","url":"https://github.com/Niyaj-Kumanali","icon":"github"},{"label":"Portfolio","url":"https://niyazdev.vercel.app","icon":"website"}]}',
    imageUrl: null, buttonText: null, buttonUrl: null,
    displayOrder: 6, active: true, startDate: null, endDate: null,
    createdBy: null, createdAt: '', updatedBy: null, updatedAt: '',
  },
  {
    id: 0, sectionName: 'Announcement', sectionType: 'announcement',
    title: 'New Partner Program Launching Q3',
    subtitle: null,
    description: 'We are excited to announce enhanced incentive tiers and streamlined onboarding for all channel partners.',
    imageUrl: null, buttonText: 'Learn More', buttonUrl: '#',
    displayOrder: 7, active: true, startDate: null, endDate: null,
    createdBy: null, createdAt: '', updatedBy: null, updatedAt: '',
  },
  {
    id: 0, sectionName: 'Information Block', sectionType: 'info_block',
    title: 'How Channel360 Works',
    subtitle: 'A unified platform for your entire distribution network',
    description: 'Channel360 connects manufacturers, distributors, and channel partners on a single platform, providing end-to-end visibility, automated workflows, and actionable insights.',
    imageUrl: null, buttonText: 'Learn More', buttonUrl: '#',
    displayOrder: 8, active: true, startDate: null, endDate: null,
    createdBy: null, createdAt: '', updatedBy: null, updatedAt: '',
  },
  {
    id: 0, sectionName: 'Promotion', sectionType: 'promotion',
    title: 'Early Access: Partner Insights Dashboard',
    subtitle: 'Beta Program',
    description: 'Be among the first to explore our new Partner Insights Dashboard with real-time performance metrics, trend analysis, and actionable recommendations.',
    imageUrl: null, buttonText: 'Join Beta', buttonUrl: '#',
    displayOrder: 9, active: true, startDate: null, endDate: null,
    createdBy: null, createdAt: '', updatedBy: null, updatedAt: '',
  },
  {
    id: 0, sectionName: 'Image Card', sectionType: 'image_card',
    title: 'Platform Dashboard Preview',
    subtitle: null,
    description: 'Get a first look at the new analytics dashboard with real-time channel performance metrics.',
    imageUrl: null, buttonText: 'View Details', buttonUrl: '#',
    displayOrder: 10, active: true, startDate: null, endDate: null,
    createdBy: null, createdAt: '', updatedBy: null, updatedAt: '',
  },
  {
    id: 0, sectionName: 'Rich Content', sectionType: 'rich_content',
    title: 'Frequently Asked Questions',
    subtitle: null,
    description: '<h3>How does onboarding work?</h3><p>Our team guides you through every step, from data migration to partner training.</p><h3>Can I integrate with my existing ERP?</h3><p>Yes, Channel360 supports integrations with major ERP and CRM systems.</p><h3>What kind of support is available?</h3><p>24/7 technical support with dedicated account managers for enterprise plans.</p><h3>Is my data secure on the platform?</h3><p>We use enterprise-grade encryption, SOC 2 compliance, and role-based access controls to keep your data safe.</p><h3>Can I customize the platform for my workflow?</h3><p>Yes, Channel360 supports custom fields, workflows, and modules tailored to your distribution model.</p><h3>What does pricing look like for enterprises?</h3><p>We offer tiered pricing based on partner count and transaction volume. Contact our sales team for a custom quote.</p>',
    imageUrl: null, buttonText: null, buttonUrl: null,
    displayOrder: 11, active: true, startDate: null, endDate: null,
    createdBy: null, createdAt: '', updatedBy: null, updatedAt: '',
  },
];

export const HomePage: React.FC = () => {
  const { theme, toggleTheme } = useTheme();
  const [sections, setSections] = useState<HomepageSection[]>(staticSections);
  const [popups, setPopups] = useState<HomepagePopup[]>([]);

  useEffect(() => {
    Promise.all([
      homeApi.getPublishedSections(),
      homeApi.getActivePopups(),
    ]).then(([sectionsRes, popupsRes]) => {
      if (sectionsRes.success && sectionsRes.data.length > 0) {
        setSections(sectionsRes.data.sort((a, b) => a.displayOrder - b.displayOrder));
      }
      if (popupsRes.success && popupsRes.data.length > 0) {
        setPopups(popupsRes.data.sort((a, b) => b.priority - a.priority));
      }
    }).catch(() => {});
  }, []);

  return (
    <div className="min-h-screen bg-background text-foreground">
      {/* Navbar */}
      <header className="sticky top-0 z-50 border-b border-border bg-background/80 backdrop-blur-lg">
        <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-4 sm:px-6 lg:px-8">
          <Link to="/">
            <Logo variant={theme === 'dark' ? 'light' : 'dark'} size="sm" />
          </Link>
          <div className="flex items-center gap-3">
            <button
              onClick={toggleTheme}
              className="flex h-9 w-9 items-center justify-center rounded-lg text-muted-foreground hover:bg-accent hover:text-accent-foreground transition-colors"
              aria-label="Toggle theme"
            >
              {theme === 'dark' ? (
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="4"/><path d="M12 2v2"/><path d="M12 20v2"/><path d="m4.93 4.93 1.41 1.41"/><path d="m17.66 17.66 1.41 1.41"/><path d="M2 12h2"/><path d="M20 12h2"/><path d="m6.34 17.66-1.41 1.41"/><path d="m19.07 4.93-1.41 1.41"/></svg>
              ) : (
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M12 3a6 6 0 0 0 9 9 9 9 0 1 1-9-9Z"/></svg>
              )}
            </button>
            <Link to="/login">
              <Button size="sm">Sign In</Button>
            </Link>
          </div>
        </div>
      </header>

        {sections.map((section) => (
          <SectionRenderer key={`${section.sectionType}-${section.displayOrder}`} section={section} />
        ))}
      {popups.length > 0 && <PopupModal popups={popups} />}
    </div>
  );
};
