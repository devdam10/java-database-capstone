package com.project.back_end.repo;

import com.project.back_end.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    // Find doctor by email
    Doctor findByEmail(String email);

    // Find doctors whose names match the pattern (case-sensitive)
    List<Doctor> findByNameLike(String name);

    // Find doctors by name (partial match, case-insensitive) and specialty (exact match, case-insensitive)
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(String name, String specialty);

    // Find doctors by specialty (case-insensitive)
    List<Doctor> findBySpecialtyIgnoreCase(String specialty);

    // Used in TokenService for token validation
    boolean existsByEmail(String email);
}
