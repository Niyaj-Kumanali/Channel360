import React from 'react';
import { Megaphone } from 'lucide-react';
import type { HomepageSection } from '@/features/cms/types/cms.types';

interface Props {
  section: HomepageSection;
}

export const AnnouncementSection: React.FC<Props> = ({ section }) => (
  <section className="border-b border-border bg-primary/5">
    <div className="mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:px-8">
      <div className="flex items-center gap-4">
        <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-primary/10 text-primary">
          <Megaphone className="h-5 w-5" />
        </div>
        <div>
          <h3 className="text-sm font-semibold text-foreground">{section.title}</h3>
          {section.description && (
            <p className="mt-0.5 text-sm text-muted-foreground">{section.description}</p>
          )}
        </div>
        {section.buttonUrl && (
          <a
            href={section.buttonUrl}
            className="ml-auto shrink-0 text-sm font-medium text-primary hover:text-primary/80 transition-colors"
          >
            {section.buttonText || 'Learn More'} &rarr;
          </a>
        )}
      </div>
    </div>
  </section>
);
