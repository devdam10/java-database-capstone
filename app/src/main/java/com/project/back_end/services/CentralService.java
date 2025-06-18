package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.DTO.Login;
import com.project.back_end.models.*;
import com.project.back_end.repo.*;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@RequiredArgsConstructor
public class CentralService {
    private static final Logger logger = LoggerFactory.getLogger(CentralService.class);

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        Map<String, String> response = new HashMap<>();
        try {
            boolean isValid = tokenService.validateToken(token, user);

            if (!isValid) {
                response.put("message", "Invalid or expired token.");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

            response.put("message", "Token is valid.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            logger.error("Token validation error: {}", e.getMessage());
            response.put("message", "Token validation failed.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();
        try {
            Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());

            if (admin != null) {
                if (admin.getPassword().equals(receivedAdmin.getPassword())) {
                    String token = tokenService.generateToken(admin.getUsername());
                    response.put("token", token);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
                else {
                    response.put("message", "Invalid password.");
                    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
                }
            }
            else {
                response.put("message", "Admin not found.");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        }
        catch (Exception e) {
            logger.error("Admin validation error: {}", e.getMessage());
            response.put("message", "Admin validation failed.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Doctor> filterDoctor(String name, String time, String specialty) {
        Map <String, Object> doctors;
        List<Doctor> doctorList = new ArrayList<>();

        try {

            if (isNotNullOrEmpty(name) && isNotNullOrEmpty(time) && isNotNullOrEmpty(specialty)) {
                doctors = doctorService.filterDoctorsByNameSpecialityAndTime(name, specialty, time);
            }
            else if (isNotNullOrEmpty(name) && isNotNullOrEmpty(specialty)) {
                doctors = doctorService.filterDoctorByNameAndSpeciality(name, specialty);
            }
            else if (isNotNullOrEmpty(name) && isNotNullOrEmpty(time)) {
                doctors = doctorService.filterDoctorByNameAndTime(name, time);
            }
            else if (isNotNullOrEmpty(specialty) && isNotNullOrEmpty(time)) {
                doctors = doctorService.filterDoctorByTimeAndSpeciality(specialty, time);
            }
            else if (isNotNullOrEmpty(name)) {
                doctors = doctorService.findDoctorByName(name);
            }
            else if (isNotNullOrEmpty(specialty)) {
                doctors = doctorService.filterDoctorBySpeciality(specialty);
            }
            else if (isNotNullOrEmpty(time)) {
                doctors = doctorService.filterDoctorsByTime(time);
            }
            else {
                doctors = new HashMap<>();
                doctors.put("doctors", doctorService.getDoctors());
            }

            doctorList = (List<Doctor>) doctors.getOrDefault("doctors", new ArrayList<>());
        }
        catch (Exception e) {
            logger.error("Error filtering doctors: {}", e.getMessage());
        }

        return doctorList;
    }

    @Transactional
    public int validateAppointment(Appointment appointment) {
        try {
            Optional<Doctor> doctorOpt = doctorRepository.findById(appointment.getDoctor().getId());
            if (doctorOpt.isEmpty()) {
                return -1;
            }

            Doctor doctor = doctorOpt.get();
            List<String> availableSlots = doctorService.getDoctorAvailability(doctor.getId(), appointment.getAppointmentDate());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            return availableSlots.contains(appointment.getAppointmentTime().format(formatter) + "-" + appointment.getEndTime().format(formatter)) ? 1 : 0;
        }
        catch (Exception e) {
            logger.error("Appointment validation error: {}", e.getMessage());
            return 0;
        }
    }

    @Transactional
    public int validateAppointment(AppointmentDTO appointmentDTO) {
        try {
            Optional<Doctor> doctorOpt = doctorRepository.findById(appointmentDTO.getDoctorId());
            if (doctorOpt.isEmpty()) {
                return -1;
            }

            Doctor doctor = doctorOpt.get();
            List<String> availableSlots = doctorService.getDoctorAvailability(doctor.getId(), appointmentDTO.getAppointmentDate());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            return availableSlots.contains(appointmentDTO.getAppointmentTime().format(formatter) + "-" + appointmentDTO.getEndTime().format(formatter)) ? 1 : 0;
        }
        catch (Exception e) {
            logger.error("Appointment validation error: {}", e.getMessage());
            return 0;
        }
    }

    public boolean validatePatient(Patient patient) {
        try {
            Patient existing = patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone());

            return existing == null; // valid only if no match found
        }
        catch (Exception e) {
            logger.error("Patient validation error: {}", e.getMessage());
            return false;
        }
    }

    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();
        try {
            Patient patient = patientRepository.findByEmail(login.getEmail());

            if (patient != null) {
                if (patient.getPassword().equals(login.getPassword())) {
                    String token = tokenService.generateToken(patient.getEmail());
                    response.put("token", token);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
                else {
                    response.put("message", "Invalid password.");
                    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
                }
            }
            else {
                response.put("message", "Patient not found.");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        }
        catch (Exception e) {
            logger.error("Patient login validation error: {}", e.getMessage());
            response.put("message", "Login failed.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Map<String, Object>> filterPatient(String name, String condition, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.extractEmail(token);
            // List<Appointment> appointments;

            ResponseEntity<Map<String, Object>> appointmentsResponse;

            Patient patient = patientRepository.findByEmail(email);
            if (patient == null) {
                response.put("message", "Patient not found.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // if (isNotNullOrEmpty(condition) && isNotNullOrEmpty(name)) {
            //     //appointments = patientService.filterByDoctorAndCondition(condition, name, patient.getId());
            //     appointmentsResponse = patientService.filterByDoctorAndCondition(condition, name, patient.getId());
            // }
            // else if (isNotNullOrEmpty(condition)) {
            //     // appointments = patientService.filterByCondition(condition, patient.getId());
            //     appointmentsResponse = patientService.filterByCondition(condition, patient.getId());
            // }
            // else if (isNotNullOrEmpty(name)) {
            //     // appointments = patientService.filterByDoctor(name, patient.getId());
            //     appointmentsResponse = patientService.filterByDoctor(name, patient.getId());
            // }
            // else {
            //     // appointments = patientService.filterByPatientId(patient.getId());
            //     appointmentsResponse = patientService.filterByPatientId(patient.getId());
            // }

            if (isNotNullOrEmpty(name) && isNotNullOrEmpty(condition)) {
                System.out.println("flag 1");
                appointmentsResponse = patientService.filterByDoctorNameAndCondition(name, condition, patient.getId());
            }
            else if (isNotNullOrEmpty(name)) {
                System.out.println("flag 2");
                appointmentsResponse = patientService.filterByDoctor(name, patient.getId());
            }
            else if (isNotNullOrEmpty(condition)) {
                System.out.println("flag 3");
                appointmentsResponse = patientService.filterByCondition(condition, patient.getId());
            }
            else {
                System.out.println("flag 4");
                appointmentsResponse = patientService.filterByPatientId(patient.getId());
            }

            // response.put("appointments", appointments);
            // return new ResponseEntity<>(response, HttpStatus.OK);

            return appointmentsResponse;
        }
        catch (Exception e) {
            logger.error("Error filtering patient appointments: {}", e.getMessage());
            response.put("message", "Error filtering appointments.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Map<String, Object>> filterPatient(Long patientId, @PathVariable Long doctorId, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.extractEmail(token);

            if (doctorRepository.findByEmail(email) == null) {
                response.put("message", "Doctor not found.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            if (patientRepository.findById(patientId).isEmpty()) {
                response.put("message", "Patient not found.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            response.put("appointments", appointmentRepository.findByDoctorIdAndPatientIdOrderByAppointmentTime(doctorId, patientId));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            logger.error("Error filtering patient appointments: {}", e.getMessage());
            response.put("message", "Error filtering appointments.");

            System.out.println("flag 6");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isNotNullOrEmpty(String text){
        return text != null && !text.isEmpty() && !text.trim().equalsIgnoreCase("null");
    }
}
