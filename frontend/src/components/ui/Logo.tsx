import React from 'react';
import { cn } from '@/lib/utils';

interface LogoProps {
  variant?: 'light' | 'dark';
  size?: 'sm' | 'md' | 'lg';
  showText?: boolean;
  className?: string;
}

const sizeMap = {
  sm: { icon: 8, text: 'text-lg' },
  md: { icon: 10, text: 'text-xl' },
  lg: { icon: 12, text: 'text-2xl' },
};

export const Logo: React.FC<LogoProps> = ({
  variant = 'dark',
  size = 'md',
  showText = true,
  className,
}) => {
  const { icon, text } = sizeMap[size];

  return (
    <div className={cn('flex items-center gap-3', className)}>
      <svg
        width={icon * 4}
        height={icon * 4}
        viewBox="0 0 40 40"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
        className="flex-shrink-0"
      >
        <rect
          width="40"
          height="40"
          rx="10"
          className={variant === 'light' ? 'fill-white/10' : 'fill-primary'}
        />
        <path
          d="M12 20C12 15.6 15.6 12 20 12C24.4 12 28 15.6 28 20"
          stroke={variant === 'light' ? 'white' : 'white'}
          strokeWidth="3.5"
          strokeLinecap="round"
          strokeLinejoin="round"
          fill="none"
        />
        <path
          d="M12 20C12 24.4 15.6 28 20 28C24.4 28 28 24.4 28 20"
          stroke={variant === 'light' ? 'white' : 'white'}
          strokeWidth="3.5"
          strokeLinecap="round"
          strokeLinejoin="round"
          fill="none"
          strokeDasharray="2 6"
          strokeDashoffset="1"
        />
      </svg>
      {showText && (
        <span
          className={cn(
            'font-semibold tracking-tight',
            text,
            variant === 'light' ? 'text-white' : 'text-gray-900'
          )}
        >
          Channel360
        </span>
      )}
    </div>
  );
};
