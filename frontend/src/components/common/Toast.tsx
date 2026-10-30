import React from 'react';
import { cn } from '../../utils/cn';
import { CheckCircle2, AlertCircle, Info, X } from 'lucide-react';

export interface ToastProps {
  type?: 'success' | 'error' | 'info';
  message: string;
  onClose?: () => void;
  className?: string;
}

export const Toast: React.FC<ToastProps> = ({
  type = 'info',
  message,
  onClose,
  className,
}) => {
  const icons = {
    success: <CheckCircle2 size={18} className="text-luxury-gold shrink-0" />,
    error: <AlertCircle size={18} className="text-luxury-danger shrink-0" />,
    info: <Info size={18} className="text-luxury-info shrink-0" />,
  };

  const borders = {
    success: 'border-l-4 border-l-luxury-gold',
    error: 'border-l-4 border-l-luxury-danger',
    info: 'border-l-4 border-l-luxury-info',
  };

  return (
    <div
      className={cn(
        'flex items-center gap-3 bg-luxury-surface border border-luxury-border shadow-luxury-lg px-4 py-3 rounded-sm text-sm text-luxury-white animate-slide-up max-w-sm',
        borders[type],
        className
      )}
    >
      {icons[type]}
      <span className="flex-1 font-medium">{message}</span>
      {onClose && (
        <button
          onClick={onClose}
          className="text-luxury-muted hover:text-luxury-white transition-colors p-0.5 rounded-sm"
        >
          <X size={14} />
        </button>
      )}
    </div>
  );
};
