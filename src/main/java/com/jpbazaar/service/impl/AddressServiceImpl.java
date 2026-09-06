package com.jpbazaar.service.impl;

import com.jpbazaar.dto.request.AddressRequest;
import com.jpbazaar.dto.response.AddressResponse;
import com.jpbazaar.entity.Address;
import com.jpbazaar.entity.User;
import com.jpbazaar.exception.ResourceNotFoundException;
import com.jpbazaar.exception.UnauthorizedException;
import com.jpbazaar.repository.AddressRepository;
import com.jpbazaar.repository.UserRepository;
import com.jpbazaar.service.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressServiceImpl(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getUserAddresses(String email) {
        User user = getUserByEmail(email);
        return addressRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToAddressResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse getAddressById(String email, Long addressId) {
        Address address = getAddressAndValidateOwnership(email, addressId);
        return mapToAddressResponse(address);
    }

    @Override
    @Transactional
    public AddressResponse addAddress(String email, AddressRequest request) {
        User user = getUserByEmail(email);

        boolean isDefault = Boolean.TRUE.equals(request.isDefault());
        if (isDefault) {
            unsetPreviousDefaultAddress(user.getId());
        } else {
            // If user has no existing addresses, force first address to be default
            List<Address> existing = addressRepository.findByUserId(user.getId());
            if (existing.isEmpty()) {
                isDefault = true;
            }
        }

        Address address = Address.builder()
                .user(user)
                .addressLine(request.addressLine())
                .city(request.city())
                .state(request.state())
                .postalCode(request.postalCode())
                .country(request.country())
                .isDefault(isDefault)
                .build();

        Address savedAddress = addressRepository.save(address);
        return mapToAddressResponse(savedAddress);
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(String email, Long addressId, AddressRequest request) {
        Address address = getAddressAndValidateOwnership(email, addressId);

        boolean isDefault = Boolean.TRUE.equals(request.isDefault());
        if (isDefault && !Boolean.TRUE.equals(address.getIsDefault())) {
            unsetPreviousDefaultAddress(address.getUser().getId());
        }

        address.setAddressLine(request.addressLine());
        address.setCity(request.city());
        address.setState(request.state());
        address.setPostalCode(request.postalCode());
        address.setCountry(request.country());
        address.setIsDefault(isDefault);

        Address updatedAddress = addressRepository.save(address);
        return mapToAddressResponse(updatedAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(String email, Long addressId) {
        Address address = getAddressAndValidateOwnership(email, addressId);
        addressRepository.delete(address);
    }

    @Override
    @Transactional
    public AddressResponse setDefaultAddress(String email, Long addressId) {
        Address address = getAddressAndValidateOwnership(email, addressId);
        unsetPreviousDefaultAddress(address.getUser().getId());

        address.setIsDefault(true);
        Address updatedAddress = addressRepository.save(address);
        return mapToAddressResponse(updatedAddress);
    }

    private Address getAddressAndValidateOwnership(String email, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with ID: " + addressId));

        if (!address.getUser().getEmail().equals(email)) {
            throw new UnauthorizedException("Access denied: You do not own address ID " + addressId);
        }
        return address;
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private void unsetPreviousDefaultAddress(Long userId) {
        addressRepository.findByUserIdAndIsDefaultTrue(userId).ifPresent(defaultAddr -> {
            defaultAddr.setIsDefault(false);
            addressRepository.save(defaultAddr);
        });
    }

    private AddressResponse mapToAddressResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getUser().getId(),
                address.getAddressLine(),
                address.getCity(),
                address.getState(),
                address.getPostalCode(),
                address.getCountry(),
                address.getIsDefault(),
                address.getCreatedAt()
        );
    }
}
