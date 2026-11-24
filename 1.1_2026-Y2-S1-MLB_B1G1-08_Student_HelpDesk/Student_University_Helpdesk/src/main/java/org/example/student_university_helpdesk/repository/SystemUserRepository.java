package org.example.student_university_helpdesk.repository;

import org.example.student_university_helpdesk.model.Users.SystemUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemUserRepository extends JpaRepository<SystemUser, Long> {

    // Find user by email for authentication / login checks
    Optional<SystemUser> findByEmail(String email);

    // Find user by username
    Optional<SystemUser> findByUsername(String username);

    // Check if an email is already registered during signup
    boolean existsByEmail(String email);

    // Check if a username is already taken
    boolean existsByUsername(String username);
}
