package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.CentralService;
import com.project.back_end.services.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

/**
 * Controller for managing patient-related operations in the healthcare system.
 * Provides endpoints for patient registration, login, and appointment management.
 */
@RestController
@RequestMapping("${api.path}patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;
    private final CentralService centralService;

    /**
     * Retrieves patient details based on the provided token.
     * Validates the token before processing the request.
     *
     * @param token the authentication token
     * @return ResponseEntity with patient details or an error message
     */
    @GetMapping("/{token}")
    public ResponseEntity<?> getPatient(@PathVariable String token) {
        ResponseEntity<Map<String, String>> tempMap = centralService.validateToken(token, "patient");

        if (!Objects.requireNonNull(tempMap.getBody()).isEmpty() && tempMap.getStatusCode() != HttpStatus.OK) {
            return tempMap;
        }

        return patientService.getPatientDetails(token);
    }

    /**
     * Creates a new patient record.
     * Validates the patient data and checks for existing records before creating a new one.
     *
     * @param patient the patient data to be created
     * @param authorization the authorization header containing the token
     * @return ResponseEntity with a message indicating success or failure
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createPatient(@RequestBody @Valid Patient patient, @RequestHeader("Authorization") String authorization) {
        // String token = authorization.replace("Bearer ", "");

        // ResponseEntity<Map<String, String>> tempMap = centralService.validateToken(token, "patient");

        // if (!Objects.requireNonNull(tempMap.getBody()).isEmpty() && tempMap.getStatusCode() != HttpStatus.OK) {
        //     return tempMap;
        // }
        
        // Check if patient already exists
        // if (centralService.validatePatient(patient)) {
        //     return ResponseEntity
        //             .status(HttpStatus.CONFLICT)
        //             .body(Map.of("message", "Patient with email ID or phone number already exists"));
        // }

        int status = patientService.createPatient(patient);

        if (status == 1) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of("message", "Signup successful"));
        }
        else if (status == 0) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Signup failed. Invalid patient data."));
        }
        else {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal server error"));
        }
    }

    /**
     * Validates the login credentials of a patient.
     *
     * @param login the login credentials
     * @return ResponseEntity with a message indicating success or failure
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Login login) {
        return centralService.validatePatientLogin(login);
    }

    /**
     * Updates an existing patient's details.
     * Validates the token before processing the request.
     *
     * @param patient the patient with updated details
     * @param token   the authentication token
     * @return ResponseEntity with a message indicating success or failure
     */
    @GetMapping("/{id}/{token}")
    public ResponseEntity<?> getPatientAppointment(@PathVariable Long id, @PathVariable String token) {
        ResponseEntity<Map<String, String>> tempMap = centralService.validateToken(token, "patient");

        if (!Objects.requireNonNull(tempMap.getBody()).isEmpty() && tempMap.getStatusCode() != HttpStatus.OK) {
            return tempMap;
        }

        return patientService.getPatientAppointment(id, token);
    }

    /**
     * Retrieves all appointments for a specific patient.
     * Validates the token before processing the request.
     *
     * @param id    the ID of the patient
     * @param token the authentication token
     * @return ResponseEntity with a list of appointments or an error message
     */
    @GetMapping("/appointments")
    public ResponseEntity<?> getPatientAppointments(@RequestParam Long id, @RequestParam String token) {
        ResponseEntity<Map<String, String>> tempMap = centralService.validateToken(token, "patient");

        if (!Objects.requireNonNull(tempMap.getBody()).isEmpty() && tempMap.getStatusCode() != HttpStatus.OK) {
            return tempMap;
        }

        return patientService.getPatientAppointment(id, token);
    }

    /**
     * Filters patient appointments based on name and condition.
     * Validates the token before processing the request.
     *
     * @param name      the name of the patient
     * @param condition the condition to filter by
     * @param token     the authentication token
     * @return ResponseEntity with filtered appointments or an error message
     */
    @GetMapping("/filter/appointments")
    public ResponseEntity<?> filterPatientAppointment(@RequestParam String name, @RequestParam String condition, @RequestParam String token) {
        ResponseEntity<Map<String, String>> tempMap = centralService.validateToken(token, "patient");

        if (!Objects.requireNonNull(tempMap.getBody()).isEmpty() && tempMap.getStatusCode() != HttpStatus.OK) {
            return tempMap;
        }

        return centralService.filterPatient(name, condition, token);
    }

    /**
     * Filters patient appointments by doctor.
     * Validates the token before processing the request.
     *
     * @param patientId the ID of the patient
     * @param doctorId  the ID of the doctor
     * @param token     the authentication token
     * @return ResponseEntity with filtered appointments or an error message
     */
    @GetMapping("/filter")
    public ResponseEntity<?> filterPatientAppointmentByDoctor(@RequestParam Long patientId, @RequestParam Long doctorId, @RequestParam String token) {
        ResponseEntity<Map<String, String>> tempMap = centralService.validateToken(token, "doctor");

        if (!Objects.requireNonNull(tempMap.getBody()).isEmpty() && tempMap.getStatusCode() != HttpStatus.OK) {
            return tempMap;
        }

        return centralService.filterPatient(patientId, doctorId, token);
    }
}
