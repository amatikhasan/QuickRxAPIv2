package com.example.quickrx.dto;

import com.example.quickrx.model.Payment;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponseDto {
    private Long id;
    private Long userId;
    private String userName; // For convenience
    private String userEmail; // For convenience
    private BigDecimal amount;
    private String method;
    private String accountNumber;
    private String transactionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PaymentResponseDto fromEntity(Payment paymentEntity) {
        if (paymentEntity == null) return null;

        PaymentResponseDto dto = new PaymentResponseDto();
        dto.setId(paymentEntity.getId());
        dto.setAmount(paymentEntity.getAmount());
        dto.setMethod(paymentEntity.getMethod());
        dto.setAccountNumber(paymentEntity.getAccountNumber());
        dto.setTransactionId(paymentEntity.getTransactionId());
        dto.setCreatedAt(paymentEntity.getCreatedAt());
        dto.setUpdatedAt(paymentEntity.getUpdatedAt());

        if (paymentEntity.getUser() != null) {
            dto.setUserId(paymentEntity.getUser().getId());
            dto.setUserName(paymentEntity.getUser().getName());
            dto.setUserEmail(paymentEntity.getUser().getEmail());
        }
        return dto;
    }
}
