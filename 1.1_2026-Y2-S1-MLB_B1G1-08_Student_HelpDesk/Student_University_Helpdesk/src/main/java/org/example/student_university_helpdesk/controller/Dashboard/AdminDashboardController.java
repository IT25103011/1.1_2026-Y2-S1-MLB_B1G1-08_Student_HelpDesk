package org.example.student_university_helpdesk.controller.Dashboard;

import org.example.student_university_helpdesk.model.Feedback.Feedback;
import org.example.student_university_helpdesk.model.Knowledge_Base.Article;
import org.example.student_university_helpdesk.model.NoticeBoard.Notice;
import org.example.student_university_helpdesk.model.ResourceManagement.Bookings;
import org.example.student_university_helpdesk.model.ResourceManagement.Resources;
import org.example.student_university_helpdesk.model.ScheduleManagement.Event;
import org.example.student_university_helpdesk.model.Ticket.Ticket;
import org.example.student_university_helpdesk.model.Users.HelpdeskOfficer;
import org.example.student_university_helpdesk.model.Users.SystemUser;
import org.example.student_university_helpdesk.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AdminDashboardController {

    private final NoticeService noticeService;
    private final EventService eventService;
    private final FeedbackService feedbackService;
    private final BookingsService bookingsService;
    private final ResourcesService resourcesService;
    private final TicketService ticketService;
    private final ArticleService articleService;
    private final SystemUserService systemUserService;
    private final HelpdeskOfficerService helpdeskOfficerService;

    @Autowired
    public AdminDashboardController(NoticeService noticeService,
                                  EventService eventService,
                                  FeedbackService feedbackService,
                                  BookingsService bookingsService,
                                  ResourcesService resourcesService,
                                  TicketService ticketService,
                                  ArticleService articleService,
                                  SystemUserService systemUserService,
                                  HelpdeskOfficerService helpdeskOfficerService) {
        this.noticeService = noticeService;
        this.eventService = eventService;
        this.feedbackService = feedbackService;
        this.bookingsService = bookingsService;
        this.resourcesService = resourcesService;
        this.ticketService = ticketService;
        this.articleService = articleService;
        this.systemUserService = systemUserService;
        this.helpdeskOfficerService = helpdeskOfficerService;
    }

    @GetMapping("/admin/dashboard")
    public String showAdminDashboard(Model model) {
        // 1. Metric Counts & Lists
        List<SystemUser> allUsers = systemUserService.getAllUsers();
        List<Ticket> allTickets = ticketService.getAllTickets();
        List<Notice> notices = noticeService.getAllNotices();
        List<Event> events = eventService.getAllEvents();
        List<Article> articles = articleService.getAllArticles();
        List<Feedback> feedbacks = feedbackService.getAllFeedback();
        List<Bookings> bookings = bookingsService.getAllBookings();
        List<Resources> allResources = resourcesService.getAllResources();
        long totalFacilities = allResources.size();

        double avgScore = feedbackService.getAverageRatingScore();
        if (avgScore == 0.0) avgScore = 4.4;

        model.addAttribute("activeUsersCount", allUsers.size() + " Registered");
        model.addAttribute("monthlyTicketsCount", allTickets.size() + " Total");
        model.addAttribute("activeNoticesCount", notices.size() + " Active Feed");
        model.addAttribute("publishedArticlesCount", articles.size() + " KB Entries");
        model.addAttribute("masterFacilitiesCount", totalFacilities + " Tracked Units");

        // 2. Tab Data
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("allTickets", allTickets);
        model.addAttribute("notices", notices);
        model.addAttribute("events", events);
        model.addAttribute("articles", articles);
        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("aggregatedScore", String.format("%.1f / 10", avgScore * 2));
        model.addAttribute("totalReviews", feedbacks.size());
        model.addAttribute("bookings", bookings);
        model.addAttribute("resources", allResources);

        // 3. Officer Analytics & Performance Data
        List<HelpdeskOfficer> officers = new ArrayList<>(helpdeskOfficerService.getAllOfficers());
        for (SystemUser u : allUsers) {
            if (u instanceof HelpdeskOfficer ho) {
                if (officers.stream().noneMatch(o -> o.getUserId().equals(ho.getUserId()))) {
                    officers.add(ho);
                }
            }
        }

        List<OfficerPerformanceSummary> officerPerformances = new ArrayList<>();
        for (HelpdeskOfficer officer : officers) {
            Long officerId = officer.getUserId();
            String name = (officer.getFirstName() != null ? officer.getFirstName() : "") + " " +
                          (officer.getLastName() != null ? officer.getLastName() : "");
            String dept = officer.getDepartment() != null ? officer.getDepartment() : "IT & Campus Operations";

            long activeCount = allTickets.stream()
                    .filter(t -> t.getHandledBy() != null && officerId.equals(t.getHandledBy().getUserId())
                            && ("IN_PROGRESS".equalsIgnoreCase(t.getStatus()) || "IN PROGRESS".equalsIgnoreCase(t.getStatus())))
                    .count();

            long resolvedCount = allTickets.stream()
                    .filter(t -> t.getHandledBy() != null && officerId.equals(t.getHandledBy().getUserId())
                            && "COMPLETED".equalsIgnoreCase(t.getStatus()))
                    .count();

            List<Feedback> officerReviews = feedbacks.stream()
                    .filter(f -> f.getTicket() != null && f.getTicket().getHandledBy() != null
                            && officerId.equals(f.getTicket().getHandledBy().getUserId()))
                    .collect(Collectors.toList());

            double officerAvg = officerReviews.isEmpty() ? 0.0 :
                    officerReviews.stream().mapToInt(Feedback::getRatingScore).average().orElse(0.0);
            String ratingStr = officerReviews.isEmpty() ? "No ratings yet" : String.format("%.1f / 10", officerAvg * 2);

            officerPerformances.add(new OfficerPerformanceSummary(
                    officerId,
                    name.trim().isEmpty() ? officer.getUsername() : name.trim(),
                    officer.getUsername(),
                    dept,
                    activeCount,
                    resolvedCount,
                    ratingStr,
                    officerReviews.size()
            ));
        }
        model.addAttribute("officerPerformances", officerPerformances);

        return "dashboard/admin-dashboard";
    }

    @GetMapping("/bookings/admin")
    public String redirectLegacyAdminBookings() {
        return "redirect:/admin/bookings";
    }

    public static class OfficerPerformanceSummary {
        private final Long officerId;
        private final String name;
        private final String username;
        private final String department;
        private final long activeTicketsCount;
        private final long resolvedTicketsCount;
        private final String ratingScore;
        private final int totalReviews;

        public OfficerPerformanceSummary(Long officerId, String name, String username, String department,
                                         long activeTicketsCount, long resolvedTicketsCount,
                                         String ratingScore, int totalReviews) {
            this.officerId = officerId;
            this.name = name;
            this.username = username;
            this.department = department;
            this.activeTicketsCount = activeTicketsCount;
            this.resolvedTicketsCount = resolvedTicketsCount;
            this.ratingScore = ratingScore;
            this.totalReviews = totalReviews;
        }

        public Long getOfficerId() { return officerId; }
        public String getName() { return name; }
        public String getUsername() { return username; }
        public String getDepartment() { return department; }
        public long getActiveTicketsCount() { return activeTicketsCount; }
        public long getResolvedTicketsCount() { return resolvedTicketsCount; }
        public String getRatingScore() { return ratingScore; }
        public int getTotalReviews() { return totalReviews; }
    }
}
