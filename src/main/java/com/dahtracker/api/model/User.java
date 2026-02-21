package com.dahtracker.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(nullable = false)
    private Boolean enabled = false;

    // 22-2-2025: change login signup feature - Email verification fields
    @Column(nullable = false)
    private Boolean verified = false;

    private String verificationPin;

    private LocalDateTime pinExpiry;

    // 22-2-2025: change login signup feature - Password reset fields
    private String resetToken;

    private LocalDateTime resetTokenExpiry;

    // 22-2-2025: change login signup feature - OAuth2 fields (for Google login)
    private String provider;

    private String providerId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}