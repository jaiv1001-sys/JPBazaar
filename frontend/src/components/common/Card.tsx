import React from 'react';
import { cn } from '../../utils/cn';

export interface CardProps extends React.HTMLAttributes<HTMLDivElement> {
  variant?: 'surface' | 'elevated' | 'ivory';
  hoverEffect?: boolean;
}

export const Card: React.FC<CardProps> = ({
  children,
  className,
  variant = 'surface',
  hoverEffect = false,
  ...props
}) => {
  const base = 'rounded-sm transition-all duration-300';

  const variants = {
    surface: 'bg-luxury-surface border border-luxury-border text-luxury-white',
    elevated: 'bg-luxury-elevated border border-luxury-border-light shadow-luxury-md text-luxury-white',
    ivory: 'bg-luxury-ivory border border-luxury-ivory-dark text-luxury-black shadow-sm',
  };

  const hover = hoverEffect
    ? 'hover:-translate-y-1 hover:border-luxury-gold/50 hover:shadow-luxury-lg'
    : '';

  return (
    <div className={cn(base, variants[variant], hover, className)} {...props}>
      {children}
    </div>
  );
};
