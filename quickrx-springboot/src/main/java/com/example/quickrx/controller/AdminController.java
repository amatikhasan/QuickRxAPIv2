package com.example.quickrx.controller;

import com.example.quickrx.dto.PasswordUpdateRequest; // Will create this DTO
import com.example.quickrx.service.AdminService; // Will create this service
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')") // Secure all endpoints in this controller for ADMIN role
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/password/update")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody PasswordUpdateRequest passwordUpdateRequest, Authentication authentication) {
        // The 'username' from authentication.getName() is the admin's username
        String adminUsername = authentication.getName();
        adminService.updatePassword(adminUsername, passwordUpdateRequest.getNewPassword());
        return ResponseEntity.ok("Password updated successfully for admin: " + adminUsername);
    }

    // Placeholder for other admin functionalities:
    // - Get all users
    // - Update user account status
    // - Manage categories
    // - Manage diseases
    // - Manage app info
    // - View feedback
    // - View payments
}
