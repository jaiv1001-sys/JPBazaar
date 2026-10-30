import React from 'react';
import { Link } from 'react-router-dom';
import { ShieldCheck, Truck, RefreshCw, Award } from 'lucide-react';

export const Footer: React.FC = () => {
  return (
    <footer className="bg-luxury-black border-t border-luxury-border text-luxury-muted">
      {/* Brand Value Pillars */}
      <div className="border-b border-luxury-border py-12 px-6">
        <div className="max-w-7xl mx-auto grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8">
          <div className="flex items-start gap-4">
            <div className="w-10 h-10 rounded-sm bg-luxury-surface border border-luxury-border flex items-center justify-center text-luxury-gold shrink-0">
              <Truck size={20} />
            </div>
            <div>
              <h4 className="text-sm font-bold text-luxury-white uppercase tracking-wider">Insured Express Shipping</h4>
              <p className="text-xs text-luxury-subtle mt-1">Complimentary white-glove transport on all orders over $150.</p>
            </div>
          </div>

          <div className="flex items-start gap-4">
            <div className="w-10 h-10 rounded-sm bg-luxury-surface border border-luxury-border flex items-center justify-center text-luxury-gold shrink-0">
              <Award size={20} />
            </div>
            <div>
              <h4 className="text-sm font-bold text-luxury-white uppercase tracking-wider">Certified Authenticity</h4>
              <p className="text-xs text-luxury-subtle mt-1">Every artifact is verified and inspected by certified specialists.</p>
            </div>
          </div>

          <div className="flex items-start gap-4">
            <div className="w-10 h-10 rounded-sm bg-luxury-surface border border-luxury-border flex items-center justify-center text-luxury-gold shrink-0">
              <ShieldCheck size={20} />
            </div>
            <div>
              <h4 className="text-sm font-bold text-luxury-white uppercase tracking-wider">Encrypted Settlement</h4>
              <p className="text-xs text-luxury-subtle mt-1">End-to-end simulated cryptographic checkout protocols.</p>
            </div>
          </div>

          <div className="flex items-start gap-4">
            <div className="w-10 h-10 rounded-sm bg-luxury-surface border border-luxury-border flex items-center justify-center text-luxury-gold shrink-0">
              <RefreshCw size={20} />
            </div>
            <div>
              <h4 className="text-sm font-bold text-luxury-white uppercase tracking-wider">Bespoke Concierge</h4>
              <p className="text-xs text-luxury-subtle mt-1">Dedicated customer advisory available around the clock.</p>
            </div>
          </div>
        </div>
      </div>

      {/* Main Footer Links */}
      <div className="max-w-7xl mx-auto px-6 py-16 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-12">
        {/* Brand Column */}
        <div className="lg:col-span-2 space-y-4">
          <Link to="/" className="text-2xl font-serif font-black tracking-wider text-luxury-white">
            JP<span className="text-luxury-gold">BAZAAR</span>
          </Link>
          <p className="text-xs text-luxury-subtle leading-relaxed max-w-sm">
            Curated luxury marketplace offering haute horlogerie, precision acoustic devices, and exceptional lifestyle essentials engineered on Spring Boot 3 &amp; Java 21.
          </p>
          <div className="pt-2">
            <p className="text-[11px] uppercase tracking-luxury-widest text-luxury-gold font-bold">
              Engineering Architecture
            </p>
            <p className="text-xs text-luxury-muted mt-1">
              Java 21 LTS • Spring Boot 3.3.4 • PostgreSQL 16 • Redis 7 • React 18
            </p>
          </div>
        </div>

        {/* Collections Column */}
        <div className="space-y-3">
          <h5 className="text-xs font-bold uppercase tracking-luxury-widest text-luxury-white">Collections</h5>
          <ul className="space-y-2 text-xs">
            <li><Link to="/products?category=electronics" className="hover:text-luxury-gold transition-colors">Computing Vault</Link></li>
            <li><Link to="/products?category=audio" className="hover:text-luxury-gold transition-colors">Acoustics &amp; Wearables</Link></li>
            <li><Link to="/products?category=fashion" className="hover:text-luxury-gold transition-colors">Haute Apparel</Link></li>
            <li><Link to="/products?category=home" className="hover:text-luxury-gold transition-colors">Maison Essentials</Link></li>
            <li><Link to="/products" className="hover:text-luxury-gold transition-colors">All New Arrivals</Link></li>
          </ul>
        </div>

        {/* Client Services Column */}
        <div className="space-y-3">
          <h5 className="text-xs font-bold uppercase tracking-luxury-widest text-luxury-white">Client Care</h5>
          <ul className="space-y-2 text-xs">
            <li><Link to="/account/orders" className="hover:text-luxury-gold transition-colors">Track Order Lifecycle</Link></li>
            <li><Link to="/account" className="hover:text-luxury-gold transition-colors">Client Profile &amp; Addresses</Link></li>
            <li><Link to="/wishlist" className="hover:text-luxury-gold transition-colors">Saved Curator Wishlist</Link></li>
            <li><a href="/swagger-ui/index.html" target="_blank" rel="noreferrer" className="hover:text-luxury-gold transition-colors">OpenAPI / Swagger Spec ↗</a></li>
            <li><a href="/actuator/health" target="_blank" rel="noreferrer" className="hover:text-luxury-gold transition-colors">Actuator Health Status ↗</a></li>
          </ul>
        </div>

        {/* Atelier Newsletter */}
        <div className="space-y-3">
          <h5 className="text-xs font-bold uppercase tracking-luxury-widest text-luxury-white">Private Atelier</h5>
          <p className="text-xs text-luxury-subtle">
            Receive private preview invitations for limited production runs.
          </p>
          <div className="flex gap-2">
            <input
              type="email"
              placeholder="client@luxury.com"
              className="bg-luxury-surface border border-luxury-border rounded-sm px-3 py-1.5 text-xs text-luxury-white placeholder:text-luxury-subtle w-full focus:outline-none focus:border-luxury-gold"
            />
            <button className="bg-luxury-gold text-luxury-black px-3 py-1.5 rounded-sm text-xs font-bold uppercase hover:bg-luxury-gold-light transition-colors shrink-0">
              Join
            </button>
          </div>
        </div>
      </div>

      {/* Bottom Legal Bar */}
      <div className="border-t border-luxury-border py-6 px-6 text-center text-xs text-luxury-subtle">
        <p>© {new Date().getFullYear()} JPBazaar Atelier. All rights reserved. Production E-Commerce Platform.</p>
      </div>
    </footer>
  );
};
