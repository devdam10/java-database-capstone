package com.project.back_end.services;

import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Service class for managing JWT tokens in the healthcare system.
 * Provides methods to generate, validate, and extract information from JWT tokens.
 */
@Component
@RequiredArgsConstructor
public class TokenService {
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Retrieves the signing key for JWT using the secret from configuration.
     * The secret is expected to be a base64-encoded string.
     * @return
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a JWT token for the given email.
     * The token is valid for 7 days.
     *
     * @param email the email to include in the token
     * @return a JWT token as a String
     */
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L)) // 7 days
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the email from the JWT token.
     *
     * @param token the JWT token
     * @return the email if the token is valid, null otherwise
     */
    public String extractEmail(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            return null; // Token is invalid or malformed
        }
    }

    /**
     * Validates the JWT token for the specified role.
     * Checks if the user exists in the corresponding repository based on the role.
     *
     * @param token the JWT token
     * @param role  the role of the user (admin, doctor, patient)
     * @return true if the token is valid and the user exists, false otherwise
     */
    public boolean validateToken(String token, String role) {
        try {
            String email = extractEmail(token);
            if (email == null) return false;

            return switch (role.toLowerCase()) {
                case "admin" -> adminRepository.existsByUsername(email);
                case "doctor" -> doctorRepository.existsByEmail(email);
                case "patient" -> patientRepository.existsByEmail(email);
                default -> false;
            };
        } catch (Exception e) {
            return false;
        }
    }
}
