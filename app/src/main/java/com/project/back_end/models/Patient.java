package com.project.back_end.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name is required!")
    @Size(min = 3, max = 100)
    private String name;

    @NotNull(message = "Email is required!")
    @Email
    private String email;

    @NotNull(message = "Password is required!")
    @Size(min = 6)
    private String password;

    @NotNull(message = "Phone number is required!")
    @Pattern(regexp = "^[0-9]{10}$")
    private String phone;

    @NotNull(message = "Address is required!")
    @Size(max = 255)
    private String address;
}
