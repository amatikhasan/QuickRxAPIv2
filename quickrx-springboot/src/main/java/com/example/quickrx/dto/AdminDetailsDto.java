package com.example.quickrx.dto;

import lombok.Data;

@Data
public class AdminDetailsDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    // Add other relevant non-sensitive admin fields if any
}
