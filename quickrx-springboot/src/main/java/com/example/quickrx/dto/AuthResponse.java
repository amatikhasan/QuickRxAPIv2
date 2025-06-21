package com.example.quickrx.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private Object userDetails; // Changed to Object to hold either UserDetailsDto or AdminDetailsDto

    public AuthResponse(String token, Object userDetails) {
        this.token = token;
        this.userDetails = userDetails;
    }
}
