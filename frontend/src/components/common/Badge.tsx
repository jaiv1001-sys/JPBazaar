import React from 'react';
import { cn } from '../../utils/cn';

export interface BadgeProps extends React.HTMLAttributes<HTMLSpanElement> {
  variant?: 'gold' | 'gold-outline' | 'dark' | 'ivory' | 'success' | 'danger' | 'warning';
  size?: 'sm' | 'md';
}

export const Badge: React.FC<BadgeProps> = ({
  children,
  className,
  variant = 'gold',
  size = 'md',
  ...props
}) => {
  const base = 'inline-flex items-center justify-center font-bold tracking-wider uppercase rounded-sm select-none';

  const variants = {
    gold: 'bg-luxury-gold text-luxury-black',
    'gold-outline': 'border border-luxury-gold text-luxury-gold bg-luxury-gold/10',
    dark: 'bg-luxury-elevated border border-luxury-border text-luxury-muted',
    ivory: 'bg-luxury-ivory text-luxury-black border border-luxury-ivory-dark',
    success: 'bg-emerald-950/80 text-emerald-400 border border-emerald-800/50',
    danger: 'bg-red-950/80 text-red-400 border border-red-800/50',
    warning: 'bg-amber-950/80 text-amber-400 border border-amber-800/50',
  };

  const sizes = {
    sm: 'text-[10px] px-2 py-0.5',
    md: 'text-xs px-2.5 py-1',
  };

  return (
    <span className={cn(base, variants[variant], sizes[size], className)} {...props}>
      {children}
    </span>
  );
};
