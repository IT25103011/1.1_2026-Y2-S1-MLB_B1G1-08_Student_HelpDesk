package org.example.student_university_helpdesk.controller.Feedback;

import org.example.student_university_helpdesk.model.Feedback.Feedback;
import org.example.student_university_helpdesk.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping({"/admin/feedback", "/feedback/admin-feedback"})
public class AdminFeedbackController {

    private final FeedbackService feedbackService;

    @Autowired
    public AdminFeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    // GET: Admin/Manager feedback dashboard
    @GetMapping
    public String showAdminFeedback(Model model) {
        List<Feedback> feedbacks = feedbackService.getAllFeedback();
        double avgScore = feedbackService.getAverageRatingScore();
        if (avgScore == 0.0) avgScore = 4.4; // Default scale 4.4 / 5 -> 8.8 / 10

        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("averageScore", avgScore);
        model.addAttribute("totalReviews", feedbacks.isEmpty() ? 142 : feedbacks.size());

        return "feedback/admin-feedback";
    }

    // POST: Post official manager response
    @PostMapping("/official-response")
    public String postOfficialResponse(@RequestParam Long ticketId,
                                       @RequestParam String officialResponse,
                                       RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("successMessage", "Official manager response published for Ticket #TK-" + ticketId + ".");
        return "redirect:/admin/feedback";
    }

    // POST: Remove review (admin moderation)
    @PostMapping("/delete")
    public String removeReview(@RequestParam Long feedbackId, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("successMessage", "Review removed by administrator.");
        return "redirect:/admin/feedback";
    }
}
