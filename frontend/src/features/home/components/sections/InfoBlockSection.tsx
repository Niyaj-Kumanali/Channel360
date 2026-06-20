import React from 'react';
import { Info } from 'lucide-react';
import type { HomepageSection } from '@/features/cms/types/cms.types';

interface Props {
  section: HomepageSection;
}

export const InfoBlockSection: React.FC<Props> = ({ section }) => (
  <section className="border-b border-border py-20">
    <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
      <div className="mx-auto max-w-3xl">
        <div className="flex items-start gap-6">
          <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-xl bg-primary/10 text-primary">
            <Info className="h-6 w-6" />
          </div>
          <div>
            <h2 className="text-2xl font-bold text-foreground sm:text-3xl">{section.title}</h2>
            {section.subtitle && (
              <p className="mt-2 text-lg text-muted-foreground">{section.subtitle}</p>
            )}
            {section.description && (
              <p className="mt-4 text-base leading-relaxed text-muted-foreground">{section.description}</p>
            )}
            {section.imageUrl && (
              <img
                src={section.imageUrl}
                alt={section.title}
                className="mt-8 rounded-xl border border-border object-cover w-full"
              />
            )}
            {section.buttonUrl && (
              <a
                href={section.buttonUrl}
                className="mt-6 inline-flex items-center gap-2 text-sm font-medium text-primary hover:text-primary/80 transition-colors"
              >
                {section.buttonText || 'Learn More'} &rarr;
              </a>
            )}
          </div>
        </div>
      </div>
    </div>
  </section>
);
