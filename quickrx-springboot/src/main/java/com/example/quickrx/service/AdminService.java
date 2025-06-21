package com.example.quickrx.service;

import com.example.quickrx.model.Admin;
import com.example.quickrx.repository.AdminRepository;
import com.example.quickrx.exception.ResourceNotFoundException; // Will create this
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void updatePassword(String username, String newPassword) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with username: " + username));

        admin.setPassword(passwordEncoder.encode(newPassword));
        adminRepository.save(admin);
    }

    // Add other admin-specific service methods here later
    // e.g., methods to manage users, categories, view feedback etc.
}
