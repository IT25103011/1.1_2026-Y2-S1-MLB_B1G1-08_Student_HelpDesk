package org.example.student_university_helpdesk.service;

import org.example.student_university_helpdesk.model.Users.User;
import org.example.student_university_helpdesk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ==========================================
    // CREATE & REGISTER OPERATIONS
    // ==========================================

    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }
        return userRepository.save(user);
    }

    // ==========================================
    // UPDATE OPERATIONS
    // ==========================================

    @Transactional
    public User updateUserAcademicInfo(Long userId, String department, String course) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        user.setDepartment(department);
        user.setCourse(course);

        return userRepository.save(user);
    }

    @Transactional
    public User updateUserProfile(Long userId, String firstName, String lastName, String email,
                                  String department, String course) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (!user.getEmail().equalsIgnoreCase(email) && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use: " + email);
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setDepartment(department);
        user.setCourse(course);

        return userRepository.save(user);
    }

    // ==========================================
    // QUERY OPERATIONS
    // ==========================================

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    @Transactional(readOnly = true)
    public List<User> getUsersByDepartment(String department) {
        return userRepository.findByDepartment(department);
    }

    @Transactional(readOnly = true)
    public List<User> getUsersByCourse(String course) {
        return userRepository.findByCourse(course);
    }

    @Transactional(readOnly = true)
    public List<User> getUsersByDepartmentAndCourse(String department, String course) {
        return userRepository.findByDepartmentAndCourse(department, course);
    }

    // ==========================================
    // DELETE OPERATIONS
    // ==========================================

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        userRepository.deleteById(userId);
    }
}