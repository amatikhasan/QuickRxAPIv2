package com.example.quickrx.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class AppInfoUpdateRequestDto {

    @NotNull(message = "Subscription fee cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Subscription fee must be non-negative")
    private BigDecimal subscriptionFee;

    @NotBlank(message = "Account number cannot be blank")
    private String accountNumber;

    @NotBlank(message = "Hotline cannot be blank")
    private String hotline;

    private String facebookLink; // Optional

    @NotBlank(message = "About Us content cannot be blank")
    private String aboutUs;

    @NotBlank(message = "Terms and Conditions content cannot be blank")
    private String termsAndCondition;
}
