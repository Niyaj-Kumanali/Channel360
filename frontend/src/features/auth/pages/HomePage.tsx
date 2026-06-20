import React from 'react';
import { Link } from 'react-router-dom';
import { ArrowRight } from 'lucide-react';
import { Button } from '@/components/ui/Button';

interface LayoutOption {
  id: string;
  name: string;
  badge: string;
  description: string;
  mockup: React.ReactNode;
}

const layouts: LayoutOption[] = [
  {
    id: 'a1',
    name: 'A1 — Brand Left',
    badge: 'Current',
    description: 'Full brand panel on left, form on right. Balanced, enterprise.',
    mockup: (
      <div className="w-full h-36 rounded-lg overflow-hidden flex border border-white/10">
        <div className="w-1/2 bg-gradient-to-br from-amber-950 via-amber-900 to-amber-800 p-3 flex flex-col justify-between">
          <div className="flex items-center gap-1.5">
            <div className="w-4 h-4 rounded bg-white/10 flex items-center justify-center"><span className="text-white text-[5px] font-bold">C</span></div>
            <span className="text-white text-[5px] font-semibold opacity-70">Channel360</span>
          </div>
          <div className="space-y-1">
            <div className="h-1 w-3/4 rounded bg-white/10" />
            <div className="h-1 w-1/2 rounded bg-white/5" />
          </div>
          <div className="text-white/20 text-[4px]">&copy; 2026</div>
        </div>
        <div className="w-1/2 bg-gray-50 p-4 flex flex-col items-center justify-center gap-2">
          <div className="w-5 h-5 rounded bg-amber-500" />
          <div className="h-1.5 w-3/4 rounded bg-gray-200" />
          <div className="h-1.5 w-3/4 rounded bg-gray-100" />
          <div className="h-2 w-3/4 rounded bg-amber-400 mt-1" />
        </div>
      </div>
    ),
  },
  {
    id: 'a2',
    name: 'A2 — Brand Right',
    badge: 'Alt',
    description: 'Mirror of A1 — form on left, brand on right. Fresh swap.',
    mockup: (
      <div className="w-full h-36 rounded-lg overflow-hidden flex border border-white/10">
        <div className="w-1/2 bg-gray-50 p-4 flex flex-col items-center justify-center gap-2">
          <div className="w-5 h-5 rounded bg-amber-500" />
          <div className="h-1.5 w-3/4 rounded bg-gray-200" />
          <div className="h-1.5 w-3/4 rounded bg-gray-100" />
          <div className="h-2 w-3/4 rounded bg-amber-400 mt-1" />
        </div>
        <div className="w-1/2 bg-gradient-to-br from-amber-950 via-amber-900 to-amber-800 p-3 flex flex-col justify-between">
          <div className="flex items-center gap-1.5">
            <div className="w-4 h-4 rounded bg-white/10 flex items-center justify-center"><span className="text-white text-[5px] font-bold">C</span></div>
            <span className="text-white text-[5px] font-semibold opacity-70">Channel360</span>
          </div>
          <div className="text-white/20 text-[4px] text-right">&copy; 2026</div>
        </div>
      </div>
    ),
  },
  {
    id: 'a3',
    name: 'A3 — Centered Card',
    badge: 'Classic',
    description: 'Single card centered on dark gradient. Simple, focused.',
    mockup: (
      <div className="w-full h-36 rounded-lg overflow-hidden border border-white/10 bg-gradient-to-br from-amber-950 via-amber-900 to-amber-800 p-3 flex items-center justify-center">
        <div className="w-3/5 bg-white rounded-lg p-3 flex flex-col items-center gap-1.5 shadow-lg">
          <div className="flex items-center gap-1 mb-1">
            <div className="w-3 h-3 rounded bg-amber-500 flex items-center justify-center"><span className="text-white text-[3px] font-bold">C</span></div>
            <span className="text-amber-800 text-[4px] font-semibold">Channel360</span>
          </div>
          <div className="h-1.5 w-3/4 rounded bg-gray-200" />
          <div className="h-1.5 w-3/4 rounded bg-gray-100" />
          <div className="h-2 w-3/4 rounded bg-amber-400 mt-1" />
        </div>
      </div>
    ),
  },
  {
    id: 'a4',
    name: 'A4 — Glass Card',
    badge: 'Modern',
    description: 'Frosted glass card on animated gradient. Premium feel.',
    mockup: (
      <div className="w-full h-36 rounded-lg overflow-hidden border border-white/10 bg-gradient-to-br from-indigo-950 via-purple-900 to-amber-900 p-3 flex items-center justify-center relative">
        <div className="absolute inset-0 opacity-15">
          <div className="absolute top-3 left-6 w-10 h-10 rounded-full bg-white blur-2xl" />
          <div className="absolute bottom-6 right-8 w-8 h-8 rounded-full bg-amber-400 blur-xl" />
        </div>
        <div className="w-3/5 bg-white/10 backdrop-blur-md rounded-lg p-3 flex flex-col items-center gap-1.5 border border-white/20">
          <div className="flex items-center gap-1 mb-1">
            <div className="w-3 h-3 rounded bg-white/20 flex items-center justify-center"><span className="text-white text-[3px] font-bold">C</span></div>
            <span className="text-white/70 text-[4px] font-semibold">Channel360</span>
          </div>
          <div className="h-1.5 w-3/4 rounded bg-white/15" />
          <div className="h-1.5 w-3/4 rounded bg-white/10" />
          <div className="h-2 w-3/4 rounded bg-amber-400/60 mt-1" />
        </div>
      </div>
    ),
  },
  {
    id: 'a5',
    name: 'A5 — Full Dark',
    badge: 'Dark',
    description: 'Dark card on dark gradient. Sleek, developer aesthetic.',
    mockup: (
      <div className="w-full h-36 rounded-lg overflow-hidden border border-white/5 bg-gray-950 p-3 flex items-center justify-center">
        <div className="w-3/5 bg-gray-900 rounded-lg p-3 flex flex-col items-center gap-1.5 border border-gray-800">
          <div className="flex items-center gap-1 mb-1">
            <div className="w-3 h-3 rounded bg-amber-500 flex items-center justify-center"><span className="text-white text-[3px] font-bold">C</span></div>
            <span className="text-gray-300 text-[4px] font-semibold">Channel360</span>
          </div>
          <div className="h-1.5 w-3/4 rounded bg-gray-700" />
          <div className="h-1.5 w-3/4 rounded bg-gray-700" />
          <div className="h-2 w-3/4 rounded bg-amber-500 mt-1" />
        </div>
      </div>
    ),
  },
  {
    id: 'a6',
    name: 'A6 — Clean Minimal',
    badge: 'Minimal',
    description: 'White bg, top brand dot, centered form. Apple-like.',
    mockup: (
      <div className="w-full h-36 rounded-lg overflow-hidden border border-gray-200 bg-white p-3 flex flex-col items-center justify-center gap-2">
        <div className="flex items-center gap-1 mb-1">
          <div className="w-3.5 h-3.5 rounded bg-amber-500 flex items-center justify-center"><span className="text-white text-[4px] font-bold">C</span></div>
          <span className="text-gray-800 text-[5px] font-semibold">Channel360</span>
        </div>
        <div className="h-1.5 w-1/2 rounded bg-gray-200" />
        <div className="h-1.5 w-1/2 rounded bg-gray-100" />
        <div className="h-2 w-1/2 rounded bg-amber-400 mt-1" />
      </div>
    ),
  },
  {
    id: 'a7',
    name: 'A7 — Brand Bottom',
    badge: 'Subtle',
    description: 'Form on top, brand footer at bottom. Unconventional, airy.',
    mockup: (
      <div className="w-full h-36 rounded-lg overflow-hidden border border-white/10 bg-gradient-to-br from-amber-950 via-amber-900 to-amber-800 p-3 flex flex-col justify-between">
        <div className="flex items-center justify-center flex-1">
          <div className="w-2/5 bg-white rounded-lg p-2 flex flex-col items-center gap-1.5">
            <div className="h-1.5 w-3/4 rounded bg-gray-200" />
            <div className="h-1.5 w-3/4 rounded bg-gray-100" />
            <div className="h-2 w-3/4 rounded bg-amber-400 mt-0.5" />
          </div>
        </div>
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-1">
            <div className="w-3 h-3 rounded bg-white/10 flex items-center justify-center"><span className="text-white text-[3px] font-bold">C</span></div>
            <span className="text-white text-[4px] font-semibold opacity-70">Channel360</span>
          </div>
          <span className="text-white/20 text-[4px]">&copy; 2026</span>
        </div>
      </div>
    ),
  },
  {
    id: 'a8',
    name: 'A8 — Card with Logo',
    badge: 'Clean',
    description: 'Centered card with brand mark above the form. Balanced.',
    mockup: (
      <div className="w-full h-36 rounded-lg overflow-hidden border border-white/10 bg-gradient-to-br from-amber-950 via-amber-900 to-amber-800 p-3 flex items-center justify-center">
        <div className="w-3/5 bg-white rounded-lg p-3 flex flex-col items-center gap-1.5 shadow-lg">
          <div className="w-5 h-5 rounded-lg bg-amber-500 flex items-center justify-center mb-1">
            <span className="text-white text-[6px] font-bold">C</span>
          </div>
          <div className="h-1.5 w-3/4 rounded bg-gray-200" />
          <div className="h-1.5 w-3/4 rounded bg-gray-100" />
          <div className="h-2 w-3/4 rounded bg-amber-400 mt-1" />
        </div>
      </div>
    ),
  },
];

