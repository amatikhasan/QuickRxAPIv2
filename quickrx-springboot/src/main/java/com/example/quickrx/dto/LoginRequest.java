package com.example.quickrx.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Email or Phone cannot be blank")
    private String emailOrPhone;

    @NotBlank(message = "Password cannot be blank")
    private String password;
}
