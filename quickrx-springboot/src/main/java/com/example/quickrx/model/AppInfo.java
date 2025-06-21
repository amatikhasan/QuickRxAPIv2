package com.example.quickrx.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "app_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Assuming a single row, but an ID is good practice

    @Column(name = "subscription_fee", precision = 10, scale = 2)
    private BigDecimal subscriptionFee;

    @Column(name = "account_number")
    private String accountNumber;

    private String hotline;

    @Column(name = "facebook_link")
    private String facebookLink;

    @Lob
    @Column(name = "about_us")
    private String aboutUs;

    @Lob
    @Column(name = "terms_and_condition")
    private String termsAndCondition;

    // created_at is likely static for this table if it's seeded once
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
