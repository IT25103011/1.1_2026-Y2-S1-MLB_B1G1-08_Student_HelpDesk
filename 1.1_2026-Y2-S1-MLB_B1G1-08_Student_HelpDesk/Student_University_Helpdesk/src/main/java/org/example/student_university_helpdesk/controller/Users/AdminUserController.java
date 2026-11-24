package org.example.student_university_helpdesk.controller.Users;

import org.example.student_university_helpdesk.model.Users.Admin;
import org.example.student_university_helpdesk.model.Users.HelpdeskOfficer;
import org.example.student_university_helpdesk.model.Users.SystemUser;
import org.example.student_university_helpdesk.model.Users.User;
import org.example.student_university_helpdesk.service.SystemUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final SystemUserService systemUserService;

    @Autowired
    public AdminUserController(SystemUserService systemUserService) {
        this.systemUserService = systemUserService;
    }

    @GetMapping("")
    public String showUsers() {
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/create")
    public String createUser(@RequestParam String username,
                             @RequestParam String password,
                             @RequestParam String email,
                             @RequestParam String firstName,
                             @RequestParam String lastName,
                             @RequestParam String role,
                             @RequestParam(required = false) String department,
                             @RequestParam(required = false) String course,
                             @RequestParam(required = false) String accessLevel,
                             RedirectAttributes redirectAttributes) {
        try {
            SystemUser newUser;
            String cleanRole = role.trim().toUpperCase();

            if ("ADMIN".equals(cleanRole)) {
                String level = (accessLevel != null && !accessLevel.isBlank()) ? accessLevel : "FULL_ACCESS";
                newUser = new Admin(username, password, email, firstName, lastName, "ROLE_ADMIN", level);
            } else if ("OFFICER".equals(cleanRole) || "HELPDESK_OFFICER".equals(cleanRole)) {
                String dept = (department != null && !department.isBlank()) ? department : "IT & Campus Operations";
                newUser = new HelpdeskOfficer(username, password, email, firstName, lastName, "ROLE_HELPDESK_OFFICER", dept);
            } else {
                String dept = (department != null && !department.isBlank()) ? department : "Faculty of Computing";
                String crse = (course != null && !course.isBlank()) ? course : "Software Engineering";
                newUser = new User(username, password, email, firstName, lastName, "ROLE_USER", dept, crse);
            }

            systemUserService.registerUser(newUser);
            redirectAttributes.addFlashAttribute("successMessage", "Account created successfully for " + username + " (" + cleanRole + ")!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating user: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam Long userId, RedirectAttributes redirectAttributes) {
        try {
            systemUserService.deleteUser(userId);
            redirectAttributes.addFlashAttribute("successMessage", "User account removed successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not remove user: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }
}
