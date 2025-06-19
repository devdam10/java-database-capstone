package com.project.back_end.controllers;

import com.project.back_end.models.Admin;
import com.project.back_end.services.CentralService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for handling admin-related operations in the healthcare system.
 * Provides endpoints for admin login and validation.
 */
@RestController
@RequestMapping("${api.path}admin")
@RequiredArgsConstructor
public class AdminController {
    private final CentralService centralService;

    /**
     * Endpoint for admin login.
     * Validates the admin credentials and returns a response entity with a message.
     *
     * @param admin the admin credentials
     * @return ResponseEntity with a message indicating success or failure
     */
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<Map<String, String>> adminLogin(@RequestBody Admin admin) {
        return centralService.validateAdmin(admin);
    }
}
