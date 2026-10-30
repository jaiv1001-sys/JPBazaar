import React from 'react';
import { Link } from 'react-router-dom';
import { useWishlist } from '../../context/WishlistContext';
import { useCart } from '../../context/CartContext';
import { Button } from '../../components/common/Button';
import { Badge } from '../../components/common/Badge';
import { Heart, ShoppingBag, Trash2, ArrowRight } from 'lucide-react';

export const WishlistPage: React.FC = () => {
  const { items, removeItem, clearWishlist, wishlistCount, isLoading } = useWishlist();
  const { addToCart } = useCart();

  const handleMoveToBag = async (productId: number, productName: string, price: number, thumbnail?: string) => {
    await addToCart(productId, 1, productName, price, thumbnail);
    await removeItem(productId);
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 w-full flex-1 flex flex-col">
      {/* Breadcrumb Navigation */}
      <nav className="flex items-center gap-2 text-xs text-luxury-muted uppercase tracking-wider mb-6">
        <Link to="/" className="hover:text-luxury-gold transition-colors">Sanctuary</Link>
        <span>/</span>
        <span className="text-luxury-white font-semibold">Personal Wishlist</span>
      </nav>

      {/* Header Bar */}
      <div className="border-b border-luxury-border pb-6 mb-8 flex flex-col sm:flex-row sm:items-end justify-between gap-4">
        <div>
          <span className="text-xs font-semibold tracking-luxury-widest uppercase text-luxury-gold">
            Curated Desires
          </span>
          <h1 className="font-serif text-3xl sm:text-4xl font-bold text-luxury-white mt-1">
            Private Wishlist ({wishlistCount} {wishlistCount === 1 ? 'Artifact' : 'Artifacts'})
          </h1>
        </div>

        {items.length > 0 && (
          <Button
            variant="ghost"
            size="sm"
            onClick={clearWishlist}
            disabled={isLoading}
            className="text-luxury-muted hover:text-rose-400 self-start sm:self-auto"
            leftIcon={<Trash2 size={14} />}
          >
            Clear Wishlist
          </Button>
        )}
      </div>

      {items.length === 0 ? (
        /* Empty State */
        <div className="flex-1 flex flex-col items-center justify-center text-center py-20 px-4 bg-luxury-surface border border-luxury-border rounded-sm">
          <div className="w-20 h-20 rounded-full bg-luxury-elevated border border-luxury-border flex items-center justify-center text-luxury-gold mb-6">
            <Heart size={36} />
          </div>
          <h2 className="font-serif text-2xl font-bold text-luxury-white">Your Wishlist is Empty</h2>
          <p className="text-sm text-luxury-muted max-w-md mt-2 mb-8 leading-relaxed">
            Bookmark items from our curated catalog to preserve them for future consideration or private concierge acquisition.
          </p>
          <Link to="/products">
            <Button variant="gold" size="lg" rightIcon={<ArrowRight size={16} />}>
              Discover Curated Collections
            </Button>
          </Link>
        </div>
      ) : (
        /* Wishlist Grid */
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {items.map((item) => (
            <div
              key={item.id}
              className="bg-luxury-surface border border-luxury-border rounded-sm overflow-hidden flex flex-col justify-between hover:border-luxury-gold/50 transition-all group hover:shadow-gold-sm"
            >
              {/* Image Frame */}
              <div className="relative aspect-square w-full bg-luxury-elevated overflow-hidden">
                <Link to={`/products/${item.productId}`} className="block w-full h-full">
                  {item.imageUrl ? (
                    <img
                      src={item.imageUrl}
                      alt={item.productName}
                      className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                    />
                  ) : (
                    <div className="w-full h-full flex items-center justify-center text-luxury-gold font-serif font-bold text-3xl">
                      JP
                    </div>
                  )}
                </Link>

                {/* Stock Badge */}
                <div className="absolute top-3 left-3">
                  {item.inStock ? (
                    <Badge variant="dark" size="sm" className="border-luxury-gold/40 text-luxury-gold">
                      In Stock
                    </Badge>
                  ) : (
                    <Badge variant="danger" size="sm">
                      Out of Stock
                    </Badge>
                  )}
                </div>

                {/* Delete Button */}
                <button
                  type="button"
                  onClick={() => removeItem(item.productId)}
                  className="absolute top-3 right-3 p-2 rounded-full bg-luxury-black/70 text-luxury-muted hover:text-rose-400 border border-luxury-border/60 transition-colors"
                  aria-label="Remove from wishlist"
                >
                  <Trash2 size={16} />
                </button>
              </div>

              {/* Item Details & Actions */}
              <div className="p-4 sm:p-5 flex flex-col flex-1 justify-between gap-4">
                <div>
                  <Link
                    to={`/products/${item.productId}`}
                    className="font-serif font-bold text-base text-luxury-white hover:text-luxury-gold transition-colors line-clamp-1"
                  >
                    {item.productName}
                  </Link>
                  <span className="font-serif font-bold text-lg text-luxury-white block mt-1">
                    ${Number(item.price).toFixed(2)}
                  </span>
                </div>

                <div className="pt-3 border-t border-luxury-border/60 space-y-2">
                  <Button
                    variant="gold"
                    size="sm"
                    fullWidth
                    onClick={() =>
                      handleMoveToBag(
                        item.productId,
                        item.productName,
                        item.price,
                        item.imageUrl
                      )
                    }
                    disabled={!item.inStock}
                    leftIcon={<ShoppingBag size={14} />}
                  >
                    {item.inStock ? 'Move to Shopping Bag' : 'Currently Unavailable'}
                  </Button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
