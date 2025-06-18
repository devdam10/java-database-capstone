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

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private static final Logger log = LoggerFactory.getLogger(AppointmentService.class);

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

        System.out.println("flag 1");
        appointmentRepository.save(existingAppointment);

        System.out.println("flag 2");
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

//    @Transactional
//    public Map<String, Object> getAppointment(String patientName, LocalDate date, String token) {
//        Map<String, Object> response = new HashMap<>();
//
//        String email = tokenService.extractEmail(token);
//        Doctor doctor = doctorRepository.findByEmail(email);
//
//        if (doctor == null) {
//            response.put("message", "Doctor not found");
//            return response;
//        }
//
//        LocalDateTime start = date.atStartOfDay();
//        LocalDateTime end = date.plusDays(1).atStartOfDay();
//
//        List<Appointment> appointments;
//        if (patientName != null && !patientName.isEmpty() && patientName.trim().equalsIgnoreCase("null")) {
//            appointments = appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(doctor.getId(), patientName, start, end);
//        }
//        else {
//            appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctor.getId(), start, end);
//        }
//
//        response.put("appointments", appointments);
//        return response;
//    }

    @Transactional
    public List<Appointment> getAppointment(String patientName, LocalDate date, String token) {
        String email = tokenService.extractEmail(token);
        Doctor doctor = doctorRepository.findByEmail(email);

        if (doctor == null) return new ArrayList<>();

        List<Appointment> appointments;
        if (patientName != null && !patientName.isEmpty() && !patientName.trim().equalsIgnoreCase("null") && date != null) {
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();

            appointments = appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetweenOrderByAppointmentTime(doctor.getId(), patientName, start, end);
        }
        else if (patientName != null && !patientName.isEmpty() && !patientName.trim().equalsIgnoreCase("null")) {
            appointments = appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseOrderByAppointmentTime(doctor.getId(), patientName);
        }
        else if (date != null) {
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();

            appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetweenOrderByAppointmentTime(doctor.getId(), start, end);
        }
        else {
            appointments = appointmentRepository.findByDoctorIdOrderByAppointmentTime(doctor.getId());
        }

        return appointments;
    }


    @Transactional
    public void changeStatus(int status, long id) {
        appointmentRepository.updateStatus(status, id);
    }
}
