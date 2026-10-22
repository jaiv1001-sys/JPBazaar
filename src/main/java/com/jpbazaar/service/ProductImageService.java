package com.jpbazaar.service;

import com.jpbazaar.dto.request.ProductImageRequest;
import com.jpbazaar.dto.response.ProductImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImageService {

    ProductImageResponse addImage(Long productId, ProductImageRequest request);

    ProductImageResponse uploadImage(Long productId, MultipartFile file, boolean isPrimary);

    List<ProductImageResponse> getProductImages(Long productId);

    ProductImageResponse setPrimaryImage(Long productId, Long imageId);

    void deleteProductImage(Long productId, Long imageId);
}
