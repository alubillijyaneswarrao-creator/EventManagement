package com.college.eventmanagement.controller;

import com.college.eventmanagement.dto.AdminDtos;
import com.college.eventmanagement.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/metrics/{eventId}")
    public ResponseEntity<AdminDtos.EventMetricsResponse> metrics(@PathVariable Long eventId) {
        return ResponseEntity.ok(adminService.metrics(eventId));
    }

    @GetMapping("/metrics")
    public ResponseEntity<List<AdminDtos.EventMetricsResponse>> all() {
        return ResponseEntity.ok(adminService.allMetrics());
    }

    @PostMapping("/bulk-email")
    public ResponseEntity<Void> bulkEmail(@Valid @RequestBody AdminDtos.BulkEmailRequest request) {
        adminService.sendBulkEmail(request);
        return ResponseEntity.accepted().build();
    }
}
