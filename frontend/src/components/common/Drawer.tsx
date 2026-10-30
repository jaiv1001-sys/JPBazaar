import React, { useEffect } from 'react';
import { cn } from '../../utils/cn';
import { X } from 'lucide-react';

export interface DrawerProps {
  isOpen: boolean;
  onClose: () => void;
  title?: string;
  children: React.ReactNode;
  position?: 'right' | 'left';
  width?: 'sm' | 'md' | 'lg';
}

export const Drawer: React.FC<DrawerProps> = ({
  isOpen,
  onClose,
  title,
  children,
  position = 'right',
  width = 'md',
}) => {
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    if (isOpen) {
      document.body.style.overflow = 'hidden';
      window.addEventListener('keydown', handleKeyDown);
    }
    return () => {
      document.body.style.overflow = 'unset';
      window.removeEventListener('keydown', handleKeyDown);
    };
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  const widths = {
    sm: 'max-w-sm',
    md: 'max-w-md',
    lg: 'max-w-lg',
  };

  return (
    <div className="fixed inset-0 z-50 overflow-hidden bg-black/75 backdrop-blur-sm animate-fade-in">
      <div className="fixed inset-0" onClick={onClose} aria-hidden="true" />
      <div className={cn('fixed inset-y-0 flex max-w-full', position === 'right' ? 'right-0' : 'left-0')}>
        <div
          className={cn(
            'w-screen bg-luxury-surface border-luxury-border shadow-2xl flex flex-col',
            position === 'right' ? 'border-l' : 'border-r',
            widths[width]
          )}
        >
          {/* Header */}
          <div className="flex items-center justify-between px-6 py-5 border-b border-luxury-border">
            <h3 className="font-serif text-lg font-bold tracking-tight text-luxury-white">
              {title || ''}
            </h3>
            <button
              onClick={onClose}
              className="text-luxury-muted hover:text-luxury-white transition-colors p-1.5 rounded-sm hover:bg-luxury-elevated"
              aria-label="Close drawer"
            >
              <X size={20} />
            </button>
          </div>

          {/* Content */}
          <div className="flex-1 overflow-y-auto p-6">
            {children}
          </div>
        </div>
      </div>
    </div>
  );
};
