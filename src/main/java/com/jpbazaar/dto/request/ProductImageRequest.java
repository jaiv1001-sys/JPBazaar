package com.jpbazaar.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductImageRequest(
        @NotBlank(message = "Image URL is required")
        @Size(max = 512, message = "Image URL must not exceed 512 characters")
        String imageUrl,

        Boolean isPrimary
) {
    public ProductImageRequest {
        if (isPrimary == null) {
            isPrimary = false;
        }
    }
}
