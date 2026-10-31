import React, { useState, useEffect, useCallback, useRef } from 'react';
import { Product, ProductImage } from '../../types';
import { productService } from '../../services/productService';
import { useToast } from '../../context/ToastContext';
import { Modal } from '../common/Modal';
import { Button } from '../common/Button';
import { Input } from '../common/Input';
import { Badge } from '../common/Badge';
import { Skeleton } from '../common/Skeleton';
import {
  Upload,
  Link as LinkIcon,
  Star,
  Trash2,
  Image as ImageIcon,
  CheckCircle2,
} from 'lucide-react';

interface ProductImageModalProps {
  isOpen: boolean;
  onClose: () => void;
  product: Product | null;
  onImagesUpdated: () => void;
}

export const ProductImageModal: React.FC<ProductImageModalProps> = ({
  isOpen,
  onClose,
  product,
  onImagesUpdated,
}) => {
  const { showToast } = useToast();
  const fileInputRef = useRef<HTMLInputElement>(null);

  const [images, setImages] = useState<ProductImage[]>([]);
  const [isLoadingImages, setIsLoadingImages] = useState(false);

  // Upload by File State
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [fileIsPrimary, setFileIsPrimary] = useState(false);
  const [isUploadingFile, setIsUploadingFile] = useState(false);
  const [isDragging, setIsDragging] = useState(false);

  // Upload by URL State
  const [imageUrlInput, setImageUrlInput] = useState('');
  const [urlIsPrimary, setUrlIsPrimary] = useState(false);
  const [isAddingUrl, setIsAddingUrl] = useState(false);

  // Action states
  const [updatingPrimaryId, setUpdatingPrimaryId] = useState<number | null>(null);
  const [deletingImageId, setDeletingImageId] = useState<number | null>(null);

  const fetchImages = useCallback(async () => {
    if (!product) return;
    setIsLoadingImages(true);
    try {
      const data = await productService.getProductImages(product.id);
      setImages(data);
    } catch (err: any) {
      console.error('Failed to load product images:', err);
      showToast('Could not load gallery images.', 'error');
    } finally {
      setIsLoadingImages(false);
    }
  }, [product, showToast]);

  useEffect(() => {
    if (isOpen && product) {
      fetchImages();
      setSelectedFile(null);
      setImageUrlInput('');
      setFileIsPrimary(false);
      setUrlIsPrimary(false);
    }
  }, [isOpen, product, fetchImages]);

  // Handle Drag & Drop
  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(false);
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(false);
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      handleFileSelected(e.dataTransfer.files[0]);
    }
  };

  const handleFileSelected = (file: File) => {
    // Validate file type
    const validTypes = ['image/jpeg', 'image/png', 'image/webp'];
    if (!validTypes.includes(file.type)) {
      showToast('Please select a valid image file (JPEG, PNG, or WebP).', 'error');
      return;
    }
    // Validate size (5MB max)
    if (file.size > 5 * 1024 * 1024) {
      showToast('Image file size must not exceed 5MB.', 'error');
      return;
    }
    setSelectedFile(file);
  };

  const handleFileUpload = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!product || !selectedFile) return;

    setIsUploadingFile(true);
    try {
      await productService.uploadImage(product.id, selectedFile, fileIsPrimary);
      showToast('Image uploaded successfully.', 'success');
      setSelectedFile(null);
      if (fileInputRef.current) fileInputRef.current.value = '';
      fetchImages();
      onImagesUpdated();
    } catch (err: any) {
      showToast(err.message || 'Failed to upload image file.', 'error');
    } finally {
      setIsUploadingFile(false);
    }
  };

  const handleAddImageUrl = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!product || !imageUrlInput.trim()) return;

    const trimmedUrl = imageUrlInput.trim();
    if (!trimmedUrl.startsWith('http://') && !trimmedUrl.startsWith('https://')) {
      showToast('Please provide a valid URL starting with http:// or https://', 'error');
      return;
    }

    setIsAddingUrl(true);
    try {
      await productService.addImageUrl(product.id, trimmedUrl, urlIsPrimary);
      showToast('Image URL associated with product.', 'success');
      setImageUrlInput('');
      fetchImages();
      onImagesUpdated();
    } catch (err: any) {
      showToast(err.message || 'Failed to add image URL.', 'error');
    } finally {
      setIsAddingUrl(false);
    }
  };

  const handleSetPrimary = async (imageId: number) => {
    if (!product) return;
    setUpdatingPrimaryId(imageId);
    try {
      await productService.setPrimaryImage(product.id, imageId);
      showToast('Primary thumbnail updated.', 'success');
      fetchImages();
      onImagesUpdated();
    } catch (err: any) {
      showToast(err.message || 'Failed to update primary thumbnail.', 'error');
    } finally {
      setUpdatingPrimaryId(null);
    }
  };

  const handleDeleteImage = async (imageId: number) => {
    if (!product) return;
    if (!window.confirm('Delete this image from product gallery?')) return;

    setDeletingImageId(imageId);
    try {
      await productService.deleteImage(product.id, imageId);
      showToast('Image removed from gallery.', 'info');
      fetchImages();
      onImagesUpdated();
    } catch (err: any) {
      showToast(err.message || 'Failed to delete image.', 'error');
    } finally {
      setDeletingImageId(null);
    }
  };

  if (!product) return null;

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      maxWidth="2xl"
      title={`Gallery Management – ${product.name}`}
    >
      <div className="space-y-6 py-2">
        {/* Existing Gallery Grid */}
        <div className="space-y-2">
          <div className="flex items-center justify-between">
            <h4 className="text-xs font-semibold text-luxury-gold uppercase tracking-wider flex items-center gap-1.5">
              <ImageIcon size={14} />
              Current Gallery Images ({images.length})
            </h4>
            <span className="text-[11px] text-luxury-muted">
              First image marked as primary appears on catalog cards
            </span>
          </div>

          {isLoadingImages ? (
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
              {[1, 2, 3, 4].map((i) => (
                <Skeleton key={i} className="h-28 w-full rounded-sm" />
              ))}
            </div>
          ) : images.length > 0 ? (
            <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-3 max-h-64 overflow-y-auto p-1">
              {images.map((img) => (
                <div
                  key={img.id}
                  className={`group relative rounded-sm border overflow-hidden bg-luxury-elevated flex flex-col ${
                    img.isPrimary ? 'border-luxury-gold shadow-sm shadow-luxury-gold/10' : 'border-luxury-border'
                  }`}
                >
                  <div className="aspect-square w-full overflow-hidden bg-black/40 relative">
                    <img
                      src={img.imageUrl}
                      alt="Product artifact"
                      className="w-full h-full object-cover transition-transform group-hover:scale-105"
                      onError={(e) => {
                        (e.currentTarget as HTMLImageElement).src =
                          'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400';
                      }}
                    />
                    {img.isPrimary && (
                      <div className="absolute top-1.5 left-1.5">
                        <Badge variant="gold" size="sm" className="font-bold flex items-center gap-1 text-[10px]">
                          <Star size={10} className="fill-luxury-black" />
                          Primary
                        </Badge>
                      </div>
                    )}
                  </div>

                  {/* Actions overlay / footer */}
                  <div className="p-2 bg-luxury-surface/90 border-t border-luxury-border flex items-center justify-between text-xs">
                    {!img.isPrimary ? (
                      <button
                        type="button"
                        onClick={() => handleSetPrimary(img.id)}
                        disabled={updatingPrimaryId === img.id}
                        className="text-[10px] text-luxury-muted hover:text-luxury-gold transition-colors font-medium flex items-center gap-1"
                        title="Designate as primary thumbnail"
                      >
                        <Star size={11} />
                        Set Primary
                      </button>
                    ) : (
                      <span className="text-[10px] text-luxury-gold font-medium flex items-center gap-1">
                        <CheckCircle2 size={11} />
                        Active Primary
                      </span>
                    )}

                    <button
                      type="button"
                      onClick={() => handleDeleteImage(img.id)}
                      disabled={deletingImageId === img.id}
                      className="p-1 text-luxury-muted hover:text-rose-400 transition-colors"
                      title="Remove image"
                    >
                      <Trash2 size={13} />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="p-6 border border-dashed border-luxury-border rounded-sm text-center text-xs text-luxury-muted">
              No gallery images cataloged for this product yet.
            </div>
          )}
        </div>

        {/* Upload Tabs / Options */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 pt-4 border-t border-luxury-border">
          {/* 1. Local File Upload */}
          <form onSubmit={handleFileUpload} className="space-y-3 bg-luxury-elevated/40 p-4 border border-luxury-border rounded-sm">
            <h5 className="text-xs font-semibold text-luxury-white flex items-center gap-1.5">
              <Upload size={14} className="text-luxury-gold" />
              Upload Image File
            </h5>

            <div
              onDragOver={handleDragOver}
              onDragLeave={handleDragLeave}
              onDrop={handleDrop}
              onClick={() => fileInputRef.current?.click()}
              className={`border-2 border-dashed rounded-sm p-4 text-center cursor-pointer transition-colors ${
                isDragging
                  ? 'border-luxury-gold bg-luxury-gold/5'
                  : 'border-luxury-border hover:border-luxury-gold/50 bg-luxury-surface/40'
              }`}
            >
              <input
                ref={fileInputRef}
                type="file"
                accept="image/jpeg,image/png,image/webp"
                className="hidden"
                onChange={(e) => {
                  if (e.target.files && e.target.files[0]) {
                    handleFileSelected(e.target.files[0]);
                  }
                }}
              />
              <Upload size={20} className="mx-auto text-luxury-muted mb-1" />
              <p className="text-xs text-luxury-white font-medium">
                {selectedFile ? selectedFile.name : 'Click or drag image file here'}
              </p>
              <p className="text-[10px] text-luxury-muted mt-0.5">JPEG, PNG, WebP up to 5MB</p>
            </div>

            <div className="flex items-center gap-2">
              <input
                type="checkbox"
                id="filePrimaryCheck"
                checked={fileIsPrimary}
                onChange={(e) => setFileIsPrimary(e.target.checked)}
                className="w-3.5 h-3.5 rounded-sm border-luxury-border bg-luxury-surface text-luxury-gold focus:ring-luxury-gold"
              />
              <label htmlFor="filePrimaryCheck" className="text-[11px] text-luxury-muted select-none cursor-pointer">
                Set as primary thumbnail
              </label>
            </div>

            <Button
              type="submit"
              variant="gold"
              size="sm"
              disabled={!selectedFile || isUploadingFile}
              isLoading={isUploadingFile}
              className="w-full"
            >
              Upload to Storage
            </Button>
          </form>

          {/* 2. Direct Image URL Association */}
          <form onSubmit={handleAddImageUrl} className="space-y-3 bg-luxury-elevated/40 p-4 border border-luxury-border rounded-sm">
            <h5 className="text-xs font-semibold text-luxury-white flex items-center gap-1.5">
              <LinkIcon size={14} className="text-luxury-gold" />
              Add Image from URL
            </h5>

            <Input
              label=""
              placeholder="https://images.unsplash.com/..."
              value={imageUrlInput}
              onChange={(e) => setImageUrlInput(e.target.value)}
              leftIcon={<LinkIcon size={14} />}
            />

            <div className="flex items-center gap-2">
              <input
                type="checkbox"
                id="urlPrimaryCheck"
                checked={urlIsPrimary}
                onChange={(e) => setUrlIsPrimary(e.target.checked)}
                className="w-3.5 h-3.5 rounded-sm border-luxury-border bg-luxury-surface text-luxury-gold focus:ring-luxury-gold"
              />
              <label htmlFor="urlPrimaryCheck" className="text-[11px] text-luxury-muted select-none cursor-pointer">
                Set as primary thumbnail
              </label>
            </div>

            <Button
              type="submit"
              variant="dark"
              size="sm"
              disabled={!imageUrlInput.trim() || isAddingUrl}
              isLoading={isAddingUrl}
              className="w-full"
            >
              Associate Image URL
            </Button>
          </form>
        </div>

        <div className="flex justify-end pt-4 border-t border-luxury-border">
          <Button variant="ghost" onClick={onClose}>
            Done
          </Button>
        </div>
      </div>
    </Modal>
  );
};
