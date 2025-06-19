package com.project.back_end.repo;

import com.project.back_end.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Patient entities.
 * Provides methods to perform CRUD operations and custom queries.
 * Extends JpaRepository for basic CRUD functionality.
 */

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Patient findByEmail(String email);
    Patient findByEmailOrPhone(String email, String phone);

    // Used in TokenService for token validation
    boolean existsByEmail(String email);
}
