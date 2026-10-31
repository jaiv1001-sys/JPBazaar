package com.jpbazaar.repository;

import com.jpbazaar.entity.Role;
import com.jpbazaar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailIgnoreCase(String email);

    long countByRole(Role role);

    long countByCreatedAtAfter(OffsetDateTime date);
}

