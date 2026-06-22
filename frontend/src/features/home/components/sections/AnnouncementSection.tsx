import React from 'react';
import type { HomepageSection } from '@/features/cms/types/cms.types';

function MegaphoneViz({ className }: { className?: string }) {
  return (
    <svg className={className} viewBox="0 0 120 120" fill="none" xmlns="http://www.w3.org/2000/svg">
      <rect x="70" y="30" width="40" height="50" rx="4" className="fill-primary/20 stroke-primary/40" strokeWidth="2" />
      <path d="M70 35 L40 50 L40 70 L70 75" className="fill-primary/30 stroke-primary/50" strokeWidth="2" />
      <circle cx="35" cy="72" r="10" className="fill-primary/20 stroke-primary/40" strokeWidth="2" />
      <path d="M95 40 Q110 60 95 80" className="stroke-primary/30" strokeWidth="2" fill="none" />
      <path d="M102 35 Q120 60 102 85" className="stroke-primary/20" strokeWidth="2" fill="none" />
      <path d="M10 65 L20 60 L20 75 Z" className="fill-primary/40" />
      <path d="M10 79 L18 74 L18 84 Z" className="fill-primary/30" />
      <circle cx="70" cy="55" r="5" className="fill-primary/60" />
    </svg>
  );
}

interface Props {
  section: HomepageSection;
}

export const AnnouncementSection: React.FC<Props> = ({ section }) => (
  <section className="relative min-h-[calc(100vh-4rem)] overflow-hidden border-b border-border py-16">
    <div className="absolute inset-0 bg-gradient-to-br from-primary/[0.02] via-transparent to-primary/[0.02]" />
    <div className="relative mx-auto flex min-h-[calc(100vh-4rem-8rem)] max-w-7xl items-center px-4 sm:px-6 lg:px-8">
      <div className="mx-auto max-w-3xl">
        <div className="flex flex-col items-center gap-8 text-center sm:flex-row sm:text-left">
          <div className="flex h-24 w-24 shrink-0 items-center justify-center rounded-2xl bg-primary/10">
            <MegaphoneViz className="h-14 w-14" />
          </div>
          <div>
            <h2 className="bg-gradient-to-b from-foreground to-foreground/80 bg-clip-text text-3xl font-extrabold text-transparent sm:text-5xl">
              {section.title}
            </h2>
            {section.description && (
              <p className="mt-4 text-base leading-relaxed text-muted-foreground sm:text-lg">
                {section.description}
              </p>
            )}
            {(section.buttonText || section.buttonUrl) && (
              <div className="mt-6">
                {section.buttonUrl ? (
                  <a
                    href={section.buttonUrl}
                    className="inline-flex items-center gap-2 text-sm font-medium text-primary hover:text-primary/80"
                  >
                    {section.buttonText || 'Learn More'} &rarr;
                  </a>
                ) : (
                  <span className="inline-flex items-center gap-2 text-sm font-medium text-primary">
                    {section.buttonText} &rarr;
                  </span>
                )}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  </section>
);
