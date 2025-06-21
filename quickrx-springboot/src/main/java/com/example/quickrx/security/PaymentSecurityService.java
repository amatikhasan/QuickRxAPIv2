package com.example.quickrx.security;

import com.example.quickrx.model.Payment;
import com.example.quickrx.model.User;
import com.example.quickrx.repository.PaymentRepository;
import com.example.quickrx.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("paymentSecurityService")
public class PaymentSecurityService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Checks if the authenticated user is the owner of the payment record.
     */
    public boolean canAccessPayment(Authentication authentication, Long paymentId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails)) {
            return false; // Should not happen with standard Spring Security setup
        }
        String username = ((UserDetails) principal).getUsername(); // This is email for Users

        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isEmpty()) {
            return false; // Or throw AccessDenied if resource must exist
        }

        Payment payment = paymentOpt.get();
        if (payment.getUser() == null) {
            return false; // Payment not associated with a user
        }

        // Check if the authenticated user's email matches the payment owner's email
        return payment.getUser().getEmail().equals(username);
    }

    /**
     * Checks if the authenticated user's ID matches the provided userId.
     * Assumes username from UserDetails is the email.
     */
    public boolean isOwner(Authentication authentication, Long userIdToCheck) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails)) {
            return false;
        }
        String username = ((UserDetails) principal).getUsername(); // This is email for Users

        Optional<User> userOpt = userRepository.findByEmail(username);
        if(userOpt.isEmpty()){
            return false;
        }
        return userOpt.get().getId().equals(userIdToCheck);
    }
}
