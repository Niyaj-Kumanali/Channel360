import React from 'react';
import { Sparkles } from 'lucide-react';
import type { HomepageSection } from '@/features/cms/types/cms.types';

interface Props {
  section: HomepageSection;
}

export const PromotionSection: React.FC<Props> = ({ section }) => (
  <section className="border-b border-border bg-gradient-to-r from-primary/5 via-primary/10 to-primary/5 py-16">
    <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
      <div className="mx-auto max-w-3xl text-center">
        <div className="mb-4 flex items-center justify-center gap-2">
          <Sparkles className="h-5 w-5 text-primary" />
          <span className="text-xs font-semibold uppercase tracking-widest text-primary">
            {section.subtitle || 'Promotion'}
          </span>
        </div>
        <h2 className="text-3xl font-bold text-foreground sm:text-4xl">{section.title}</h2>
        {section.description && (
          <p className="mt-4 text-lg text-muted-foreground">{section.description}</p>
        )}
        {(section.buttonText || section.buttonUrl) && (
          section.buttonUrl ? (
            <a
              href={section.buttonUrl}
              className="mt-8 inline-flex items-center gap-2 rounded-lg bg-primary px-6 py-3 text-sm font-medium text-primary-foreground hover:bg-primary/90 transition-colors"
            >
              {section.buttonText || 'Learn More'} &rarr;
            </a>
          ) : (
            <span className="mt-8 inline-flex items-center gap-2 rounded-lg bg-primary px-6 py-3 text-sm font-medium text-primary-foreground">
              {section.buttonText} &rarr;
            </span>
          )
        )}
      </div>
    </div>
  </section>
);
