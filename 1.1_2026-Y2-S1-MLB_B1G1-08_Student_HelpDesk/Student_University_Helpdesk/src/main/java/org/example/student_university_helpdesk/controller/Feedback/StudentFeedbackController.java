package org.example.student_university_helpdesk.controller.Feedback;

import org.example.student_university_helpdesk.model.Feedback.Feedback;
import org.example.student_university_helpdesk.model.Ticket.Ticket;
import org.example.student_university_helpdesk.service.FeedbackService;
import org.example.student_university_helpdesk.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/feedback")
public class StudentFeedbackController {

    private final FeedbackService feedbackService;
    private final TicketService ticketService;
    private final org.example.student_university_helpdesk.service.SystemUserService systemUserService;

    @Autowired
    public StudentFeedbackController(FeedbackService feedbackService, 
                                     TicketService ticketService,
                                     org.example.student_university_helpdesk.service.SystemUserService systemUserService) {
        this.feedbackService = feedbackService;
        this.ticketService = ticketService;
        this.systemUserService = systemUserService;
    }

    // GET: Post-ticket feedback view
    @GetMapping({"", "/student-feedback"})
    public String showStudentFeedback(@RequestParam(required = false) Long ticketId, 
                                      Model model, 
                                      java.security.Principal principal) {
        Long currentUserId = resolveUserId(principal);

        List<Feedback> allFeedbacks = feedbackService.getAllFeedback();
        List<Feedback> myFeedbacks = (currentUserId != null) ? allFeedbacks.stream()
                .filter(fb -> fb.getTicket() != null 
                              && fb.getTicket().getSubmittedBy() != null 
                              && currentUserId.equals(fb.getTicket().getSubmittedBy().getUserId()))
                .toList() : List.of();
        model.addAttribute("myFeedbacks", myFeedbacks);

        List<Ticket> myTickets = (currentUserId != null) ? ticketService.getTicketsByUserId(currentUserId) : List.of();
        List<Ticket> completedTickets = myTickets.stream()
                .filter(t -> "COMPLETED".equalsIgnoreCase(t.getStatus()))
                .toList();
        model.addAttribute("completedTickets", completedTickets);

        Ticket selectedTicket = null;
        if (ticketId != null) {
            try {
                Ticket t = ticketService.getTicketById(ticketId);
                if (currentUserId == null || (t.getSubmittedBy() != null && currentUserId.equals(t.getSubmittedBy().getUserId()))) {
                    selectedTicket = t;
                }
            } catch (Exception ignored) {}
        } else if (!completedTickets.isEmpty()) {
            selectedTicket = completedTickets.get(0);
        }

        model.addAttribute("ticket", selectedTicket);
        return "feedback/student-feedback";
    }

    private Long resolveUserId(java.security.Principal principal) {
        if (principal == null) return null;
        String identifier = principal.getName();
        return systemUserService.getUserByUsername(identifier)
                .or(() -> systemUserService.getUserByEmail(identifier))
                .map(org.example.student_university_helpdesk.model.Users.SystemUser::getUserId)
                .orElse(null);
    }

    // POST: Submit feedback
    @PostMapping("/create")
    public String submitFeedback(@RequestParam Long ticketId,
                                 @RequestParam Integer ratingScore,
                                 @RequestParam String comments,
                                 RedirectAttributes redirectAttributes) {
        try {
            // Normalize rating score to 1-5 if scale 1-10 was submitted
            int normalizedScore = (ratingScore > 5) ? (ratingScore + 1) / 2 : ratingScore;
            if (normalizedScore < 1) normalizedScore = 1;
            if (normalizedScore > 5) normalizedScore = 5;

            feedbackService.submitFeedback(ticketId, normalizedScore, comments);
            redirectAttributes.addFlashAttribute("message", "Thank you! Your feedback has been recorded.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error submitting feedback: " + e.getMessage());
        }
        return "redirect:/feedback/student-feedback";
    }

    // POST: Delete feedback
    @PostMapping("/delete")
    public String deleteFeedback(@RequestParam Long feedbackId, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "Feedback entry removed successfully.");
        return "redirect:/feedback/student-feedback";
    }
}
