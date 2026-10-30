package com.jpbazaar.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Customer shipping address payload")
public record AddressRequest(
        @Schema(description = "Street address line and building number", example = "42 Silicon Avenue, Apt 7B")
        @NotBlank(message = "Address line is required")
        @Size(max = 255, message = "Address line must not exceed 255 characters")
        String addressLine,

        @Schema(description = "City name", example = "San Francisco")
        @NotBlank(message = "City is required")
        @Size(max = 100, message = "City must not exceed 100 characters")
        String city,

        @Schema(description = "State or province", example = "California")
        @NotBlank(message = "State is required")
        @Size(max = 100, message = "State must not exceed 100 characters")
        String state,

        @Schema(description = "Postal/ZIP code", example = "94107")
        @NotBlank(message = "Postal code is required")
        @Size(max = 20, message = "Postal code must not exceed 20 characters")
        String postalCode,

        @Schema(description = "Country name", example = "United States")
        @NotBlank(message = "Country is required")
        @Size(max = 100, message = "Country must not exceed 100 characters")
        String country,

        @Schema(description = "Set as primary default shipping address", example = "true")
        Boolean isDefault
) {}
