import React from 'react';
import { Outlet, Link } from 'react-router-dom';
import { Logo } from '@/components/ui/Logo';

export const AuthLayout: React.FC = () => {
  return (
    <div className="min-h-screen flex">
      <div className="hidden lg:flex lg:w-1/2 bg-gradient-to-br from-primary-950 via-primary-900 to-primary-800 p-12 flex-col justify-between relative overflow-hidden">
        <div className="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHZpZXdCb3g9IjAgMCA2MCA2MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48ZyBmaWxsPSJub25lIiBmaWxsLXJ1bGU9ImV2ZW5vZGQiPjxnIGZpbGw9IiNmZmYiIGZpbGwtb3BhY2l0eT0iMC4wMyI+PGNpcmNsZSBjeD0iMzAiIGN5PSIzMCIgcj0iMiIvPjwvZz48L2c+PC9zdmc+')] opacity-50" />
        <Link to="/" className="relative z-10 hover:opacity-80 transition-opacity">
          <Logo variant="light" />
        </Link>
        <div className="relative z-10">
          <blockquote className="text-white">
            <p className="text-2xl font-light leading-relaxed mb-4">
              "Channel management shouldn't be complex. It should be intuitive, powerful, and effortless."
            </p>
            <footer className="text-primary-200 text-sm">
              Channel360 Platform
            </footer>
          </blockquote>
        </div>
        <div className="relative z-10 text-primary-200 text-sm">
          &copy; {new Date().getFullYear()} Channel360. All rights reserved.
        </div>
      </div>
      <div className="flex-1 flex items-center justify-center p-4 sm:p-8 bg-gray-50">
        <div className="w-full max-w-sm animate-slide-up">
          <Link to="/" className="lg:hidden flex justify-center mb-8 hover:opacity-80 transition-opacity">
            <Logo variant="dark" />
          </Link>
          <Outlet />
        </div>
      </div>
    </div>
  );
};
