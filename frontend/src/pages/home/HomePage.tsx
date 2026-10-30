import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Product, Category } from '../../types';
import { productService } from '../../services/productService';
import { categoryService } from '../../services/categoryService';
import { ProductCard } from '../../components/product/ProductCard';
import { QuickViewModal } from '../../components/product/QuickViewModal';
import { Button } from '../../components/common/Button';
import { Input } from '../../components/common/Input';
import { Skeleton } from '../../components/common/Skeleton';
import { useToast } from '../../context/ToastContext';
import {
  ArrowRight,
  Sparkles,
  ShieldCheck,
  Truck,
  Award,
  Headphones,
  Mail,
  CheckCircle,
} from 'lucide-react';

export const HomePage: React.FC = () => {
  const { showToast } = useToast();

  const [products, setProducts] = useState<Product[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [isLoadingProducts, setIsLoadingProducts] = useState(true);
  const [isLoadingCategories, setIsLoadingCategories] = useState(true);

  // Quick View Modal State
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
  const [isQuickViewOpen, setIsQuickViewOpen] = useState(false);

  // Newsletter State
  const [newsletterEmail, setNewsletterEmail] = useState('');
  const [isSubscribing, setIsSubscribing] = useState(false);
  const [isSubscribed, setIsSubscribed] = useState(false);

  useEffect(() => {
    // 1. Fetch live products from backend
    const fetchProducts = async () => {
      try {
        const res = await productService.getProducts({ page: 0, size: 8, sortBy: 'createdAt', sortDir: 'desc' });
        setProducts(res.content || []);
      } catch (err: any) {
        console.error('Failed to load products for homepage:', err);
      } finally {
        setIsLoadingProducts(false);
      }
    };

    // 2. Fetch live categories from backend
    const fetchCategories = async () => {
      try {
        const res = await categoryService.getCategories();
        setCategories(res || []);
      } catch (err: any) {
        console.error('Failed to load categories for homepage:', err);
      } finally {
        setIsLoadingCategories(false);
      }
    };

    fetchProducts();
    fetchCategories();
  }, []);

  const handleQuickView = (product: Product) => {
    setSelectedProduct(product);
    setIsQuickViewOpen(true);
  };

  const handleNewsletterSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newsletterEmail || !newsletterEmail.includes('@')) {
      showToast('Please provide a valid private client email address.', 'error');
      return;
    }
    setIsSubscribing(true);
    setTimeout(() => {
      setIsSubscribing(false);
      setIsSubscribed(true);
      showToast('Invitation accepted. Welcome to the JPBazaar Private Salon.', 'success');
      setNewsletterEmail('');
    }, 700);
  };

  return (
    <div className="flex flex-col bg-luxury-black text-luxury-white">
      {/* 1. Grand Hero Section */}
      <section className="relative min-h-[85vh] flex items-center justify-center overflow-hidden border-b border-luxury-border">
        {/* Ambient Glows */}
        <div className="absolute inset-0 bg-[radial-gradient(ellipse_80%_60%_at_50%_-10%,rgba(212,175,55,0.18),rgba(8,8,8,0))]" />
        <div className="absolute inset-0 bg-[radial-gradient(circle_at_bottom_left,rgba(212,175,55,0.06),transparent_50%)]" />

        <div className="relative max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-24 sm:py-32 text-center z-10">
          <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-luxury-gold/10 border border-luxury-gold/30 text-luxury-gold text-xs font-semibold uppercase tracking-luxury-widest mb-8 animate-fade-in">
            <Sparkles size={14} />
            <span>The Premier Boutique • Haute Collection</span>
          </div>

          <h1 className="font-serif text-4xl sm:text-6xl md:text-7xl font-bold tracking-tight text-luxury-white leading-[1.1] mb-6">
            Beyond Shopping. <br />
            <span className="text-luxury-gold italic">Beyond Ordinary.</span>
          </h1>

          <p className="text-base sm:text-lg text-luxury-muted max-w-2xl mx-auto font-sans leading-relaxed mb-10">
            Discover a curated world of high-performance computing, audiophile fidelity, and timeless luxury, engineered with Spring Boot 3 resilience.
          </p>

          <div className="flex flex-wrap items-center justify-center gap-4">
            <Link to="/products">
              <Button variant="gold" size="lg" className="min-w-[200px]" rightIcon={<ArrowRight size={18} />}>
                Explore Collection
              </Button>
            </Link>
            <Link to="/auth">
              <Button variant="outline" size="lg" className="min-w-[200px]">
                Private Client Sign In
              </Button>
            </Link>
          </div>
        </div>
      </section>

      {/* 2. Live Category Showcase */}
      <section className="py-20 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto w-full">
        <div className="flex flex-col md:flex-row items-baseline justify-between mb-12 border-b border-luxury-border pb-4">
          <div>
            <span className="text-xs font-semibold tracking-luxury-widest uppercase text-luxury-gold">
              Curated Departments
            </span>
            <h2 className="font-serif text-3xl font-bold text-luxury-white mt-1">
              Atelier Collections
            </h2>
          </div>
          <Link
            to="/products"
            className="text-luxury-gold hover:text-luxury-gold-light text-xs font-semibold uppercase tracking-widest flex items-center gap-1 group mt-2 md:mt-0"
          >
            Explore Complete Catalog <ArrowRight size={14} className="group-hover:translate-x-1 transition-transform" />
          </Link>
        </div>

        {isLoadingCategories ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {[1, 2, 3, 4].map((i) => (
              <div key={i} className="bg-luxury-surface border border-luxury-border p-6 rounded-sm space-y-4">
                <Skeleton className="h-12 w-12 rounded-sm" />
                <Skeleton className="h-6 w-3/4" />
                <Skeleton className="h-12 w-full" />
              </div>
            ))}
          </div>
        ) : categories.length > 0 ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {categories.map((cat) => (
              <Link
                key={cat.id}
                to={`/products?categoryId=${cat.id}`}
                className="group relative bg-luxury-surface border border-luxury-border p-7 rounded-sm hover:border-luxury-gold/50 transition-all duration-300 hover:shadow-gold-sm flex flex-col justify-between"
              >
                <div>
                  <div className="w-12 h-12 rounded-sm bg-luxury-elevated border border-luxury-border flex items-center justify-center text-luxury-gold mb-6 group-hover:scale-110 group-hover:border-luxury-gold transition-all">
                    <Sparkles size={20} />
                  </div>
                  <h3 className="font-serif text-xl font-bold text-luxury-white group-hover:text-luxury-gold transition-colors">
                    {cat.name}
                  </h3>
                  <p className="text-xs text-luxury-muted mt-2 leading-relaxed">
                    {cat.description || `Explore our bespoke curated artifacts in the ${cat.name} vault.`}
                  </p>
                </div>
                <div className="pt-6 mt-6 border-t border-luxury-border/50 flex items-center text-xs font-semibold uppercase tracking-widest text-luxury-gold group-hover:text-luxury-gold-light">
                  <span>Enter Collection</span>
                  <ArrowRight size={14} className="ml-2 group-hover:translate-x-1 transition-transform" />
                </div>
              </Link>
            ))}
          </div>
        ) : (
          <div className="text-center py-12 text-luxury-muted text-sm">
            Categories initializing from database...
          </div>
        )}
      </section>

      {/* 3. Featured & New Arrivals Product Showcase */}
      <section className="py-16 bg-luxury-surface/40 border-y border-luxury-border">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex flex-col md:flex-row items-baseline justify-between mb-12 border-b border-luxury-border pb-4">
            <div>
              <span className="text-xs font-semibold tracking-luxury-widest uppercase text-luxury-gold">
                Premier Acquisitions
              </span>
              <h2 className="font-serif text-3xl font-bold text-luxury-white mt-1">
                Featured Artifacts &amp; Arrivals
              </h2>
            </div>
            <Link
              to="/products"
              className="text-luxury-gold hover:text-luxury-gold-light text-xs font-semibold uppercase tracking-widest flex items-center gap-1 group mt-2 md:mt-0"
            >
              Browse All ({products.length}) <ArrowRight size={14} className="group-hover:translate-x-1 transition-transform" />
            </Link>
          </div>

          {isLoadingProducts ? (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
              {[1, 2, 3, 4, 5, 6, 7, 8].map((n) => (
                <div key={n} className="bg-luxury-surface border border-luxury-border p-4 rounded-sm space-y-4">
                  <Skeleton className="aspect-square w-full rounded-sm" />
                  <Skeleton className="h-4 w-1/3" />
                  <Skeleton className="h-6 w-3/4" />
                  <Skeleton className="h-5 w-1/2" />
                </div>
              ))}
            </div>
          ) : products.length > 0 ? (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
              {products.map((product) => (
                <ProductCard
                  key={product.id}
                  product={product}
                  onQuickView={handleQuickView}
                />
              ))}
            </div>
          ) : (
            <div className="text-center py-16 bg-luxury-surface border border-luxury-border rounded-sm">
              <h4 className="font-serif text-lg font-bold text-luxury-white">Boutique Vault Initializing</h4>
              <p className="text-xs text-luxury-muted max-w-md mx-auto mt-2">
                Our backend data initializer automatically seeds precision artifacts upon initial launch.
              </p>
            </div>
          )}
        </div>
      </section>

      {/* 4. Luxury Brand Story / Editorial */}
      <section className="py-24 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto w-full">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 lg:gap-16 items-center">
          <div className="space-y-6">
            <span className="text-xs font-semibold tracking-luxury-widest uppercase text-luxury-gold">
              The JPBazaar Atelier
            </span>
            <h2 className="font-serif text-3xl sm:text-4xl lg:text-5xl font-bold text-luxury-white leading-tight">
              Where Rare Craftsmanship Meets Digital Precision
            </h2>
            <p className="text-sm text-luxury-muted leading-relaxed">
              Founded on the belief that everyday technology and fine horology should be celebrated as modern art, JPBazaar curates only verified, high-provenance artifacts.
            </p>
            <p className="text-sm text-luxury-muted leading-relaxed">
              Every order placed through our boutique is backed by distributed transactional integrity, multi-layered Redis rate-limiting, and simulated instant cryptographic clearance.
            </p>
            <div className="pt-4 flex items-center gap-6">
              <div>
                <span className="font-serif text-3xl font-bold text-luxury-gold block">100%</span>
                <span className="text-[11px] uppercase tracking-wider text-luxury-muted">Verified Provenance</span>
              </div>
              <div className="h-10 w-px bg-luxury-border" />
              <div>
                <span className="font-serif text-3xl font-bold text-luxury-gold block">&lt; 10ms</span>
                <span className="text-[11px] uppercase tracking-wider text-luxury-muted">Redis Cache Response</span>
              </div>
              <div className="h-10 w-px bg-luxury-border" />
              <div>
                <span className="font-serif text-3xl font-bold text-luxury-gold block">Global</span>
                <span className="text-[11px] uppercase tracking-wider text-luxury-muted">White-Glove Transport</span>
              </div>
            </div>
          </div>

          <div className="bg-gradient-to-br from-luxury-surface via-luxury-elevated to-luxury-black border border-luxury-border p-8 sm:p-12 rounded-sm relative overflow-hidden">
            <div className="absolute top-0 right-0 w-32 h-32 bg-luxury-gold/5 rounded-full blur-3xl pointer-events-none" />
            <h3 className="font-serif text-2xl font-bold text-luxury-white mb-4">
              Private Client Concierge
            </h3>
            <p className="text-xs text-luxury-muted leading-relaxed mb-6">
              Registered patrons receive prioritized allocations of limited-production audio monitors, flagship enterprise laptops, and rare chronometers before global release.
            </p>
            <div className="space-y-3 mb-8 text-xs text-luxury-white">
              <div className="flex items-center gap-3">
                <CheckCircle size={16} className="text-luxury-gold shrink-0" />
                <span>Complimentary insured air freight on consignments over $150</span>
              </div>
              <div className="flex items-center gap-3">
                <CheckCircle size={16} className="text-luxury-gold shrink-0" />
                <span>Dedicated account manager for corporate and private collections</span>
              </div>
              <div className="flex items-center gap-3">
                <CheckCircle size={16} className="text-luxury-gold shrink-0" />
                <span>Instant simulated payment gateway settlements</span>
              </div>
            </div>
            <Link to="/auth">
              <Button variant="gold" size="md">
                Register as a Private Client
              </Button>
            </Link>
          </div>
        </div>
      </section>

      {/* 5. Four Core Brand Value Pillars */}
      <section className="py-16 bg-luxury-surface/50 border-y border-luxury-border">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8">
          <div className="flex items-start gap-4">
            <div className="w-12 h-12 rounded-sm bg-luxury-surface border border-luxury-border flex items-center justify-center text-luxury-gold shrink-0">
              <Truck size={22} />
            </div>
            <div>
              <h4 className="text-sm font-bold text-luxury-white uppercase tracking-wider">White-Glove Logistics</h4>
              <p className="text-xs text-luxury-muted mt-1 leading-relaxed">
                Insured express delivery directly to your residence or executive suite.
              </p>
            </div>
          </div>

          <div className="flex items-start gap-4">
            <div className="w-12 h-12 rounded-sm bg-luxury-surface border border-luxury-border flex items-center justify-center text-luxury-gold shrink-0">
              <Award size={22} />
            </div>
            <div>
              <h4 className="text-sm font-bold text-luxury-white uppercase tracking-wider">Certified Authenticity</h4>
              <p className="text-xs text-luxury-muted mt-1 leading-relaxed">
                Each product is certified with serial tracking and provenance verification.
              </p>
            </div>
          </div>

          <div className="flex items-start gap-4">
            <div className="w-12 h-12 rounded-sm bg-luxury-surface border border-luxury-border flex items-center justify-center text-luxury-gold shrink-0">
              <ShieldCheck size={22} />
            </div>
            <div>
              <h4 className="text-sm font-bold text-luxury-white uppercase tracking-wider">Settlement Security</h4>
              <p className="text-xs text-luxury-muted mt-1 leading-relaxed">
                Full cryptographic token authorization backed by Spring Security 6.
              </p>
            </div>
          </div>

          <div className="flex items-start gap-4">
            <div className="w-12 h-12 rounded-sm bg-luxury-surface border border-luxury-border flex items-center justify-center text-luxury-gold shrink-0">
              <Headphones size={22} />
            </div>
            <div>
              <h4 className="text-sm font-bold text-luxury-white uppercase tracking-wider">Bespoke Concierge</h4>
              <p className="text-xs text-luxury-muted mt-1 leading-relaxed">
                Direct client support for personalized consultations and orders.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* 6. Newsletter Subscription Salon */}
      <section className="py-20 px-4 sm:px-6 lg:px-8 max-w-4xl mx-auto w-full text-center">
        <div className="w-14 h-14 rounded-full bg-luxury-gold/10 border border-luxury-gold/30 flex items-center justify-center mx-auto text-luxury-gold mb-6">
          <Mail size={24} />
        </div>
        <span className="text-xs font-semibold tracking-luxury-widest uppercase text-luxury-gold">
          Private Correspondence
        </span>
        <h2 className="font-serif text-3xl sm:text-4xl font-bold text-luxury-white mt-2 mb-4">
          Join the JPBazaar Salon
        </h2>
        <p className="text-sm text-luxury-muted max-w-lg mx-auto mb-8 leading-relaxed">
          Receive confidential notifications of new acquisitions, limited atelier drops, and invitations to private showroom events.
        </p>

        {isSubscribed ? (
          <div className="p-4 bg-luxury-surface border border-luxury-gold/40 rounded-sm inline-flex items-center gap-3 text-luxury-gold text-sm font-semibold">
            <CheckCircle size={18} />
            <span>Thank you. Your invitation has been confirmed.</span>
          </div>
        ) : (
          <form onSubmit={handleNewsletterSubmit} className="flex flex-col sm:flex-row gap-3 max-w-md mx-auto">
            <Input
              type="email"
              placeholder="Enter private client email..."
              value={newsletterEmail}
              onChange={(e) => setNewsletterEmail(e.target.value)}
              className="flex-1"
              required
            />
            <Button
              type="submit"
              variant="gold"
              size="md"
              isLoading={isSubscribing}
              className="shrink-0"
            >
              Request Access
            </Button>
          </form>
        )}
      </section>

      {/* Quick View Modal */}
      <QuickViewModal
        product={selectedProduct}
        isOpen={isQuickViewOpen}
        onClose={() => {
          setIsQuickViewOpen(false);
          setSelectedProduct(null);
        }}
      />
    </div>
  );
};
