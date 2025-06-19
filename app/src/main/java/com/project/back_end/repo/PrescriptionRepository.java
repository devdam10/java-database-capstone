package com.project.back_end.repo;

import com.project.back_end.models.Prescription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Prescription entities.
 * Provides methods to perform CRUD operations and custom queries.
 * Extends MongoRepository for basic CRUD functionality.
 */

@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {
    List<Prescription> findByAppointmentId(Long appointmentId);
}
