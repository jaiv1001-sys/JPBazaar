import React, { useState, useEffect } from 'react';
import { Product, Category, ProductCreateRequest, ProductUpdateRequest } from '../../types';
import { productService } from '../../services/productService';
import { useToast } from '../../context/ToastContext';
import { Modal } from '../common/Modal';
import { Input } from '../common/Input';
import { Select } from '../common/Select';
import { Button } from '../common/Button';
import { Package, Tag, DollarSign, Plus, Trash2, Layers } from 'lucide-react';

interface ProductModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSaved: (product: Product) => void;
  categories: Category[];
  productToEdit?: Product | null;
}

interface SpecItem {
  key: string;
  value: string;
}

export const ProductModal: React.FC<ProductModalProps> = ({
  isOpen,
  onClose,
  onSaved,
  categories,
  productToEdit,
}) => {
  const { showToast } = useToast();

  const [name, setName] = useState('');
  const [categoryId, setCategoryId] = useState<number>(categories[0]?.id || 1);
  const [brand, setBrand] = useState('');
  const [sku, setSku] = useState('');
  const [price, setPrice] = useState<string>('');
  const [originalPrice, setOriginalPrice] = useState<string>('');
  const [stockQuantity, setStockQuantity] = useState<string>('10');
  const [description, setDescription] = useState('');
  const [active, setActive] = useState(true);
  const [specificationsList, setSpecificationsList] = useState<SpecItem[]>([]);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (productToEdit) {
      setName(productToEdit.name);
      setCategoryId(productToEdit.categoryId || categories[0]?.id || 1);
      setBrand(productToEdit.brand || '');
      setSku(productToEdit.sku || '');
      setPrice(productToEdit.price.toString());
      setOriginalPrice(productToEdit.originalPrice ? productToEdit.originalPrice.toString() : '');
      setStockQuantity(productToEdit.stockQuantity.toString());
      setDescription(productToEdit.description || '');
      setActive(productToEdit.active);

      // Parse specifications
      if (productToEdit.specifications) {
        try {
          const parsed = JSON.parse(productToEdit.specifications);
          if (typeof parsed === 'object' && parsed !== null && !Array.isArray(parsed)) {
            setSpecificationsList(
              Object.entries(parsed).map(([k, v]) => ({ key: k, value: String(v) }))
            );
          } else if (Array.isArray(parsed)) {
            setSpecificationsList(parsed);
          } else {
            setSpecificationsList([{ key: 'Attributes', value: String(parsed) }]);
          }
        } catch {
          // If plain text format, split lines by colon
          const lines = productToEdit.specifications.split('\n').filter((l) => l.trim().length > 0);
          const pairs: SpecItem[] = lines.map((line) => {
            const idx = line.indexOf(':');
            return idx !== -1
              ? { key: line.substring(0, idx).trim(), value: line.substring(idx + 1).trim() }
              : { key: 'Detail', value: line.trim() };
          });
          setSpecificationsList(pairs.length > 0 ? pairs : []);
        }
      } else {
        setSpecificationsList([]);
      }
    } else {
      setName('');
      setCategoryId(categories[0]?.id || 1);
      setBrand('');
      setSku('');
      setPrice('');
      setOriginalPrice('');
      setStockQuantity('10');
      setDescription('');
      setActive(true);
      setSpecificationsList([]);
    }
  }, [productToEdit, categories, isOpen]);

  // Live calculated discount percentage
  const numPrice = parseFloat(price);
  const numOrigPrice = parseFloat(originalPrice);
  const calculatedDiscount =
    !isNaN(numPrice) && !isNaN(numOrigPrice) && numOrigPrice > numPrice && numOrigPrice > 0
      ? Math.round(((numOrigPrice - numPrice) / numOrigPrice) * 100)
      : null;

  // Specification Builder actions
  const handleAddSpecRow = () => {
    setSpecificationsList((prev) => [...prev, { key: '', value: '' }]);
  };

  const handleUpdateSpec = (index: number, field: 'key' | 'value', val: string) => {
    setSpecificationsList((prev) =>
      prev.map((item, idx) => (idx === index ? { ...item, [field]: val } : item))
    );
  };

  const handleRemoveSpec = (index: number) => {
    setSpecificationsList((prev) => prev.filter((_, idx) => idx !== index));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim() || !sku.trim() || !price || !stockQuantity) {
      showToast('Please complete all required product fields.', 'error');
      return;
    }

    const parsedPrice = Number(price);
    const parsedStock = parseInt(stockQuantity, 10);
    const parsedOriginalPrice = originalPrice.trim() ? Number(originalPrice) : undefined;

    if (isNaN(parsedPrice) || parsedPrice <= 0) {
      showToast('Please specify a valid monetary price.', 'error');
      return;
    }

    if (parsedOriginalPrice !== undefined && (isNaN(parsedOriginalPrice) || parsedOriginalPrice < 0)) {
      showToast('Please specify a valid non-negative original price.', 'error');
      return;
    }

    // Build specifications JSON
    let serializedSpecs: string | undefined = undefined;
    const validSpecs = specificationsList.filter((s) => s.key.trim().length > 0);
    if (validSpecs.length > 0) {
      const specObj: Record<string, string> = {};
      validSpecs.forEach((s) => {
        specObj[s.key.trim()] = s.value.trim();
      });
      serializedSpecs = JSON.stringify(specObj);
    }

    setIsSubmitting(true);
    try {
      let saved: Product;
      if (productToEdit) {
        const updatePayload: ProductUpdateRequest = {
          name: name.trim(),
          categoryId,
          brand: brand.trim() || undefined,
          sku: sku.trim(),
          price: parsedPrice,
          originalPrice: parsedOriginalPrice,
          specifications: serializedSpecs,
          stockQuantity: parsedStock,
          description: description.trim() || undefined,
          active,
        };
        saved = await productService.updateProduct(productToEdit.id, updatePayload);
        showToast(`Artifact "${saved.name}" updated.`, 'success');
      } else {
        const createPayload: ProductCreateRequest = {
          name: name.trim(),
          categoryId,
          brand: brand.trim() || undefined,
          sku: sku.trim(),
          price: parsedPrice,
          originalPrice: parsedOriginalPrice,
          specifications: serializedSpecs,
          stockQuantity: parsedStock,
          description: description.trim() || undefined,
          active,
        };
        saved = await productService.createProduct(createPayload);
        showToast(`New artifact "${saved.name}" added to catalog.`, 'success');
      }
      onSaved(saved);
      onClose();
    } catch (err: any) {
      showToast(err.message || 'Failed to save product.', 'error');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      maxWidth="2xl"
      title={productToEdit ? 'Amend Catalog Artifact' : 'Catalog New Luxury Artifact'}
    >
      <form onSubmit={handleSubmit} className="space-y-4 py-2">
        <Input
          label="Artifact Display Name"
          placeholder="e.g. Bang &amp; Olufsen Beoplay H95"
          value={name}
          onChange={(e) => setName(e.target.value)}
          leftIcon={<Package size={16} />}
          required
        />

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <Select
            label="Department / Category"
            value={categoryId.toString()}
            onChange={(e) => setCategoryId(Number(e.target.value))}
            options={categories.map((c) => ({
              value: c.id.toString(),
              label: c.name,
            }))}
          />

          <Input
            label="Brand / Atelier"
            placeholder="e.g. Sony, Apple, Rolex, BoAt..."
            value={brand}
            onChange={(e) => setBrand(e.target.value)}
            leftIcon={<Tag size={16} />}
          />
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <Input
            label="Stock Keeping Unit (SKU)"
            placeholder="e.g. ELEC-SONY-001"
            value={sku}
            onChange={(e) => setSku(e.target.value.toUpperCase())}
            required
          />

          <div>
            <Input
              label="Selling Price"
              type="number"
              step="0.01"
              placeholder="199.99"
              value={price}
              onChange={(e) => setPrice(e.target.value)}
              leftIcon={<DollarSign size={16} />}
              required
            />
            {calculatedDiscount !== null && (
              <span className="inline-block mt-1 text-[11px] font-bold text-emerald-400 bg-emerald-950/60 border border-emerald-500/30 px-2 py-0.5 rounded-sm">
                {calculatedDiscount}% OFF
              </span>
            )}
          </div>

          <Input
            label="Original Price / MRP"
            type="number"
            step="0.01"
            placeholder="249.99"
            value={originalPrice}
            onChange={(e) => setOriginalPrice(e.target.value)}
            leftIcon={<DollarSign size={16} />}
          />
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <Input
            label="Warehouse Stock"
            type="number"
            placeholder="50"
            value={stockQuantity}
            onChange={(e) => setStockQuantity(e.target.value)}
            required
          />

          <div className="flex items-center gap-2 pt-6">
            <input
              type="checkbox"
              id="activeCheckbox"
              checked={active}
              onChange={(e) => setActive(e.target.checked)}
              className="w-4 h-4 rounded-sm border-luxury-border bg-luxury-surface text-luxury-gold focus:ring-luxury-gold"
            />
            <label htmlFor="activeCheckbox" className="text-xs text-luxury-muted select-none cursor-pointer">
              Publish actively in public storefront catalog
            </label>
          </div>
        </div>

        <div className="space-y-1">
          <label className="text-xs text-luxury-muted block">Artifact Narrative &amp; Description</label>
          <textarea
            rows={3}
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="Provide architectural narrative, craftsmanship details, materials..."
            className="w-full bg-luxury-elevated border border-luxury-border rounded-sm p-3 text-xs text-luxury-white placeholder:text-luxury-subtle focus:outline-none focus:border-luxury-gold"
          />
        </div>

        {/* Specifications Key-Value Builder */}
        <div className="space-y-2 pt-2 border-t border-luxury-border/60">
          <div className="flex items-center justify-between">
            <label className="text-xs font-semibold text-luxury-gold uppercase tracking-wider flex items-center gap-1.5">
              <Layers size={14} />
              Technical Specifications &amp; Attributes
            </label>
            <Button
              type="button"
              variant="dark"
              size="sm"
              onClick={handleAddSpecRow}
              leftIcon={<Plus size={12} />}
            >
              Add Spec
            </Button>
          </div>

          {specificationsList.length === 0 ? (
            <p className="text-[11px] text-luxury-muted italic py-1">
              No custom specifications configured. Click "Add Spec" to add attributes (e.g. Battery: 30h, Connectivity: Bluetooth 5.2).
            </p>
          ) : (
            <div className="space-y-2 max-h-48 overflow-y-auto pr-1">
              {specificationsList.map((item, idx) => (
                <div key={idx} className="flex items-center gap-2">
                  <input
                    type="text"
                    placeholder="Attribute (e.g. Material)"
                    value={item.key}
                    onChange={(e) => handleUpdateSpec(idx, 'key', e.target.value)}
                    className="flex-1 bg-luxury-elevated border border-luxury-border rounded-sm px-2.5 py-1.5 text-xs text-luxury-white placeholder:text-luxury-subtle focus:outline-none focus:border-luxury-gold"
                  />
                  <input
                    type="text"
                    placeholder="Value (e.g. Italian Leather)"
                    value={item.value}
                    onChange={(e) => handleUpdateSpec(idx, 'value', e.target.value)}
                    className="flex-1 bg-luxury-elevated border border-luxury-border rounded-sm px-2.5 py-1.5 text-xs text-luxury-white placeholder:text-luxury-subtle focus:outline-none focus:border-luxury-gold"
                  />
                  <button
                    type="button"
                    onClick={() => handleRemoveSpec(idx)}
                    className="p-1.5 text-luxury-muted hover:text-rose-400 transition-colors"
                    title="Remove specification"
                  >
                    <Trash2 size={14} />
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="flex justify-end gap-3 pt-6 border-t border-luxury-border">
          <Button type="button" variant="ghost" onClick={onClose} disabled={isSubmitting}>
            Cancel
          </Button>
          <Button type="submit" variant="gold" isLoading={isSubmitting}>
            {productToEdit ? 'Save Amendments' : 'Publish to Catalog'}
          </Button>
        </div>
      </form>
    </Modal>
  );
};
