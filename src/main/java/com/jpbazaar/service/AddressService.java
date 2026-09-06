package com.jpbazaar.service;

import com.jpbazaar.dto.request.AddressRequest;
import com.jpbazaar.dto.response.AddressResponse;

import java.util.List;

public interface AddressService {

    List<AddressResponse> getUserAddresses(String email);

    AddressResponse getAddressById(String email, Long addressId);

    AddressResponse addAddress(String email, AddressRequest request);

    AddressResponse updateAddress(String email, Long addressId, AddressRequest request);

    void deleteAddress(String email, Long addressId);

    AddressResponse setDefaultAddress(String email, Long addressId);
}
