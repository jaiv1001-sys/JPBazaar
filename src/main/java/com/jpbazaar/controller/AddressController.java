package com.jpbazaar.controller;

import com.jpbazaar.dto.ApiResponse;
import com.jpbazaar.dto.request.AddressRequest;
import com.jpbazaar.dto.response.AddressResponse;
import com.jpbazaar.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@Tag(name = "Address Book", description = "Customer Address Book CRUD and Default Address APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    @Operation(summary = "Get user addresses", description = "Retrieves all shipping/billing addresses for the authenticated user.")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getUserAddresses(@AuthenticationPrincipal UserDetails userDetails) {
        List<AddressResponse> response = addressService.getUserAddresses(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get address by ID", description = "Fetches a specific address by ID (with IDOR ownership protection).")
    public ResponseEntity<ApiResponse<AddressResponse>> getAddressById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id
    ) {
        AddressResponse response = addressService.getAddressById(userDetails.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @Operation(summary = "Add new address", description = "Adds a new address to the authenticated user's address book.")
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddressRequest request
    ) {
        AddressResponse response = addressService.addAddress(userDetails.getUsername(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Address added successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update address", description = "Updates an existing address by ID (with IDOR ownership protection).")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request
    ) {
        AddressResponse response = addressService.updateAddress(userDetails.getUsername(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Address updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete address", description = "Deletes an address by ID (with IDOR ownership protection).")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id
    ) {
        addressService.deleteAddress(userDetails.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.success("Address deleted successfully", null));
    }

    @PutMapping("/{id}/set-default")
    @Operation(summary = "Set default address", description = "Sets an address as the default address for checkout.")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefaultAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id
    ) {
        AddressResponse response = addressService.setDefaultAddress(userDetails.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.success("Default address set successfully", response));
    }
}
