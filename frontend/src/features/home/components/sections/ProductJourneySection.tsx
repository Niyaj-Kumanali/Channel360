import React from 'react';
import { Package, Network, ArrowRightLeft, ShoppingCart, Cpu, ArrowRight } from 'lucide-react';
import type { HomepageSection } from '@/features/cms/types/cms.types';

interface Props {
  section: HomepageSection;
}

const defaultIcons = [Package, Network, ArrowRightLeft, ShoppingCart, Cpu];

interface JourneyStep {
  title: string;
  description: string;
}

export const ProductJourneySection: React.FC<Props> = ({ section }) => {
  const steps: JourneyStep[] = section.description
    ? JSON.parse(section.description)
    : [
        { title: 'Manufacturer', description: 'Products enter the channel network from manufacturers and suppliers.' },
        { title: 'Distributor', description: 'Distributors receive and forward products to channel partners.' },
        { title: 'Channel Partner', description: 'Partners sell products to end customers and manage local inventory.' },
        { title: 'End Customer', description: 'Customers purchase products through the partner network.' },
        { title: 'Activation', description: 'Products are activated and linked back to their channel journey.' },
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
        <div className="mt-16 grid gap-6 sm:grid-cols-2 lg:grid-cols-5">
          {steps.map((step, index) => {
            const Icon = defaultIcons[index] || Package;
            return (
              <div key={step.title} className="relative">
                <div className="group rounded-xl border border-border bg-card p-6 text-center transition-all duration-200 hover:border-primary/30 hover:shadow-md hover:shadow-primary/5">
                  <div className="mx-auto mb-4 flex h-10 w-10 items-center justify-center rounded-lg bg-primary/10 text-primary group-hover:bg-primary group-hover:text-primary-foreground transition-colors duration-200">
                    <Icon className="h-5 w-5" />
                  </div>
                  <h3 className="text-sm font-semibold text-foreground">{step.title}</h3>
                  <p className="mt-2 text-xs leading-relaxed text-muted-foreground">{step.description}</p>
                </div>
                {index < steps.length - 1 && (
                  <div className="hidden lg:flex absolute top-1/2 -right-3 z-10 text-muted-foreground/30">
                    <ArrowRight className="h-5 w-5" />
                  </div>
                )}
              </div>
            );
          })}
        </div>
      </div>
    </section>
  );
};
