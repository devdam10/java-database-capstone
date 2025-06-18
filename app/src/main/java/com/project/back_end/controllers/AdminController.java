package com.project.back_end.controllers;

import com.project.back_end.models.Admin;
import com.project.back_end.services.CentralService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}admin")
@RequiredArgsConstructor
public class AdminController {
    private final CentralService centralService;

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<Map<String, String>> adminLogin(@RequestBody Admin admin) {
        return centralService.validateAdmin(admin);
    }
}
