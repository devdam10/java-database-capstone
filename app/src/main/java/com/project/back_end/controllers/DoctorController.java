package com.project.back_end.controllers;

import com.project.back_end.DTO.DoctorDTO;
import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.CentralService;
import com.project.back_end.services.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Controller for managing doctors in the healthcare system.
 * Provides endpoints for retrieving doctor availability, saving, updating, deleting doctors,
 * and filtering doctors based on various criteria.
 */
@RestController
@RequestMapping("${api.path}doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;
    private final CentralService centralService;

    /**
     * Retrieves the availability of a specific doctor for a given date.
     * Validates the token before processing the request.
     *
     * @param user       the username of the user making the request
     * @param doctorId   the ID of the doctor
     * @param date       the date for which availability is requested
     * @param token      the authentication token
     * @return ResponseEntity with available times or an error message
     */
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(@PathVariable String user, @PathVariable Long doctorId, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @PathVariable String token) {
        ResponseEntity<Map<String, String>> tempMap = centralService.validateToken(token, user);

        if (!Objects.requireNonNull(tempMap.getBody()).isEmpty() && tempMap.getStatusCode() != HttpStatus.OK) {
            // Return early with validation error (already wrapped as a response)
            return new ResponseEntity<>(new HashMap<>(tempMap.getBody()), tempMap.getStatusCode());
        }

        List<String> doctorAvailableTimes = doctorService.getDoctorAvailability(doctorId, date);

        Map<String, Object> response = new HashMap<>();
        response.put("availableTimes", doctorAvailableTimes);

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a list of all doctors.
     *
     * @return ResponseEntity containing a map with the list of doctors
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctors() {
        List<Doctor> doctors = doctorService.getDoctors();

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctors);

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a specific doctor by ID.
     *
     * @param id the ID of the doctor
     * @return ResponseEntity containing a map with the doctor's details
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDoctor(@PathVariable Long id) {
        DoctorDTO doctorDTO = new DoctorDTO();

        doctorDTO.copy(doctorService.findDoctorById(id));

        Map<String, Object> response = new HashMap<>();
        response.put("doctor", doctorDTO);

        return ResponseEntity.ok(response);
    }

    /**
     * Saves a new doctor to the database.
     * Validates the token from the Authorization header before processing the request.
     *
     * @param doctor                the doctor to be saved
     * @param authorizationHeader   the Authorization header containing the token
     * @return ResponseEntity with a message indicating success or failure
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> saveDoctor(@RequestBody @Valid Doctor doctor, @RequestHeader("Authorization") String authorizationHeader) {
        // Validate the token from the Authorization header
        String token = authorizationHeader.replace("Bearer ", "").trim();

        ResponseEntity<Map<String, String>> tempMap = centralService.validateToken(token, "admin");

        if (!Objects.requireNonNull(tempMap.getBody()).isEmpty() && tempMap.getStatusCode() != HttpStatus.OK) {
            return tempMap;
        }

        int result = doctorService.saveDoctor(doctor);
        Map<String, String> response = new HashMap<>();

        System.out.println("Result: " + result);

        if (result == 1) {
            response.put("message", "Doctor added to db");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        else if (result == -1) {
            response.put("message", "Doctor already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        response.put("message", "Some internal error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Validates the login credentials of a doctor.
     *
     * @param login the login credentials
     * @return ResponseEntity with a message indicating success or failure
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody @Valid Login login) {
        return doctorService.validateDoctor(login);
    }

    /**
     * Updates an existing doctor's details.
     * Validates the token before processing the request.
     *
     * @param doctor the doctor with updated details
     * @param token  the authentication token
     * @return ResponseEntity with a message indicating success or failure
     */
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(@RequestBody @Valid Doctor doctor, @PathVariable String token) {
        ResponseEntity<Map<String, String>> tempMap = centralService.validateToken(token, "admin");

        if (!Objects.requireNonNull(tempMap.getBody()).isEmpty() && tempMap.getStatusCode() != HttpStatus.OK) {
            return tempMap;
        }

        int status = doctorService.updateDoctor(doctor);
        Map<String, String> response = new HashMap<>();

        if (status == 1) {
            response.put("message", "Doctor updated successfully");
            return ResponseEntity.ok(response); // 200 OK
        }
        else if (status == 0) {
            response.put("message", "Doctor not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // 404 Not Found
        }
        else {
            response.put("message", "Some internal error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 500 Internal Server Error
        }
    }

    /**
     * Deletes a doctor by ID.
     * Validates the token before processing the request.
     *
     * @param id    the ID of the doctor to be deleted
     * @param token the authentication token
     * @return ResponseEntity with a message indicating success or failure
     */
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(@PathVariable Long id, @PathVariable String token) {
        ResponseEntity<Map<String, String>> tempMap = centralService.validateToken(token, "admin");

        if (!Objects.requireNonNull(tempMap.getBody()).isEmpty() && tempMap.getStatusCode() != HttpStatus.OK) {
            return tempMap;
        }

        int status = doctorService.deleteDoctor(id);
        Map<String, String> response = new HashMap<>();

        if (status == 1) {
            response.put("message", "Doctor deleted successfully");
            return ResponseEntity.ok(response); // 200 OK
        }
        else if (status == 0) {
            response.put("message", "Doctor not found with id");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // 404 Not Found
        }
        else {
            response.put("message", "Some internal error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 500 Internal Server Error
        }
    }

    // @GetMapping("/filter/{name}/{time}/{specialty}")
    // public ResponseEntity<List<Doctor>> filterDoctors(@PathVariable String name, @PathVariable String time, @PathVariable String specialty) {
    //     return ResponseEntity.ok(centralService.filterDoctor(name, time, specialty));
    // }

    /**
     * Filters doctors based on name, time, and specialty.
     * If any parameter is not provided, it will be ignored in the filtering process.
     *
     * @param name       the name of the doctor (optional)
     * @param time       the available time of the doctor (optional)
     * @param specialty  the specialty of the doctor (optional)
     * @return ResponseEntity with a list of filtered doctors
     */
    @GetMapping("/filter")
    public ResponseEntity<List<Doctor>> filterDoctors(@RequestParam(required = false) String name, @RequestParam(required = false) String time, @RequestParam(required = false) String specialty) {
        return ResponseEntity.ok(centralService.filterDoctor(name, time, specialty));
    }
}
