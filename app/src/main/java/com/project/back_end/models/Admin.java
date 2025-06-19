package com.project.back_end.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an Admin entity in the healthcare system.
 * Contains details such as username and password.
 */

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Username is required!")
    private String username;

    @NotNull(message = "Password is required!")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
