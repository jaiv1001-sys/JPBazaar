package com.jpbazaar.service.impl;

import com.jpbazaar.dto.request.ProductCreateRequest;
import com.jpbazaar.dto.request.ProductUpdateRequest;
import com.jpbazaar.dto.response.PageResponse;
import com.jpbazaar.dto.response.ProductResponse;
import com.jpbazaar.entity.Category;
import com.jpbazaar.entity.Product;
import com.jpbazaar.entity.ProductImage;
import com.jpbazaar.exception.DuplicateResourceException;
import com.jpbazaar.exception.InvalidRequestException;
import com.jpbazaar.exception.ResourceNotFoundException;
import com.jpbazaar.repository.CategoryRepository;
import com.jpbazaar.repository.OrderItemRepository;
import com.jpbazaar.repository.ProductRepository;
import com.jpbazaar.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderItemRepository orderItemRepository;

    public ProductServiceImpl(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            OrderItemRepository orderItemRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.categoryId()));

        if (productRepository.existsBySku(request.sku())) {
            throw new DuplicateResourceException("Product with SKU already exists: " + request.sku());
        }

        Product product = Product.builder()
                .category(category)
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .stockQuantity(request.stockQuantity())
                .sku(request.sku())
                .brand(request.brand())
                .rating(BigDecimal.ZERO)
                .active(request.active() == null || request.active())
                .build();

        Product savedProduct = productRepository.save(product);
        return mapToProductResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = getProductOrThrow(id);
        return mapToProductResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getProducts(
            Long categoryId,
            String brand,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean active,
            String keyword,
            Pageable pageable
    ) {
        Page<Product> page = productRepository.searchAndFilterProducts(
                categoryId,
                brand,
                minPrice,
                maxPrice,
                active,
                keyword,
                pageable
        );
        return PageResponse.from(page, this::mapToProductResponse);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request) {
        Product product = getProductOrThrow(id);

        if (request.categoryId() != null && !request.categoryId().equals(product.getCategory().getId())) {
            Category newCategory = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.categoryId()));
            product.setCategory(newCategory);
        }

        if (request.name() != null) {
            product.setName(request.name());
        }
        if (request.description() != null) {
            product.setDescription(request.description());
        }
        if (request.price() != null) {
            product.setPrice(request.price());
        }
        if (request.stockQuantity() != null) {
            product.setStockQuantity(request.stockQuantity());
        }
        if (request.brand() != null) {
            product.setBrand(request.brand());
        }
        if (request.active() != null) {
            product.setActive(request.active());
        }

        Product updatedProduct = productRepository.save(product);
        return mapToProductResponse(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductOrThrow(id);

        if (orderItemRepository.existsByProductId(id)) {
            throw new InvalidRequestException("Cannot delete product that has existing orders. Consider setting active to false instead.");
        }

        productRepository.delete(product);
    }

    private Product getProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
    }

    private ProductResponse mapToProductResponse(Product product) {
        List<String> imageUrls = product.getImages() != null
                ? product.getImages().stream().map(ProductImage::getImageUrl).toList()
                : List.of();

        return new ProductResponse(
                product.getId(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getSku(),
                product.getBrand(),
                product.getRating(),
                product.getActive(),
                imageUrls,
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
