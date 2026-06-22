import React, { useEffect, useRef, useState } from 'react';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/Button';
import type { HomepageSection } from '@/features/cms/types/cms.types';

interface ContactMethod {
  type: string;
  label: string;
  value: string;
}

interface Props {
  section: HomepageSection;
}

const defaults: ContactMethod[] = [
  { type: 'email', label: 'Email Address', value: 'niyajkumanali@gmail.com' },
  { type: 'phone', label: 'Phone Number', value: '+91 8217097121' },
  { type: 'address', label: 'HQ Location', value: 'Nipani, Belagavi, Karnataka, India, 591237' },
  { type: 'linkedin', label: 'LinkedIn', value: 'linkedin.com/in/niyaj-kumanali' },
  { type: 'github', label: 'GitHub Profile', value: 'github.com/Niyaj-Kumanali' },
  { type: 'website', label: 'Digital Portfolio', value: 'niyazdev.vercel.app' },
];

const icons: Record<string, React.ReactNode> = {
  email: (
    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <rect x="2" y="4" width="20" height="16" rx="2" /><path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7" />
    </svg>
  ),
  phone: (
    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z" />
    </svg>
  ),
  address: (
    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z" /><circle cx="12" cy="10" r="3" />
    </svg>
  ),
  linkedin: (
    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
      <path d="M19 0h-14c-2.761 0-5 2.239-5 5v14c0 2.761 2.239 5 5 5h14c2.762 0 5-2.239 5-5v-14c0-2.761-2.238-5-5-5zm-11 19h-3v-11h3v11zm-1.5-12.268c-.966 0-1.75-.79-1.75-1.764s.784-1.764 1.75-1.764 1.75.79 1.75 1.764-.783 1.764-1.75 1.764zm13.5 12.268h-3v-5.604c0-3.368-4-3.113-4 0v5.604h-3v-11h3v1.765c1.396-2.586 7-2.777 7 2.476v6.759z" />
    </svg>
  ),
  github: (
    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
      <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z" />
    </svg>
  ),
  website: (
    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <circle cx="12" cy="12" r="10" /><line x1="2" x2="22" y1="12" y2="12" /><path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z" />
    </svg>
  ),
};