const Card: React.FC<{ layout: LayoutOption }> = ({ layout }) => (
  <div className={`rounded-xl border p-4 flex flex-col gap-3 transition-all duration-200 cursor-pointer group
    ${layout.id === 'a1' ? 'border-amber-500/40 bg-amber-500/5 ring-1 ring-amber-500/20' : 'border-white/10 bg-white/[0.03] hover:border-amber-500/30 hover:bg-white/[0.06]'}
  `}>
    {layout.mockup}
    <div className="flex items-start gap-2">
      <div className="flex-1 min-w-0">
        <div className="flex items-center gap-2">
          <span className={`text-sm font-semibold ${layout.id === 'a1' ? 'text-amber-300' : 'text-white group-hover:text-amber-300'} transition-colors`}>
            {layout.name}
          </span>
          <span className={`text-[10px] font-medium px-1.5 py-0.5 rounded ${layout.badge === 'Current' ? 'text-amber-500 bg-amber-500/10' : 'text-gray-500 bg-white/5'}`}>
            {layout.badge}
          </span>
        </div>
        <p className="text-[10px] text-gray-500 mt-0.5 leading-relaxed">{layout.description}</p>
      </div>
    </div>
  </div>
);

export const HomePage: React.FC = () => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-950 via-gray-900 to-gray-950">
      <header className="border-b border-white/5 sticky top-0 bg-gray-950/80 backdrop-blur-md z-20">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 h-14 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <svg width="28" height="28" viewBox="0 0 40 40" fill="none" className="flex-shrink-0">
              <rect width="40" height="40" rx="10" className="fill-white/10" />
              <path d="M12 20C12 15.6 15.6 12 20 12C24.4 12 28 15.6 28 20" stroke="white" strokeWidth="3.5" strokeLinecap="round" fill="none" />
              <path d="M12 20C12 24.4 15.6 28 20 28C24.4 28 28 24.4 28 20" stroke="white" strokeWidth="3.5" strokeLinecap="round" fill="none" strokeDasharray="2 6" strokeDashoffset="1" />
            </svg>
            <span className="text-sm font-semibold text-white">channel360</span>
          </div>
          <Link to="/login">
            <Button variant="ghost" className="text-white hover:text-white hover:bg-white/10 text-xs">
              Sign In
            </Button>
          </Link>
        </div>
      </header>
      <main className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="pt-12 pb-6 text-center">
          <h1 className="text-3xl font-bold text-white mb-1">Choose a Login Layout</h1>
          <p className="text-gray-400 text-sm">8 layout concepts — A1 is current</p>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 pb-24">
          {layouts.map((layout) => (
            <Card key={layout.id} layout={layout} />
          ))}
        </div>

        <div className="pb-16 text-center">
          <p className="text-gray-500 text-xs">Tell me which layout you want and I'll rebuild the AuthLayout + all auth pages.</p>
        </div>
      </main>
    </div>
  );
};
