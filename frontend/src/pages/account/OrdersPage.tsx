import React from 'react';
import { Link } from 'react-router-dom';
import { Button } from '../../components/common/Button';
import { Package, ArrowLeft } from 'lucide-react';

export const OrdersPage: React.FC = () => {
  return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-12 w-full">
      <Link to="/account" className="inline-flex items-center text-xs font-semibold tracking-wider uppercase text-luxury-gold hover:text-luxury-gold-light mb-8">
        <ArrowLeft size={16} className="mr-2" /> Back to Profile
      </Link>

      <div className="border-b border-luxury-border pb-6 mb-8">
        <span className="text-xs font-semibold tracking-luxury-widest uppercase text-luxury-gold">Consignment Archive</span>
        <h1 className="font-serif text-3xl font-bold text-luxury-white mt-1">Orders & Acquisitions</h1>
      </div>

      <div className="bg-luxury-surface border border-luxury-border p-12 rounded-sm text-center">
        <div className="w-16 h-16 rounded-full bg-luxury-elevated border border-luxury-border flex items-center justify-center mx-auto text-luxury-gold mb-4">
          <Package size={28} />
        </div>
        <h3 className="font-serif text-xl font-bold text-luxury-white">Order Management Foundation Active</h3>
        <p className="text-sm text-luxury-muted max-w-md mx-auto mt-2 mb-6">
          Order history, payment state reconciliation, and real-time tracking will be fully connected to the backend Order API in Phase E.
        </p>
        <Link to="/products">
          <Button variant="gold" size="sm">Explore Collections</Button>
        </Link>
      </div>
    </div>
  );
};
