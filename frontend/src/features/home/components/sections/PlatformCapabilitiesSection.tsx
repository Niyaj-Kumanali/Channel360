import React, { useEffect, useRef, useState } from 'react';
import type { HomepageSection } from '@/features/cms/types/cms.types';

interface Props {
  section: HomepageSection;
}

interface Capability {
  title: string;
  description: string;
}

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

function BarChartViz({ className }: { className?: string }) {
  return (
    <svg viewBox="0 0 200 160" className={className} fill="none">
      <rect x="10" y="80" width="18" height="40" rx="2" className="fill-primary/20" />
      <rect x="32" y="60" width="18" height="60" rx="2" className="fill-primary/35" />
      <rect x="54" y="40" width="18" height="80" rx="2" className="fill-primary/50" />
      <rect x="76" y="20" width="18" height="100" rx="2" className="fill-primary/65" />
      <path d="M10 110 L38 90 L66 75 L94 55 L122 65 L150 45 L178 50"
        className="stroke-primary/40" strokeWidth="2" fill="none" strokeLinecap="round" />
      <circle cx="178" cy="50" r="3" className="fill-primary/40" />
      <rect x="106" y="10" width="34" height="42" rx="2" className="stroke-primary/25" strokeWidth="1.5" />
      <line x1="112" y1="18" x2="134" y2="18" className="stroke-primary/25" strokeWidth="1" />
      <line x1="112" y1="24" x2="130" y2="24" className="stroke-primary/25" strokeWidth="1" />
      <line x1="112" y1="30" x2="126" y2="30" className="stroke-primary/25" strokeWidth="1" />
      <line x1="112" y1="40" x2="132" y2="40" className="stroke-primary/25" strokeWidth="1" />
      <path d="M148 130 L158 140 L168 130" className="stroke-primary/25" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
      <line x1="158" y1="140" x2="158" y2="116" className="stroke-primary/25" strokeWidth="1.5" strokeLinecap="round" />
      <circle cx="20" cy="25" r="2.5" className="fill-primary/20" />
      <circle cx="42" cy="18" r="2.5" className="fill-primary/20" />
      <circle cx="64" cy="30" r="2.5" className="fill-primary/20" />
      <circle cx="86" cy="14" r="2.5" className="fill-primary/20" />
      <line x1="0" y1="140" x2="200" y2="140" className="stroke-primary/10" strokeWidth="0.5" />
      <line x1="0" y1="110" x2="200" y2="110" className="stroke-primary/10" strokeWidth="0.5" />
      <line x1="0" y1="80" x2="200" y2="80" className="stroke-primary/10" strokeWidth="0.5" />
      <line x1="0" y1="50" x2="200" y2="50" className="stroke-primary/10" strokeWidth="0.5" />
    </svg>
  );
}

function NetworkViz({ className }: { className?: string }) {
  return (
    <svg viewBox="0 0 80 60" className={className} fill="none">
      <circle cx="16" cy="30" r="4" className="fill-primary/60" />
      <circle cx="40" cy="12" r="4" className="fill-primary/40" />
      <circle cx="40" cy="48" r="4" className="fill-primary/40" />
      <circle cx="64" cy="30" r="4" className="fill-primary/60" />
      <line x1="20" y1="30" x2="36" y2="14" className="stroke-primary/25" strokeWidth="1.5" />
      <line x1="20" y1="30" x2="36" y2="46" className="stroke-primary/25" strokeWidth="1.5" />
      <line x1="44" y1="14" x2="60" y2="28" className="stroke-primary/25" strokeWidth="1.5" />
      <line x1="44" y1="46" x2="60" y2="32" className="stroke-primary/25" strokeWidth="1.5" />
      <circle cx="20" cy="30" r="1.5" className="fill-background" />
      <circle cx="36" cy="14" r="1.5" className="fill-background" />
      <circle cx="36" cy="46" r="1.5" className="fill-background" />
      <circle cx="60" cy="28" r="1.5" className="fill-background" />
      <circle cx="60" cy="32" r="1.5" className="fill-background" />
    </svg>
  );
}

