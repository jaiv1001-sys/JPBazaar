package com.jpbazaar.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

@Schema(description = "Customer shipping address response")
public record AddressResponse(
        @Schema(description = "Address ID", example = "1")
        Long id,

        @Schema(description = "User ID", example = "1")
        Long userId,

        @Schema(description = "Street address line", example = "42 Silicon Avenue, Apt 7B")
        String addressLine,

        @Schema(description = "City", example = "San Francisco")
        String city,

        @Schema(description = "State", example = "California")
        String state,

        @Schema(description = "Postal code", example = "94107")
        String postalCode,

        @Schema(description = "Country", example = "United States")
        String country,

        @Schema(description = "Primary default shipping address flag", example = "true")
        Boolean isDefault,

        @Schema(description = "Creation timestamp")
        OffsetDateTime createdAt
) {}
