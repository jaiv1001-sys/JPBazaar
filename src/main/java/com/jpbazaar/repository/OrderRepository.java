package com.jpbazaar.repository;

import com.jpbazaar.entity.Order;
import com.jpbazaar.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(value = "SELECT o FROM Order o LEFT JOIN FETCH o.address LEFT JOIN FETCH o.payment WHERE o.user.id = :userId",
           countQuery = "SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId")
    Page<Order> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "SELECT o FROM Order o LEFT JOIN FETCH o.address LEFT JOIN FETCH o.payment WHERE o.user.id = :userId AND o.status = :status",
           countQuery = "SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId AND o.status = :status")
    Page<Order> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") OrderStatus status, Pageable pageable);

    Optional<Order> findByIdAndUserId(Long id, Long userId);

    @Query(value = "SELECT o FROM Order o LEFT JOIN FETCH o.user LEFT JOIN FETCH o.address LEFT JOIN FETCH o.payment WHERE o.status = :status",
           countQuery = "SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Page<Order> findByStatus(@Param("status") OrderStatus status, Pageable pageable);

    @Query(value = "SELECT o FROM Order o LEFT JOIN FETCH o.user LEFT JOIN FETCH o.address LEFT JOIN FETCH o.payment",
           countQuery = "SELECT COUNT(o) FROM Order o")
    Page<Order> findAllWithDetails(Pageable pageable);

    long countByStatus(OrderStatus status);

    long countByCreatedAtAfter(OffsetDateTime date);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status IN (:statuses)")
    long countByStatusIn(@Param("statuses") Collection<OrderStatus> statuses);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status IN (com.jpbazaar.entity.OrderStatus.PLACED, com.jpbazaar.entity.OrderStatus.CONFIRMED, com.jpbazaar.entity.OrderStatus.PROCESSING)")
    long countPendingOrders();

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status != com.jpbazaar.entity.OrderStatus.CANCELLED")
    BigDecimal calculateTotalRevenue();

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status != com.jpbazaar.entity.OrderStatus.CANCELLED AND o.createdAt >= :startDate")
    BigDecimal calculateRevenueSince(@Param("startDate") OffsetDateTime startDate);

    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countOrdersByStatusGroup();

    @Query("SELECT o.id, u.id, u.firstName, u.lastName, u.email, o.totalAmount, o.status, SIZE(o.items), o.createdAt " +
           "FROM Order o JOIN o.user u " +
           "ORDER BY o.createdAt DESC")
    List<Object[]> findRecentOrdersSummary(Pageable pageable);

    @Query("SELECT cast(o.createdAt as LocalDate) as orderDate, COUNT(o), COALESCE(SUM(o.totalAmount), 0) " +
           "FROM Order o " +
           "WHERE o.status != com.jpbazaar.entity.OrderStatus.CANCELLED AND o.createdAt >= :startDate " +
           "GROUP BY cast(o.createdAt as LocalDate) " +
           "ORDER BY cast(o.createdAt as LocalDate) ASC")
    List<Object[]> getDailySalesTrend(@Param("startDate") OffsetDateTime startDate);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.product WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);

    @Query("SELECT o FROM Order o " +
           "LEFT JOIN FETCH o.items i " +
           "LEFT JOIN FETCH i.product p " +
           "LEFT JOIN FETCH o.address a " +
           "LEFT JOIN FETCH o.user u " +
           "LEFT JOIN FETCH o.payment pay " +
           "WHERE o.id = :id")
    Optional<Order> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT o FROM Order o " +
           "LEFT JOIN FETCH o.items i " +
           "LEFT JOIN FETCH i.product p " +
           "LEFT JOIN FETCH o.address a " +
           "LEFT JOIN FETCH o.user u " +
           "LEFT JOIN FETCH o.payment pay " +
           "WHERE o.id = :id AND u.id = :userId")
    Optional<Order> findByIdAndUserIdWithDetails(@Param("id") Long id, @Param("userId") Long userId);
}
