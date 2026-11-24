package org.example.student_university_helpdesk.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/")
    public String rootRedirect(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/default";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(Authentication authentication) {
        // If user is already authenticated, redirect away from login page
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/default";
        }
        return "auth/login";
    }

    @GetMapping("/default")
    public String defaultAfterLogin(Authentication authentication) {
        // Role-based post-login redirection
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            if (role.equals("ROLE_ADMIN")) {
                return "redirect:/admin/dashboard";
            } else if (role.equals("ROLE_HELPDESK_OFFICER")) {
                return "redirect:/officer/dashboard";
            } else if (role.equals("ROLE_USER")) {
                return "redirect:/student/dashboard";
            }
        }
        return "redirect:/login?error=true";
    }
}