import React from 'react';
import { Link } from 'react-router-dom';
import { Button } from '../../components/common/Button';
import { Compass } from 'lucide-react';

export const NotFoundPage: React.FC = () => {
  return (
    <div className="min-h-[70vh] flex flex-col items-center justify-center text-center px-4 py-16">
      <div className="w-20 h-20 rounded-full bg-luxury-surface border border-luxury-border flex items-center justify-center text-luxury-gold mb-6">
        <Compass size={36} />
      </div>
      <span className="text-xs font-semibold tracking-luxury-widest uppercase text-luxury-gold">Error 404</span>
      <h1 className="font-serif text-3xl sm:text-4xl font-bold text-luxury-white mt-2 mb-3">
        Artifact Undiscovered
      </h1>
      <p className="text-luxury-muted text-sm max-w-md mb-8">
        The corridor or artifact you are seeking does not exist in our boutique or may have been archived.
      </p>
      <Link to="/">
        <Button variant="gold" size="md">
          Return to Sanctuary
        </Button>
      </Link>
    </div>
  );
};
