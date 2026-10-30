import React from 'react';
import { Link } from 'react-router-dom';
import { Button } from '../../components/common/Button';
import { ShieldCheck, BarChart3, Package, Users, Layers } from 'lucide-react';

export const AdminDashboardPage: React.FC = () => {
  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 w-full">
      <div className="border-b border-luxury-border pb-6 mb-8 flex flex-col sm:flex-row sm:items-end justify-between gap-4">
        <div>
          <span className="text-xs font-semibold tracking-luxury-widest uppercase text-luxury-gold">Executive Command</span>
          <h1 className="font-serif text-3xl sm:text-4xl font-bold text-luxury-white mt-1">Boutique Administration</h1>
        </div>
        <Link to="/">
          <Button variant="outline" size="sm">Back to Storefront</Button>
        </Link>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mb-12">
        {[
          { label: 'Total Revenue', value: '$124,500', icon: BarChart3 },
          { label: 'Active Catalog', value: '10 Artifacts', icon: Package },
          { label: 'Client Accounts', value: '2 Verified', icon: Users },
          { label: 'Categories', value: '4 Curations', icon: Layers },
        ].map((stat, idx) => {
          const Icon = stat.icon;
          return (
            <div key={idx} className="bg-luxury-surface border border-luxury-border p-6 rounded-sm">
              <div className="flex items-center justify-between text-luxury-muted mb-3">
                <span className="text-xs uppercase font-semibold tracking-wider">{stat.label}</span>
                <Icon size={18} className="text-luxury-gold" />
              </div>
              <div className="text-2xl font-serif font-bold text-luxury-white">{stat.value}</div>
            </div>
          );
        })}
      </div>

      <div className="bg-luxury-surface border border-luxury-border p-12 rounded-sm text-center">
        <div className="w-16 h-16 rounded-full bg-luxury-elevated border border-luxury-border flex items-center justify-center mx-auto text-luxury-gold mb-4">
          <ShieldCheck size={28} />
        </div>
        <h3 className="font-serif text-xl font-bold text-luxury-white">Executive Hub Architecture Online</h3>
        <p className="text-sm text-luxury-muted max-w-lg mx-auto mt-2 mb-6">
          Admin route security guard verified. Product CRUD, category management, order fulfillment state machine, and real-time dashboard analytics will be rendered in Phase F.
        </p>
        <Link to="/products">
          <Button variant="gold" size="sm">Inspect Current Boutique</Button>
        </Link>
      </div>
    </div>
  );
};
