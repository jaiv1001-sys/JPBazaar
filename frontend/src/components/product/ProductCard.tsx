import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Product } from '../../types';
import { useCart } from '../../context/CartContext';
import { useWishlist } from '../../context/WishlistContext';
import { Button } from '../common/Button';
import { Badge } from '../common/Badge';
import { Heart, ShoppingBag, Eye, Star } from 'lucide-react';

interface ProductCardProps {
  product: Product;
  onQuickView?: (product: Product) => void;
}

export const ProductCard: React.FC<ProductCardProps> = ({ product, onQuickView }) => {
  const { addToCart } = useCart();
  const { isInWishlist, toggleWishlist } = useWishlist();

  const [isAdding, setIsAdding] = useState(false);
  const [imageError, setImageError] = useState(false);

  const isSaved = isInWishlist(product.id);
  const isOutOfStock = product.stockQuantity <= 0;
  const isLowStock = product.stockQuantity > 0 && product.stockQuantity <= 5;

  const primaryImage =
    !imageError && (product.imageUrls?.[0] || product.images?.[0]?.imageUrl)
      ? product.imageUrls?.[0] || product.images?.[0]?.imageUrl
      : null;

  const handleAddToCart = async (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    if (isOutOfStock) return;

    setIsAdding(true);
    try {
      await addToCart(
        product.id,
        1,
        product.name,
        product.price,
        primaryImage || undefined
      );
    } finally {
      setIsAdding(false);
    }
  };

  const handleToggleWishlist = async (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    await toggleWishlist(product);
  };

  const handleQuickView = (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    if (onQuickView) {
      onQuickView(product);
    }
  };

  return (
    <div className="group relative bg-luxury-surface border border-luxury-border hover:border-luxury-gold/50 rounded-sm transition-all duration-300 flex flex-col h-full overflow-hidden hover:shadow-gold-sm">
      {/* Product Image Frame */}
      <div className="relative aspect-square w-full bg-luxury-elevated overflow-hidden">
        <Link to={`/products/${product.id}`} className="block w-full h-full">
          {primaryImage ? (
            <img
              src={primaryImage}
              alt={product.name}
              onError={() => setImageError(true)}
              className="w-full h-full object-cover object-center group-hover:scale-105 transition-transform duration-500"
              loading="lazy"
            />
          ) : (
            <div className="w-full h-full flex flex-col items-center justify-center p-6 text-center bg-gradient-to-br from-luxury-surface to-luxury-elevated">
              <span className="font-serif text-3xl font-bold text-luxury-gold/30 mb-2">JP</span>
              <span className="text-xs uppercase tracking-luxury-widest text-luxury-muted font-semibold">
                {product.brand || 'Atelier Vault'}
              </span>
            </div>
          )}
        </Link>

        {/* Stock / Promotion Badges */}
        <div className="absolute top-3 left-3 flex flex-col gap-1.5 z-10 pointer-events-none">
          {isOutOfStock ? (
            <Badge variant="danger" size="sm">Out of Stock</Badge>
          ) : isLowStock ? (
            <Badge variant="gold" size="sm">Only {product.stockQuantity} Left</Badge>
          ) : null}
        </div>

        {/* Wishlist Toggle Button */}
        <button
          type="button"
          onClick={handleToggleWishlist}
          className={`absolute top-3 right-3 z-10 p-2 rounded-full transition-all duration-200 ${
            isSaved
              ? 'bg-luxury-gold text-luxury-black shadow-gold-glow'
              : 'bg-luxury-black/70 backdrop-blur-sm text-luxury-white hover:text-luxury-gold border border-luxury-border/60 hover:border-luxury-gold/60'
          }`}
          aria-label={isSaved ? 'Remove from wishlist' : 'Save to wishlist'}
        >
          <Heart size={16} fill={isSaved ? 'currentColor' : 'none'} />
        </button>

        {/* Hover Quick Action Tray */}
        <div className="absolute inset-x-3 bottom-3 z-10 hidden sm:flex items-center gap-2 opacity-0 group-hover:opacity-100 transition-opacity duration-300 pointer-events-none group-hover:pointer-events-auto">
          {onQuickView && (
            <Button
              type="button"
              variant="dark"
              size="sm"
              onClick={handleQuickView}
              className="flex-1 bg-luxury-black/80 backdrop-blur-sm border-luxury-border/80 text-xs py-2"
              leftIcon={<Eye size={14} />}
            >
              Quick View
            </Button>
          )}
          <Button
            type="button"
            variant="gold"
            size="sm"
            onClick={handleAddToCart}
            disabled={isOutOfStock}
            isLoading={isAdding}
            className="flex-1 text-xs py-2 shadow-luxury-sm"
            leftIcon={<ShoppingBag size={14} />}
          >
            {isOutOfStock ? 'Sold Out' : 'Add to Bag'}
          </Button>
        </div>
      </div>

      {/* Product Information Body */}
      <div className="p-4 sm:p-5 flex flex-col flex-1 justify-between gap-3">
        <div>
          {/* Brand & Category */}
          <div className="flex items-center justify-between text-[11px] uppercase tracking-widest text-luxury-muted font-semibold mb-1">
            <span>{product.brand || 'Bespoke'}</span>
            {product.rating !== undefined && product.rating > 0 && (
              <span className="flex items-center gap-1 text-luxury-gold font-sans font-bold">
                <Star size={12} fill="currentColor" />
                {Number(product.rating).toFixed(1)}
              </span>
            )}
          </div>

          {/* Product Title */}
          <Link to={`/products/${product.id}`} className="group-hover:text-luxury-gold transition-colors">
            <h3 className="font-serif font-bold text-sm sm:text-base text-luxury-white line-clamp-1">
              {product.name}
            </h3>
          </Link>

          {/* Description Snippet */}
          {product.description && (
            <p className="text-xs text-luxury-muted line-clamp-2 mt-1 leading-relaxed">
              {product.description}
            </p>
          )}
        </div>

        {/* Pricing & Mobile Quick Add */}
        <div className="pt-3 border-t border-luxury-border/60 flex items-center justify-between gap-2">
          <div>
            <span className="text-xs text-luxury-subtle uppercase tracking-wider block">Price</span>
            <span className="font-serif font-bold text-base sm:text-lg text-luxury-white">
              ${Number(product.price).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
            </span>
          </div>

          {/* Mobile visible action button */}
          <Button
            type="button"
            variant="gold"
            size="sm"
            onClick={handleAddToCart}
            disabled={isOutOfStock}
            isLoading={isAdding}
            className="sm:hidden px-3"
            aria-label="Add to cart"
          >
            <ShoppingBag size={14} />
          </Button>
        </div>
      </div>
    </div>
  );
};
