package com.project.back_end.mvc;

import com.project.back_end.services.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    private final TokenService tokenService;

    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {
        boolean isValid = tokenService.validateToken(token, "admin");

        if (isValid) {
            return "admin/adminDashboard"; // Thymeleaf template view
        }
        else {
            return "redirect:/"; // Redirect to login or home
        }
    }

    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {
        boolean isValid = tokenService.validateToken(token, "doctor");

        if (isValid) {
            return "doctor/doctorDashboard";
        }
        else {
            return "redirect:/";
        }
    }
}
