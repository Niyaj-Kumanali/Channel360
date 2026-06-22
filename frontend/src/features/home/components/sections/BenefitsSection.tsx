import React, { useEffect, useRef, useState } from 'react';
import type { HomepageSection } from '@/features/cms/types/cms.types';

interface Props {
  section: HomepageSection;
}

interface Benefit {
  title: string;
  description: string;
}

function useInView(threshold = 0.1): [React.RefObject<HTMLDivElement | null>, boolean] {
  const ref = useRef<HTMLDivElement | null>(null);
  const [inView, setInView] = useState(false);

  useEffect(() => {
    const el = ref.current;
    if (!el) return;
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          setInView(true);
          observer.unobserve(el);
        }
      },
      { threshold }
    );
    observer.observe(el);
    return () => observer.disconnect();
  }, [threshold]);

  return [ref, inView];
}

function TimelineViz({ className }: { className?: string }) {
  return (
    <svg viewBox="0 0 200 120" className={className} fill="none">
      <path d="M20 60 Q50 40 80 55 Q110 70 140 50 Q170 30 190 45" className="stroke-primary/40" strokeWidth="1.5" />
      <circle cx="20" cy="60" r="8" className="fill-primary/40 stroke-primary/60" strokeWidth="1.5" />
      <circle cx="80" cy="55" r="6" className="fill-primary/35 stroke-primary/50" strokeWidth="1" />
      <circle cx="140" cy="50" r="6" className="fill-primary/35 stroke-primary/50" strokeWidth="1" />
      <circle cx="190" cy="45" r="9" className="fill-primary/60 stroke-primary" strokeWidth="2" />
      <path d="M20 60 Q50 80 80 65 Q110 50 140 70 Q170 90 190 75" className="stroke-primary/20" strokeWidth="1" strokeDasharray="3 3" />
      <circle cx="190" cy="75" r="4" className="fill-primary/30" />
      <circle cx="80" cy="55" r="2.5" className="fill-primary/70" />
      <circle cx="140" cy="50" r="2.5" className="fill-primary/70" />
      <circle cx="20" cy="60" r="2.5" className="fill-primary/70" />
    </svg>
  );
}

function HubViz({ className }: { className?: string }) {
  return (
    <svg viewBox="0 0 80 80" className={className} fill="none">
      <circle cx="40" cy="40" r="20" className="stroke-primary/25" strokeWidth="1" strokeDasharray="3 2" />
      <circle cx="40" cy="40" r="12" className="fill-primary/35 stroke-primary/55" strokeWidth="1.5" />
      <circle cx="40" cy="40" r="5" className="fill-primary/80" />
      <line x1="40" y1="20" x2="40" y2="8" className="stroke-primary/25" strokeWidth="1" />
      <line x1="40" y1="60" x2="40" y2="72" className="stroke-primary/25" strokeWidth="1" />
      <line x1="20" y1="40" x2="8" y2="40" className="stroke-primary/25" strokeWidth="1" />
      <line x1="60" y1="40" x2="72" y2="40" className="stroke-primary/25" strokeWidth="1" />
      <line x1="25" y1="25" x2="16" y2="16" className="stroke-primary/20" strokeWidth="1" />
      <line x1="55" y1="25" x2="64" y2="16" className="stroke-primary/20" strokeWidth="1" />
      <line x1="25" y1="55" x2="16" y2="64" className="stroke-primary/20" strokeWidth="1" />
      <line x1="55" y1="55" x2="64" y2="64" className="stroke-primary/20" strokeWidth="1" />
      <circle cx="40" cy="8" r="4" className="fill-primary/45" />
      <circle cx="40" cy="72" r="4" className="fill-primary/45" />
      <circle cx="8" cy="40" r="4" className="fill-primary/45" />
      <circle cx="72" cy="40" r="4" className="fill-primary/45" />
      <circle cx="16" cy="16" r="3" className="fill-primary/30" />
      <circle cx="64" cy="16" r="3" className="fill-primary/30" />
      <circle cx="16" cy="64" r="3" className="fill-primary/30" />
      <circle cx="64" cy="64" r="3" className="fill-primary/30" />
    </svg>
  );
}

