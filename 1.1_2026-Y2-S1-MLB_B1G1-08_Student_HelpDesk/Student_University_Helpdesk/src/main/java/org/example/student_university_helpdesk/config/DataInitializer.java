package org.example.student_university_helpdesk.config;

import org.example.student_university_helpdesk.model.ResourceManagement.Resources;
import org.example.student_university_helpdesk.model.Users.Admin;
import org.example.student_university_helpdesk.model.Users.HelpdeskOfficer;
import org.example.student_university_helpdesk.model.Users.SystemUser;
import org.example.student_university_helpdesk.model.Users.User;
import org.example.student_university_helpdesk.repository.SystemUserRepository;
import org.example.student_university_helpdesk.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final SystemUserRepository systemUserRepository;
    private final SystemUserService systemUserService;

    @Autowired
    public DataInitializer(SystemUserRepository systemUserRepository,
                           SystemUserService systemUserService) {
        this.systemUserRepository = systemUserRepository;
        this.systemUserService = systemUserService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (systemUserRepository.count() == 0) {
            logger.info("Database is empty. Seeding initial accounts and sample data...");

            // 1. Seed Admin User
            Admin admin = new Admin(
                    "admin",
                    "admin123",
                    "admin@university.edu",
                    "System",
                    "Administrator",
                    "ROLE_ADMIN",
                    "SUPER_ADMIN"
            );
            admin = (Admin) systemUserService.registerUser(admin);
            logger.info("Seeded Admin Account: admin / admin123 (ID: {})", admin.getUserId());

            // 2. Seed Helpdesk Officer User
            HelpdeskOfficer officer = new HelpdeskOfficer(
                    "officer",
                    "officer123",
                    "officer@university.edu",
                    "Sarah",
                    "Jenkins",
                    "ROLE_HELPDESK_OFFICER",
                    "IT & Campus Operations"
            );
            officer = (HelpdeskOfficer) systemUserService.registerUser(officer);
            logger.info("Seeded Officer Account: officer / officer123 (ID: {})", officer.getUserId());

            // 3. Seed Student User
            User student = new User(
                    "student",
                    "student123",
                    "student@university.edu",
                    "Janindu",
                    "Bandara",
                    "ROLE_USER",
                    "Faculty of Computing",
                    "BSc Software Engineering"
            );
            student = (User) systemUserService.registerUser(student);
            logger.info("Seeded Student Account: student / student123 (ID: {})", student.getUserId());

            logger.info("Initial role accounts seeded. No mock entries added.");
        }
    }
}
