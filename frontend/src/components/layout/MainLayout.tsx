import React from 'react';
import { Outlet } from 'react-router-dom';
import { Navbar } from './Navbar';
import { Footer } from './Footer';
import { CartDrawer } from './CartDrawer';
import { ErrorBoundary } from '../common/ErrorBoundary';

export const MainLayout: React.FC = () => {
  return (
    <div className="min-h-screen bg-luxury-black text-luxury-white flex flex-col selection:bg-luxury-gold selection:text-luxury-black">
      <Navbar />
      <main className="flex-1 flex flex-col">
        <ErrorBoundary>
          <Outlet />
        </ErrorBoundary>
      </main>
      <Footer />
      <CartDrawer />
    </div>
  );
};
