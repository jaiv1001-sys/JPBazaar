package com.jpbazaar.dto.request;

import com.jpbazaar.entity.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Checkout order creation payload")
public record CreateOrderRequest(
        @Schema(description = "ID of the customer shipping address", example = "1")
        @NotNull(message = "Shipping address ID is required")
        Long addressId,

        @Schema(description = "Selected payment method (COD, CREDIT_CARD, DEBIT_CARD, UPI, NET_BANKING)", example = "CREDIT_CARD")
        PaymentMethod paymentMethod,

        @Schema(description = "Optional delivery instructions or notes", example = "Leave at front door")
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
