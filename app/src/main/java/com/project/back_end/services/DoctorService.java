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

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    @Transactional
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);

        if (doctorOpt.isEmpty()) return Collections.emptyList();

        List<String> allSlots = new ArrayList<>(doctorOpt.get().getAvailableTimes());

        // List<String> bookedSlots = appointmentRepository.findByDoctorIdAndAppointmentTimeBetweenOrderByAppointmentTime(
        //         doctorId,
        //         date.atStartOfDay(),
        //         date.atTime(23, 59)
        // ).stream().map(appointment -> {
        //     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        //     return appointment.getAppointmentTime().format(formatter) + "-" + appointment.getEndTime().format(formatter);
        // }).toList();

        // Filter out booked slots from all available slots and update doctor's available times
        // if (!bookedSlots.isEmpty()){
        //     doctorOpt.get().setAvailableTimes(
        //         allSlots.stream()
        //                 .filter(slot -> !bookedSlots.contains(slot))
        //                 .collect(Collectors.toList())
        //     );

        //     doctorRepository.save(doctorOpt.get());
        // };

        // return allSlots.stream().filter(slot -> !bookedSlots.contains(slot)).collect(Collectors.toList());

        return allSlots;
    }

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

    public int updateDoctor(Doctor doctor) {
        if (!doctorRepository.existsById(doctor.getId())) return -1;
        try {
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public List<Doctor> getDoctors() {
        // return doctorRepository.findAll();
        return doctorRepository.findAll(Sort.by("name").ascending());
    }

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

    @Transactional
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> res = new HashMap<>();
        res.put("doctors", doctorRepository.findByNameLikeOrderByName(name));
        return res;
    }

    @Transactional
    public Map<String, Object> filterDoctorsByNameSpecialityAndTime(String name, String specialty, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCaseOrderByName(name, specialty);
        Map<String, Object> map = new HashMap<>();
        map.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return map;
    }

    @Transactional
    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findByNameLikeOrderByName(name);

        Map<String, Object> map = new HashMap<>();
        map.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return map;
    }

    @Transactional
    public Map<String, Object> filterDoctorByNameAndSpeciality(String name, String specialty) {
        Map<String, Object> map = new HashMap<>();
        map.put("doctors", doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCaseOrderByName(name, specialty));
        return map;
    }

    @Transactional
    public Map<String, Object> filterDoctorByTimeAndSpeciality(String specialty, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCaseOrderByName(specialty);
        Map<String, Object> map = new HashMap<>();
        map.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return map;
    }

    @Transactional
    public Map<String, Object> filterDoctorBySpeciality(String specialty) {
        Map<String, Object> map = new HashMap<>();
        map.put("doctors", doctorRepository.findBySpecialtyIgnoreCaseOrderByName(specialty));
        return map;
    }

    @Transactional
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        List<Doctor> doctors = doctorRepository.findAll();
        Map<String, Object> map = new HashMap<>();
        map.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return map;
    }

    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        return doctors.stream().filter(doctor ->
                doctor.getAvailableTimes().stream().anyMatch(time ->
                        (amOrPm.equalsIgnoreCase("AM") && time.compareTo("12:00") < 0) ||
                                (amOrPm.equalsIgnoreCase("PM") && time.compareTo("12:00") >= 0)
                )
        ).toList();
    }

    public Doctor findDoctorById(Long id) {
        return doctorRepository.findById(id).orElseThrow(() -> new RuntimeException("Doctor not found"));
    }
}
