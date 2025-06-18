package com.project.back_end.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull(message = "Doctor is required!")
    private Doctor doctor;

    @ManyToOne
    @NotNull(message = "Patient is required!")
    private Patient patient;

    @Future(message = "Appointment time must be in the future")
    private LocalDateTime appointmentTime;

    @NotNull(message = "Status is required!")
    private int status;

    @Transient
    public LocalDateTime getEndTime() {
        return appointmentTime.plusHours(1);
    }

    @Transient
    public LocalDate getAppointmentDate(){
        return appointmentTime.toLocalDate();
    }

    @Transient
    public LocalTime getAppointmentTime(){
        return appointmentTime.toLocalTime();
    }
}

