import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { Button } from '../../components/common/Button';
import { Card } from '../../components/common/Card';
import { User as UserIcon, Mail, Phone, ShieldCheck, Package, MapPin, LogOut } from 'lucide-react';

export const AccountPage: React.FC = () => {
  const { user, logout, isAdmin } = useAuth();

  return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-12 w-full">
      <div className="border-b border-luxury-border pb-6 mb-8 flex flex-col sm:flex-row sm:items-end justify-between gap-4">
        <div>
          <span className="text-xs font-semibold tracking-luxury-widest uppercase text-luxury-gold">Private Concierge</span>
          <h1 className="font-serif text-3xl sm:text-4xl font-bold text-luxury-white mt-1">Client Profile</h1>
        </div>
        <div className="flex items-center gap-3">
          {isAdmin && (
            <Link to="/admin">
              <Button variant="outline" size="sm" className="flex items-center gap-2 border-luxury-gold/50 text-luxury-gold">
                <ShieldCheck size={16} /> Admin Dashboard
              </Button>
            </Link>
          )}
          <Button variant="ghost" size="sm" onClick={logout} className="flex items-center gap-2 text-rose-400 hover:text-rose-300">
            <LogOut size={16} /> Sign Out
          </Button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* User Card */}
        <Card variant="elevated" className="p-6">
          <div className="flex items-center gap-4 mb-6">
            <div className="w-14 h-14 rounded-full bg-luxury-gold/10 border border-luxury-gold/30 flex items-center justify-center text-luxury-gold text-xl font-serif font-bold">
              {user?.firstName?.charAt(0) || 'U'}
            </div>
            <div>
              <h3 className="font-serif text-lg font-bold text-luxury-white">
                {user?.firstName} {user?.lastName}
              </h3>
              <span className="inline-block px-2 py-0.5 mt-1 rounded text-[10px] uppercase font-bold tracking-wider bg-luxury-surface border border-luxury-border text-luxury-gold">
                {user?.role || 'CUSTOMER'}
              </span>
            </div>
          </div>

          <div className="space-y-4 pt-4 border-t border-luxury-border text-sm">
            <div className="flex items-center gap-3 text-luxury-muted">
              <Mail size={16} className="text-luxury-gold shrink-0" />
              <span className="truncate">{user?.email}</span>
            </div>
            {user?.phone && (
              <div className="flex items-center gap-3 text-luxury-muted">
                <Phone size={16} className="text-luxury-gold shrink-0" />
                <span>{user.phone}</span>
              </div>
            )}
            <div className="flex items-center gap-3 text-luxury-muted">
              <UserIcon size={16} className="text-luxury-gold shrink-0" />
              <span>Client ID #{user?.id}</span>
            </div>
          </div>
        </Card>

        {/* Quick Actions & Navigation */}
        <div className="lg:col-span-2 grid grid-cols-1 sm:grid-cols-2 gap-4">
          <Link to="/account/orders" className="group">
            <Card variant="surface" className="p-6 h-full hover:border-luxury-gold/50 transition-all">
              <div className="w-10 h-10 rounded-sm bg-luxury-surface border border-luxury-border flex items-center justify-center text-luxury-gold mb-4 group-hover:scale-110 transition-transform">
                <Package size={20} />
              </div>
              <h4 className="font-serif text-base font-bold text-luxury-white group-hover:text-luxury-gold transition-colors">
                Acquisition History
              </h4>
              <p className="text-xs text-luxury-muted mt-1">
                Track your active orders, consignments, and inspect invoices.
              </p>
            </Card>
          </Link>

          <Link to="/products" className="group">
            <Card variant="surface" className="p-6 h-full hover:border-luxury-gold/50 transition-all">
              <div className="w-10 h-10 rounded-sm bg-luxury-surface border border-luxury-border flex items-center justify-center text-luxury-gold mb-4 group-hover:scale-110 transition-transform">
                <MapPin size={20} />
              </div>
              <h4 className="font-serif text-base font-bold text-luxury-white group-hover:text-luxury-gold transition-colors">
                Delivery Residences
              </h4>
              <p className="text-xs text-luxury-muted mt-1">
                Manage registered dispatch destinations and diplomatic suites.
              </p>
            </Card>
          </Link>
        </div>
      </div>
    </div>
  );
};
