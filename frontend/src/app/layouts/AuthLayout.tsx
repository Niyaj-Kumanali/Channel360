import React from 'react';
import { Outlet, Link } from 'react-router-dom';
import { Logo } from '@/components/ui/Logo';

const NetworkBg: React.FC = () => (
  <div className="absolute inset-0 flex items-center justify-center pointer-events-none select-none">
    <svg
      viewBox="0 0 600 600"
      className="w-full h-full"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
    >
      <defs>
        <radialGradient id="hubGlow" cx="50%" cy="50%" r="50%">
          <stop offset="0%" stopColor="#f59e0b" stopOpacity="0.35" />
          <stop offset="60%" stopColor="#f59e0b" stopOpacity="0.1" />
          <stop offset="100%" stopColor="#f59e0b" stopOpacity="0" />
        </radialGradient>
        <radialGradient id="nodeGlow" cx="50%" cy="50%" r="50%">
          <stop offset="0%" stopColor="white" stopOpacity="0.5" />
          <stop offset="100%" stopColor="white" stopOpacity="0" />
        </radialGradient>
      </defs>

      {/* Central glow */}
      <circle cx="300" cy="300" r="200" fill="url(#hubGlow)" />

      {/* Background grid */}
      <g opacity="0.06">
        <line x1="300" y1="30" x2="300" y2="570" stroke="white" strokeWidth="0.5" />
        <line x1="30" y1="300" x2="570" y2="300" stroke="white" strokeWidth="0.5" />
        <line x1="110" y1="110" x2="490" y2="490" stroke="white" strokeWidth="0.5" />
        <line x1="490" y1="110" x2="110" y2="490" stroke="white" strokeWidth="0.5" />
        <circle cx="300" cy="300" r="100" stroke="white" strokeWidth="0.5" />
        <circle cx="300" cy="300" r="200" stroke="white" strokeWidth="0.5" />
        <circle cx="300" cy="300" r="250" stroke="white" strokeWidth="0.5" />
      </g>

      {/* Orbital ring 1 (dashed, slow clockwise) */}
      <g className="animate-orbit" style={{ transformOrigin: '300px 300px' }}>
        <circle
          cx="300" cy="300" r="90"
          stroke="white" strokeWidth="0.8" strokeDasharray="4 8"
          opacity="0.15"
        />
        <circle cx="300" cy="210" r="3" fill="white" opacity="0.3" />
        <circle cx="390" cy="300" r="3" fill="white" opacity="0.3" />
        <circle cx="300" cy="390" r="3" fill="white" opacity="0.3" />
        <circle cx="210" cy="300" r="3" fill="white" opacity="0.3" />
      </g>

      {/* Orbital ring 2 (dashed, slow counter-clockwise) */}
      <g className="animate-orbit-reverse" style={{ transformOrigin: '300px 300px' }}>
        <circle
          cx="300" cy="300" r="170"
          stroke="white" strokeWidth="0.6" strokeDasharray="2 10"
          opacity="0.12"
        />
        <circle cx="300" cy="130" r="2" fill="white" opacity="0.2" />
        <circle cx="470" cy="300" r="2" fill="white" opacity="0.2" />
        <circle cx="300" cy="470" r="2" fill="white" opacity="0.2" />
        <circle cx="130" cy="300" r="2" fill="white" opacity="0.2" />
        <circle cx="420" cy="180" r="2" fill="white" opacity="0.2" />
        <circle cx="180" cy="420" r="2" fill="white" opacity="0.2" />
      </g>

      {/* Connection lines from hub to inner orbit */}
      <g opacity="0.12">
        <line x1="300" y1="300" x2="300" y2="210" stroke="white" strokeWidth="1" />
        <line x1="300" y1="300" x2="390" y2="300" stroke="white" strokeWidth="1" />
        <line x1="300" y1="300" x2="300" y2="390" stroke="white" strokeWidth="1" />
        <line x1="300" y1="300" x2="210" y2="300" stroke="white" strokeWidth="1" />
      </g>

      {/* Connection lines from inner orbit to outer orbit */}
      <g opacity="0.08">
        <line x1="300" y1="210" x2="300" y2="130" stroke="white" strokeWidth="0.6" />
        <line x1="390" y1="300" x2="470" y2="300" stroke="white" strokeWidth="0.6" />
        <line x1="300" y1="390" x2="300" y2="470" stroke="white" strokeWidth="0.6" />
        <line x1="210" y1="300" x2="130" y2="300" stroke="white" strokeWidth="0.6" />
        <line x1="390" y1="300" x2="420" y2="180" stroke="white" strokeWidth="0.6" strokeDasharray="3 2" className="animate-dash-flow" />
        <line x1="210" y1="300" x2="180" y2="420" stroke="white" strokeWidth="0.6" strokeDasharray="3 2" className="animate-dash-flow" />
      </g>

      {/* Central hub */}
      <circle cx="300" cy="300" r="24" fill="url(#nodeGlow)" />
      <circle cx="300" cy="300" r="10" fill="white" opacity="0.15" />
      <circle cx="300" cy="300" r="5" fill="white" opacity="0.5" className="animate-pulse-glow" />
      <circle cx="300" cy="300" r="12" stroke="white" strokeWidth="1" opacity="0.1" strokeDasharray="3 3" />

      {/* Floating dots */}
      <circle cx="140" cy="200" r="1.5" fill="white" opacity="0.15" className="animate-float" />
      <circle cx="460" cy="220" r="1.5" fill="white" opacity="0.15" className="animate-float-delayed" />
      <circle cx="180" cy="450" r="1.5" fill="white" opacity="0.15" className="animate-float-delayed" />
      <circle cx="440" cy="430" r="1.5" fill="white" opacity="0.15" className="animate-float" />

      {/* Node labels (hidden on smaller screens) */}
      <text x="300" y="198" fill="white" opacity="0.35" fontSize="7" textAnchor="middle" fontFamily="Inter, sans-serif">HQ</text>
      <text x="402" y="303" fill="white" opacity="0.25" fontSize="6" textAnchor="middle" fontFamily="Inter, sans-serif">REGION</text>
      <text x="300" y="404" fill="white" opacity="0.25" fontSize="6" textAnchor="middle" fontFamily="Inter, sans-serif">REGION</text>
      <text x="198" y="303" fill="white" opacity="0.25" fontSize="6" textAnchor="middle" fontFamily="Inter, sans-serif">REGION</text>
      <text x="300" y="122" fill="white" opacity="0.15" fontSize="5" textAnchor="middle" fontFamily="Inter, sans-serif">PARTNER</text>
      <text x="478" y="303" fill="white" opacity="0.15" fontSize="5" textAnchor="middle" fontFamily="Inter, sans-serif">PARTNER</text>
    </svg>
  </div>
);

