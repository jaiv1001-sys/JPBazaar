package com.jpbazaar.service;

import com.jpbazaar.dto.request.ProductCreateRequest;
import com.jpbazaar.dto.request.ProductUpdateRequest;
import com.jpbazaar.dto.response.PageResponse;
import com.jpbazaar.dto.response.ProductResponse;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductCreateRequest request);

    ProductResponse getProductById(Long id);

    default PageResponse<ProductResponse> getProducts(
            Long categoryId,
            String brand,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean active,
            String keyword,
            Pageable pageable
    ) {
        return getProducts(categoryId, brand, minPrice, maxPrice, active, null, keyword, pageable);
    }

    PageResponse<ProductResponse> getProducts(
            Long categoryId,
            String brand,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean active,
            Boolean inStock,
            String keyword,
            Pageable pageable
    );

    ProductResponse updateProduct(Long id, ProductUpdateRequest request);

    ProductResponse updateProductStatus(Long id, boolean active);

    void deleteProduct(Long id);

    List<String> getBrands();
}
