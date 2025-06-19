package com.project.back_end.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


/**
 * Represents an Appointment Data Transfer Object (DTO) in the healthcare system.
 * Contains details such as doctor, patient, appointment time, and status.
 * Provides custom getters for appointment date, time, and end time.
 */

@Getter
@Setter
@NoArgsConstructor
public class AppointmentDTO {
    private Long id;
    private Long doctorId;
    private String doctorName;

    private Long patientId;
    private String patientName;
    private String patientEmail;
    private String patientPhone;
    private String patientAddress;

    private LocalDateTime appointmentTime;
    private int status;

    /**
     * Returns the appointment date as a LocalDate.
     * If appointmentTime is null, returns null.
     *
     * @return LocalDate representing the appointment date
     */
    public LocalDate getAppointmentDate() {
        return appointmentTime != null ? appointmentTime.toLocalDate() : null;
    }

    /**
     * Returns the appointment time as a LocalTime.
     * If appointmentTime is null, returns null.
     *
     * @return LocalTime representing the appointment time
     */
    public LocalTime getAppointmentTimeOnly() {
        return appointmentTime != null ? appointmentTime.toLocalTime() : null;
    }

    /**
     * Returns the end time of the appointment, which is one hour after the appointmentTime.
     * If appointmentTime is null, returns null.
     *
     * @return LocalDateTime representing the end time of the appointment
     */
    public LocalDateTime getEndTime() {
        return appointmentTime != null ? appointmentTime.plusHours(1) : null;
    }
}
