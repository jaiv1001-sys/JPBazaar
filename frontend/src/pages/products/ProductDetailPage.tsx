import React, { useEffect, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { Product, ProductReviewSummaryResponse } from '../../types';
import { productService } from '../../services/productService';
import { reviewService } from '../../services/reviewService';
import { useCart } from '../../context/CartContext';
import { useWishlist } from '../../context/WishlistContext';
import { useAuth } from '../../context/AuthContext';
import { useToast } from '../../context/ToastContext';
import { ProductCard } from '../../components/product/ProductCard';
import { Button } from '../../components/common/Button';
import { Badge } from '../../components/common/Badge';
import { Skeleton } from '../../components/common/Skeleton';
import {
  Heart,
  ShoppingBag,
  Star,
  ShieldCheck,
  Truck,
  RotateCcw,
  CheckCircle,
  MessageSquare,
} from 'lucide-react';

export const ProductDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const { addToCart } = useCart();
  const { isInWishlist, toggleWishlist } = useWishlist();
  const { isAuthenticated } = useAuth();
  const { showToast } = useToast();

  const [product, setProduct] = useState<Product | null>(null);
  const [relatedProducts, setRelatedProducts] = useState<Product[]>([]);
  const [reviewsData, setReviewsData] = useState<ProductReviewSummaryResponse | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [selectedImageIndex, setSelectedImageIndex] = useState<number>(0);

  // Add to Bag state
  const [quantity, setQuantity] = useState<number>(1);
  const [isAdding, setIsAdding] = useState<boolean>(false);

  // Review Form state
  const [newRating, setNewRating] = useState<number>(5);
  const [newComment, setNewComment] = useState<string>('');
  const [isSubmittingReview, setIsSubmittingReview] = useState<boolean>(false);

  const productId = Number(id);

  // Load product & reviews
  useEffect(() => {
    if (isNaN(productId)) return;

    setIsLoading(true);
    setSelectedImageIndex(0);
    setQuantity(1);

    Promise.all([
      productService.getProductById(productId),
      reviewService.getProductReviews(productId, 0, 5).catch(() => null),
    ])
      .then(([prod, reviews]) => {
        setProduct(prod);
        if (reviews) setReviewsData(reviews);

        // Fetch related products from same category
        if (prod.categoryId) {
          productService.getProducts({ categoryId: prod.categoryId, size: 4, active: true })
            .then((res) => {
              setRelatedProducts(res.content.filter((p) => p.id !== prod.id).slice(0, 3));
            })
            .catch(() => {});
        }
      })
      .catch((err) => {
        console.error('Failed to load product detail:', err);
        showToast('Unable to discover this artifact.', 'error');
      })
      .finally(() => {
        setIsLoading(false);
      });
  }, [productId, showToast]);

  if (isLoading) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 w-full space-y-8">
        <Skeleton className="h-4 w-48" />
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-12">
          <Skeleton className="aspect-square w-full rounded-sm" />
          <div className="space-y-6">
            <Skeleton className="h-6 w-32" />
            <Skeleton className="h-10 w-3/4" />
            <Skeleton className="h-8 w-40" />
            <Skeleton className="h-24 w-full" />
            <Skeleton className="h-12 w-full" />
          </div>
        </div>
      </div>
    );
  }

  if (!product) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20 text-center">
        <h2 className="font-serif text-2xl font-bold text-luxury-white">Artifact Undiscovered</h2>
        <p className="text-sm text-luxury-muted mt-2 mb-6">
          The requested luxury piece could not be located in our inventory.
        </p>
        <Link to="/products">
          <Button variant="gold" size="sm">Return to Collections</Button>
        </Link>
      </div>
    );
  }

  const isSaved = isInWishlist(product.id);
  const isOutOfStock = product.stockQuantity <= 0;
  const isLowStock = product.stockQuantity > 0 && product.stockQuantity <= 5;

  const images = product.imageUrls?.length
    ? product.imageUrls
    : product.images?.map((img) => img.imageUrl) || [];

  const currentImage = images[selectedImageIndex] || null;

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
    } finally {
      setIsAdding(false);
    }
  };

  const handleBuyNow = async () => {
    if (isOutOfStock) return;
    await addToCart(
      product.id,
      quantity,
      product.name,
      product.price,
      currentImage || undefined
    );
    navigate('/checkout');
  };

  const handleReviewSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!isAuthenticated) {
      showToast('Please sign in to submit a verified patron review.', 'error');
      navigate(`/auth?redirect=/products/${product.id}`);
      return;
    }

    if (!newComment.trim()) {
      showToast('Please provide your commentary.', 'error');
      return;
    }

    setIsSubmittingReview(true);
    try {
      await reviewService.addReview({
        productId: product.id,
        rating: newRating,
        comment: newComment.trim(),
      });
      showToast('Thank you for sharing your experience. Review published.', 'success');
      setNewComment('');

      // Refresh reviews
      const updated = await reviewService.getProductReviews(product.id, 0, 5);
      setReviewsData(updated);
    } catch (err: any) {
      showToast(err.message || 'Failed to submit review. Verified buyer verification required.', 'error');
    } finally {
      setIsSubmittingReview(false);
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 w-full flex-1 flex flex-col">
      {/* Breadcrumb Navigation */}
      <nav className="flex items-center gap-2 text-xs text-luxury-muted uppercase tracking-wider mb-8">
        <Link to="/" className="hover:text-luxury-gold transition-colors">Sanctuary</Link>
        <span>/</span>
        <Link to="/products" className="hover:text-luxury-gold transition-colors">Curated Collections</Link>
        {product.categoryName && (
          <>
            <span>/</span>
            <Link to={`/products?categoryId=${product.categoryId}`} className="hover:text-luxury-gold transition-colors">
              {product.categoryName}
            </Link>
          </>
        )}
        <span>/</span>
        <span className="text-luxury-white font-semibold truncate max-w-xs">{product.name}</span>
      </nav>

      {/* Main Two-Column Product Presentation */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 lg:gap-16 pb-16 border-b border-luxury-border">
        {/* Left Column: Gallery */}
        <div className="space-y-4">
          <div className="relative aspect-square w-full bg-luxury-elevated rounded-sm overflow-hidden border border-luxury-border">
            {currentImage ? (
              <img
                src={currentImage}
                alt={product.name}
                className="w-full h-full object-cover object-center"
              />
            ) : (
              <div className="w-full h-full flex flex-col items-center justify-center p-8 text-center bg-gradient-to-br from-luxury-surface to-luxury-elevated">
                <span className="font-serif text-5xl font-bold text-luxury-gold/30 mb-3">JP</span>
                <span className="text-xs uppercase tracking-luxury-widest text-luxury-muted">
                  {product.brand || 'Atelier Masterpiece'}
                </span>
              </div>
            )}

            {/* Badges */}
            <div className="absolute top-4 left-4 flex flex-col gap-2">
              {isOutOfStock ? (
                <Badge variant="danger" size="md">Currently Out of Stock</Badge>
              ) : isLowStock ? (
                <Badge variant="gold" size="md">Only {product.stockQuantity} Remaining</Badge>
              ) : (
                <Badge variant="dark" size="md" className="border-luxury-gold/50 text-luxury-gold">
                  In Stock • Dispatch Ready
                </Badge>
              )}
            </div>
          </div>

          {/* Thumbnail Strip */}
          {images.length > 1 && (
            <div className="flex gap-3 overflow-x-auto pb-2">
              {images.map((imgUrl, idx) => (
                <button
                  key={idx}
                  type="button"
                  onClick={() => setSelectedImageIndex(idx)}
                  className={`w-20 h-20 rounded-sm overflow-hidden border transition-all shrink-0 ${
                    selectedImageIndex === idx
                      ? 'border-luxury-gold shadow-gold-sm'
                      : 'border-luxury-border opacity-60 hover:opacity-100'
                  }`}
                >
                  <img src={imgUrl} alt={`Thumbnail ${idx + 1}`} className="w-full h-full object-cover" />
                </button>
              ))}
            </div>
          )}
        </div>

        {/* Right Column: Information & Actions */}
        <div className="flex flex-col justify-between space-y-8">
          <div className="space-y-6">
            <div>
              <div className="flex items-center justify-between">
                <span className="text-xs font-semibold tracking-luxury-widest uppercase text-luxury-gold">
                  {product.brand || 'Bespoke Atelier'}
                </span>
                <span className="text-xs text-luxury-subtle font-mono">
                  SKU: {product.sku}
                </span>
              </div>

              <h1 className="font-serif text-3xl sm:text-4xl font-bold text-luxury-white mt-2 leading-tight">
                {product.name}
              </h1>

              {/* Rating & Review Count */}
              <div className="flex items-center gap-4 mt-3 text-xs text-luxury-muted">
                <div className="flex items-center gap-1.5 text-luxury-gold font-bold">
                  <Star size={15} fill="currentColor" />
                  <span>{product.rating !== undefined && product.rating > 0 ? Number(product.rating).toFixed(1) : '5.0'}</span>
                </div>
                <span>•</span>
                <a href="#reviews" className="hover:text-luxury-white underline transition-colors">
                  {reviewsData?.totalReviews || 0} Verified Reviews
                </a>
                <span>•</span>
                <span className="text-luxury-gold">{product.categoryName}</span>
              </div>
            </div>

            {/* Price Presentation */}
            <div className="py-4 border-y border-luxury-border flex items-baseline gap-4">
              <span className="font-serif text-3xl sm:text-4xl font-bold text-luxury-white">
                ${Number(product.price).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
              </span>
              <span className="text-xs text-luxury-muted uppercase tracking-widest">
                Complimentary Insured Freight
              </span>
            </div>

            {/* Description */}
            <div className="space-y-3">
              <h4 className="text-xs font-semibold uppercase tracking-widest text-luxury-gold">
                Provenance &amp; Specifications
              </h4>
              <p className="text-sm text-luxury-muted leading-relaxed">
                {product.description ||
                  'Manufactured with uncompromising precision from select composite materials. Every unit is inspected for acoustic balance, structural integrity, and electronic excellence prior to acquisition.'}
              </p>
            </div>

            {/* Guarantee Pillars */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 py-4 bg-luxury-surface/50 border border-luxury-border p-4 rounded-sm text-xs text-luxury-muted">
              <div className="flex items-center gap-3">
                <ShieldCheck size={18} className="text-luxury-gold shrink-0" />
                <span>100% Certified Authenticity</span>
              </div>
              <div className="flex items-center gap-3">
                <Truck size={18} className="text-luxury-gold shrink-0" />
                <span>Global Insured White-Glove Transport</span>
              </div>
              <div className="flex items-center gap-3">
                <RotateCcw size={18} className="text-luxury-gold shrink-0" />
                <span>30-Day Discretionary Returns</span>
              </div>
              <div className="flex items-center gap-3">
                <CheckCircle size={18} className="text-luxury-gold shrink-0" />
                <span>Spring Boot 3 Transaction Engine</span>
              </div>
            </div>
          </div>

          {/* Action Deck */}
          <div className="space-y-4 pt-6 border-t border-luxury-border">
            <div className="flex flex-col sm:flex-row items-stretch sm:items-center gap-3">
              {/* Quantity Picker */}
              <div className="flex items-center justify-between border border-luxury-border rounded-sm bg-luxury-surface px-2">
                <button
                  type="button"
                  onClick={() => setQuantity((q) => Math.max(1, q - 1))}
                  disabled={quantity <= 1 || isOutOfStock}
                  className="px-3 py-3 text-luxury-muted hover:text-luxury-white disabled:opacity-30 text-base"
                >
                  -
                </button>
                <span className="px-3 py-3 text-sm font-bold text-luxury-white min-w-[3rem] text-center">
                  {quantity}
                </span>
                <button
                  type="button"
                  onClick={() => setQuantity((q) => Math.min(product.stockQuantity || 99, q + 1))}
                  disabled={quantity >= product.stockQuantity || isOutOfStock}
                  className="px-3 py-3 text-luxury-muted hover:text-luxury-white disabled:opacity-30 text-base"
                >
                  +
                </button>
              </div>

              {/* Add to Bag */}
              <Button
                variant="dark"
                size="lg"
                onClick={handleAddToCart}
                disabled={isOutOfStock}
                isLoading={isAdding}
                className="flex-1 py-3.5 border-luxury-gold/50 hover:bg-luxury-gold hover:text-luxury-black transition-colors"
                leftIcon={<ShoppingBag size={18} />}
              >
                {isOutOfStock ? 'Sold Out' : 'Add to Shopping Bag'}
              </Button>

              {/* Buy Now Direct Checkout */}
              <Button
                variant="gold"
                size="lg"
                onClick={handleBuyNow}
                disabled={isOutOfStock}
                className="flex-1 py-3.5"
              >
                Buy Now
              </Button>

              {/* Wishlist Toggle Button */}
              <button
                type="button"
                onClick={() => toggleWishlist(product)}
                className={`p-3.5 rounded-sm border transition-all flex items-center justify-center shrink-0 ${
                  isSaved
                    ? 'bg-luxury-gold text-luxury-black border-luxury-gold shadow-gold-glow'
                    : 'bg-luxury-surface border-luxury-border text-luxury-muted hover:text-luxury-gold'
                }`}
                title={isSaved ? 'Remove from wishlist' : 'Save to private wishlist'}
              >
                <Heart size={20} fill={isSaved ? 'currentColor' : 'none'} />
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Verified Reviews Section */}
      <section id="reviews" className="py-16 border-b border-luxury-border">
        <div className="flex flex-col md:flex-row md:items-end justify-between mb-10 pb-4 border-b border-luxury-border gap-4">
          <div>
            <span className="text-xs font-semibold tracking-luxury-widest uppercase text-luxury-gold">
              Client Feedback
            </span>
            <h2 className="font-serif text-3xl font-bold text-luxury-white mt-1">
              Verified Patron Reviews
            </h2>
          </div>
          <div className="flex items-center gap-3">
            <span className="text-3xl font-serif font-bold text-luxury-gold">
              {reviewsData?.averageRating ? Number(reviewsData.averageRating).toFixed(1) : '5.0'}
            </span>
            <div className="text-xs text-luxury-muted">
              <div className="flex items-center text-luxury-gold">
                {[1, 2, 3, 4, 5].map((s) => (
                  <Star key={s} size={14} fill="currentColor" />
                ))}
              </div>
              <span>Based on {reviewsData?.totalReviews || 0} verified acquisitions</span>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-10">
          {/* Review List */}
          <div className="lg:col-span-2 space-y-6">
            {reviewsData && reviewsData.reviews && reviewsData.reviews.content.length > 0 ? (
              reviewsData.reviews.content.map((rev) => (
                <div key={rev.id} className="bg-luxury-surface border border-luxury-border p-6 rounded-sm space-y-3">
                  <div className="flex items-center justify-between">
                    <div>
                      <h4 className="font-serif font-bold text-sm text-luxury-white">{rev.userName}</h4>
                      <span className="text-[10px] uppercase tracking-wider text-luxury-gold font-semibold">Verified Patron</span>
                    </div>
                    <div className="flex text-luxury-gold">
                      {Array.from({ length: rev.rating }).map((_, i) => (
                        <Star key={i} size={13} fill="currentColor" />
                      ))}
                    </div>
                  </div>
                  <p className="text-xs text-luxury-muted leading-relaxed">{rev.comment}</p>
                  <span className="text-[10px] text-luxury-subtle block">
                    {new Date(rev.createdAt).toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' })}
                  </span>
                </div>
              ))
            ) : (
              <div className="text-center py-12 bg-luxury-surface border border-luxury-border rounded-sm text-luxury-muted text-xs">
                No customer reviews submitted for this specific piece yet. Be the first verified patron to review it.
              </div>
            )}
          </div>

          {/* Submit Review Card */}
          <div className="bg-luxury-surface border border-luxury-border p-6 rounded-sm h-fit space-y-4">
            <div className="flex items-center gap-2 text-xs font-semibold uppercase tracking-widest text-luxury-gold">
              <MessageSquare size={14} />
              <span>Submit Patron Review</span>
            </div>

            {isAuthenticated ? (
              <form onSubmit={handleReviewSubmit} className="space-y-4">
                <div>
                  <label className="text-xs text-luxury-muted block mb-1">Your Rating</label>
                  <div className="flex items-center gap-2">
                    {[1, 2, 3, 4, 5].map((star) => (
                      <button
                        key={star}
                        type="button"
                        onClick={() => setNewRating(star)}
                        className={`p-1 transition-colors ${
                          newRating >= star ? 'text-luxury-gold' : 'text-stone-600'
                        }`}
                      >
                        <Star size={20} fill={newRating >= star ? 'currentColor' : 'none'} />
                      </button>
                    ))}
                    <span className="text-xs font-bold text-luxury-white ml-2">{newRating} Stars</span>
                  </div>
                </div>

                <div>
                  <label className="text-xs text-luxury-muted block mb-1">Commentary</label>
                  <textarea
                    rows={4}
                    value={newComment}
                    onChange={(e) => setNewComment(e.target.value)}
                    placeholder="Describe your tactile, acoustic, or performance experience with this artifact..."
                    className="w-full bg-luxury-elevated border border-luxury-border rounded-sm p-3 text-xs text-luxury-white placeholder:text-luxury-subtle focus:outline-none focus:border-luxury-gold"
                    required
                  />
                </div>

                <Button
                  type="submit"
                  variant="gold"
                  fullWidth
                  size="sm"
                  isLoading={isSubmittingReview}
                >
                  Publish Verified Review
                </Button>
              </form>
            ) : (
              <div className="text-center py-6 text-xs text-luxury-muted space-y-3">
                <p>Only verified patrons with private accounts can submit authenticated reviews.</p>
                <Link to={`/auth?redirect=/products/${product.id}`}>
                  <Button variant="outline" size="sm" fullWidth>
                    Sign In to Review
                  </Button>
                </Link>
              </div>
            )}
          </div>
        </div>
      </section>

      {/* Related Products Showcase */}
      {relatedProducts.length > 0 && (
        <section className="py-16">
          <div className="flex items-baseline justify-between mb-8 border-b border-luxury-border pb-3">
            <div>
              <span className="text-xs font-semibold tracking-luxury-widest uppercase text-luxury-gold">
                Complementary Curations
              </span>
              <h2 className="font-serif text-2xl font-bold text-luxury-white mt-1">
                You May Also Appreciate
              </h2>
            </div>
            <Link to={`/products?categoryId=${product.categoryId}`} className="text-xs text-luxury-gold font-semibold uppercase tracking-widest hover:text-luxury-gold-light">
              View Department
            </Link>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {relatedProducts.map((rel) => (
              <ProductCard key={rel.id} product={rel} />
            ))}
          </div>
        </section>
      )}
    </div>
  );
};