function DonutViz({ className }: { className?: string }) {
  return (
    <svg viewBox="0 0 60 60" className={className}>
      <circle cx="30" cy="30" r="24" fill="none" className="stroke-primary/10" strokeWidth="6" />
      <circle cx="30" cy="30" r="24" fill="none" className="stroke-primary/60" strokeWidth="6"
        strokeDasharray="60 92" strokeDashoffset="0" transform="rotate(-90 30 30)" />
      <circle cx="30" cy="30" r="24" fill="none" className="stroke-primary/35" strokeWidth="6"
        strokeDasharray="44 108" strokeDashoffset="-66" transform="rotate(-90 30 30)" />
    </svg>
  );
}

function ProgressViz({ className }: { className?: string }) {
  return (
    <svg viewBox="0 0 100 50" className={className} fill="none">
      <rect x="0" y="8" width="80" height="6" rx="3" className="fill-primary/15" />
      <rect x="0" y="8" width="60" height="6" rx="3" className="fill-primary/70" />
      <rect x="0" y="22" width="80" height="6" rx="3" className="fill-primary/15" />
      <rect x="0" y="22" width="44" height="6" rx="3" className="fill-primary/50" />
      <rect x="0" y="36" width="80" height="6" rx="3" className="fill-primary/15" />
      <rect x="0" y="36" width="72" height="6" rx="3" className="fill-primary/60" />
    </svg>
  );
}

