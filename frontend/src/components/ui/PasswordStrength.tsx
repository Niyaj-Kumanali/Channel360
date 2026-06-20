import React from 'react';
import { getPasswordRules } from '@/lib/password-strength';
import { cn } from '@/lib/utils';

interface PasswordStrengthProps {
  value: string;
}

export const PasswordStrength: React.FC<PasswordStrengthProps> = ({ value }) => {
  if (!value) return null;

  const rules = getPasswordRules(value);
  const metCount = rules.filter((r) => r.met).length;

  return (
    <div className="mt-4 space-y-2">
      <div className="flex gap-1">
        {rules.map((rule, i) => (
          <div
            key={rule.key}
            className={cn(
              'h-1 flex-1 rounded-full transition-colors duration-300',
              i < metCount ? (
                metCount <= 2 ? 'bg-red-500' : metCount <= 4 ? 'bg-amber-400' : 'bg-green-500'
              ) : 'bg-muted'
            )}
          />
        ))}
      </div>
      <ul className="space-y-1">
        {rules.map((rule) => (
          <li key={rule.key} className="flex items-center gap-1.5 text-xs">
            <span className={rule.met ? 'text-green-500' : 'text-muted-foreground'}>
              {rule.met ? '✓' : '○'}
            </span>
            <span className={rule.met ? 'text-foreground' : 'text-muted-foreground'}>
              {rule.label}
            </span>
          </li>
        ))}
      </ul>
    </div>
  );
};
