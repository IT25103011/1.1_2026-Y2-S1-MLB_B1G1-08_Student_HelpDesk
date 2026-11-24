package org.example.student_university_helpdesk.controller.Dashboard;

import org.example.student_university_helpdesk.model.Feedback.Feedback;
import org.example.student_university_helpdesk.model.Ticket.Ticket;
import org.example.student_university_helpdesk.model.Users.SystemUser;
import org.example.student_university_helpdesk.service.FeedbackService;
import org.example.student_university_helpdesk.service.HelpdeskOfficerService;
import org.example.student_university_helpdesk.service.SystemUserService;
import org.example.student_university_helpdesk.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class OfficerDashboardController {

    private final TicketService ticketService;
    private final HelpdeskOfficerService officerService;
    private final FeedbackService feedbackService;
    private final SystemUserService systemUserService;

    @Autowired
    public OfficerDashboardController(TicketService ticketService,
                                    HelpdeskOfficerService officerService,
                                    FeedbackService feedbackService,
                                    SystemUserService systemUserService) {
        this.ticketService = ticketService;
        this.officerService = officerService;
        this.feedbackService = feedbackService;
        this.systemUserService = systemUserService;
    }

    @GetMapping("/officer/dashboard")
    public String showOfficerDashboard(Model model, Principal principal) {
        SystemUser currentUser = resolveCurrentUser(principal);
        Long currentOfficerId = (currentUser != null) ? currentUser.getUserId() : null;
        String officerName = (currentUser != null && currentUser.getFirstName() != null)
                ? (currentUser.getFirstName() + " " + (currentUser.getLastName() != null ? currentUser.getLastName() : ""))
                : "Helpdesk Officer";
        String officerDept = (currentUser instanceof org.example.student_university_helpdesk.model.Users.HelpdeskOfficer ho && ho.getDepartment() != null)
                ? ho.getDepartment() : "IT & Campus Operations";

        List<Ticket> allTickets = ticketService.getAllTickets();

        // 1. Incoming unassigned queue (PENDING / OPEN, handledBy is null)
        List<Ticket> incomingTickets = allTickets.stream()
                .filter(t -> ("PENDING".equalsIgnoreCase(t.getStatus()) || "OPEN".equalsIgnoreCase(t.getStatus())) 
                             && t.getHandledBy() == null)
                .collect(Collectors.toList());

        // 2. My accepted tickets (assigned to this officer and in progress)
        List<Ticket> myAcceptedTickets = allTickets.stream()
                .filter(t -> t.getHandledBy() != null 
                             && currentOfficerId != null 
                             && currentOfficerId.equals(t.getHandledBy().getUserId())
                             && ("IN_PROGRESS".equalsIgnoreCase(t.getStatus()) || "IN PROGRESS".equalsIgnoreCase(t.getStatus())))
                .collect(Collectors.toList());

        // 3. Tickets resolved by this officer
        long resolvedTodayCount = allTickets.stream()
                .filter(t -> t.getHandledBy() != null 
                             && currentOfficerId != null 
                             && currentOfficerId.equals(t.getHandledBy().getUserId())
                             && "COMPLETED".equalsIgnoreCase(t.getStatus()))
                .count();

        // 4. Feedback and evaluations specifically for tickets handled by this officer
        List<Feedback> allFeedback = feedbackService.getAllFeedback();
        List<Feedback> officerReviews = allFeedback.stream()
                .filter(f -> f.getTicket() != null 
                             && f.getTicket().getHandledBy() != null 
                             && currentOfficerId != null 
                             && currentOfficerId.equals(f.getTicket().getHandledBy().getUserId()))
                .collect(Collectors.toList());

        double avgScore = officerReviews.isEmpty() ? 0.0 :
                officerReviews.stream().mapToInt(Feedback::getRatingScore).average().orElse(0.0);

        model.addAttribute("officerName", officerName);
        model.addAttribute("department", officerDept);
        model.addAttribute("shiftStatus", "Active");
        model.addAttribute("myActiveTicketsCount", myAcceptedTickets.size());
        model.addAttribute("resolvedTodayCount", resolvedTodayCount);
        model.addAttribute("incomingCount", incomingTickets.size());
        model.addAttribute("incomingTickets", incomingTickets);
        model.addAttribute("myAcceptedTickets", myAcceptedTickets);
        model.addAttribute("reviews", officerReviews);
        model.addAttribute("ratingScore", officerReviews.isEmpty() ? "0.0 / 10" : String.format("%.1f / 10", avgScore * 2));
        model.addAttribute("totalReviews", officerReviews.size());

        return "dashboard/officer-dashboard";
    }

    private SystemUser resolveCurrentUser(Principal principal) {
        if (principal == null) return null;
        String identifier = principal.getName();
        return systemUserService.getUserByEmail(identifier)
                .or(() -> systemUserService.getUserByUsername(identifier))
                .orElse(null);
    }
}
