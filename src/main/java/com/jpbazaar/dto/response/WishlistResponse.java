package com.jpbazaar.dto.response;

import java.util.List;

public record WishlistResponse(
        Long id,
        Long userId,
        List<WishlistItemResponse> items,
        Integer totalItems
) {}
