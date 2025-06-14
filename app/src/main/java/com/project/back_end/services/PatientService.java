//package com.project.back_end.services;
//
//public class PatientService {
//// 1. **Add @Service Annotation**:
////    - The `@Service` annotation is used to mark this class as a Spring service component.
////    - It will be managed by Spring's container and used for business logic related to patients and appointments.
////    - Instruction: Ensure that the `@Service` annotation is applied above the class declaration.
//
//// 2. **Constructor Injection for Dependencies**:
////    - The `PatientService` class has dependencies on `PatientRepository`, `AppointmentRepository`, and `TokenService`.
////    - These dependencies are injected via the constructor to maintain good practices of dependency injection and testing.
////    - Instruction: Ensure constructor injection is used for all the required dependencies.
//
//// 3. **createPatient Method**:
////    - Creates a new patient in the database. It saves the patient object using the `PatientRepository`.
////    - If the patient is successfully saved, the method returns `1`; otherwise, it logs the error and returns `0`.
////    - Instruction: Ensure that error handling is done properly and exceptions are caught and logged appropriately.
//
//// 4. **getPatientAppointment Method**:
////    - Retrieves a list of appointments for a specific patient, based on their ID.
////    - The appointments are then converted into `AppointmentDTO` objects for easier consumption by the API client.
////    - This method is marked as `@Transactional` to ensure database consistency during the transaction.
////    - Instruction: Ensure that appointment data is properly converted into DTOs and the method handles errors gracefully.
//
//// 5. **filterByCondition Method**:
////    - Filters appointments for a patient based on the condition (e.g., "past" or "future").
////    - Retrieves appointments with a specific status (0 for future, 1 for past) for the patient.
////    - Converts the appointments into `AppointmentDTO` and returns them in the response.
////    - Instruction: Ensure the method correctly handles "past" and "future" conditions, and that invalid conditions are caught and returned as errors.
//
//// 6. **filterByDoctor Method**:
////    - Filters appointments for a patient based on the doctor's name.
////    - It retrieves appointments where the doctor’s name matches the given value, and the patient ID matches the provided ID.
////    - Instruction: Ensure that the method correctly filters by doctor's name and patient ID and handles any errors or invalid cases.
//
//// 7. **filterByDoctorAndCondition Method**:
////    - Filters appointments based on both the doctor's name and the condition (past or future) for a specific patient.
////    - This method combines filtering by doctor name and appointment status (past or future).
////    - Converts the appointments into `AppointmentDTO` objects and returns them in the response.
////    - Instruction: Ensure that the filter handles both doctor name and condition properly, and catches errors for invalid input.
//
//// 8. **getPatientDetails Method**:
////    - Retrieves patient details using the `tokenService` to extract the patient's email from the provided token.
////    - Once the email is extracted, it fetches the corresponding patient from the `patientRepository`.
////    - It returns the patient's information in the response body.
//    //    - Instruction: Make sure that the token extraction process works correctly and patient details are fetched properly based on the extracted email.
//
//// 9. **Handling Exceptions and Errors**:
////    - The service methods handle exceptions using try-catch blocks and log any issues that occur. If an error occurs during database operations, the service responds with appropriate HTTP status codes (e.g., `500 Internal Server Error`).
////    - Instruction: Ensure that error handling is consistent across the service, with proper logging and meaningful error messages returned to the client.
//
//// 10. **Use of DTOs (Data Transfer Objects)**:
////    - The service uses `AppointmentDTO` to transfer appointment-related data between layers. This ensures that sensitive or unnecessary data (e.g., password or private patient information) is not exposed in the response.
////    - Instruction: Ensure that DTOs are used appropriately to limit the exposure of internal data and only send the relevant fields to the client.
//
//
//
//}


