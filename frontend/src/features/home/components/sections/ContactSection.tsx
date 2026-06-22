import React, { useState } from 'react';
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
  { type: 'github', label: 'GitHub', value: 'github.com/Niyaj-Kumanali' },
  { type: 'website', label: 'Portfolio', value: 'niyazdev.vercel.app' },
];

const icons: Record<string, React.ReactNode> = {
  email: (
    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <rect x="2" y="4" width="20" height="16" rx="2" /><path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7" />
    </svg>
  ),
  phone: (
    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z" />
    </svg>
  ),
  address: (
    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
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

const ContactCard: React.FC<{ method: ContactMethod }> = ({ method }) => {
  const [copied, setCopied] = useState(false);

  const handleClick = () => {
    if (method.type === 'address') {
      window.open(`https://google.com/maps/search/?api=1&query=${encodeURIComponent(method.value)}`, '_blank');
      return;
    }
    if (method.type === 'email') {
      window.location.href = `mailto:${method.value}`;
      return;
    }
    if (method.type === 'phone') {
      window.location.href = `tel:${method.value.replace(/\s/g, '')}`;
      return;
    }
  };

  const handleCopy = async (e: React.MouseEvent) => {
    e.stopPropagation();
    try {
      await navigator.clipboard.writeText(method.value);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch {
      void 0;
    }
  };

  return (
    <div
      className="group relative flex cursor-pointer items-center justify-between rounded-xl border border-border bg-card p-4 border-l-4 border-l-primary/40"
      onClick={handleClick}
    >
      <div className="flex items-center gap-4 min-w-0">
        <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg border border-primary/20 bg-gradient-to-br from-primary/10 to-primary/5 text-primary">
          {icons[method.type] || icons.website}
        </div>
        <div className="flex flex-col min-w-0">
          <span className="text-[10px] font-bold tracking-widest text-muted-foreground uppercase">{method.label}</span>
          <span className="text-sm font-semibold text-foreground mt-0.5 truncate">{method.value}</span>
        </div>
      </div>
      <button
        onClick={handleCopy}
        type="button"
        className="flex h-8 w-8 shrink-0 items-center justify-center rounded-lg border border-border bg-background text-muted-foreground"
        title={`Copy ${method.label}`}
      >
        {copied ? (
          <svg xmlns="http://www.w3.org/2000/svg" width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" className="text-emerald-500">
            <polyline points="20 6 9 17 4 12" />
          </svg>
        ) : (
          <svg xmlns="http://www.w3.org/2000/svg" width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <rect x="9" y="9" width="13" height="13" rx="2" ry="2" /><path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1" />
          </svg>
        )}
      </button>
    </div>
  );
};

function highlightTitle(title: string) {
  const parts = title.split('Channel360');
  if (parts.length < 2) return title;
  return (
    <>
      {parts[0]}<span className="text-primary">Channel360</span>{parts[1]}
    </>
  );
}

export const ContactSection: React.FC<Props> = ({ section }) => {
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
    <section className="relative flex min-h-[calc(100vh-4rem)] flex-col items-center justify-center overflow-hidden bg-background py-20 lg:py-32">
      <div className="absolute top-1/4 right-1/4 -z-10 h-[500px] w-[500px] rounded-full bg-primary/10 blur-[130px] pointer-events-none" />
      <div className="absolute bottom-1/4 left-1/4 -z-10 h-[400px] w-[400px] rounded-full bg-primary/5 blur-[140px] pointer-events-none" />

      <div className="absolute inset-0 pointer-events-none overflow-hidden">
        <div className="absolute left-[8%] top-[20%] h-36 w-36 rounded-full bg-gradient-to-br from-primary/30 to-primary/10 blur-2xl animate-blob-1" />
        <div className="absolute right-[15%] top-[30%] h-20 w-20 rounded-full bg-gradient-to-br from-primary/25 to-primary/5 blur-xl animate-blob-2" style={{ animationDelay: '-6s' }} />
        <div className="absolute left-[40%] top-[55%] h-16 w-16 rounded-full bg-gradient-to-br from-primary/20 to-primary/5 blur-lg animate-blob-3" style={{ animationDelay: '-12s' }} />
        <div className="absolute right-[25%] bottom-[25%] h-32 w-32 rounded-full bg-gradient-to-br from-primary/15 to-transparent blur-2xl animate-blob-4" style={{ animationDelay: '-4s' }} />
      </div>

      <div className="mx-auto w-full max-w-7xl px-4 sm:px-6 lg:px-8 relative z-10">
        <div className="grid grid-cols-1 items-center gap-12 lg:grid-cols-12 lg:gap-16">

          <div className="lg:col-span-5 flex flex-col">
            <h2 className="text-5xl font-extrabold tracking-tight text-foreground sm:text-6xl">
              {highlightTitle(section.title)}
            </h2>
            {section.subtitle && (
              <p className="mt-4 text-base leading-relaxed text-muted-foreground max-w-md">
                {section.subtitle}
              </p>
            )}
            {(section.buttonText || section.buttonUrl) && (
              <div className="mt-8">
                {section.buttonUrl ? (
                  <Link to={section.buttonUrl}>
                    <Button size="lg" className="gap-2 font-semibold shadow-lg shadow-primary/10 px-8">
                      {section.buttonText || 'Initiate Discussion'}
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                        <path d="M5 12h14" /><path d="m12 5 7 7-7 7" />
                      </svg>
                    </Button>
                  </Link>
                ) : (
                  <Button size="lg" className="gap-2 font-semibold px-8">
                    {section.buttonText}
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                      <path d="M5 12h14" /><path d="m12 5 7 7-7 7" />
                    </svg>
                  </Button>
                )}
              </div>
            )}
          </div>

          <div className="lg:col-span-6 lg:col-start-7 flex flex-col gap-4">
            {baselineChannels.map((method) => (
              <ContactCard key={method.type} method={method} />
            ))}

            {networkProfiles.length > 0 && (
              <div className="mt-2 flex items-center justify-between pt-4">
                <span className="text-xs font-bold uppercase tracking-widest text-muted-foreground">
                  Network Profiles
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
                        className="flex h-10 w-10 items-center justify-center rounded-lg border border-border bg-card text-muted-foreground"
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
      <style>{`
        @keyframes blob-1 {
          0%, 100% { transform: translate(0, 0) scale(1); }
          25% { transform: translate(15px, -25px) scale(1.08); }
          50% { transform: translate(-10px, 10px) scale(0.92); }
          75% { transform: translate(20px, 15px) scale(1.04); }
        }
        @keyframes blob-2 {
          0%, 100% { transform: translate(0, 0) scale(1) rotate(0deg); }
          33% { transform: translate(-20px, -15px) scale(1.1) rotate(5deg); }
          66% { transform: translate(15px, 20px) scale(0.9) rotate(-3deg); }
        }
        @keyframes blob-3 {
          0%, 100% { transform: translate(0, 0) scale(1); }
          50% { transform: translate(12px, -18px) scale(1.12); }
        }
        @keyframes blob-4 {
          0%, 100% { transform: translate(0, 0) scale(1); }
          25% { transform: translate(-18px, 10px) scale(0.95); }
          50% { transform: translate(10px, -15px) scale(1.05); }
          75% { transform: translate(-8px, 20px) scale(0.98); }
        }
        .animate-blob-1 { animation: blob-1 18s ease-in-out infinite; }
        .animate-blob-2 { animation: blob-2 14s ease-in-out infinite; }
        .animate-blob-3 { animation: blob-3 10s ease-in-out infinite; }
        .animate-blob-4 { animation: blob-4 16s ease-in-out infinite; }
      `}</style>
    </section>
  );
};

export default ContactSection;