function DataFlowViz({ className }: { className?: string }) {
  return (
    <svg viewBox="0 0 100 150" className={className} fill="none">
      {/* Microcontroller chip — centered right, bold */}
      <rect x="195" y="30" width="90" height="100" rx="8" className="fill-primary/55" stroke="none" />
      <rect x="195" y="30" width="90" height="100" rx="8" className="stroke-primary/70" strokeWidth="1.5" fill="none" />
      {/* Die / core */}
      <rect x="212" y="47" width="56" height="56" rx="4" className="fill-primary/80" stroke="none" />
      <rect x="220" y="55" width="12" height="12" rx="1.5" className="fill-primary/90" stroke="none" />
      <rect x="236" y="55" width="12" height="12" rx="1.5" className="fill-primary/90" stroke="none" />
      <rect x="220" y="71" width="12" height="12" rx="1.5" className="fill-primary/90" stroke="none" />
      <rect x="236" y="71" width="12" height="12" rx="1.5" className="fill-primary/90" stroke="none" />
      <rect x="252" y="55" width="8" height="8" rx="1.5" className="fill-primary/90" stroke="none" />
      {/* Pin rows — left side */}
      <rect x="189" y="36" width="8" height="4" rx="1" className="fill-primary/70" />
      <rect x="189" y="48" width="8" height="4" rx="1" className="fill-primary/70" />
      <rect x="189" y="60" width="8" height="4" rx="1" className="fill-primary/70" />
      <rect x="189" y="72" width="8" height="4" rx="1" className="fill-primary/70" />
      <rect x="189" y="84" width="8" height="4" rx="1" className="fill-primary/70" />
      <rect x="189" y="96" width="8" height="4" rx="1" className="fill-primary/70" />
      <rect x="189" y="108" width="8" height="4" rx="1" className="fill-primary/70" />
      <rect x="189" y="120" width="8" height="4" rx="1" className="fill-primary/70" />
      {/* Pin rows — right side */}
      <rect x="283" y="36" width="8" height="4" rx="1" className="fill-primary/70" />
      <rect x="283" y="48" width="8" height="4" rx="1" className="fill-primary/70" />
      <rect x="283" y="60" width="8" height="4" rx="1" className="fill-primary/70" />
      <rect x="283" y="72" width="8" height="4" rx="1" className="fill-primary/70" />
      <rect x="283" y="84" width="8" height="4" rx="1" className="fill-primary/70" />
      <rect x="283" y="96" width="8" height="4" rx="1" className="fill-primary/70" />
      <rect x="283" y="108" width="8" height="4" rx="1" className="fill-primary/70" />
      <rect x="283" y="120" width="8" height="4" rx="1" className="fill-primary/70" />
      {/* Notch / dot on chip */}
      <circle cx="290" cy="36" r="2.5" className="fill-primary/80" />

      {/* Primary branches from left pins — smooth bezier curves */}
      <path d="M189 38 Q160 32 140 28 Q120 24 100 22" className="stroke-primary/50" strokeWidth="1.5" fill="none" strokeLinecap="round" />
      <path d="M189 50 Q160 46 140 44 Q118 42 96 42" className="stroke-primary/50" strokeWidth="1.5" fill="none" strokeLinecap="round" />
      <path d="M189 62 Q162 60 142 60 Q122 60 100 62" className="stroke-primary/55" strokeWidth="1.6" fill="none" strokeLinecap="round" />
      <path d="M189 74 Q162 76 142 78 Q122 80 102 80" className="stroke-primary/50" strokeWidth="1.5" fill="none" strokeLinecap="round" />
      <path d="M189 86 Q162 92 142 96 Q122 99 102 100" className="stroke-primary/50" strokeWidth="1.5" fill="none" strokeLinecap="round" />
      <path d="M189 98 Q160 102 140 106 Q118 110 96 112" className="stroke-primary/50" strokeWidth="1.5" fill="none" strokeLinecap="round" />
      <path d="M189 110 Q160 118 140 124 Q120 130 105 132" className="stroke-primary/50" strokeWidth="1.5" fill="none" strokeLinecap="round" />
      <path d="M189 122 Q160 130 140 134 Q120 138 108 140" className="stroke-primary/45" strokeWidth="1.3" fill="none" strokeLinecap="round" />

      {/* Sub-branches splitting from primary trunk endpoints */}
      <path d="M100 22 Q85 16 72 14 Q62 12 52 14" className="stroke-primary/40" strokeWidth="1.2" fill="none" strokeLinecap="round" />
      <path d="M100 22 Q88 24 78 26 Q68 28 58 28" className="stroke-primary/35" strokeWidth="1" fill="none" strokeLinecap="round" />
      <path d="M96 42 Q80 38 66 36 Q54 34 44 36" className="stroke-primary/40" strokeWidth="1.2" fill="none" strokeLinecap="round" />
      <path d="M96 42 Q82 46 70 48 Q60 50 50 50" className="stroke-primary/35" strokeWidth="1" fill="none" strokeLinecap="round" />
      <path d="M100 62 Q84 58 70 56 Q58 55 48 56" className="stroke-primary/40" strokeWidth="1.2" fill="none" strokeLinecap="round" />
      <path d="M100 62 Q88 66 76 68 Q66 69 56 70" className="stroke-primary/35" strokeWidth="1" fill="none" strokeLinecap="round" />
      <path d="M102 80 Q86 78 72 78 Q60 78 50 80" className="stroke-primary/40" strokeWidth="1.2" fill="none" strokeLinecap="round" />
      <path d="M102 80 Q88 84 76 86 Q66 88 56 88" className="stroke-primary/35" strokeWidth="1" fill="none" strokeLinecap="round" />
      <path d="M102 100 Q86 96 72 94 Q60 93 50 94" className="stroke-primary/40" strokeWidth="1.2" fill="none" strokeLinecap="round" />
      <path d="M102 100 Q88 104 76 106 Q66 108 56 108" className="stroke-primary/35" strokeWidth="1" fill="none" strokeLinecap="round" />
      <path d="M96 112 Q80 108 66 106 Q54 105 44 106" className="stroke-primary/40" strokeWidth="1.2" fill="none" strokeLinecap="round" />
      <path d="M96 112 Q82 116 70 118 Q60 120 50 120" className="stroke-primary/35" strokeWidth="1" fill="none" strokeLinecap="round" />
      <path d="M105 132 Q90 128 78 126 Q68 125 58 126" className="stroke-primary/40" strokeWidth="1.2" fill="none" strokeLinecap="round" />
      <path d="M105 132 Q92 136 80 138 Q70 140 60 140" className="stroke-primary/35" strokeWidth="1" fill="none" strokeLinecap="round" />
      <path d="M108 140 Q94 138 82 138 Q72 138 62 140" className="stroke-primary/35" strokeWidth="1" fill="none" strokeLinecap="round" />
      <path d="M108 140 Q96 144 84 146 Q74 148 64 148" className="stroke-primary/30" strokeWidth="0.9" fill="none" strokeLinecap="round" />

      {/* Fine tertiary branches deeper left */}
      <path d="M52 14 Q40 10 30 10 Q22 12 16 14" className="stroke-primary/30" strokeWidth="0.8" fill="none" strokeLinecap="round" />
      <path d="M58 28 Q46 24 36 24 Q28 26 20 28" className="stroke-primary/25" strokeWidth="0.7" fill="none" strokeLinecap="round" />
      <path d="M44 36 Q32 32 22 32 Q14 34 8 36" className="stroke-primary/30" strokeWidth="0.8" fill="none" strokeLinecap="round" />
      <path d="M50 50 Q38 50 28 52 Q20 54 14 56" className="stroke-primary/25" strokeWidth="0.7" fill="none" strokeLinecap="round" />
      <path d="M48 56 Q36 52 26 52 Q18 54 12 56" className="stroke-primary/30" strokeWidth="0.8" fill="none" strokeLinecap="round" />
      <path d="M56 70 Q44 72 34 74 Q26 76 18 78" className="stroke-primary/25" strokeWidth="0.7" fill="none" strokeLinecap="round" />
      <path d="M50 80 Q38 78 28 78 Q20 80 14 82" className="stroke-primary/30" strokeWidth="0.8" fill="none" strokeLinecap="round" />
      <path d="M56 88 Q44 90 34 92 Q26 94 18 96" className="stroke-primary/25" strokeWidth="0.7" fill="none" strokeLinecap="round" />
      <path d="M50 94 Q38 92 28 92 Q20 94 14 96" className="stroke-primary/30" strokeWidth="0.8" fill="none" strokeLinecap="round" />
      <path d="M56 108 Q44 110 34 112 Q26 114 18 116" className="stroke-primary/25" strokeWidth="0.7" fill="none" strokeLinecap="round" />
      <path d="M44 106 Q32 102 22 102 Q14 104 8 106" className="stroke-primary/30" strokeWidth="0.8" fill="none" strokeLinecap="round" />
      <path d="M50 120 Q38 122 28 124 Q20 126 14 128" className="stroke-primary/25" strokeWidth="0.7" fill="none" strokeLinecap="round" />
      <path d="M58 126 Q46 124 36 124 Q28 126 20 128" className="stroke-primary/30" strokeWidth="0.8" fill="none" strokeLinecap="round" />
      <path d="M60 140 Q48 142 38 144 Q30 146 22 148" className="stroke-primary/25" strokeWidth="0.7" fill="none" strokeLinecap="round" />
      <path d="M64 148 Q52 148 42 150 Q34 152 26 154" className="stroke-primary/22" strokeWidth="0.6" fill="none" strokeLinecap="round" />

      {/* Fine quaternary branches — deepest */}
      <path d="M16 14 Q8 12 4 16" className="stroke-primary/20" strokeWidth="0.5" fill="none" strokeLinecap="round" />
      <path d="M20 28 Q12 26 6 30" className="stroke-primary/18" strokeWidth="0.45" fill="none" strokeLinecap="round" />
      <path d="M8 36 Q4 38 2 42" className="stroke-primary/20" strokeWidth="0.5" fill="none" strokeLinecap="round" />
      <path d="M14 56 Q8 58 4 62" className="stroke-primary/18" strokeWidth="0.45" fill="none" strokeLinecap="round" />
      <path d="M12 56 Q6 58 3 62" className="stroke-primary/20" strokeWidth="0.5" fill="none" strokeLinecap="round" />
      <path d="M18 78 Q10 80 6 84" className="stroke-primary/18" strokeWidth="0.45" fill="none" strokeLinecap="round" />
      <path d="M14 82 Q8 84 4 88" className="stroke-primary/20" strokeWidth="0.5" fill="none" strokeLinecap="round" />
      <path d="M18 96 Q10 98 6 102" className="stroke-primary/18" strokeWidth="0.45" fill="none" strokeLinecap="round" />
      <path d="M14 96 Q8 98 4 102" className="stroke-primary/20" strokeWidth="0.5" fill="none" strokeLinecap="round" />
      <path d="M18 116 Q10 118 6 122" className="stroke-primary/18" strokeWidth="0.45" fill="none" strokeLinecap="round" />
      <path d="M8 106 Q4 108 2 112" className="stroke-primary/20" strokeWidth="0.5" fill="none" strokeLinecap="round" />
      <path d="M14 128 Q8 130 4 134" className="stroke-primary/18" strokeWidth="0.45" fill="none" strokeLinecap="round" />
      <path d="M20 128 Q12 130 6 134" className="stroke-primary/20" strokeWidth="0.5" fill="none" strokeLinecap="round" />
      <path d="M22 148 Q14 150 8 154" className="stroke-primary/18" strokeWidth="0.45" fill="none" strokeLinecap="round" />
      <path d="M26 154 Q18 156 10 158" className="stroke-primary/16" strokeWidth="0.4" fill="none" strokeLinecap="round" />

      {/* Terminal nodes */}
      <circle cx="4" cy="16" r="1.2" className="fill-primary/30" />
      <circle cx="6" cy="30" r="1" className="fill-primary/25" />
      <circle cx="2" cy="42" r="1.2" className="fill-primary/30" />
      <circle cx="4" cy="62" r="1" className="fill-primary/25" />
      <circle cx="3" cy="62" r="1.2" className="fill-primary/30" />
      <circle cx="6" cy="84" r="1" className="fill-primary/25" />
      <circle cx="4" cy="88" r="1.2" className="fill-primary/30" />
      <circle cx="6" cy="102" r="1" className="fill-primary/25" />
      <circle cx="4" cy="102" r="1.2" className="fill-primary/30" />
      <circle cx="6" cy="122" r="1" className="fill-primary/25" />
      <circle cx="2" cy="112" r="1.2" className="fill-primary/30" />
      <circle cx="4" cy="134" r="1" className="fill-primary/25" />
      <circle cx="6" cy="134" r="1.2" className="fill-primary/30" />
      <circle cx="8" cy="154" r="1" className="fill-primary/25" />
      <circle cx="10" cy="158" r="1.2" className="fill-primary/30" />
      <circle cx="52" cy="14" r="1" className="fill-primary/20" />
      <circle cx="58" cy="28" r="0.9" className="fill-primary/18" />
      <circle cx="44" cy="36" r="1" className="fill-primary/20" />
      <circle cx="50" cy="50" r="0.9" className="fill-primary/18" />
      <circle cx="48" cy="56" r="1" className="fill-primary/20" />
      <circle cx="56" cy="70" r="0.9" className="fill-primary/18" />
      <circle cx="50" cy="80" r="1" className="fill-primary/20" />
      <circle cx="56" cy="88" r="0.9" className="fill-primary/18" />
      <circle cx="50" cy="94" r="1" className="fill-primary/20" />
      <circle cx="56" cy="108" r="0.9" className="fill-primary/18" />
      <circle cx="44" cy="106" r="1" className="fill-primary/20" />
      <circle cx="50" cy="120" r="0.9" className="fill-primary/18" />
      <circle cx="58" cy="126" r="1" className="fill-primary/20" />
      <circle cx="60" cy="140" r="0.9" className="fill-primary/18" />
      <circle cx="64" cy="148" r="0.9" className="fill-primary/18" />
      <circle cx="16" cy="14" r="0.8" className="fill-primary/15" />
      <circle cx="20" cy="28" r="0.8" className="fill-primary/15" />
      <circle cx="8" cy="36" r="0.8" className="fill-primary/15" />
      <circle cx="14" cy="56" r="0.8" className="fill-primary/15" />
      <circle cx="12" cy="56" r="0.8" className="fill-primary/15" />
      <circle cx="18" cy="78" r="0.8" className="fill-primary/15" />
      <circle cx="14" cy="82" r="0.8" className="fill-primary/15" />
      <circle cx="18" cy="96" r="0.8" className="fill-primary/15" />
      <circle cx="14" cy="96" r="0.8" className="fill-primary/15" />
      <circle cx="18" cy="116" r="0.8" className="fill-primary/15" />
      <circle cx="8" cy="106" r="0.8" className="fill-primary/15" />
      <circle cx="14" cy="128" r="0.8" className="fill-primary/15" />
      <circle cx="20" cy="128" r="0.8" className="fill-primary/15" />
      <circle cx="22" cy="148" r="0.8" className="fill-primary/15" />
      <circle cx="26" cy="154" r="0.8" className="fill-primary/15" />
    </svg>
  );
}

