//package com.project.back_end.controllers;
//
//public class PrescriptionController {
//
//// 1. Set Up the Controller Class:
////    - Annotate the class with `@RestController` to define it as a REST API controller.
////    - Use `@RequestMapping("${api.path}prescription")` to set the base path for all prescription-related endpoints.
////    - This controller manages creating and retrieving prescriptions tied to appointments.
//
//
//// 2. Autowire Dependencies:
////    - Inject `PrescriptionService` to handle logic related to saving and fetching prescriptions.
////    - Inject the shared `Service` class for token validation and role-based access control.
////    - Inject `AppointmentService` to update appointment status after a prescription is issued.
//
//
//// 3. Define the `savePrescription` Method:
////    - Handles HTTP POST requests to save a new prescription for a given appointment.
////    - Accepts a validated `Prescription` object in the request body and a doctor’s token as a path variable.
////    - Validates the token for the `"doctor"` role.
////    - If the token is valid, updates the status of the corresponding appointment to reflect that a prescription has been added.
////    - Delegates the saving logic to `PrescriptionService` and returns a response indicating success or failure.
//
//
//// 4. Define the `getPrescription` Method:
////    - Handles HTTP GET requests to retrieve a prescription by its associated appointment ID.
////    - Accepts the appointment ID and a doctor’s token as path variables.
////    - Validates the token for the `"doctor"` role using the shared service.
////    - If the token is valid, fetches the prescription using the `PrescriptionService`.
////    - Returns the prescription details or an appropriate error message if validation fails.
//
//
//}


package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Prescription;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.CentralService;
import com.project.back_end.services.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("${api.path}prescription")
@RequiredArgsConstructor
public class PrescriptionController {
    private final PrescriptionService prescriptionService;
    private final CentralService centralService;
    private final AppointmentService appointmentService;

    private final AppointmentRepository appointmentRepository;

    // 1. Save a new Prescription
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(@PathVariable String token, @RequestBody @Valid Prescription prescription) {
        // Validate token for 'doctor' role
        ResponseEntity<Map<String, String>> tokenValidation = centralService.validateToken(token, "doctor");

        if (!Objects.requireNonNull(tokenValidation.getBody()).isEmpty()) {
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

    // 2. Get a Prescription by appointment ID
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescription(@PathVariable Long appointmentId, @PathVariable String token) {
        // Validate token for 'doctor' role
        ResponseEntity<Map<String, String>> tokenValidation = centralService.validateToken(token, "doctor");

        if (!Objects.requireNonNull(tokenValidation.getBody()).isEmpty()) {
            return tokenValidation;
        }

        return prescriptionService.getPrescription(appointmentId);
    }
}
