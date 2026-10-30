import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useCart } from '../../context/CartContext';
import { useAuth } from '../../context/AuthContext';
import { Button } from '../../components/common/Button';
import {
  ShoppingBag,
  Trash2,
  Plus,
  Minus,
  ArrowRight,
  ShieldCheck,
  Truck,
  RotateCcw,
} from 'lucide-react';

export const CartPage: React.FC = () => {
  const {
    cart,
    guestItems,
    updateQuantity,
    removeItem,
    clearCart,
    totalCount,
    totalAmount,
    isLoading,
  } = useCart();
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const items = isAuthenticated ? cart?.items || [] : guestItems;
  const shippingThreshold = 150;
  const isFreeShipping = totalAmount >= shippingThreshold;
  const estimatedShipping = totalAmount > 0 ? (isFreeShipping ? 0 : 25) : 0;
  const finalTotal = totalAmount + estimatedShipping;

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 w-full flex-1 flex flex-col">
      {/* Breadcrumb Navigation */}
      <nav className="flex items-center gap-2 text-xs text-luxury-muted uppercase tracking-wider mb-6">
        <Link to="/" className="hover:text-luxury-gold transition-colors">Sanctuary</Link>
        <span>/</span>
        <span className="text-luxury-white font-semibold">Shopping Bag</span>
      </nav>

      <div className="border-b border-luxury-border pb-6 mb-8 flex flex-col sm:flex-row sm:items-end justify-between gap-4">
        <div>
          <span className="text-xs font-semibold tracking-luxury-widest uppercase text-luxury-gold">
            Personal Acquisition
          </span>
          <h1 className="font-serif text-3xl sm:text-4xl font-bold text-luxury-white mt-1">
            Shopping Bag ({totalCount} {totalCount === 1 ? 'Artifact' : 'Artifacts'})
          </h1>
        </div>

        {items.length > 0 && (
          <Button
            variant="ghost"
            size="sm"
            onClick={clearCart}
            disabled={isLoading}
            className="text-luxury-muted hover:text-rose-400 self-start sm:self-auto"
            leftIcon={<Trash2 size={14} />}
          >
            Clear Bag
          </Button>
        )}
      </div>

      {items.length === 0 ? (
        /* Empty Bag State */
        <div className="flex-1 flex flex-col items-center justify-center text-center py-20 px-4 bg-luxury-surface border border-luxury-border rounded-sm">
          <div className="w-20 h-20 rounded-full bg-luxury-elevated border border-luxury-border flex items-center justify-center text-luxury-gold mb-6">
            <ShoppingBag size={36} />
          </div>
          <h2 className="font-serif text-2xl font-bold text-luxury-white">Your Shopping Bag is Empty</h2>
          <p className="text-sm text-luxury-muted max-w-md mt-2 mb-8 leading-relaxed">
            Our atelier houses precision acoustics, liquid-cooled computing, and fine horology. Begin your curation by exploring our seasonal vault.
          </p>
          <Link to="/products">
            <Button variant="gold" size="lg" rightIcon={<ArrowRight size={16} />}>
              Explore Curated Collections
            </Button>
          </Link>
        </div>
      ) : (
        /* Bag Table & Checkout Summary */
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-10 items-start">
          {/* Left: Item Rows */}
          <div className="lg:col-span-2 space-y-4">
            {items.map((item) => (
              <div
                key={item.id}
                className="bg-luxury-surface border border-luxury-border p-4 sm:p-6 rounded-sm flex flex-col sm:flex-row items-center gap-6"
              >
                {/* Thumbnail */}
                <Link
                  to={`/products/${item.productId}`}
                  className="w-24 h-24 sm:w-28 sm:h-28 rounded-sm bg-luxury-elevated border border-luxury-border overflow-hidden shrink-0 block"
                >
                  {item.imageUrl ? (
                    <img
                      src={item.imageUrl}
                      alt={item.productName}
                      className="w-full h-full object-cover"
                    />
                  ) : (
                    <div className="w-full h-full flex items-center justify-center text-luxury-gold font-serif font-bold text-xl">
                      JP
                    </div>
                  )}
                </Link>

                {/* Details */}
                <div className="flex-1 text-center sm:text-left space-y-1">
                  <Link
                    to={`/products/${item.productId}`}
                    className="font-serif font-bold text-base sm:text-lg text-luxury-white hover:text-luxury-gold transition-colors line-clamp-1"
                  >
                    {item.productName}
                  </Link>
                  <p className="text-xs text-luxury-muted">
                    Unit Price: ${Number(item.price).toFixed(2)}
                  </p>

                  <div className="pt-2 flex items-center justify-center sm:justify-start gap-4">
                    {/* Quantity Selector */}
                    <div className="flex items-center border border-luxury-border rounded-sm bg-luxury-elevated">
                      <button
                        type="button"
                        onClick={() => updateQuantity(item.id, Math.max(1, item.quantity - 1))}
                        disabled={isLoading || item.quantity <= 1}
                        className="px-2.5 py-1 text-luxury-muted hover:text-luxury-white disabled:opacity-30"
                        aria-label="Decrease quantity"
                      >
                        <Minus size={14} />
                      </button>
                      <span className="px-3 py-1 text-xs font-bold text-luxury-white min-w-[2rem] text-center">
                        {item.quantity}
                      </span>
                      <button
                        type="button"
                        onClick={() => updateQuantity(item.id, item.quantity + 1)}
                        disabled={isLoading}
                        className="px-2.5 py-1 text-luxury-muted hover:text-luxury-white disabled:opacity-30"
                        aria-label="Increase quantity"
                      >
                        <Plus size={14} />
                      </button>
                    </div>

                    {/* Delete Item */}
                    <button
                      type="button"
                      onClick={() => removeItem(item.id)}
                      disabled={isLoading}
                      className="text-xs text-luxury-muted hover:text-rose-400 transition-colors flex items-center gap-1"
                      aria-label="Remove item"
                    >
                      <Trash2 size={14} />
                      <span className="hidden sm:inline">Remove</span>
                    </button>
                  </div>
                </div>

                {/* Line Total */}
                <div className="text-center sm:text-right">
                  <span className="text-[11px] text-luxury-muted uppercase tracking-wider block">Line Total</span>
                  <span className="font-serif font-bold text-lg sm:text-xl text-luxury-white">
                    ${(item.price * item.quantity).toFixed(2)}
                  </span>
                </div>
              </div>
            ))}

            <div className="pt-4 flex justify-between items-center text-xs">
              <Link to="/products" className="text-luxury-gold hover:text-luxury-gold-light uppercase tracking-wider font-semibold inline-flex items-center gap-1">
                ← Continue Browsing Catalog
              </Link>
            </div>
          </div>

          {/* Right: Order Summary Sidebar */}
          <div className="bg-luxury-surface border border-luxury-border p-6 sm:p-8 rounded-sm space-y-6">
            <h3 className="font-serif text-xl font-bold text-luxury-white pb-3 border-b border-luxury-border">
              Acquisition Summary
            </h3>

            <div className="space-y-3 text-sm">
              <div className="flex justify-between text-luxury-muted">
                <span>Subtotal ({totalCount} items)</span>
                <span className="text-luxury-white font-medium">${totalAmount.toFixed(2)}</span>
              </div>

              <div className="flex justify-between text-luxury-muted">
                <span>Insured Global Freight</span>
                {isFreeShipping ? (
                  <span className="text-luxury-gold font-bold">COMPLIMENTARY</span>
                ) : (
                  <span className="text-luxury-white font-medium">${estimatedShipping.toFixed(2)}</span>
                )}
              </div>

              {!isFreeShipping && (
                <div className="p-2.5 bg-luxury-gold/10 border border-luxury-gold/30 rounded-sm text-[11px] text-luxury-gold text-center">
                  Add ${(shippingThreshold - totalAmount).toFixed(2)} more to unlock complimentary white-glove transport.
                </div>
              )}

              <div className="border-t border-luxury-border pt-4 flex justify-between items-baseline">
                <span className="font-serif font-bold text-base text-luxury-white">Estimated Total</span>
                <span className="font-serif font-bold text-2xl text-luxury-gold">
                  ${finalTotal.toFixed(2)}
                </span>
              </div>
            </div>

            <Button
              variant="gold"
              size="lg"
              fullWidth
              onClick={() => navigate('/checkout')}
              rightIcon={<ArrowRight size={18} />}
            >
              Proceed to Secure Checkout
            </Button>

            {/* Guarantees */}
            <div className="space-y-3 pt-6 border-t border-luxury-border text-xs text-luxury-muted">
              <div className="flex items-center gap-2.5">
                <ShieldCheck size={16} className="text-luxury-gold shrink-0" />
                <span>Simulated Cryptographic Checkout</span>
              </div>
              <div className="flex items-center gap-2.5">
                <Truck size={16} className="text-luxury-gold shrink-0" />
                <span>Insured White-Glove Dispatch</span>
              </div>
              <div className="flex items-center gap-2.5">
                <RotateCcw size={16} className="text-luxury-gold shrink-0" />
                <span>30-Day Discretionary Returns</span>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
