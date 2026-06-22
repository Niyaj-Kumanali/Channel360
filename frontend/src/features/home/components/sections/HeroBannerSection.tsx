import React from 'react';
import { Link } from 'react-router-dom';
import { ArrowRight, Globe } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { RotatingEarth } from '@/features/home/components/RotatingEarth';
import type { HomepageSection } from '@/features/cms/types/cms.types';

interface Props {
  section: HomepageSection;
}

function highlightTitle(title: string) {
  const parts = title.split('Your Channel Ecosystem');
  if (parts.length < 2) return title;
  return (
    <>
      {parts[0]}Your{' '}
      <span className="text-primary">Channel Ecosystem</span>
    </>
  );
}

export const HeroBannerSection: React.FC<Props> = ({ section }) => (
  <section className="relative min-h-[calc(100vh-4rem)] overflow-hidden">
    <RotatingEarth positionX={0.8} />
    <div className="absolute inset-y-0 left-0 w-[65%] bg-gradient-to-r from-background via-background/95 via-65% to-transparent z-10" />

    <div className="relative z-20 mx-auto flex min-h-[calc(100vh-4rem)] max-w-7xl items-center px-4 sm:px-6 lg:px-8">
      <div className="w-full max-w-xl">


        <h1 className="text-4xl font-bold tracking-tight text-foreground sm:text-5xl lg:text-6xl leading-[1.1]">
          {highlightTitle(section.title)}
        </h1>

        {section.description && (
          <p className="mt-6 text-base leading-relaxed text-muted-foreground sm:text-lg">
            {section.description}
          </p>
        )}

        <div className="mt-10 flex items-center gap-4">
          {(section.buttonText || section.buttonUrl) && (
            section.buttonUrl ? (
              <Link to={section.buttonUrl}>
                <Button size="lg" className="gap-2 px-8">
                  {section.buttonText || 'Get Started'} <ArrowRight className="h-4 w-4" />
                </Button>
              </Link>
            ) : (
              <Button size="lg" className="gap-2 px-8">
                {section.buttonText} <ArrowRight className="h-4 w-4" />
              </Button>
            )
          )}
        </div>


      </div>
    </div>
  </section>
);
