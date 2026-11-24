package org.example.student_university_helpdesk.service;

import org.example.student_university_helpdesk.model.Users.HelpdeskOfficer;
import org.example.student_university_helpdesk.repository.HelpdeskOfficerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HelpdeskOfficerService {

    private final HelpdeskOfficerRepository helpdeskOfficerRepository;

    @Autowired
    public HelpdeskOfficerService(HelpdeskOfficerRepository helpdeskOfficerRepository) {
        this.helpdeskOfficerRepository = helpdeskOfficerRepository;
    }

    // CREATE & REGISTER OPERATIONS

    @Transactional
    public HelpdeskOfficer registerOfficer(HelpdeskOfficer officer) {
        if (helpdeskOfficerRepository.existsByEmail(officer.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + officer.getEmail());
        }
        return helpdeskOfficerRepository.save(officer);
    }

    // ==========================================
    // UPDATE OPERATIONS
    // ==========================================

    @Transactional
    public HelpdeskOfficer updateOfficerDepartment(Long officerId, String department) {
        HelpdeskOfficer officer = helpdeskOfficerRepository.findById(officerId)
                .orElseThrow(() -> new RuntimeException("Helpdesk Officer not found with ID: " + officerId));

        officer.setDepartment(department);
        return helpdeskOfficerRepository.save(officer);
    }

    @Transactional
    public HelpdeskOfficer updateOfficerProfile(Long officerId, String firstName, String lastName,
                                                String email, String department) {
        HelpdeskOfficer officer = helpdeskOfficerRepository.findById(officerId)
                .orElseThrow(() -> new RuntimeException("Helpdesk Officer not found with ID: " + officerId));

        if (!officer.getEmail().equalsIgnoreCase(email) && helpdeskOfficerRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use: " + email);
        }

        officer.setFirstName(firstName);
        officer.setLastName(lastName);
        officer.setEmail(email);
        officer.setDepartment(department);

        return helpdeskOfficerRepository.save(officer);
    }

    // ==========================================
    // QUERY OPERATIONS
    // ==========================================

    @Transactional(readOnly = true)
    public List<HelpdeskOfficer> getAllOfficers() {
        return helpdeskOfficerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public HelpdeskOfficer getOfficerById(Long officerId) {
        return helpdeskOfficerRepository.findById(officerId)
                .orElseThrow(() -> new RuntimeException("Helpdesk Officer not found with ID: " + officerId));
    }

    @Transactional(readOnly = true)
    public HelpdeskOfficer getOfficerByEmail(String email) {
        return helpdeskOfficerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Helpdesk Officer not found with email: " + email));
    }

    @Transactional(readOnly = true)
    public List<HelpdeskOfficer> getOfficersByDepartment(String department) {
        return helpdeskOfficerRepository.findByDepartment(department);
    }

    // ==========================================
    // DELETE OPERATIONS
    // ==========================================

    @Transactional
    public void deleteOfficer(Long officerId) {
        if (!helpdeskOfficerRepository.existsById(officerId)) {
            throw new RuntimeException("Helpdesk Officer not found with ID: " + officerId);
        }
        helpdeskOfficerRepository.deleteById(officerId);
    }
}