function PulseViz({ className }: { className?: string }) {
  return (
    <svg viewBox="0 0 200 50" className={className} fill="none">
      <path d="M0 28 Q20 30 40 25 T80 22 T120 18 T160 20 T200 15"
        className="stroke-primary/40" strokeWidth="2" fill="none" />
      <path d="M0 32 Q20 34 40 28 T80 24 T120 20 T160 22 T200 18"
        className="stroke-primary/25" strokeWidth="1.5" fill="none" />
      <circle cx="40" cy="25" r="3" className="fill-primary/60" />
      <circle cx="120" cy="18" r="3" className="fill-primary/60" />
      <circle cx="200" cy="15" r="3" className="fill-primary/60" />
      <line x1="90" y1="16" x2="90" y2="34" className="stroke-primary/40" strokeWidth="1.5" strokeLinecap="round" />
      <line x1="94" y1="12" x2="94" y2="38" className="stroke-primary/25" strokeWidth="1" strokeLinecap="round" />
    </svg>
  );
}

const vizMap = [BarChartViz, NetworkViz, DonutViz, ProgressViz, DataFlowViz, PulseViz];

const cardConfigs = [
  { span: 'lg:col-span-2 lg:row-span-2 sm:col-span-2', align: 'text-left items-start', pad: 'p-10', titleSize: 'text-xl', gradient: true },
  { span: 'lg:col-span-1', align: 'text-center items-center', pad: 'p-10', titleSize: 'text-base', gradient: false },
  { span: 'lg:col-span-1', align: 'text-center items-center', pad: 'p-10', titleSize: 'text-base', gradient: false },
  { span: 'lg:col-span-1', align: 'text-center items-center', pad: 'p-10', titleSize: 'text-base', gradient: false },
  { span: 'lg:col-span-2', align: 'text-left items-start', pad: 'p-10', titleSize: 'text-base', gradient: false },
  { span: 'lg:col-span-3', align: 'text-left items-start', pad: 'p-10', titleSize: 'text-lg', gradient: false },
];

