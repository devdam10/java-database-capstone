package com.project.back_end.services;

import com.project.back_end.models.Prescription;
import com.project.back_end.repo.PrescriptionRepository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for managing prescriptions in the healthcare system.
 * Provides methods to save and retrieve prescriptions.
 */
@Service
@RequiredArgsConstructor
public class PrescriptionService {
    private static final Logger logger = LoggerFactory.getLogger(PrescriptionService.class);

    private final PrescriptionRepository prescriptionRepository;

    /**
     * Saves a prescription to the database.
     * Checks if a prescription already exists for the given appointment ID.
     *
     * @param prescription the prescription to save
     * @return ResponseEntity with a message indicating success or failure
     */
    public ResponseEntity<Map<String, String>> savePrescription(Prescription prescription) {
        Map<String, String> response = new HashMap<>();
        try {
            List<Prescription> list = prescriptionRepository.findByAppointmentId(prescription.getAppointmentId());

            if (!list.isEmpty()) {
                response.put("message", "Prescription already exists for this appointment.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            prescriptionRepository.save(prescription);
            response.put("message", "Prescription saved successfully.");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        catch (Exception e) {
            logger.error("Error saving prescription: {}", e.getMessage());
            response.put("message", "Failed to save prescription.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a prescription by appointment ID.
     *
     * @param appointmentId the ID of the appointment
     * @return ResponseEntity containing the prescription details or an error message
     */
    public ResponseEntity<Map<String, Object>> getPrescription(Long appointmentId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Prescription> prescriptions = prescriptionRepository.findByAppointmentId(appointmentId);

            response.put("prescriptions", prescriptions);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            logger.error("Error retrieving prescription: {}", e.getMessage());
            response.put("message", "Failed to retrieve prescription.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
