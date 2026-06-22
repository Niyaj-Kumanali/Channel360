import React from 'react';
import type { HomepageSection } from '@/features/cms/types/cms.types';

function BadgeViz({ className }: { className?: string }) {
  return (
    <svg className={className} viewBox="0 0 120 120" fill="none" xmlns="http://www.w3.org/2000/svg">
      <path d="M60 10 L72 30 L95 25 L90 48 L110 60 L90 72 L95 95 L72 90 L60 110 L48 90 L25 95 L30 72 L10 60 L30 48 L25 25 L48 30 Z" className="fill-primary/15 stroke-primary/40" strokeWidth="2" />
      <path d="M60 25 L68 38 L83 35 L80 50 L93 60 L80 70 L83 85 L68 82 L60 95 L52 82 L37 85 L40 70 L27 60 L40 50 L37 35 L52 38 Z" className="fill-primary/10 stroke-primary/30" strokeWidth="1.5" />
      <circle cx="60" cy="60" r="18" className="fill-primary/20 stroke-primary/40" strokeWidth="2" />
      <path d="M52 60 L58 66 L68 55" className="stroke-primary/60" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" />
      <circle cx="60" cy="95" r="3" className="fill-primary/40" />
      <circle cx="95" cy="60" r="3" className="fill-primary/40" />
      <circle cx="25" cy="60" r="3" className="fill-primary/40" />
      <circle cx="60" cy="25" r="3" className="fill-primary/40" />
      <path d="M60 108 L63 114 L57 114 Z" className="fill-primary/30" />
      <path d="M108 60 L114 57 L114 63 Z" className="fill-primary/30" />
      <path d="M12 60 L6 57 L6 63 Z" className="fill-primary/30" />
      <path d="M60 12 L57 6 L63 6 Z" className="fill-primary/30" />
    </svg>
  );
}

interface Props {
  section: HomepageSection;
}

export const PromotionSection: React.FC<Props> = ({ section }) => (
  <section className="relative min-h-[calc(100vh-4rem)] overflow-hidden border-b border-border py-16">
    <div className="absolute inset-0 bg-gradient-to-br from-primary/[0.03] via-transparent to-primary/[0.03]" />
    <div className="absolute left-1/2 top-1/2 h-80 w-80 -translate-x-1/2 -translate-y-1/2 rounded-full bg-primary/[0.02] blur-[140px]" />
    <div className="relative mx-auto flex min-h-[calc(100vh-4rem-8rem)] max-w-7xl items-center px-4 sm:px-6 lg:px-8">
      <div className="mx-auto max-w-3xl text-center">
        <div className="mx-auto mb-6 flex h-20 w-20 items-center justify-center rounded-2xl bg-primary/10">
          <BadgeViz className="h-12 w-12" />
        </div>
        {section.subtitle && (
          <span className="inline-block rounded-full border border-primary/30 bg-primary/10 px-4 py-1 text-xs font-semibold uppercase tracking-widest text-primary">
            {section.subtitle}
          </span>
        )}
        <h2 className="mt-6 bg-gradient-to-b from-foreground to-foreground/80 bg-clip-text text-3xl font-extrabold text-transparent sm:text-5xl">
          {section.title}
        </h2>
        {section.description && (
          <p className="mt-4 text-base leading-relaxed text-muted-foreground sm:text-lg">
            {section.description}
          </p>
        )}
        {(section.buttonText || section.buttonUrl) && (
          <div className="mt-8">
            {section.buttonUrl ? (
              <a
                href={section.buttonUrl}
                className="inline-flex items-center gap-2 rounded-lg bg-primary px-6 py-3 text-sm font-medium text-primary-foreground hover:bg-primary/90"
              >
                {section.buttonText || 'Learn More'} &rarr;
              </a>
            ) : (
              <span className="inline-flex items-center gap-2 rounded-lg bg-primary px-6 py-3 text-sm font-medium text-primary-foreground">
                {section.buttonText} &rarr;
              </span>
            )}
          </div>
        )}
      </div>
    </div>
  </section>
);
