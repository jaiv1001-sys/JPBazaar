import React from 'react';
import { Link } from 'react-router-dom';
import { Button } from '../../components/common/Button';
import { ArrowRight, Sparkles, Compass, ShieldCheck } from 'lucide-react';

export const HomePage: React.FC = () => {
  return (
    <div className="flex flex-col">
      {/* Hero Section */}
      <section className="relative min-h-[85vh] flex items-center justify-center overflow-hidden border-b border-luxury-border">
        {/* Ambient Glow & Radial Background */}
        <div className="absolute inset-0 bg-[radial-gradient(ellipse_80%_60%_at_50%_-20%,rgba(212,175,55,0.15),rgba(8,8,8,0))]" />
        <div className="absolute inset-0 bg-[radial-gradient(circle_at_bottom_left,rgba(212,175,55,0.05),transparent_50%)]" />

        <div className="relative max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-24 text-center z-10">
          <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-luxury-gold/10 border border-luxury-gold/30 text-luxury-gold text-xs font-semibold uppercase tracking-widest mb-6">
            <Sparkles size={14} />
            <span>The Premier Boutique • Winter Edition</span>
          </div>

          <h1 className="font-serif text-4xl sm:text-6xl md:text-7xl font-bold tracking-tight text-luxury-white leading-tight">
            Curated Opulence. <br />
            <span className="text-luxury-gold italic">Engineered Precision.</span>
          </h1>

          <p className="mt-6 text-base sm:text-lg text-luxury-muted max-w-2xl mx-auto font-sans leading-relaxed">
            Welcome to JPBazaar, where high-performance computing, audiophile acoustics, and high fashion converge under an enterprise Spring Boot & PostgreSQL core.
          </p>

          <div className="mt-10 flex flex-wrap items-center justify-center gap-4">
            <Link to="/products">
              <Button variant="gold" size="lg" className="min-w-[180px]">
                Explore Catalog
                <ArrowRight size={18} className="ml-2" />
              </Button>
            </Link>
            <Link to="/auth">
              <Button variant="outline" size="lg" className="min-w-[180px]">
                Private Client Sign In
              </Button>
            </Link>
          </div>
        </div>
      </section>

      {/* Featured Categories Teaser */}
      <section className="py-20 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto w-full">
        <div className="flex flex-col md:flex-row items-baseline justify-between mb-12 border-b border-luxury-border pb-4">
          <div>
            <span className="text-xs font-semibold tracking-luxury-widest uppercase text-luxury-gold">Atelier Categories</span>
            <h2 className="font-serif text-3xl font-bold text-luxury-white mt-1">Exceptional Departments</h2>
          </div>
          <Link to="/products" className="text-luxury-gold hover:text-luxury-gold-light text-sm font-semibold flex items-center gap-1 group mt-2 md:mt-0">
            View All Collections <ArrowRight size={16} className="group-hover:translate-x-1 transition-transform" />
          </Link>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {[
            {
              title: 'Luxury Audio & Studio',
              subtitle: 'Acoustic fidelity crafted with hand-selected titanium and beryllium drivers.',
              icon: Compass,
              category: 'Audio',
            },
            {
              title: 'Precision Computing',
              subtitle: 'Liquid-cooled enterprise grade workstations and bespoke carbon fiber laptops.',
              icon: Sparkles,
              category: 'Computing',
            },
            {
              title: 'Horology & Bespoke',
              subtitle: 'Chronometers and tailored garments manufactured in legendary European houses.',
              icon: ShieldCheck,
              category: 'Horology',
            },
          ].map((cat, idx) => {
            const Icon = cat.icon;
            return (
              <div
                key={idx}
                className="group relative bg-luxury-surface border border-luxury-border p-8 rounded-sm hover:border-luxury-gold/50 transition-all duration-300 hover:shadow-gold-sm"
              >
                <div className="w-12 h-12 rounded-sm bg-luxury-elevated border border-luxury-border flex items-center justify-center text-luxury-gold mb-6 group-hover:scale-110 transition-transform">
                  <Icon size={24} />
                </div>
                <h3 className="font-serif text-xl font-bold text-luxury-white mb-2">{cat.title}</h3>
                <p className="text-sm text-luxury-muted mb-6 leading-relaxed">{cat.subtitle}</p>
                <Link
                  to={`/products?category=${encodeURIComponent(cat.category)}`}
                  className="inline-flex items-center text-xs font-semibold uppercase tracking-widest text-luxury-gold group-hover:text-luxury-gold-light"
                >
                  Discover Collection <ArrowRight size={14} className="ml-1" />
                </Link>
              </div>
            );
          })}
        </div>
      </section>
    </div>
  );
};
