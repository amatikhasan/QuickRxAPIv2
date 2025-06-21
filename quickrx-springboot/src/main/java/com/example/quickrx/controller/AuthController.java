package com.example.quickrx.controller;

import com.example.quickrx.dto.LoginRequest;
import com.example.quickrx.dto.UserRegistrationRequest;
import com.example.quickrx.dto.AuthResponse;
import com.example.quickrx.dto.UserDetailsDto;
import com.example.quickrx.exception.ErrorDetails; // For documenting error responses
import com.example.quickrx.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "APIs for user registration and login")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "Register a new user", description = "Creates a new user account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                         content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDetailsDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data (e.g., validation error, email/phone already exists)",
                         content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))), // Assuming ErrorDetails from GlobalExceptionHandler
            @ApiResponse(responseCode = "409", description = "Conflict - Email or Phone already exists",
                         content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
    })
    @PostMapping("/register/user")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationRequest registrationRequest) {
        // GlobalExceptionHandler will handle DuplicateResourceException (409) and MethodArgumentNotValidException (400)
        UserDetailsDto registeredUser = authService.registerUser(registrationRequest);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @Operation(summary = "Authenticate user or admin", description = "Logs in a user or admin and returns a JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful",
                         content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed (invalid credentials)",
                         content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@Valid @RequestBody LoginRequest loginRequest) {
        // Specific AuthenticationException is caught here for a tailored message,
        // but GlobalExceptionHandler could also handle it.
        try {
            AuthResponse authResponse = authService.login(loginRequest);
            return ResponseEntity.ok(authResponse);
        } catch (AuthenticationException e) {
             return new ResponseEntity<>(
                 new ErrorDetails(new java.util.Date(), "Login failed: Invalid credentials.", "uri=/api/auth/login"), // Example ErrorDetails
                 HttpStatus.UNAUTHORIZED
             );
        }
        // Other exceptions will be caught by GlobalExceptionHandler
    }

    // If Admins have a separate registration flow, it would go here.
    // For now, assuming Admins are pre-registered or managed via a different mechanism.
}
