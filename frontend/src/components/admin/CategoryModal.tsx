import React, { useState, useEffect } from 'react';
import { Category, CategoryRequest } from '../../types';
import { categoryService } from '../../services/categoryService';
import { useToast } from '../../context/ToastContext';
import { Modal } from '../common/Modal';
import { Input } from '../common/Input';
import { Button } from '../common/Button';
import { Layers } from 'lucide-react';

interface CategoryModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSaved: (category: Category) => void;
  categoryToEdit?: Category | null;
}

export const CategoryModal: React.FC<CategoryModalProps> = ({
  isOpen,
  onClose,
  onSaved,
  categoryToEdit,
}) => {
  const { showToast } = useToast();

  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (categoryToEdit) {
      setName(categoryToEdit.name);
      setDescription(categoryToEdit.description || '');
    } else {
      setName('');
      setDescription('');
    }
  }, [categoryToEdit, isOpen]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) {
      showToast('Category name is required.', 'error');
      return;
    }

    const payload: CategoryRequest = {
      name: name.trim(),
      description: description.trim() || undefined,
    };

    setIsSubmitting(true);
    try {
      let saved: Category;
      if (categoryToEdit) {
        saved = await categoryService.updateCategory(categoryToEdit.id, payload);
        showToast(`Category "${saved.name}" updated.`, 'success');
      } else {
        saved = await categoryService.createCategory(payload);
        showToast(`Category "${saved.name}" created.`, 'success');
      }
      onSaved(saved);
      onClose();
    } catch (err: any) {
      showToast(err.message || 'Failed to save category.', 'error');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      maxWidth="md"
      title={categoryToEdit ? 'Amend Atelier Department' : 'Establish New Department'}
    >
      <form onSubmit={handleSubmit} className="space-y-4 py-2">
        <Input
          label="Category Name"
          placeholder="e.g. Haute Horlogerie, Acoustic Engineering..."
          value={name}
          onChange={(e) => setName(e.target.value)}
          leftIcon={<Layers size={16} />}
          required
        />

        <div className="space-y-1">
          <label className="text-xs text-luxury-muted block">Department Narrative</label>
          <textarea
            rows={3}
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="Describe the aesthetic and standards of this curated category..."
            className="w-full bg-luxury-elevated border border-luxury-border rounded-sm p-3 text-xs text-luxury-white placeholder:text-luxury-subtle focus:outline-none focus:border-luxury-gold"
          />
        </div>

        <div className="flex justify-end gap-3 pt-6 border-t border-luxury-border">
          <Button type="button" variant="ghost" onClick={onClose} disabled={isSubmitting}>
            Cancel
          </Button>
          <Button type="submit" variant="gold" isLoading={isSubmitting}>
            {categoryToEdit ? 'Save Department' : 'Create Department'}
          </Button>
        </div>
      </form>
    </Modal>
  );
};
