import React from 'react';
import { cn } from '@/lib/utils';

interface CardProps {
  className?: string;
  children: React.ReactNode;
}

export const Card: React.FC<CardProps> = ({ className, children }) => (
  <div className={cn('bg-card rounded-xl border border-border shadow-sm', className)}>
    {children}
  </div>
);

export const CardHeader: React.FC<CardProps> = ({ className, children }) => (
  <div className={cn('p-6 border-b border-border', className)}>{children}</div>
);

export const CardContent: React.FC<CardProps> = ({ className, children }) => (
  <div className={cn('p-6', className)}>{children}</div>
);
