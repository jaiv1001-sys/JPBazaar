import React, { useState, useEffect } from 'react';
import { Address, AddressRequest } from '../../types';
import { addressService } from '../../services/addressService';
import { useToast } from '../../context/ToastContext';
import { Modal } from '../common/Modal';
import { Input } from '../common/Input';
import { Button } from '../common/Button';
import { MapPin, Globe, Building } from 'lucide-react';

interface AddressModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSaved: (address: Address) => void;
  addressToEdit?: Address | null;
}

export const AddressModal: React.FC<AddressModalProps> = ({
  isOpen,
  onClose,
  onSaved,
  addressToEdit,
}) => {
  const { showToast } = useToast();

  const [addressLine, setAddressLine] = useState('');
  const [city, setCity] = useState('');
  const [state, setState] = useState('');
  const [postalCode, setPostalCode] = useState('');
  const [country, setCountry] = useState('United States');
  const [isDefault, setIsDefault] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (addressToEdit) {
      setAddressLine(addressToEdit.addressLine);
      setCity(addressToEdit.city);
      setState(addressToEdit.state);
      setPostalCode(addressToEdit.postalCode);
      setCountry(addressToEdit.country);
      setIsDefault(addressToEdit.isDefault);
    } else {
      setAddressLine('');
      setCity('');
      setState('');
      setPostalCode('');
      setCountry('United States');
      setIsDefault(false);
    }
  }, [addressToEdit, isOpen]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!addressLine || !city || !state || !postalCode || !country) {
      showToast('Please complete all delivery residence fields.', 'error');
      return;
    }

    const payload: AddressRequest = {
      addressLine,
      city,
      state,
      postalCode,
      country,
      isDefault,
    };

    setIsSubmitting(true);
    try {
      let saved: Address;
      if (addressToEdit) {
        saved = await addressService.updateAddress(addressToEdit.id, payload);
        showToast('Delivery residence updated successfully.', 'success');
      } else {
        saved = await addressService.addAddress(payload);
        showToast('New delivery residence registered.', 'success');
      }
      onSaved(saved);
      onClose();
    } catch (err: any) {
      showToast(err.message || 'Failed to register delivery residence.', 'error');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      maxWidth="lg"
      title={addressToEdit ? 'Amend Delivery Residence' : 'Register Delivery Residence'}
    >
      <form onSubmit={handleSubmit} className="space-y-4 py-2">
        <Input
          label="Street Address / Diplomatic Suite"
          placeholder="e.g. 742 Evergreen Terrace, Penthouse B"
          value={addressLine}
          onChange={(e) => setAddressLine(e.target.value)}
          leftIcon={<MapPin size={16} />}
          required
        />

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <Input
            label="City"
            placeholder="e.g. Springfield"
            value={city}
            onChange={(e) => setCity(e.target.value)}
            leftIcon={<Building size={16} />}
            required
          />

          <Input
            label="State / Province / Region"
            placeholder="e.g. Oregon"
            value={state}
            onChange={(e) => setState(e.target.value)}
            required
          />
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <Input
            label="Postal Code"
            placeholder="e.g. 97477"
            value={postalCode}
            onChange={(e) => setPostalCode(e.target.value)}
            required
          />

          <Input
            label="Country"
            placeholder="e.g. United States"
            value={country}
            onChange={(e) => setCountry(e.target.value)}
            leftIcon={<Globe size={16} />}
            required
          />
        </div>

        <div className="flex items-center gap-2 pt-2">
          <input
            type="checkbox"
            id="isDefaultCheckbox"
            checked={isDefault}
            onChange={(e) => setIsDefault(e.target.checked)}
            className="w-4 h-4 rounded-sm border-luxury-border bg-luxury-surface text-luxury-gold focus:ring-luxury-gold focus:ring-offset-luxury-black"
          />
          <label htmlFor="isDefaultCheckbox" className="text-xs text-luxury-muted select-none cursor-pointer">
            Designate as primary residence for priority dispatch
          </label>
        </div>

        <div className="flex justify-end gap-3 pt-6 border-t border-luxury-border">
          <Button type="button" variant="ghost" onClick={onClose} disabled={isSubmitting}>
            Cancel
          </Button>
          <Button type="submit" variant="gold" isLoading={isSubmitting}>
            {addressToEdit ? 'Save Amendments' : 'Register Address'}
          </Button>
        </div>
      </form>
    </Modal>
  );
};
