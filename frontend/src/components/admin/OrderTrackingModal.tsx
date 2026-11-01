import React, { useState } from 'react';
import { Order, OrderStatus, UpdateOrderStatusRequest } from '../../types';
import { adminService } from '../../services/adminService';
import { useToast } from '../../context/ToastContext';
import { Button } from '../common/Button';
import { Input } from '../common/Input';
import { Select } from '../common/Select';
import { Truck, X, ShieldCheck } from 'lucide-react';

interface OrderTrackingModalProps {
  order: Order | null;
  isOpen: boolean;
  onClose: () => void;
  onSuccess: (updated: Order) => void;
}

export const OrderTrackingModal: React.FC<OrderTrackingModalProps> = ({
  order,
  isOpen,
  onClose,
  onSuccess,
}) => {
  const { showToast } = useToast();

  const [status, setStatus] = useState<OrderStatus>(order?.status || 'PROCESSING');
  const [carrier, setCarrier] = useState<string>('JPBazaar White-Glove Express');
  const [trackingNumber, setTrackingNumber] = useState<string>(`JPB-${order?.id || 0}-TRK`);
  const [estimatedDate, setEstimatedDate] = useState<string>('');
  const [notes, setNotes] = useState<string>('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  React.useEffect(() => {
    if (order) {
      setStatus(order.status);
      setTrackingNumber(`JPB-${order.id}-${Math.floor(100000 + Math.random() * 900000)}`);
    }
  }, [order]);

  if (!isOpen || !order) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      const payload: UpdateOrderStatusRequest = {
        status,
        carrier: carrier.trim() || undefined,
        trackingNumber: trackingNumber.trim() || undefined,
        estimatedDeliveryDate: estimatedDate ? new Date(estimatedDate).toISOString() : undefined,
        notes: notes.trim() || undefined,
      };

      const updated = await adminService.updateOrderStatus(order.id, payload);
      showToast(`Consignment #${order.id} status updated to ${status}.`, 'success');
      onSuccess(updated);
      onClose();
    } catch (err: any) {
      showToast(err.message || 'Failed to update consignment status.', 'error');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-sm animate-fade-in">
      <div className="bg-luxury-surface border border-luxury-border w-full max-w-lg rounded-lg shadow-2xl overflow-hidden flex flex-col">
        {/* Modal Header */}
        <div className="flex items-center justify-between p-6 border-b border-luxury-border">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-full bg-luxury-gold/10 border border-luxury-gold/30 flex items-center justify-center text-luxury-gold">
              <Truck size={20} />
            </div>
            <div>
              <h3 className="font-serif font-bold text-lg text-luxury-white">
                Logistics Dispatch &amp; Tracking
              </h3>
              <p className="text-xs text-luxury-muted">
                Consignment Reference #{order.id} &bull; Client: {order.userEmail}
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="text-luxury-muted hover:text-luxury-white transition-colors p-1"
          >
            <X size={20} />
          </button>
        </div>

        {/* Form Body */}
        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider text-luxury-gold mb-1.5">
              Lifecycle Transition State
            </label>
            <Select
              value={status}
              onChange={(e) => setStatus(e.target.value as OrderStatus)}
              options={[
                { value: 'PLACED', label: 'PLACED - Registered Order' },
                { value: 'CONFIRMED', label: 'CONFIRMED - Payment Verified' },
                { value: 'PROCESSING', label: 'PROCESSING - Atelier Preparation' },
                { value: 'SHIPPED', label: 'SHIPPED - Dispatched with Courier' },
                { value: 'OUT_FOR_DELIVERY', label: 'OUT_FOR_DELIVERY - Delivery Imminent' },
                { value: 'DELIVERED', label: 'DELIVERED - Client Handover Complete' },
                { value: 'CANCELLED', label: 'CANCELLED - Release Consignment' },
                { value: 'REFUNDED', label: 'REFUNDED - Settlement Returned' },
              ]}
            />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider text-luxury-muted mb-1.5">
                Designated Courier
              </label>
              <Input
                value={carrier}
                onChange={(e) => setCarrier(e.target.value)}
                placeholder="e.g. JPBazaar White-Glove"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider text-luxury-muted mb-1.5">
                Tracking Number
              </label>
              <Input
                value={trackingNumber}
                onChange={(e) => setTrackingNumber(e.target.value)}
                placeholder="e.g. JPB-101-TRK"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider text-luxury-muted mb-1.5">
              Estimated Delivery Date
            </label>
            <Input
              type="datetime-local"
              value={estimatedDate}
              onChange={(e) => setEstimatedDate(e.target.value)}
            />
          </div>

          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider text-luxury-muted mb-1.5">
              Custody &amp; Inspection Audit Note
            </label>
            <textarea
              rows={3}
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              placeholder="e.g. Authenticity seals verified. Security barcode attached."
              className="w-full bg-luxury-surface border border-luxury-border rounded p-3 text-xs text-luxury-white placeholder:text-luxury-subtle focus:outline-none focus:border-luxury-gold resize-none"
            />
          </div>

          <div className="flex items-center gap-2 text-[11px] text-luxury-muted bg-luxury-surface/50 p-2.5 rounded border border-luxury-border/50">
            <ShieldCheck size={14} className="text-luxury-gold shrink-0" />
            <span>Updating status will trigger real-time client notification emails and append to the immutable audit log.</span>
          </div>

          {/* Modal Actions */}
          <div className="pt-4 border-t border-luxury-border flex justify-end gap-3">
            <Button variant="dark" size="sm" type="button" onClick={onClose}>
              Cancel
            </Button>
            <Button variant="gold" size="sm" type="submit" isLoading={isSubmitting}>
              Dispatch &amp; Save
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
};
