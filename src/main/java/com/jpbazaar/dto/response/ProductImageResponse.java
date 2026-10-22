package com.jpbazaar.dto.response;

public record ProductImageResponse(
        Long id,
        Long productId,
        String imageUrl,
        Boolean isPrimary
) {}
