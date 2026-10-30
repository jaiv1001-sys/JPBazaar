import React, { useEffect, useState, useCallback } from 'react';
import { Link } from 'react-router-dom';
import {
  AdminDashboardStats,
  Product,
  Category,
  Order,
  OrderStatus,
  PageResponse,
} from '../../types';
import { adminService } from '../../services/adminService';
import { productService } from '../../services/productService';
import { categoryService } from '../../services/categoryService';
import { useToast } from '../../context/ToastContext';
import { ProductModal } from '../../components/admin/ProductModal';
import { CategoryModal } from '../../components/admin/CategoryModal';
import { Button } from '../../components/common/Button';
import { Badge } from '../../components/common/Badge';
import { Input } from '../../components/common/Input';
import { Select } from '../../components/common/Select';
import { Skeleton } from '../../components/common/Skeleton';
import {
  BarChart3,
  Package,
  Layers,
  ShoppingBag,
  Users,
  AlertTriangle,
  Plus,
  Trash2,
  Edit2,
  Search,
  ExternalLink,
  ChevronRight,
  TrendingUp,
} from 'lucide-react';

export const AdminDashboardPage: React.FC = () => {
  const { showToast } = useToast();

  const [activeTab, setActiveTab] = useState<'overview' | 'products' | 'categories' | 'orders'>('overview');

  // Stats State
  const [stats, setStats] = useState<AdminDashboardStats | null>(null);
  const [isLoadingStats, setIsLoadingStats] = useState(true);

  // Products State
  const [productsData, setProductsData] = useState<PageResponse<Product> | null>(null);
  const [productSearch, setProductSearch] = useState('');
  const [productPage, setProductPage] = useState(0);
  const [isLoadingProducts, setIsLoadingProducts] = useState(false);
  const [isProductModalOpen, setIsProductModalOpen] = useState(false);
  const [productToEdit, setProductToEdit] = useState<Product | null>(null);

  // Categories State
  const [categories, setCategories] = useState<Category[]>([]);
  const [isLoadingCategories, setIsLoadingCategories] = useState(false);
  const [isCategoryModalOpen, setIsCategoryModalOpen] = useState(false);
  const [categoryToEdit, setCategoryToEdit] = useState<Category | null>(null);

  // Orders State
  const [ordersData, setOrdersData] = useState<PageResponse<Order> | null>(null);
  const [orderStatusFilter, setOrderStatusFilter] = useState<string>('');
  const [orderPage, setOrderPage] = useState(0);
  const [isLoadingOrders, setIsLoadingOrders] = useState(false);
  const [updatingOrderId, setUpdatingOrderId] = useState<number | null>(null);

  // Load Overview Stats
  const fetchStats = useCallback(async () => {
    setIsLoadingStats(true);
    try {
      const data = await adminService.getDashboardStats();
      setStats(data);
    } catch (err: any) {
      console.error('Failed to load dashboard statistics:', err);
      showToast('Failed to retrieve executive statistics.', 'error');
    } finally {
      setIsLoadingStats(false);
    }
  }, [showToast]);

  // Load Products
  const fetchProducts = useCallback(async () => {
    setIsLoadingProducts(true);
    try {
      const data = await productService.getProducts({
        page: productPage,
        size: 10,
        keyword: productSearch.trim() || undefined,
        sortBy: 'id',
        sortDir: 'desc',
      });
      setProductsData(data);
    } catch (err: any) {
      console.error('Failed to load products for admin:', err);
    } finally {
      setIsLoadingProducts(false);
    }
  }, [productPage, productSearch]);

  // Load Categories
  const fetchCategories = useCallback(async () => {
    setIsLoadingCategories(true);
    try {
      const data = await categoryService.getCategories();
      setCategories(data);
    } catch (err: any) {
      console.error('Failed to load categories for admin:', err);
    } finally {
      setIsLoadingCategories(false);
    }
  }, []);

  // Load Orders
  const fetchOrders = useCallback(async () => {
    setIsLoadingOrders(true);
    try {
      const data = await adminService.getAllOrders(orderStatusFilter || undefined, orderPage, 10);
      setOrdersData(data);
    } catch (err: any) {
      console.error('Failed to load admin orders:', err);
    } finally {
      setIsLoadingOrders(false);
    }
  }, [orderPage, orderStatusFilter]);

  useEffect(() => {
    fetchStats();
    fetchCategories();
  }, [fetchStats, fetchCategories]);

  useEffect(() => {
    if (activeTab === 'products') fetchProducts();
    if (activeTab === 'categories') fetchCategories();
    if (activeTab === 'orders') fetchOrders();
  }, [activeTab, fetchProducts, fetchCategories, fetchOrders]);

  // Product Actions
  const handleDeleteProduct = async (id: number, name: string) => {
    if (!window.confirm(`Archive artifact "${name}" from catalog?`)) return;
    try {
      await productService.deleteProduct(id);
      showToast(`Artifact "${name}" archived.`, 'info');
      fetchProducts();
      fetchStats();
    } catch (err: any) {
      showToast(err.message || 'Failed to archive artifact.', 'error');
    }
  };

  // Category Actions
  const handleDeleteCategory = async (id: number, name: string) => {
    if (!window.confirm(`Delete department "${name}"? Associated products may be affected.`)) return;
    try {
      await categoryService.deleteCategory(id);
      showToast(`Department "${name}" removed.`, 'info');
      fetchCategories();
      fetchStats();
    } catch (err: any) {
      showToast(err.message || 'Cannot delete department containing active artifacts.', 'error');
    }
  };

  // Order Status Transitions
  const handleStatusChange = async (orderId: number, nextStatus: OrderStatus) => {
    setUpdatingOrderId(orderId);
    try {
      const updated = await adminService.updateOrderStatus(orderId, nextStatus);
      setOrdersData((prev) => {
        if (!prev) return null;
        return {
          ...prev,
          content: prev.content.map((o) => (o.id === orderId ? updated : o)),
        };
      });
      showToast(`Order #${orderId} transitioned to ${nextStatus}.`, 'success');
      fetchStats();
    } catch (err: any) {
      showToast(err.message || 'Invalid state transition for this order.', 'error');
    } finally {
      setUpdatingOrderId(null);
    }
  };

  const overview = stats?.overview;

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 w-full flex-1 flex flex-col">
      {/* Top Header */}
      <div className="border-b border-luxury-border pb-6 mb-8 flex flex-col sm:flex-row sm:items-end justify-between gap-4">
        <div>
          <span className="text-xs font-semibold tracking-luxury-widest uppercase text-luxury-gold">
            Executive Command Suite
          </span>
          <h1 className="font-serif text-3xl sm:text-4xl font-bold text-luxury-white mt-1">
            Boutique Administration
          </h1>
        </div>

        <Link to="/">
          <Button variant="outline" size="sm" rightIcon={<ExternalLink size={14} />}>
            View Public Storefront
          </Button>
        </Link>
      </div>

      {/* Admin Navigation Tabs */}
      <div className="flex border-b border-luxury-border mb-8 overflow-x-auto">
        {[
          { id: 'overview', label: 'Executive Analytics', icon: BarChart3 },
          { id: 'products', label: 'Catalog Inventory', icon: Package },
          { id: 'categories', label: 'Departments', icon: Layers },
          { id: 'orders', label: 'Order Fulfillment', icon: ShoppingBag },
        ].map((tab) => {
          const Icon = tab.icon;
          const isActive = activeTab === tab.id;
          return (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id as any)}
              className={`flex items-center gap-2 px-6 py-3.5 text-xs font-semibold uppercase tracking-wider transition-colors shrink-0 ${
                isActive
                  ? 'text-luxury-gold border-b-2 border-luxury-gold bg-luxury-gold/5'
                  : 'text-luxury-muted hover:text-luxury-white hover:bg-luxury-surface/50'
              }`}
            >
              <Icon size={16} />
              <span>{tab.label}</span>
            </button>
          );
        })}
      </div>

      {/* 1. OVERVIEW & ANALYTICS TAB */}
      {activeTab === 'overview' && (
        <div className="space-y-10">
          {/* KPI Cards Grid */}
          {isLoadingStats ? (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
              {[1, 2, 3, 4].map((i) => (
                <Skeleton key={i} className="h-32 w-full" />
              ))}
            </div>
          ) : overview ? (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
              <div className="bg-luxury-surface border border-luxury-border p-6 rounded-sm">
                <div className="flex items-center justify-between text-luxury-muted mb-2">
                  <span className="text-xs uppercase font-bold tracking-wider">Gross Lifetime Revenue</span>
                  <TrendingUp size={18} className="text-luxury-gold" />
                </div>
                <div className="font-serif text-3xl font-bold text-luxury-white">
                  ${Number(overview.totalRevenue).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                </div>
                <span className="text-[11px] text-emerald-400 mt-2 block font-medium">Reconciled via Ledger</span>
              </div>

              <div className="bg-luxury-surface border border-luxury-border p-6 rounded-sm">
                <div className="flex items-center justify-between text-luxury-muted mb-2">
                  <span className="text-xs uppercase font-bold tracking-wider">Lifetime Consignments</span>
                  <ShoppingBag size={18} className="text-luxury-gold" />
                </div>
                <div className="font-serif text-3xl font-bold text-luxury-white">
                  {overview.totalOrders}
                </div>
                <span className="text-[11px] text-luxury-muted mt-2 block">
                  {overview.pendingOrders} awaiting fulfillment
                </span>
              </div>

              <div className="bg-luxury-surface border border-luxury-border p-6 rounded-sm">
                <div className="flex items-center justify-between text-luxury-muted mb-2">
                  <span className="text-xs uppercase font-bold tracking-wider">Cataloged Artifacts</span>
                  <Package size={18} className="text-luxury-gold" />
                </div>
                <div className="font-serif text-3xl font-bold text-luxury-white">
                  {overview.totalProducts}
                </div>
                <span className="text-[11px] text-luxury-muted mt-2 block">
                  {categories.length} Curated Departments
                </span>
              </div>

              <div className="bg-luxury-surface border border-luxury-border p-6 rounded-sm">
                <div className="flex items-center justify-between text-luxury-muted mb-2">
                  <span className="text-xs uppercase font-bold tracking-wider">Registered Patrons</span>
                  <Users size={18} className="text-luxury-gold" />
                </div>
                <div className="font-serif text-3xl font-bold text-luxury-white">
                  {overview.totalUsers}
                </div>
                <span className="text-[11px] text-luxury-gold mt-2 block font-medium">
                  Spring Security Verified
                </span>
              </div>
            </div>
          ) : null}

          {/* Low Stock Alerts & Order Status Distribution */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
            {/* Order Status Breakdown */}
            <div className="bg-luxury-surface border border-luxury-border p-6 rounded-sm space-y-4">
              <h3 className="font-serif text-lg font-bold text-luxury-white pb-3 border-b border-luxury-border flex items-center justify-between">
                <span>Order Status Distribution</span>
                <span className="text-xs text-luxury-muted font-sans font-normal">State Machine</span>
              </h3>
              <div className="space-y-3">
                {stats?.ordersByStatus?.map((sc) => (
                  <div key={sc.status} className="flex items-center justify-between text-xs">
                    <span className="font-semibold text-luxury-muted uppercase tracking-wider">{sc.status}</span>
                    <Badge variant="dark" className="border-luxury-gold/40 text-luxury-gold font-bold">
                      {sc.count} Orders
                    </Badge>
                  </div>
                )) || <div className="text-xs text-luxury-muted">Status counts updating...</div>}
              </div>
            </div>

            {/* Low Stock Alerts */}
            <div className="bg-luxury-surface border border-luxury-border p-6 rounded-sm space-y-4">
              <h3 className="font-serif text-lg font-bold text-luxury-white pb-3 border-b border-luxury-border flex items-center justify-between">
                <span>Low Inventory Watch</span>
                <AlertTriangle size={18} className="text-amber-400" />
              </h3>
              <div className="space-y-3">
                {stats?.lowStockProducts && stats.lowStockProducts.length > 0 ? (
                  stats.lowStockProducts.map((lsp) => (
                    <div key={lsp.id} className="flex items-center justify-between text-xs border-b border-luxury-border/50 pb-2">
                      <div>
                        <span className="font-serif font-bold text-luxury-white block">{lsp.name}</span>
                        <span className="text-[10px] text-luxury-muted">SKU: {lsp.sku}</span>
                      </div>
                      <Badge variant="gold">
                        Only {lsp.stockQuantity} Left
                      </Badge>
                    </div>
                  ))
                ) : (
                  <div className="text-xs text-luxury-muted py-4 text-center">
                    All catalog items maintain healthy inventory levels.
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* Top Products & Recent Orders */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
            {/* Top Products */}
            <div className="bg-luxury-surface border border-luxury-border p-6 rounded-sm space-y-4">
              <h3 className="font-serif text-lg font-bold text-luxury-white pb-3 border-b border-luxury-border">
                Top Selling Artifacts
              </h3>
              <div className="space-y-3">
                {stats?.topSellingProducts && stats.topSellingProducts.length > 0 ? (
                  stats.topSellingProducts.map((tp) => (
                    <div key={tp.productId} className="flex items-center justify-between text-xs border-b border-luxury-border/50 pb-2">
                      <div>
                        <span className="font-serif font-bold text-luxury-white block">{tp.productName}</span>
                        <span className="text-[10px] text-luxury-muted">{tp.unitsSold} units consigned</span>
                      </div>
                      <span className="font-serif font-bold text-luxury-gold">${Number(tp.totalRevenue).toFixed(2)}</span>
                    </div>
                  ))
                ) : (
                  <div className="text-xs text-luxury-muted py-4 text-center">No sales records logged yet.</div>
                )}
              </div>
            </div>

            {/* Recent Orders */}
            <div className="bg-luxury-surface border border-luxury-border p-6 rounded-sm space-y-4">
              <h3 className="font-serif text-lg font-bold text-luxury-white pb-3 border-b border-luxury-border">
                Recent Customer Orders
              </h3>
              <div className="space-y-3">
                {stats?.recentOrders && stats.recentOrders.length > 0 ? (
                  stats.recentOrders.map((ro) => (
                    <div key={ro.id} className="flex items-center justify-between text-xs border-b border-luxury-border/50 pb-2">
                      <div>
                        <span className="font-bold text-luxury-white block">Order #{ro.id}</span>
                        <span className="text-[10px] text-luxury-muted">{ro.userEmail}</span>
                      </div>
                      <div className="text-right">
                        <span className="font-bold text-luxury-gold block">${Number(ro.totalAmount).toFixed(2)}</span>
                        <span className="text-[10px] text-luxury-muted uppercase">{ro.status}</span>
                      </div>
                    </div>
                  ))
                ) : (
                  <div className="text-xs text-luxury-muted py-4 text-center">No recent orders found.</div>
                )}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* 2. PRODUCT CATALOG MANAGEMENT TAB */}
      {activeTab === 'products' && (
        <div className="space-y-6">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-luxury-surface p-4 border border-luxury-border rounded-sm">
            <div className="flex-1 max-w-sm relative">
              <Input
                type="text"
                placeholder="Search catalog by name, brand, SKU..."
                value={productSearch}
                onChange={(e) => {
                  setProductSearch(e.target.value);
                  setProductPage(0);
                }}
                leftIcon={<Search size={14} />}
              />
            </div>
            <Button
              variant="gold"
              size="sm"
              onClick={() => {
                setProductToEdit(null);
                setIsProductModalOpen(true);
              }}
              leftIcon={<Plus size={14} />}
            >
              Add New Artifact
            </Button>
          </div>

          {isLoadingProducts ? (
            <div className="space-y-2">
              {[1, 2, 3, 4, 5].map((i) => (
                <Skeleton key={i} className="h-16 w-full" />
              ))}
            </div>
          ) : productsData && productsData.content.length > 0 ? (
            <div className="bg-luxury-surface border border-luxury-border rounded-sm overflow-x-auto">
              <table className="w-full text-left text-xs">
                <thead className="bg-luxury-elevated/60 text-luxury-gold uppercase tracking-wider border-b border-luxury-border">
                  <tr>
                    <th className="p-4">Artifact</th>
                    <th className="p-4">SKU</th>
                    <th className="p-4">Department</th>
                    <th className="p-4">Price</th>
                    <th className="p-4">Stock</th>
                    <th className="p-4">Status</th>
                    <th className="p-4 text-right">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-luxury-border">
                  {productsData.content.map((p) => (
                    <tr key={p.id} className="hover:bg-luxury-elevated/30 transition-colors">
                      <td className="p-4 font-serif font-bold text-luxury-white">
                        <Link to={`/products/${p.id}`} className="hover:text-luxury-gold flex items-center gap-1.5 group">
                          {p.name}
                          <ExternalLink size={12} className="opacity-0 group-hover:opacity-100" />
                        </Link>
                        <span className="text-[10px] text-luxury-muted font-sans block">{p.brand}</span>
                      </td>
                      <td className="p-4 font-mono text-luxury-muted">{p.sku}</td>
                      <td className="p-4 text-luxury-muted">{p.categoryName || 'General'}</td>
                      <td className="p-4 font-bold text-luxury-white">${p.price.toFixed(2)}</td>
                      <td className="p-4">
                        <span className={`font-bold ${p.stockQuantity <= 5 ? 'text-amber-400' : 'text-luxury-white'}`}>
                          {p.stockQuantity}
                        </span>
                      </td>
                      <td className="p-4">
                        {p.active ? (
                          <Badge variant="dark" size="sm" className="border-emerald-500/40 text-emerald-400">Active</Badge>
                        ) : (
                          <Badge variant="danger" size="sm">Archived</Badge>
                        )}
                      </td>
                      <td className="p-4 text-right">
                        <div className="flex items-center justify-end gap-2">
                          <button
                            onClick={() => {
                              setProductToEdit(p);
                              setIsProductModalOpen(true);
                            }}
                            className="p-1.5 text-luxury-muted hover:text-luxury-gold"
                            title="Edit Artifact"
                          >
                            <Edit2 size={14} />
                          </button>
                          <button
                            onClick={() => handleDeleteProduct(p.id, p.name)}
                            className="p-1.5 text-luxury-muted hover:text-rose-400"
                            title="Archive Artifact"
                          >
                            <Trash2 size={14} />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>

              {/* Pagination */}
              {productsData.totalPages > 1 && (
                <div className="p-4 border-t border-luxury-border flex justify-between items-center text-xs text-luxury-muted">
                  <span>Page {productPage + 1} of {productsData.totalPages}</span>
                  <div className="flex gap-2">
                    <Button
                      variant="dark"
                      size="sm"
                      onClick={() => setProductPage((p) => Math.max(0, p - 1))}
                      disabled={productPage === 0}
                    >
                      Previous
                    </Button>
                    <Button
                      variant="dark"
                      size="sm"
                      onClick={() => setProductPage((p) => Math.min(productsData.totalPages - 1, p + 1))}
                      disabled={productPage >= productsData.totalPages - 1}
                    >
                      Next
                    </Button>
                  </div>
                </div>
              )}
            </div>
          ) : (
            <div className="text-center py-16 bg-luxury-surface border border-luxury-border rounded-sm text-xs text-luxury-muted">
              No artifacts match your search query.
            </div>
          )}
        </div>
      )}

      {/* 3. CATEGORY MANAGEMENT TAB */}
      {activeTab === 'categories' && (
        <div className="space-y-6">
          <div className="flex justify-between items-center bg-luxury-surface p-4 border border-luxury-border rounded-sm">
            <div>
              <h3 className="font-serif font-bold text-base text-luxury-white">Atelier Departments</h3>
              <p className="text-xs text-luxury-muted">Manage product departments and curated collections.</p>
            </div>
            <Button
              variant="gold"
              size="sm"
              onClick={() => {
                setCategoryToEdit(null);
                setIsCategoryModalOpen(true);
              }}
              leftIcon={<Plus size={14} />}
            >
              Add Department
            </Button>
          </div>

          {isLoadingCategories ? (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Skeleton className="h-28 w-full" />
              <Skeleton className="h-28 w-full" />
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {categories.map((cat) => (
                <div key={cat.id} className="bg-luxury-surface border border-luxury-border p-6 rounded-sm flex flex-col justify-between space-y-4">
                  <div>
                    <div className="flex items-center justify-between mb-2">
                      <span className="text-[10px] uppercase font-bold tracking-widest text-luxury-gold">
                        Department #{cat.id}
                      </span>
                    </div>
                    <h4 className="font-serif font-bold text-lg text-luxury-white">{cat.name}</h4>
                    <p className="text-xs text-luxury-muted mt-1 leading-relaxed">
                      {cat.description || 'Curated luxury collection.'}
                    </p>
                  </div>

                  <div className="flex items-center justify-between pt-4 border-t border-luxury-border text-xs">
                    <Link to={`/products?categoryId=${cat.id}`} className="text-luxury-gold hover:underline flex items-center gap-1">
                      Browse Artifacts <ChevronRight size={14} />
                    </Link>
                    <div className="flex items-center gap-2">
                      <button
                        onClick={() => {
                          setCategoryToEdit(cat);
                          setIsCategoryModalOpen(true);
                        }}
                        className="p-1 text-luxury-muted hover:text-luxury-white"
                        title="Edit Department"
                      >
                        <Edit2 size={14} />
                      </button>
                      <button
                        onClick={() => handleDeleteCategory(cat.id, cat.name)}
                        className="p-1 text-luxury-muted hover:text-rose-400"
                        title="Delete Department"
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
      )}

      {/* 4. ORDER FULFILLMENT STATE MACHINE TAB */}
      {activeTab === 'orders' && (
        <div className="space-y-6">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-luxury-surface p-4 border border-luxury-border rounded-sm">
            <h3 className="font-serif font-bold text-base text-luxury-white">
              Platform Consignment Lifecycle
            </h3>
            <div className="w-56">
              <Select
                value={orderStatusFilter}
                onChange={(e) => {
                  setOrderStatusFilter(e.target.value);
                  setOrderPage(0);
                }}
                options={[
                  { value: '', label: 'All Lifecycle States' },
                  { value: 'PLACED', label: 'PLACED' },
                  { value: 'CONFIRMED', label: 'CONFIRMED' },
                  { value: 'PROCESSING', label: 'PROCESSING' },
                  { value: 'SHIPPED', label: 'SHIPPED' },
                  { value: 'DELIVERED', label: 'DELIVERED' },
                  { value: 'CANCELLED', label: 'CANCELLED' },
                ]}
              />
            </div>
          </div>

          {isLoadingOrders ? (
            <div className="space-y-2">
              {[1, 2, 3, 4].map((i) => (
                <Skeleton key={i} className="h-20 w-full" />
              ))}
            </div>
          ) : ordersData && ordersData.content.length > 0 ? (
            <div className="bg-luxury-surface border border-luxury-border rounded-sm overflow-x-auto">
              <table className="w-full text-left text-xs">
                <thead className="bg-luxury-elevated/60 text-luxury-gold uppercase tracking-wider border-b border-luxury-border">
                  <tr>
                    <th className="p-4">Order ID</th>
                    <th className="p-4">Customer</th>
                    <th className="p-4">Total</th>
                    <th className="p-4">Current Status</th>
                    <th className="p-4">State Transition</th>
                    <th className="p-4">Date</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-luxury-border">
                  {ordersData.content.map((ord) => {
                    const isUpdating = updatingOrderId === ord.id;
                    return (
                      <tr key={ord.id} className="hover:bg-luxury-elevated/30 transition-colors">
                        <td className="p-4 font-mono font-bold text-luxury-white">#{ord.id}</td>
                        <td className="p-4">
                          <span className="font-bold text-luxury-white block">{ord.userEmail}</span>
                          <span className="text-[10px] text-luxury-muted truncate block max-w-xs">{ord.shippingAddress}</span>
                        </td>
                        <td className="p-4 font-serif font-bold text-luxury-gold">
                          ${Number(ord.totalAmount).toFixed(2)}
                        </td>
                        <td className="p-4">
                          <Badge variant="dark" className="border-luxury-gold/30 text-luxury-gold font-bold">
                            {ord.status}
                          </Badge>
                        </td>
                        <td className="p-4">
                          {ord.status !== 'DELIVERED' && ord.status !== 'CANCELLED' ? (
                            <select
                              disabled={isUpdating}
                              value={ord.status}
                              onChange={(e) => handleStatusChange(ord.id, e.target.value as OrderStatus)}
                              className="bg-luxury-elevated border border-luxury-border rounded-sm px-2 py-1 text-xs text-luxury-white focus:outline-none focus:border-luxury-gold"
                            >
                              <option value={ord.status} disabled>
                                Transition...
                              </option>
                              {ord.status === 'PLACED' && <option value="CONFIRMED">CONFIRMED</option>}
                              {ord.status === 'CONFIRMED' && <option value="PROCESSING">PROCESSING</option>}
                              {ord.status === 'PROCESSING' && <option value="SHIPPED">SHIPPED</option>}
                              {ord.status === 'SHIPPED' && <option value="OUT_FOR_DELIVERY">OUT_FOR_DELIVERY</option>}
                              {(ord.status === 'SHIPPED' || ord.status === 'OUT_FOR_DELIVERY') && (
                                <option value="DELIVERED">DELIVERED</option>
                              )}
                              {(ord.status === 'PLACED' || ord.status === 'CONFIRMED' || ord.status === 'PROCESSING') && (
                                <option value="CANCELLED">CANCELLED (Restore Stock)</option>
                              )}
                            </select>
                          ) : (
                            <span className="text-[11px] text-luxury-muted italic">Terminal Lifecycle State</span>
                          )}
                        </td>
                        <td className="p-4 text-luxury-muted">
                          {new Date(ord.createdAt).toLocaleDateString()}
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>

              {ordersData.totalPages > 1 && (
                <div className="p-4 border-t border-luxury-border flex justify-between items-center text-xs text-luxury-muted">
                  <span>Page {orderPage + 1} of {ordersData.totalPages}</span>
                  <div className="flex gap-2">
                    <Button
                      variant="dark"
                      size="sm"
                      onClick={() => setOrderPage((p) => Math.max(0, p - 1))}
                      disabled={orderPage === 0}
                    >
                      Previous
                    </Button>
                    <Button
                      variant="dark"
                      size="sm"
                      onClick={() => setOrderPage((p) => Math.min(ordersData.totalPages - 1, p + 1))}
                      disabled={orderPage >= ordersData.totalPages - 1}
                    >
                      Next
                    </Button>
                  </div>
                </div>
              )}
            </div>
          ) : (
            <div className="text-center py-16 bg-luxury-surface border border-luxury-border rounded-sm text-xs text-luxury-muted">
              No orders found matching this filter.
            </div>
          )}
        </div>
      )}

      {/* Product Modal */}
      <ProductModal
        isOpen={isProductModalOpen}
        onClose={() => {
          setIsProductModalOpen(false);
          setProductToEdit(null);
        }}
        onSaved={() => {
          fetchProducts();
          fetchStats();
        }}
        categories={categories}
        productToEdit={productToEdit}
      />

      {/* Category Modal */}
      <CategoryModal
        isOpen={isCategoryModalOpen}
        onClose={() => {
          setIsCategoryModalOpen(false);
          setCategoryToEdit(null);
        }}
        onSaved={() => {
          fetchCategories();
          fetchStats();
        }}
        categoryToEdit={categoryToEdit}
      />
    </div>
  );
};
