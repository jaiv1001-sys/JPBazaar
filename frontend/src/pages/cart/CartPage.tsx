import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useCart } from '../../context/CartContext';
import { Button } from '../../components/common/Button';
import { ShoppingBag, ArrowRight } from 'lucide-react';

export const CartPage: React.FC = () => {
  const { totalCount, totalAmount } = useCart();
  const navigate = useNavigate();

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 w-full">
      <div className="border-b border-luxury-border pb-6 mb-8">
        <span className="text-xs font-semibold tracking-luxury-widest uppercase text-luxury-gold">Personal Acquisition</span>
        <h1 className="font-serif text-3xl sm:text-4xl font-bold text-luxury-white mt-1">Shopping Bag</h1>
      </div>

      <div className="bg-luxury-surface border border-luxury-border p-12 rounded-sm text-center">
        <div className="w-16 h-16 rounded-full bg-luxury-elevated border border-luxury-border flex items-center justify-center mx-auto text-luxury-gold mb-4">
          <ShoppingBag size={28} />
        </div>
        <h3 className="font-serif text-xl font-bold text-luxury-white">
          {totalCount > 0 ? `${totalCount} Artifacts Reserved` : 'Your Bag is Currently Empty'}
        </h3>
        <p className="text-sm text-luxury-muted max-w-md mx-auto mt-2 mb-6">
          {totalCount > 0
            ? `Estimated total: $${totalAmount.toFixed(2)}. Proceed to secure checkout or continue reviewing exclusive collections.`
            : 'Add exquisite artifacts from our catalog to review and prepare for checkout.'}
        </p>
        <div className="flex justify-center gap-4">
          <Link to="/products">
            <Button variant="outline" size="sm">Browse Boutique</Button>
          </Link>
          {totalCount > 0 && (
            <Button variant="gold" size="sm" onClick={() => navigate('/checkout')}>
              Proceed to Checkout <ArrowRight size={16} className="ml-2" />
            </Button>
          )}
        </div>
      </div>
    </div>
  );
};
