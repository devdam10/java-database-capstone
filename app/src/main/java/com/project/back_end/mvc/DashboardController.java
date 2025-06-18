package com.project.back_end.mvc;

import com.project.back_end.services.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    private final TokenService tokenService;

    @GetMapping("/adminDashboard")
    public String adminDashboard(@RequestParam String token) {
        boolean isValid = tokenService.validateToken(token, "admin");

        return isValid ? "admin/adminDashboard" : "redirect:/";
    }

    @GetMapping("/doctorDashboard")
    public String doctorDashboard(@RequestParam String token) {
        boolean isValid = tokenService.validateToken(token, "doctor");

        return isValid ? "doctor/doctorDashboard" : "redirect:/";
    }
}
