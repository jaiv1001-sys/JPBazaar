import React from 'react';
import { Link } from 'react-router-dom';
import { Button } from '../../components/common/Button';
import { Lock, ArrowLeft } from 'lucide-react';

export const CheckoutPage: React.FC = () => {
  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-12 w-full">
      <Link to="/cart" className="inline-flex items-center text-xs font-semibold tracking-wider uppercase text-luxury-gold hover:text-luxury-gold-light mb-8">
        <ArrowLeft size={16} className="mr-2" /> Back to Bag
      </Link>

      <div className="border-b border-luxury-border pb-6 mb-8">
        <span className="text-xs font-semibold tracking-luxury-widest uppercase text-luxury-gold">Secure Settlement</span>
        <h1 className="font-serif text-3xl font-bold text-luxury-white mt-1">Checkout & Delivery Concierge</h1>
      </div>

      <div className="bg-luxury-surface border border-luxury-border p-12 rounded-sm text-center">
        <div className="w-16 h-16 rounded-full bg-luxury-elevated border border-luxury-border flex items-center justify-center mx-auto text-luxury-gold mb-4">
          <Lock size={28} />
        </div>
        <h3 className="font-serif text-xl font-bold text-luxury-white">Encrypted Checkout Pipeline Ready</h3>
        <p className="text-sm text-luxury-muted max-w-md mx-auto mt-2 mb-6">
          Multi-step address selection, payment gateway simulation (CARD, UPI, NET_BANKING, COD), and order generation activate in Phase E.
        </p>
        <Link to="/products">
          <Button variant="outline" size="sm">Continue Exploring</Button>
        </Link>
      </div>
    </div>
  );
};
