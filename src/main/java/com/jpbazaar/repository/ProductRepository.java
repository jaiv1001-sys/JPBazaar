package com.jpbazaar.repository;

import com.jpbazaar.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithPessimisticLock(@Param("id") Long id);

    @Override
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.id = :id")
    Optional<Product> findById(@Param("id") Long id);

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, Long id);

    boolean existsByCategoryId(Long categoryId);

    @Query(value = "SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.category.id = :categoryId AND p.active = true",
           countQuery = "SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId AND p.active = true")
    Page<Product> findByCategoryIdAndActiveTrue(@Param("categoryId") Long categoryId, Pageable pageable);

    long countByActiveTrue();

    long countByStockQuantityLessThanEqualAndActiveTrue(Integer threshold);

    @Query(value = "SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.stockQuantity <= :threshold AND p.active = true ORDER BY p.stockQuantity ASC",
           countQuery = "SELECT COUNT(p) FROM Product p WHERE p.stockQuantity <= :threshold AND p.active = true")
    Page<Product> findLowStockProducts(@Param("threshold") Integer threshold, Pageable pageable);

    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.brand IS NOT NULL AND TRIM(p.brand) != '' ORDER BY p.brand ASC")
    List<String> findDistinctBrands();

    @Query(value = """
            SELECT p FROM Product p
            LEFT JOIN FETCH p.category
            WHERE (:categoryId IS NULL OR p.category.id = :categoryId)
              AND (:brand IS NULL OR LOWER(p.brand) = LOWER(:brand))
              AND (:minPrice IS NULL OR p.price >= :minPrice)
              AND (:maxPrice IS NULL OR p.price <= :maxPrice)
              AND (:active IS NULL OR p.active = :active)
              AND (:inStock IS NULL OR (:inStock = true AND p.stockQuantity > 0) OR (:inStock = false AND p.stockQuantity = 0))
              AND (:keyword IS NULL OR (
                     LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                     LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
              ))
            """,
            countQuery = """
            SELECT COUNT(p) FROM Product p
            WHERE (:categoryId IS NULL OR p.category.id = :categoryId)
              AND (:brand IS NULL OR LOWER(p.brand) = LOWER(:brand))
              AND (:minPrice IS NULL OR p.price >= :minPrice)
              AND (:maxPrice IS NULL OR p.price <= :maxPrice)
              AND (:active IS NULL OR p.active = :active)
              AND (:inStock IS NULL OR (:inStock = true AND p.stockQuantity > 0) OR (:inStock = false AND p.stockQuantity = 0))
              AND (:keyword IS NULL OR (
                     LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                     LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
              ))
            """)
    Page<Product> searchAndFilterProducts(
            @Param("categoryId") Long categoryId,
            @Param("brand") String brand,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("active") Boolean active,
            @Param("inStock") Boolean inStock,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
