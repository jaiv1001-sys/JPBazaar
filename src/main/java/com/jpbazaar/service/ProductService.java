package com.jpbazaar.service;

import com.jpbazaar.dto.request.ProductCreateRequest;
import com.jpbazaar.dto.request.ProductUpdateRequest;
import com.jpbazaar.dto.response.PageResponse;
import com.jpbazaar.dto.response.ProductResponse;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ProductService {

    ProductResponse createProduct(ProductCreateRequest request);

    ProductResponse getProductById(Long id);

    PageResponse<ProductResponse> getProducts(
            Long categoryId,
            String brand,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean active,
            String keyword,
            Pageable pageable
    );

    ProductResponse updateProduct(Long id, ProductUpdateRequest request);

    void deleteProduct(Long id);
}
