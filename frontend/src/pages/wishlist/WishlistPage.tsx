import React from 'react';
import { Link } from 'react-router-dom';
import { Button } from '../../components/common/Button';
import { Heart } from 'lucide-react';

export const WishlistPage: React.FC = () => {
  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 w-full">
      <div className="border-b border-luxury-border pb-6 mb-8">
        <span className="text-xs font-semibold tracking-luxury-widest uppercase text-luxury-gold">Curated Desires</span>
        <h1 className="font-serif text-3xl sm:text-4xl font-bold text-luxury-white mt-1">Personal Wishlist</h1>
      </div>

      <div className="bg-luxury-surface border border-luxury-border p-12 rounded-sm text-center">
        <div className="w-16 h-16 rounded-full bg-luxury-elevated border border-luxury-border flex items-center justify-center mx-auto text-luxury-gold mb-4">
          <Heart size={28} />
        </div>
        <h3 className="font-serif text-xl font-bold text-luxury-white">Saved Artifacts</h3>
        <p className="text-sm text-luxury-muted max-w-md mx-auto mt-2 mb-6">
          Your saved items are synced securely to the backend Spring Boot Wishlist service.
        </p>
        <Link to="/products">
          <Button variant="gold" size="sm">Discover More Artifacts</Button>
        </Link>
      </div>
    </div>
  );
};
