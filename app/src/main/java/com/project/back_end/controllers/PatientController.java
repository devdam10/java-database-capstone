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

@RestController
@RequestMapping("${api.path}patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;
    private final CentralService centralService;

    @GetMapping("/{token}")
    public ResponseEntity<?> getPatient(@PathVariable String token) {
        ResponseEntity<Map<String, String>> tempMap = centralService.validateToken(token, "patient");

        if (!Objects.requireNonNull(tempMap.getBody()).isEmpty() && tempMap.getStatusCode() != HttpStatus.OK) {
            return tempMap;
        }

        return patientService.getPatientDetails(token);
    }

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

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Login login) {
        return centralService.validatePatientLogin(login);
    }

    @GetMapping("/{id}/{token}")
    public ResponseEntity<?> getPatientAppointment(@PathVariable Long id, @PathVariable String token) {
        ResponseEntity<Map<String, String>> tempMap = centralService.validateToken(token, "patient");

        if (!Objects.requireNonNull(tempMap.getBody()).isEmpty() && tempMap.getStatusCode() != HttpStatus.OK) {
            return tempMap;
        }

        return patientService.getPatientAppointment(id, token);
    }

    @GetMapping("/appointments")
    public ResponseEntity<?> getPatientAppointments(@RequestParam Long id, @RequestParam String token) {
        ResponseEntity<Map<String, String>> tempMap = centralService.validateToken(token, "patient");

        if (!Objects.requireNonNull(tempMap.getBody()).isEmpty() && tempMap.getStatusCode() != HttpStatus.OK) {
            return tempMap;
        }

        return patientService.getPatientAppointment(id, token);
    }

    @GetMapping("/filter/appointments")
    public ResponseEntity<?> filterPatientAppointment(@RequestParam String name, @RequestParam String condition, @RequestParam String token) {
        ResponseEntity<Map<String, String>> tempMap = centralService.validateToken(token, "patient");

        if (!Objects.requireNonNull(tempMap.getBody()).isEmpty() && tempMap.getStatusCode() != HttpStatus.OK) {
            return tempMap;
        }

        return centralService.filterPatient(name, condition, token);
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterPatientAppointmentByDoctor(@RequestParam Long patientId, @RequestParam Long doctorId, @RequestParam String token) {
        ResponseEntity<Map<String, String>> tempMap = centralService.validateToken(token, "doctor");

        if (!Objects.requireNonNull(tempMap.getBody()).isEmpty() && tempMap.getStatusCode() != HttpStatus.OK) {
            return tempMap;
        }

        return centralService.filterPatient(patientId, doctorId, token);
    }
}
