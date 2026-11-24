package org.example.student_university_helpdesk.repository;

import org.example.student_university_helpdesk.model.Users.HelpdeskOfficer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HelpdeskOfficerRepository extends JpaRepository<HelpdeskOfficer, Long> {

    Optional<HelpdeskOfficer> findByEmail(String email);

    Optional<HelpdeskOfficer> findByUsername(String username);

    List<HelpdeskOfficer> findByDepartment(String department);

    boolean existsByEmail(String email);
}