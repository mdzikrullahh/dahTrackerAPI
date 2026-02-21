package com.dahtracker.api.service;

// 22-2-2025: change login signup feature - Email service interface

public interface EmailService {

    /**
     * Send verification PIN email to user
     * @param to recipient email address
     * @param name recipient name
     * @param pin 6-digit verification PIN
     */
    void sendVerificationEmail(String to, String name, String pin);

    /**
     * Send password reset email with reset link
     * @param to recipient email address
     * @param name recipient name
     * @param resetToken password reset token
     */
    void sendPasswordResetEmail(String to, String name, String resetToken);
}
