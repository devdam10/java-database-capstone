package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Prescription;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.CentralService;
import com.project.back_end.services.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("${api.path}prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {
    private final PrescriptionService prescriptionService;
    private final CentralService centralService;
    private final AppointmentService appointmentService;

    private final AppointmentRepository appointmentRepository;

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(@PathVariable String token, @RequestBody @Valid Prescription prescription) {
        // Validate token for 'doctor' role
        ResponseEntity<Map<String, String>> tokenValidation = centralService.validateToken(token, "doctor");

        if (!Objects.requireNonNull(tokenValidation.getBody()).isEmpty() && tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }

        Optional<Appointment> appointmentOptional = appointmentRepository.findById(prescription.getAppointmentId());
        if(appointmentOptional.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Appointment not found");
            return ResponseEntity.status(404).body(response);
        }

        // Update the appointment status to reflect prescription was added
        Appointment appointment = appointmentOptional.get();
        appointment.setStatus(1);

        appointmentService.updateAppointment(appointment);

        // Save the prescription
        return prescriptionService.savePrescription(prescription);
    }

    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescription(@PathVariable Long appointmentId, @PathVariable String token) {
        // Validate token for 'doctor' role
        ResponseEntity<Map<String, String>> tokenValidation = centralService.validateToken(token, "doctor");

        if (!Objects.requireNonNull(tokenValidation.getBody()).isEmpty() && tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }

        return prescriptionService.getPrescription(appointmentId);
    }
}
