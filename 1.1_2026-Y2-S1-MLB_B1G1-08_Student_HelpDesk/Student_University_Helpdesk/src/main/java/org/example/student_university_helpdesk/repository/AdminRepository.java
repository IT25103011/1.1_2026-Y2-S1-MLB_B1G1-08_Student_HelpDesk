package org.example.student_university_helpdesk.repository;

import org.example.student_university_helpdesk.model.Users.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    // Find admin by email (inherited from SystemUser)
    Optional<Admin> findByEmail(String email);

    // Find admin by username (inherited from SystemUser)
    Optional<Admin> findByUsername(String username);

    // Find all admins with a specific access level (e.g., "SUPER_ADMIN", "DEPT_ADMIN")
    List<Admin> findByAccessLevel(String accessLevel);

    // Check if an admin exists with a given email
    boolean existsByEmail(String email);
}