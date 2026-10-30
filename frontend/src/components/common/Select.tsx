import React from 'react';
import { cn } from '../../utils/cn';

export interface SelectProps extends React.SelectHTMLAttributes<HTMLSelectElement> {
  label?: string;
  error?: string;
  options: { value: string | number; label: string }[];
}

export const Select = React.forwardRef<HTMLSelectElement, SelectProps>(
  ({ label, error, options, className, id, ...props }, ref) => {
    const selectId = id || (label ? label.toLowerCase().replace(/\s+/g, '-') : undefined);

    return (
      <div className="w-full flex flex-col gap-1.5">
        {label && (
          <label htmlFor={selectId} className="text-xs font-semibold tracking-wider uppercase text-luxury-muted">
            {label}
          </label>
        )}
        <div className="relative">
          <select
            id={selectId}
            ref={ref}
            className={cn(
              'w-full bg-luxury-surface border border-luxury-border rounded-sm px-4 py-2.5 text-sm text-luxury-white transition-all duration-200 focus:outline-none focus:border-luxury-gold focus:ring-1 focus:ring-luxury-gold appearance-none cursor-pointer',
              error && 'border-luxury-danger focus:border-luxury-danger focus:ring-luxury-danger',
              className
            )}
            {...props}
          >
            {options.map((opt) => (
              <option key={opt.value} value={opt.value} className="bg-luxury-surface text-luxury-white">
                {opt.label}
              </option>
            ))}
          </select>
          <div className="absolute right-3.5 top-1/2 -translate-y-1/2 pointer-events-none text-luxury-muted text-xs">
            ▼
          </div>
        </div>
        {error && <span className="text-xs text-luxury-danger font-medium mt-0.5">{error}</span>}
      </div>
    );
  }
);

Select.displayName = 'Select';
