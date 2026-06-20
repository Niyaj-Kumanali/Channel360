const AUTH_PREFIX = 'auth_';

export const authStorage = {
  get(key: string): string | null {
    return sessionStorage.getItem(AUTH_PREFIX + key);
  },
  set(key: string, value: string): void {
    sessionStorage.setItem(AUTH_PREFIX + key, value);
  },
  remove(key: string): void {
    sessionStorage.removeItem(AUTH_PREFIX + key);
  },
  clear(): void {
    sessionStorage.clear();
  },
};

export const appStorage = {
  get: <T = string>(key: string): T | null => {
    const raw = localStorage.getItem(key);
    if (raw === null) return null;
    try { return JSON.parse(raw) as T; } catch { return raw as unknown as T; }
  },
  set(key: string, value: unknown): void {
    localStorage.setItem(key, typeof value === 'string' ? value : JSON.stringify(value));
  },
  remove(key: string): void {
    localStorage.removeItem(key);
  },
};