package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {
    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        }
        catch (Exception e) {
            logger.error("Error while creating patient: {}", e.getMessage());
            return 0;
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);

            if (patient == null || !Objects.equals(patient.getId(), id)) {
                response.put("error", "Unauthorized access");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

            List<AppointmentDTO> appointments = appointmentRepository
                    .findByPatientIdOrderByAppointmentTime(id)
                    .stream()
                    .map(this::convertAppointmentToDTO)
                    .collect(Collectors.toList());

            response.put("appointments", appointments);
            return ResponseEntity.ok(response);
        }
        catch (Exception e) {
            logger.error("Error retrieving patient appointments: {}", e.getMessage());
            response.put("error", "Failed to retrieve appointments");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            int status = switch (condition.toLowerCase()) {
                case "future" -> 0;
                case "past" -> 1;
                default -> -1;
            };

            if(status == -1){
                response.put("error", "Invalid condition: must be 'past' or 'future'");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            List<AppointmentDTO> appointments = appointmentRepository
                    .findByPatient_IdAndStatusOrderByAppointmentTimeAsc(id, status)
                    .stream()
                    .map(this::convertAppointmentToDTO)
                    .collect(Collectors.toList());

            response.put("appointments", appointments);
            return ResponseEntity.ok(response);
        }
        catch (Exception e) {
            logger.error("Error filtering appointments by condition: {}", e.getMessage());
            response.put("error", "Failed to filter appointments");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<AppointmentDTO> appointments = appointmentRepository
                    .filterByDoctorNameContainingIgnoreCaseAndPatientIdOrderByAppointmentTime(name, patientId)
                    .stream()
                    .map(this::convertAppointmentToDTO)
                    .collect(Collectors.toList());

            response.put("appointments", appointments);
            return ResponseEntity.ok(response);
        }
        catch (Exception e) {
            logger.error("Error filtering by doctor: {}", e.getMessage());
            response.put("error", "Failed to filter appointments by doctor");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, long patientId) {
        Map<String, Object> response = new HashMap<>();
        try {
            int status = switch (condition.toLowerCase()) {
                case "future" -> 0;
                case "past" -> 1;
                default -> -1;
            };

            if(status == -1){
                response.put("error", "Invalid condition: must be 'past' or 'future'");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            List<AppointmentDTO> appointments = appointmentRepository
                    .filterByDoctorNameContainingIgnoreCaseAndPatientIdAndStatusOrderByAppointmentTime(name, patientId, status)
                    .stream()
                    .map(this::convertAppointmentToDTO)
                    .collect(Collectors.toList());

            response.put("appointments", appointments);
            return ResponseEntity.ok(response);
        }
        catch (Exception e) {
            logger.error("Error filtering by doctor and condition: {}", e.getMessage());
            response.put("error", "Failed to filter appointments");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);

            if (patient == null) {
                response.put("error", "Patient not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            response.put("patient", patient);
            return ResponseEntity.ok(response);
        }
        catch (Exception e) {
            logger.error("Error fetching patient details: {}", e.getMessage());
            response.put("error", "Failed to fetch patient details");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Map<String, Object>> filterByPatientId(Long patientId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Appointment> appointments = appointmentRepository.findByPatientIdOrderByAppointmentTime(patientId);

            response.put("appointments", appointments);
            return ResponseEntity.ok(response);
        }
        catch (Exception e) {
            logger.error("Error fetching patient details: {}", e.getMessage());
            response.put("error", "Failed to fetch patient details");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private AppointmentDTO convertAppointmentToDTO(Appointment appointment) {
        AppointmentDTO appointmentDTO = new AppointmentDTO();

        appointmentDTO.setPatientId(appointment.getId());
        appointmentDTO.setStatus(appointment.getStatus());
        appointmentDTO.setAppointmentTime(LocalDateTime.from(appointment.getAppointmentTime()));

        // Doctor's Info
        appointmentDTO.setDoctorId(appointment.getDoctor().getId());
        appointmentDTO.setDoctorName(appointment.getDoctor().getName());

        // Patient's Info
        appointmentDTO.setPatientName(appointment.getPatient().getName());
        appointmentDTO.setPatientEmail(appointment.getPatient().getEmail());
        appointmentDTO.setPatientPhone(appointment.getPatient().getPhone());
        appointmentDTO.setPatientAddress(appointment.getPatient().getAddress());

        return appointmentDTO;
    }
}

