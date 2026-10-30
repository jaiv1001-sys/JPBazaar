import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Address } from '../../types';
import { addressService } from '../../services/addressService';
import { useAuth } from '../../context/AuthContext';
import { useWishlist } from '../../context/WishlistContext';
import { useToast } from '../../context/ToastContext';
import { AddressModal } from '../../components/account/AddressModal';
import { Button } from '../../components/common/Button';
import { Card } from '../../components/common/Card';
import { Badge } from '../../components/common/Badge';
import { Skeleton } from '../../components/common/Skeleton';
import {
  User as UserIcon,
  Mail,
  Phone,
  ShieldCheck,
  Package,
  Heart,
  MapPin,
  LogOut,
  Plus,
  Trash2,
  Edit2,
  Check,
} from 'lucide-react';

export const AccountPage: React.FC = () => {
  const { user, logout, isAdmin } = useAuth();
  const { wishlistCount } = useWishlist();
  const { showToast } = useToast();

  const [addresses, setAddresses] = useState<Address[]>([]);
  const [isLoadingAddresses, setIsLoadingAddresses] = useState<boolean>(true);

  // Address Modal State
  const [isAddressModalOpen, setIsAddressModalOpen] = useState<boolean>(false);
  const [addressToEdit, setAddressToEdit] = useState<Address | null>(null);

  useEffect(() => {
    addressService.getAddresses()
      .then(setAddresses)
      .catch((err) => console.error('Failed to load addresses:', err))
      .finally(() => setIsLoadingAddresses(false));
  }, []);

  const handleAddressSaved = (saved: Address) => {
    setAddresses((prev) => {
      const exists = prev.some((a) => a.id === saved.id);
      if (exists) {
        return prev.map((a) => (a.id === saved.id ? saved : a));
      }
      return [...prev, saved];
    });
  };

  const handleSetDefault = async (id: number) => {
    try {
      const updated = await addressService.setDefaultAddress(id);
      setAddresses((prev) =>
        prev.map((a) => ({
          ...a,
          isDefault: a.id === updated.id,
        }))
      );
      showToast('Primary delivery residence updated.', 'success');
    } catch (err: any) {
      showToast(err.message || 'Failed to designate default residence.', 'error');
    }
  };

  const handleDeleteAddress = async (id: number) => {
    try {
      await addressService.deleteAddress(id);
      setAddresses((prev) => prev.filter((a) => a.id !== id));
      showToast('Delivery residence removed.', 'info');
    } catch (err: any) {
      showToast(err.message || 'Failed to remove address.', 'error');
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 w-full flex-1 flex flex-col">
      {/* Header Bar */}
      <div className="border-b border-luxury-border pb-6 mb-8 flex flex-col sm:flex-row sm:items-end justify-between gap-4">
        <div>
          <span className="text-xs font-semibold tracking-luxury-widest uppercase text-luxury-gold">
            Private Concierge
          </span>
          <h1 className="font-serif text-3xl sm:text-4xl font-bold text-luxury-white mt-1">
            Client Sanctuary
          </h1>
        </div>

        <div className="flex items-center gap-3">
          {isAdmin && (
            <Link to="/admin">
              <Button variant="outline" size="sm" className="border-luxury-gold/50 text-luxury-gold" leftIcon={<ShieldCheck size={16} />}>
                Executive Portal
              </Button>
            </Link>
          )}
          <Button variant="ghost" size="sm" onClick={logout} className="text-rose-400 hover:text-rose-300" leftIcon={<LogOut size={16} />}>
            Sign Out
          </Button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Left Column: Client Credentials Profile Card */}
        <div className="space-y-6">
          <Card variant="elevated" className="p-6">
            <div className="flex items-center gap-4 mb-6">
              <div className="w-14 h-14 rounded-full bg-luxury-gold/10 border border-luxury-gold/30 flex items-center justify-center text-luxury-gold text-2xl font-serif font-bold">
                {user?.firstName?.charAt(0) || 'U'}
              </div>
              <div>
                <h3 className="font-serif text-lg font-bold text-luxury-white">
                  {user?.firstName} {user?.lastName}
                </h3>
                <span className="inline-block px-2 py-0.5 mt-1 rounded text-[10px] uppercase font-bold tracking-wider bg-luxury-surface border border-luxury-border text-luxury-gold">
                  {user?.role || 'CUSTOMER'}
                </span>
              </div>
            </div>

            <div className="space-y-3 pt-4 border-t border-luxury-border text-xs text-luxury-muted">
              <div className="flex items-center gap-3">
                <Mail size={16} className="text-luxury-gold shrink-0" />
                <span className="truncate text-luxury-white">{user?.email}</span>
              </div>
              {user?.phone && (
                <div className="flex items-center gap-3">
                  <Phone size={16} className="text-luxury-gold shrink-0" />
                  <span className="text-luxury-white">{user.phone}</span>
                </div>
              )}
              <div className="flex items-center gap-3">
                <UserIcon size={16} className="text-luxury-gold shrink-0" />
                <span>Patron Identification #{user?.id}</span>
              </div>
            </div>
          </Card>

          {/* Quick Navigation Cards */}
          <div className="grid grid-cols-1 gap-3">
            <Link to="/account/orders" className="group">
              <div className="bg-luxury-surface border border-luxury-border p-4 rounded-sm hover:border-luxury-gold/50 transition-all flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <Package size={20} className="text-luxury-gold group-hover:scale-110 transition-transform" />
                  <div>
                    <h4 className="font-serif font-bold text-sm text-luxury-white group-hover:text-luxury-gold transition-colors">
                      Acquisition Archive
                    </h4>
                    <p className="text-[11px] text-luxury-muted">Consignment history &amp; tracking</p>
                  </div>
                </div>
                <span className="text-xs text-luxury-gold font-bold">View →</span>
              </div>
            </Link>

            <Link to="/wishlist" className="group">
              <div className="bg-luxury-surface border border-luxury-border p-4 rounded-sm hover:border-luxury-gold/50 transition-all flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <Heart size={20} className="text-luxury-gold group-hover:scale-110 transition-transform" />
                  <div>
                    <h4 className="font-serif font-bold text-sm text-luxury-white group-hover:text-luxury-gold transition-colors">
                      Curated Wishlist
                    </h4>
                    <p className="text-[11px] text-luxury-muted">{wishlistCount} artifacts reserved</p>
                  </div>
                </div>
                <span className="text-xs text-luxury-gold font-bold">View →</span>
              </div>
            </Link>
          </div>
        </div>

        {/* Right Column: Registered Delivery Residences (Address Book) */}
        <div className="lg:col-span-2 space-y-6">
          <div className="flex items-center justify-between border-b border-luxury-border pb-3">
            <div>
              <h2 className="font-serif text-xl font-bold text-luxury-white">
                Delivery Residences
              </h2>
              <p className="text-xs text-luxury-muted mt-0.5">
                Manage your registered destinations for insured white-glove transport.
              </p>
            </div>
            <Button
              variant="outline"
              size="sm"
              onClick={() => {
                setAddressToEdit(null);
                setIsAddressModalOpen(true);
              }}
              leftIcon={<Plus size={14} />}
            >
              Add Destination
            </Button>
          </div>

          {isLoadingAddresses ? (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Skeleton className="h-36 w-full" />
              <Skeleton className="h-36 w-full" />
            </div>
          ) : addresses.length === 0 ? (
            <div className="text-center py-12 bg-luxury-surface border border-luxury-border rounded-sm p-6">
              <MapPin size={28} className="text-luxury-gold mx-auto mb-2" />
              <h4 className="font-serif font-bold text-base text-luxury-white">No Registered Addresses</h4>
              <p className="text-xs text-luxury-muted max-w-sm mx-auto mt-1 mb-4">
                Register a primary address for seamless one-click checkout.
              </p>
              <Button
                variant="gold"
                size="sm"
                onClick={() => {
                  setAddressToEdit(null);
                  setIsAddressModalOpen(true);
                }}
              >
                Add Residence
              </Button>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {addresses.map((addr) => (
                <div
                  key={addr.id}
                  className="bg-luxury-surface border border-luxury-border p-5 rounded-sm flex flex-col justify-between space-y-4 hover:border-luxury-gold/40 transition-all"
                >
                  <div>
                    <div className="flex items-center justify-between mb-2">
                      {addr.isDefault ? (
                        <Badge variant="gold" size="sm">Primary Dispatch</Badge>
                      ) : (
                        <span className="text-[10px] text-luxury-muted uppercase tracking-wider font-semibold">
                          Alternative Destination
                        </span>
                      )}
                    </div>
                    <p className="font-medium text-sm text-luxury-white">{addr.addressLine}</p>
                    <p className="text-xs text-luxury-muted mt-0.5">
                      {addr.city}, {addr.state} {addr.postalCode}
                    </p>
                    <p className="text-xs text-luxury-subtle">{addr.country}</p>
                  </div>

                  <div className="flex items-center justify-between pt-3 border-t border-luxury-border/60 text-xs">
                    {!addr.isDefault ? (
                      <button
                        type="button"
                        onClick={() => handleSetDefault(addr.id)}
                        className="text-luxury-gold hover:underline font-semibold flex items-center gap-1"
                      >
                        <Check size={13} /> Set as Primary
                      </button>
                    ) : (
                      <span className="text-[11px] text-luxury-muted">Default Selected</span>
                    )}

                    <div className="flex items-center gap-2">
                      <button
                        type="button"
                        onClick={() => {
                          setAddressToEdit(addr);
                          setIsAddressModalOpen(true);
                        }}
                        className="p-1 text-luxury-muted hover:text-luxury-white"
                        title="Edit address"
                      >
                        <Edit2 size={14} />
                      </button>
                      <button
                        type="button"
                        onClick={() => handleDeleteAddress(addr.id)}
                        className="p-1 text-luxury-muted hover:text-rose-400"
                        title="Delete address"
                      >
                        <Trash2 size={14} />
                      </button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Address Edit/Create Modal */}
      <AddressModal
        isOpen={isAddressModalOpen}
        onClose={() => {
          setIsAddressModalOpen(false);
          setAddressToEdit(null);
        }}
        onSaved={handleAddressSaved}
        addressToEdit={addressToEdit}
      />
    </div>
  );
};