function WaveViz({ className }: { className?: string }) {
  return (
    <svg viewBox="0 0 100 60" className={className} fill="none">
      <rect x="6" y="30" width="8" height="20" rx="2" className="fill-primary/25" />
      <rect x="18" y="22" width="8" height="28" rx="2" className="fill-primary/35" />
      <rect x="30" y="14" width="8" height="36" rx="2" className="fill-primary/45" />
      <rect x="42" y="8" width="8" height="42" rx="2" className="fill-primary/55" />
      <rect x="54" y="18" width="8" height="32" rx="2" className="fill-primary/40" />
      <rect x="66" y="26" width="8" height="24" rx="2" className="fill-primary/30" />
      <rect x="78" y="16" width="8" height="34" rx="2" className="fill-primary/35" />
      <rect x="90" y="10" width="8" height="40" rx="2" className="fill-primary/50" />
      <path d="M6 40 L20 40 L24 30 L34 30 L38 20 L48 20 L52 32 L62 32 L66 24 L76 24 L80 36 L90 36" className="stroke-primary/70" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  );
}

function ShieldViz({ className }: { className?: string }) {
  return (
    <svg viewBox="0 0 60 80" className={className} fill="none">
      <path d="M30 6 L6 16 V34 C6 52 16 64 30 74 C44 64 54 52 54 34 V16 Z" className="fill-primary/10 stroke-primary/35" strokeWidth="1.5" />
      <path d="M30 14 L14 21 V34 C14 48 20 56 30 66 C40 56 46 48 46 34 V21 Z" className="fill-primary/18 stroke-primary/45" strokeWidth="1" />
      <circle cx="30" cy="34" r="7" className="fill-primary/35" />
      <path d="M27 34 L30 37 L35 30" className="stroke-background" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
      <line x1="8" y1="28" x2="16" y2="26" className="stroke-primary/18" strokeWidth="1" />
      <line x1="8" y1="36" x2="16" y2="38" className="stroke-primary/18" strokeWidth="1" />
      <line x1="52" y1="28" x2="44" y2="26" className="stroke-primary/18" strokeWidth="1" />
      <line x1="52" y1="36" x2="44" y2="38" className="stroke-primary/18" strokeWidth="1" />
      <circle cx="30" cy="34" r="2" className="fill-primary/70" />
    </svg>
  );
}

function GrowthViz({ className }: { className?: string }) {
  return (
    <svg viewBox="0 0 250 100" className={className} fill="none">
      <rect x="15" y="60" width="22" height="30" rx="2" className="fill-primary/15" />
      <rect x="42" y="45" width="22" height="45" rx="2" className="fill-primary/25" />
      <rect x="69" y="35" width="22" height="55" rx="2" className="fill-primary/35" />
      <rect x="96" y="25" width="22" height="65" rx="2" className="fill-primary/45" />
      <rect x="123" y="20" width="22" height="70" rx="2" className="fill-primary/50" />
      <rect x="150" y="30" width="22" height="60" rx="2" className="fill-primary/40" />
      <rect x="177" y="15" width="22" height="75" rx="2" className="fill-primary/55" />
      <rect x="204" y="10" width="22" height="80" rx="2" className="fill-primary/60" />
      <path d="M26 60 L53 45 L80 35 L107 25 L134 20 L161 30 L188 15 L215 10" className="stroke-primary/70" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
      <circle cx="215" cy="10" r="4" className="fill-primary/70" />
      <line x1="10" y1="90" x2="230" y2="90" className="stroke-primary/8" strokeWidth="0.5" />
      <line x1="10" y1="70" x2="230" y2="70" className="stroke-primary/8" strokeWidth="0.5" />
      <line x1="10" y1="50" x2="230" y2="50" className="stroke-primary/8" strokeWidth="0.5" />
      <line x1="10" y1="30" x2="230" y2="30" className="stroke-primary/8" strokeWidth="0.5" />
      <line x1="10" y1="10" x2="230" y2="10" className="stroke-primary/8" strokeWidth="0.5" />
    </svg>
  );
}

const vizMap = [TimelineViz, HubViz, WaveViz, ShieldViz, GrowthViz];

