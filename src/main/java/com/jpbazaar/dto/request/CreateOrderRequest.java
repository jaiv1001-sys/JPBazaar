package com.jpbazaar.dto.request;

import com.jpbazaar.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateOrderRequest(
        @NotNull(message = "Shipping address ID is required")
        Long addressId,

        PaymentMethod paymentMethod,

        @Size(max = 500, message = "Notes cannot exceed 500 characters")
        String notes
) {
    public CreateOrderRequest(Long addressId) {
        this(addressId, PaymentMethod.COD, null);
    }

    public CreateOrderRequest(Long addressId, String notes) {
        this(addressId, PaymentMethod.COD, notes);
    }

    public CreateOrderRequest(Long addressId, PaymentMethod paymentMethod) {
        this(addressId, paymentMethod, null);
    }
}
