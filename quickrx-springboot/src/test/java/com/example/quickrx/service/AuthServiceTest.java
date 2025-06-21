package com.example.quickrx.service;

import com.example.quickrx.dto.UserDetailsDto;
import com.example.quickrx.dto.UserRegistrationRequest;
import com.example.quickrx.exception.DuplicateResourceException;
import com.example.quickrx.model.User;
import com.example.quickrx.repository.AdminRepository;
import com.example.quickrx.repository.UserRepository;
import com.example.quickrx.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager; // Needed for login, not registerUser

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdminRepository adminRepository; // Needed for login, not registerUser

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider; // Needed for login, not registerUser

    @InjectMocks
    private AuthService authService;

    private UserRegistrationRequest registrationRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registrationRequest = new UserRegistrationRequest();
        registrationRequest.setName("Test User");
        registrationRequest.setEmail("test@example.com");
        registrationRequest.setPhone("1234567890");
        registrationRequest.setPassword("password123");
        registrationRequest.setDob(new Date(System.currentTimeMillis() - 1000000000)); // Past date
        registrationRequest.setRegNumber("REG123");

        user = new User();
        user.setId(1L);
        user.setName(registrationRequest.getName());
        user.setEmail(registrationRequest.getEmail());
        user.setPhone(registrationRequest.getPhone());
        user.setDob(registrationRequest.getDob());
        user.setRegNumber(registrationRequest.getRegNumber());
        user.setAccountStatus(1);
    }

    @Test
    void registerUser_success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhone(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L); // Simulate saving and getting an ID
            return savedUser;
        });

        UserDetailsDto result = authService.registerUser(registrationRequest);

        assertNotNull(result);
        assertEquals(registrationRequest.getName(), result.getName());
        assertEquals(registrationRequest.getEmail(), result.getEmail());
        verify(userRepository, times(1)).existsByEmail(registrationRequest.getEmail());
        verify(userRepository, times(1)).existsByPhone(registrationRequest.getPhone());
        verify(passwordEncoder, times(1)).encode(registrationRequest.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_emailExists_throwsDuplicateResourceException() {
        when(userRepository.existsByEmail(registrationRequest.getEmail())).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            authService.registerUser(registrationRequest);
        });

        assertEquals("Email address already in use.", exception.getMessage());
        verify(userRepository, times(1)).existsByEmail(registrationRequest.getEmail());
        verify(userRepository, never()).existsByPhone(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_phoneExists_throwsDuplicateResourceException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhone(registrationRequest.getPhone())).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            authService.registerUser(registrationRequest);
        });

        assertEquals("Phone number already in use.", exception.getMessage());
        verify(userRepository, times(1)).existsByEmail(registrationRequest.getEmail());
        verify(userRepository, times(1)).existsByPhone(registrationRequest.getPhone());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}
