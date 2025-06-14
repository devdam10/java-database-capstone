package com.project.back_end.DTO;


import com.project.back_end.models.Doctor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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

    public void copy(Doctor doctor) {
        this.id = doctor.getId();
        this.name = doctor.getName();
        this.specialty = doctor.getSpecialty();
        this.email = doctor.getEmail();
        this.phone = doctor.getPhone();
        this.availableTimes = doctor.getAvailableTimes(); // now safe to access
    }
}
