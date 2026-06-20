import React from 'react';
import { FileText } from 'lucide-react';
import type { HomepageSection } from '@/features/cms/types/cms.types';

interface Props {
  section: HomepageSection;
}

export const RichContentSection: React.FC<Props> = ({ section }) => (
  <section className="border-b border-border py-20">
    <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
      <div className="mx-auto max-w-4xl">
        <h2 className="text-2xl font-bold text-foreground sm:text-3xl">{section.title}</h2>
        {section.description && (
          <div
            className="mt-6 prose prose-sm dark:prose-invert max-w-none text-muted-foreground leading-relaxed"
            dangerouslySetInnerHTML={{ __html: section.description }}
          />
        )}
      </div>
    </div>
  </section>
);
