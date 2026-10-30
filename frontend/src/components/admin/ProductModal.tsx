import React, { useState, useEffect } from 'react';
import { Product, Category, ProductCreateRequest, ProductUpdateRequest } from '../../types';
import { productService } from '../../services/productService';
import { useToast } from '../../context/ToastContext';
import { Modal } from '../common/Modal';
import { Input } from '../common/Input';
import { Select } from '../common/Select';
import { Button } from '../common/Button';
import { Package, Tag, DollarSign } from 'lucide-react';

interface ProductModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSaved: (product: Product) => void;
  categories: Category[];
  productToEdit?: Product | null;
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
  const [stockQuantity, setStockQuantity] = useState<string>('10');
  const [description, setDescription] = useState('');
  const [active, setActive] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (productToEdit) {
      setName(productToEdit.name);
      setCategoryId(productToEdit.categoryId || categories[0]?.id || 1);
      setBrand(productToEdit.brand || '');
      setSku(productToEdit.sku || '');
      setPrice(productToEdit.price.toString());
      setStockQuantity(productToEdit.stockQuantity.toString());
      setDescription(productToEdit.description || '');
      setActive(productToEdit.active);
    } else {
      setName('');
      setCategoryId(categories[0]?.id || 1);
      setBrand('');
      setSku('');
      setPrice('');
      setStockQuantity('10');
      setDescription('');
      setActive(true);
    }
  }, [productToEdit, categories, isOpen]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim() || !sku.trim() || !price || !stockQuantity) {
      showToast('Please complete all required product fields.', 'error');
      return;
    }

    const numPrice = Number(price);
    const numStock = parseInt(stockQuantity, 10);

    if (isNaN(numPrice) || numPrice <= 0) {
      showToast('Please specify a valid monetary price.', 'error');
      return;
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
          price: numPrice,
          stockQuantity: numStock,
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
          price: numPrice,
          stockQuantity: numStock,
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
      maxWidth="xl"
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
            placeholder="e.g. Sony, Apple, Rolex..."
            value={brand}
            onChange={(e) => setBrand(e.target.value)}
            leftIcon={<Tag size={16} />}
          />
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <Input
            label="Stock Keeping Unit (SKU)"
            placeholder="e.g. SONY-XM5-BLK"
            value={sku}
            onChange={(e) => setSku(e.target.value.toUpperCase())}
            required
          />

          <Input
            label="Price ($ USD)"
            type="number"
            step="0.01"
            placeholder="399.99"
            value={price}
            onChange={(e) => setPrice(e.target.value)}
            leftIcon={<DollarSign size={16} />}
            required
          />

          <Input
            label="Warehouse Stock"
            type="number"
            placeholder="50"
            value={stockQuantity}
            onChange={(e) => setStockQuantity(e.target.value)}
            required
          />
        </div>

        <div className="space-y-1">
          <label className="text-xs text-luxury-muted block">Artifact Narrative &amp; Specifications</label>
          <textarea
            rows={3}
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="Provide architectural specifications, materials, acoustic tuning, etc..."
            className="w-full bg-luxury-elevated border border-luxury-border rounded-sm p-3 text-xs text-luxury-white placeholder:text-luxury-subtle focus:outline-none focus:border-luxury-gold"
          />
        </div>

        <div className="flex items-center gap-2 pt-2">
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
