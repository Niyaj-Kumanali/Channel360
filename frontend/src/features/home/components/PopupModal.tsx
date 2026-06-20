import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { X, ChevronLeft, ChevronRight } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import type { HomepagePopup } from '@/features/cms/types/cms.types';

const STORAGE_KEY = 'popup_dismissed';

const loadDismissed = (): Set<number> => {
  try {
    const raw = sessionStorage.getItem(STORAGE_KEY);
    return new Set<number>(raw ? JSON.parse(raw) : []);
  } catch {
    return new Set();
  }
};

const persistDismissed = (ids: Set<number>) => {
  try {
    sessionStorage.setItem(STORAGE_KEY, JSON.stringify([...ids]));
  } catch { /* ignore */ }
};

interface Props {
  popups: HomepagePopup[];
}

export const PopupModal: React.FC<Props> = ({ popups }) => {
  const [isOpen, setIsOpen] = useState(false);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [dismissedIds, setDismissedIds] = useState<Set<number>>(loadDismissed);

  const visible = popups.filter(p => !dismissedIds.has(p.id));

  useEffect(() => {
    if (visible.length > 0 && !isOpen) {
      const timer = setTimeout(() => setIsOpen(true), 1500);
      return () => clearTimeout(timer);
    }
  }, [visible.length, isOpen]);

  const current = visible[currentIndex];
  if (!isOpen || !current) return null;

  const hasNext = currentIndex < visible.length - 1;
  const hasPrev = currentIndex > 0;

  const handleClose = () => {
    setIsOpen(false);
    setCurrentIndex(0);
  };

  const handleDismiss = () => {
    setDismissedIds(prev => {
      const next = new Set(prev).add(current.id);
      persistDismissed(next);
      return next;
    });
    if (currentIndex >= visible.length - 1) {
      setIsOpen(false);
      setCurrentIndex(0);
    } else {
      setCurrentIndex(i => i + 1);
    }
  };

  return (
    <div className="fixed inset-0 z-[60] flex items-center justify-center p-4">
      <div className="absolute inset-0 bg-black/60" onClick={handleClose} />
      <div className="relative max-w-lg w-full rounded-2xl border border-border bg-card shadow-2xl overflow-hidden animate-in fade-in zoom-in duration-200">
        {current.imageUrl && (
          <div className="aspect-video w-full overflow-hidden bg-muted">
            <img
              src={current.imageUrl}
              alt={current.title}
              className="h-full w-full object-cover"
            />
          </div>
        )}

        <div className="p-6">
          <h3 className="text-xl font-semibold text-foreground">{current.title}</h3>
          {current.description && (
            <p className="mt-2 text-sm text-muted-foreground leading-relaxed">
              {current.description}
            </p>
          )}

          <div className="mt-6 flex items-center gap-3">
            {(current.ctaUrl || current.ctaButtonText) && (
              current.ctaUrl ? (
                <Link to={current.ctaUrl} onClick={handleClose}>
                  <Button size="sm">{current.ctaButtonText || 'Learn More'}</Button>
                </Link>
              ) : (
                <Button size="sm" onClick={handleClose}>{current.ctaButtonText}</Button>
              )
            )}
            <button
              onClick={handleDismiss}
              className="text-xs text-muted-foreground hover:text-foreground transition-colors"
            >
              {hasNext ? 'Show next' : "Don't show again"}
            </button>
          </div>
        </div>

        <button
          onClick={handleClose}
          className="absolute top-3 right-3 flex h-8 w-8 items-center justify-center rounded-full bg-background/80 text-muted-foreground hover:text-foreground transition-colors"
        >
          <X className="h-4 w-4" />
        </button>

        {visible.length > 1 && (
          <div className="absolute bottom-3 left-1/2 -translate-x-1/2 flex items-center gap-2">
            {hasPrev && (
              <button
                onClick={() => setCurrentIndex(i => i - 1)}
                className="flex h-6 w-6 items-center justify-center rounded-full bg-background/80 text-muted-foreground hover:text-foreground transition-colors"
              >
                <ChevronLeft className="h-3 w-3" />
              </button>
            )}
            <span className="text-xs text-muted-foreground">
              {currentIndex + 1} / {visible.length}
            </span>
            {hasNext && (
              <button
                onClick={() => setCurrentIndex(i => i + 1)}
                className="flex h-6 w-6 items-center justify-center rounded-full bg-background/80 text-muted-foreground hover:text-foreground transition-colors"
              >
                <ChevronRight className="h-3 w-3" />
              </button>
            )}
          </div>
        )}
      </div>
    </div>
  );
};
