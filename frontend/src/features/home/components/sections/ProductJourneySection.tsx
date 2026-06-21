import React, { useCallback, useEffect, useRef, useState } from 'react';
import { Package, Network, ArrowRightLeft, ShoppingCart, Cpu } from 'lucide-react';
import type { HomepageSection } from '@/features/cms/types/cms.types';

interface Props {
  section: HomepageSection;
}

const defaultIcons = [Package, Network, ArrowRightLeft, ShoppingCart, Cpu];

interface JourneyStep {
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

export const ProductJourneySection: React.FC<Props> = ({ section }) => {
  const [sectionRef, inView] = useInView(0.1);
  const [phase, setPhase] = useState<'ball' | 'repair'>('ball');
  const [repairStatus, setRepairStatus] = useState<'progress' | 'complete'>('progress');
  const [activeNode, setActiveNode] = useState<number | null>(0);
  const [pulsingNode, setPulsingNode] = useState<number | null>(null);
  const iconTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const repairTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const cycleRef = useRef(0);
  const gearTypeRef = useRef(true);

  useEffect(() => {
    return () => {
      if (iconTimerRef.current) clearTimeout(iconTimerRef.current);
      if (repairTimerRef.current) clearTimeout(repairTimerRef.current);
    };
  }, []);

  useEffect(() => {
    if (!inView) {
      if (iconTimerRef.current) clearTimeout(iconTimerRef.current);
      if (repairTimerRef.current) clearTimeout(repairTimerRef.current);
      setPhase('ball');
      cycleRef.current = 0;
    }
  }, [inView]);

  // Triggered when ball reaches Manufacturer (70% of 7s = 4.9s)
  const handleBallReturn = useCallback(() => {
    setRepairStatus('progress');
    gearTypeRef.current = cycleRef.current % 2 === 0;
    cycleRef.current += 1;
    setPhase('repair');
    repairTimerRef.current = setTimeout(() => {
      setRepairStatus('complete');
    }, 5500);
    iconTimerRef.current = setTimeout(() => {
      setPhase('ball');
    }, 7000);
  }, []);

  // Start 4900ms timer when ball begins its journey
  useEffect(() => {
    if (inView && phase === 'ball') {
      const timer = setTimeout(handleBallReturn, 4900);
      return () => clearTimeout(timer);
    }
  }, [inView, phase, handleBallReturn]);

  // Track which step card the ball is currently at (polling, synced with CSS animation)
  useEffect(() => {
    if (phase === 'repair') {
      setActiveNode(0);
      return;
    }
    if (!inView) {
      setActiveNode(0);
      return;
    }
    const startTime = Date.now();
    setActiveNode(0);
    const interval = setInterval(() => {
      const t = Date.now() - startTime;
      let next: number;
      if (t < 700) next = 0;
      else if (t < 1400) next = 1;
      else if (t < 2100) next = 2;
      else if (t < 2800) next = 3;
      else if (t < 3325) next = 4;
      else if (t < 3850) next = 3;
      else if (t < 4375) next = 2;
      else next = 1;

      setActiveNode(next);
    }, 50);

    const pulseTimes = [0, 700, 1400, 2100, 2800, 3325, 3850, 4375];
    const pulseNodes = [0, 1, 2, 3, 4, 3, 2, 1];
    const pulseTimeouts = pulseTimes.map((time, i) =>
      setTimeout(() => {
        setPulsingNode(pulseNodes[i]);
        setTimeout(() => setPulsingNode(null), 400);
      }, time)
    );

    return () => {
      clearInterval(interval);
      pulseTimeouts.forEach(clearTimeout);
    };
  }, [phase, inView]);

  const isBallRunning = phase === 'ball' && inView;

  const steps: JourneyStep[] = section.description
    ? JSON.parse(section.description)
    : [
        { title: 'Manufacturer', description: 'Products enter the channel network from manufacturers and suppliers.' },
        { title: 'Distributor', description: 'Distributors receive and forward products to channel partners.' },
        { title: 'Channel Partner', description: 'Partners sell products to end customers and manage local inventory.' },
        { title: 'End Customer', description: 'Customers purchase products through the partner network.' },
        { title: 'Activation', description: 'Products are activated and linked back to their channel journey.' },
      ];

  return (
    <section ref={sectionRef} className="flex min-h-[calc(100vh-4rem)] flex-col items-center justify-center overflow-hidden">
      <div className="mx-auto w-full max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className={`mx-auto max-w-2xl text-center transition-all duration-700 ${inView ? 'translate-y-0 opacity-100' : 'translate-y-4 opacity-0'}`}>
          <h2 className="text-3xl font-bold text-foreground sm:text-4xl">{section.title}</h2>
          {section.subtitle && (
            <p className="mt-4 text-lg text-muted-foreground">{section.subtitle}</p>
          )}
        </div>

        <div className="mt-16 relative">
          {/* Pipeline connector line */}
          <div className="absolute top-6 left-[8%] right-[8%] h-0.5 rounded-full bg-gradient-to-r from-primary/10 via-primary/30 to-primary/10 hidden lg:block overflow-hidden">
            <div
              className={`h-full w-full bg-gradient-to-r from-primary/10 via-primary/40 to-primary/10 transition-all duration-1000 ease-out ${inView ? 'translate-x-0 opacity-100' : '-translate-x-full opacity-0'}`}
            />
          </div>

          {/* Bouncing ball — outer moves horizontally, inner bounces vertically */}
          <div className="absolute top-[1.35rem] left-[8%] right-[8%] hidden lg:block" style={{ pointerEvents: 'none' }}>
            <div
              className={`relative ${inView ? 'opacity-100' : 'opacity-0'}`}
              style={{
                animation: isBallRunning ? 'bounce-h 7s linear forwards' : 'none',
                left: '0.75%',
              }}
            >
              <div
                className="h-2 w-2 rounded-full bg-primary shadow-[0_0_10px_4px_#f59e0b50]"
                style={{
                  animation: phase === 'repair' ? 'ball-repair 7s linear forwards' : (isBallRunning ? 'bounce-v 7s linear forwards' : 'none'),
                  transform: 'translateY(0)',
                }}
              />
            </div>
          </div>

          <div className="grid gap-8 sm:grid-cols-2 lg:grid-cols-5">
            {steps.map((step, index) => {
              const Icon = defaultIcons[index] || Package;
              const delay = index * 120;

              return (
                <div
                  key={step.title}
                  className={`flex h-full flex-col items-center transition-all duration-600 ease-out ${inView ? 'translate-y-0 opacity-100' : 'translate-y-8 opacity-0'}`}
                  style={{
                    transitionDuration: '600ms',
                    transitionDelay: `${delay}ms`,
                  }}
                >
                  {/* Icon node with pulse ring */}
                  <div className="relative z-10">
                    <div className={`flex h-12 w-12 items-center justify-center rounded-full border-2 border-primary/30 bg-card shadow-sm transition-transform duration-300 hover:scale-110 ${inView ? 'scale-100' : 'scale-0'}`}
                      style={{
                        transitionDelay: `${delay + 100}ms`,
                        transitionDuration: '400ms',
                        animation: pulsingNode === index ? 'node-pulse 0.4s ease-out' : 'none',
                      }}>
                      <Icon className="h-5 w-5 text-primary" />
                    </div>
                    {/* Icons that alternate behind Manufacturer icon */}
                    {index === 0 && (
                      <>
                        <div
                          className="absolute inset-0 flex items-center justify-center pointer-events-none"
                          style={{
                            animation: phase === 'repair' && gearTypeRef.current ? 'gear-combo 7s linear forwards' : 'none',
                            opacity: 0,
                            transform: 'scale(0.2) translateY(0) rotate(1080deg)',
                          }}
                        >
                          <div className="flex items-center justify-center" style={{
                            transformOrigin: 'center',
                            zIndex: -1,
                          }}>
                            <svg className="h-6 w-6 text-primary" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                              <circle cx="12" cy="12" r="3" />
                              <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z" />
                            </svg>
                          </div>
                        </div>
                        <div
                          className="absolute inset-0 flex items-center justify-center pointer-events-none"
                          style={{
                            animation: phase === 'repair' && !gearTypeRef.current ? 'recycle-combo 7s linear forwards' : 'none',
                            opacity: 0,
                            transform: 'scale(0.2) translateY(0) rotate(1080deg)',
                          }}
                        >
                          <div className="flex items-center justify-center" style={{
                            transformOrigin: 'center',
                            zIndex: -1,
                          }}>
                            <svg className="h-6 w-6 text-primary" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                              <polyline points="23 4 23 10 17 10" />
                              <polyline points="1 20 1 14 7 14" />
                              <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15" />
                            </svg>
                          </div>
                        </div>
                      </>
                    )}
                    {/* Pulse ring */}
                    {inView && (
                      <div className="absolute inset-0 rounded-full border-2 border-primary/20 animate-ping-slow" />
                    )}
                  </div>

                  {/* Card */}
                  <div className={`mt-4 flex w-full flex-1 flex-col rounded-xl border p-4 text-center transition-[opacity,transform] duration-500 ${inView ? 'translate-y-0 opacity-100' : 'translate-y-4 opacity-0'} ${
                    activeNode === index ? 'border-primary/40 bg-primary/[0.02] shadow-[0_0_10px_-3px] shadow-primary/20' : 'border-border bg-card'
                  }`}
                    style={{
                      transitionDelay: `${delay + 200}ms`,
                    }}>
                    <div className="flex items-center justify-center gap-1.5">
                      <span className="text-[10px] font-bold text-primary/40">0{index + 1}</span>
                      <h3 className="text-sm font-semibold text-foreground">{step.title}</h3>
                    </div>
                    <span
                      className={`mt-1 text-[10px] font-semibold transition-all duration-300 ${
                        index === 0 && phase === 'repair' ? 'visible' : 'invisible'
                      } ${repairStatus === 'progress' ? 'text-primary' : 'text-emerald-500'}`}
                    >
                      {repairStatus === 'progress'
                        ? (gearTypeRef.current ? 'Repairing…' : 'Replacing…')
                        : (gearTypeRef.current ? 'Repaired ✓' : 'Replaced ✓')}
                    </span>
                    <p className="mt-2 text-xs leading-relaxed text-muted-foreground">{step.description}</p>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </div>

      <style>{`
         @keyframes bounce-h {
          0% { left: 0.75%; }
          10% { left: 24%; }
          20% { left: 48%; }
          30% { left: 72%; }
          40% { left: 96%; }
          47.5% { left: 72%; }
          55% { left: 48%; }
          62.5% { left: 24%; }
          70% { left: 0.75%; }
          100% { left: 0.75%; }
         }
         @keyframes bounce-v {
          0% { transform: translateY(0); animation-timing-function: ease-out; }
          5% { transform: translateY(-20px); animation-timing-function: ease-in; }
          10% { transform: translateY(0); animation-timing-function: ease-out; }
          15% { transform: translateY(-20px); animation-timing-function: ease-in; }
          20% { transform: translateY(0); animation-timing-function: ease-out; }
          25% { transform: translateY(-20px); animation-timing-function: ease-in; }
          30% { transform: translateY(0); animation-timing-function: ease-out; }
          35% { transform: translateY(-20px); animation-timing-function: ease-in; }
          40% { transform: translateY(0); animation-timing-function: ease-out; }
          43.75% { transform: translateY(-20px); animation-timing-function: ease-in; }
          47.5% { transform: translateY(0); animation-timing-function: ease-out; }
          51.25% { transform: translateY(-20px); animation-timing-function: ease-in; }
          55% { transform: translateY(0); animation-timing-function: ease-out; }
          58.75% { transform: translateY(-20px); animation-timing-function: ease-in; }
          62.5% { transform: translateY(0); animation-timing-function: ease-out; }
          66.25% { transform: translateY(-20px); animation-timing-function: ease-in; }
          70% { transform: translateY(0); }
          100% { transform: translateY(0); }
         }
         @keyframes gear-combo {
          0%, 5% { opacity: 0; transform: scale(0.2) translateY(0) rotate(0deg); }
          7% { opacity: 0.6; transform: scale(0.5) translateY(-8px) rotate(0deg); }
          9% { opacity: 1; transform: scale(1) translateY(-50px) rotate(0deg); }
          22% { opacity: 1; transform: scale(1) translateY(-50px) rotate(0deg); }
          70% { opacity: 1; transform: scale(1) translateY(-50px) rotate(1080deg); }
          85% { opacity: 1; transform: scale(1) translateY(-50px) rotate(1080deg); }
          87% { opacity: 0.5; transform: scale(0.6) translateY(-12px) rotate(1080deg); }
          90% { opacity: 0; transform: scale(0.2) translateY(0) rotate(1080deg); }
         }
         @keyframes recycle-combo {
          0%, 5% { opacity: 0; transform: scale(0.2) translateY(0) rotate(0deg); }
          7% { opacity: 0.6; transform: scale(0.5) translateY(-8px) rotate(0deg); }
          9% { opacity: 1; transform: scale(1) translateY(-50px) rotate(0deg); }
          22% { opacity: 1; transform: scale(1) translateY(-50px) rotate(0deg); }
          70% { opacity: 1; transform: scale(1) translateY(-50px) rotate(1080deg); }
          85% { opacity: 1; transform: scale(1) translateY(-50px) rotate(1080deg); }
          87% { opacity: 0.5; transform: scale(0.6) translateY(-12px) rotate(1080deg); }
          90% { opacity: 0; transform: scale(0.2) translateY(0) rotate(1080deg); }
         }
          @keyframes ball-repair {
           0% { transform: translateY(0); }
           10% { transform: translateY(0); }
           15% { transform: translateY(-51px); }
           77% { transform: translateY(-51px); }
           84% { transform: translateY(0); }
           100% { transform: translateY(0); }
          }
          @keyframes node-pulse {
           0% { transform: scale(1); }
           20% { transform: scale(1.35); }
           40% { transform: scale(0.9); }
           60% { transform: scale(1.05); }
           100% { transform: scale(1); }
          }
          @keyframes ping-slow {
          0% { transform: scale(1); opacity: 0.5; }
          100% { transform: scale(1.8); opacity: 0; }
        }
        .animate-ping-slow {
          animation: ping-slow 2s ease-out infinite;
        }
      `}</style>
    </section>
  );
};
