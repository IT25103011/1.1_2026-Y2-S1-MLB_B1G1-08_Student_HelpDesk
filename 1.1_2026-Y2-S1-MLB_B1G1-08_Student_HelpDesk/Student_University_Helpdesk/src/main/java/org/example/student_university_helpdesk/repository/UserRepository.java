package org.example.student_university_helpdesk.repository;

import org.example.student_university_helpdesk.model.Users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Find student by email (inherited from SystemUser)
    Optional<User> findByEmail(String email);

    // Find all users belonging to a specific department
    List<User> findByDepartment(String department);

    // Find all users enrolled in a specific course
    List<User> findByCourse(String course);

    // Find users by department and course combined
    List<User> findByDepartmentAndCourse(String department, String course);

    // Check if a student email exists
    boolean existsByEmail(String email);
}