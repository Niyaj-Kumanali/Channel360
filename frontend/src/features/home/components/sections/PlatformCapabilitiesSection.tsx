import React, { useEffect, useRef, useState } from 'react';
import type { HomepageSection } from '@/features/cms/types/cms.types';

// ─── Types ────────────────────────────────────────────────────────────────────

interface Props {
  section: HomepageSection;
}

interface Capability {
  title: string;
  description: string;
}

// ─── Hook ─────────────────────────────────────────────────────────────────────

function useInView(threshold = 0.15): [React.RefObject<HTMLDivElement | null>, boolean] {
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

// ─── Visualizations ───────────────────────────────────────────────────────────

/* ─── Channel Analytics Dashboard (Preserving Bar Chart Dimensions Exactly) ─── */
function BarChartViz({ className }: { className?: string }) {
  return (
    <svg viewBox="0 0 200 160" className={className} fill="none">
      <defs>
        <filter id="glow-bar" x="-20%" y="-20%" width="140%" height="140%">
          <feGaussianBlur stdDeviation="2" result="blur" />
          <feComposite in="SourceGraphic" in2="blur" operator="over" />
        </filter>
      </defs>

      {/* ─── 1. TRENDS VIZ (Soft Background Stream Running Underneath) ─── */}
      <path d="M5 130 Q 35 90, 70 120 T 130 95 T 195 110" 
        className="stroke-primary/20" strokeWidth="1.2" fill="none" strokeDasharray="3 2" />
      <circle cx="130" cy="95" r="2" className="fill-primary/30" />

      {/* ─── 2. DOWNLOAD VIZ (Shifted Up/Right into Visible Bottom-Left Viewport) ─── */}
      <g opacity="0.85">
        {/* Progress track bars */}
        <rect x="15" y="146" width="35" height="2.5" rx="1" className="fill-primary/10" />
        <rect x="15" y="146" width="24" height="2.5" rx="1" className="fill-primary/70" filter="url(#glow-bar)" />
        <rect x="15" y="152" width="20" height="2" rx="1" className="fill-primary/30" />
        {/* Micro download arrow icon */}
        <path d="M58 145 L58 151 M55 148 L58 151 L61 148" className="stroke-primary/80" strokeWidth="1" strokeLinecap="round" strokeLinejoin="round" />
      </g>

      {/* ─── 3. TABLE DATA MATRIX VIZ (Top Right Space) ─── */}
      <g opacity="0.85">
        {/* Data lines grid row elements */}
        <line x1="145" y1="5" x2="195" y2="5" className="stroke-primary/25" strokeWidth="0.75" />
        <rect x="150" y="9" width="18" height="3" rx="0.5" className="fill-primary/40" />
        <rect x="180" y="9" width="10" height="3" rx="0.5" className="fill-primary/60" />
        
        <line x1="145" y1="16" x2="195" y2="16" className="stroke-primary/15" strokeWidth="0.75" />
        <rect x="150" y="20" width="24" height="3" rx="0.5" className="fill-primary/25" />
        <rect x="184" y="20" width="6" height="3" rx="0.5" className="fill-primary/40" />

        <line x1="145" y1="27" x2="195" y2="27" className="stroke-primary/15" strokeWidth="0.75" />
        <rect x="150" y="31" width="14" height="3" rx="0.5" className="fill-primary/25" />
        <rect x="178" y="31" width="12" height="3" rx="0.5" className="fill-primary/50" />
      </g>

      {/* ─── 4. ORIGINAL BAR CHART VIZ (UNTOUCHED DIMENSIONS & POSITIONS) ─── */}
      <line x1="0" y1="140" x2="200" y2="140" className="stroke-primary/15" strokeWidth="0.75" />
      <line x1="0" y1="110" x2="200" y2="110" className="stroke-primary/15" strokeWidth="0.75" />
      <line x1="0" y1="80"  x2="200" y2="80"  className="stroke-primary/15" strokeWidth="0.75" />
      <line x1="0" y1="50"  x2="200" y2="50"  className="stroke-primary/15" strokeWidth="0.75" />
      <rect x="15" y="80"  width="22" height="60"  rx="4" className="fill-primary/15 stroke-primary/40" strokeWidth="1" />
      <rect x="45" y="60"  width="22" height="80"  rx="4" className="fill-primary/25 stroke-primary/50" strokeWidth="1" />
      <rect x="75" y="40"  width="22" height="100" rx="4" className="fill-primary/35 stroke-primary/70" strokeWidth="1" />
      <rect x="105" y="20" width="22" height="120" rx="4" className="fill-primary/50 stroke-primary/90" strokeWidth="1" filter="url(#glow-bar)" />
      <path d="M15 110 L45 90 L75 75 L105 55 L135 65 L165 45"
        className="stroke-primary/80" strokeWidth="2.5" fill="none" strokeLinecap="round" strokeLinejoin="round" filter="url(#glow-bar)" />
      <circle cx="165" cy="45" r="4" className="fill-primary stroke-background" strokeWidth="2" />
    </svg>
  );
}

function NetworkViz({ className }: { className?: string }) {
  return (
    <svg viewBox="0 0 80 60" className={className} fill="none">
      <line x1="15" y1="30" x2="40" y2="12" className="stroke-primary/40" strokeWidth="1.5" />
      <line x1="15" y1="30" x2="40" y2="48" className="stroke-primary/40" strokeWidth="1.5" />
      <line x1="40" y1="12" x2="65" y2="30" className="stroke-primary/40" strokeWidth="1.5" />
      <line x1="40" y1="48" x2="65" y2="30" className="stroke-primary/40" strokeWidth="1.5" />
      <line x1="40" y1="12" x2="40" y2="48" className="stroke-primary/25" strokeWidth="1" strokeDasharray="2 2" />
      <circle cx="15" cy="30" r="6" className="fill-primary/30 stroke-primary" strokeWidth="1.5" />
      <circle cx="40" cy="12" r="6" className="fill-primary/20 stroke-primary/70" strokeWidth="1.5" />
      <circle cx="40" cy="48" r="6" className="fill-primary/20 stroke-primary/70" strokeWidth="1.5" />
      <circle cx="65" cy="30" r="6" className="fill-primary/30 stroke-primary" strokeWidth="1.5" />
    </svg>
  );
}

function DonutViz({ className }: { className?: string }) {
  return (
    <svg viewBox="0 0 60 60" className={className}>
      <circle cx="30" cy="30" r="20" fill="none" className="stroke-primary/10" strokeWidth="6" />
      <circle cx="30" cy="30" r="20" fill="none" className="stroke-primary text-primary" strokeWidth="6"
        strokeDasharray="75 125" strokeDashoffset="0" strokeLinecap="round" transform="rotate(-90 30 30)" />
      <circle cx="30" cy="30" r="20" fill="none" className="stroke-primary/40" strokeWidth="6"
        strokeDasharray="35 125" strokeDashoffset="-80" strokeLinecap="round" transform="rotate(-90 30 30)" />
    </svg>
  );
}

function ProgressViz({ className }: { className?: string }) {
  return (
    <svg viewBox="0 0 100 50" className={className} fill="none">
      <rect x="0" y="8"  width="100" height="6" rx="3" className="fill-primary/10" />
      <rect x="0" y="8"  width="85" height="6" rx="3" className="fill-primary" />
      <rect x="0" y="22" width="100" height="6" rx="3" className="fill-primary/10" />
      <rect x="0" y="22" width="55" height="6" rx="3" className="fill-primary/60" />
      <rect x="0" y="36" width="100" height="6" rx="3" className="fill-primary/10" />
      <rect x="0" y="36" width="92" height="6" rx="3" className="fill-primary/80" />
    </svg>
  );
}

/* ─── Data Integration Hub: Fully Distributed, Edge-to-Edge Spreading Network ─── */
function DataFlowViz({ className }: { className?: string }) {
  return (
    <svg
      viewBox="0 0 500 240"
      className={className}
      fill="none"
      preserveAspectRatio="none"
    >
      <defs>
        <filter id="glow-flow" x="-20%" y="-20%" width="140%" height="140%">
          <feGaussianBlur stdDeviation="2.5" result="blur" />
          <feComposite in="SourceGraphic" in2="blur" operator="over" />
        </filter>
      </defs>

      {/* ================= HIGH-TECH ENGINE HUB (FAR RIGHT) ================= */}
      {/* Outer Structure Shield */}
      <rect
        x="446"
        y="66"
        width="44"
        height="108"
        rx="8"
        className="stroke-primary/20 fill-primary/[0.02]"
        strokeWidth="1"
      />
      
      {/* Primary Server Grid Core */}
      <rect
        x="452"
        y="72"
        width="28"
        height="96"
        rx="5"
        className="fill-primary/10 stroke-primary/40"
        strokeWidth="1.5"
      />

      {/* Micro Intake Data Ports (Right-aligned ingress points) */}
      <rect x="483" y="82"  width="5" height="10" rx="1.5" className="fill-primary/30" />
      <rect x="483" y="100" width="5" height="10" rx="1.5" className="fill-primary/50" />
      <rect x="483" y="118" width="5" height="10" rx="1.5" className="fill-primary/60" />
      <rect x="483" y="136" width="5" height="10" rx="1.5" className="fill-primary/30" />
      
      {/* Internal Bus Matrix Links */}
      <line x1="480" y1="87"  x2="466" y2="105" className="stroke-primary/30" strokeWidth="1" />
      <line x1="480" y1="105" x2="466" y2="120" className="stroke-primary/50" strokeWidth="1" />
      <line x1="480" y1="123" x2="466" y2="120" className="stroke-primary/50" strokeWidth="1" />
      <line x1="480" y1="141" x2="466" y2="135" className="stroke-primary/30" strokeWidth="1" />

      {/* Core Aggregator Central Nodes */}
      <circle
        cx="466"
        cy="105"
        r="3"
        className="fill-primary/60 stroke-background"
        strokeWidth="1"
      />
      <circle
        cx="466"
        cy="135"
        r="3"
        className="fill-primary/60 stroke-background"
        strokeWidth="1"
      />

      {/* Main Quantum Node Cluster & Concentric Wave Orbit */}
      <circle
        cx="466"
        cy="120"
        r="11"
        className="stroke-primary/30"
        strokeWidth="1"
        strokeDasharray="3 1.5"
      />
      <circle
        cx="466"
        cy="120"
        r="7"
        className="fill-primary stroke-background"
        strokeWidth="2"
        filter="url(#glow-flow)"
      />


      {/* ================= UNTOUCHED ASYMMETRIC BRANCHES ================= */}
      {/* Heavy Spine 1: High Top-Arc */}
      <path d="M452 100 C 390 100, 350 40, 270 40" className="stroke-primary" strokeWidth="2.5" />
      {/* Heavy Spine 2: Immediate Shallow Drop */}
      <path d="M452 115 C 410 115, 380 90, 310 90" className="stroke-primary/80" strokeWidth="2" />
      {/* Heavy Spine 3: Lower-Mid Horizontal Tracking */}
      <path d="M452 130 C 370 130, 330 150, 240 150" className="stroke-primary/90" strokeWidth="2.2" />
      {/* Heavy Spine 4: Ground Wave Floor Line */}
      <path d="M452 145 C 400 145, 340 210, 290 210" className="stroke-primary/60" strokeWidth="1.8" />

      {/* --- Fractures from Top-Arc --- */}
      <path d="M350 55 C 310 55, 290 20, 210 20" className="stroke-primary/70" strokeWidth="1.5" />
      <path d="M350 55 C 290 65, 260 110, 180 110" className="stroke-primary/50" strokeWidth="1.2" strokeDasharray="3 2" />

      {/* --- Fractures from Spine 2 --- */}
      <path d="M380 95 C 330 95, 300 70, 230 70" className="stroke-primary/60" strokeWidth="1.5" />
      <path d="M310 90 C 250 90, 210 130, 130 130" className="stroke-primary/70" strokeWidth="1.8" />

      {/* --- Fractures from Spine 3 --- */}
      <path d="M310 145 C 260 145, 220 180, 150 180" className="stroke-primary/80" strokeWidth="1.5" />
      <path d="M240 150 C 180 150, 160 115, 90 115" className="stroke-primary/40" strokeWidth="1.2" />
      <path d="M240 150 C 190 160, 140 230, 70 230" className="stroke-primary/60" strokeWidth="1.5" />

      {/* Top Outer Outlets */}
      <path d="M210 20 C 150 20, 120 10, 20 10" className="stroke-primary/60" strokeWidth="1.2" />
      <path d="M210 20 C 160 25, 130 45, 30 45" className="stroke-primary/40" strokeWidth="1" />
      <path d="M270 40 C 200 40, 170 60, 15 60" className="stroke-primary/70" strokeWidth="1.5" />

      {/* Mid Matrix Chaos Ducts */}
      <path d="M230 70 C 170 70, 140 85, 40 85" className="stroke-primary/50" strokeWidth="1.2" />
      <path d="M180 110 C 130 110, 110 95, 25 95" className="stroke-primary/30" strokeWidth="1" strokeDasharray="4 2" />
      <path d="M130 130 C 80 130, 60 145, 15 145" className="stroke-primary/60" strokeWidth="1.5" />
      
      {/* Low Ground Outlets */}
      <path d="M150 180 C 100 180, 80 165, 35 165" className="stroke-primary/50" strokeWidth="1.2" />
      <path d="M150 180 C 90 190, 70 215, 20 215" className="stroke-primary/70" strokeWidth="1.6" />
      <path d="M290 210 C 220 210, 190 195, 110 195" className="stroke-primary/40" strokeWidth="1.2" />
      <path d="M110 195 C 70 195, 50 235, 15 235" className="stroke-primary/30" strokeWidth="1" />

      {/* Netting Cross-links */}
      <path d="M270 40 C 250 65, 250 65, 230 70" className="stroke-primary/10" strokeWidth="1" strokeDasharray="3 3" />
      <path d="M180 110 C 160 145, 160 145, 150 180" className="stroke-primary/15" strokeWidth="1" strokeDasharray="3 3" />
      <path d="M310 90 C 310 120, 310 120, 310 145" className="stroke-primary/15" strokeWidth="0.75" strokeDasharray="2 4" />

      {/* Midpoint Processing Joints */}
      <circle cx="350" cy="55" r="4.5" className="fill-background stroke-primary" strokeWidth="2" />
      <circle cx="380" cy="95" r="3.5" className="fill-primary/60" />
      <circle cx="310" cy="145" r="4" className="fill-background stroke-primary" strokeWidth="1.5" />
      <circle cx="270" cy="40" r="5" className="fill-primary/80" />
      <circle cx="240" cy="150" r="4.5" className="fill-primary" />
      <circle cx="110" cy="195" r="3" className="fill-primary/40" />

      {/* Dispersed Left Edge Terminals */}
      <circle cx="20" cy="10" r="3.5" className="fill-primary/70" />
      <circle cx="30" cy="45" r="2" className="fill-primary/40" />
      <circle cx="15" cy="60" r="3" className="fill-background stroke-primary" strokeWidth="1" />
      <circle cx="40" cy="85" r="2.5" className="fill-primary/50" />
      <circle cx="25" cy="95" r="2" className="fill-primary/30" />
      <circle cx="15" cy="145" r="3.5" className="fill-primary/80" />
      <circle cx="35" cy="165" r="2" className="fill-primary/40" />
      <circle cx="20" cy="215" r="4" className="fill-primary" filter="url(#glow-flow)" />
      <circle cx="15" cy="235" r="2.5" className="fill-primary/30" />
    </svg>
  );
}

function PulseViz({ className }: { className?: string }) {
  return (
    <svg viewBox="0 0 280 90" className={className} fill="none" preserveAspectRatio="xMidYMid slice">
      <line x1="10" y1="15" x2="270" y2="15" className="stroke-primary/[0.07]" strokeWidth="1" />
      <line x1="10" y1="45" x2="270" y2="45" className="stroke-primary/[0.07]" strokeWidth="1" />
      <line x1="10" y1="75" x2="270" y2="75" className="stroke-primary/[0.07]" strokeWidth="1" />
      <path d="M10 55 Q 35 15, 60 45 T 110 35 T 160 65 T 210 25 T 270 45" className="stroke-primary" strokeWidth="2.5" strokeLinecap="round" />
      <path d="M10 35 Q 40 65, 70 35 T 120 55 T 170 25 T 220 65 T 270 35" className="stroke-primary/40" strokeWidth="1.5" strokeDasharray="4 3" />
      <g className="fill-primary stroke-background" strokeWidth="2">
        <circle cx="60" cy="45" r="4.5" />
        <circle cx="110" cy="35" r="4.5" />
        <circle cx="160" cy="65" r="4.5" />
        <circle cx="210" cy="25" r="4.5" />
      </g>
      <line x1="110" y1="10" x2="110" y2="80" className="stroke-primary/20" strokeWidth="1" strokeDasharray="2 2" />
      <line x1="210" y1="10" x2="210" y2="80" className="stroke-primary/20" strokeWidth="1" strokeDasharray="2 2" />
    </svg>
  );
}

// ─── Card Configuration ───────────────────────────────────────────────────────

type CardVariant = 'hero' | 'center' | 'donut' | 'progress' | 'flow' | 'wide';

interface CardConfig {
  variant: CardVariant;
  span: string;
  Viz: React.FC<{ className?: string }>;
}

const CARD_CONFIGS: CardConfig[] = [
  { variant: 'hero',     span: 'lg:col-span-2 lg:row-span-2 sm:col-span-2', Viz: BarChartViz },
  { variant: 'center',   span: 'lg:col-span-1',                             Viz: NetworkViz  },
  { variant: 'center',   span: 'lg:col-span-1',                             Viz: DonutViz    },
  { variant: 'progress', span: 'lg:col-span-1',                             Viz: ProgressViz },
  { variant: 'flow',     span: 'lg:col-span-2',                             Viz: DataFlowViz },
  { variant: 'wide',     span: 'lg:col-span-3 sm:col-span-2',                Viz: PulseViz    },
];

// ─── Card Components ──────────────────────────────────────────────────────────

interface CardContentProps {
  cap: Capability;
  variant: CardVariant;
}

function HighlightTitle({ title, highlightFirst = true }: { title: string; highlightFirst?: boolean }) {
  const [first, ...rest] = title.split(' ');
  return highlightFirst ? (
    <>
      <span className="bg-gradient-to-r from-primary to-primary/80 bg-clip-text text-transparent font-bold">
        {first}
      </span>
      {' '}{rest.join(' ')}
    </>
  ) : (
    <>
      {first}{' '}
      <span className="bg-gradient-to-r from-primary to-primary/80 bg-clip-text text-transparent font-bold">
        {rest.join(' ')}
      </span>
    </>
  );
}

function CardContent({ cap, variant }: CardContentProps) {
  switch (variant) {
    case 'hero':
      return (
        <div className="relative z-10 flex flex-col h-full justify-between">
          <div className="max-w-xs sm:max-w-sm">
            <h3 className="text-2xl font-bold tracking-tight text-foreground">
              <HighlightTitle title={cap.title} />
            </h3>
            <p className="mt-3 text-sm leading-relaxed text-muted-foreground">
              {cap.description}
            </p>
          </div>
        </div>
      );
    case 'center':
    case 'donut':
    case 'progress':
      return (
        <div className="relative z-10 flex flex-col items-start text-left w-full">
          <h3 className="text-lg font-semibold tracking-tight text-foreground">
            {variant === 'progress' ? cap.title : <HighlightTitle title={cap.title} highlightFirst={false} />}
          </h3>
          <p className="mt-2 text-sm leading-relaxed text-muted-foreground max-w-[200px]">
            {cap.description}
          </p>
        </div>
      );
    case 'flow':
      return (
        <div className="relative z-10 max-w-xl">
          <h3 className="text-xl font-bold tracking-tight text-foreground">
            {cap.title}
          </h3>
          <p className="mt-2 text-sm sm:text-base leading-relaxed text-muted-foreground max-w-md">
            <span className="font-semibold text-foreground/90">Centralized</span>{' '}
            {cap.description.split(' ').slice(1).join(' ')}
          </p>
        </div>
      );
    case 'wide':
      return (
        <div className="relative z-10 flex flex-col justify-center h-full max-w-[55%]">
          <h3 className="text-xl font-bold tracking-tight text-foreground">{cap.title}</h3>
          <p className="mt-2 text-sm leading-relaxed text-muted-foreground">
            {cap.description}
          </p>
        </div>
      );
    default:
      return null;
  }
}

// ─── Dynamic Layout Sizing Adjustments ────────────────────────────────────────

const VIZ_CLASS: Record<CardVariant, string> = {
  hero:     'absolute right-0 bottom-0 w-3/5 h-3/5 sm:w-1/2 sm:h-3/4 opacity-85 pointer-events-none select-none [mask-image:linear-gradient(to_bottom_left,white_40%,transparent_95%)]',
  center:   'absolute right-2 bottom-4 w-28 h-28 sm:w-32 sm:h-32 opacity-80 pointer-events-none select-none',
  donut:    'absolute right-4 bottom-4 w-24 h-24 sm:w-28 sm:h-28 opacity-90 pointer-events-none select-none',
  progress: 'absolute right-4 bottom-6 w-28 h-20 sm:w-36 sm:h-24 opacity-85 pointer-events-none select-none',
  flow:     'absolute inset-0 w-full h-full opacity-60 pointer-events-none select-none [mask-image:linear-gradient(to_right,transparent_0%,white_15%)]',
  wide:     'absolute right-4 inset-y-0 w-2/5 opacity-95 pointer-events-none select-none [mask-image:linear-gradient(to_left,white_80%,transparent)]',
};

const CARD_CLASS: Record<CardVariant, string> = {
  hero:     'flex-col justify-between items-start bg-gradient-to-br from-muted/20 via-card to-primary/[0.04] min-h-[340px]',
  center:   'flex-col justify-between items-start min-h-[240px]',
  donut:    'flex-col justify-between items-start min-h-[240px]',
  progress: 'flex-col justify-between items-start min-h-[240px]',
  flow:     'flex-col justify-center items-start min-h-[240px]',
  wide:     'flex-row items-center p-8 min-h-[160px]',
};

// ─── Main Section Component ───────────────────────────────────────────────────

export const PlatformCapabilitiesSection: React.FC<Props> = ({ section }) => {
  const [sectionRef, inView] = useInView(0.05);

  const capabilities: Capability[] = section.description
    ? JSON.parse(section.description)
    : [
        { title: 'Channel Analytics', description: 'Real-time dashboards, reports, and actionable insights across the entire channel network.' },
        { title: 'Partner Lifecycle', description: 'Streamlined onboarding, performance tracking, and relationship management for every partner.' },
        { title: 'Claims & Incentives', description: 'Automated rebate, claim, and incentive program management with real-time tracking.' },
        { title: 'Compliance Management', description: 'Automated compliance checks, audit trails, and regulatory reporting across markets.' },
        { title: 'Data Integration Hub', description: 'Centralized data ingestion from ERP, CRM, and external partner systems.' },
        { title: 'Smart Notifications', description: 'Configurable alerts for inventory thresholds, claim status, and partner activity.' },
      ];

  return (
    <section
      ref={sectionRef}
      className="relative flex min-h-screen flex-col items-center justify-center overflow-hidden bg-background py-24 sm:py-32"
    >
      <div className="absolute top-1/3 left-1/2 -z-10 h-[500px] w-[800px] -translate-x-1/2 -translate-y-1/2 rounded-full bg-primary/5 blur-[140px]" />
      
      <div className="mx-auto w-full max-w-7xl px-4 sm:px-6 lg:px-8">
        
        {/* Header Block */}
        <div
          className={`mx-auto max-w-3xl text-center transition-all cubic-bezier(0.16,1,0.3,1) duration-1000 ${
            inView ? 'translate-y-0 opacity-100' : 'translate-y-6 opacity-0'
          }`}
        >
          <h2 className="mt-4 text-3xl font-extrabold tracking-tight bg-gradient-to-b from-foreground to-foreground/80 bg-clip-text text-transparent sm:text-5xl">
            {section.title}
          </h2>
          {section.subtitle && (
            <p className="mt-4 text-base sm:text-lg text-muted-foreground max-w-2xl mx-auto leading-relaxed">
              {section.subtitle}
            </p>
          )}
        </div>

        {/* Visual Dashboard Grid */}
        <div className="mt-16 grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
          {capabilities.map((cap, index) => {
            const cfg = CARD_CONFIGS[index] ?? CARD_CONFIGS[1];
            const { variant, span, Viz } = cfg;

            return (
              <div
                key={cap.title}
                style={{ 
                  transitionDelay: `${index * 60}ms`,
                }}
                className={[
                  'group relative flex overflow-hidden rounded-2xl border border-primary/15 bg-card/70 p-8 backdrop-blur-md',
                  'transition-all duration-500 ease-out-quad',
                  ' hover:border-primary/40 hover:bg-card/95',
                  'hover:shadow-[0_20px_40px_-15px_rgba(0,0,0,0.6),0_0_25px_-2px_rgba(var(--primary),0.12)]',
                  inView ? 'translate-y-0 opacity-100' : 'translate-y-12 opacity-0',
                  span,
                  CARD_CLASS[variant],
                ].join(' ')}
              >
                <div className={VIZ_CLASS[variant]}>
                  <Viz className="h-full w-full transition-all duration-700 ease-out group-hover:scale-[1.02] group-hover:brightness-110" />
                </div>

                <CardContent cap={cap} variant={variant} />
              </div>
            );
          })}
        </div>
      </div>
    </section>
  );
};