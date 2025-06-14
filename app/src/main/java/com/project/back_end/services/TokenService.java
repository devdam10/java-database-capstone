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

@Component
@RequiredArgsConstructor
public class TokenService {
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    // Retrieves the signing key for JWT using the secret from configuration
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // Generates a JWT token with email as the subject, valid for 7 days
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L)) // 7 days
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extracts the email (subject) from a JWT token
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

    // Validates the token based on user role (admin, doctor, patient)
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
