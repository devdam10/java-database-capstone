//package com.project.back_end.controllers;
//
//public class PatientController {
//
//// 1. Set Up the Controller Class:
////    - Annotate the class with `@RestController` to define it as a REST API controller for patient-related operations.
////    - Use `@RequestMapping("/patient")` to prefix all endpoints with `/patient`, grouping all patient functionalities under a common route.
//
//
//// 2. Autowire Dependencies:
////    - Inject `PatientService` to handle patient-specific logic such as creation, retrieval, and appointments.
////    - Inject the shared `Service` class for tasks like token validation and login authentication.
//
//
//// 3. Define the `getPatient` Method:
////    - Handles HTTP GET requests to retrieve patient details using a token.
////    - Validates the token for the `"patient"` role using the shared service.
////    - If the token is valid, returns patient information; otherwise, returns an appropriate error message.
//
//
//// 4. Define the `createPatient` Method:
////    - Handles HTTP POST requests for patient registration.
////    - Accepts a validated `Patient` object in the request body.
////    - First checks if the patient already exists using the shared service.
////    - If validation passes, attempts to create the patient and returns success or error messages based on the outcome.
//
//
//// 5. Define the `login` Method:
////    - Handles HTTP POST requests for patient login.
////    - Accepts a `Login` DTO containing email/username and password.
////    - Delegates authentication to the `validatePatientLogin` method in the shared service.
////    - Returns a response with a token or an error message depending on login success.
//
//
//// 6. Define the `getPatientAppointment` Method:
////    - Handles HTTP GET requests to fetch appointment details for a specific patient.
////    - Requires the patient ID, token, and user role as path variables.
////    - Validates the token using the shared service.
////    - If valid, retrieves the patient's appointment data from `PatientService`; otherwise, returns a validation error.
//
//
//// 7. Define the `filterPatientAppointment` Method:
////    - Handles HTTP GET requests to filter a patient's appointments based on specific conditions.
////    - Accepts filtering parameters: `condition`, `name`, and a token.
////    - Token must be valid for a `"patient"` role.
////    - If valid, delegates filtering logic to the shared service and returns the filtered result.
//
//
//
//}
//
//


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
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientController {
    private PatientService patientService;
    private CentralService centralService;

    @GetMapping("/{token}")
    public ResponseEntity<?> getPatient(@PathVariable String token) {
        ResponseEntity<Map<String, String>> tempMap = centralService.validateToken(token, "patient");

        if (!Objects.requireNonNull(tempMap.getBody()).isEmpty()) {
            return tempMap;
        }

        return patientService.getPatientDetails(token);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createPatient(@RequestBody @Valid Patient patient) {
        // Check if patient already exists
        if (centralService.validatePatient(patient)) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Patient with email ID or phone number already exists"));
        }

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

        if (!Objects.requireNonNull(tempMap.getBody()).isEmpty()) {
            return tempMap;
        }

        return patientService.getPatientAppointment(id, token);
    }

    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<?> filterPatientAppointment(@PathVariable String condition, @PathVariable String name, @PathVariable String token) {
        ResponseEntity<Map<String, String>> tempMap = centralService.validateToken(token, "patient");

        if (!Objects.requireNonNull(tempMap.getBody()).isEmpty()) {
            return tempMap;
        }

        return centralService.filterPatient(condition, name, token);
    }
}
