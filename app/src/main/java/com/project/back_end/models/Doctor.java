package com.project.back_end.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name is required!")
    @Size(min = 3, max = 100)
    private String name;

    @NotNull(message = "Specialty is required!")
    @Size(min = 3, max = 50)
    private String specialty;

    @NotNull(message = "Email is required!")
    @Email
    private String email;

    @NotNull(message = "Password is required!")
    @Size(min = 6)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotNull(message = "Phone number is required!")
    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits!")
    private String phone;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> availableTimes;
}

