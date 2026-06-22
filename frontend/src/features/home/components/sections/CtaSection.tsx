import React from 'react';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/Button';
import type { HomepageSection } from '@/features/cms/types/cms.types';

interface Props {
  section: HomepageSection;
}

export const CtaSection: React.FC<Props> = ({ section }) => (
  <section className="flex min-h-[calc(100vh-4rem)] flex-col items-center justify-center">
    <div className="mx-auto w-full max-w-7xl px-4 sm:px-6 lg:px-8">
      <div className="relative overflow-hidden rounded-2xl px-8 py-16 text-center sm:px-16">
        <div className="relative">
          <h2 className="text-3xl font-bold text-foreground sm:text-4xl">{section.title}</h2>
          {section.description && (
            <p className="mx-auto mt-4 max-w-xl text-muted-foreground">{section.description}</p>
          )}
          <div className="mt-8 flex items-center justify-center gap-4">
            {(section.buttonText || section.buttonUrl) && (
              section.buttonUrl ? (
                <Link to={section.buttonUrl}>
                  <Button size="lg" className="gap-2">
                    {section.buttonText || 'Get Started'}
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                      <path d="M5 12h14" /><path d="m12 5 7 7-7 7" />
                    </svg>
                  </Button>
                </Link>
              ) : (
                <Button size="lg" className="gap-2">
                  {section.buttonText}
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <path d="M5 12h14" /><path d="m12 5 7 7-7 7" />
                  </svg>
                </Button>
              )
            )}
          </div>
        </div>
      </div>
    </div>
  </section>
);

export default CtaSection;
