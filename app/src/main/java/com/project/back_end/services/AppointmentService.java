//package com.project.back_end.services;
//
//public class AppointmentService {
//// 1. **Add @Service Annotation**:
////    - To indicate that this class is a service layer class for handling business logic.
////    - The `@Service` annotation should be added before the class declaration to mark it as a Spring service component.
////    - Instruction: Add `@Service` above the class definition.
//
//// 2. **Constructor Injection for Dependencies**:
////    - The `AppointmentService` class requires several dependencies like `AppointmentRepository`, `Service`, `TokenService`, `PatientRepository`, and `DoctorRepository`.
////    - These dependencies should be injected through the constructor.
////    - Instruction: Ensure constructor injection is used for proper dependency management in Spring.
//
//// 3. **Add @Transactional Annotation for Methods that Modify Database**:
////    - The methods that modify or update the database should be annotated with `@Transactional` to ensure atomicity and consistency of the operations.
////    - Instruction: Add the `@Transactional` annotation above methods that interact with the database, especially those modifying data.
//
//// 4. **Book Appointment Method**:
////    - Responsible for saving the new appointment to the database.
////    - If the save operation fails, it returns `0`; otherwise, it returns `1`.
////    - Instruction: Ensure that the method handles any exceptions and returns an appropriate result code.
//
//// 5. **Update Appointment Method**:
////    - This method is used to update an existing appointment based on its ID.
////    - It validates whether the patient ID matches, checks if the appointment is available for updating, and ensures that the doctor is available at the specified time.
////    - If the update is successful, it saves the appointment; otherwise, it returns an appropriate error message.
////    - Instruction: Ensure proper validation and error handling is included for appointment updates.
//
//// 6. **Cancel Appointment Method**:
////    - This method cancels an appointment by deleting it from the database.
////    - It ensures the patient who owns the appointment is trying to cancel it and handles possible errors.
////    - Instruction: Make sure that the method checks for the patient ID match before deleting the appointment.
//
//// 7. **Get Appointments Method**:
////    - This method retrieves a list of appointments for a specific doctor on a particular day, optionally filtered by the patient's name.
////    - It uses `@Transactional` to ensure that database operations are consistent and handled in a single transaction.
////    - Instruction: Ensure the correct use of transaction boundaries, especially when querying the database for appointments.
//
//// 8. **Change Status Method**:
////    - This method updates the status of an appointment by changing its value in the database.
////    - It should be annotated with `@Transactional` to ensure the operation is executed in a single transaction.
////    - Instruction: Add `@Transactional` before this method to ensure atomicity when updating appointment status.
//
//
//}

package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;
    private final CentralService centralService;

    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        }
        catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();

        Optional<Appointment> optional = appointmentRepository.findById(appointment.getId());
        if (optional.isEmpty()) {
            response.put("message", "Appointment not found");
            return ResponseEntity.badRequest().body(response);
        }

        Appointment existing = optional.get();

        if (!Objects.equals(existing.getPatient().getId(), appointment.getPatient().getId())) {
            response.put("message", "Unauthorized update attempt");
            return ResponseEntity.status(403).body(response);
        }

        if (centralService.validateAppointment(appointment) != 1) {
            response.put("message", "Invalid appointment time or conflict");
            return ResponseEntity.badRequest().body(response);
        }

        appointmentRepository.save(appointment);
        response.put("message", "Appointment updated successfully");
        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();
        String email = tokenService.extractEmail(token);
        Patient patient = patientRepository.findByEmail(email);

        if (patient == null) {
            response.put("message", "Patient not found");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Appointment> optional = appointmentRepository.findById(id);
        if (optional.isEmpty()) {
            response.put("message", "Appointment not found");
            return ResponseEntity.badRequest().body(response);
        }

        Appointment appointment = optional.get();
        if (!Objects.equals(appointment.getPatient().getId(), patient.getId())) {
            response.put("message", "Unauthorized cancellation");
            return ResponseEntity.status(403).body(response);
        }

        appointmentRepository.delete(appointment);
        response.put("message", "Appointment cancelled successfully");
        return ResponseEntity.ok(response);
    }

    @Transactional
    public Map<String, Object> getAppointment(String patientName, LocalDate date, String token) {
        Map<String, Object> response = new HashMap<>();

        String email = tokenService.extractEmail(token);
        Doctor doctor = doctorRepository.findByEmail(email);

        if (doctor == null) {
            response.put("message", "Doctor not found");
            return response;
        }

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Appointment> appointments;
        if (patientName != null && !patientName.isBlank()) {
            appointments = appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(doctor.getId(), patientName, start, end);
        }
        else {
            appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctor.getId(), start, end);
        }

        response.put("appointments", appointments);
        return response;
    }

    @Transactional
    public void changeStatus(int status, long id) {
        appointmentRepository.updateStatus(status, id);
    }
}
