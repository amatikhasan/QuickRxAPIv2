package com.example.quickrx.security;

import com.example.quickrx.model.Admin;
import com.example.quickrx.model.User;
import com.example.quickrx.repository.AdminRepository;
import com.example.quickrx.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmailOrPhone) throws UsernameNotFoundException {
        // Try loading as a regular user first
        Optional<User> userOptional = userRepository.findByEmail(usernameOrEmailOrPhone);
        if (!userOptional.isPresent()) {
            userOptional = userRepository.findByPhone(usernameOrEmailOrPhone);
        }

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Set<GrantedAuthority> authorities = new HashSet<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER")); // All registered users get ROLE_USER
            // Add other roles if you have a role system for users
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(), // Use email as the username for Spring Security context
                    user.getPassword(),
                    authorities);
        }

        // If not found as a regular user, try loading as an admin
        Optional<Admin> adminOptional = adminRepository.findByUsername(usernameOrEmailOrPhone);
        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();
            Set<GrantedAuthority> authorities = new HashSet<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            // Add other roles if you have a role system for admins
            return new org.springframework.security.core.userdetails.User(
                    admin.getUsername(),
                    admin.getPassword(),
                    authorities);
        }

        throw new UsernameNotFoundException("User not found with identifier: " + usernameOrEmailOrPhone);
    }

    // Used by JWTAuthenticationFilter
    @Transactional
    public UserDetails loadUserById(Long id, String userType) { // userType can be "user" or "admin"
        if ("user".equalsIgnoreCase(userType)) {
            User user = userRepository.findById(id).orElseThrow(
                    () -> new UsernameNotFoundException("User not found with id : " + id)
            );
            Set<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
        } else if ("admin".equalsIgnoreCase(userType)) {
            Admin admin = adminRepository.findById(id).orElseThrow(
                    () -> new UsernameNotFoundException("Admin not found with id : " + id)
            );
            Set<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"));
            return new org.springframework.security.core.userdetails.User(admin.getUsername(), admin.getPassword(), authorities);
        }
        throw new UsernameNotFoundException("Invalid user type specified for id : " + id);
    }
}
