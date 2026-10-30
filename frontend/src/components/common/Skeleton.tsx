import React from 'react';
import { cn } from '../../utils/cn';

export interface SkeletonProps extends React.HTMLAttributes<HTMLDivElement> {
  variant?: 'rect' | 'circle' | 'text';
}

export const Skeleton: React.FC<SkeletonProps> = ({
  className,
  variant = 'rect',
  ...props
}) => {
  const base = 'animate-pulse bg-luxury-elevated border border-luxury-border/40';

  const variants = {
    rect: 'rounded-sm',
    circle: 'rounded-full',
    text: 'h-4 rounded-sm w-full',
  };

  return <div className={cn(base, variants[variant], className)} {...props} />;
};
