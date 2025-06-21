package com.example.quickrx.service;

import com.example.quickrx.dto.*;
import com.example.quickrx.model.Admin;
import com.example.quickrx.model.User;
import com.example.quickrx.repository.AdminRepository;
import com.example.quickrx.repository.UserRepository;
import com.example.quickrx.security.JwtTokenProvider;
import com.example.quickrx.exception.DuplicateResourceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;


@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Transactional
    public UserDetailsDto registerUser(UserRegistrationRequest registrationRequest) {
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new DuplicateResourceException("Email address already in use.");
        }

        if (userRepository.existsByPhone(registrationRequest.getPhone())) {
            throw new DuplicateResourceException("Phone number already in use.");
        }

        User user = new User();
        user.setName(registrationRequest.getName());
        user.setEmail(registrationRequest.getEmail());
        user.setPhone(registrationRequest.getPhone());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setDob(registrationRequest.getDob());
        user.setRegNumber(registrationRequest.getRegNumber());
        user.setAccountStatus(1); // Default to active

        User savedUser = userRepository.save(user);

        return mapUserToUserDetailsDto(savedUser);
    }

    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmailOrPhone(), // This is treated as 'username' by Spring Security
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        // Determine if the authenticated principal is a User or Admin
        String authenticatedUsername = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean isAdmin = authorities.stream()
                                 .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            Admin admin = adminRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new RuntimeException("Admin not found after authentication. Critical error."));
            // In AuthResponse, we need a way to pass generic user details or specific ones.
            // For now, let's adapt UserDetailsDto or create a more generic one.
            // Or, AuthResponse can hold Object for userDetails.
            // For simplicity, let's use AdminDetailsDto here and modify AuthResponse or use a common DTO.
            // This example assumes AuthResponse is flexible or we create AdminAuthResponse.
            // I will modify AuthResponse to take a generic userDetails object.
            return new AuthResponse(jwt, mapAdminToAdminDetailsDto(admin));
        } else {
            User user = userRepository.findByEmailOrPhone(authenticatedUsername, authenticatedUsername)
                .orElseThrow(() -> new RuntimeException("User not found after authentication. Critical error."));
            return new AuthResponse(jwt, mapUserToUserDetailsDto(user));
        }
    }

    private UserDetailsDto mapUserToUserDetailsDto(User user) {
        UserDetailsDto dto = new UserDetailsDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setDob(user.getDob());
        dto.setRegNumber(user.getRegNumber());
        dto.setImageUrl(user.getImageUrl());
        dto.setAccountStatus(user.getAccountStatus());
        return dto;
    }

    private AdminDetailsDto mapAdminToAdminDetailsDto(Admin admin) {
        AdminDetailsDto dto = new AdminDetailsDto();
        dto.setId(admin.getId());
        dto.setUsername(admin.getUsername());
        dto.setEmail(admin.getEmail());
        dto.setPhone(admin.getPhone());
        return dto;
    }
}
