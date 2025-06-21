package com.example.quickrx.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.Date; // For dob, assuming it might be just a date

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String phone;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // Will be encoded by Spring Security

    @Column(name = "firebase_uid")
    private String firebaseUid;

    @Column(name = "unique_id")
    private String uniqueId;

    @Temporal(TemporalType.DATE) // Assuming dob is just a date
    private Date dob;

    @Column(name = "reg_number")
    private String regNumber;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "account_status")
    private Integer accountStatus; // 0: Inactive, 1: Active, etc. Or use an Enum

    @Column(name = "account_valid_from")
    private LocalDateTime accountValidFrom;

    @Column(name = "account_valid_until")
    private LocalDateTime accountValidUntil;

    // 'token' field is omitted as Spring Security will handle JWTs

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
