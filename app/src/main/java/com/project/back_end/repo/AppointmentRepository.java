package com.project.back_end.repo;

import com.project.back_end.models.Appointment;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctorIdAndAppointmentTimeBetweenOrderByAppointmentTime(Long doctorId, LocalDateTime start, LocalDateTime end);
    List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetweenOrderByAppointmentTime(Long doctorId, String patientName, LocalDateTime start, LocalDateTime end);

    @Modifying
    @Transactional
    void deleteAllByDoctorId(Long doctorId);

    List<Appointment> findByPatientIdOrderByAppointmentTime(Long patientId);
    List<Appointment> findByPatient_IdAndStatusOrderByAppointmentTimeAsc(Long patientId, int status);

    @Query("SELECT a FROM Appointment a WHERE LOWER(a.doctor.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) AND a.patient.id = :patientId")
    List<Appointment> filterByDoctorNameContainingIgnoreCaseAndPatientIdOrderByAppointmentTime(@Param("doctorName") String doctorName, @Param("patientId") Long patientId);

    @Query("SELECT a FROM Appointment a WHERE LOWER(a.doctor.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) AND a.patient.id = :patientId AND a.status = :status")
    List<Appointment> filterByDoctorNameContainingIgnoreCaseAndPatientIdAndStatusOrderByAppointmentTime(@Param("doctorName") String doctorName, @Param("patientId") Long patientId, @Param("status") int status);

    // Update appointment status
    @Modifying
    @Transactional
    @Query("UPDATE Appointment a SET a.status = :status WHERE a.id = :id")
    void updateStatus(@Param("status") int status, @Param("id") Long id);

    List<Appointment> findByDoctorIdOrderByAppointmentTime(Long doctorId);
    List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseOrderByAppointmentTime(Long doctorId, String patientName);

    List<Appointment> findByDoctorIdAndPatientIdOrderByAppointmentTime(Long doctorId, Long patientId);
}
