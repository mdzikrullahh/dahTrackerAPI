package com.dahtracker.api.service;

import com.dahtracker.api.model.User;

import java.util.Optional;

public interface UserService {
    User createUser(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    User updateUser(User user);
    void deleteUser(Long id);
    void changePassword(User user, String newPassword);
    boolean checkPassword(User user, String currentPassword);

    // 22-2-2025: change login signup feature - Find user by reset token
    Optional<User> findByResetToken(String resetToken);
}
