import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Product } from '../../types';
import { useCart } from '../../context/CartContext';
import { useWishlist } from '../../context/WishlistContext';
import { Modal } from '../common/Modal';
import { Button } from '../common/Button';
import { Badge } from '../common/Badge';
import { ShoppingBag, Heart, Star, Check, ArrowRight, ShieldCheck } from 'lucide-react';

interface QuickViewModalProps {
  product: Product | null;
  isOpen: boolean;
  onClose: () => void;
}

export const QuickViewModal: React.FC<QuickViewModalProps> = ({
  product,
  isOpen,
  onClose,
}) => {
  const { addToCart } = useCart();
  const { isInWishlist, toggleWishlist } = useWishlist();

  const [quantity, setQuantity] = useState(1);
  const [isAdding, setIsAdding] = useState(false);
  const [activeImageIndex, setActiveImageIndex] = useState(0);

  if (!product) return null;

  const isSaved = isInWishlist(product.id);
  const isOutOfStock = product.stockQuantity <= 0;
  const isLowStock = product.stockQuantity > 0 && product.stockQuantity <= 5;

  const images = product.imageUrls?.length
    ? product.imageUrls
    : product.images?.map((img) => img.imageUrl) || [];

  const currentImage = images[activeImageIndex] || null;

  const handleAddToCart = async () => {
    if (isOutOfStock) return;
    setIsAdding(true);
    try {
      await addToCart(
        product.id,
        quantity,
        product.name,
        product.price,
        currentImage || undefined
      );
      onClose();
    } finally {
      setIsAdding(false);
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} maxWidth="xl" title={`Artifact Inspection • SKU: ${product.sku}`}>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8 py-2">
        {/* Left: Gallery Showcase */}
        <div className="flex flex-col gap-4">
          <div className="relative aspect-square w-full bg-luxury-elevated rounded-sm overflow-hidden border border-luxury-border">
            {currentImage ? (
              <img
                src={currentImage}
                alt={product.name}
                className="w-full h-full object-cover object-center"
              />
            ) : (
              <div className="w-full h-full flex flex-col items-center justify-center p-6 text-center">
                <span className="font-serif text-4xl font-bold text-luxury-gold/30 mb-2">JP</span>
                <span className="text-xs uppercase tracking-luxury-widest text-luxury-muted">
                  {product.brand || 'Atelier Vault'}
                </span>
              </div>
            )}

            <div className="absolute top-3 left-3">
              {isOutOfStock ? (
                <Badge variant="danger">Out of Stock</Badge>
              ) : isLowStock ? (
                <Badge variant="gold">Only {product.stockQuantity} Remaining</Badge>
              ) : (
                <Badge variant="dark" className="border-luxury-gold/40 text-luxury-gold">
                  In Stock
                </Badge>
              )}
            </div>
          </div>

          {/* Thumbnail Strip */}
          {images.length > 1 && (
            <div className="flex gap-2 overflow-x-auto pb-1">
              {images.map((url, idx) => (
                <button
                  key={idx}
                  type="button"
                  onClick={() => setActiveImageIndex(idx)}
                  className={`w-16 h-16 rounded-sm overflow-hidden border transition-all shrink-0 ${
                    activeImageIndex === idx
                      ? 'border-luxury-gold shadow-gold-sm'
                      : 'border-luxury-border opacity-60 hover:opacity-100'
                  }`}
                >
                  <img src={url} alt={`Thumbnail ${idx + 1}`} className="w-full h-full object-cover" />
                </button>
              ))}
            </div>
          )}
        </div>

        {/* Right: Product Specifications & Actions */}
        <div className="flex flex-col justify-between space-y-6">
          <div className="space-y-4">
            <div>
              <span className="text-xs font-semibold tracking-widest uppercase text-luxury-gold">
                {product.brand || 'Bespoke Atelier'}
              </span>
              <h2 className="font-serif text-2xl font-bold text-luxury-white mt-1">
                {product.name}
              </h2>

              {/* Rating & Category */}
              <div className="flex items-center gap-4 mt-2 text-xs text-luxury-muted">
                {product.rating !== undefined && (
                  <span className="flex items-center gap-1 text-luxury-gold font-bold">
                    <Star size={14} fill="currentColor" />
                    {Number(product.rating).toFixed(1)} / 5.0
                  </span>
                )}
                {product.categoryName && (
                  <span>Category: <strong className="text-luxury-white">{product.categoryName}</strong></span>
                )}
              </div>
            </div>

            {/* Price */}
            <div className="py-3 border-y border-luxury-border flex items-baseline gap-3">
              <span className="font-serif text-3xl font-bold text-luxury-white">
                ${Number(product.price).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
              </span>
              <span className="text-xs text-luxury-muted uppercase tracking-wider">
                USD • Tax Included
              </span>
            </div>

            {/* Description */}
            <p className="text-sm text-luxury-muted leading-relaxed">
              {product.description || 'Exclusive handcrafted item from the JPBazaar vault. Designed with uncompromising standards for elite collectors and discerning professionals.'}
            </p>

            {/* Trust Badges */}
            <div className="grid grid-cols-2 gap-3 pt-2 text-xs text-luxury-muted">
              <div className="flex items-center gap-2">
                <ShieldCheck size={16} className="text-luxury-gold shrink-0" />
                <span>Certified Authenticity</span>
              </div>
              <div className="flex items-center gap-2">
                <Check size={16} className="text-luxury-gold shrink-0" />
                <span>Insured Global Dispatch</span>
              </div>
            </div>
          </div>

          {/* Action Row */}
          <div className="space-y-4 pt-4 border-t border-luxury-border">
            <div className="flex items-center gap-4">
              {/* Quantity Picker */}
              <div className="flex items-center border border-luxury-border rounded-sm bg-luxury-surface">
                <button
                  type="button"
                  onClick={() => setQuantity((q) => Math.max(1, q - 1))}
                  disabled={quantity <= 1 || isOutOfStock}
                  className="px-3 py-2 text-luxury-muted hover:text-luxury-white disabled:opacity-30"
                >
                  -
                </button>
                <span className="px-3 py-2 text-sm font-bold text-luxury-white min-w-[2.5rem] text-center">
                  {quantity}
                </span>
                <button
                  type="button"
                  onClick={() => setQuantity((q) => Math.min(product.stockQuantity || 99, q + 1))}
                  disabled={quantity >= product.stockQuantity || isOutOfStock}
                  className="px-3 py-2 text-luxury-muted hover:text-luxury-white disabled:opacity-30"
                >
                  +
                </button>
              </div>

              {/* Add to Bag Button */}
              <Button
                variant="gold"
                fullWidth
                onClick={handleAddToCart}
                disabled={isOutOfStock}
                isLoading={isAdding}
                leftIcon={<ShoppingBag size={16} />}
              >
                {isOutOfStock ? 'Currently Unavailable' : `Add to Bag • $${(product.price * quantity).toFixed(2)}`}
              </Button>

              {/* Wishlist Button */}
              <button
                type="button"
                onClick={() => toggleWishlist(product)}
                className={`p-3 rounded-sm border transition-colors ${
                  isSaved
                    ? 'bg-luxury-gold text-luxury-black border-luxury-gold'
                    : 'bg-luxury-surface border-luxury-border text-luxury-muted hover:text-luxury-gold'
                }`}
                aria-label="Wishlist"
              >
                <Heart size={18} fill={isSaved ? 'currentColor' : 'none'} />
              </button>
            </div>

            <div className="text-center">
              <Link
                to={`/products/${product.id}`}
                onClick={onClose}
                className="inline-flex items-center text-xs font-semibold uppercase tracking-luxury-widest text-luxury-gold hover:text-luxury-gold-light"
              >
                View Full Product Details &amp; Customer Reviews <ArrowRight size={14} className="ml-1" />
              </Link>
            </div>
          </div>
        </div>
      </div>
    </Modal>
  );
};
