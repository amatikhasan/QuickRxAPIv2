package com.example.quickrx.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentRequestDto {

    @NotNull(message = "User ID cannot be null for payment")
    private Long userId; // In a real scenario, this might be inferred or validated against authenticated user

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "Payment method cannot be blank")
    private String method; // e.g., "BKash", "Nagad", "Card"

    @NotBlank(message = "Account number used for payment cannot be blank")
    private String accountNumber;

    private String transactionId; // Optional, can be null
}
