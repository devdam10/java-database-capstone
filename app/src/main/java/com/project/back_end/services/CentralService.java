//package com.project.back_end.services;
//
//public class Service {
//// 1. **@Service Annotation**
//// The @Service annotation marks this class as a service component in Spring. This allows Spring to automatically detect it through component scanning
//// and manage its lifecycle, enabling it to be injected into controllers or other services using @Autowired or constructor injection.
//
//// 2. **Constructor Injection for Dependencies**
//// The constructor injects all required dependencies (TokenService, Repositories, and other Services). This approach promotes loose coupling, improves testability,
//// and ensures that all required dependencies are provided at object creation time.
//
//// 3. **validateToken Method**
//// This method checks if the provided JWT token is valid for a specific user. It uses the TokenService to perform the validation.
//// If the token is invalid or expired, it returns a 401 Unauthorized response with an appropriate error message. This ensures security by preventing
//// unauthorized access to protected resources.
//
//// 4. **validateAdmin Method**
//// This method validates the login credentials for an admin user.
//// - It first searches the admin repository using the provided username.
//// - If an admin is found, it checks if the password matches.
//// - If the password is correct, it generates and returns a JWT token (using the admin’s username) with a 200 OK status.
//// - If the password is incorrect, it returns a 401 Unauthorized status with an error message.
//// - If no admin is found, it also returns a 401 Unauthorized.
//// - If any unexpected error occurs during the process, a 500 Internal Server Error response is returned.
//// This method ensures that only valid admin users can access secured parts of the system.
//
//// 5. **filterDoctor Method**
//// This method provides filtering functionality for doctors based on name, specialty, and available time slots.
//// - It supports various combinations of the three filters.
//// - If none of the filters are provided, it returns all available doctors.
//// This flexible filtering mechanism allows the frontend or consumers of the API to search and narrow down doctors based on user criteria.
//
//// 6. **validateAppointment Method**
//// This method validates if the requested appointment time for a doctor is available.
//// - It first checks if the doctor exists in the repository.
//// - Then, it retrieves the list of available time slots for the doctor on the specified date.
//// - It compares the requested appointment time with the start times of these slots.
//// - If a match is found, it returns 1 (valid appointment time).
//// - If no matching time slot is found, it returns 0 (invalid).
//// - If the doctor doesn’t exist, it returns -1.
//// This logic prevents overlapping or invalid appointment bookings.
//
//// 7. **validatePatient Method**
//// This method checks whether a patient with the same email or phone number already exists in the system.
//// - If a match is found, it returns false (indicating the patient is not valid for new registration).
//// - If no match is found, it returns true.
//// This helps enforce uniqueness constraints on patient records and prevent duplicate entries.
//
//// 8. **validatePatientLogin Method**
//// This method handles login validation for patient users.
//// - It looks up the patient by email.
//// - If found, it checks whether the provided password matches the stored one.
//// - On successful validation, it generates a JWT token and returns it with a 200 OK status.
//// - If the password is incorrect or the patient doesn't exist, it returns a 401 Unauthorized with a relevant error.
//// - If an exception occurs, it returns a 500 Internal Server Error.
//// This method ensures only legitimate patients can log in and access their data securely.
//
//// 9. **filterPatient Method**
//// This method filters a patient's appointment history based on condition and doctor name.
//// - It extracts the email from the JWT token to identify the patient.
//// - Depending on which filters (condition, doctor name) are provided, it delegates the filtering logic to PatientService.
//// - If no filters are provided, it retrieves all appointments for the patient.
//// This flexible method supports patient-specific querying and enhances user experience on the client side.
//
//
//}

package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.DTO.Login;
import com.project.back_end.models.*;
import com.project.back_end.repo.*;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Service
@RequiredArgsConstructor
public class CentralService {
    private static final Logger logger = LoggerFactory.getLogger(CentralService.class);

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
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

