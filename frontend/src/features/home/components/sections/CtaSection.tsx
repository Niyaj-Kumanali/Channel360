import React from 'react';
import { Link } from 'react-router-dom';
import { ArrowRight } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import type { HomepageSection } from '@/features/cms/types/cms.types';

interface Props {
  section: HomepageSection;
}

export const CtaSection: React.FC<Props> = ({ section }) => (
  <section className="flex py-16 min-h-[calc(100vh-4rem)] flex-col items-center justify-center">
    <div className="mx-auto w-full max-w-7xl px-4 sm:px-6 lg:px-8">
      <div className="relative overflow-hidden rounded-2xl border border-border bg-card px-8 py-16 text-center sm:px-16">
        <div className="absolute inset-0 bg-gradient-to-br from-primary/10 to-transparent" />
        <div className="relative">
          <h2 className="text-3xl font-bold text-foreground sm:text-4xl">{section.title}</h2>
          {section.description && (
            <p className="mx-auto mt-4 max-w-xl text-muted-foreground">{section.description}</p>
          )}
          <div className="mt-8 flex items-center justify-center gap-4">
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
        </div>
      </div>
    </div>
  </section>
);
