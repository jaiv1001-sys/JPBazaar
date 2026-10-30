import React, { useState } from 'react';
import {
  Button,
  Input,
  Select,
  Badge,
  Card,
  Modal,
  Drawer,
  Skeleton,
  Toast,
} from '../../components/common';
import { Search, ShoppingBag, Heart, ArrowRight, ShieldCheck, Sparkles } from 'lucide-react';

export const DesignShowcasePage: React.FC = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isDrawerOpen, setIsDrawerOpen] = useState(false);
  const [searchVal, setSearchVal] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('all');

  return (
    <div className="py-12 px-6 max-w-7xl mx-auto space-y-16">
      {/* Brand Header */}
      <section className="text-center space-y-4">
        <div className="inline-flex items-center gap-2 px-3 py-1 bg-luxury-gold/10 border border-luxury-gold/30 rounded-sm text-luxury-gold text-xs uppercase tracking-luxury-wide font-semibold">
          <Sparkles size={14} /> Luxury Design System Foundation
        </div>
        <h1 className="heading-hero">
          JPBazaar <span className="text-luxury-gold font-serif italic">Atelier</span>
        </h1>
        <p className="max-w-2xl mx-auto text-luxury-muted text-base">
          Meticulously engineered color tokens, editorial typography, and high-performance reusable UI components built for our luxury e-commerce platform.
        </p>
      </section>

      {/* 1. Brand Color System */}
      <section className="space-y-6">
        <div className="border-b border-luxury-border pb-3">
          <h2 className="heading-section">1. Brand Color Tokens</h2>
          <p className="text-sm text-luxury-muted mt-1">
            Curated black and metallic gold palette designed for sophisticated contrast and elegance.
          </p>
        </div>

        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-4">
          <div className="bg-luxury-black p-4 border border-luxury-border rounded-sm">
            <div className="h-14 rounded-sm border border-luxury-border mb-3 bg-[#080808]" />
            <p className="text-xs font-bold text-luxury-white">Primary Black</p>
            <code className="text-[11px] text-luxury-muted">#080808</code>
          </div>

          <div className="bg-luxury-surface p-4 border border-luxury-border rounded-sm">
            <div className="h-14 rounded-sm mb-3 bg-[#181818]" />
            <p className="text-xs font-bold text-luxury-white">Surface Black</p>
            <code className="text-[11px] text-luxury-muted">#181818</code>
          </div>

          <div className="bg-luxury-elevated p-4 border border-luxury-border rounded-sm">
            <div className="h-14 rounded-sm mb-3 bg-[#222222]" />
            <p className="text-xs font-bold text-luxury-white">Elevated Black</p>
            <code className="text-[11px] text-luxury-muted">#222222</code>
          </div>

          <div className="bg-luxury-surface p-4 border border-luxury-border rounded-sm">
            <div className="h-14 rounded-sm mb-3 bg-[#D4AF37]" />
            <p className="text-xs font-bold text-luxury-gold">Primary Gold</p>
            <code className="text-[11px] text-luxury-muted">#D4AF37</code>
          </div>

          <div className="bg-luxury-surface p-4 border border-luxury-border rounded-sm">
            <div className="h-14 rounded-sm mb-3 bg-[#F5D76E]" />
            <p className="text-xs font-bold text-luxury-gold-light">Shining Gold</p>
            <code className="text-[11px] text-luxury-muted">#F5D76E</code>
          </div>

          <div className="bg-luxury-surface p-4 border border-luxury-border rounded-sm">
            <div className="h-14 rounded-sm mb-3 bg-[#F5F3ED]" />
            <p className="text-xs font-bold text-luxury-white">Soft Ivory Surface</p>
            <code className="text-[11px] text-luxury-muted">#F5F3ED</code>
          </div>
        </div>
      </section>

      {/* 2. Typography Scale */}
      <section className="space-y-6">
        <div className="border-b border-luxury-border pb-3">
          <h2 className="heading-section">2. Editorial Typography</h2>
          <p className="text-sm text-luxury-muted mt-1">
            Playfair Display serif for luxury titles paired with Plus Jakarta Sans for high-clarity reading.
          </p>
        </div>

        <div className="space-y-6 bg-luxury-surface p-8 border border-luxury-border rounded-sm">
          <div>
            <span className="subheading-gold">Hero Headline • 54px Serif</span>
            <p className="font-serif text-3xl sm:text-4xl lg:text-5xl font-bold mt-1 text-luxury-white">
              Beyond Ordinary. The Fine Art of Living.
            </p>
          </div>

          <div className="border-t border-luxury-border pt-4">
            <span className="subheading-gold">Section Header • 28px Serif</span>
            <p className="font-serif text-2xl font-bold mt-1 text-luxury-white">
              Curated Haute Horlogerie &amp; Precision Audio
            </p>
          </div>

          <div className="border-t border-luxury-border pt-4">
            <span className="subheading-gold">Body Typography • 15px Sans</span>
            <p className="font-sans text-sm text-luxury-muted leading-relaxed max-w-2xl mt-1">
              Each product featured in the JPBazaar vault undergoes stringent authentication and provenance verification. Handcrafted materials, sapphire crystal, and acoustically tuned drivers unite in our private collections.
            </p>
          </div>
        </div>
      </section>

      {/* 3. Reusable Buttons */}
      <section className="space-y-6">
        <div className="border-b border-luxury-border pb-3">
          <h2 className="heading-section">3. Interactive Button Hierarchy</h2>
          <p className="text-sm text-luxury-muted mt-1">
            Solid gold primary actions, luxury metallic outlines, dark secondary surfaces, and ghost triggers.
          </p>
        </div>

        <div className="bg-luxury-surface p-8 border border-luxury-border rounded-sm space-y-6">
          <div className="flex flex-wrap items-center gap-4">
            <Button variant="gold" rightIcon={<ArrowRight size={16} />}>
              Explore Collection
            </Button>
            <Button variant="outline" leftIcon={<ShoppingBag size={16} />}>
              Add to Cart
            </Button>
            <Button variant="dark" leftIcon={<Heart size={16} />}>
              Save to Wishlist
            </Button>
            <Button variant="ghost">
              View Specifications
            </Button>
            <Button variant="danger">
              Remove Item
            </Button>
          </div>

          <div className="flex flex-wrap items-center gap-4 pt-4 border-t border-luxury-border">
            <Button variant="gold" size="sm">Small Gold</Button>
            <Button variant="gold" size="md">Medium Gold</Button>
            <Button variant="gold" size="lg">Large Gold</Button>
            <Button variant="gold" isLoading>Processing</Button>
            <Button variant="gold" disabled>Disabled State</Button>
          </div>
        </div>
      </section>

      {/* 4. Form Controls */}
      <section className="space-y-6">
        <div className="border-b border-luxury-border pb-3">
          <h2 className="heading-section">4. Luxury Form Inputs &amp; Selects</h2>
          <p className="text-sm text-luxury-muted mt-1">
            Precision inputs with glowing gold focus states and accessible error messages.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 bg-luxury-surface p-8 border border-luxury-border rounded-sm">
          <Input
            label="Search Catalog"
            placeholder="Search products by brand, model..."
            leftIcon={<Search size={16} />}
            value={searchVal}
            onChange={(e) => setSearchVal(e.target.value)}
            helperText="Instant search across 10+ categories"
          />

          <Select
            label="Sort by Category"
            value={selectedCategory}
            onChange={(e) => setSelectedCategory(e.target.value)}
            options={[
              { value: 'all', label: 'All Curated Collections' },
              { value: 'electronics', label: 'High-Performance Computing' },
              { value: 'audio', label: 'Acoustics & Wearables' },
              { value: 'fashion', label: 'Haute Horlogerie & Apparel' },
            ]}
          />

          <Input
            label="Delivery Postal Code"
            defaultValue="97477"
            error="Valid 5-digit postal code required for overnight shipping"
          />
        </div>
      </section>

      {/* 5. Luxury Cards */}
      <section className="space-y-6">
        <div className="border-b border-luxury-border pb-3">
          <h2 className="heading-section">5. Dual Surface Philosophy</h2>
          <p className="text-sm text-luxury-muted mt-1">
            Dark elevated cards for brand ambiance contrasted with soft ivory surfaces for product clarity.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <Card variant="surface" hoverEffect className="p-6 space-y-4">
            <div className="flex justify-between items-start">
              <Badge variant="gold">In Stock</Badge>
              <span className="text-luxury-gold text-sm font-semibold">SKU: SONY-XM5</span>
            </div>
            <h3 className="font-serif text-xl font-bold text-luxury-white">Dark Surface Card</h3>
            <p className="text-sm text-luxury-muted">
              Premium dark backdrop with subtle 1px border and smooth hover lift for high-tech items.
            </p>
            <div className="pt-4 border-t border-luxury-border flex justify-between items-center">
              <span className="font-bold text-lg text-luxury-white">$398.00</span>
              <Button size="sm" variant="gold">Quick Add</Button>
            </div>
          </Card>

          <Card variant="elevated" hoverEffect className="p-6 space-y-4">
            <div className="flex justify-between items-start">
              <Badge variant="gold-outline">Exclusive</Badge>
              <ShieldCheck size={18} className="text-luxury-gold" />
            </div>
            <h3 className="font-serif text-xl font-bold text-luxury-white">Elevated Card</h3>
            <p className="text-sm text-luxury-muted">
              Higher shadow contrast and gold accent borders for spotlight promotions and VIP items.
            </p>
            <div className="pt-4 border-t border-luxury-border flex justify-between items-center">
              <span className="font-bold text-lg text-luxury-gold">$2,499.00</span>
              <Button size="sm" variant="outline">Reserve</Button>
            </div>
          </Card>

          <Card variant="ivory" hoverEffect className="p-6 space-y-4">
            <div className="flex justify-between items-start">
              <Badge variant="dark">Product Clarity</Badge>
              <span className="text-xs font-bold text-stone-600">IVORY SURFACE</span>
            </div>
            <h3 className="font-serif text-xl font-bold text-luxury-black">Ivory Clarity Surface</h3>
            <p className="text-sm text-stone-700 leading-relaxed">
              Light background recommended by our creative director for crisp readability on detailed product specs.
            </p>
            <div className="pt-4 border-t border-stone-300 flex justify-between items-center">
              <span className="font-bold text-luxury-black">$160.00</span>
              <Button size="sm" variant="gold" className="bg-luxury-black text-luxury-white hover:bg-stone-800">
                Inspect Spec
              </Button>
            </div>
          </Card>
        </div>
      </section>

      {/* 6. Overlays */}
      <section className="space-y-6">
        <div className="border-b border-luxury-border pb-3">
          <h2 className="heading-section">6. Modals &amp; Slide-Over Drawers</h2>
          <p className="text-sm text-luxury-muted mt-1">
            Accessible dialogs and slide-over panels for interactive cart and details.
          </p>
        </div>

        <div className="bg-luxury-surface p-8 border border-luxury-border rounded-sm flex flex-wrap gap-4 items-center justify-between">
          <div>
            <h4 className="font-serif font-bold text-lg">Interactive Experience Triggers</h4>
            <p className="text-sm text-luxury-muted">Test drawer sliding animations and modal backdrop blurs.</p>
          </div>
          <div className="flex gap-4">
            <Button variant="outline" onClick={() => setIsModalOpen(true)}>
              Open Luxury Modal
            </Button>
            <Button variant="gold" onClick={() => setIsDrawerOpen(true)}>
              Open Slide-Over Drawer
            </Button>
          </div>
        </div>
      </section>

      {/* 7. Skeletons & Toasts */}
      <section className="space-y-6">
        <div className="border-b border-luxury-border pb-3">
          <h2 className="heading-section">7. Loading Skeletons &amp; Feedback Toasts</h2>
          <p className="text-sm text-luxury-muted mt-1">
            Shimmering dark placeholders and gold-accented feedback toasts.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-8 bg-luxury-surface p-8 border border-luxury-border rounded-sm">
          <div className="space-y-3">
            <p className="text-xs font-semibold uppercase text-luxury-muted">Card Shimmer Placeholder</p>
            <Skeleton className="h-44 w-full" />
            <div className="space-y-2">
              <Skeleton variant="text" className="w-3/4" />
              <Skeleton variant="text" className="w-1/2" />
            </div>
          </div>

          <div className="space-y-3">
            <p className="text-xs font-semibold uppercase text-luxury-muted">Status Toasts</p>
            <Toast type="success" message="Sony WH-1000XM5 added to cart." />
            <Toast type="error" message="Simulated payment declined. Please try another card." />
            <Toast type="info" message="Dual-engine sliding counter rate limit: 98 remaining." />
          </div>
        </div>
      </section>

      {/* Demonstration Modal */}
      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="VIP Collector Concierge">
        <div className="space-y-4">
          <p className="text-sm text-luxury-muted leading-relaxed">
            Welcome to the JPBazaar private showroom. Our bespoke concierge team is standing by to assist with personal styling and priority insured delivery worldwide.
          </p>
          <Input label="Your Dedicated Concierge Notes" placeholder="Specify ring size, preferred serial number, etc." />
          <div className="flex justify-end gap-3 pt-4 border-t border-luxury-border">
            <Button variant="ghost" onClick={() => setIsModalOpen(false)}>Dismiss</Button>
            <Button variant="gold" onClick={() => setIsModalOpen(false)}>Submit Request</Button>
          </div>
        </div>
      </Modal>

      {/* Demonstration Drawer */}
      <Drawer isOpen={isDrawerOpen} onClose={() => setIsDrawerOpen(false)} title="Design Showcase Drawer">
        <div className="space-y-4 text-sm text-luxury-muted">
          <p>This drawer is part of the Phase B design system showcase.</p>
          <Button variant="gold" fullWidth onClick={() => setIsDrawerOpen(false)}>
            Close Drawer
          </Button>
        </div>
      </Drawer>
    </div>
  );
};
