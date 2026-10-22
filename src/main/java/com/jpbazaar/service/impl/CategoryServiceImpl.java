package com.jpbazaar.service.impl;

import com.jpbazaar.dto.request.CategoryRequest;
import com.jpbazaar.dto.response.CategoryResponse;
import com.jpbazaar.entity.Category;
import com.jpbazaar.exception.DuplicateResourceException;
import com.jpbazaar.exception.InvalidRequestException;
import com.jpbazaar.exception.ResourceNotFoundException;
import com.jpbazaar.repository.CategoryRepository;
import com.jpbazaar.repository.ProductRepository;
import com.jpbazaar.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Category already exists with name: " + request.name());
        }

        Category category = Category.builder()
                .name(request.name())
                .description(request.description())
                .build();

        Category savedCategory = categoryRepository.save(category);
        return mapToCategoryResponse(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = getCategoryOrThrow(id);
        return mapToCategoryResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::mapToCategoryResponse)
                .toList();
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = getCategoryOrThrow(id);

        if (categoryRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new DuplicateResourceException("Another category already exists with name: " + request.name());
        }

        category.setName(request.name());
        category.setDescription(request.description());

        Category updatedCategory = categoryRepository.save(category);
        return mapToCategoryResponse(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryOrThrow(id);

        if (productRepository.existsByCategoryId(id)) {
            throw new InvalidRequestException("Cannot delete category with associated products. Reassign or delete products first.");
        }

        categoryRepository.delete(category);
    }

    private Category getCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
    }

    private CategoryResponse mapToCategoryResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getCreatedAt()
        );
    }
}
