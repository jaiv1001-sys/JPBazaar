import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Address, PaymentMethod, Order, Payment } from '../../types';
import { addressService } from '../../services/addressService';
import { orderService } from '../../services/orderService';
import { paymentService } from '../../services/paymentService';
import { razorpayService } from '../../services/razorpayService';
import { useCart } from '../../context/CartContext';
import { useToast } from '../../context/ToastContext';
import { AddressModal } from '../../components/account/AddressModal';
import { ErrorBoundary } from '../../components/common/ErrorBoundary';
import { Button } from '../../components/common/Button';
import { Input } from '../../components/common/Input';
import { Skeleton } from '../../components/common/Skeleton';
import {
  MapPin,
  CheckCircle,
  CreditCard,
  ShieldCheck,
  Truck,
  ArrowRight,
  ArrowLeft,
  AlertTriangle,
  Lock,
  Plus,
  Building,
  Sparkles,
} from 'lucide-react';

export const CheckoutPage: React.FC = () => {
  return (
    <ErrorBoundary
      fallbackTitle="Checkout Concierge Disruption"
      fallbackMessage="An unexpected issue occurred while rendering your checkout review. You can safely reload the screen or return to your bag."
    >
      <CheckoutContent />
    </ErrorBoundary>
  );
};

const CheckoutContent: React.FC = () => {
  const { cart, guestItems, totalAmount, totalCount, clearCart } = useCart();
  const { showToast } = useToast();
  const navigate = useNavigate();

  // Step state: 1 (Address), 2 (Review), 3 (Payment), 4 (Confirmation)
  const [currentStep, setCurrentStep] = useState<number>(1);

  // Address state
  const [addresses, setAddresses] = useState<Address[]>([]);
  const [selectedAddressId, setSelectedAddressId] = useState<number | null>(null);
  const [isLoadingAddresses, setIsLoadingAddresses] = useState<boolean>(true);
  const [isAddressModalOpen, setIsAddressModalOpen] = useState<boolean>(false);

  // Order Review state
  const [deliveryNotes, setDeliveryNotes] = useState<string>('');

  // Payment state
  const [gatewayMode, setGatewayMode] = useState<'RAZORPAY' | 'SIMULATED'>('RAZORPAY');
  const [paymentMethod, setPaymentMethod] = useState<PaymentMethod>('CARD');
  const [simulateFailure, setSimulateFailure] = useState<boolean>(false);
  const [isProcessingPayment, setIsProcessingPayment] = useState<boolean>(false);
  const [activeOrder, setActiveOrder] = useState<Order | null>(null);
  const [razorpayError, setRazorpayError] = useState<string | null>(null);

  // Card Inputs Mock
  const [cardName, setCardName] = useState<string>('Alexander Vance');
  const [cardNumber, setCardNumber] = useState<string>('•••• •••• •••• 4242');
  const [cardExpiry, setCardExpiry] = useState<string>('12/28');
  const [cardCvv, setCardCvv] = useState<string>('888');

  // Confirmation state
  const [completedOrder, setCompletedOrder] = useState<Order | null>(null);
  const [completedPayment, setCompletedPayment] = useState<Payment | null>(null);

  // Calculations (Safely guarded against null/undefined or NaN)
  const shippingThreshold = 150;
  const safeTotalAmount = Number(totalAmount || 0);
  const isFreeShipping = safeTotalAmount >= shippingThreshold;
  const shippingFee = safeTotalAmount > 0 ? (isFreeShipping ? 0 : 25) : 0;
  const finalTotal = safeTotalAmount + shippingFee;

  // Active items from server cart with guest cart fallback
  const checkoutItems = (cart?.items && cart.items.length > 0) ? cart.items : (guestItems || []);

  // Load addresses on mount
  useEffect(() => {
    addressService.getAddresses()
      .then((data) => {
        setAddresses(data);
        const defaultAddr = data.find((a) => a.isDefault) || data[0];
        if (defaultAddr) setSelectedAddressId(defaultAddr.id);
      })
      .catch((err) => {
        console.error('Failed to load user addresses:', err);
      })
      .finally(() => {
        setIsLoadingAddresses(false);
      });
  }, []);

  // Redirect if cart is empty and not already confirmed
  useEffect(() => {
    if (currentStep !== 4 && totalCount === 0 && !completedOrder) {
      navigate('/cart');
    }
  }, [currentStep, totalCount, completedOrder, navigate]);

  const handleAddressSaved = (newAddr: Address) => {
    setAddresses((prev) => {
      const exists = prev.some((a) => a.id === newAddr.id);
      if (exists) return prev.map((a) => (a.id === newAddr.id ? newAddr : a));
      return [...prev, newAddr];
    });
    setSelectedAddressId(newAddr.id);
  };

  const handleStep1Next = () => {
    if (!selectedAddressId) {
      showToast('Please select or register a delivery residence.', 'error');
      return;
    }
    window.scrollTo({ top: 0, behavior: 'smooth' });
    setCurrentStep(2);
  };

  const handleStep2Next = () => {
    window.scrollTo({ top: 0, behavior: 'smooth' });
    setCurrentStep(3);
  };

  const handleFinalSettlement = async () => {
    if (!selectedAddressId) {
      showToast('Delivery residence missing.', 'error');
      setCurrentStep(1);
      return;
    }

    setIsProcessingPayment(true);
    try {
      // 1. Atomically place order from cart
      const placedOrder = await orderService.placeOrder({
        addressId: selectedAddressId,
        paymentMethod: paymentMethod,
        notes: deliveryNotes.trim() || undefined,
      });

      // 2. Process simulated payment
      const paymentReceipt = await paymentService.processPayment({
        orderId: placedOrder.id,
        paymentMethod: paymentMethod,
        simulateFailure: simulateFailure,
      });

      // 3. Clear cart from state
      await clearCart();

      setCompletedOrder(placedOrder);
      setCompletedPayment(paymentReceipt);
      setCurrentStep(4);
      showToast('Consignment confirmed and cleared for priority dispatch.', 'success');
    } catch (err: any) {
      showToast(err.message || 'Settlement failed. Please verify credentials or retry.', 'error');
    } finally {
      setIsProcessingPayment(false);
    }
  };

  const handleRazorpaySettlement = async () => {
    if (!selectedAddressId) {
      showToast('Delivery residence missing.', 'error');
      setCurrentStep(1);
      return;
    }

    setIsProcessingPayment(true);
    setRazorpayError(null);

    try {
      // 1. Atomically place or reuse existing in-progress order
      let orderToSettle = activeOrder;
      if (!orderToSettle) {
        orderToSettle = await orderService.placeOrder({
          addressId: selectedAddressId,
          paymentMethod: 'CARD', // Razorpay modal supports Card, UPI, NetBanking
          notes: deliveryNotes.trim() || undefined,
        });
        setActiveOrder(orderToSettle);
      }

      // 2. Initiate Razorpay order on backend
      const rzpOrder = await razorpayService.createOrder(orderToSettle.id);

      // 3. Open Razorpay Checkout modal with gold luxury theme
      await razorpayService.openCheckout(
        rzpOrder,
        async (verifiedPayment) => {
          await clearCart();
          setCompletedOrder(orderToSettle);
          setCompletedPayment(verifiedPayment);
          setCurrentStep(4);
          showToast('Payment verified via Razorpay! Consignment confirmed.', 'success');
          setIsProcessingPayment(false);
        },
        (errMsg) => {
          setRazorpayError(errMsg);
          showToast(errMsg, 'error');
          setIsProcessingPayment(false);
        },
        () => {
          showToast('Payment modal closed. You can retry settlement when ready.', 'info');
          setIsProcessingPayment(false);
        }
      );
    } catch (err: any) {
      const msg = err.message || 'Settlement initiation failed. Please try again.';
      setRazorpayError(msg);
      showToast(msg, 'error');
      setIsProcessingPayment(false);
    }
  };

  const selectedAddress = addresses.find((a) => a.id === selectedAddressId);

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-10 w-full flex-1 flex flex-col">
      {/* Checkout Progress Stepper */}
      <div className="mb-10">
        <div className="flex items-center justify-between max-w-2xl mx-auto relative">
          <div className="absolute top-1/2 left-0 right-0 h-0.5 bg-luxury-border -translate-y-1/2 z-0" />

          {[
            { step: 1, label: 'Delivery Residence' },
            { step: 2, label: 'Consignment Review' },
            { step: 3, label: 'Settlement Protocol' },
            { step: 4, label: 'Confirmation' },
          ].map((s) => {
            const isPassed = currentStep > s.step;
            const isCurrent = currentStep === s.step;

            return (
              <div key={s.step} className="relative z-10 flex flex-col items-center">
                <div
                  className={`w-9 h-9 rounded-full flex items-center justify-center font-bold text-xs transition-all ${
                    isPassed
                      ? 'bg-luxury-gold text-luxury-black'
                      : isCurrent
                      ? 'bg-luxury-gold text-luxury-black ring-4 ring-luxury-gold/20'
                      : 'bg-luxury-surface border border-luxury-border text-luxury-muted'
                  }`}
                >
                  {isPassed ? <CheckCircle size={16} /> : s.step}
                </div>
                <span
                  className={`text-[10px] uppercase tracking-wider font-semibold mt-2 hidden sm:block ${
                    isCurrent ? 'text-luxury-gold' : 'text-luxury-muted'
                  }`}
                >
                  {s.label}
                </span>
              </div>
            );
          })}
        </div>
      </div>

      {/* STEP 1: Delivery Residence Selection */}
      {currentStep === 1 && (
        <div className="space-y-6">
          <div className="border-b border-luxury-border pb-4 flex items-center justify-between">
            <div>
              <span className="text-xs font-semibold tracking-luxury-widest uppercase text-luxury-gold">
                Step 1 of 3
              </span>
              <h2 className="font-serif text-2xl font-bold text-luxury-white mt-1">
                Select Delivery Destination
              </h2>
            </div>
            <Button
              variant="outline"
              size="sm"
              onClick={() => setIsAddressModalOpen(true)}
              leftIcon={<Plus size={14} />}
            >
              Register New Residence
            </Button>
          </div>

          {isLoadingAddresses ? (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Skeleton className="h-32 w-full" />
              <Skeleton className="h-32 w-full" />
            </div>
          ) : addresses.length === 0 ? (
            <div className="text-center py-16 bg-luxury-surface border border-luxury-border rounded-sm p-6">
              <Building size={32} className="text-luxury-gold mx-auto mb-3" />
              <h3 className="font-serif font-bold text-lg text-luxury-white">
                No Registered Residences Found
              </h3>
              <p className="text-xs text-luxury-muted max-w-sm mx-auto mt-1 mb-6">
                Register your primary residence or diplomatic address to proceed with dispatch.
              </p>
              <Button variant="gold" size="sm" onClick={() => setIsAddressModalOpen(true)}>
                Add Delivery Address
              </Button>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {addresses.map((addr) => {
                const isSelected = selectedAddressId === addr.id;
                return (
                  <div
                    key={addr.id}
                    onClick={() => setSelectedAddressId(addr.id)}
                    className={`p-5 rounded-sm border cursor-pointer transition-all flex flex-col justify-between ${
                      isSelected
                        ? 'bg-luxury-surface border-luxury-gold shadow-gold-sm'
                        : 'bg-luxury-surface/50 border-luxury-border hover:border-luxury-gold/40'
                    }`}
                  >
                    <div>
                      <div className="flex items-center justify-between mb-2">
                        <span className="text-xs font-bold text-luxury-gold uppercase tracking-wider flex items-center gap-1.5">
                          <MapPin size={14} />
                          {addr.isDefault ? 'Primary Residence' : 'Alternative Destination'}
                        </span>
                        <div
                          className={`w-4 h-4 rounded-full border flex items-center justify-center ${
                            isSelected
                              ? 'border-luxury-gold bg-luxury-gold text-luxury-black'
                              : 'border-luxury-border'
                          }`}
                        >
                          {isSelected && <div className="w-1.5 h-1.5 rounded-full bg-luxury-black" />}
                        </div>
                      </div>
                      <p className="font-medium text-sm text-luxury-white">{addr.addressLine}</p>
                      <p className="text-xs text-luxury-muted mt-1">
                        {addr.city}, {addr.state} {addr.postalCode}
                      </p>
                      <p className="text-xs text-luxury-subtle">{addr.country}</p>
                    </div>
                  </div>
                );
              })}
            </div>
          )}

          <div className="pt-6 border-t border-luxury-border flex justify-between items-center">
            <Link to="/cart">
              <Button variant="ghost" size="sm" leftIcon={<ArrowLeft size={16} />}>
                Return to Bag
              </Button>
            </Link>
            <Button
              variant="gold"
              size="md"
              onClick={handleStep1Next}
              disabled={!selectedAddressId}
              rightIcon={<ArrowRight size={16} />}
            >
              Continue to Review
            </Button>
          </div>
        </div>
      )}

      {/* STEP 2: Consignment Review & Notes */}
      {currentStep === 2 && (
        <div className="space-y-6">
          <div className="border-b border-luxury-border pb-4">
            <span className="text-xs font-semibold tracking-luxury-widest uppercase text-luxury-gold">
              Step 2 of 3
            </span>
            <h2 className="font-serif text-2xl font-bold text-luxury-white mt-1">
              Review Consignment &amp; Dispatch Instructions
            </h2>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <div className="lg:col-span-2 space-y-4">
              {/* Items Summary Table */}
              <div className="bg-luxury-surface border border-luxury-border rounded-sm divide-y divide-luxury-border">
                {checkoutItems.length === 0 ? (
                  <div className="p-6 text-center text-xs text-luxury-muted">
                    No artifacts currently in checkout consignment.
                  </div>
                ) : (
                  checkoutItems.map((item) => {
                    const unitPrice = Number(item.price ?? item.unitPrice ?? 0);
                    const subtotal = Number(item.subtotal ?? (unitPrice * item.quantity));
                    return (
                      <div key={item.id} className="p-4 flex items-center justify-between gap-4">
                        <div className="flex items-center gap-3">
                          {item.imageUrl && (
                            <img
                              src={item.imageUrl}
                              alt={item.productName}
                              className="w-12 h-12 rounded-sm object-cover bg-luxury-elevated border border-luxury-border"
                            />
                          )}
                          <div>
                            <h4 className="font-serif font-bold text-sm text-luxury-white line-clamp-1">
                              {item.productName}
                            </h4>
                            <span className="text-xs text-luxury-muted">
                              Qty: {item.quantity} • ${unitPrice.toFixed(2)} each
                            </span>
                          </div>
                        </div>
                        <span className="font-serif font-bold text-sm text-luxury-white">
                          ${subtotal.toFixed(2)}
                        </span>
                      </div>
                    );
                  })
                )}
              </div>

              {/* Delivery Instructions Input */}
              <div className="bg-luxury-surface border border-luxury-border p-5 rounded-sm space-y-2">
                <label className="text-xs font-semibold uppercase tracking-wider text-luxury-gold block">
                  Concierge Delivery Instructions (Optional)
                </label>
                <textarea
                  rows={2}
                  value={deliveryNotes}
                  onChange={(e) => setDeliveryNotes(e.target.value)}
                  placeholder="e.g. Leave with residential concierge, ring bell upon arrival, white-glove setup..."
                  className="w-full bg-luxury-elevated border border-luxury-border rounded-sm p-3 text-xs text-luxury-white placeholder:text-luxury-subtle focus:outline-none focus:border-luxury-gold"
                  maxLength={500}
                />
              </div>
            </div>

            {/* Address Snapshot & Totals */}
            <div className="space-y-4">
              <div className="bg-luxury-surface border border-luxury-border p-5 rounded-sm space-y-3">
                <h4 className="text-xs font-semibold uppercase tracking-wider text-luxury-gold flex items-center gap-1.5">
                  <MapPin size={14} /> Dispatch Destination
                </h4>
                {selectedAddress ? (
                  <div className="text-xs text-luxury-muted leading-relaxed">
                    <p className="font-bold text-luxury-white">{selectedAddress.addressLine}</p>
                    <p>{selectedAddress.city}, {selectedAddress.state} {selectedAddress.postalCode}</p>
                    <p>{selectedAddress.country}</p>
                  </div>
                ) : (
                  <p className="text-xs text-luxury-muted italic">No delivery residence selected</p>
                )}
                <button
                  type="button"
                  onClick={() => {
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    setCurrentStep(1);
                  }}
                  className="text-[11px] text-luxury-gold hover:underline font-semibold block"
                >
                  Change Destination
                </button>
              </div>

              <div className="bg-luxury-surface border border-luxury-border p-5 rounded-sm space-y-3">
                <h4 className="font-serif font-bold text-sm text-luxury-white pb-2 border-b border-luxury-border">
                  Cost Summary
                </h4>
                <div className="flex justify-between text-xs text-luxury-muted">
                  <span>Subtotal</span>
                  <span className="text-luxury-white font-medium">${safeTotalAmount.toFixed(2)}</span>
                </div>
                <div className="flex justify-between text-xs text-luxury-muted">
                  <span>Insured Transport</span>
                  <span className={isFreeShipping ? 'text-luxury-gold font-bold' : 'text-luxury-white'}>
                    {isFreeShipping ? 'COMPLIMENTARY' : `$${shippingFee.toFixed(2)}`}
                  </span>
                </div>
                <div className="border-t border-luxury-border pt-3 flex justify-between items-baseline font-serif">
                  <span className="font-bold text-sm text-luxury-white">Total</span>
                  <span className="font-bold text-xl text-luxury-gold">${finalTotal.toFixed(2)}</span>
                </div>
              </div>
            </div>
          </div>

          <div className="pt-6 border-t border-luxury-border flex justify-between items-center">
            <Button
              variant="ghost"
              size="sm"
              onClick={() => {
                window.scrollTo({ top: 0, behavior: 'smooth' });
                setCurrentStep(1);
              }}
              leftIcon={<ArrowLeft size={16} />}
            >
              Back to Address
            </Button>
            <Button variant="gold" size="md" onClick={handleStep2Next} rightIcon={<ArrowRight size={16} />}>
              Proceed to Settlement
            </Button>
          </div>
        </div>
      )}

      {/* STEP 3: Settlement Protocol (Payment Method & Simulation) */}
      {currentStep === 3 && (
        <div className="space-y-6">
          <div className="border-b border-luxury-border pb-4">
            <span className="text-xs font-semibold tracking-luxury-widest uppercase text-luxury-gold">
              Step 3 of 3
            </span>
            <h2 className="font-serif text-2xl font-bold text-luxury-white mt-1">
              Encrypted Settlement Protocol
            </h2>
          </div>

          {/* Protocol / Gateway Engine Switcher */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <button
              type="button"
              onClick={() => {
                setGatewayMode('RAZORPAY');
                setRazorpayError(null);
              }}
              className={`p-4 rounded-sm border text-left transition-all relative flex flex-col justify-between ${
                gatewayMode === 'RAZORPAY'
                  ? 'bg-luxury-surface border-luxury-gold shadow-gold-sm ring-1 ring-luxury-gold'
                  : 'bg-luxury-surface/50 border-luxury-border hover:border-luxury-muted'
              }`}
            >
              <div className="flex items-center justify-between mb-2">
                <span className="text-xs font-bold uppercase tracking-wider text-luxury-white flex items-center gap-1.5">
                  <Sparkles size={15} className="text-luxury-gold" />
                  Razorpay Secure Gateway
                </span>
                <span className="text-[10px] font-mono uppercase px-2 py-0.5 rounded-full bg-luxury-gold/20 text-luxury-gold border border-luxury-gold/40">
                  Test Mode Active
                </span>
              </div>
              <p className="text-[11px] text-luxury-muted leading-relaxed">
                Official Razorpay checkout modal with Card, Instant UPI QR, NetBanking, and Wallets.
              </p>
            </button>

            <button
              type="button"
              onClick={() => setGatewayMode('SIMULATED')}
              className={`p-4 rounded-sm border text-left transition-all relative flex flex-col justify-between ${
                gatewayMode === 'SIMULATED'
                  ? 'bg-luxury-surface border-luxury-gold shadow-gold-sm ring-1 ring-luxury-gold'
                  : 'bg-luxury-surface/50 border-luxury-border hover:border-luxury-muted'
              }`}
            >
              <div className="flex items-center justify-between mb-2">
                <span className="text-xs font-bold uppercase tracking-wider text-luxury-white flex items-center gap-1.5">
                  <ShieldCheck size={15} className="text-luxury-gold" />
                  Internal Simulation / COD
                </span>
                <span className="text-[10px] font-mono uppercase px-2 py-0.5 rounded-full bg-luxury-elevated text-luxury-muted border border-luxury-border">
                  Simulation Fallback
                </span>
              </div>
              <p className="text-[11px] text-luxury-muted leading-relaxed">
                Developer simulation harness with error injection and Cash on Delivery handover.
              </p>
            </button>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <div className="lg:col-span-2 space-y-6">
              {gatewayMode === 'RAZORPAY' ? (
                <div className="p-6 bg-luxury-surface border border-luxury-gold/50 rounded-sm space-y-4">
                  <div className="flex items-center justify-between border-b border-luxury-border pb-3">
                    <div className="flex items-center gap-2">
                      <div className="w-8 h-8 rounded-full bg-luxury-gold/10 border border-luxury-gold/30 flex items-center justify-center text-luxury-gold">
                        <Lock size={16} />
                      </div>
                      <div>
                        <h4 className="text-sm font-serif font-bold text-luxury-white">
                          Razorpay Standard Checkout
                        </h4>
                        <span className="text-[11px] text-luxury-muted">
                          Official PCI-DSS Level 1 Compliant Modal
                        </span>
                      </div>
                    </div>
                    <span className="text-[11px] font-mono text-luxury-gold font-bold px-2 py-1 bg-luxury-gold/10 rounded">
                      Sandbox Test Mode
                    </span>
                  </div>

                  <p className="text-xs text-luxury-muted leading-relaxed">
                    Clicking the authorization button below launches the official Razorpay Checkout modal styled in JPBazaar black-and-gold. You can test transactions using test cards or any test UPI ID without real financial charges.
                  </p>

                  {/* Test Credentials Helper Box */}
                  <div className="bg-luxury-elevated border border-luxury-border p-4 rounded-sm text-xs space-y-2">
                    <span className="text-luxury-gold font-bold uppercase tracking-wider text-[10px] block">
                      Recommended Test Credentials:
                    </span>
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 text-luxury-muted text-[11px]">
                      <div>
                        <span className="text-luxury-white font-medium">Test Card:</span>{' '}
                        <code className="text-luxury-gold font-mono bg-luxury-surface px-1.5 py-0.5 rounded">4111 2222 3333 4444</code>
                      </div>
                      <div>
                        <span className="text-luxury-white font-medium">Expiry / CVV:</span>{' '}
                        <code className="text-luxury-gold font-mono bg-luxury-surface px-1.5 py-0.5 rounded">12/28 • 123</code>
                      </div>
                      <div>
                        <span className="text-luxury-white font-medium">Instant UPI:</span>{' '}
                        <code className="text-luxury-gold font-mono bg-luxury-surface px-1.5 py-0.5 rounded">success@razorpay</code>
                      </div>
                      <div>
                        <span className="text-luxury-white font-medium">Test OTP:</span>{' '}
                        <code className="text-luxury-gold font-mono bg-luxury-surface px-1.5 py-0.5 rounded">Any 6 digits (e.g. 123456)</code>
                      </div>
                    </div>
                  </div>

                  {razorpayError && (
                    <div className="p-3 bg-red-950/40 border border-red-500/50 rounded-sm flex items-start gap-2 text-xs text-red-300">
                      <AlertTriangle size={16} className="text-red-400 shrink-0 mt-0.5" />
                      <div className="space-y-1">
                        <strong className="block font-semibold">Settlement Disruption:</strong>
                        <span>{razorpayError}</span>
                      </div>
                    </div>
                  )}
                </div>
              ) : (
                /* Simulated Disclaimer & Selector */
                <div className="space-y-6">
                  <div className="p-4 bg-luxury-surface border border-luxury-gold/50 rounded-sm flex items-start gap-3 text-xs text-luxury-muted">
                    <ShieldCheck size={20} className="text-luxury-gold shrink-0 mt-0.5" />
                    <div>
                      <strong className="text-luxury-gold font-bold block mb-0.5">
                        Simulated Gateway Demonstration Protocol
                      </strong>
                      JPBazaar operates a simulated transaction gateway. No financial credit or debit card is charged. Transactions are immediately reconciled with our Spring Boot order state machine.
                    </div>
                  </div>

                  {/* Payment Method Selector Tabs */}
                  <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
                    {[
                      { id: 'CARD' as PaymentMethod, label: 'Credit Card', icon: CreditCard },
                      { id: 'UPI' as PaymentMethod, label: 'Instant UPI', icon: Lock },
                      { id: 'NET_BANKING' as PaymentMethod, label: 'Wire Transfer', icon: Building },
                      { id: 'COD' as PaymentMethod, label: 'Consignment Handover', icon: Truck },
                    ].map((m) => {
                      const Icon = m.icon;
                      const isSelected = paymentMethod === m.id;
                      return (
                        <button
                          key={m.id}
                          type="button"
                          onClick={() => setPaymentMethod(m.id)}
                          className={`p-4 rounded-sm border text-center transition-all flex flex-col items-center justify-center gap-2 ${
                            isSelected
                              ? 'bg-luxury-surface border-luxury-gold shadow-gold-sm text-luxury-white'
                              : 'bg-luxury-surface/40 border-luxury-border text-luxury-muted hover:text-luxury-white'
                          }`}
                        >
                          <Icon size={20} className={isSelected ? 'text-luxury-gold' : 'text-luxury-muted'} />
                          <span className="text-xs font-bold uppercase tracking-wider">{m.label}</span>
                        </button>
                      );
                    })}
                  </div>

                  {/* Method Mock Input Details */}
                  <div className="bg-luxury-surface border border-luxury-border p-6 rounded-sm space-y-4">
                    {paymentMethod === 'CARD' && (
                      <div className="space-y-4">
                        <Input
                          label="Cardholder Full Name"
                          value={cardName}
                          onChange={(e) => setCardName(e.target.value)}
                        />
                        <Input
                          label="Simulated Card Number"
                          value={cardNumber}
                          onChange={(e) => setCardNumber(e.target.value)}
                        />
                        <div className="grid grid-cols-2 gap-4">
                          <Input
                            label="Expiry Date"
                            value={cardExpiry}
                            onChange={(e) => setCardExpiry(e.target.value)}
                          />
                          <Input
                            label="CVV / CVC"
                            type="password"
                            value={cardCvv}
                            onChange={(e) => setCardCvv(e.target.value)}
                          />
                        </div>
                      </div>
                    )}

                    {paymentMethod === 'UPI' && (
                      <div className="space-y-3">
                        <Input
                          label="Virtual Payment Address (VPA / UPI ID)"
                          defaultValue="patron@okjpbazaar"
                        />
                        <p className="text-[11px] text-luxury-muted">
                          Approval notification simulated with instant clearance.
                        </p>
                      </div>
                    )}

                    {paymentMethod === 'NET_BANKING' && (
                      <div className="space-y-3">
                        <label className="text-xs text-luxury-muted block">Select Banking Institution</label>
                        <select className="w-full bg-luxury-elevated border border-luxury-border rounded-sm p-2.5 text-xs text-luxury-white focus:outline-none focus:border-luxury-gold">
                          <option>JP Morgan Private Client Banking</option>
                          <option>Credit Suisse Geneva</option>
                          <option>Barclays Wealth Management</option>
                          <option>Standard Chartered Priority</option>
                        </select>
                      </div>
                    )}

                    {paymentMethod === 'COD' && (
                      <div className="space-y-2 text-xs text-luxury-muted">
                        <p className="font-semibold text-luxury-white">
                          Complimentary White-Glove Handover Settlement
                        </p>
                        <p>
                          Payment is settled in-person via certified draft or encrypted terminal upon final delivery inspection.
                        </p>
                      </div>
                    )}

                    {/* Developer / QA Simulator failure toggle */}
                    <div className="pt-4 border-t border-luxury-border flex items-center gap-2">
                      <input
                        type="checkbox"
                        id="simFailureCheckbox"
                        checked={simulateFailure}
                        onChange={(e) => setSimulateFailure(e.target.checked)}
                        className="w-4 h-4 rounded-sm border-luxury-border bg-luxury-surface text-luxury-danger focus:ring-luxury-danger"
                      />
                      <label htmlFor="simFailureCheckbox" className="text-xs text-luxury-muted cursor-pointer select-none flex items-center gap-1">
                        <AlertTriangle size={14} className="text-amber-400" />
                        <span>Test Gateway Error Recovery (Simulate Failure)</span>
                      </label>
                    </div>
                  </div>
                </div>
              )}
            </div>

            {/* Sidebar Final Amount */}
            <div className="bg-luxury-surface border border-luxury-border p-6 rounded-sm space-y-6 h-fit">
              <h4 className="font-serif font-bold text-base text-luxury-white pb-2 border-b border-luxury-border">
                Settlement Authorization
              </h4>
              <div className="space-y-2 text-xs text-luxury-muted">
                <div className="flex justify-between">
                  <span>Protocol</span>
                  <span className="text-luxury-gold font-bold">
                    {gatewayMode === 'RAZORPAY' ? 'Razorpay Test' : paymentMethod}
                  </span>
                </div>
                <div className="flex justify-between">
                  <span>Payable Amount</span>
                  <span className="font-serif font-bold text-base text-luxury-white">${finalTotal.toFixed(2)}</span>
                </div>
              </div>

              <Button
                variant="gold"
                size="lg"
                fullWidth
                onClick={gatewayMode === 'RAZORPAY' ? handleRazorpaySettlement : handleFinalSettlement}
                isLoading={isProcessingPayment}
              >
                {gatewayMode === 'RAZORPAY'
                  ? `Pay with Razorpay • $${finalTotal.toFixed(2)}`
                  : `Authorize Settlement • $${finalTotal.toFixed(2)}`}
              </Button>
            </div>
          </div>

          <div className="pt-6 border-t border-luxury-border flex justify-between items-center">
            <Button
              variant="ghost"
              size="sm"
              onClick={() => {
                window.scrollTo({ top: 0, behavior: 'smooth' });
                setCurrentStep(2);
              }}
              leftIcon={<ArrowLeft size={16} />}
            >
              Back to Review
            </Button>
          </div>
        </div>
      )}

      {/* STEP 4: Confirmation & Receipt */}
      {currentStep === 4 && completedOrder && (
        <div className="bg-luxury-surface border border-luxury-border p-8 sm:p-12 rounded-sm text-center max-w-2xl mx-auto space-y-6 animate-fade-in">
          <div className="w-20 h-20 rounded-full bg-luxury-gold/10 border border-luxury-gold/30 flex items-center justify-center text-luxury-gold mx-auto">
            <CheckCircle size={40} />
          </div>

          <div>
            <span className="text-xs font-semibold tracking-luxury-widest uppercase text-luxury-gold">
              Consignment Confirmed
            </span>
            <h1 className="font-serif text-3xl sm:text-4xl font-bold text-luxury-white mt-2">
              Acquisition Successful
            </h1>
            <p className="text-xs text-luxury-muted mt-2">
              Order #{completedOrder.id} has been recorded in the database and approved for white-glove transport.
            </p>
          </div>

          {/* Receipt Snapshot Card */}
          <div className="bg-luxury-elevated border border-luxury-border p-6 rounded-sm text-left text-xs space-y-3">
            <div className="flex justify-between border-b border-luxury-border pb-2">
              <span className="text-luxury-muted">Consignment ID</span>
              <span className="font-mono font-bold text-luxury-white">#{completedOrder.id}</span>
            </div>
            <div className="flex justify-between border-b border-luxury-border pb-2">
              <span className="text-luxury-muted">Gateway Transaction ID</span>
              <span className="font-mono font-bold text-luxury-gold">
                {completedPayment?.transactionId || 'TXN_SIMULATED_CONFIRMED'}
              </span>
            </div>
            {completedPayment?.razorpayOrderId && (
              <div className="flex justify-between border-b border-luxury-border pb-2">
                <span className="text-luxury-muted">Razorpay Order Reference</span>
                <span className="font-mono font-bold text-luxury-white">
                  {completedPayment.razorpayOrderId}
                </span>
              </div>
            )}
            <div className="flex justify-between border-b border-luxury-border pb-2">
              <span className="text-luxury-muted">Monetary Total</span>
              <span className="font-serif font-bold text-base text-luxury-white">
                ${Number(completedOrder.totalAmount).toFixed(2)}
              </span>
            </div>
            <div className="flex justify-between">
              <span className="text-luxury-muted">Delivery Destination</span>
              <span className="text-luxury-white text-right max-w-xs font-medium">
                {completedOrder.shippingAddress}
              </span>
            </div>
          </div>

          <div className="flex flex-wrap items-center justify-center gap-4 pt-4">
            <Link to="/account/orders">
              <Button variant="gold" size="md">
                Track in Acquisition History
              </Button>
            </Link>
            <Link to="/products">
              <Button variant="outline" size="md">
                Return to Boutique
              </Button>
            </Link>
          </div>
        </div>
      )}

      {/* Address Management Modal */}
      <AddressModal
        isOpen={isAddressModalOpen}
        onClose={() => setIsAddressModalOpen(false)}
        onSaved={handleAddressSaved}
      />
    </div>
  );
};
