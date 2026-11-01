import React, { createContext, useContext, useState, useEffect, useCallback, useMemo } from 'react';
import { Cart, CartItem } from '../types';
import { cartService } from '../services/cartService';
import { useAuth } from './AuthContext';
import { useToast } from './ToastContext';

interface CartContextType {
  cart: Cart | null;
  guestItems: CartItem[];
  isLoading: boolean;
  isDrawerOpen: boolean;
  openDrawer: () => void;
  closeDrawer: () => void;
  toggleDrawer: () => void;
  addToCart: (productId: number, quantity?: number, productName?: string, price?: number, imageUrl?: string) => Promise<void>;
  updateQuantity: (itemId: number, quantity: number) => Promise<void>;
  removeItem: (itemId: number) => Promise<void>;
  clearCart: () => Promise<void>;
  totalCount: number;
  totalAmount: number;
}

const CartContext = createContext<CartContextType | undefined>(undefined);

export const CartProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated } = useAuth();
  const { showToast } = useToast();

  const [cart, setCart] = useState<Cart | null>(null);
  const [guestItems, setGuestItems] = useState<CartItem[]>(() => {
    const saved = localStorage.getItem('jpbazaar_guest_cart');
    return saved ? JSON.parse(saved) : [];
  });
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [isDrawerOpen, setIsDrawerOpen] = useState<boolean>(false);

  const openDrawer = useCallback(() => setIsDrawerOpen(true), []);
  const closeDrawer = useCallback(() => setIsDrawerOpen(false), []);
  const toggleDrawer = useCallback(() => setIsDrawerOpen((prev) => !prev), []);

  // Sync / Load Cart
  const loadServerCart = useCallback(async () => {
    setIsLoading(true);
    try {
      const data = await cartService.getCart();
      setCart(data);
    } catch {
      setCart(null);
    } finally {
      setIsLoading(false);
    }
  }, []);

  // When auth state changes, sync guest items if any, then load server cart
  useEffect(() => {
    if (isAuthenticated) {
      const syncGuestItems = async () => {
        const saved = localStorage.getItem('jpbazaar_guest_cart');
        const itemsToSync: CartItem[] = saved ? JSON.parse(saved) : [];
        if (itemsToSync.length > 0) {
          for (const item of itemsToSync) {
            try {
              await cartService.addToCart({ productId: item.productId, quantity: item.quantity });
            } catch {}
          }
          localStorage.removeItem('jpbazaar_guest_cart');
          setGuestItems([]);
        }
        await loadServerCart();
      };
      syncGuestItems();
    } else {
      setCart(null);
    }
  }, [isAuthenticated, loadServerCart]);

  // Save guest items to localStorage
  useEffect(() => {
    if (!isAuthenticated) {
      localStorage.setItem('jpbazaar_guest_cart', JSON.stringify(guestItems));
    }
  }, [guestItems, isAuthenticated]);

  const addToCart = useCallback(async (
    productId: number,
    quantity = 1,
    productName = 'Product',
    price = 0,
    imageUrl?: string
  ) => {
    if (isAuthenticated) {
      setIsLoading(true);
      try {
        const updated = await cartService.addToCart({ productId, quantity });
        setCart(updated);
        showToast(`Added ${quantity > 1 ? quantity + 'x ' : ''}"${productName}" to your cart.`, 'success');
      } catch (err: any) {
        showToast(err.message || 'Failed to add item to cart', 'error');
      } finally {
        setIsLoading(false);
      }
    } else {
      // Guest local cart
      setGuestItems((prev) => {
        const existing = prev.find((i) => i.productId === productId);
        if (existing) {
          return prev.map((i) =>
            i.productId === productId
              ? { ...i, quantity: i.quantity + quantity, subtotal: (i.quantity + quantity) * i.price }
              : i
          );
        }
        return [
          ...prev,
          {
            id: Date.now(),
            productId,
            productName,
            sku: 'SKU-' + productId,
            price,
            quantity,
            subtotal: price * quantity,
            imageUrl,
          },
        ];
      });
      showToast(`Added ${quantity > 1 ? quantity + 'x ' : ''}"${productName}" to your cart.`, 'success');
    }
  }, [isAuthenticated, showToast]);

  const updateQuantity = useCallback(async (itemId: number, quantity: number) => {
    if (quantity <= 0) {
      if (isAuthenticated) {
        setIsLoading(true);
        try {
          const updated = await cartService.removeItem(itemId);
          setCart(updated);
          showToast('Item removed from cart', 'info');
        } catch (err: any) {
          showToast(err.message || 'Failed to remove item', 'error');
        } finally {
          setIsLoading(false);
        }
      } else {
        setGuestItems((prev) => prev.filter((i) => i.id !== itemId));
        showToast('Item removed from cart', 'info');
      }
      return;
    }

    if (isAuthenticated) {
      setIsLoading(true);
      try {
        const updated = await cartService.updateItemQuantity(itemId, { quantity });
        setCart(updated);
      } catch (err: any) {
        showToast(err.message || 'Failed to update quantity', 'error');
      } finally {
        setIsLoading(false);
      }
    } else {
      setGuestItems((prev) =>
        prev.map((i) =>
          i.id === itemId
            ? { ...i, quantity, subtotal: quantity * i.price }
            : i
        )
      );
    }
  }, [isAuthenticated, showToast]);

  const removeItem = useCallback(async (itemId: number) => {
    if (isAuthenticated) {
      setIsLoading(true);
      try {
        const updated = await cartService.removeItem(itemId);
        setCart(updated);
        showToast('Item removed from cart', 'info');
      } catch (err: any) {
        showToast(err.message || 'Failed to remove item', 'error');
      } finally {
        setIsLoading(false);
      }
    } else {
      setGuestItems((prev) => prev.filter((i) => i.id !== itemId));
      showToast('Item removed from cart', 'info');
    }
  }, [isAuthenticated, showToast]);

  const clearCart = useCallback(async () => {
    if (isAuthenticated) {
      try {
        await cartService.clearCart();
        setCart(null);
      } catch {}
    } else {
      setGuestItems([]);
      localStorage.removeItem('jpbazaar_guest_cart');
    }
  }, [isAuthenticated]);

  const activeItems = isAuthenticated ? cart?.items || [] : guestItems;
  const totalCount = activeItems.reduce((sum, item) => sum + item.quantity, 0);
  const totalAmount = activeItems.reduce((sum, item) => sum + item.subtotal, 0);

  const value = useMemo(() => ({
    cart,
    guestItems,
    isLoading,
    isDrawerOpen,
    openDrawer,
    closeDrawer,
    toggleDrawer,
    addToCart,
    updateQuantity,
    removeItem,
    clearCart,
    totalCount,
    totalAmount,
  }), [
    cart,
    guestItems,
    isLoading,
    isDrawerOpen,
    openDrawer,
    closeDrawer,
    toggleDrawer,
    addToCart,
    updateQuantity,
    removeItem,
    clearCart,
    totalCount,
    totalAmount,
  ]);

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
};

export const useCart = (): CartContextType => {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error('useCart must be used within a CartProvider');
  }
  return context;
};
