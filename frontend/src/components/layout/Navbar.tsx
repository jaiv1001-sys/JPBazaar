import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useCart } from '../../context/CartContext';
import { Button } from '../common/Button';
import {
  ShoppingBag,
  Heart,
  User as UserIcon,
  Search,
  Menu,
  X,
  Shield,
  LogOut,
  ChevronDown,
} from 'lucide-react';

export const Navbar: React.FC = () => {
  const { user, isAuthenticated, isAdmin, logout } = useAuth();
  const { totalCount, openDrawer } = useCart();
  const navigate = useNavigate();

  const [searchQuery, setSearchQuery] = useState('');
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const [isUserMenuOpen, setIsUserMenuOpen] = useState(false);

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      navigate(`/products?search=${encodeURIComponent(searchQuery.trim())}`);
      setIsMobileMenuOpen(false);
    }
  };

  return (
    <header className="sticky top-0 z-40 w-full">
      {/* Top Luxury Announcement Bar */}
      <div className="bg-luxury-secondary border-b border-luxury-border py-1.5 px-4 text-center">
        <p className="text-[11px] sm:text-xs tracking-luxury-widest uppercase font-semibold text-luxury-muted">
          Complimentary Insured Delivery Worldwide Over $150 • Spring Boot 3 Engine
        </p>
      </div>

      {/* Primary Navigation Bar */}
      <nav className="glass-dark border-b border-luxury-border">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-20 flex items-center justify-between gap-6">
          {/* Mobile Menu Button */}
          <button
            onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
            className="lg:hidden text-luxury-muted hover:text-luxury-white p-2"
            aria-label="Toggle navigation"
          >
            {isMobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
          </button>

          {/* Luxury Brand Logo */}
          <Link to="/" className="flex items-center gap-2 group">
            <span className="text-xl sm:text-2xl font-serif font-black tracking-wider text-luxury-white group-hover:text-luxury-gold transition-colors">
              JP<span className="text-luxury-gold">BAZAAR</span>
            </span>
            <span className="hidden sm:inline-block text-[9px] font-bold px-1.5 py-0.5 rounded-sm bg-luxury-gold/10 text-luxury-gold border border-luxury-gold/30 tracking-widest uppercase">
              ATELIER
            </span>
          </Link>

          {/* Desktop Navigation Links */}
          <div className="hidden lg:flex items-center gap-8 text-xs font-semibold tracking-luxury-wide uppercase">
            <Link to="/products" className="text-luxury-muted hover:text-luxury-gold transition-colors">
              Collections
            </Link>
            <Link to="/products?category=electronics" className="text-luxury-muted hover:text-luxury-gold transition-colors">
              Computing
            </Link>
            <Link to="/products?category=audio" className="text-luxury-muted hover:text-luxury-gold transition-colors">
              Acoustics
            </Link>
            <Link to="/products?category=fashion" className="text-luxury-muted hover:text-luxury-gold transition-colors">
              Haute Apparel
            </Link>
            <Link to="/products?category=home" className="text-luxury-muted hover:text-luxury-gold transition-colors">
              Maison
            </Link>
          </div>

          {/* Search Input */}
          <form onSubmit={handleSearchSubmit} className="hidden md:flex flex-1 max-w-xs relative items-center">
            <Search size={14} className="absolute left-3 text-luxury-subtle pointer-events-none" />
            <input
              type="text"
              placeholder="Search collections..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full bg-luxury-surface/80 border border-luxury-border rounded-sm pl-9 pr-4 py-1.5 text-xs text-luxury-white placeholder:text-luxury-subtle focus:outline-none focus:border-luxury-gold focus:ring-1 focus:ring-luxury-gold transition-all"
            />
          </form>

          {/* Right Action Controls */}
          <div className="flex items-center gap-3 sm:gap-4">
            {/* Admin Portal Indicator */}
            {isAdmin && (
              <Link to="/admin">
                <Button variant="outline" size="sm" leftIcon={<Shield size={14} />} className="hidden sm:inline-flex">
                  Admin
                </Button>
              </Link>
            )}

            {/* Wishlist */}
            <Link
              to="/wishlist"
              className="relative p-2 text-luxury-muted hover:text-luxury-gold transition-colors"
              title="Saved Wishlist"
            >
              <Heart size={20} />
            </Link>

            {/* Shopping Cart Drawer Trigger */}
            <button
              onClick={openDrawer}
              className="relative p-2 text-luxury-muted hover:text-luxury-gold transition-colors flex items-center"
              title="Shopping Cart"
            >
              <ShoppingBag size={20} />
              {totalCount > 0 && (
                <span className="absolute -top-1 -right-1 bg-luxury-gold text-luxury-black text-[10px] font-black w-4 h-4 rounded-full flex items-center justify-center shadow-gold-glow animate-fade-in">
                  {totalCount}
                </span>
              )}
            </button>

            {/* User Account Menu */}
            {isAuthenticated ? (
              <div className="relative">
                <button
                  onClick={() => setIsUserMenuOpen(!isUserMenuOpen)}
                  className="flex items-center gap-2 p-1.5 rounded-sm hover:bg-luxury-elevated transition-colors text-xs font-semibold tracking-wide text-luxury-white"
                >
                  <div className="w-7 h-7 rounded-full bg-luxury-gold/20 border border-luxury-gold/40 flex items-center justify-center text-luxury-gold font-bold">
                    {user?.firstName?.[0] || 'U'}
                  </div>
                  <span className="hidden sm:inline-block">{user?.firstName}</span>
                  <ChevronDown size={14} className="text-luxury-muted" />
                </button>

                {/* Dropdown Menu */}
                {isUserMenuOpen && (
                  <div
                    className="absolute right-0 mt-2 w-48 bg-luxury-surface border border-luxury-border rounded-sm shadow-luxury-lg py-2 z-50 animate-slide-up"
                    onClick={() => setIsUserMenuOpen(false)}
                  >
                    <div className="px-4 py-2 border-b border-luxury-border">
                      <p className="text-xs font-bold text-luxury-white">{user?.firstName} {user?.lastName}</p>
                      <p className="text-[11px] text-luxury-muted truncate">{user?.email}</p>
                    </div>

                    <Link
                      to="/account"
                      className="flex items-center gap-2 px-4 py-2 text-xs text-luxury-muted hover:text-luxury-white hover:bg-luxury-elevated transition-colors"
                    >
                      <UserIcon size={14} /> My Profile &amp; Addresses
                    </Link>

                    <Link
                      to="/account/orders"
                      className="flex items-center gap-2 px-4 py-2 text-xs text-luxury-muted hover:text-luxury-white hover:bg-luxury-elevated transition-colors"
                    >
                      <ShoppingBag size={14} /> My Orders &amp; Tracking
                    </Link>

                    {isAdmin && (
                      <Link
                        to="/admin"
                        className="flex items-center gap-2 px-4 py-2 text-xs text-luxury-gold hover:bg-luxury-gold/10 transition-colors font-semibold"
                      >
                        <Shield size={14} /> Admin Dashboard
                      </Link>
                    )}

                    <button
                      onClick={logout}
                      className="w-full text-left flex items-center gap-2 px-4 py-2 text-xs text-luxury-danger hover:bg-red-950/20 transition-colors border-t border-luxury-border mt-1"
                    >
                      <LogOut size={14} /> Sign Out
                    </button>
                  </div>
                )}
              </div>
            ) : (
              <Link to="/auth">
                <Button variant="gold" size="sm">
                  Sign In
                </Button>
              </Link>
            )}
          </div>
        </div>

        {/* Mobile Navigation Drawer */}
        {isMobileMenuOpen && (
          <div className="lg:hidden border-t border-luxury-border bg-luxury-black px-6 py-6 space-y-6 animate-slide-down">
            <form onSubmit={handleSearchSubmit} className="relative">
              <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-luxury-subtle pointer-events-none" />
              <input
                type="text"
                placeholder="Search collections..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full bg-luxury-surface border border-luxury-border rounded-sm pl-10 pr-4 py-2 text-sm text-luxury-white placeholder:text-luxury-subtle focus:outline-none focus:border-luxury-gold"
              />
            </form>

            <div className="flex flex-col gap-4 text-sm font-semibold uppercase tracking-luxury-wide">
              <Link
                to="/products"
                onClick={() => setIsMobileMenuOpen(false)}
                className="text-luxury-muted hover:text-luxury-gold"
              >
                All Collections
              </Link>
              <Link
                to="/products?category=electronics"
                onClick={() => setIsMobileMenuOpen(false)}
                className="text-luxury-muted hover:text-luxury-gold"
              >
                High-Performance Computing
              </Link>
              <Link
                to="/products?category=audio"
                onClick={() => setIsMobileMenuOpen(false)}
                className="text-luxury-muted hover:text-luxury-gold"
              >
                Precision Acoustics
              </Link>
              <Link
                to="/products?category=fashion"
                onClick={() => setIsMobileMenuOpen(false)}
                className="text-luxury-muted hover:text-luxury-gold"
              >
                Haute Apparel &amp; Horlogerie
              </Link>
              <Link
                to="/products?category=home"
                onClick={() => setIsMobileMenuOpen(false)}
                className="text-luxury-muted hover:text-luxury-gold"
              >
                Maison &amp; Living
              </Link>
            </div>
          </div>
        )}
      </nav>
    </header>
  );
};
