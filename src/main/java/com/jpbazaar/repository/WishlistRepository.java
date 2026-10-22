package com.jpbazaar.repository;

import com.jpbazaar.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    Optional<Wishlist> findByUserId(Long userId);

    Optional<Wishlist> findByUserEmail(String email);

    @Query("SELECT w FROM Wishlist w LEFT JOIN FETCH w.items i LEFT JOIN FETCH i.product WHERE w.user.id = :userId")
    Optional<Wishlist> findByUserIdWithItems(@Param("userId") Long userId);

    @Query("SELECT w FROM Wishlist w LEFT JOIN FETCH w.items i LEFT JOIN FETCH i.product p LEFT JOIN FETCH p.category WHERE w.user.email = :email")
    Optional<Wishlist> findByUserEmailWithItems(@Param("email") String email);
}
