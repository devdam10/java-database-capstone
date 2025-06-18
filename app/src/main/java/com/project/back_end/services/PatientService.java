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

    public ResponseEntity<Map<String, Object>> filterByDoctorNameAndCondition(String name, String condition, long patientId) {
        Map<String, Object> response = new HashMap<>();
        try {
            int status = switch (condition.toLowerCase()) {
                case "future" -> 0;
                case "past" -> 1;
                default -> 0; // Default to future if condition is invalid
            };

            // if(status == -1){
            //     response.put("error", "Invalid condition: must be 'past' or 'future'");
            //     return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            // }

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
            //List<Appointment> appointments = appointmentRepository.findByPatientIdOrderByAppointmentTime(patientId);

            List<AppointmentDTO> appointments = appointmentRepository
                    .findByPatientIdOrderByAppointmentTime(patientId)
                    .stream()
                    .map(this::convertAppointmentToDTO)
                    .collect(Collectors.toList());

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

        appointmentDTO.setId(appointment.getId());
        appointmentDTO.setStatus(appointment.getStatus());
        // appointmentDTO.setAppointmentTime(LocalDateTime.from(appointment.getAppointmentTime()));
        appointmentDTO.setAppointmentTime(appointment.getEndTime().minusHours(1));

        // Doctor's Info
        appointmentDTO.setDoctorId(appointment.getDoctor().getId());
        appointmentDTO.setDoctorName(appointment.getDoctor().getName());

        // Patient's Info
        appointmentDTO.setPatientId(appointment.getPatient().getId());
        appointmentDTO.setPatientName(appointment.getPatient().getName());
        appointmentDTO.setPatientEmail(appointment.getPatient().getEmail());
        appointmentDTO.setPatientPhone(appointment.getPatient().getPhone());
        appointmentDTO.setPatientAddress(appointment.getPatient().getAddress());

        return appointmentDTO;
    }
}

