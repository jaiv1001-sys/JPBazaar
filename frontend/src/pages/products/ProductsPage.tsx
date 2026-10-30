import React, { useEffect, useState, useCallback } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { Product, Category, PageResponse } from '../../types';
import { productService, ProductFilterParams } from '../../services/productService';
import { categoryService } from '../../services/categoryService';
import { ProductCard } from '../../components/product/ProductCard';
import { QuickViewModal } from '../../components/product/QuickViewModal';
import { Button } from '../../components/common/Button';
import { Input } from '../../components/common/Input';
import { Select } from '../../components/common/Select';
import { Skeleton } from '../../components/common/Skeleton';
import {
  SlidersHorizontal,
  Search,
  X,
  ChevronLeft,
  ChevronRight,
  Sparkles,
  RotateCcw,
} from 'lucide-react';

export const ProductsPage: React.FC = () => {
  const [searchParams, setSearchParams] = useSearchParams();

  // URL Query Parameters
  const initialKeyword = searchParams.get('search') || searchParams.get('keyword') || '';
  const initialCategoryId = searchParams.get('categoryId') ? Number(searchParams.get('categoryId')) : undefined;

  // State
  const [productsData, setProductsData] = useState<PageResponse<Product> | null>(null);
  const [categories, setCategories] = useState<Category[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  // Filters
  const [keyword, setKeyword] = useState<string>(initialKeyword);
  const [selectedCategoryId, setSelectedCategoryId] = useState<number | undefined>(initialCategoryId);
  const [selectedBrand, setSelectedBrand] = useState<string>('');
  const [minPrice, setMinPrice] = useState<string>('');
  const [maxPrice, setMaxPrice] = useState<string>('');
  const [sortOption, setSortOption] = useState<string>('createdAt_desc');
  const [currentPage, setCurrentPage] = useState<number>(0);
  const pageSize = 12;

  // Mobile Filter Drawer Toggle
  const [isMobileFiltersOpen, setIsMobileFiltersOpen] = useState(false);

  // Quick View
  const [quickViewProduct, setQuickViewProduct] = useState<Product | null>(null);
  const [isQuickViewOpen, setIsQuickViewOpen] = useState(false);

  // Load categories on mount
  useEffect(() => {
    categoryService.getCategories()
      .then(setCategories)
      .catch((err) => console.error('Failed to load categories:', err));
  }, []);

  // Sync state if URL changes
  useEffect(() => {
    const urlSearch = searchParams.get('search') || searchParams.get('keyword') || '';
    const urlCat = searchParams.get('categoryId') ? Number(searchParams.get('categoryId')) : undefined;
    setKeyword(urlSearch);
    setSelectedCategoryId(urlCat);
    setCurrentPage(0);
  }, [searchParams]);

  // Fetch products from backend
  const fetchProducts = useCallback(async () => {
    setIsLoading(true);
    try {
      const [sortBy, sortDir] = sortOption.split('_') as [string, 'asc' | 'desc'];

      const params: ProductFilterParams = {
        page: currentPage,
        size: pageSize,
        sortBy: sortBy || 'id',
        sortDir: sortDir || 'desc',
        active: true,
      };

      if (keyword.trim()) params.keyword = keyword.trim();
      if (selectedCategoryId) params.categoryId = selectedCategoryId;
      if (selectedBrand.trim()) params.brand = selectedBrand.trim();
      if (minPrice && !isNaN(Number(minPrice))) params.minPrice = Number(minPrice);
      if (maxPrice && !isNaN(Number(maxPrice))) params.maxPrice = Number(maxPrice);

      const response = await productService.getProducts(params);
      setProductsData(response);
    } catch (err) {
      console.error('Failed to load products:', err);
    } finally {
      setIsLoading(false);
    }
  }, [currentPage, pageSize, sortOption, keyword, selectedCategoryId, selectedBrand, minPrice, maxPrice]);

  useEffect(() => {
    fetchProducts();
  }, [fetchProducts]);

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setCurrentPage(0);
    const newParams = new URLSearchParams(searchParams);
    if (keyword.trim()) {
      newParams.set('search', keyword.trim());
    } else {
      newParams.delete('search');
      newParams.delete('keyword');
    }
    setSearchParams(newParams);
  };

  const handleClearFilters = () => {
    setKeyword('');
    setSelectedCategoryId(undefined);
    setSelectedBrand('');
    setMinPrice('');
    setMaxPrice('');
    setSortOption('createdAt_desc');
    setCurrentPage(0);
    setSearchParams({});
  };

  const handleCategorySelect = (catId?: number) => {
    setSelectedCategoryId(catId);
    setCurrentPage(0);
    const newParams = new URLSearchParams(searchParams);
    if (catId) {
      newParams.set('categoryId', catId.toString());
    } else {
      newParams.delete('categoryId');
    }
    setSearchParams(newParams);
  };

  const hasActiveFilters = Boolean(
    keyword || selectedCategoryId || selectedBrand || minPrice || maxPrice || sortOption !== 'createdAt_desc'
  );

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 w-full flex-1 flex flex-col">
      {/* Breadcrumb Navigation */}
      <nav className="flex items-center gap-2 text-xs text-luxury-muted uppercase tracking-wider mb-6">
        <Link to="/" className="hover:text-luxury-gold transition-colors">Sanctuary</Link>
        <span>/</span>
        <span className="text-luxury-white font-semibold">Curated Collections</span>
        {selectedCategoryId && categories.find((c) => c.id === selectedCategoryId) && (
          <>
            <span>/</span>
            <span className="text-luxury-gold">
              {categories.find((c) => c.id === selectedCategoryId)?.name}
            </span>
          </>
        )}
      </nav>

      {/* Catalog Header Bar */}
      <div className="flex flex-col md:flex-row md:items-end justify-between border-b border-luxury-border pb-6 mb-8 gap-4">
        <div>
          <span className="text-xs font-semibold tracking-luxury-widest uppercase text-luxury-gold">
            Atelier Inventory
          </span>
          <h1 className="font-serif text-3xl sm:text-4xl font-bold text-luxury-white mt-1">
            Catalog of Distinction
          </h1>
          <p className="text-xs text-luxury-muted mt-1">
            {productsData ? `Displaying ${productsData.totalElements} verified luxury artifacts` : 'Scanning inventory...'}
          </p>
        </div>

        {/* Search & Sort Controls */}
        <div className="flex flex-wrap items-center gap-3">
          {/* Mobile Filter Button */}
          <Button
            type="button"
            variant="outline"
            size="sm"
            onClick={() => setIsMobileFiltersOpen(!isMobileFiltersOpen)}
            className="lg:hidden flex items-center gap-2"
          >
            <SlidersHorizontal size={14} />
            Filters {hasActiveFilters && '•'}
          </Button>

          {/* Sort Dropdown */}
          <div className="w-48">
            <Select
              value={sortOption}
              onChange={(e) => {
                setSortOption(e.target.value);
                setCurrentPage(0);
              }}
              options={[
                { value: 'createdAt_desc', label: 'Newest Acquisitions' },
                { value: 'price_asc', label: 'Price: Low to High' },
                { value: 'price_desc', label: 'Price: High to Low' },
                { value: 'name_asc', label: 'Alphabetical: A to Z' },
                { value: 'rating_desc', label: 'Client Rating' },
              ]}
            />
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-8 items-start">
        {/* Desktop Filter Sidebar */}
        <aside
          className={`lg:block ${
            isMobileFiltersOpen
              ? 'fixed inset-0 z-50 bg-luxury-black/95 p-6 overflow-y-auto'
              : 'hidden'
          } lg:relative lg:p-0 lg:bg-transparent space-y-6 bg-luxury-surface/50 border border-luxury-border p-6 rounded-sm`}
        >
          {isMobileFiltersOpen && (
            <div className="flex items-center justify-between pb-4 border-b border-luxury-border lg:hidden">
              <h3 className="font-serif text-lg font-bold text-luxury-white">Filter Catalog</h3>
              <button
                onClick={() => setIsMobileFiltersOpen(false)}
                className="text-luxury-muted hover:text-luxury-white p-1"
              >
                <X size={20} />
              </button>
            </div>
          )}

          {/* Search by Keyword */}
          <div>
            <h4 className="text-xs font-semibold uppercase tracking-widest text-luxury-gold mb-3">
              Search Catalog
            </h4>
            <form onSubmit={handleSearchSubmit} className="relative">
              <Input
                type="text"
                placeholder="Product, brand, SKU..."
                value={keyword}
                onChange={(e) => setKeyword(e.target.value)}
                leftIcon={<Search size={14} />}
              />
            </form>
          </div>

          {/* Category Filter */}
          <div className="pt-4 border-t border-luxury-border">
            <h4 className="text-xs font-semibold uppercase tracking-widest text-luxury-gold mb-3">
              Department
            </h4>
            <div className="space-y-1 text-xs">
              <button
                type="button"
                onClick={() => handleCategorySelect(undefined)}
                className={`w-full text-left px-3 py-2 rounded-sm transition-colors flex items-center justify-between ${
                  !selectedCategoryId
                    ? 'bg-luxury-gold text-luxury-black font-bold'
                    : 'text-luxury-muted hover:text-luxury-white hover:bg-luxury-elevated'
                }`}
              >
                <span>All Collections</span>
                <Sparkles size={12} />
              </button>
              {categories.map((cat) => (
                <button
                  key={cat.id}
                  type="button"
                  onClick={() => handleCategorySelect(cat.id)}
                  className={`w-full text-left px-3 py-2 rounded-sm transition-colors flex items-center justify-between ${
                    selectedCategoryId === cat.id
                      ? 'bg-luxury-gold text-luxury-black font-bold'
                      : 'text-luxury-muted hover:text-luxury-white hover:bg-luxury-elevated'
                  }`}
                >
                  <span>{cat.name}</span>
                </button>
              ))}
            </div>
          </div>

          {/* Price Range Filter */}
          <div className="pt-4 border-t border-luxury-border">
            <h4 className="text-xs font-semibold uppercase tracking-widest text-luxury-gold mb-3">
              Price Range ($)
            </h4>
            <div className="grid grid-cols-2 gap-2">
              <Input
                type="number"
                placeholder="Min"
                value={minPrice}
                onChange={(e) => {
                  setMinPrice(e.target.value);
                  setCurrentPage(0);
                }}
              />
              <Input
                type="number"
                placeholder="Max"
                value={maxPrice}
                onChange={(e) => {
                  setMaxPrice(e.target.value);
                  setCurrentPage(0);
                }}
              />
            </div>
          </div>

          {/* Brand Filter */}
          <div className="pt-4 border-t border-luxury-border">
            <h4 className="text-xs font-semibold uppercase tracking-widest text-luxury-gold mb-3">
              Brand / Manufacturer
            </h4>
            <Input
              type="text"
              placeholder="e.g. Sony, Apple, Bose..."
              value={selectedBrand}
              onChange={(e) => {
                setSelectedBrand(e.target.value);
                setCurrentPage(0);
              }}
            />
          </div>

          {/* Clear All Filters Action */}
          {hasActiveFilters && (
            <div className="pt-4 border-t border-luxury-border">
              <Button
                type="button"
                variant="outline"
                size="sm"
                fullWidth
                onClick={() => {
                  handleClearFilters();
                  if (isMobileFiltersOpen) setIsMobileFiltersOpen(false);
                }}
                leftIcon={<RotateCcw size={14} />}
              >
                Reset All Filters
              </Button>
            </div>
          )}

          {isMobileFiltersOpen && (
            <div className="pt-4">
              <Button
                type="button"
                variant="gold"
                fullWidth
                onClick={() => setIsMobileFiltersOpen(false)}
              >
                Apply Filters
              </Button>
            </div>
          )}
        </aside>

        {/* Product Grid & Pagination */}
        <main className="lg:col-span-3 flex flex-col justify-between min-h-[500px]">
          {isLoading ? (
            <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-6">
              {[1, 2, 3, 4, 5, 6, 7, 8, 9].map((i) => (
                <div key={i} className="bg-luxury-surface border border-luxury-border p-4 rounded-sm space-y-4">
                  <Skeleton className="aspect-square w-full rounded-sm" />
                  <Skeleton className="h-4 w-1/4" />
                  <Skeleton className="h-6 w-3/4" />
                  <Skeleton className="h-5 w-1/2" />
                </div>
              ))}
            </div>
          ) : productsData && productsData.content && productsData.content.length > 0 ? (
            <div className="space-y-10">
              <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-6">
                {productsData.content.map((product) => (
                  <ProductCard
                    key={product.id}
                    product={product}
                    onQuickView={(p) => {
                      setQuickViewProduct(p);
                      setIsQuickViewOpen(true);
                    }}
                  />
                ))}
              </div>

              {/* Pagination Controls */}
              {productsData.totalPages > 1 && (
                <div className="flex items-center justify-between border-t border-luxury-border pt-6">
                  <span className="text-xs text-luxury-muted">
                    Page <strong className="text-luxury-white">{currentPage + 1}</strong> of{' '}
                    <strong className="text-luxury-white">{productsData.totalPages}</strong> ({productsData.totalElements} artifacts)
                  </span>

                  <div className="flex items-center gap-2">
                    <Button
                      type="button"
                      variant="dark"
                      size="sm"
                      onClick={() => setCurrentPage((p) => Math.max(0, p - 1))}
                      disabled={currentPage === 0}
                      leftIcon={<ChevronLeft size={16} />}
                    >
                      Previous
                    </Button>

                    <div className="hidden sm:flex items-center gap-1">
                      {Array.from({ length: productsData.totalPages }).map((_, idx) => (
                        <button
                          key={idx}
                          type="button"
                          onClick={() => setCurrentPage(idx)}
                          className={`w-8 h-8 rounded-sm text-xs font-bold transition-colors ${
                            currentPage === idx
                              ? 'bg-luxury-gold text-luxury-black'
                              : 'bg-luxury-surface border border-luxury-border text-luxury-muted hover:text-luxury-white'
                          }`}
                        >
                          {idx + 1}
                        </button>
                      ))}
                    </div>

                    <Button
                      type="button"
                      variant="dark"
                      size="sm"
                      onClick={() => setCurrentPage((p) => Math.min(productsData.totalPages - 1, p + 1))}
                      disabled={currentPage >= productsData.totalPages - 1}
                      rightIcon={<ChevronRight size={16} />}
                    >
                      Next
                    </Button>
                  </div>
                </div>
              )}
            </div>
          ) : (
            /* Empty State */
            <div className="text-center py-20 bg-luxury-surface border border-luxury-border rounded-sm p-8">
              <div className="w-16 h-16 rounded-full bg-luxury-elevated border border-luxury-border flex items-center justify-center mx-auto text-luxury-gold mb-4">
                <Search size={28} />
              </div>
              <h3 className="font-serif text-xl font-bold text-luxury-white">
                No Artifacts Discovered
              </h3>
              <p className="text-xs text-luxury-muted max-w-sm mx-auto mt-2 mb-6 leading-relaxed">
                We could not find any matching items for your criteria. Try adjusting your search keyword or clearing active filters.
              </p>
              <Button variant="gold" size="sm" onClick={handleClearFilters}>
                Clear All Criteria
              </Button>
            </div>
          )}
        </main>
      </div>

      {/* Quick View Modal */}
      <QuickViewModal
        product={quickViewProduct}
        isOpen={isQuickViewOpen}
        onClose={() => {
          setIsQuickViewOpen(false);
          setQuickViewProduct(null);
        }}
      />
    </div>
  );
};
