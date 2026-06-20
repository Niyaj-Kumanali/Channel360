import React from 'react';
import { Package, ArrowRightLeft, ShoppingCart, Cpu, FileText, Database } from 'lucide-react';
import type { HomepageSection } from '@/features/cms/types/cms.types';

interface Props {
  section: HomepageSection;
}

const defaultIcons = [Package, ArrowRightLeft, ShoppingCart, Cpu, FileText, Database];

interface Area {
  title: string;
  description: string;
}

export const BusinessAreasSection: React.FC<Props> = ({ section }) => {
  const areas: Area[] = section.description
    ? JSON.parse(section.description)
    : [
        { title: 'Channel Entry', description: 'Track product movement from manufacturers to distributors and strategic partners.' },
        { title: 'Partner Transfer', description: 'Monitor product flow between distributors and channel partners.' },
        { title: 'Customer Purchase', description: 'Track product sales from channel partners to end customers.' },
        { title: 'Product Activation', description: 'Connect activation records with channel movement data.' },
        { title: 'Claims Management', description: 'Manage and track channel-related claims and incentive programs.' },
        { title: 'External Data Integration', description: 'Upload and manage business data from external sources.' },
      ];

  return (
    <section className="border-b border-border py-20">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="mx-auto max-w-2xl text-center">
          <h2 className="text-3xl font-bold text-foreground sm:text-4xl">{section.title}</h2>
          {section.subtitle && (
            <p className="mt-4 text-lg text-muted-foreground">{section.subtitle}</p>
          )}
        </div>
        <div className="mt-16 grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {areas.map((area, index) => {
            const Icon = defaultIcons[index] || Package;
            return (
              <div
                key={area.title}
                className="group rounded-xl border border-border bg-card p-6 transition-all duration-200 hover:border-primary/30 hover:shadow-md hover:shadow-primary/5"
              >
                <div className="mb-4 flex h-10 w-10 items-center justify-center rounded-lg bg-primary/10 text-primary group-hover:bg-primary group-hover:text-primary-foreground transition-colors duration-200">
                  <Icon className="h-5 w-5" />
                </div>
                <h3 className="text-base font-semibold text-foreground">{area.title}</h3>
                <p className="mt-2 text-sm leading-relaxed text-muted-foreground">{area.description}</p>
              </div>
            );
          })}
        </div>
      </div>
    </section>
  );
};
