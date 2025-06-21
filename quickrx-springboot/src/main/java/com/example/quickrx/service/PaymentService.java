package com.example.quickrx.service;

import com.example.quickrx.dto.PaymentRequestDto;
import com.example.quickrx.dto.PaymentResponseDto;
import com.example.quickrx.model.Payment;
import com.example.quickrx.model.User;
import com.example.quickrx.repository.PaymentRepository;
import com.example.quickrx.repository.UserRepository;
import com.example.quickrx.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto) {
        User user = userRepository.findById(paymentRequestDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + paymentRequestDto.getUserId()));

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setAmount(paymentRequestDto.getAmount());
        payment.setMethod(paymentRequestDto.getMethod());
        payment.setAccountNumber(paymentRequestDto.getAccountNumber());
        payment.setTransactionId(paymentRequestDto.getTransactionId());

        // In the old PHP code, there was an `updatePayment` that was also an insert.
        // This single `createPayment` should suffice for new payment records.
        // If actual updates to existing payments are needed, a separate method would be required.

        Payment savedPayment = paymentRepository.save(payment);

        // Potentially, after a successful payment, the user's account_status or account_valid_until
        // might need to be updated. This logic would go here if needed.
        // For example:
        // updateSubscriptionStatus(user, savedPayment);

        return PaymentResponseDto.fromEntity(savedPayment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getAllPayments() {
        // The PHP code joins with users table to get user_name.
        // The PaymentRepository's findAllWithUserOrderByDesc already handles this.
        return paymentRepository.findAllWithUserOrderByDesc().stream()
                .map(PaymentResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        return PaymentResponseDto.fromEntity(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return paymentRepository.findByUserId(userId).stream()
                .map(PaymentResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // If payments can be deleted (e.g., for administrative correction)
    @Transactional
    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Payment not found with id: " + id);
        }
        paymentRepository.deleteById(id);
    }

    // Example of updating user subscription status after payment
    // private void updateSubscriptionStatus(User user, Payment payment) {
    //     // Logic to determine new validity period based on payment amount/type
    //     // AppInfoService might be needed to get subscription_fee
    //     // LocalDateTime newValidity = user.getAccountValidUntil() != null ? user.getAccountValidUntil() : LocalDateTime.now();
    //     // newValidity = newValidity.plusMonths(1); // Example: +1 month
    //     // user.setAccountValidUntil(newValidity);
    //     // user.setAccountStatus(1); // Active
    //     // userRepository.save(user);
    // }
}
