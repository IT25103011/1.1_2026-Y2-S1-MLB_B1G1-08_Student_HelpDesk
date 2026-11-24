package org.example.student_university_helpdesk.controller.Tickets;

import org.example.student_university_helpdesk.model.Ticket.Ticket;
import org.example.student_university_helpdesk.model.Users.SystemUser;
import org.example.student_university_helpdesk.model.Users.User;
import org.example.student_university_helpdesk.repository.UserRepository;
import org.example.student_university_helpdesk.service.SystemUserService;
import org.example.student_university_helpdesk.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tickets")
public class StudentTicketController {

    private final TicketService ticketService;
    private final SystemUserService systemUserService;
    private final UserRepository userRepository;

    @Autowired
    public StudentTicketController(TicketService ticketService,
                                  SystemUserService systemUserService,
                                  UserRepository userRepository) {
        this.ticketService = ticketService;
        this.systemUserService = systemUserService;
        this.userRepository = userRepository;
    }

    // GET: View tickets dashboard
    @GetMapping({"", "/student-tickets"})
    public String showStudentTickets(Model model, Principal principal) {
        Long currentUserId = resolveUserId(principal);
        List<Ticket> myTickets = ticketService.getTicketsByUserId(currentUserId);

        // Removed fallback logic

        model.addAttribute("tickets", myTickets);
        model.addAttribute("currentUserId", currentUserId);
        return "tickets/student-tickets";
    }

    // POST: Submit a new ticket
    @PostMapping("/create")
    public String createTicket(@RequestParam String subject,
                               @RequestParam String description,
                               @RequestParam(required = false) Long userId,
                               @RequestParam(required = false) String returnUrl,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        try {
            Long targetUserId = (userId != null && userRepository.existsById(userId)) ? userId : resolveUserId(principal);

            ticketService.createTicket(targetUserId, subject, description);
            redirectAttributes.addFlashAttribute("successMessage", "Support ticket submitted successfully.");

            if (returnUrl != null && !returnUrl.isBlank()) {
                return "redirect:" + returnUrl + "?success=true";
            }
            return "redirect:/tickets/student-tickets?success=true";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unable to submit ticket: " + e.getMessage());
            if (returnUrl != null && !returnUrl.isBlank()) {
                return "redirect:" + returnUrl + "?error=true";
            }
            return "redirect:/tickets/student-tickets?error=true";
        }
    }

    // POST: Delete pending ticket
    @PostMapping("/delete")
    public String deleteTicket(@RequestParam Long ticketId, RedirectAttributes redirectAttributes) {
        try {
            Ticket ticket = ticketService.getTicketById(ticketId);
            if ("PENDING".equalsIgnoreCase(ticket.getStatus()) || "OPEN".equalsIgnoreCase(ticket.getStatus())) {
                ticketService.updateTicketStatus(ticketId, "CANCELLED");
                redirectAttributes.addFlashAttribute("message", "Ticket cancelled successfully.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Only pending tickets can be deleted.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unable to delete ticket: " + e.getMessage());
        }
        return "redirect:/tickets/student-tickets?deleted=true";
    }

    private Long resolveUserId(Principal principal) {
        if (principal != null) {
            String identifier = principal.getName();
            Optional<SystemUser> systemUserOpt = systemUserService.getUserByUsername(identifier)
                    .or(() -> systemUserService.getUserByEmail(identifier));
            if (systemUserOpt.isPresent()) {
                Long uid = systemUserOpt.get().getUserId();
                if (userRepository.existsById(uid)) {
                    return uid;
                }
                // If in system_users but not yet mapped in users table, insert mapping
                SystemUser su = systemUserOpt.get();
                User student = new User();
                student.setUserId(su.getUserId());
                student.setUsername(su.getUsername());
                student.setPassword(su.getPassword());
                student.setEmail(su.getEmail());
                student.setFirstName(su.getFirstName());
                student.setLastName(su.getLastName());
                student.setDepartment("Faculty of Computing");
                student.setCourse("BSc Software Engineering");
                userRepository.save(student);
                return uid;
            }
        }

        // Fallback to the first existing student in the users table
        return userRepository.findAll().stream()
                .findFirst()
                .map(User::getUserId)
                .orElse(3L);
    }
}
