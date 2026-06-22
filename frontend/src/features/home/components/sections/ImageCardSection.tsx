import React from 'react';
import type { HomepageSection } from '@/features/cms/types/cms.types';

function ImagePlaceholderViz({ className }: { className?: string }) {
  return (
    <svg className={className} viewBox="0 0 120 120" fill="none" xmlns="http://www.w3.org/2000/svg">
      <rect x="10" y="15" width="100" height="90" rx="8" className="fill-primary/10 stroke-primary/30" strokeWidth="2" />
      <rect x="18" y="23" width="84" height="74" rx="4" className="fill-primary/5 stroke-primary/20" strokeWidth="1.5" />
      <circle cx="42" cy="48" r="10" className="fill-primary/30" />
      <path d="M18 82 L42 62 L58 74 L72 58 L102 82" className="stroke-primary/40" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
      <path d="M72 58 L80 50 L102 68" className="stroke-primary/30" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
      <rect x="82" y="26" width="16" height="16" rx="3" className="fill-primary/20 stroke-primary/30" strokeWidth="1.5" />
      <circle cx="90" cy="34" r="3" className="fill-primary/50" />
      <path d="M55 97 L60 103 L65 97" className="stroke-primary/40" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  );
}

interface Props {
  section: HomepageSection;
}

export const ImageCardSection: React.FC<Props> = ({ section }) => (
  <section className="relative min-h-[calc(100vh-4rem)] overflow-hidden border-b border-border py-16">
    <div className="relative mx-auto flex min-h-[calc(100vh-4rem-8rem)] max-w-7xl items-center px-4 sm:px-6 lg:px-8">
      <div className="mx-auto w-full max-w-4xl">
        <div className="overflow-hidden rounded-2xl border border-border bg-card shadow-sm">
          {section.imageUrl ? (
            <img
              src={section.imageUrl}
              alt={section.title}
              className="h-80 w-full object-cover"
            />
          ) : (
            <div className="flex h-80 items-center justify-center bg-gradient-to-br from-primary/[0.03] to-primary/[0.08]">
              <ImagePlaceholderViz className="h-32 w-32" />
            </div>
          )}
          <div className="p-8 sm:p-10">
            <h2 className="bg-gradient-to-b from-foreground to-foreground/80 bg-clip-text text-2xl font-extrabold text-transparent sm:text-4xl">
              {section.title}
            </h2>
            {section.description && (
              <p className="mt-3 text-base leading-relaxed text-muted-foreground">{section.description}</p>
            )}
            {(section.buttonText || section.buttonUrl) && (
              <div className="mt-5">
                {section.buttonUrl ? (
                  <a
                    href={section.buttonUrl}
                    className="inline-flex items-center gap-2 text-sm font-medium text-primary hover:text-primary/80"
                  >
                    {section.buttonText || 'View Details'} &rarr;
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
