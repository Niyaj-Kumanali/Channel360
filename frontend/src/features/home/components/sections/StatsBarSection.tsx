import React from 'react';
import type { HomepageSection } from '@/features/cms/types/cms.types';

interface Props {
  section: HomepageSection;
}

export const StatsBarSection: React.FC<Props> = ({ section }) => {
  const stats: { value: string; label: string }[] = section.description
    ? JSON.parse(section.description)
    : [
        { value: 'End-to-End', label: 'Product Lifecycle Visibility' },
        { value: 'Multi-Tier', label: 'Channel Ecosystem Support' },
        { value: 'Role-Based', label: 'Access Control' },
        { value: 'CMS-Driven', label: 'Dynamic Content Management' },
      ];

  return (
    <section className="border-b border-border bg-muted/50">
      <div className="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
        <div className="grid grid-cols-2 gap-8 md:grid-cols-4">
          {stats.map((stat) => (
            <div key={stat.label} className="text-center">
              <div className="text-lg font-bold text-foreground sm:text-xl">{stat.value}</div>
              <div className="mt-1 text-sm text-muted-foreground">{stat.label}</div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
};
