package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service class for managing appointments.
 * Provides methods to book, update, cancel, and retrieve appointments.
 * Uses repositories for data access and a token service for authentication.
 */

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private static final Logger log = LoggerFactory.getLogger(AppointmentService.class);

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;
    private final CentralService centralService;

    /**
     * Book an appointment using the provided Appointment object.
     * @param appointment The Appointment object to be booked.
     * @return 1 if booking is successful, 0 otherwise.
     */
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

    /**
     * Book an appointment using the provided AppointmentDTO object.
     * @param appointmentDTO The AppointmentDTO object containing appointment details.
     * @return 1 if booking is successful, 0 otherwise.
     */
    @Transactional
    public int bookAppointment(AppointmentDTO appointmentDTO) {
        Appointment appointment = new Appointment();
        appointment.setDoctor(doctorRepository.findById(appointmentDTO.getDoctorId()).orElse(null));
        appointment.setPatient(patientRepository.findById(appointmentDTO.getPatientId()).orElse(null));
        appointment.setAppointmentTime(appointmentDTO.getAppointmentTime());
        appointment.setStatus(0);

        try {
            appointmentRepository.save(appointment);
            return 1;
        }
        catch (Exception e) {
            return 0;
        }
    }

    /**
     * Update an existing appointment using the provided Appointment object.
     * @param appointment The Appointment object containing updated details.
     * @return ResponseEntity with a message indicating success or failure.
     */
    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();

        Optional<Appointment> optional = appointmentRepository.findById(appointment.getId());
        if (optional.isEmpty()) {
            response.put("message", "Appointment not found");
            return ResponseEntity.badRequest().body(response);
        }

        // Appointment existing = optional.get();
        Appointment existingAppointment = optional.get();
        if (!Objects.equals(existingAppointment.getPatient().getId(), appointment.getPatient().getId())) {
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

    /**
     * Update an existing appointment using the provided AppointmentDTO object.
     * @param appointmentDTO The AppointmentDTO object containing updated details.
     * @return ResponseEntity with a message indicating success or failure.
     */
    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(AppointmentDTO appointmentDTO) {
        Map<String, String> response = new HashMap<>();

        Optional<Appointment> optional = appointmentRepository.findById(appointmentDTO.getId());

        if (optional.isEmpty()) {
            response.put("message", "Appointment not found");
            return ResponseEntity.badRequest().body(response);
        }

        Appointment existingAppointment = optional.get();
        if (!Objects.equals(existingAppointment.getPatient().getId(), appointmentDTO.getPatientId())) {
            response.put("message", "Unauthorized update attempt");
            return ResponseEntity.status(403).body(response);
        }

        if (!Objects.equals(existingAppointment.getDoctor().getId(), appointmentDTO.getDoctorId())) {
            response.put("message", "Unauthorized update attempt");
            return ResponseEntity.status(403).body(response);
        }

        // Update appointment details
        existingAppointment.setAppointmentTime(appointmentDTO.getAppointmentTime());

        if (centralService.validateAppointment(existingAppointment) != 1) {
            response.put("message", "Invalid appointment time or conflict");
            return ResponseEntity.badRequest().body(response);
        }

        appointmentRepository.save(existingAppointment);
        response.put("message", "Appointment updated successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel an appointment by its ID and the patient's token.
     * @param id The ID of the appointment to be cancelled.
     * @param token The token of the patient requesting the cancellation.
     * @return ResponseEntity with a message indicating success or failure.
     */
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

    /**
     * Retrieve appointments for a doctor based on patient name and/or a specific date.
     * @param patientName The name of the patient to filter appointments.
     * @param date The date to filter appointments.
     * @param token The token of the doctor requesting the appointments.
     * @return List of appointments matching the criteria.
     */
    @Transactional
    public List<Appointment> getAppointments(String patientName, LocalDate date, String token) {
        String email = tokenService.extractEmail(token);
        Doctor doctor = doctorRepository.findByEmail(email);

        if (doctor == null) return new ArrayList<>();

        List<Appointment> appointments;

        if (patientName != null && !patientName.isEmpty() && !patientName.trim().equalsIgnoreCase("null") && date != null) {
            // If both patientName and date are provided, filter by both
            // Convert LocalDate to LocalDateTime for start and end of the day
            // to ensure appointments are filtered correctly within the date range
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();

            appointments = appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetweenOrderByAppointmentTime(doctor.getId(), patientName, start, end);
        }
        else if (patientName != null && !patientName.isEmpty() && !patientName.trim().equalsIgnoreCase("null")) {
            // If only patientName is provided, filter by patient name
            appointments = appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseOrderByAppointmentTime(doctor.getId(), patientName);
        }
        else if (date != null) {

            // If only date is provided, filter by date
            // Convert LocalDate to LocalDateTime for start and end of the day
            // to ensure appointments are filtered correctly within the date range
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();

            appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetweenOrderByAppointmentTime(doctor.getId(), start, end);
        }
        else {
            // If neither patientName nor date is provided, return all appointments for the doctor
            appointments = appointmentRepository.findByDoctorIdOrderByAppointmentTime(doctor.getId());
        }

        return appointments;
    }

    /**
     * Retrieve appointments for a patient based on their token.
     * @param token The token of the patient requesting the appointments.
     * @return List of appointments for the patient.
     */
    @Transactional
    public List<Appointment> getAppointmentsForPatient(String token) {
        String email = tokenService.extractEmail(token);
        Patient patient = patientRepository.findByEmail(email);

        if (patient == null) return new ArrayList<>();

        return appointmentRepository.findByPatientIdOrderByAppointmentTime(patient.getId());
    }

    /**
     * Change the status of an appointment.
     * @param status The new status to set for the appointment.
     * @param appointmentId The ID of the appointment to update.
     * @return void
     */
    @Transactional
    public void changeStatus(int status, long appointmentId) {
        appointmentRepository.updateStatus(status, appointmentId);
    }
}
