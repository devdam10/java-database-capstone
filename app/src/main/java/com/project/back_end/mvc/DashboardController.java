package com.project.back_end.mvc;

import com.project.back_end.services.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for handling dashboard requests for different user roles.
 * Validates tokens and redirects to the appropriate dashboard based on the user's role.
 */

@Controller
@RequiredArgsConstructor
public class DashboardController {
    private final TokenService tokenService;

    /**
     * Redirects to the appropriate dashboard based on the user's role.
     * Validates the token and checks if the user is an admin or a doctor.
     *
     * @param token The token to validate.
     * @return The view name for the dashboard or a redirect to the home page if validation fails.
     */
    @GetMapping("/adminDashboard")
    public String adminDashboard(@RequestParam String token) {
        boolean isValid = tokenService.validateToken(token, "admin");

        return isValid ? "admin/adminDashboard" : "redirect:/";
    }

    /**
     * Redirects to the doctor's dashboard if the token is valid for a doctor.
     *
     * @param token The token to validate.
     * @return The view name for the doctor's dashboard or a redirect to the home page if validation fails.
     */
    @GetMapping("/doctorDashboard")
    public String doctorDashboard(@RequestParam String token) {
        boolean isValid = tokenService.validateToken(token, "doctor");

        return isValid ? "doctor/doctorDashboard" : "redirect:/";
    }
}
