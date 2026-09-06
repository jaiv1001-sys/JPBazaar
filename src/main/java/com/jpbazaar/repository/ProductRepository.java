package com.jpbazaar.repository;

import com.jpbazaar.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    Page<Product> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);

    @Query("""
            SELECT p FROM Product p
            WHERE (:categoryId IS NULL OR p.category.id = :categoryId)
              AND (:brand IS NULL OR LOWER(p.brand) = LOWER(:brand))
              AND (:minPrice IS NULL OR p.price >= :minPrice)
              AND (:maxPrice IS NULL OR p.price <= :maxPrice)
              AND (:active IS NULL OR p.active = :active)
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
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
