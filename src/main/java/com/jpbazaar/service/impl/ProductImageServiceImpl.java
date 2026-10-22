package com.jpbazaar.service.impl;

import com.jpbazaar.dto.request.ProductImageRequest;
import com.jpbazaar.dto.response.ProductImageResponse;
import com.jpbazaar.entity.Product;
import com.jpbazaar.entity.ProductImage;
import com.jpbazaar.exception.ResourceNotFoundException;
import com.jpbazaar.repository.ProductImageRepository;
import com.jpbazaar.repository.ProductRepository;
import com.jpbazaar.service.ProductImageService;
import com.jpbazaar.service.storage.ImageStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ImageStorageService imageStorageService;

    public ProductImageServiceImpl(
            ProductRepository productRepository,
            ProductImageRepository productImageRepository,
            ImageStorageService imageStorageService
    ) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.imageStorageService = imageStorageService;
    }

    @Override
    public ProductImageResponse addImage(Long productId, ProductImageRequest request) {
        Product product = getProductOrThrow(productId);

        boolean isFirstImage = productImageRepository.findByProductId(productId).isEmpty();
        boolean makePrimary = Boolean.TRUE.equals(request.isPrimary()) || isFirstImage;

        if (makePrimary) {
            unsetCurrentPrimary(productId);
        }

        ProductImage image = ProductImage.builder()
                .product(product)
                .imageUrl(request.imageUrl().trim())
                .isPrimary(makePrimary)
                .build();

        ProductImage savedImage = productImageRepository.save(image);
        return mapToResponse(savedImage);
    }

    @Override
    public ProductImageResponse uploadImage(Long productId, MultipartFile file, boolean isPrimary) {
        Product product = getProductOrThrow(productId);

        String storedUrl = imageStorageService.upload(file, "products/" + productId);

        boolean isFirstImage = productImageRepository.findByProductId(productId).isEmpty();
        boolean makePrimary = isPrimary || isFirstImage;

        if (makePrimary) {
            unsetCurrentPrimary(productId);
        }

        ProductImage image = ProductImage.builder()
                .product(product)
                .imageUrl(storedUrl)
                .isPrimary(makePrimary)
                .build();

        ProductImage savedImage = productImageRepository.save(image);
        return mapToResponse(savedImage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductImageResponse> getProductImages(Long productId) {
        // Validate product exists
        getProductOrThrow(productId);

        return productImageRepository.findByProductIdOrderByIsPrimaryDescIdAsc(productId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ProductImageResponse setPrimaryImage(Long productId, Long imageId) {
        getProductOrThrow(productId);

        ProductImage targetImage = productImageRepository.findByIdAndProductId(imageId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product image not found with ID: " + imageId + " for product ID: " + productId));

        if (!targetImage.getIsPrimary()) {
            unsetCurrentPrimary(productId);
            targetImage.setIsPrimary(true);
            targetImage = productImageRepository.save(targetImage);
        }

        return mapToResponse(targetImage);
    }

    @Override
    public void deleteProductImage(Long productId, Long imageId) {
        getProductOrThrow(productId);

        ProductImage image = productImageRepository.findByIdAndProductId(imageId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product image not found with ID: " + imageId + " for product ID: " + productId));

        boolean wasPrimary = Boolean.TRUE.equals(image.getIsPrimary());

        // Remove file from storage
        imageStorageService.delete(image.getImageUrl());

        // Remove entity from DB
        productImageRepository.delete(image);

        // If the deleted image was primary, elevate the first remaining image to primary
        if (wasPrimary) {
            List<ProductImage> remainingImages = productImageRepository.findByProductId(productId);
            if (!remainingImages.isEmpty()) {
                ProductImage newPrimary = remainingImages.get(0);
                newPrimary.setIsPrimary(true);
                productImageRepository.save(newPrimary);
            }
        }
    }

    private Product getProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
    }

    private void unsetCurrentPrimary(Long productId) {
        productImageRepository.findByProductIdAndIsPrimaryTrue(productId).ifPresent(primary -> {
            primary.setIsPrimary(false);
            productImageRepository.save(primary);
        });
    }

    private ProductImageResponse mapToResponse(ProductImage image) {
        return new ProductImageResponse(
                image.getId(),
                image.getProduct().getId(),
                image.getImageUrl(),
                image.getIsPrimary()
        );
    }
}
