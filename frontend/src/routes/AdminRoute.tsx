import React from 'react';
import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Skeleton } from '../components/common/Skeleton';
import { ShieldAlert } from 'lucide-react';
import { Button } from '../components/common/Button';

interface AdminRouteProps {
  children?: React.ReactNode;
}

export const AdminRoute: React.FC<AdminRouteProps> = ({ children }) => {
  const { isAuthenticated, isAdmin, isLoading } = useAuth();
  const location = useLocation();

  if (isLoading) {
    return (
      <div className="min-h-[60vh] flex flex-col items-center justify-center p-8 space-y-4">
        <div className="w-12 h-12 rounded-full border-2 border-luxury-gold/20 border-t-luxury-gold animate-spin" />
        <Skeleton variant="text" className="w-40 h-4" />
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to={`/auth?redirect=${encodeURIComponent(location.pathname + location.search)}`} replace />;
  }

  if (!isAdmin) {
    return (
      <div className="min-h-[60vh] flex flex-col items-center justify-center text-center p-8">
        <div className="w-16 h-16 rounded-full bg-rose-500/10 border border-rose-500/30 flex items-center justify-center text-rose-400 mb-4">
          <ShieldAlert size={32} />
        </div>
        <h2 className="font-serif text-2xl font-bold text-luxury-white">Restricted Clearance</h2>
        <p className="text-luxury-muted text-sm max-w-md mt-2 mb-6">
          You do not possess the required administrator privileges to access the JPBazaar Executive Management Portal.
        </p>
        <Button variant="outline" onClick={() => window.location.href = '/'}>
          Return to Boutique
        </Button>
      </div>
    );
  }

  return children ? <>{children}</> : <Outlet />;
};
