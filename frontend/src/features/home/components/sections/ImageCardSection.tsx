import React from 'react';
import { Image as ImageIcon } from 'lucide-react';
import type { HomepageSection } from '@/features/cms/types/cms.types';

interface Props {
  section: HomepageSection;
}

export const ImageCardSection: React.FC<Props> = ({ section }) => (
  <section className="border-b border-border py-20">
    <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
      <div className="mx-auto max-w-4xl">
        <div className="overflow-hidden rounded-2xl border border-border bg-card">
          {section.imageUrl ? (
            <img
              src={section.imageUrl}
              alt={section.title}
              className="w-full h-80 object-cover"
            />
          ) : (
            <div className="flex h-80 items-center justify-center bg-muted">
              <ImageIcon className="h-16 w-16 text-muted-foreground/30" />
            </div>
          )}
          <div className="p-8">
            <h3 className="text-xl font-bold text-foreground">{section.title}</h3>
            {section.description && (
              <p className="mt-3 text-muted-foreground leading-relaxed">{section.description}</p>
            )}
            {section.buttonUrl && (
              <a
                href={section.buttonUrl}
                className="mt-4 inline-flex items-center gap-2 text-sm font-medium text-primary hover:text-primary/80 transition-colors"
              >
                {section.buttonText || 'View Details'} &rarr;
              </a>
            )}
          </div>
        </div>
      </div>
    </div>
  </section>
);
