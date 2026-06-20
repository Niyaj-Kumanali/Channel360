import React from 'react';
import { cn } from '@/lib/utils';

interface FloatingLabelInputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label: string;
  error?: string;
  hint?: string;
}

export const FloatingLabelInput = React.forwardRef<HTMLInputElement, FloatingLabelInputProps>(
  ({ className, label, error, hint, id, placeholder, ...props }, ref) => {
    return (
      <div className="space-y-1">
        <div className="relative">
          <input
            id={id}
            placeholder={placeholder || ' '}
            className={cn(
              'peer h-12 w-full rounded-lg border bg-background px-3 py-3.5 text-sm ring-offset-background',
              'focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2',
              'placeholder-transparent transition-shadow duration-200',
              'disabled:cursor-not-allowed disabled:opacity-50',
              error
                ? 'border-red-500 focus-visible:ring-red-500'
                : 'border-input hover:border-gray-300 focus-visible:border-ring focus-visible:ring-ring'
            )}
            ref={ref}
            {...props}
          />
          <label
            htmlFor={id}
            className={cn(
              'absolute left-3 px-1 transition-all duration-200 pointer-events-none bg-white',
              'top-1/2 -translate-y-1/2 text-sm',
              'peer-focus:-top-2.5 peer-focus:translate-y-0 peer-focus:text-xs peer-focus:font-medium',
              'peer-[:not(:placeholder-shown)]:-top-2.5 peer-[:not(:placeholder-shown)]:translate-y-0 peer-[:not(:placeholder-shown)]:text-xs peer-[:not(:placeholder-shown)]:font-medium',
              error
                ? 'text-red-500'
                : 'text-muted-foreground peer-focus:text-primary'
            )}
          >
            {label}
          </label>
        </div>
        {error ? (
          <p className="text-xs text-red-500">{error}</p>
        ) : hint ? (
          <p className="text-xs text-gray-400">{hint}</p>
        ) : null}
      </div>
    );
  }
);

FloatingLabelInput.displayName = 'FloatingLabelInput';
