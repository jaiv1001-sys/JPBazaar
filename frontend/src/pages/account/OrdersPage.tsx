import React, { useEffect, useState, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { Order, OrderStatus } from '../../types';
import { orderService } from '../../services/orderService';
import { useToast } from '../../context/ToastContext';
import { Button } from '../../components/common/Button';
import { Badge } from '../../components/common/Badge';
import { Skeleton } from '../../components/common/Skeleton';
import {
  Package,
  CheckCircle,
  RotateCcw,
  XCircle,
  ChevronDown,
  ChevronUp,
} from 'lucide-react';

export const OrdersPage: React.FC = () => {
  const { showToast } = useToast();

  const [orders, setOrders] = useState<Order[]>([]);
  const [selectedStatus, setSelectedStatus] = useState<string>('ALL');
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [cancellingOrderId, setCancellingOrderId] = useState<number | null>(null);
  const [expandedOrderId, setExpandedOrderId] = useState<number | null>(null);

  const fetchOrders = useCallback(async () => {
    setIsLoading(true);
    try {
      const statusParam = selectedStatus !== 'ALL' ? (selectedStatus as OrderStatus) : undefined;
      const res = await orderService.getMyOrders(0, 50, statusParam);
      setOrders(res.content || []);
      if (res.content?.length > 0 && expandedOrderId === null) {
        setExpandedOrderId(res.content[0].id);
      }
    } catch (err: any) {
      console.error('Failed to load orders:', err);
      showToast('Failed to load consignment records.', 'error');
    } finally {
      setIsLoading(false);
    }
  }, [selectedStatus, expandedOrderId, showToast]);

  useEffect(() => {
    fetchOrders();
  }, [fetchOrders]);

  const handleCancelOrder = async (orderId: number) => {
    if (!window.confirm(`Are you certain you wish to request cancellation for Consignment #${orderId}? Inventory stock will be immediately restored.`)) {
      return;
    }

    setCancellingOrderId(orderId);
    try {
      const updated = await orderService.cancelOrder(orderId);
      setOrders((prev) => prev.map((o) => (o.id === orderId ? updated : o)));
      showToast(`Consignment #${orderId} has been cancelled and refunded.`, 'info');
    } catch (err: any) {
      showToast(err.message || 'Unable to cancel consignment at this stage.', 'error');
    } finally {
      setCancellingOrderId(null);
    }
  };

  const getStatusBadge = (status: OrderStatus) => {
    switch (status) {
      case 'DELIVERED':
        return <Badge variant="dark" className="border-emerald-500/50 text-emerald-400">Delivered</Badge>;
      case 'SHIPPED':
      case 'OUT_FOR_DELIVERY':
        return <Badge variant="gold">In Transit</Badge>;
      case 'CONFIRMED':
      case 'PROCESSING':
        return <Badge variant="gold-outline">Processing</Badge>;
      case 'PLACED':
        return <Badge variant="dark" className="text-amber-300 border-amber-500/40">Order Placed</Badge>;
      case 'CANCELLED':
        return <Badge variant="danger">Cancelled</Badge>;
      default:
        return <Badge variant="dark">{status}</Badge>;
    }
  };

  const orderStages: OrderStatus[] = ['PLACED', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED'];

  const getStageIndex = (status: OrderStatus) => {
    return orderStages.indexOf(status);
  };

  return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-10 w-full flex-1 flex flex-col">
      {/* Breadcrumb Navigation */}
      <nav className="flex items-center gap-2 text-xs text-luxury-muted uppercase tracking-wider mb-6">
        <Link to="/" className="hover:text-luxury-gold transition-colors">Sanctuary</Link>
        <span>/</span>
        <Link to="/account" className="hover:text-luxury-gold transition-colors">Client Profile</Link>
        <span>/</span>
        <span className="text-luxury-white font-semibold">Consignment Archive</span>
      </nav>

      {/* Header Bar */}
      <div className="border-b border-luxury-border pb-6 mb-8 flex flex-col sm:flex-row sm:items-end justify-between gap-4">
        <div>
          <span className="text-xs font-semibold tracking-luxury-widest uppercase text-luxury-gold">
            Historical Records
          </span>
          <h1 className="font-serif text-3xl sm:text-4xl font-bold text-luxury-white mt-1">
            Orders &amp; Consignments
          </h1>
        </div>

        {/* Filter Tabs */}
        <div className="flex flex-wrap gap-2 text-xs">
          {['ALL', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED'].map((st) => (
            <button
              key={st}
              type="button"
              onClick={() => setSelectedStatus(st)}
              className={`px-3 py-1.5 rounded-sm uppercase tracking-wider font-semibold transition-colors ${
                selectedStatus === st
                  ? 'bg-luxury-gold text-luxury-black'
                  : 'bg-luxury-surface border border-luxury-border text-luxury-muted hover:text-luxury-white'
              }`}
            >
              {st}
            </button>
          ))}
        </div>
      </div>

      {isLoading ? (
        <div className="space-y-4">
          <Skeleton className="h-40 w-full" />
          <Skeleton className="h-40 w-full" />
        </div>
      ) : orders.length === 0 ? (
        <div className="text-center py-20 bg-luxury-surface border border-luxury-border rounded-sm p-8">
          <Package size={36} className="text-luxury-gold mx-auto mb-3" />
          <h3 className="font-serif font-bold text-xl text-luxury-white">No Consignments Discovered</h3>
          <p className="text-xs text-luxury-muted max-w-sm mx-auto mt-1 mb-6">
            You do not have any acquisitions matching this status filter.
          </p>
          <Link to="/products">
            <Button variant="gold" size="sm">Explore Catalog</Button>
          </Link>
        </div>
      ) : (
        <div className="space-y-6">
          {orders.map((order) => {
            const isExpanded = expandedOrderId === order.id;
            const isCancellable = order.status === 'PLACED' || order.status === 'CONFIRMED' || order.status === 'PROCESSING';
            const currentStageIdx = getStageIndex(order.status);
            const isCancelled = order.status === 'CANCELLED';

            return (
              <div
                key={order.id}
                className="bg-luxury-surface border border-luxury-border rounded-sm overflow-hidden transition-all"
              >
                {/* Header Summary Row */}
                <div
                  onClick={() => setExpandedOrderId(isExpanded ? null : order.id)}
                  className="p-5 sm:p-6 bg-luxury-elevated/40 cursor-pointer flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-luxury-border"
                >
                  <div className="flex items-center gap-4">
                    <div className="w-10 h-10 rounded-sm bg-luxury-surface border border-luxury-border flex items-center justify-center text-luxury-gold shrink-0">
                      <Package size={20} />
                    </div>
                    <div>
                      <div className="flex items-center gap-3">
                        <span className="font-serif font-bold text-base text-luxury-white">
                          Consignment #{order.id}
                        </span>
                        {getStatusBadge(order.status)}
                      </div>
                      <span className="text-xs text-luxury-muted block mt-0.5">
                        {new Date(order.createdAt).toLocaleDateString('en-US', {
                          year: 'numeric',
                          month: 'long',
                          day: 'numeric',
                          hour: '2-digit',
                          minute: '2-digit',
                        })}
                      </span>
                    </div>
                  </div>

                  <div className="flex items-center gap-4 self-end sm:self-auto">
                    <div className="text-right">
                      <span className="text-[10px] text-luxury-muted uppercase tracking-wider block">Total Amount</span>
                      <span className="font-serif font-bold text-base text-luxury-gold">
                        ₹{Number(order.totalAmount).toFixed(2)}
                      </span>
                    </div>
                    <Link
                      to={`/account/orders/${order.id}/track`}
                      onClick={(e) => e.stopPropagation()}
                      className="px-3 py-1.5 text-xs font-semibold text-luxury-gold border border-luxury-gold/50 rounded hover:bg-luxury-gold hover:text-luxury-black transition-colors flex items-center gap-1.5 shrink-0"
                    >
                      <Package size={13} />
                      <span>Track</span>
                    </Link>
                    {isExpanded ? <ChevronUp size={18} className="text-luxury-muted" /> : <ChevronDown size={18} className="text-luxury-muted" />}
                  </div>
                </div>

                {/* Collapsible Details Body */}
                {isExpanded && (
                  <div className="p-6 space-y-6 animate-fade-in">
                    {/* Visual Order Lifecycle Stepper */}
                    {!isCancelled ? (
                      <div className="py-4 border-b border-luxury-border">
                        <span className="text-[11px] font-semibold uppercase tracking-widest text-luxury-gold block mb-4">
                          Consignment Milestone Progression
                        </span>
                        <div className="grid grid-cols-5 gap-2 relative">
                          {orderStages.map((stage, idx) => {
                            const isDone = currentStageIdx >= idx;
                            const isCurrent = currentStageIdx === idx;

                            return (
                              <div key={stage} className="flex flex-col items-center text-center">
                                <div
                                  className={`w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold transition-all ${
                                    isDone
                                      ? 'bg-luxury-gold text-luxury-black'
                                      : 'bg-luxury-surface border border-luxury-border text-luxury-muted'
                                  } ${isCurrent ? 'ring-2 ring-luxury-gold ring-offset-2 ring-offset-luxury-black' : ''}`}
                                >
                                  {isDone ? <CheckCircle size={14} /> : idx + 1}
                                </div>
                                <span className="text-[10px] uppercase tracking-wider text-luxury-muted mt-2 font-semibold line-clamp-1">
                                  {stage.toLowerCase()}
                                </span>
                              </div>
                            );
                          })}
                        </div>
                      </div>
                    ) : (
                      <div className="p-4 bg-rose-500/10 border border-rose-500/30 rounded-sm flex items-center gap-3 text-xs text-rose-300">
                        <XCircle size={18} className="shrink-0 text-rose-400" />
                        <span>This consignment was cancelled. Stock has been restored and payment cleared for refund.</span>
                      </div>
                    )}

                    {/* Consignment Line Items */}
                    <div>
                      <h4 className="text-xs font-semibold uppercase tracking-wider text-luxury-gold mb-3">
                        Acquired Artifacts ({order.items.length})
                      </h4>
                      <div className="bg-luxury-elevated/20 border border-luxury-border rounded-sm divide-y divide-luxury-border">
                        {order.items.map((item) => (
                          <div key={item.id} className="p-3 sm:p-4 flex items-center justify-between gap-4">
                            <div className="flex items-center gap-3">
                              {item.imageUrl && (
                                <img
                                  src={item.imageUrl}
                                  alt={item.productName}
                                  className="w-12 h-12 rounded-sm object-cover bg-luxury-surface border border-luxury-border"
                                />
                              )}
                              <div>
                                <Link
                                  to={`/products/${item.productId}`}
                                  className="font-serif font-bold text-sm text-luxury-white hover:text-luxury-gold transition-colors line-clamp-1"
                                >
                                  {item.productName}
                                </Link>
                                <span className="text-xs text-luxury-muted">
                                  Qty: {item.quantity} • ${item.price.toFixed(2)} each
                                </span>
                              </div>
                            </div>
                            <span className="font-serif font-bold text-sm text-luxury-white">
                              ${item.subtotal.toFixed(2)}
                            </span>
                          </div>
                        ))}
                      </div>
                    </div>

                    {/* Metadata & Actions */}
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 pt-4 border-t border-luxury-border text-xs text-luxury-muted">
                      <div>
                        <span className="text-luxury-gold font-semibold uppercase tracking-wider block mb-1">
                          Delivery Residence
                        </span>
                        <p className="text-luxury-white">{order.shippingAddress}</p>
                        {order.notes && (
                          <p className="text-luxury-subtle mt-1 italic">
                            Concierge Notes: "{order.notes}"
                          </p>
                        )}
                      </div>

                      <div className="space-y-2">
                        <span className="text-luxury-gold font-semibold uppercase tracking-wider block mb-1">
                          Payment &amp; Clearance
                        </span>
                        <div className="flex justify-between">
                          <span>Method</span>
                          <span className="text-luxury-white font-medium">{order.payment?.method || 'SIMULATED'}</span>
                        </div>
                        {order.payment?.transactionId && (
                          <div className="flex justify-between">
                            <span>Transaction ID</span>
                            <span className="text-luxury-white font-mono text-[11px]">{order.payment.transactionId}</span>
                          </div>
                        )}
                        <div className="flex justify-between">
                          <span>Payment Status</span>
                          <span className="text-luxury-gold font-bold">{order.payment?.status || 'CONFIRMED'}</span>
                        </div>
                      </div>
                    </div>

                    {/* Action Bar */}
                    <div className="pt-4 border-t border-luxury-border flex flex-wrap items-center justify-between gap-3">
                      <Link to={`/account/orders/${order.id}/track`}>
                        <Button variant="outline" size="sm" leftIcon={<Package size={14} />}>
                          Track Consignment Progression
                        </Button>
                      </Link>

                      {isCancellable && (
                        <Button
                          variant="danger"
                          size="sm"
                          onClick={() => handleCancelOrder(order.id)}
                          isLoading={cancellingOrderId === order.id}
                          leftIcon={<RotateCcw size={14} />}
                        >
                          Request Consignment Cancellation
                        </Button>
                      )}
                    </div>
                  </div>
                )}
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};
