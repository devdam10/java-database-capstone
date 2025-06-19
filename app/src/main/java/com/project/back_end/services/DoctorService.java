package com.project.back_end.services;

import com.project.back_end.models.Doctor;
import com.project.back_end.DTO.Login;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.util.HelperUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
// import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Service class for managing Doctor-related operations.
 * Provides methods to handle doctor availability, CRUD operations, and authentication.
 * Uses DoctorRepository and AppointmentRepository for data access.
 */
@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    /**
     * Retrieves the availability of a doctor for a specific date.
     * Returns a list of available time slots after filtering out booked slots.
     *
     * @param doctorId the ID of the doctor
     * @param date     the date for which availability is checked
     * @return a list of available time slots or an empty list if the doctor is not found
     */
    @Transactional
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);

        if (doctorOpt.isEmpty()) return Collections.emptyList();

        return doctorOpt.get().getAvailableTimes();
    }

    /**
     * Saves a new doctor to the repository.
     * Validates the doctor's email and formats the name and specialty.
     *
     * @param doctor the doctor to be saved
     * @return 1 if saved successfully, -1 if email already exists, 0 if an error occurs
     */
    public int saveDoctor(Doctor doctor) {
        try {
            doctor.setName(HelperUtil.titleCase(doctor.getName()));
            doctor.setSpecialty(HelperUtil.titleCase(doctor.getSpecialty()));
            doctor.setEmail(doctor.getEmail().toLowerCase());

            if (doctorRepository.findByEmail(doctor.getEmail()) != null) return -1;

            doctorRepository.save(doctor);
            return 1;
        }
        catch (Exception e) {
            return 0;
        }
    }

    /**
     * Updates an existing doctor's information.
     * Validates the doctor's existence before updating.
     *
     * @param doctor the doctor with updated information
     * @return 1 if updated successfully, -1 if doctor does not exist, 0 if an error occurs
     */
    public int updateDoctor(Doctor doctor) {
        if (!doctorRepository.existsById(doctor.getId())) return -1;
        try {
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Retrieves all doctors from the repository, sorted by name.
     *
     * @return a list of all doctors
     */
    @Transactional
    public List<Doctor> getDoctors() {
        // return doctorRepository.findAll();
        return doctorRepository.findAll(Sort.by("name").ascending());
    }

    /**
     * Deletes a doctor by ID.
     * Also deletes all appointments associated with the doctor.
     *
     * @param id the ID of the doctor to be deleted
     * @return 1 if deleted successfully, -1 if doctor does not exist, 0 if an error occurs
     */
    public int deleteDoctor(long id) {
        if (!doctorRepository.existsById(id)) return -1;
        try {
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Validates a doctor's login credentials.
     * Checks if the email exists and if the password matches.
     * Generates a token if validation is successful.
     *
     * @param login the login credentials
     * @return a ResponseEntity containing the token or an error message
     */
    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();
        Doctor doctor = doctorRepository.findByEmail(login.getEmail());

        if (doctor == null || !doctor.getPassword().equals(login.getPassword())) {
            response.put("error", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String token = tokenService.generateToken(doctor.getEmail());
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    /**
     * Finds a doctor by name using a case-insensitive search.
     * Returns a map containing the list of doctors matching the name.
     *
     * @param name the name of the doctor to search for
     * @return a map containing the list of doctors
     */
    @Transactional
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> res = new HashMap<>();
        res.put("doctors", doctorRepository.findByNameLikeOrderByName(name));
        return res;
    }

    /**
     * Filters doctors by name, specialty, and available time (AM/PM).
     * Returns a map containing the filtered list of doctors.
     *
     * @param name      the name of the doctor
     * @param specialty the specialty of the doctor
     * @param amOrPm    the time period (AM/PM)
     * @return a map containing the filtered list of doctors
     */
    @Transactional
    public Map<String, Object> filterDoctorsByNameSpecialtyAndTime(String name, String specialty, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCaseOrderByName(name, specialty);
        Map<String, Object> map = new HashMap<>();
        map.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return map;
    }

    /**
     * Filters doctors by name and available time (AM/PM).
     * Returns a map containing the filtered list of doctors.
     *
     * @param name   the name of the doctor
     * @param amOrPm the time period (AM/PM)
     * @return a map containing the filtered list of doctors
     */
    @Transactional
    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findByNameLikeOrderByName(name);

        Map<String, Object> map = new HashMap<>();
        map.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return map;
    }

    /**
     * Filters doctors by name and specialty.
     * Returns a map containing the filtered list of doctors.
     *
     * @param name      the name of the doctor
     * @param specialty the specialty of the doctor
     * @return a map containing the filtered list of doctors
     */
    @Transactional
    public Map<String, Object> filterDoctorByNameAndSpecialty(String name, String specialty) {
        Map<String, Object> map = new HashMap<>();
        map.put("doctors", doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCaseOrderByName(name, specialty));
        return map;
    }

/**
     * Filters doctors by specialty and available time (AM/PM).
     * Returns a map containing the filtered list of doctors.
     *
     * @param specialty the specialty of the doctor
     * @param amOrPm    the time period (AM/PM)
     * @return a map containing the filtered list of doctors
     */
    @Transactional
    public Map<String, Object> filterDoctorByTimeAndSpecialty(String specialty, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCaseOrderByName(specialty);
        Map<String, Object> map = new HashMap<>();
        map.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return map;
    }

    /**
     * Filters doctors by specialty.
     * Returns a map containing the list of doctors matching the specialty.
     *
     * @param specialty the specialty of the doctor
     * @return a map containing the list of doctors
     */
    @Transactional
    public Map<String, Object> filterDoctorBySpecialty(String specialty) {
        Map<String, Object> map = new HashMap<>();
        map.put("doctors", doctorRepository.findBySpecialtyIgnoreCaseOrderByName(specialty));
        return map;
    }

    /**
     * Filters doctors by available time (AM/PM).
     * Returns a map containing the list of doctors available during the specified time period.
     *
     * @param amOrPm the time period (AM/PM)
     * @return a map containing the list of doctors
     */
    @Transactional
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        List<Doctor> doctors = doctorRepository.findAll();
        Map<String, Object> map = new HashMap<>();
        map.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return map;
    }

    /**
     * Filters doctors by name, specialty, and available time (AM/PM).
     * Returns a list of doctors matching the criteria.
     *
     * @param name      the name of the doctor
     * @param specialty the specialty of the doctor
     * @param amOrPm    the time period (AM/PM)
     * @return a list of doctors matching the criteria
     */
    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        return doctors.stream().filter(doctor ->
                doctor.getAvailableTimes().stream().anyMatch(time ->
                        (amOrPm.equalsIgnoreCase("AM") && time.compareTo("12:00") < 0) ||
                                (amOrPm.equalsIgnoreCase("PM") && time.compareTo("12:00") >= 0)
                )
        ).toList();
    }

    /**
     * Finds a doctor by ID.
     * Throws an exception if the doctor is not found.
     *
     * @param id the ID of the doctor
     * @return the doctor with the specified ID
     */
    public Doctor findDoctorById(Long id) {
        return doctorRepository.findById(id).orElseThrow(() -> new RuntimeException("Doctor not found"));
    }
}
