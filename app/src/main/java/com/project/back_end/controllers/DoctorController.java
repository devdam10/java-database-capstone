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

@RestController
@RequestMapping("${api.path}doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;
    private final CentralService centralService;

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

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctors() {
        List<Doctor> doctors = doctorService.getDoctors();

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctors);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDoctor(@PathVariable Long id) {
        DoctorDTO doctorDTO = new DoctorDTO();

        doctorDTO.copy(doctorService.findDoctorById(id));

        Map<String, Object> response = new HashMap<>();
        response.put("doctor", doctorDTO);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> saveDoctor(@RequestBody @Valid Doctor doctor, @PathVariable String token) {
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

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody @Valid Login login) {
        return doctorService.validateDoctor(login);
    }

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(@RequestBody @Valid Doctor doctor, @PathVariable String token) {
        ResponseEntity<Map<String, String>> tempMap = centralService.validateToken(token, "admin");

        if (!Objects.requireNonNull(tempMap.getBody()).isEmpty()) {
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

    @GetMapping("/filter/{name}/{time}/{specialty}")
    public ResponseEntity<List<Doctor>> filterDoctors( @PathVariable String name, @PathVariable String time, @PathVariable String specialty) {
        return ResponseEntity.ok(centralService.filterDoctor(name, time, specialty));
    }
}
