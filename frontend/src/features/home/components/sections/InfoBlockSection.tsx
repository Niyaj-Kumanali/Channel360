import React from 'react';
import type { HomepageSection } from '@/features/cms/types/cms.types';

function InfoViz({ className }: { className?: string }) {
  return (
    <svg className={className} viewBox="0 0 120 120" fill="none" xmlns="http://www.w3.org/2000/svg">
      <rect x="15" y="20" width="90" height="80" rx="6" className="fill-primary/10 stroke-primary/30" strokeWidth="2" />
      <rect x="25" y="30" width="70" height="8" rx="2" className="fill-primary/40" />
      <rect x="25" y="45" width="50" height="5" rx="2" className="fill-muted-foreground/20" />
      <rect x="25" y="56" width="60" height="5" rx="2" className="fill-muted-foreground/20" />
      <rect x="25" y="67" width="40" height="5" rx="2" className="fill-muted-foreground/20" />
      <circle cx="88" cy="85" r="15" className="fill-primary/15 stroke-primary/40" strokeWidth="2" />
      <path d="M83 85 L87 89 L93 81" className="stroke-primary/60" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" />
      <path d="M60 85 L70 85" className="stroke-primary/30" strokeWidth="2" strokeLinecap="round" />
      <circle cx="55" cy="85" r="3" className="fill-primary/50" />
    </svg>
  );
}

interface Props {
  section: HomepageSection;
}

export const InfoBlockSection: React.FC<Props> = ({ section }) => (
  <section className="relative min-h-[calc(100vh-4rem)] overflow-hidden border-b border-border py-16">
    <div className="absolute right-0 top-0 h-72 w-72 -translate-y-1/3 translate-x-1/3 rounded-full bg-primary/[0.02] blur-[120px]" />
    <div className="relative mx-auto flex min-h-[calc(100vh-4rem-8rem)] max-w-7xl items-center px-4 sm:px-6 lg:px-8">
      <div className="grid items-center gap-12 lg:grid-cols-2 lg:gap-16">
        <div>
          <h2 className="bg-gradient-to-b from-foreground to-foreground/80 bg-clip-text text-3xl font-extrabold text-transparent sm:text-5xl">
            {section.title}
          </h2>
          {section.subtitle && (
            <p className="mt-3 text-lg text-muted-foreground">{section.subtitle}</p>
          )}
          {section.description && (
            <p className="mt-4 text-base leading-relaxed text-muted-foreground">{section.description}</p>
          )}
          {section.imageUrl && (
            <img
              src={section.imageUrl}
              alt={section.title}
              className="mt-8 w-full rounded-xl border border-border object-cover lg:hidden"
            />
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
        <div className="hidden lg:flex lg:items-center lg:justify-center">
          <div className="flex h-72 w-72 items-center justify-center rounded-2xl bg-primary/5">
            {section.imageUrl ? (
              <img
                src={section.imageUrl}
                alt={section.title}
                className="h-full w-full rounded-2xl border border-border object-cover"
              />
            ) : (
              <InfoViz className="h-48 w-48" />
            )}
          </div>
        </div>
      </div>
    </div>
  </section>
);
