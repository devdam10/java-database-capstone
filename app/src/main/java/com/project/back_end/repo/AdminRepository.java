package com.project.back_end.repo;

import com.project.back_end.models.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Admin entities.
 * Provides methods to perform CRUD operations and custom queries.
 * Extends JpaRepository for basic CRUD functionality.
 */

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByUsername(String username);

    // Used in TokenService for token validation
    boolean existsByUsername(String username);
}
