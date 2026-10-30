import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { Wishlist, WishlistItem, Product } from '../types';
import { wishlistService } from '../services/wishlistService';
import { useAuth } from './AuthContext';
import { useToast } from './ToastContext';

interface WishlistContextType {
  wishlist: Wishlist | null;
  items: WishlistItem[];
  wishlistCount: number;
  isLoading: boolean;
  isInWishlist: (productId: number) => boolean;
  toggleWishlist: (product: Product) => Promise<void>;
  removeItem: (productId: number) => Promise<void>;
  clearWishlist: () => Promise<void>;
  refreshWishlist: () => Promise<void>;
}

const WishlistContext = createContext<WishlistContextType | undefined>(undefined);

export const WishlistProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated } = useAuth();
  const { showToast } = useToast();

  const [wishlist, setWishlist] = useState<Wishlist | null>(null);
  const [guestWishlist, setGuestWishlist] = useState<WishlistItem[]>(() => {
    const saved = localStorage.getItem('jpbazaar_guest_wishlist');
    return saved ? JSON.parse(saved) : [];
  });
  const [isLoading, setIsLoading] = useState<boolean>(false);

  const loadServerWishlist = useCallback(async () => {
    setIsLoading(true);
    try {
      const data = await wishlistService.getWishlist();
      setWishlist(data);
    } catch {
      setWishlist(null);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    if (isAuthenticated) {
      loadServerWishlist();
    } else {
      setWishlist(null);
    }
  }, [isAuthenticated, loadServerWishlist]);

  const items = isAuthenticated ? wishlist?.items || [] : guestWishlist;
  const wishlistCount = items.length;

  const isInWishlist = useCallback(
    (productId: number) => items.some((item) => item.productId === productId),
    [items]
  );

  const toggleWishlist = async (product: Product) => {
    const isPresent = isInWishlist(product.id);

    if (isAuthenticated) {
      setIsLoading(true);
      try {
        if (isPresent) {
          const updated = await wishlistService.removeFromWishlist(product.id);
          setWishlist(updated);
          showToast(`${product.name} removed from your wishlist.`, 'info');
        } else {
          const updated = await wishlistService.addToWishlist({ productId: product.id });
          setWishlist(updated);
          showToast(`${product.name} added to your wishlist.`, 'success');
        }
      } catch (err: any) {
        showToast(err.message || 'Failed to update wishlist.', 'error');
      } finally {
        setIsLoading(false);
      }
    } else {
      // Guest mode
      if (isPresent) {
        const next = guestWishlist.filter((item) => item.productId !== product.id);
        setGuestWishlist(next);
        localStorage.setItem('jpbazaar_guest_wishlist', JSON.stringify(next));
        showToast(`${product.name} removed from saved items.`, 'info');
      } else {
        const newItem: WishlistItem = {
          id: Date.now(),
          productId: product.id,
          productName: product.name,
          price: product.price,
          inStock: product.stockQuantity > 0,
          imageUrl: product.imageUrls?.[0] || product.images?.[0]?.imageUrl || '',
          addedAt: new Date().toISOString(),
        };
        const next = [...guestWishlist, newItem];
        setGuestWishlist(next);
        localStorage.setItem('jpbazaar_guest_wishlist', JSON.stringify(next));
        showToast(`${product.name} saved to private wishlist.`, 'success');
      }
    }
  };

  const removeItem = async (productId: number) => {
    if (isAuthenticated) {
      try {
        const updated = await wishlistService.removeFromWishlist(productId);
        setWishlist(updated);
        showToast('Item removed from wishlist.', 'info');
      } catch (err: any) {
        showToast(err.message || 'Failed to remove item.', 'error');
      }
    } else {
      const next = guestWishlist.filter((item) => item.productId !== productId);
      setGuestWishlist(next);
      localStorage.setItem('jpbazaar_guest_wishlist', JSON.stringify(next));
      showToast('Item removed from saved items.', 'info');
    }
  };

  const clearWishlist = async () => {
    if (isAuthenticated) {
      try {
        await wishlistService.clearWishlist();
        setWishlist(null);
        showToast('Wishlist cleared.', 'info');
      } catch (err: any) {
        showToast(err.message || 'Failed to clear wishlist.', 'error');
      }
    } else {
      setGuestWishlist([]);
      localStorage.removeItem('jpbazaar_guest_wishlist');
      showToast('Saved items cleared.', 'info');
    }
  };

  return (
    <WishlistContext.Provider
      value={{
        wishlist,
        items,
        wishlistCount,
        isLoading,
        isInWishlist,
        toggleWishlist,
        removeItem,
        clearWishlist,
        refreshWishlist: loadServerWishlist,
      }}
    >
      {children}
    </WishlistContext.Provider>
  );
};

export const useWishlist = (): WishlistContextType => {
  const context = useContext(WishlistContext);
  if (!context) {
    throw new Error('useWishlist must be used within a WishlistProvider');
  }
  return context;
};
