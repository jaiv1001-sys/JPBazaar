package com.jpbazaar.repository;

import com.jpbazaar.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    @Query("SELECT c.id, c.name, COUNT(DISTINCT p.id), " +
           "COALESCE(SUM(CASE WHEN o.status != com.jpbazaar.entity.OrderStatus.CANCELLED THEN oi.quantity ELSE 0 END), 0), " +
           "COALESCE(SUM(CASE WHEN o.status != com.jpbazaar.entity.OrderStatus.CANCELLED THEN oi.subtotal ELSE 0 END), 0) " +
           "FROM Category c " +
           "LEFT JOIN c.products p " +
           "LEFT JOIN OrderItem oi ON oi.product = p " +
           "LEFT JOIN oi.order o " +
           "GROUP BY c.id, c.name " +
           "ORDER BY COALESCE(SUM(CASE WHEN o.status != com.jpbazaar.entity.OrderStatus.CANCELLED THEN oi.subtotal ELSE 0 END), 0) DESC")
    List<Object[]> getCategorySalesStatistics();
}
