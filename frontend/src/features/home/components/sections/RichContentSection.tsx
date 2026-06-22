import React from 'react';
import { Link } from 'react-router-dom';
import type { HomepageSection } from '@/features/cms/types/cms.types';

function DocumentViz({ className }: { className?: string }) {
  return (
    <svg className={className} viewBox="0 0 120 120" fill="none" xmlns="http://www.w3.org/2000/svg">
      <rect x="20" y="8" width="80" height="104" rx="6" className="fill-primary/10 stroke-primary/30" strokeWidth="2" />
      <rect x="28" y="18" width="64" height="6" rx="2" className="fill-primary/40" />
      <rect x="28" y="30" width="44" height="4" rx="2" className="fill-muted-foreground/20" />
      <rect x="28" y="40" width="56" height="4" rx="2" className="fill-muted-foreground/20" />
      <rect x="28" y="50" width="36" height="4" rx="2" className="fill-muted-foreground/20" />
      <line x1="28" y1="64" x2="78" y2="64" className="stroke-border" strokeWidth="1.5" strokeDasharray="4 3" />
      <rect x="28" y="74" width="52" height="4" rx="2" className="fill-muted-foreground/20" />
      <rect x="28" y="84" width="60" height="4" rx="2" className="fill-muted-foreground/20" />
      <rect x="28" y="94" width="40" height="4" rx="2" className="fill-muted-foreground/20" />
      <circle cx="88" cy="22" r="8" className="fill-primary/15 stroke-primary/40" strokeWidth="1.5" />
      <path d="M85 22 L88 25 L92 19" className="stroke-primary/60" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  );
}

interface Props {
  section: HomepageSection;
  isFullPage?: boolean;
}

function parseQAPairs(html: string): string[] {
  return html.split(/(?=<h3)/).filter(Boolean);
}

export const RichContentSection: React.FC<Props> = ({ section, isFullPage }) => {
  const qaPairs = section.description ? parseQAPairs(section.description) : [];
  const maxItems = 2;
  const visiblePairs = isFullPage ? qaPairs : qaPairs.slice(0, maxItems);

  return (
    <section className={`relative ${isFullPage ? 'min-h-screen' : 'min-h-[calc(100vh-4rem)]'} border-b border-border py-16 ${isFullPage ? '' : 'overflow-hidden'}`}>
      <div className="absolute bottom-0 left-0 h-72 w-72 translate-x-[-30%] translate-y-[30%] rounded-full bg-primary/[0.02] blur-[120px]" />
      <div className={`relative mx-auto flex ${isFullPage ? 'min-h-0' : 'min-h-[calc(100vh-4rem-8rem)]'} max-w-7xl items-center px-4 sm:px-6 lg:px-8`}>
        <div className="mx-auto w-full max-w-4xl">
          <div className="mb-8 flex items-center gap-4">
            <div className="flex h-14 w-14 shrink-0 items-center justify-center rounded-xl bg-primary/10">
              <DocumentViz className="h-8 w-8" />
            </div>
            <h2 className="bg-gradient-to-b from-foreground to-foreground/80 bg-clip-text text-3xl font-extrabold text-transparent sm:text-5xl">
              {section.title}
            </h2>
          </div>
          {visiblePairs.length > 0 && (
            <>
              <div className="divide-y divide-border">
                {visiblePairs.map((pair, i) => (
                  <div key={i} className="py-5 first:pt-0 last:pb-0">
                    <div
                      className="prose prose-sm max-w-none leading-relaxed text-muted-foreground prose-headings:text-foreground prose-headings:font-bold prose-p:mt-2 prose-p:leading-relaxed prose-a:text-primary"
                      dangerouslySetInnerHTML={{ __html: pair }}
                    />
                  </div>
                ))}
              </div>
              {!isFullPage && qaPairs.length > maxItems && (
                <div className="mt-8">
                  <Link
                    to="/faq"
                    className="inline-flex items-center gap-2 rounded-lg bg-primary px-6 py-3 text-sm font-medium text-primary-foreground hover:bg-primary/90"
                  >
                    View All FAQs &rarr;
                  </Link>
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </section>
  );
};
