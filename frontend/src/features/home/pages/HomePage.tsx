import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { ArrowRight } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { Logo } from '@/components/ui/Logo';
import { useTheme } from '@/app/hooks/useTheme';
import { homeApi } from '@/features/home/api/home.api';
import { SectionRenderer } from '@/features/home/components/sections/SectionRenderer';
import { PopupModal } from '@/features/home/components/PopupModal';
import { Loader } from '@/components/ui/Loader';
import type { HomepageSection, HomepagePopup } from '@/features/cms/types/cms.types';

const staticSections: HomepageSection[] = [
  {
    id: 0, sectionName: 'Hero Banner', sectionType: 'hero_banner',
    title: 'Complete Visibility Across Your Channel Ecosystem',
    subtitle: 'Enterprise Channel Management Platform',
    description: 'Track the complete lifecycle of products across your distribution network — from channel entry to end-customer engagement and activation.',
    imageUrl: null, buttonText: 'Access Platform', buttonUrl: '/login',
    displayOrder: 1, active: true, startDate: null, endDate: null,
    createdBy: null, createdAt: '', updatedBy: null, updatedAt: '',
  },
  {
    id: 0, sectionName: 'Stats Bar', sectionType: 'stats_bar',
    title: 'Stats', subtitle: null,
    description: '[{"value":"End-to-End","label":"Product Lifecycle Visibility"},{"value":"Multi-Tier","label":"Channel Ecosystem Support"},{"value":"Role-Based","label":"Access Control"},{"value":"CMS-Driven","label":"Dynamic Content Management"}]',
    imageUrl: null, buttonText: null, buttonUrl: null,
    displayOrder: 2, active: true, startDate: null, endDate: null,
    createdBy: null, createdAt: '', updatedBy: null, updatedAt: '',
  },
  {
    id: 0, sectionName: 'Product Journey', sectionType: 'product_journey',
    title: 'The Product Journey',
    subtitle: 'Follow every product from manufacturer through distribution to end-customer activation.',
    description: '[{"title":"Manufacturer","description":"Products enter the channel network from manufacturers and suppliers."},{"title":"Distributor","description":"Distributors receive and forward products to channel partners."},{"title":"Channel Partner","description":"Partners sell products to end customers and manage local inventory."},{"title":"End Customer","description":"Customers purchase products through the partner network."},{"title":"Activation","description":"Products are activated and linked back to their channel journey."}]',
    imageUrl: null, buttonText: null, buttonUrl: null,
    displayOrder: 3, active: true, startDate: null, endDate: null,
    createdBy: null, createdAt: '', updatedBy: null, updatedAt: '',
  },
  {
    id: 0, sectionName: 'Core Business Areas', sectionType: 'business_areas',
    title: 'Core Business Areas',
    subtitle: 'Channel360 is purpose-built to support every stage of the channel ecosystem.',
    description: '[{"title":"Channel Entry","description":"Track product movement from manufacturers to distributors and strategic partners. Gain full visibility into initial channel distribution."},{"title":"Partner Transfer","description":"Monitor product flow between distributors and channel partners. Ensure accurate tracking across the entire partner network."},{"title":"Customer Purchase","description":"Track product sales from channel partners to end customers. Capture point-of-sale data for complete revenue visibility."},{"title":"Product Activation","description":"Connect activation records with channel movement data. Enable complete lifecycle tracking for every product unit."},{"title":"Claims Management","description":"Manage and track channel-related claims and incentive programs. Streamline rebates, promotions, and partner compensation."},{"title":"External Data Integration","description":"Upload and manage business data from external sources. Centralize third-party data for unified reporting and analysis."}]',
    imageUrl: null, buttonText: null, buttonUrl: null,
    displayOrder: 4, active: true, startDate: null, endDate: null,
    createdBy: null, createdAt: '', updatedBy: null, updatedAt: '',
  },
  {
    id: 0, sectionName: 'Benefits', sectionType: 'benefits',
    title: 'Why Channel360', subtitle: null,
    description: '[{"title":"Complete Lifecycle Visibility","description":"Track every product from manufacturer to end customer with full activation visibility."},{"title":"Centralized Operations","description":"Manage users, content, announcements, and partner communications in one place."},{"title":"Dynamic Content Control","description":"Update homepage, promotions, and announcements without code deployments."},{"title":"Secure by Design","description":"Role-based access control ensures users see only what they need."},{"title":"Scalable Foundation","description":"Built for enterprise growth with a modular architecture ready for analytics and reporting."}]',
    imageUrl: null, buttonText: null, buttonUrl: null,
    displayOrder: 5, active: true, startDate: null, endDate: null,
    createdBy: null, createdAt: '', updatedBy: null, updatedAt: '',
  },
  {
    id: 0, sectionName: 'Call to Action', sectionType: 'cta',
    title: 'Ready to Unify Your Channel Operations?', subtitle: null,
    description: 'Access the platform to manage your channel ecosystem, track product lifecycles, and gain complete operational visibility.',
    imageUrl: null, buttonText: 'Access Platform', buttonUrl: '/login',
    displayOrder: 6, active: true, startDate: null, endDate: null,
    createdBy: null, createdAt: '', updatedBy: null, updatedAt: '',
  },
];

export const HomePage: React.FC = () => {
  const { theme, toggleTheme } = useTheme();
  const [sections, setSections] = useState<HomepageSection[]>([]);
  const [popups, setPopups] = useState<HomepagePopup[]>([]);
  const [loading, setLoading] = useState(true);

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
    }).catch(() => {}).finally(() => setLoading(false));
  }, []);

  const displaySections = sections.length > 0 ? sections : staticSections;

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

      {loading ? (
        <div className="flex items-center justify-center min-h-[calc(100vh-4rem)]">
          <Loader size="lg" />
        </div>
      ) : (
        displaySections.map((section) => (
          <SectionRenderer key={`${section.sectionType}-${section.displayOrder}`} section={section} />
        ))
      )}

      {popups.length > 0 && <PopupModal popups={popups} />}

      {/* Footer */}
      <footer className="border-t border-border bg-muted/50">
        <div className="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
          <div className="flex flex-col items-center justify-between gap-6 sm:flex-row">
            <Logo variant={theme === 'dark' ? 'light' : 'dark'} size="sm" />
            <div className="flex items-center gap-6 text-sm text-muted-foreground">
              <a href="#" className="hover:text-foreground transition-colors">Privacy Policy</a>
              <a href="#" className="hover:text-foreground transition-colors">Terms of Service</a>
              <a href="#" className="hover:text-foreground transition-colors">Contact</a>
            </div>
            <p className="text-xs text-muted-foreground">
              &copy; {new Date().getFullYear()} Channel360. All rights reserved.
            </p>
          </div>
        </div>
      </footer>
    </div>
  );
};
