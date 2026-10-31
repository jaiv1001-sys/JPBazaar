import { Component, ErrorInfo, ReactNode } from 'react';
import { AlertTriangle, RotateCcw, Home } from 'lucide-react';
import { Button } from './Button';

interface Props {
  children: ReactNode;
  fallbackTitle?: string;
  fallbackMessage?: string;
}

interface State {
  hasError: boolean;
  error: Error | null;
}

export class ErrorBoundary extends Component<Props, State> {
  public state: State = {
    hasError: false,
    error: null,
  };

  public static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error };
  }

  public componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error('Uncaught error caught by ErrorBoundary:', error, errorInfo);
  }

  private handleReset = () => {
    this.setState({ hasError: false, error: null });
  };

  private handleGoHome = () => {
    window.location.href = '/';
  };

  public render() {
    if (this.state.hasError) {
      return (
        <div className="min-h-[60vh] flex items-center justify-center px-4 py-12 bg-luxury-black text-luxury-white">
          <div className="max-w-md w-full bg-luxury-surface border border-luxury-border rounded-sm p-8 text-center space-y-6 shadow-luxury-lg">
            <div className="w-16 h-16 rounded-full bg-luxury-gold/10 border border-luxury-gold/30 flex items-center justify-center text-luxury-gold mx-auto">
              <AlertTriangle size={32} />
            </div>

            <div className="space-y-2">
              <span className="text-[10px] font-semibold tracking-luxury-widest uppercase text-luxury-gold">
                System Advisory
              </span>
              <h2 className="font-serif text-2xl font-bold text-luxury-white">
                {this.props.fallbackTitle || 'Rendering Exception Encountered'}
              </h2>
              <p className="text-xs text-luxury-muted leading-relaxed">
                {this.props.fallbackMessage ||
                  'An unexpected interface disruption occurred. Our concierge architecture has isolated the state to protect your session.'}
              </p>
              {this.state.error && (
                <div className="bg-luxury-elevated border border-luxury-border p-3 rounded-sm text-left text-[11px] font-mono text-rose-300 overflow-x-auto">
                  {this.state.error.message}
                </div>
              )}
            </div>

            <div className="flex flex-col sm:flex-row items-center justify-center gap-3 pt-2">
              <Button
                variant="gold"
                size="sm"
                onClick={this.handleReset}
                leftIcon={<RotateCcw size={14} />}
                className="w-full sm:w-auto"
              >
                Reload Component
              </Button>
              <Button
                variant="outline"
                size="sm"
                onClick={this.handleGoHome}
                leftIcon={<Home size={14} />}
                className="w-full sm:w-auto"
              >
                Return to Sanctuary
              </Button>
            </div>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}
