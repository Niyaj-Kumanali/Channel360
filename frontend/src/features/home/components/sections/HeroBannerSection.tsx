import React from 'react';
import { Link } from 'react-router-dom';
import { ArrowRight, Shield, BarChart3, Database } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { RotatingEarth } from '@/features/home/components/RotatingEarth';
import type { HomepageSection } from '@/features/cms/types/cms.types';

interface Props {
  section: HomepageSection;
}

export const HeroBannerSection: React.FC<Props> = ({ section }) => (
  <section className="relative overflow-hidden border-b border-border">
    <RotatingEarth />
    <div className="absolute inset-0 bg-gradient-to-b from-primary/5 to-transparent" />
    <div className="mx-auto max-w-7xl px-4 pb-24 pt-16 sm:px-6 lg:px-8">
      <div className="relative mx-auto max-w-4xl text-center">
        {section.subtitle && (
          <div className="mb-6 inline-flex items-center gap-2 rounded-full border border-primary/20 bg-primary/10 px-4 py-1.5 text-xs font-medium text-primary">
            <Shield className="h-3.5 w-3.5" />
            {section.subtitle}
          </div>
        )}
        <h1 className="text-4xl font-bold tracking-tight text-foreground sm:text-5xl lg:text-6xl">
          {section.title}
        </h1>
        {section.description && (
          <p className="mt-6 text-lg leading-relaxed text-muted-foreground sm:text-xl">
            {section.description}
          </p>
        )}
        <div className="mt-10 flex items-center justify-center gap-4">
          {(section.buttonText || section.buttonUrl) && (
            section.buttonUrl ? (
              <Link to={section.buttonUrl}>
                <Button size="lg" className="gap-2">
                  {section.buttonText || 'Get Started'} <ArrowRight className="h-4 w-4" />
                </Button>
              </Link>
            ) : (
              <Button size="lg" className="gap-2">
                {section.buttonText} <ArrowRight className="h-4 w-4" />
              </Button>
            )
          )}
        </div>
        <div className="mt-8 flex items-center justify-center gap-6 text-sm text-muted-foreground">
          <span className="flex items-center gap-1.5">
            <Shield className="h-4 w-4 text-primary" /> Role-Based Access
          </span>
          <span className="flex items-center gap-1.5">
            <BarChart3 className="h-4 w-4 text-primary" /> Real-Time Analytics
          </span>
          <span className="flex items-center gap-1.5">
            <Database className="h-4 w-4 text-primary" /> CMS-Driven Content
          </span>
        </div>
      </div>
    </div>
  </section>
);
