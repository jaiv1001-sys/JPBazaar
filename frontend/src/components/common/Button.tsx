import React from 'react';
import { cn } from '../../utils/cn';

export interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'gold' | 'outline' | 'dark' | 'ghost' | 'danger';
  size?: 'sm' | 'md' | 'lg';
  isLoading?: boolean;
  leftIcon?: React.ReactNode;
  rightIcon?: React.ReactNode;
}

export const Button: React.FC<ButtonProps> = ({
  children,
  className,
  variant = 'gold',
  size = 'md',
  isLoading = false,
  leftIcon,
  rightIcon,
  disabled,
  ...props
}) => {
  const baseStyles =
    'relative inline-flex items-center justify-center font-medium transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-luxury-black disabled:opacity-50 disabled:cursor-not-allowed select-none active:scale-[0.98]';

  const variants = {
    gold: 'bg-luxury-gold text-luxury-black hover:bg-luxury-gold-light focus:ring-luxury-gold font-semibold shadow-luxury-sm hover:shadow-gold-glow',
    outline: 'border border-luxury-gold text-luxury-gold hover:bg-luxury-gold hover:text-luxury-black focus:ring-luxury-gold',
    dark: 'bg-luxury-surface border border-luxury-border text-luxury-white hover:border-luxury-gold/50 hover:bg-luxury-elevated focus:ring-luxury-gold',
    ghost: 'text-luxury-muted hover:text-luxury-white hover:bg-luxury-elevated focus:ring-luxury-border',
    danger: 'bg-luxury-danger text-luxury-white hover:bg-red-600 focus:ring-luxury-danger',
  };

  const sizes = {
    sm: 'text-xs px-3 py-1.5 rounded-sm gap-1.5 tracking-wider uppercase',
    md: 'text-sm px-5 py-2.5 rounded-sm gap-2 tracking-wide font-medium',
    lg: 'text-base px-7 py-3.5 rounded-sm gap-2.5 tracking-wide font-semibold',
  };

  return (
    <button
      className={cn(baseStyles, variants[variant], sizes[size], className)}
      disabled={disabled || isLoading}
      {...props}
    >
      {isLoading ? (
        <span className="inline-block w-4 h-4 border-2 border-current border-t-transparent rounded-full animate-spin" />
      ) : (
        leftIcon && <span className="inline-flex shrink-0">{leftIcon}</span>
      )}
      <span>{children}</span>
      {!isLoading && rightIcon && <span className="inline-flex shrink-0">{rightIcon}</span>}
    </button>
  );
};
