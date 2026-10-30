import React from 'react';
import { useParams, Link } from 'react-router-dom';
import { Button } from '../../components/common/Button';
import { ArrowLeft, Sparkles } from 'lucide-react';

export const ProductDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 w-full">
      <Link to="/products" className="inline-flex items-center text-xs font-semibold tracking-wider uppercase text-luxury-gold hover:text-luxury-gold-light mb-8">
        <ArrowLeft size={16} className="mr-2" /> Back to Collections
      </Link>

      <div className="bg-luxury-surface border border-luxury-border p-12 rounded-sm text-center">
        <div className="w-16 h-16 rounded-full bg-luxury-elevated border border-luxury-border flex items-center justify-center mx-auto text-luxury-gold mb-4">
          <Sparkles size={28} />
        </div>
        <h2 className="font-serif text-2xl font-bold text-luxury-white">Product Artifact #{id}</h2>
        <p className="text-sm text-luxury-muted max-w-md mx-auto mt-2 mb-6">
          Product detail view initialized with multi-angle gallery, inventory stock badges, and customer reviews coming in Phase D.
        </p>
        <Link to="/products">
          <Button variant="gold" size="sm">Explore Other Artifacts</Button>
        </Link>
      </div>
    </div>
  );
};
