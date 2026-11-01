import React, { useEffect, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { orderService } from '../../services/orderService';
import { OrderTrackingResponse, OrderStatus } from '../../types';
import { useToast } from '../../context/ToastContext';
import { Button } from '../../components/common/Button';
import { Badge } from '../../components/common/Badge';
import { Skeleton } from '../../components/common/Skeleton';
import {
  Package,
  Truck,
  CheckCircle,
  Clock,
  MapPin,
  Calendar,
  Copy,
  Check,
  ChevronLeft,
  AlertCircle,
  ShieldCheck,
  ArrowRight,
} from 'lucide-react';

export const OrderTrackingPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { showToast } = useToast();

  const [tracking, setTracking] = useState<OrderTrackingResponse | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [copied, setCopied] = useState<boolean>(false);

  useEffect(() => {
    const fetchTracking = async () => {
      if (!id || isNaN(Number(id))) {
        setError('Invalid consignment identifier.');
        setIsLoading(false);
        return;
      }

      setIsLoading(true);
      setError(null);
      try {
        const data = await orderService.getOrderTracking(Number(id));
        setTracking(data);
      } catch (err: any) {
        console.error('Failed to load order tracking:', err);
        setError(err.message || 'Unable to retrieve tracking intelligence for this consignment.');
      } finally {
        setIsLoading(false);
      }
    };

    fetchTracking();
  }, [id]);

  const handleCopyTrackingNumber = () => {
    if (tracking?.trackingNumber) {
      navigator.clipboard.writeText(tracking.trackingNumber);
      setCopied(true);
      showToast('Consignment tracking code copied to clipboard', 'info');
      setTimeout(() => setCopied(false), 2500);
    }
  };

  const getStatusBadge = (status: OrderStatus) => {
    switch (status) {
      case 'DELIVERED':
        return <Badge variant="dark" className="border-emerald-500/50 text-emerald-400">Delivered</Badge>;
      case 'SHIPPED':
        return <Badge variant="gold">In Transit</Badge>;
      case 'OUT_FOR_DELIVERY':
        return <Badge variant="gold" className="animate-pulse">Out For Delivery</Badge>;
      case 'CONFIRMED':
      case 'PROCESSING':
        return <Badge variant="gold-outline">Atelier Preparation</Badge>;
      case 'PLACED':
        return <Badge variant="dark" className="text-amber-300 border-amber-500/40">Order Registered</Badge>;
      case 'CANCELLED':
        return <Badge variant="danger">Cancelled</Badge>;
      case 'REFUNDED':
        return <Badge variant="dark" className="border-purple-500/50 text-purple-400">Refunded</Badge>;
      default:
        return <Badge variant="dark">{status}</Badge>;
    }
  };

  const formatDateTime = (isoString?: string) => {
    if (!isoString) return 'Pending Confirmation';
    return new Date(isoString).toLocaleString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  if (isLoading) {
    return (
      <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-10 w-full flex-1">
        <Skeleton className="h-6 w-64 mb-4" />
        <Skeleton className="h-12 w-full mb-8" />
        <div className="space-y-6">
          <Skeleton className="h-44 w-full" />
          <Skeleton className="h-64 w-full" />
          <Skeleton className="h-48 w-full" />
        </div>
      </div>
    );
  }

  if (error || !tracking) {
    return (
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-16 w-full text-center">
        <div className="w-16 h-16 rounded-full bg-rose-500/10 border border-rose-500/30 flex items-center justify-center mx-auto mb-6 text-rose-400">
          <AlertCircle size={32} />
        </div>
        <h2 className="font-serif text-2xl text-luxury-white mb-2">Tracking Telemetry Unavailable</h2>
        <p className="text-luxury-muted text-sm max-w-md mx-auto mb-8">{error || 'Consignment records could not be verified.'}</p>
        <div className="flex items-center justify-center gap-4">
          <Button variant="outline" onClick={() => navigate('/account/orders')} leftIcon={<ChevronLeft size={16} />}>
            Return to Consignment Archive
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-10 w-full flex-1 flex flex-col">
      {/* Breadcrumb Navigation */}
      <nav className="flex items-center gap-2 text-xs text-luxury-muted uppercase tracking-wider mb-6">
        <Link to="/" className="hover:text-luxury-gold transition-colors">Sanctuary</Link>
        <span>/</span>
        <Link to="/account" className="hover:text-luxury-gold transition-colors">Client Profile</Link>
        <span>/</span>
        <Link to="/account/orders" className="hover:text-luxury-gold transition-colors">Consignments</Link>
        <span>/</span>
        <span className="text-luxury-white font-semibold">Tracking #{tracking.orderId}</span>
      </nav>

      {/* Header Banner */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 pb-6 border-b border-luxury-border mb-8">
        <div>
          <div className="flex items-center gap-3 mb-1">
            <h1 className="font-serif text-2xl sm:text-3xl font-bold text-luxury-white tracking-wide">
              Consignment Tracking
            </h1>
            {getStatusBadge(tracking.currentStatus)}
          </div>
          <p className="text-xs text-luxury-muted">
            Acquisition Reference <span className="text-luxury-gold font-mono font-medium">#{tracking.orderId}</span> &bull; Verified Courier Telemetry
          </p>
        </div>

        <div className="flex items-center gap-3">
          <Link to="/account/orders">
            <Button variant="dark" size="sm" leftIcon={<ChevronLeft size={14} />}>
              Archive
            </Button>
          </Link>
        </div>
      </div>

      {/* Logistics & Tracking Highlights Card */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        {/* Carrier Info */}
        <div className="bg-luxury-surface/40 border border-luxury-border rounded-lg p-5">
          <div className="flex items-center gap-2 text-luxury-gold text-xs font-semibold uppercase tracking-wider mb-3">
            <Truck size={16} />
            <span>Designated Carrier</span>
          </div>
          <p className="text-luxury-white font-serif font-bold text-base mb-1">
            {tracking.carrier || 'JPBazaar White-Glove Express'}
          </p>
          <div className="flex items-center justify-between mt-3 pt-3 border-t border-luxury-border/50 text-xs">
            <span className="text-luxury-muted font-mono">{tracking.trackingNumber || `JPB-${tracking.orderId}-TRK`}</span>
            <button
              onClick={handleCopyTrackingNumber}
              className="text-luxury-gold hover:text-luxury-white transition-colors flex items-center gap-1"
              title="Copy Tracking Number"
            >
              {copied ? <Check size={13} className="text-emerald-400" /> : <Copy size={13} />}
              <span className="text-[11px]">{copied ? 'Copied' : 'Copy'}</span>
            </button>
          </div>
        </div>

        {/* Estimated Handover */}
        <div className="bg-luxury-surface/40 border border-luxury-border rounded-lg p-5">
          <div className="flex items-center gap-2 text-luxury-gold text-xs font-semibold uppercase tracking-wider mb-3">
            <Calendar size={16} />
            <span>Estimated Handover</span>
          </div>
          <p className="text-luxury-white font-serif font-bold text-base mb-1">
            {tracking.estimatedDeliveryDate
              ? new Date(tracking.estimatedDeliveryDate).toLocaleDateString('en-US', {
                  weekday: 'short',
                  month: 'short',
                  day: 'numeric',
                  year: 'numeric',
                })
              : '3 - 5 Business Days'}
          </p>
          <p className="text-xs text-luxury-muted mt-2">
            Status: <span className="text-luxury-white font-medium">{tracking.currentStatus}</span>
          </p>
        </div>

        {/* Delivery Destination */}
        <div className="bg-luxury-surface/40 border border-luxury-border rounded-lg p-5">
          <div className="flex items-center gap-2 text-luxury-gold text-xs font-semibold uppercase tracking-wider mb-3">
            <MapPin size={16} />
            <span>Client Destination</span>
          </div>
          <p className="text-luxury-white font-serif font-semibold text-sm mb-1 line-clamp-1">
            {tracking.recipientName || 'Valued Patron'}
          </p>
          <p className="text-xs text-luxury-muted line-clamp-2">
            {tracking.deliveryAddress || 'Primary Registered Residence'}
          </p>
        </div>
      </div>

      {/* Milestone Progress Stepper */}
      <div className="bg-luxury-surface/30 border border-luxury-border rounded-lg p-6 sm:p-8 mb-8">
        <div className="flex items-center justify-between mb-8">
          <div>
            <h2 className="font-serif text-lg font-bold text-luxury-white tracking-wide">
              Milestone Progression
            </h2>
            <p className="text-xs text-luxury-muted mt-0.5">
              Live custody and protocol transitions logged by logistics dispatchers
            </p>
          </div>
          <div className="flex items-center gap-2 text-xs text-emerald-400 bg-emerald-500/10 border border-emerald-500/20 px-3 py-1 rounded-full">
            <ShieldCheck size={14} />
            <span>Vault Verified</span>
          </div>
        </div>

        {/* Horizontal Stepper (Desktop) & Vertical (Mobile) */}
        <div className="hidden md:grid md:grid-cols-6 gap-3 relative mb-6">
          {tracking.checkpoints.map((cp) => {
            const isDone = cp.completed;
            const isCurr = cp.current;

            return (
              <div key={cp.step} className="flex flex-col items-center text-center relative group">
                <div
                  className={`w-10 h-10 rounded-full flex items-center justify-center font-bold text-sm transition-all shadow-md ${
                    isDone
                      ? 'bg-luxury-gold text-luxury-black border-2 border-luxury-gold'
                      : 'bg-luxury-surface border border-luxury-border text-luxury-muted'
                  } ${isCurr ? 'ring-4 ring-luxury-gold/30 ring-offset-2 ring-offset-luxury-black' : ''}`}
                >
                  {isDone ? <CheckCircle size={18} /> : cp.step}
                </div>
                <span className={`text-xs font-semibold mt-3 ${isCurr ? 'text-luxury-gold' : isDone ? 'text-luxury-white' : 'text-luxury-muted'}`}>
                  {cp.label}
                </span>
                <span className="text-[10px] text-luxury-muted mt-1 leading-snug px-1">
                  {cp.description}
                </span>
                {cp.timestamp && (
                  <span className="text-[9px] text-luxury-gold/80 font-mono mt-2 bg-luxury-surface/80 px-2 py-0.5 rounded border border-luxury-border">
                    {new Date(cp.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                  </span>
                )}
              </div>
            );
          })}
        </div>

        {/* Mobile Vertical Stepper */}
        <div className="md:hidden space-y-6 relative pl-6 before:content-[''] before:absolute before:left-2.5 before:top-2 before:bottom-2 before:w-0.5 before:bg-luxury-border">
          {tracking.checkpoints.map((cp) => {
            const isDone = cp.completed;
            const isCurr = cp.current;

            return (
              <div key={cp.step} className="relative flex items-start gap-4">
                <div
                  className={`absolute -left-6 w-5 h-5 rounded-full flex items-center justify-center text-[10px] font-bold ${
                    isDone
                      ? 'bg-luxury-gold text-luxury-black'
                      : 'bg-luxury-surface border border-luxury-border text-luxury-muted'
                  } ${isCurr ? 'ring-2 ring-luxury-gold ring-offset-1 ring-offset-luxury-black' : ''}`}
                >
                  {isDone ? '✓' : cp.step}
                </div>
                <div>
                  <h3 className={`text-xs font-bold ${isCurr ? 'text-luxury-gold' : isDone ? 'text-luxury-white' : 'text-luxury-muted'}`}>
                    {cp.label}
                  </h3>
                  <p className="text-[11px] text-luxury-muted mt-0.5">{cp.description}</p>
                  {cp.timestamp && (
                    <span className="text-[10px] text-luxury-gold/70 font-mono block mt-1">
                      {formatDateTime(cp.timestamp)}
                    </span>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Two-Column Layout: Status Audit History & Package Details */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Status Audit History Trail (2 Columns) */}
        <div className="lg:col-span-2 bg-luxury-surface/30 border border-luxury-border rounded-lg p-6">
          <div className="flex items-center gap-2 mb-4">
            <Clock size={16} className="text-luxury-gold" />
            <h3 className="font-serif text-base font-bold text-luxury-white tracking-wide">
              Custody &amp; Inspection Audit Log
            </h3>
          </div>
          <p className="text-xs text-luxury-muted mb-6">
            Immutable timeline of state modifications, courier receipts, and verification milestones
          </p>

          <div className="space-y-4">
            {tracking.history && tracking.history.length > 0 ? (
              tracking.history.map((h) => (
                <div key={h.id} className="p-4 bg-luxury-surface/60 border border-luxury-border/60 rounded-md flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                  <div>
                    <div className="flex items-center gap-2 mb-1">
                      <span className="text-xs font-bold text-luxury-gold font-mono">{h.status}</span>
                      <span className="text-[10px] text-luxury-muted">&bull; Updated by {h.updatedBy || 'SYSTEM'}</span>
                    </div>
                    <p className="text-xs text-luxury-white">
                      {h.notes || `Consignment advanced to ${h.status}`}
                    </p>
                  </div>
                  <div className="text-right sm:self-center shrink-0">
                    <span className="text-[11px] font-mono text-luxury-muted">
                      {formatDateTime(h.createdAt)}
                    </span>
                  </div>
                </div>
              ))
            ) : (
              <p className="text-xs text-luxury-muted italic">Initial consignment logs are being registered.</p>
            )}
          </div>
        </div>

        {/* Consignment Items Snapshot (1 Column) */}
        <div className="bg-luxury-surface/30 border border-luxury-border rounded-lg p-6">
          <div className="flex items-center gap-2 mb-4">
            <Package size={16} className="text-luxury-gold" />
            <h3 className="font-serif text-base font-bold text-luxury-white tracking-wide">
              Consignment Contents
            </h3>
          </div>
          <p className="text-xs text-luxury-muted mb-4">
            {tracking.items?.length || 0} Artifacts in this delivery package
          </p>

          <div className="divide-y divide-luxury-border/50 mb-6">
            {tracking.items?.map((item) => (
              <div key={item.id} className="py-3 flex items-center gap-3">
                {item.imageUrl && (
                  <img
                    src={item.imageUrl}
                    alt={item.productName}
                    className="w-10 h-10 rounded object-cover bg-luxury-surface border border-luxury-border shrink-0"
                  />
                )}
                <div className="flex-1 min-w-0">
                  <p className="text-xs font-medium text-luxury-white truncate">{item.productName}</p>
                  <p className="text-[11px] text-luxury-muted">Qty: {item.quantity}</p>
                </div>
                <div className="text-right">
                  <span className="text-xs font-bold text-luxury-gold">₹{Number(item.subtotal).toFixed(2)}</span>
                </div>
              </div>
            ))}
          </div>

          <div className="pt-4 border-t border-luxury-border flex justify-between items-center text-sm">
            <span className="text-xs text-luxury-muted uppercase font-semibold">Total Declared Value</span>
            <span className="font-serif font-bold text-base text-luxury-gold">₹{Number(tracking.totalAmount).toFixed(2)}</span>
          </div>

          <div className="mt-6 pt-4 border-t border-luxury-border/50 text-center">
            <Link
              to="/account/orders"
              className="inline-flex items-center gap-1.5 text-xs text-luxury-gold hover:text-luxury-white transition-colors uppercase tracking-wider font-semibold"
            >
              <span>View All Acquisitions</span>
              <ArrowRight size={13} />
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};