const cardConfigs = [
  { span: 'lg:col-span-2 lg:row-span-2 sm:col-span-2', align: 'text-left items-start', pad: 'p-10', titleSize: 'text-xl', gradient: true },
  { span: 'lg:col-span-1', align: 'text-center items-center', pad: 'p-8', titleSize: 'text-base', gradient: false },
  { span: 'lg:col-span-1', align: 'text-center items-center', pad: 'p-8', titleSize: 'text-base', gradient: false },
  { span: 'lg:col-span-1', align: 'text-center items-center', pad: 'p-8', titleSize: 'text-sm', gradient: false },
  { span: 'lg:col-span-2 sm:col-span-2', align: 'text-left items-start', pad: 'p-10', titleSize: 'text-lg', gradient: false },
];

export const BenefitsSection: React.FC<Props> = ({ section }) => {
  const [sectionRef, inView] = useInView(0.05);

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
    <section
      ref={sectionRef}
      className="relative flex min-h-[calc(100vh-4rem)] flex-col items-center justify-center overflow-hidden bg-background py-16"
    >
      <div className="absolute top-1/2 left-1/2 -z-10 h-[600px] w-[900px] -translate-x-1/2 -translate-y-1/2 rounded-full bg-primary/[0.03] blur-[160px] pointer-events-none" />

      <div className="mx-auto w-full max-w-7xl px-4 sm:px-6 lg:px-8">
        <div
          className={`mx-auto max-w-3xl text-center transition-all duration-1000 ease-out-expo ${
            inView ? 'translate-y-0 opacity-100' : 'translate-y-8 opacity-0'
          }`}
        >
          <h2 className="mt-4 text-3xl font-extrabold tracking-tight bg-gradient-to-b from-foreground to-foreground/80 bg-clip-text text-transparent sm:text-5xl">
            {section.title}
          </h2>
          {section.subtitle && (
            <p className="mt-4 text-lg text-muted-foreground max-w-xl mx-auto leading-relaxed">
              {section.subtitle}
            </p>
          )}
        </div>

        <div className="mt-14 grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {benefits.map((benefit, index) => {
            const Viz = vizMap[index];
            const delay = index * 120;
            const cfg = cardConfigs[index] || cardConfigs[1];

            return (
              <div
                key={benefit.title}
                className={`group relative flex overflow-hidden rounded-xl border bg-card transition-all duration-600 ease-out hover:border-primary/30 hover:shadow-[0_0_15px_-5px] hover:shadow-primary/15 ${inView ? 'translate-y-0 opacity-100' : 'translate-y-8 opacity-0'} ${cfg.span} ${cfg.align} ${cfg.pad} ${cfg.gradient ? 'border-primary/20 bg-gradient-to-br from-card to-primary/[0.02]' : ''} ${index === 0 ? 'justify-between' : ''} flex-col`}
                style={{
                  transitionDuration: '600ms',
                  transitionDelay: `${delay}ms`,
                }}
              >
                {Viz && (
                  <div className={`absolute pointer-events-none select-none transition-all duration-1000 ease-out group-hover:scale-105 group-hover:opacity-60 ${index === 0 ? 'inset-0 w-full h-full opacity-40' : index === 4 ? 'right-0 bottom-0 w-62 h-40 opacity-40' : 'right-0 bottom-0 w-40 h-32 opacity-40'}`}>
                    <Viz className="h-full w-full" />
                  </div>
                )}

                {index === 0 ? (
                  <>
                    <div className="relative z-10">
                      <h3 className="text-xl font-semibold text-foreground">
                        <span className="text-primary">{benefit.title.split(' ')[0]}</span>{' '}
                        {benefit.title.split(' ').slice(1).join(' ')}
                      </h3>
                      <p className="mt-2 text-sm leading-relaxed text-muted-foreground max-w-md">{benefit.description}</p>
                    </div>
                  </>
                ) : index === 4 ? (
                  <>
                    <div className="relative z-10 flex-1">
                      <h3 className={`relative z-10 ${cfg.titleSize} font-semibold text-foreground`}>{benefit.title}</h3>
                      <p className="relative z-10 mt-2 text-left text-sm leading-relaxed text-muted-foreground max-w-sm">{benefit.description}</p>
                    </div>
                  </>
                ) : (
                  <>
                    <h3 className={`relative z-10 ${cfg.titleSize} font-semibold text-foreground`}>{benefit.title}</h3>
                    <p className="relative z-10 mt-2 text-sm leading-relaxed text-muted-foreground max-w-xs">{benefit.description}</p>
                  </>
                )}
              </div>
            );
          })}
        </div>
      </div>
    </section>
  );
};
