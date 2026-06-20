import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { ArrowRight } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { Logo } from '@/components/ui/Logo';
import { useTheme } from '@/app/hooks/useTheme';
import { homeApi } from '@/features/home/api/home.api';
import { SectionRenderer } from '@/features/home/components/sections/SectionRenderer';
import type { HomepageSection } from '@/features/cms/types/cms.types';

import {
  Package,
  ArrowRightLeft,
  ShoppingCart,
  Cpu,
  FileText,
  Database,
  Shield,
  BarChart3,
  Network,
} from 'lucide-react';

const hardcodedBusinessAreas = [
  { icon: <Package className="h-5 w-5" />, title: 'Channel Entry', description: 'Track product movement from manufacturers to distributors and strategic partners. Gain full visibility into initial channel distribution.' },
  { icon: <ArrowRightLeft className="h-5 w-5" />, title: 'Partner Transfer', description: 'Monitor product flow between distributors and channel partners. Ensure accurate tracking across the entire partner network.' },
  { icon: <ShoppingCart className="h-5 w-5" />, title: 'Customer Purchase', description: 'Track product sales from channel partners to end customers. Capture point-of-sale data for complete revenue visibility.' },
  { icon: <Cpu className="h-5 w-5" />, title: 'Product Activation', description: 'Connect activation records with channel movement data. Enable complete lifecycle tracking for every product unit.' },
  { icon: <FileText className="h-5 w-5" />, title: 'Claims Management', description: 'Manage and track channel-related claims and incentive programs. Streamline rebates, promotions, and partner compensation.' },
  { icon: <Database className="h-5 w-5" />, title: 'External Data Integration', description: 'Upload and manage business data from external sources. Centralize third-party data for unified reporting and analysis.' },
];

const hardcodedStats = [
  { value: 'End-to-End', label: 'Product Lifecycle Visibility' },
  { value: 'Multi-Tier', label: 'Channel Ecosystem Support' },
  { value: 'Role-Based', label: 'Access Control' },
  { value: 'CMS-Driven', label: 'Dynamic Content Management' },
];

const hardcodedJourneySteps = [
  { title: 'Manufacturer', description: 'Products enter the channel network from manufacturers and suppliers.', icon: <Package className="h-5 w-5" /> },
  { title: 'Distributor', description: 'Distributors receive and forward products to channel partners.', icon: <Network className="h-5 w-5" /> },
  { title: 'Channel Partner', description: 'Partners sell products to end customers and manage local inventory.', icon: <ArrowRightLeft className="h-5 w-5" /> },
  { title: 'End Customer', description: 'Customers purchase products through the partner network.', icon: <ShoppingCart className="h-5 w-5" /> },
  { title: 'Activation', description: 'Products are activated and linked back to their channel journey.', icon: <Cpu className="h-5 w-5" /> },
];

const hardcodedBenefits = [
  { title: 'Complete Lifecycle Visibility', desc: 'Track every product from manufacturer to end customer with full activation visibility.' },
  { title: 'Centralized Operations', desc: 'Manage users, content, announcements, and partner communications in one place.' },
  { title: 'Dynamic Content Control', desc: 'Update homepage, promotions, and announcements without code deployments.' },
  { title: 'Secure by Design', desc: 'Role-based access control ensures users see only what they need.' },
  { title: 'Scalable Foundation', desc: 'Built for enterprise growth with a modular architecture ready for analytics and reporting.' },
];

