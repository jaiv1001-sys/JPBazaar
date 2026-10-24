package com.jpbazaar.cache;

import com.jpbazaar.config.RedisConfig;
import com.jpbazaar.dto.request.CategoryRequest;
import com.jpbazaar.dto.request.ProductCreateRequest;
import com.jpbazaar.dto.request.ProductUpdateRequest;
import com.jpbazaar.dto.response.CategoryResponse;
import com.jpbazaar.dto.response.ProductResponse;
import com.jpbazaar.repository.CategoryRepository;
import com.jpbazaar.repository.ProductRepository;
import com.jpbazaar.service.CategoryService;
import com.jpbazaar.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class ProductCacheIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CacheManager cacheManager;

    @SpyBean
    private ProductRepository productRepository;

    @SpyBean
    private CategoryRepository categoryRepository;

    private Long testCategoryId;
    private Long testProductId;

    @BeforeEach
    void setUp() {
        // Clear caches prior to test
        Cache productsCache = cacheManager.getCache(RedisConfig.CACHE_PRODUCTS);
        if (productsCache != null) {
            productsCache.clear();
        }
        Cache categoriesCache = cacheManager.getCache(RedisConfig.CACHE_CATEGORIES);
        if (categoriesCache != null) {
            categoriesCache.clear();
        }
        Cache categoryCache = cacheManager.getCache(RedisConfig.CACHE_CATEGORY);
        if (categoryCache != null) {
            categoryCache.clear();
        }

        // Setup test category and product
        String uniqueSuffix = String.valueOf(System.nanoTime());
        CategoryResponse category = categoryService.createCategory(
                new CategoryRequest("Cache Category " + uniqueSuffix, "Test category for cache")
        );
        testCategoryId = category.id();

        ProductResponse product = productService.createProduct(new ProductCreateRequest(
                testCategoryId,
                "Cache Test Phone " + uniqueSuffix,
                "Phone for cache testing",
                BigDecimal.valueOf(799.99),
                50,
                "SKU-CACHE-" + uniqueSuffix,
                "CacheBrand",
                true
        ));
        testProductId = product.id();
        reset(productRepository);
        reset(categoryRepository);
    }

    @Test
    @DisplayName("Should populate cache on first read (Cache Miss) and serve from cache on second read (Cache Hit)")
    void shouldCacheProductOnRead() {
        // First read: Cache MISS -> invokes productRepository.findById
        ProductResponse firstRead = productService.getProductById(testProductId);
        assertThat(firstRead).isNotNull();
        assertThat(firstRead.id()).isEqualTo(testProductId);
        verify(productRepository, times(1)).findById(testProductId);

        // Verify item is placed in cache
        Cache productsCache = cacheManager.getCache(RedisConfig.CACHE_PRODUCTS);
        assertThat(productsCache).isNotNull();
        ProductResponse cachedValue = productsCache.get(testProductId, ProductResponse.class);
        assertThat(cachedValue).isNotNull();
        assertThat(cachedValue.id()).isEqualTo(testProductId);

        // Second read: Cache HIT -> returns cached instance without hitting repository
        ProductResponse secondRead = productService.getProductById(testProductId);
        assertThat(secondRead).isNotNull();
        assertThat(secondRead.name()).isEqualTo(firstRead.name());
        verify(productRepository, times(1)).findById(testProductId); // Still only 1 DB invocation!
    }

    @Test
    @DisplayName("Should invalidate product cache when product is updated")
    void shouldEvictProductCacheOnUpdate() {
        // Prime the cache
        ProductResponse initial = productService.getProductById(testProductId);
        assertThat(initial).isNotNull();
        verify(productRepository, times(1)).findById(testProductId);

        // Verify product is cached
        Cache productsCache = cacheManager.getCache(RedisConfig.CACHE_PRODUCTS);
        assertThat(productsCache.get(testProductId)).isNotNull();

        // Update product
        ProductUpdateRequest updateRequest = new ProductUpdateRequest(
                testCategoryId,
                "Updated Cache Phone",
                "New Description",
                BigDecimal.valueOf(899.99),
                45,
                "CacheBrand",
                true
        );
        productService.updateProduct(testProductId, updateRequest);

        // Verify cache was evicted
        assertThat(productsCache.get(testProductId)).isNull();

        // Reset spy invocations to isolate post-update read behavior
        clearInvocations(productRepository);

        // Read again: must trigger repository lookup (Cache MISS after eviction)
        ProductResponse freshRead = productService.getProductById(testProductId);
        assertThat(freshRead.name()).isEqualTo("Updated Cache Phone");
        verify(productRepository, times(1)).findById(testProductId);
    }

    @Test
    @DisplayName("Should invalidate product cache when product is deleted")
    void shouldEvictProductCacheOnDelete() {
        // Prime the cache
        productService.getProductById(testProductId);
        Cache productsCache = cacheManager.getCache(RedisConfig.CACHE_PRODUCTS);
        assertThat(productsCache.get(testProductId)).isNotNull();

        // Delete product
        productService.deleteProduct(testProductId);

        // Verify cache was evicted
        assertThat(productsCache.get(testProductId)).isNull();
    }

    @Test
    @DisplayName("Should cache categories list and invalidate when a new category is created")
    void shouldCacheAndEvictCategories() {
        // First read: Cache MISS
        List<CategoryResponse> list1 = categoryService.getAllCategories();
        assertThat(list1).isNotEmpty();
        verify(categoryRepository, times(1)).findAll();

        // Second read: Cache HIT
        List<CategoryResponse> list2 = categoryService.getAllCategories();
        assertThat(list2).hasSize(list1.size());
        verify(categoryRepository, times(1)).findAll(); // Still 1 invocation

        // Create new category -> evicts 'categories' cache
        categoryService.createCategory(new CategoryRequest("New Category Evict", "Desc"));

        // Third read: Cache MISS after eviction -> triggers repository findAll() again
        List<CategoryResponse> list3 = categoryService.getAllCategories();
        assertThat(list3.size()).isGreaterThan(list1.size());
        verify(categoryRepository, times(2)).findAll();
    }

    @Test
    @DisplayName("Should cache single category by ID and evict on update")
    void shouldCacheAndEvictSingleCategory() {
        // Read single category: Cache MISS
        CategoryResponse cat1 = categoryService.getCategoryById(testCategoryId);
        assertThat(cat1).isNotNull();
        verify(categoryRepository, times(1)).findById(testCategoryId);

        // Second read: Cache HIT
        CategoryResponse cat2 = categoryService.getCategoryById(testCategoryId);
        assertThat(cat2.name()).isEqualTo(cat1.name());
        verify(categoryRepository, times(1)).findById(testCategoryId);

        // Update category -> evicts single category and all categories cache
        categoryService.updateCategory(testCategoryId, new CategoryRequest("Updated Cat Name", "Updated Desc"));

        Cache singleCatCache = cacheManager.getCache(RedisConfig.CACHE_CATEGORY);
        assertThat(singleCatCache.get(testCategoryId)).isNull();

        // Reset spy invocations to isolate post-update read behavior
        clearInvocations(categoryRepository);

        // Third read: Cache MISS after eviction
        CategoryResponse cat3 = categoryService.getCategoryById(testCategoryId);
        assertThat(cat3.name()).isEqualTo("Updated Cat Name");
        verify(categoryRepository, times(1)).findById(testCategoryId);
    }
}
