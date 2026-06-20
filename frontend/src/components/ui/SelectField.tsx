import React, { useState, useRef, useEffect } from 'react';
import { ChevronDown } from 'lucide-react';
import { cn } from '@/lib/utils';

export interface SelectOption {
  value: string;
  label: string;
  icon?: React.ReactNode;
}

interface SelectFieldProps {
  label?: string;
  error?: string;
  value: string;
  onChange: (value: string) => void;
  options: SelectOption[];
  placeholder?: string;
  className?: string;
  id?: string;
}

export const SelectField = React.forwardRef<HTMLDivElement, SelectFieldProps>(
  ({ label, error, value, onChange, options, placeholder = 'Select...', className, id }, ref) => {
    const [open, setOpen] = useState(false);
    const containerRef = useRef<HTMLDivElement>(null);

    const selected = options.find(o => o.value === value);

    useEffect(() => {
      const handleClickOutside = (e: MouseEvent) => {
        if (containerRef.current && !containerRef.current.contains(e.target as Node)) {
          setOpen(false);
        }
      };
      document.addEventListener('mousedown', handleClickOutside);
      return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    return (
      <div className="space-y-1.5" ref={containerRef}>
        {label && (
          <label className="text-sm font-medium text-foreground">{label}</label>
        )}
        <div className="relative">
          <button
            type="button"
            onClick={() => setOpen(!open)}
            className={cn(
              'flex h-10 w-full items-center gap-2 rounded-lg border bg-background px-3 py-2 text-sm ring-offset-background transition-shadow duration-200',
              error ? 'border-red-500' : 'border-input hover:border-ring/50',
              !selected && 'text-muted-foreground',
              className
            )}
          >
            {selected?.icon && <span className="h-4 w-4 shrink-0">{selected.icon}</span>}
            <span className="flex-1 text-left">{selected ? selected.label : placeholder}</span>
            <ChevronDown className={cn('h-4 w-4 shrink-0 text-muted-foreground transition-transform', open && 'rotate-180')} />
          </button>
          {open && (
            <div className="absolute z-50 mt-1 max-h-60 w-full overflow-y-auto rounded-lg border border-border bg-background py-1 shadow-lg">
              {options.map((opt) => (
                <button
                  key={opt.value}
                  type="button"
                  onClick={() => { onChange(opt.value); setOpen(false); }}
                  className={cn(
                    'flex w-full items-center gap-2 px-3 py-2 text-sm transition-colors hover:bg-accent hover:text-accent-foreground',
                    opt.value === value && 'bg-accent text-accent-foreground'
                  )}
                >
                  {opt.icon && <span className="h-4 w-4 shrink-0">{opt.icon}</span>}
                  <span>{opt.label}</span>
                </button>
              ))}
            </div>
          )}
        </div>
        {error && <p className="mt-1 text-sm text-red-500">{error}</p>}
      </div>
    );
  }
);
SelectField.displayName = 'SelectField';
