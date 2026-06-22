import React from 'react';
import { BarChart3, Users, Gift, ShieldCheck, Network, Bell, Hexagon } from 'lucide-react';
import type { HomepageSection } from '@/features/cms/types/cms.types';

interface Props {
  section: HomepageSection;
}

const icons = [BarChart3, Users, Gift, ShieldCheck, Network, Bell];

interface Capability {
  title: string;
  description: string;
}

const defaults: Capability[] = [
  { title: 'Channel Analytics', description: 'Real-time dashboards, reports, and actionable insights across the entire channel network.' },
  { title: 'Partner Lifecycle', description: 'Streamlined onboarding, performance tracking, and relationship management for every partner.' },
  { title: 'Claims & Incentives', description: 'Automated rebate, claim, and incentive program management with real-time tracking.' },
  { title: 'Compliance Management', description: 'Automated compliance checks, audit trails, and regulatory reporting across markets.' },
  { title: 'Data Integration Hub', description: 'Centralized data ingestion from ERP, CRM, and external partner systems.' },
  { title: 'Smart Notifications', description: 'Configurable alerts for inventory thresholds, claim status, and partner activity.' },
];

export const BusinessAreasSection: React.FC<Props> = ({ section }) => {
  const items: Capability[] = section.description ? JSON.parse(section.description) : defaults;
  const r = 30;

  return (
    <section className="flex py-16 min-h-[calc(100vh-4rem)] flex-col items-center justify-center bg-background px-4 sm:px-6 lg:px-8">
      <div className="mx-auto w-full max-w-5xl">
        <div className="mx-auto max-w-2xl text-center">
          <h2 className="text-3xl font-bold text-foreground sm:text-4xl">{section.title}</h2>
          {section.subtitle && (
            <p className="mt-4 text-lg text-muted-foreground">{section.subtitle}</p>
          )}
        </div>

        <div className="relative mx-auto mt-16 aspect-square max-w-lg">
          <svg className="absolute inset-0 h-full w-full text-primary/30" viewBox="0 0 100 100" fill="none">
            {/* Orbit rings */}
            <circle cx="50" cy="50" r={r} stroke="currentColor" strokeWidth="0.25" strokeOpacity="0.5" fill="none" />
            <circle cx="50" cy="50" r={r * 0.65} stroke="currentColor" strokeWidth="0.2" strokeOpacity="0.35" fill="none" strokeDasharray="2 3" />
            <circle cx="50" cy="50" r={r * 0.35} stroke="currentColor" strokeWidth="0.15" strokeOpacity="0.2" fill="none" strokeDasharray="1.5 2.5" />

            {/* Spokes */}
            {items.map((_, i) => {
              const angle = (i * 60 - 90) * (Math.PI / 180);
              return <line key={`spoke-${i}`} x1={50 + r * 0.35 * Math.cos(angle)} y1={50 + r * 0.35 * Math.sin(angle)} x2={50 + r * Math.cos(angle)} y2={50 + r * Math.sin(angle)} stroke="currentColor" strokeWidth="0.2" strokeOpacity="0.4" />;
            })}

            {/* Curved arcs between adjacent nodes */}
            {items.map((_, i) => {
              const a1 = (i * 60 - 90) * (Math.PI / 180);
              const a2 = ((i + 1) % 6 * 60 - 90) * (Math.PI / 180);
              const x1 = 50 + r * Math.cos(a1), y1 = 50 + r * Math.sin(a1);
              const x2 = 50 + r * Math.cos(a2), y2 = 50 + r * Math.sin(a2);
              const mx = (x1 + x2) / 2, my = (y1 + y2) / 2;
              return <path key={`arc-${i}`} d={`M${x1} ${y1} Q${mx} ${my - (i % 2 === 0 ? 4 : -4)} ${x2} ${y2}`} stroke="currentColor" strokeWidth="0.2" strokeOpacity="0.5" fill="none" />;
            })}

            {/* Cross connections */}
            {[[0, 3], [1, 4], [2, 5], [0, 2], [1, 3], [2, 4]]
              .map(([a, b]) => {
                const angleA = (a * 60 - 90) * (Math.PI / 180);
                const angleB = (b * 60 - 90) * (Math.PI / 180);
                return { x1: 50 + r * Math.cos(angleA), y1: 50 + r * Math.sin(angleA), x2: 50 + r * Math.cos(angleB), y2: 50 + r * Math.sin(angleB) };
              })
              .map((line, idx) => (
                <line key={`cross-${idx}`} x1={line.x1} y1={line.y1} x2={line.x2} y2={line.y2} stroke="currentColor" strokeWidth="0.15" strokeOpacity="0.25" />
              ))}

            {/* Center glow */}
            <circle cx="50" cy="50" r="4" fill="currentColor" fillOpacity="0.04" />
            <circle cx="50" cy="50" r="1.5" fill="currentColor" fillOpacity="0.1" />
          </svg>

          {/* Center hub */}
          <div className="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2">
            <div className="flex h-11 w-11 items-center justify-center rounded-full border-2 border-primary/30 bg-card shadow-sm">
              <Hexagon className="h-5 w-5 text-primary" />
            </div>
          </div>

          {/* Nodes */}
          {items.map((item, i) => {
            const Icon = icons[i] || BarChart3;
            const angle = (i * 60 - 90) * (Math.PI / 180);

            return (
              <div key={item.title} className="absolute -translate-x-1/2 -translate-y-1/2" style={{ left: `${50 + r * Math.cos(angle)}%`, top: `${50 + r * Math.sin(angle)}%` }}>
                <div className="group flex flex-col items-center">
                  <div className="flex h-11 w-11 items-center justify-center rounded-full border-2 border-primary/30 bg-card shadow-sm transition-transform duration-200 hover:scale-110">
                    <Icon className="h-5 w-5 text-primary" />
                  </div>
                  <span className="mt-1.5 whitespace-nowrap rounded-md bg-card/80 px-2 py-0.5 text-xs font-semibold text-foreground shadow-sm backdrop-blur-sm">
                    {item.title}
                  </span>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </section>
  );
};

export default BusinessAreasSection;
