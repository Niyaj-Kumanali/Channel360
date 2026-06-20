import React from 'react';
import { cn } from '@/lib/utils';

interface LoaderProps {
  size?: 'sm' | 'md' | 'lg';
  className?: string;
}

const sizeMap = { sm: 20, md: 32, lg: 48 };

const MixedLoader: React.FC<{ s: number }> = ({ s }) => {
  const h = s / 2;
  const or = h * 0.85;
  const ir = h * 0.55;
  const cs = h * 0.35;
  const sw = Math.max(1.5, s * 0.065);

  return (
    <div className="relative flex items-center justify-center" style={{ width: s, height: s }}>
      {/* L13 — Pulse rings */}
      {[0, 1].map((i) => (
        <div
          key={`pulse-${i}`}
          className="absolute rounded-full border-2 border-current animate-ping"
          style={{
            width: s,
            height: s,
            animationDelay: `${i * 0.5}s`,
            animationDuration: '1.6s',
            opacity: 0.15 - i * 0.05,
          }}
        />
      ))}
      {/* L3 — Outer dual ring (spins slow) */}
      <svg width={s} height={s} viewBox={`0 0 ${s} ${s}`} fill="none" className="absolute inset-0 animate-spin-slow">
        <circle cx={h} cy={h} r={or} stroke="currentColor" strokeWidth={sw * 0.6} fill="none" strokeDasharray={`${s * 0.25} ${s * 0.75}`} opacity={0.25} />
      </svg>
      {/* L3 — Inner dual ring (spins reverse) */}
      <svg width={s} height={s} viewBox={`0 0 ${s} ${s}`} fill="none" className="absolute inset-0 animate-spin-reverse">
        <circle cx={h} cy={h} r={ir} stroke="currentColor" strokeWidth={sw * 0.5} fill="none" strokeDasharray={`${s * 0.15} ${s * 0.85}`} strokeLinecap="round" opacity={0.4} />
      </svg>
      {/* L1 — C arc (spins medium) */}
      <svg width={s} height={s} viewBox={`0 0 ${s} ${s}`} fill="none" className="absolute inset-0" style={{ animation: 'spin-slow 1.2s linear infinite' }}>
        <path d={`M${h - cs},${h} A${cs},${cs} 0 0,1 ${h + cs},${h}`} stroke="currentColor" strokeWidth={sw} strokeLinecap="round" fill="none" />
        <path d={`M${h - cs},${h} A${cs},${cs} 0 0,0 ${h + cs},${h}`} stroke="currentColor" strokeWidth={sw} strokeLinecap="round" fill="none" strokeDasharray={`${sw * 0.6} ${sw * 2}`} strokeDashoffset={sw * 0.3} />
      </svg>
      {/* L13 — Center dot */}
      <div className="rounded-full bg-current" style={{ width: s * 0.1, height: s * 0.1 }} />
    </div>
  );
};

export const Loader: React.FC<LoaderProps> = ({
  size = 'md',
  className,
}) => {
  const s = sizeMap[size];

  return (
    <div
      className={cn(
        'inline-flex items-center justify-center text-amber-500',
        className
      )}
      role="status"
      aria-label="Loading"
    >
      <MixedLoader s={s} />
    </div>
  );
};