function useInView(threshold = 0.05): [React.RefObject<HTMLDivElement | null>, boolean] {
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

const ContactUtilityCard: React.FC<{ method: ContactMethod }> = ({ method }) => {
  const [copied, setCopied] = useState(false);

  const handleCopy = async (e: React.MouseEvent) => {
    if (method.type === 'address') return;
    e.preventDefault();
    try {
      await navigator.clipboard.writeText(method.value);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch {
      void 0;
    }
  };

  const baseHref =
    method.type === 'email' ? `mailto:${method.value}` :
    method.type === 'phone' ? `tel:${method.value.replace(/\s/g, '')}` :
    `https://google.com/maps/search/?api=1&query=${encodeURIComponent(method.value)}`;

  return (
    <div className="group relative flex items-center justify-between rounded-2xl border border-border bg-card p-5 transition-all duration-300 hover:border-primary/40 hover:shadow-lg hover:shadow-primary/5">
      <div className="flex items-center gap-4 min-w-0">
        <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-xl border border-primary/20 bg-primary/5 text-primary group-hover:bg-primary/10 group-hover:scale-105 transition-all duration-300">
          {icons[method.type] || icons.website}
        </div>
        <div className="flex flex-col min-w-0">
          <span className="text-[10px] font-bold tracking-widest text-muted-foreground uppercase">{method.label}</span>
          <a
            href={baseHref}
            target={method.type === 'address' ? '_blank' : undefined}
            rel="noopener noreferrer"
            className="text-base font-semibold text-foreground mt-0.5 hover:text-primary transition-colors truncate break-all"
          >
            {method.value}
          </a>
        </div>
      </div>

      {method.type !== 'address' && (
        <button
          onClick={handleCopy}
          type="button"
          className="flex h-9 w-9 shrink-0 items-center justify-center rounded-xl border border-border bg-background text-muted-foreground opacity-0 group-hover:opacity-100 hover:text-primary hover:border-primary/30 transition-all duration-200"
          title={`Copy ${method.label}`}
        >
          {copied ? (
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" className="text-emerald-500">
              <polyline points="20 6 9 17 4 12" />
            </svg>
          ) : (
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <rect x="9" y="9" width="13" height="13" rx="2" ry="2" /><path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1" />
            </svg>
          )}
        </button>
      )}
    </div>
  );
};

export const ContactSection: React.FC<Props> = ({ section }) => {
  const [sectionRef, inView] = useInView(0.05);

  let methods: ContactMethod[];
  try {
    const parsed = JSON.parse(section.description || '');
    methods = Array.isArray(parsed) ? parsed : defaults;
  } catch {
    methods = defaults;
  }

  const baselineChannels = methods.filter(m => ['email', 'phone', 'address'].includes(m.type));
  const networkProfiles = methods.filter(m => !['email', 'phone', 'address'].includes(m.type));

  return (
    <section
      ref={sectionRef}
      className="relative flex min-h-[calc(100vh-4rem)] flex-col items-center justify-center overflow-hidden bg-background py-20 lg:py-32"
    >
      <div className="absolute top-1/4 right-1/4 -z-10 h-[550px] w-[550px] rounded-full bg-primary/10 blur-[130px] pointer-events-none" />
      <div className="absolute bottom-1/4 left-1/4 -z-10 h-[450px] w-[450px] rounded-full bg-primary/5 blur-[140px] pointer-events-none" />

      <div className="mx-auto w-full max-w-6xl px-4 sm:px-6 lg:px-8 relative z-10">
        <div className="grid grid-cols-1 items-center gap-12 lg:grid-cols-12 lg:gap-16">

          <div
            className={`flex flex-col justify-center lg:col-span-5 transition-all duration-1000 ease-out ${
              inView ? 'translate-x-0 opacity-100' : '-translate-x-8 opacity-0'
            }`}
          >
            <div className="flex items-center gap-2 text-xs font-bold uppercase tracking-widest text-primary mb-3">
              <span className="h-2 w-2 rounded-full bg-emerald-500 animate-pulse" />
              Active Connections
            </div>

            <h2 className="text-4xl font-extrabold tracking-tight text-foreground sm:text-5xl">
              {section.title}
            </h2>

            {section.subtitle && (
              <p className="mt-4 text-base leading-relaxed text-muted-foreground max-w-md">
                {section.subtitle}
              </p>
            )}

            {(section.buttonText || section.buttonUrl) && (
              <div className="mt-8">
                {section.buttonUrl ? (
                  <Link to={section.buttonUrl} className="inline-block sm:w-auto w-full">
                    <Button size="lg" className="w-full gap-2 font-semibold shadow-lg shadow-primary/10 group px-8">
                      {section.buttonText || 'Initiate Discussion'}
                      <svg className="transition-transform duration-300 group-hover:translate-x-1" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                        <path d="M5 12h14" /><path d="m12 5 7 7-7 7" />
                      </svg>
                    </Button>
                  </Link>
                ) : (
                  <Button size="lg" className="sm:w-auto w-full gap-2 font-semibold group px-8">
                    {section.buttonText}
                    <svg className="transition-transform duration-300 group-hover:translate-x-1" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                      <path d="M5 12h14" /><path d="m12 5 7 7-7 7" />
                    </svg>
                  </Button>
                )}
              </div>
            )}
          </div>

          <div
            className={`space-y-4 lg:col-span-7 transition-all duration-1000 ease-out delay-100 ${
              inView ? 'translate-x-0 opacity-100' : 'translate-x-8 opacity-0'
            }`}
          >
            <div className="flex flex-col gap-3.5">
              {baselineChannels.map((method) => (
                <ContactUtilityCard key={method.type} method={method} />
              ))}
            </div>

            {networkProfiles.length > 0 && (
              <div className="mt-6 flex items-center justify-between gap-4 border-t border-border pt-5">
                <span className="text-xs font-bold uppercase tracking-widest text-muted-foreground">
                  Network Nodes
                </span>

                <div className="flex items-center gap-2">
                  {networkProfiles.map((method) => {
                    const href = method.value.startsWith('http') ? method.value : `https://${method.value}`;
                    return (
                      <a
                        key={method.type}
                        href={href}
                        target="_blank"
                        rel="noopener noreferrer"
                        aria-label={`Open external link to ${method.label}`}
                        className="flex h-11 w-11 items-center justify-center rounded-xl border border-border bg-card text-muted-foreground hover:text-primary hover:border-primary/30 hover:bg-primary/5 transition-all duration-300"
                        title={method.label}
                      >
                        {icons[method.type] || icons.website}
                      </a>
                    );
                  })}
                </div>
              </div>
            )}
          </div>

        </div>
      </div>
    </section>
  );
};

export default ContactSection;
