package com.project.back_end.models;


import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Document(collection = "prescriptions")
public class Prescription {
    @Id
    private String id;

    @NotNull(message = "Patient name is required!")
    @Size(min = 3, max = 200)
    private String patientName;

    @Indexed(unique = true) // ensure one appointment per prescription
    @NotNull(message = "Appoint id is required!")
    private Long appointmentId;

    @NotNull(message = "Medication is required!")
    @Size(min = 3, max = 100)
    private String medication;

    @NotNull(message = "Dosage is required!")
    private String dosage;

    @Size(max = 200)
    private String doctorNotes;
}
