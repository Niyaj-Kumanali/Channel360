import React from 'react';
import { Link } from 'react-router-dom';
import { Logo } from '@/components/ui/Logo';
import { useTheme } from '@/app/hooks/useTheme';
import type { HomepageSection } from '@/features/cms/types/cms.types';

interface FooterLink {
  label: string;
  url: string;
}

interface FooterGroup {
  title: string;
  links: FooterLink[];
}

interface SocialLink {
  label: string;
  url: string;
  icon: string;
}

interface FooterData {
  tagline: string;
  groups: FooterGroup[];
  socialLinks: SocialLink[];
}

const defaultFooterData: FooterData = {
  tagline: 'End-to-end visibility across your entire channel ecosystem — from manufacturer to end-customer activation.',
  groups: [
    {
      title: 'Platform',
      links: [
        { label: 'Home', url: '/' },
        { label: 'Features', url: '#' },
        { label: 'Pricing', url: '#' },
        { label: 'FAQ', url: '#' },
      ],
    },
    {
      title: 'Resources',
      links: [
        { label: 'Documentation', url: '#' },
        { label: 'API Reference', url: '#' },
        { label: 'Changelog', url: '#' },
        { label: 'Status', url: '#' },
      ],
    },
    {
      title: 'Company',
      links: [
        { label: 'About', url: '#' },
        { label: 'Blog', url: '#' },
        { label: 'Careers', url: '#' },
        { label: 'Contact', url: '#' },
      ],
    },
  ],
  socialLinks: [
    { label: 'Email', url: 'mailto:niyajkumanali@gmail.com', icon: 'email' },
    { label: 'LinkedIn', url: 'https://linkedin.com/in/niyaj-kumanali', icon: 'linkedin' },
    { label: 'GitHub', url: 'https://github.com/Niyaj-Kumanali', icon: 'github' },
    { label: 'Portfolio', url: 'https://niyazdev.vercel.app', icon: 'website' },
  ],
};

const socialIcons: Record<string, React.ReactNode> = {
  email: (
    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <rect x="2" y="4" width="20" height="16" rx="2" /><path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7" />
    </svg>
  ),
  linkedin: (
    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
      <path d="M19 0h-14c-2.761 0-5 2.239-5 5v14c0 2.761 2.239 5 5 5h14c2.762 0 5-2.239 5-5v-14c0-2.761-2.238-5-5-5zm-11 19h-3v-11h3v11zm-1.5-12.268c-.966 0-1.75-.79-1.75-1.764s.784-1.764 1.75-1.764 1.75.79 1.75 1.764-.783 1.764-1.75 1.764zm13.5 12.268h-3v-5.604c0-3.368-4-3.113-4 0v5.604h-3v-11h3v1.765c1.396-2.586 7-2.777 7 2.476v6.759z" />
    </svg>
  ),
  github: (
    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
      <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z" />
    </svg>
  ),
  website: (
    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <circle cx="12" cy="12" r="10" /><line x1="2" x2="22" y1="12" y2="12" /><path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z" />
    </svg>
  ),
};

interface Props {
  section?: HomepageSection;
}

export const Footer: React.FC<Props> = ({ section }) => {
  const { theme } = useTheme();
  const year = new Date().getFullYear();

  const parsed = section?.description
    ? (() => {
        try { return JSON.parse(section.description) as FooterData; } catch { return null; }
      })()
    : null;

  const data = parsed || defaultFooterData;

  return (
    <footer className="border-t border-border bg-muted/50">
      <div className="mx-auto max-w-7xl px-4 py-16 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 gap-12 sm:grid-cols-2 lg:grid-cols-12">

          <div className="flex flex-col gap-5 sm:col-span-2 lg:col-span-4">
            <Logo variant={theme === 'dark' ? 'light' : 'dark'} size="sm" />
            <p className="max-w-xs text-sm leading-relaxed text-muted-foreground">
              {data.tagline}
            </p>
            <div className="flex items-center gap-3">
              {data.socialLinks.map((link) => (
                <a
                  key={link.label}
                  href={link.url}
                  target="_blank"
                  rel="noopener noreferrer"
                  aria-label={link.label}
                  className="flex h-9 w-9 items-center justify-center rounded-lg border border-border text-muted-foreground hover:text-foreground hover:border-foreground/20 transition-colors"
                >
                  {socialIcons[link.icon] || socialIcons.website}
                </a>
              ))}
            </div>
          </div>

          {data.groups.map((group) => (
            <div key={group.title} className="flex flex-col gap-4 lg:col-span-2">
              <span className="text-xs font-bold uppercase tracking-widest text-foreground">
                {group.title}
              </span>
              <div className="flex flex-col gap-2.5">
                {group.links.map((link) => (
                  <Link
                    key={link.label}
                    to={link.url}
                    className="text-sm text-muted-foreground hover:text-foreground transition-colors w-fit"
                  >
                    {link.label}
                  </Link>
                ))}
              </div>
            </div>
          ))}

        </div>
      </div>

      <div className="border-t border-border">
        <div className="mx-auto flex max-w-7xl flex-col items-center justify-between gap-4 px-4 py-6 sm:flex-row sm:px-6 lg:px-8">
          <p className="text-xs text-muted-foreground">
            &copy; {year} Channel360. All rights reserved.
          </p>
          <div className="flex items-center gap-6 text-xs text-muted-foreground">
            <a href="#" className="hover:text-foreground transition-colors">Privacy Policy</a>
            <a href="#" className="hover:text-foreground transition-colors">Terms of Service</a>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
