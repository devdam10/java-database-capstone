package com.project.back_end.DTO;


import com.project.back_end.models.Doctor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Represents a Doctor Data Transfer Object (DTO) in the healthcare system.
 * Contains details such as name, specialty, email, phone number, and available times.
 */

@Getter
@Setter
@NoArgsConstructor
public class DoctorDTO {
    private Long id;
    private String name;
    private String specialty;
    private String email;
    private String phone;
    private List<String> availableTimes;

    /**
     * Copies the properties from a Doctor entity to this DTO.
     * This method is used to convert a Doctor entity into a DoctorDTO.
     * @param doctor
     */
    public void copy(Doctor doctor) {
        this.id = doctor.getId();
        this.name = doctor.getName();
        this.specialty = doctor.getSpecialty();
        this.email = doctor.getEmail();
        this.phone = doctor.getPhone();
        this.availableTimes = doctor.getAvailableTimes(); // now safe to access
    }
}
