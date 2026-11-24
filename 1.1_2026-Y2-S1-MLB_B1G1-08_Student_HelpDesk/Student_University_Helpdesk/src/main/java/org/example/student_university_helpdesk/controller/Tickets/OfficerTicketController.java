package org.example.student_university_helpdesk.controller.Tickets;

import org.example.student_university_helpdesk.model.Ticket.Ticket;
import org.example.student_university_helpdesk.model.Users.HelpdeskOfficer;
import org.example.student_university_helpdesk.model.Users.SystemUser;
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
import java.util.stream.Collectors;

@Controller
@RequestMapping({"/officer/tickets", "/tickets/officer-queue"})
public class OfficerTicketController {

    private final TicketService ticketService;
    private final SystemUserService systemUserService;

    @Autowired
    public OfficerTicketController(TicketService ticketService, SystemUserService systemUserService) {
        this.ticketService = ticketService;
        this.systemUserService = systemUserService;
    }

    // GET: Officer queue view
    @GetMapping
    public String showOfficerQueue(Model model, Principal principal) {
        Long officerId = resolveOfficerId(principal);
        List<Ticket> allTickets = ticketService.getAllTickets();

        // Split into unassigned incoming (PENDING/OPEN) and active assigned to this officer
        List<Ticket> incomingTickets = allTickets.stream()
                .filter(t -> ("PENDING".equalsIgnoreCase(t.getStatus()) || "OPEN".equalsIgnoreCase(t.getStatus())) 
                             && t.getHandledBy() == null)
                .collect(Collectors.toList());

        List<Ticket> myActiveTickets = allTickets.stream()
                .filter(t -> t.getHandledBy() != null 
                             && officerId != null 
                             && officerId.equals(t.getHandledBy().getUserId()))
                .collect(Collectors.toList());

        model.addAttribute("incomingTickets", incomingTickets);
        model.addAttribute("myActiveTickets", myActiveTickets);
        model.addAttribute("tickets", allTickets);

        return "tickets/officer-queue";
    }

    // POST: Accept incoming ticket
    @PostMapping("/accept")
    public String acceptTicket(@RequestParam Long ticketId,
                               @RequestParam(required = false) String returnUrl,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        try {
            Long officerId = resolveOfficerId(principal);
            ticketService.assignOfficer(ticketId, officerId);
            redirectAttributes.addFlashAttribute("successMessage", "Ticket #TK-" + ticketId + " accepted and assigned to your queue.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unable to accept ticket: " + e.getMessage());
        }
        if (returnUrl != null && !returnUrl.isBlank()) {
            return "redirect:" + returnUrl;
        }
        return "redirect:/officer/dashboard";
    }

    // POST: Update status (Pending / In Progress / Completed)
    @PostMapping("/update-status")
    public String updateTicketStatus(@RequestParam Long ticketId,
                                     @RequestParam String status,
                                     @RequestParam(required = false) String returnUrl,
                                     RedirectAttributes redirectAttributes) {
        try {
            ticketService.updateTicketStatus(ticketId, status);
            redirectAttributes.addFlashAttribute("successMessage", "Ticket #TK-" + ticketId + " status updated to " + status + ".");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unable to update ticket #" + ticketId + ": " + e.getMessage());
        }
        if (returnUrl != null && !returnUrl.isBlank()) {
            return "redirect:" + returnUrl;
        }
        return "redirect:/officer/dashboard";
    }

    private Long resolveOfficerId(Principal principal) {
        if (principal != null) {
            String identifier = principal.getName();
            Optional<SystemUser> userOpt = systemUserService.getUserByUsername(identifier)
                    .or(() -> systemUserService.getUserByEmail(identifier));
            if (userOpt.isPresent()) {
                return userOpt.get().getUserId();
            }
        }
        // Fallback to first officer registered in DB
        return systemUserService.getAllUsers().stream()
                .filter(u -> u instanceof HelpdeskOfficer)
                .map(SystemUser::getUserId)
                .findFirst()
                .orElse(2L);
    }
}
