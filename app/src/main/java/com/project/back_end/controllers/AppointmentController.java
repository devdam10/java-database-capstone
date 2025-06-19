package com.project.back_end.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.project.back_end.services.CentralService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.services.AppointmentService;

import jakarta.validation.Valid;

/**
 * Controller for managing appointments in the healthcare system.
 * Provides endpoints for booking, updating, retrieving, and canceling appointments.
 */
@RestController
@RequestMapping("${api.path}appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final CentralService centralService;

    /**
     * Retrieves appointments for a specific date and patient name.
     * Validates the token before processing the request.
     *
     * @param date         the date of the appointments
     * @param patientName  the name of the patient
     * @param token        the authentication token
     * @return ResponseEntity with the list of appointments or an error message
     */
    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<?> getAppointments(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @PathVariable String patientName,@PathVariable String token) {
        Map<String, Object> map;
        ResponseEntity<Map<String, String>> tempMap = centralService.validateToken(token, "doctor");

        if (!Objects.requireNonNull(tempMap.getBody()).isEmpty() && tempMap.getStatusCode() != HttpStatus.OK) {
            map = new HashMap<>(tempMap.getBody());
            return new ResponseEntity<>(map, tempMap.getStatusCode());
        }

        // map = appointmentService.getAppointment(patientName, date, token);
        return ResponseEntity.status(HttpStatus.OK).body(appointmentService.getAppointments(patientName, date, token));
    }

    /**
     * Retrieves appointments for a specific patient.
     * Validates the token before processing the request.
     *
     * @param patientName the name of the patient
     * @param token       the authentication token
     * @return ResponseEntity with the list of appointments or an error message
     */
    @GetMapping("/{patientName}/{token}")
    public ResponseEntity<?> getAppointments(@PathVariable String patientName, @PathVariable String token) {
        Map<String, Object> map;
        ResponseEntity<Map<String, String>> tempMap = centralService.validateToken(token, "doctor");

        if (!Objects.requireNonNull(tempMap.getBody()).isEmpty() && tempMap.getStatusCode() != HttpStatus.OK) {
            map = new HashMap<>(tempMap.getBody());
            return new ResponseEntity<>(map, tempMap.getStatusCode());
        }

        return ResponseEntity.status(HttpStatus.OK).body(appointmentService.getAppointments(patientName, null, token));
    }

    /**
     * Retrieves appointments for the authenticated patient.
     * Validates the token before processing the request.
     *
     * @param authorizationHeader the Authorization header containing the token
     * @return ResponseEntity with the list of appointments or an error message
     */
    @GetMapping("/patient")
    public ResponseEntity<?> getAppointmentsByPatient(@RequestHeader("Authorization") String authorizationHeader) {
        // Extract the token from the Authorization header
        String token = authorizationHeader.replace("Bearer ", "");
        
        ResponseEntity<Map<String, String>> tempMap = centralService.validateToken(token, "patient");

        Map<String, Object> map;

        if (!Objects.requireNonNull(tempMap.getBody()).isEmpty() && tempMap.getStatusCode() != HttpStatus.OK) {
            map = new HashMap<>(tempMap.getBody());
            return new ResponseEntity<>(map, tempMap.getStatusCode());
        }

        return ResponseEntity.status(HttpStatus.OK).body(appointmentService.getAppointmentsForPatient(token));
    }

    /**
     * Books an appointment for a patient.
     * Validates the appointment details and the token before processing the request.
     *
     * @param appointmentDTO       the appointment details
     * @param authorizationHeader  the Authorization header containing the token
     * @return ResponseEntity with a message indicating success or failure
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> bookAppointment(@RequestBody @Valid AppointmentDTO appointmentDTO, @RequestHeader("Authorization") String authorizationHeader) {
        // Extract the token from the Authorization header
        String token = authorizationHeader.replace("Bearer ", "");
        
        ResponseEntity<Map<String, String>> tempMap = centralService.validateToken(token, "patient");

        if (isTokenInvalid(tempMap)) return tempMap;

        Map<String, String> response = new HashMap<>();
        int out = centralService.validateAppointment(appointmentDTO);

        if (out == 1) {
            int res = appointmentService.bookAppointment(appointmentDTO);

            if (res == 1) {
                response.put("message", "Appointment Booked Successfully");
                return ResponseEntity.status(HttpStatus.CREATED).body(response); // 201 Created

            }
            response.put("message", "Internal Server Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 409 Conflict

        }
        else if (out == -1) {
            response.put("message", "Invalid doctor id");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        response.put("message", "Appointment already booked for given time or Doctor not available");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Updates an existing appointment.
     * Validates the appointment details and the token before processing the request.
     *
     * @param token          the authentication token
     * @param appointmentDTO the updated appointment details
     * @return ResponseEntity with a message indicating success or failure
     */
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(@PathVariable String token, @RequestBody @Valid AppointmentDTO appointmentDTO) {
        // ResponseEntity<Map<String, String>> tempMap = centralService.validateToken(token, "patient");
        ResponseEntity<Map<String, String>> tempMap = centralService.validateToken(token, "doctor");

        if (isTokenInvalid(tempMap)) return tempMap;

        return appointmentService.updateAppointment(appointmentDTO);
    }

    /**
     * Cancels an appointment.
     * Validates the token before processing the request.
     *
     * @param id    the ID of the appointment to cancel
     * @param token the authentication token
     * @return ResponseEntity with a message indicating success or failure
     */
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>>  cancelAppointment(@PathVariable Long id, @PathVariable String token) {
        ResponseEntity<Map<String, String>> tempMap = centralService.validateToken(token, "patient");

        if (isTokenInvalid(tempMap)) return tempMap;

        return appointmentService.cancelAppointment(id,token);
    }

    /**
     * Checks if the token is invalid based on the validation response.
     *
     * @param validationResponse the response from the token validation
     * @return true if the token is invalid, false otherwise
     */
    private boolean isTokenInvalid(ResponseEntity<Map<String, String>> validationResponse) {
        return !Objects.requireNonNull(validationResponse.getBody()).isEmpty() && validationResponse.getStatusCode() != HttpStatus.OK;
    }

}
