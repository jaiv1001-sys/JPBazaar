import React from 'react';
import { cn } from '../../utils/cn';

export interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  helperText?: string;
  leftIcon?: React.ReactNode;
  rightIcon?: React.ReactNode;
}

export const Input = React.forwardRef<HTMLInputElement, InputProps>(
  ({ label, error, helperText, leftIcon, rightIcon, className, id, ...props }, ref) => {
    const inputId = id || (label ? label.toLowerCase().replace(/\s+/g, '-') : undefined);

    return (
      <div className="w-full flex flex-col gap-1.5">
        {label && (
          <label htmlFor={inputId} className="text-xs font-semibold tracking-wider uppercase text-luxury-muted">
            {label}
          </label>
        )}
        <div className="relative flex items-center">
          {leftIcon && (
            <span className="absolute left-3.5 text-luxury-subtle pointer-events-none flex items-center justify-center">
              {leftIcon}
            </span>
          )}
          <input
            id={inputId}
            ref={ref}
            className={cn(
              'w-full bg-luxury-surface border border-luxury-border rounded-sm px-4 py-2.5 text-sm text-luxury-white placeholder:text-luxury-subtle transition-all duration-200 focus:outline-none focus:border-luxury-gold focus:ring-1 focus:ring-luxury-gold',
              leftIcon && 'pl-10',
              rightIcon && 'pr-10',
              error && 'border-luxury-danger focus:border-luxury-danger focus:ring-luxury-danger',
              className
            )}
            {...props}
          />
          {rightIcon && (
            <span className="absolute right-3.5 text-luxury-subtle flex items-center justify-center">
              {rightIcon}
            </span>
          )}
        </div>
        {error && <span className="text-xs text-luxury-danger font-medium mt-0.5">{error}</span>}
        {!error && helperText && <span className="text-xs text-luxury-subtle mt-0.5">{helperText}</span>}
      </div>
    );
  }
);

Input.displayName = 'Input';
