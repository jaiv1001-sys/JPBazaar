import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../../context/CartContext';
import { useAuth } from '../../context/AuthContext';
import { Drawer } from '../common/Drawer';
import { Button } from '../common/Button';
import { Trash2, Plus, Minus, ArrowRight, ShoppingBag } from 'lucide-react';

export const CartDrawer: React.FC = () => {
  const { isDrawerOpen, closeDrawer, cart, guestItems, updateQuantity, removeItem, totalCount, totalAmount } = useCart();
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const items = isAuthenticated ? cart?.items || [] : guestItems;

  const handleCheckout = () => {
    closeDrawer();
    navigate('/checkout');
  };

  return (
    <Drawer
      isOpen={isDrawerOpen}
      onClose={closeDrawer}
      title={`Shopping Bag (${totalCount} ${totalCount === 1 ? 'Item' : 'Items'})`}
      width="md"
    >
      {items.length === 0 ? (
        <div className="h-full flex flex-col items-center justify-center text-center py-16 px-4">
          <div className="w-16 h-16 rounded-full bg-luxury-elevated border border-luxury-border flex items-center justify-center text-luxury-gold mb-4">
            <ShoppingBag size={28} />
          </div>
          <h4 className="font-serif text-lg font-bold text-luxury-white">Your Shopping Bag is Empty</h4>
          <p className="text-xs text-luxury-muted max-w-xs mt-2 mb-6">
            Explore our curated collections of precision audio, high-performance computing, and luxury apparel.
          </p>
          <Button
            variant="gold"
            size="sm"
            onClick={() => {
              closeDrawer();
              navigate('/products');
            }}
          >
            Explore Collections
          </Button>
        </div>
      ) : (
        <div className="flex flex-col h-full justify-between gap-6">
          {/* Item List */}
          <div className="space-y-4 divide-y divide-luxury-border/50 overflow-y-auto pr-1">
            {items.map((item) => (
              <div key={item.id} className="pt-4 first:pt-0 flex gap-4">
                {/* Thumbnail */}
                <img
                  src={
                    item.imageUrl ||
                    'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=300&q=80'
                  }
                  alt={item.productName}
                  className="w-20 h-20 object-cover rounded-sm bg-luxury-elevated border border-luxury-border shrink-0"
                />

                {/* Info & Quantity */}
                <div className="flex-1 flex flex-col justify-between">
                  <div className="flex justify-between items-start gap-2">
                    <h5 className="text-xs font-bold text-luxury-white line-clamp-1">{item.productName}</h5>
                    <button
                      onClick={() => removeItem(item.id)}
                      className="text-luxury-subtle hover:text-luxury-danger transition-colors p-1"
                      title="Remove Item"
                    >
                      <Trash2 size={14} />
                    </button>
                  </div>

                  <p className="text-xs text-luxury-muted font-mono">{item.sku}</p>

                  <div className="flex justify-between items-center mt-2">
                    <span className="text-sm font-bold text-luxury-gold">
                      ${Number(item.subtotal ?? (Number(item.price ?? item.unitPrice ?? 0) * item.quantity)).toFixed(2)}
                    </span>

                    {/* Quantity Selector */}
                    <div className="flex items-center border border-luxury-border rounded-sm bg-luxury-black">
                      <button
                        onClick={() => updateQuantity(item.id, item.quantity - 1)}
                        className="p-1 hover:text-luxury-gold text-luxury-muted transition-colors"
                        aria-label="Decrease quantity"
                      >
                        <Minus size={12} />
                      </button>
                      <span className="text-xs font-bold px-2.5 text-luxury-white select-none">
                        {item.quantity}
                      </span>
                      <button
                        onClick={() => updateQuantity(item.id, item.quantity + 1)}
                        className="p-1 hover:text-luxury-gold text-luxury-muted transition-colors"
                        aria-label="Increase quantity"
                      >
                        <Plus size={12} />
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>

          {/* Footer Summary & Checkout */}
          <div className="border-t border-luxury-border pt-6 space-y-4">
            <div className="space-y-2 text-xs">
              <div className="flex justify-between text-luxury-muted">
                <span>Subtotal</span>
                <span className="text-luxury-white font-bold">${totalAmount.toFixed(2)}</span>
              </div>
              <div className="flex justify-between text-luxury-muted">
                <span>Insured Express Shipping</span>
                <span className="text-luxury-gold font-bold">
                  {totalAmount >= 150 ? 'COMPLIMENTARY' : '$15.00'}
                </span>
              </div>
            </div>

            <div className="flex justify-between items-center border-t border-luxury-border pt-4">
              <span className="font-serif font-bold text-sm text-luxury-white">Estimated Total</span>
              <span className="font-serif font-bold text-xl text-luxury-gold">
                ${(totalAmount >= 150 ? totalAmount : totalAmount + 15).toFixed(2)}
              </span>
            </div>

            <Button
              variant="gold"
              className="w-full"
              size="lg"
              rightIcon={<ArrowRight size={18} />}
              onClick={handleCheckout}
            >
              Proceed to Checkout
            </Button>
          </div>
        </div>
      )}
    </Drawer>
  );
};
