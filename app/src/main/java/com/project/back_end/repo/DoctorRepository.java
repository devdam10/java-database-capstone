package com.project.back_end.repo;

import com.project.back_end.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Doctor entities.
 * Provides methods to perform CRUD operations and custom queries.
 * Extends JpaRepository for basic CRUD functionality.
 */

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Doctor findByEmail(String email);

    @Query("SELECT d FROM Doctor d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Doctor> findByNameLikeOrderByName(@Param("name") String name);

    @Query("SELECT d FROM Doctor d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')) AND LOWER(d.specialty) = LOWER(:specialty)")
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCaseOrderByName(@Param("name") String name, @Param("specialty") String specialty);

    List<Doctor> findBySpecialtyIgnoreCaseOrderByName(String specialty);

    boolean existsByEmail(String email);
}