export const PlatformCapabilitiesSection: React.FC<Props> = ({ section }) => {
  const [sectionRef, inView] = useInView(0.1);

  const capabilities: Capability[] = section.description
    ? JSON.parse(section.description)
    : [];

  return (
    <section ref={sectionRef} className="flex py-16 min-h-[calc(100vh-4rem)] flex-col items-center justify-center">
      <div className="mx-auto w-full max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className={`mx-auto max-w-2xl text-center transition-all duration-700 ${inView ? 'translate-y-0 opacity-100' : 'translate-y-4 opacity-0'}`}>
          <h2 className="text-3xl font-bold text-foreground sm:text-4xl">{section.title}</h2>
          {section.subtitle && (
            <p className="mt-4 text-lg text-muted-foreground">{section.subtitle}</p>
          )}
        </div>

        <div className="mt-14 grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {capabilities.map((cap, index) => {
            const Viz = vizMap[index];
            const delay = index * 120;
            const cfg = cardConfigs[index] || cardConfigs[1];
            const isFullWidth = index === 5;

            return (
              <div
                key={cap.title}
                className={`relative flex overflow-hidden rounded-xl border bg-card transition-all duration-600 ease-out hover:border-primary/30 hover:shadow-[0_0_15px_-5px] hover:shadow-primary/15 ${inView ? 'translate-y-0 opacity-100' : 'translate-y-8 opacity-0'} ${cfg.span} ${cfg.align} ${cfg.pad} ${cfg.gradient ? 'border-primary/20 bg-gradient-to-br from-card to-primary/[0.02]' : ''} ${isFullWidth ? 'sm:col-span-2 lg:flex-row lg:items-center lg:gap-6' : 'flex-col'} ${index === 0 ? 'justify-between' : ''}`}
                style={{
                  transitionDuration: '600ms',
                  transitionDelay: `${delay}ms`,
                }}
              >
                {Viz && (
                  <div className={`absolute pointer-events-none select-none ${index === 0 ? 'inset-0 w-full h-full opacity-40' : index === 4 ? 'right-0 inset-y-0 w-3/4 opacity-40' : index === 5 ? 'right-0 bottom-0 w-62 h-40 opacity-40' : index === 3 ? 'hidden' : 'right-0 bottom-0 w-40 h-32 opacity-40'}`}>
                    <Viz className="h-full w-full" />
                  </div>
                )}

                {index === 0 ? (
                  <>
                    <div className="relative z-10">
                      <h3 className="text-xl font-semibold text-foreground">
                        <span className="text-primary">{cap.title.split(' ')[0]}</span>{' '}
                        {cap.title.split(' ').slice(1).join(' ')}
                      </h3>
                      <p className="mt-2 text-sm leading-relaxed text-muted-foreground max-w-md">{cap.description}</p>
                    </div>
                  </>
                ) : index === 1 ? (
                  <>
                    <h3 className="relative z-10 text-base font-semibold text-foreground">
                      {cap.title.split(' ')[0]}{' '}
                      <span className="text-primary">{cap.title.split(' ').slice(1).join(' ')}</span>
                    </h3>
                    <p className="relative z-10 mt-2 text-center text-sm leading-relaxed text-muted-foreground max-w-xs">{cap.description}</p>
                  </>
                ) : index === 2 ? (
                  <>
                    <h3 className="relative z-10 text-base font-semibold text-foreground">{cap.title}</h3>
                    <p className="relative z-10 mt-2 text-center text-sm leading-relaxed text-muted-foreground max-w-xs">{cap.description}</p>
                  </>
                ) : index === 3 ? (
                  <>
                    <h3 className="relative z-10 text-sm font-semibold text-foreground">{cap.title}</h3>
                    <p className="relative z-10 mt-1.5 text-center text-xs leading-relaxed text-muted-foreground">{cap.description}</p>
                  </>
                ) : index === 4 ? (
                  <>
                    <h3 className="relative z-10 text-base font-semibold text-foreground">{cap.title}</h3>
                    <p className="relative z-10 mt-2 text-left text-sm leading-relaxed text-muted-foreground max-w-sm">
                      <span className="font-medium text-foreground/80">Centralized</span>{' '}
                      {cap.description.split(' ').slice(1).join(' ')}
                    </p>
                  </>
                ) : (
                  <>
                    <div className="relative z-10 flex-1">
                      <h3 className="text-lg font-semibold text-foreground">{cap.title}</h3>
                      <p className="mt-2 text-sm leading-relaxed text-muted-foreground max-w-2xl">{cap.description}</p>
                    </div>
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
