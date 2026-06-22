import React from 'react';
import type { HomepageSection } from '@/features/cms/types/cms.types';

interface Props {
  section: HomepageSection;
}

interface Benefit {
  title: string;
  description: string;
}

export const BenefitsSection: React.FC<Props> = ({ section }) => {
  const benefits: Benefit[] = section.description
    ? JSON.parse(section.description)
    : [
        { title: 'Complete Lifecycle Visibility', description: 'Track every product from manufacturer to end customer with full activation visibility.' },
        { title: 'Centralized Operations', description: 'Manage users, content, announcements, and partner communications in one place.' },
        { title: 'Dynamic Content Control', description: 'Update homepage, promotions, and announcements without code deployments.' },
        { title: 'Secure by Design', description: 'Role-based access control ensures users see only what they need.' },
        { title: 'Scalable Foundation', description: 'Built for enterprise growth with a modular architecture ready for analytics and reporting.' },
      ];

  return (
    <section className="flex py-16 lg:min-h-[calc(100vh-4rem)] flex-col items-center justify-center bg-muted/30">
      <div className="mx-auto w-full max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="mx-auto max-w-2xl text-center">
          <h2 className="text-3xl font-bold text-foreground sm:text-4xl">{section.title}</h2>
        </div>
        <div className="mt-12 mx-auto max-w-3xl space-y-4">
          {benefits.map((benefit) => (
            <div
              key={benefit.title}
              className="flex items-start gap-4 rounded-xl border border-border bg-card p-5"
            >
              <div className="mt-0.5 flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-primary/10">
                <div className="h-2 w-2 rounded-full bg-primary" />
              </div>
              <div>
                <h3 className="text-sm font-semibold text-foreground">{benefit.title}</h3>
                <p className="mt-1 text-sm text-muted-foreground">{benefit.description}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
};