export const HomePage: React.FC = () => {
  const { theme, toggleTheme } = useTheme();
  const [sections, setSections] = useState<HomepageSection[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    homeApi.getPublishedSections().then((res) => {
      if (res.success && res.data.length > 0) {
        setSections(res.data.sort((a, b) => a.displayOrder - b.displayOrder));
      }
    }).catch(() => {}).finally(() => setLoading(false));
  }, []);

  const hasCms = sections.length > 0;

  const getByType = (type: string) => sections.find((s) => s.sectionType === type);

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
        <div className="flex items-center justify-center h-64">
          <div className="h-8 w-8 animate-spin rounded-full border-2 border-primary border-t-transparent" />
        </div>
      ) : hasCms ? (
        sections.map((section) => (
          <SectionRenderer key={section.id} section={section} />
        ))
      ) : (
        <>
          {/* Hero (fallback) */}
          <section className="relative overflow-hidden border-b border-border">
            <div className="absolute inset-0 bg-gradient-to-b from-primary/5 to-transparent" />
            <div className="mx-auto max-w-7xl px-4 pb-24 pt-16 sm:px-6 lg:px-8">
              <div className="relative mx-auto max-w-4xl text-center">
                <div className="mb-6 inline-flex items-center gap-2 rounded-full border border-primary/20 bg-primary/10 px-4 py-1.5 text-xs font-medium text-primary">
                  <Shield className="h-3.5 w-3.5" />
                  Enterprise Channel Management Platform
                </div>
                <h1 className="text-4xl font-bold tracking-tight text-foreground sm:text-5xl lg:text-6xl">
                  Complete Visibility Across
                  <br />
                  <span className="text-primary">Your Channel Ecosystem</span>
                </h1>
                <p className="mt-6 text-lg leading-relaxed text-muted-foreground sm:text-xl">
                  Track the complete lifecycle of products across your distribution network —
                  from channel entry to end-customer engagement and activation.
                </p>
                <div className="mt-10 flex items-center justify-center gap-4">
                  <Link to="/login">
                    <Button size="lg" className="gap-2">
                      Access Platform <ArrowRight className="h-4 w-4" />
                    </Button>
                  </Link>
                  <Button variant="outline" size="lg">
                    View Documentation
                  </Button>
                </div>
                <div className="mt-8 flex items-center justify-center gap-6 text-sm text-muted-foreground">
                  <span className="flex items-center gap-1.5"><Shield className="h-4 w-4 text-primary" /> Role-Based Access</span>
                  <span className="flex items-center gap-1.5"><BarChart3 className="h-4 w-4 text-primary" /> Real-Time Analytics</span>
                  <span className="flex items-center gap-1.5"><Database className="h-4 w-4 text-primary" /> CMS-Driven Content</span>
                </div>
              </div>
            </div>
          </section>

          {/* Stats (fallback) */}
          <section className="border-b border-border bg-muted/50">
            <div className="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
              <div className="grid grid-cols-2 gap-8 md:grid-cols-4">
                {hardcodedStats.map((stat) => (
                  <div key={stat.label} className="text-center">
                    <div className="text-lg font-bold text-foreground sm:text-xl">{stat.value}</div>
                    <div className="mt-1 text-sm text-muted-foreground">{stat.label}</div>
                  </div>
                ))}
              </div>
            </div>
          </section>

          {/* Product Journey (fallback) */}
          <section className="border-b border-border py-20">
            <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
              <div className="mx-auto max-w-2xl text-center">
                <h2 className="text-3xl font-bold text-foreground sm:text-4xl">The Product Journey</h2>
                <p className="mt-4 text-lg text-muted-foreground">Follow every product from manufacturer through distribution to end-customer activation.</p>
              </div>
              <div className="mt-16 grid gap-6 sm:grid-cols-2 lg:grid-cols-5">
                {hardcodedJourneySteps.map((step, index) => (
                  <div key={step.title} className="relative">
                    <div className="group rounded-xl border border-border bg-card p-6 text-center transition-all duration-200 hover:border-primary/30 hover:shadow-md hover:shadow-primary/5">
                      <div className="mx-auto mb-4 flex h-10 w-10 items-center justify-center rounded-lg bg-primary/10 text-primary group-hover:bg-primary group-hover:text-primary-foreground transition-colors duration-200">
                        {step.icon}
                      </div>
                      <h3 className="text-sm font-semibold text-foreground">{step.title}</h3>
                      <p className="mt-2 text-xs leading-relaxed text-muted-foreground">{step.description}</p>
                    </div>
                    {index < hardcodedJourneySteps.length - 1 && (
                      <div className="hidden lg:flex absolute top-1/2 -right-3 z-10 text-muted-foreground/30">
                        <ArrowRight className="h-5 w-5" />
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </div>
          </section>

          {/* Business Areas (fallback) */}
          <section className="border-b border-border py-20">
            <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
              <div className="mx-auto max-w-2xl text-center">
                <h2 className="text-3xl font-bold text-foreground sm:text-4xl">Core Business Areas</h2>
                <p className="mt-4 text-lg text-muted-foreground">Channel360 is purpose-built to support every stage of the channel ecosystem.</p>
              </div>
              <div className="mt-16 grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
                {hardcodedBusinessAreas.map((area) => (
                  <div key={area.title} className="group rounded-xl border border-border bg-card p-6 transition-all duration-200 hover:border-primary/30 hover:shadow-md hover:shadow-primary/5">
                    <div className="mb-4 flex h-10 w-10 items-center justify-center rounded-lg bg-primary/10 text-primary group-hover:bg-primary group-hover:text-primary-foreground transition-colors duration-200">
                      {area.icon}
                    </div>
                    <h3 className="text-base font-semibold text-foreground">{area.title}</h3>
                    <p className="mt-2 text-sm leading-relaxed text-muted-foreground">{area.description}</p>
                  </div>
                ))}
              </div>
            </div>
          </section>

          {/* Benefits (fallback) */}
          <section className="border-b border-border bg-muted/30 py-20">
            <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
              <div className="mx-auto max-w-2xl text-center">
                <h2 className="text-3xl font-bold text-foreground sm:text-4xl">Why Channel360</h2>
              </div>
              <div className="mt-12 mx-auto max-w-3xl space-y-4">
                {hardcodedBenefits.map((benefit) => (
                  <div key={benefit.title} className="flex items-start gap-4 rounded-xl border border-border bg-card p-5">
                    <div className="mt-0.5 flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-primary/10">
                      <div className="h-2 w-2 rounded-full bg-primary" />
                    </div>
                    <div>
                      <h3 className="text-sm font-semibold text-foreground">{benefit.title}</h3>
                      <p className="mt-1 text-sm text-muted-foreground">{benefit.desc}</p>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </section>

          {/* CTA (fallback) */}
          <section className="py-20">
            <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
              <div className="relative overflow-hidden rounded-2xl border border-border bg-card px-8 py-16 text-center sm:px-16">
                <div className="absolute inset-0 bg-gradient-to-br from-primary/10 to-transparent" />
                <div className="relative">
                  <h2 className="text-3xl font-bold text-foreground sm:text-4xl">Ready to Unify Your Channel Operations?</h2>
                  <p className="mx-auto mt-4 max-w-xl text-muted-foreground">Access the platform to manage your channel ecosystem, track product lifecycles, and gain complete operational visibility.</p>
                  <div className="mt-8 flex items-center justify-center gap-4">
                    <Link to="/login">
                      <Button size="lg" className="gap-2">
                        Access Platform <ArrowRight className="h-4 w-4" />
                      </Button>
                    </Link>
                    <Button variant="outline" size="lg">Learn More</Button>
                  </div>
                </div>
              </div>
            </div>
          </section>
        </>
      )}

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
