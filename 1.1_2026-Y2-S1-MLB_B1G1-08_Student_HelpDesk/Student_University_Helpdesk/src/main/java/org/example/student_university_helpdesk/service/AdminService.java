package org.example.student_university_helpdesk.service;

import org.example.student_university_helpdesk.model.Users.Admin;
import org.example.student_university_helpdesk.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    @Autowired
    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    // CREATE & REGISTER OPERATIONS

    @Transactional
    public Admin registerAdmin(Admin admin) {
        if (adminRepository.existsByEmail(admin.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + admin.getEmail());
        }
        return adminRepository.save(admin);
    }

    // ==========================================
    // UPDATE OPERATIONS
    // ==========================================

    @Transactional
    public Admin updateAccessLevel(Long adminId, String accessLevel) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found with ID: " + adminId));

        admin.setAccessLevel(accessLevel);
        return adminRepository.save(admin);
    }

    @Transactional
    public Admin updateAdminProfile(Long adminId, String firstName, String lastName,
                                    String email, String accessLevel) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found with ID: " + adminId));

        if (!admin.getEmail().equalsIgnoreCase(email) && adminRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use: " + email);
        }

        admin.setFirstName(firstName);
        admin.setLastName(lastName);
        admin.setEmail(email);
        admin.setAccessLevel(accessLevel);

        return adminRepository.save(admin);
    }

    // ==========================================
    // QUERY OPERATIONS
    // ==========================================

    @Transactional(readOnly = true)
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Admin getAdminById(Long adminId) {
        return adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found with ID: " + adminId));
    }

    @Transactional(readOnly = true)
    public Admin getAdminByEmail(String email) {
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found with email: " + email));
    }

    @Transactional(readOnly = true)
    public List<Admin> getAdminsByAccessLevel(String accessLevel) {
        return adminRepository.findByAccessLevel(accessLevel);
    }

    // ==========================================
    // DELETE OPERATIONS
    // ==========================================

    @Transactional
    public void deleteAdmin(Long adminId) {
        if (!adminRepository.existsById(adminId)) {
            throw new RuntimeException("Admin not found with ID: " + adminId);
        }
        adminRepository.deleteById(adminId);
    }
}