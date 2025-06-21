package com.example.quickrx.dto;

import lombok.Data;
import java.util.Date;

@Data
public class UserDetailsDto {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private Date dob;
    private String regNumber;
    private String imageUrl;
    private Integer accountStatus;
    // Add other fields as necessary, but avoid sensitive ones like password
}
