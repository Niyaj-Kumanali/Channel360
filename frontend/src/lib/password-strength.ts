export interface PasswordRule {
  key: string;
  label: string;
  met: boolean;
}

const rules: Omit<PasswordRule, 'met'>[] = [
  { key: 'min6', label: 'At least 6 characters' },
  { key: 'uppercase', label: 'Contains uppercase letter' },
  { key: 'lowercase', label: 'Contains lowercase letter' },
  { key: 'number', label: 'Contains number' },
  { key: 'symbol', label: 'Contains special character' },
];

export function getPasswordRules(password: string): PasswordRule[] {
  return rules.map((r) => {
    switch (r.key) {
      case 'min6': return { ...r, met: password.length >= 6 };
      case 'uppercase': return { ...r, met: /[A-Z]/.test(password) };
      case 'lowercase': return { ...r, met: /[a-z]/.test(password) };
      case 'number': return { ...r, met: /\d/.test(password) };
      case 'symbol': return { ...r, met: /[^a-zA-Z0-9]/.test(password) };
      default: return { ...r, met: false };
    }
  });
}
