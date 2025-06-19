package com.project.back_end.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a Login Data Transfer Object (DTO) in the healthcare system.
 * Contains user credentials such as email and password.
 */

@Getter
@Setter
@NoArgsConstructor
public class Login {
    private String email;
    private String password;
}