export const AuthLayout: React.FC = () => {
  return (
    <div className="min-h-screen flex">
      {/* Brand Panel */}
      <div className="hidden lg:flex lg:w-1/2 bg-gradient-to-br from-amber-800 via-amber-700 to-amber-500 p-12 flex-col relative overflow-hidden">
        <NetworkBg />
        <div className="relative z-10 flex flex-col h-full">
          {/* Top */}
          <Link to="/" className="hover:opacity-80 transition-opacity w-fit">
            <Logo variant="light" />
          </Link>

          {/* Center */}
          <div className="flex-1 flex flex-col items-center justify-center text-center px-8">
            <h1 className="text-white text-3xl sm:text-4xl font-light leading-tight max-w-sm">
              360° Visibility Across Your Entire Channel Ecosystem
            </h1>
            <div className="flex items-center gap-3 my-6">
              <div className="h-px w-16 bg-white/15" />
              <div className="h-2 w-2 rotate-45 bg-amber-300/60" />
              <div className="h-px w-16 bg-white/15" />
            </div>
            <p className="text-white/40 text-sm font-light tracking-wider uppercase">
              Channel Data Management Platform
            </p>
          </div>

          {/* Bottom */}
          <div className="flex items-center justify-between">
            <div className="text-white/35 text-xs">
              &copy; {new Date().getFullYear()} Channel360
            </div>
            <div className="flex gap-5 text-white/30 text-xs">
              <span className="hover:text-white/50 transition-colors cursor-pointer">Privacy</span>
              <span className="hover:text-white/50 transition-colors cursor-pointer">Terms</span>
            </div>
          </div>
        </div>
      </div>

      {/* Form Panel */}
      <div className="flex-1 flex items-center justify-center p-4 sm:p-8 bg-gray-50">
        <div className="w-full max-w-md animate-slide-up">
          <Link
            to="/"
            className="lg:hidden flex justify-center mb-8 hover:opacity-80 transition-opacity"
          >
            <Logo variant="dark" />
          </Link>
          <Outlet />
        </div>
      </div>
    </div>
  );
};