    public Map<String, Object> filterDoctor(String name, String time, String specialty) {
        //logger.info("Filter doctors called with name: {}, time: {}, speciality: {}", name.length(), time, specialty);

        Map<String, Object> response = new HashMap<>();
        try {
            Map <String, Object> doctors;
            logger.info("flag 1");

//            if (name != null && !name.isEmpty() && specialty != null && !specialty.isEmpty() && time != null && !time.isEmpty()) {
//                logger.info("flag 2");
//                doctors = doctorService.filterDoctorsByNameSpecialityAndTime(name, specialty, time);
//            }
//            else if (name != null && !name.isEmpty() && specialty != null && !specialty.isEmpty() ) {
//                logger.info("flag 3");
//                doctors = doctorService.filterDoctorByNameAndSpeciality(name, specialty);
//            }
//            else if (name != null && !name.isEmpty()  && time != null && !time.isEmpty()) {
//                logger.info("flag 4");
//                doctors = doctorService.filterDoctorByNameAndTime(name, time);
//            }
//            else if (specialty != null && !specialty.isEmpty() && time != null && !time.isEmpty() ) {
//                logger.info("flag 5");
//                doctors = doctorService.filterDoctorByTimeAndSpeciality(specialty, time);
//            }
//            else if (name != null && !name.isEmpty()) {
//                logger.info("flag 6");
//                doctors = doctorService.findDoctorByName(name);
//            }
//            else if (specialty != null && !specialty.isEmpty()) {
//                logger.info("flag 7");
//                doctors = doctorService.filterDoctorBySpeciality(specialty);
//            }
//            else if (time != null && !time.isEmpty()) {
//                logger.info("flag 8");
//                doctors = doctorService.filterDoctorsByTime(time);
//            }
//            else {
//                logger.info("flag 9");
//                doctors = new HashMap<>();
//                doctors.put("doctors", doctorService.getDoctors());
//            }

            if (isNotNullOrEmpty(name) && isNotNullOrEmpty(time) && isNotNullOrEmpty(specialty)) {
                logger.info("flag 2");
                doctors = doctorService.filterDoctorsByNameSpecialityAndTime(name, specialty, time);
            }
            else if (isNotNullOrEmpty(name) && isNotNullOrEmpty(specialty)) {
                logger.info("flag 3");
                doctors = doctorService.filterDoctorByNameAndSpeciality(name, specialty);
            }
            else if (isNotNullOrEmpty(name) && isNotNullOrEmpty(time)) {
                logger.info("flag 4");
                doctors = doctorService.filterDoctorByNameAndTime(name, time);
            }
            else if (isNotNullOrEmpty(specialty) && isNotNullOrEmpty(time)) {
                logger.info("flag 5");
                doctors = doctorService.filterDoctorByTimeAndSpeciality(specialty, time);
            }
            else if (isNotNullOrEmpty(name)) {
                logger.info("flag 6");
                doctors = doctorService.findDoctorByName(name);
            }
            else if (isNotNullOrEmpty(specialty)) {
                logger.info("flag 7");
                doctors = doctorService.filterDoctorBySpeciality(specialty);
            }
            else if (isNotNullOrEmpty(time)) {
                logger.info("flag 8");
                doctors = doctorService.filterDoctorsByTime(time);
            }
            else {
                logger.info("flag 9");
                doctors = new HashMap<>();
                doctors.put("doctors", doctorService.getDoctors());
            }

            logger.info("flag 10");

            response.put("doctors", doctors.getOrDefault("doctors", new HashMap<>()));
        } catch (Exception e) {
            logger.error("Error filtering doctors: {}", e.getMessage());
            response.put("message", "Failed to filter doctors.");
        }

        return response;
    }

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

    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
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

            if (condition != null && name != null) {
                //appointments = patientService.filterByDoctorAndCondition(condition, name, patient.getId());
                appointmentsResponse = patientService.filterByDoctorAndCondition(condition, name, patient.getId());
            }
            else if (condition != null) {
                // appointments = patientService.filterByCondition(condition, patient.getId());
                appointmentsResponse = patientService.filterByCondition(condition, patient.getId());
            }
            else if (name != null) {
                // appointments = patientService.filterByDoctor(name, patient.getId());
                appointmentsResponse = patientService.filterByDoctor(name, patient.getId());
            }
            else {
                // appointments = patientService.filterByPatientId(patient.getId());
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

    private boolean isNotNullOrEmpty(String text){
        return text != null && !text.isEmpty() && !text.trim().equalsIgnoreCase("null");
    }
}
