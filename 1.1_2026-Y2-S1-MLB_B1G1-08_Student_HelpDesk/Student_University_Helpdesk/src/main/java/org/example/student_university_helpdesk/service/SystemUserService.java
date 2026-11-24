package org.example.student_university_helpdesk.service;

import org.example.student_university_helpdesk.model.Users.Admin;
import org.example.student_university_helpdesk.model.Users.HelpdeskOfficer;
import org.example.student_university_helpdesk.model.Users.SystemUser;
import org.example.student_university_helpdesk.model.Users.User;
import org.example.student_university_helpdesk.repository.SystemUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SystemUserService {

    private final SystemUserRepository systemUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SystemUserService(SystemUserRepository systemUserRepository, PasswordEncoder passwordEncoder) {
        this.systemUserRepository = systemUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ==========================================
    // REGISTRATION OPERATIONS
    // ==========================================

    @Transactional
    public SystemUser registerUser(SystemUser user) {
        if (systemUserRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email is already registered: " + user.getEmail());
        }

        if (systemUserRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username is already taken: " + user.getUsername());
        }

        // Encrypt plain text password prior to database persistence
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Apply fallback defaults for required non-nullable fields on subclasses if instantiated generically
        if (user instanceof Admin admin && (admin.getAccessLevel() == null || admin.getAccessLevel().isBlank())) {
            admin.setAccessLevel("FULL_ACCESS");
        } else if (user instanceof User student) {
            if (student.getDepartment() == null) student.setDepartment("GENERAL");
            if (student.getCourse() == null) student.setCourse("GENERAL");
        } else if (user instanceof HelpdeskOfficer officer) {
            if (officer.getDepartment() == null) officer.setDepartment("GENERAL");
        }

        return systemUserRepository.save(user);
    }

    // ==========================================
    // PROFILE MANAGEMENT
    // ==========================================

    @Transactional
    public SystemUser updateProfile(Long userId, String firstName, String lastName, String email) {
        SystemUser user = systemUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (!user.getEmail().equalsIgnoreCase(email) && systemUserRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use: " + email);
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);

        return systemUserRepository.save(user);
    }

    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        SystemUser user = systemUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Verify old raw password against BCrypt hash in DB
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new SecurityException("Incorrect current password.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        systemUserRepository.save(user);
    }

    // ==========================================
    // QUERY & ADMIN OPERATIONS
    // ==========================================

    @Transactional(readOnly = true)
    public List<SystemUser> getAllUsers() {
        return systemUserRepository.findAll();
    }

    @Transactional(readOnly = true)
    public SystemUser getUserById(Long userId) {
        return systemUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    @Transactional(readOnly = true)
    public Optional<SystemUser> getUserByEmail(String email) {
        return systemUserRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<SystemUser> getUserByUsername(String username) {
        return systemUserRepository.findByUsername(username);
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!systemUserRepository.existsById(userId)) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        systemUserRepository.deleteById(userId);
    }
}