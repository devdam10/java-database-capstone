//package com.project.back_end.controllers;
//
//import com.project.back_end.services.TokenService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@Controller
//@RequestMapping("${api.path}dashboard")
//@RequiredArgsConstructor
//public class DashboardController {
//    private final TokenService tokenService;
//
//    @GetMapping("/adminDashboard")
//    public String getAdminDashboard(@RequestParam("token") String token) {
//        if (!tokenService.validateToken(token, "admin")) {
//            return "redirect:/";
//        }
//
//        return "admin/adminDashboard";
//    }
//}
