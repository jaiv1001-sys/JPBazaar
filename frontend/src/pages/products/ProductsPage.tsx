import React from 'react';
import { Link } from 'react-router-dom';
import { Button } from '../../components/common/Button';
import { SlidersHorizontal, Grid, Search } from 'lucide-react';

export const ProductsPage: React.FC = () => {
  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 w-full">
      <div className="flex flex-col md:flex-row md:items-end justify-between border-b border-luxury-border pb-6 gap-4">
        <div>
          <span className="text-xs font-semibold tracking-luxury-widest uppercase text-luxury-gold">Curated Inventory</span>
          <h1 className="font-serif text-3xl sm:text-4xl font-bold text-luxury-white mt-1">Catalog of Distinction</h1>
        </div>
        <div className="flex items-center gap-3">
          <Button variant="outline" size="sm" className="flex items-center gap-2">
            <SlidersHorizontal size={16} /> Filters
          </Button>
          <Button variant="outline" size="sm" className="flex items-center gap-2">
            <Grid size={16} /> View
          </Button>
        </div>
      </div>

      <div className="py-16 text-center text-luxury-muted">
        <div className="w-16 h-16 rounded-full bg-luxury-surface border border-luxury-border flex items-center justify-center mx-auto text-luxury-gold mb-4">
          <Search size={28} />
        </div>
        <h3 className="font-serif text-xl font-bold text-luxury-white">Catalog Foundation Ready</h3>
        <p className="text-sm max-w-md mx-auto mt-2 mb-6">
          Phase C routing and layout verified. Rich faceted filtering, Spring Boot Pageable pagination, and instant search integration will activate in Phase D.
        </p>
        <Link to="/">
          <Button variant="outline" size="sm">Return to Sanctuary</Button>
        </Link>
      </div>
    </div>
  );
};
