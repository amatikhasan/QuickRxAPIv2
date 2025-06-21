package com.example.quickrx.repository;

import com.example.quickrx.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserId(Long userId);

    // To mimic: "SELECT payment.*,users.name AS user_name FROM payment LEFT JOIN users ON payment.user_id=users.id ORDER BY payment.id DESC"
    // Similar to Feedback, this can be handled by eager fetching or a DTO in the service layer.
    // For a direct query if needed:
    @Query("SELECT p FROM Payment p JOIN FETCH p.user ORDER BY p.id DESC")
    List<Payment> findAllWithUserOrderByDesc();
}
