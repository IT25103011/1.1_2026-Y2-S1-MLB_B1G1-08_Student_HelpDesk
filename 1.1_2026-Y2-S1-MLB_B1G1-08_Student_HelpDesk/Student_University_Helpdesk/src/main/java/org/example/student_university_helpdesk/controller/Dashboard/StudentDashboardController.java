package org.example.student_university_helpdesk.controller.Dashboard;

import org.example.student_university_helpdesk.model.NoticeBoard.Notice;
import org.example.student_university_helpdesk.model.ResourceManagement.Bookings;
import org.example.student_university_helpdesk.model.ScheduleManagement.Event;
import org.example.student_university_helpdesk.model.ScheduleManagement.PersonalEvent;
import org.example.student_university_helpdesk.model.Ticket.Ticket;
import org.example.student_university_helpdesk.model.Users.SystemUser;
import org.example.student_university_helpdesk.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class StudentDashboardController {

    private final TicketService ticketService;
    private final BookingsService bookingsService;
    private final NoticeService noticeService;
    private final PersonalEventService personalEventService;
    private final EventService eventService;
    private final SystemUserService systemUserService;

    @Autowired
    public StudentDashboardController(TicketService ticketService,
                                      BookingsService bookingsService,
                                      NoticeService noticeService,
                                      PersonalEventService personalEventService,
                                      EventService eventService,
                                      SystemUserService systemUserService) {
        this.ticketService = ticketService;
        this.bookingsService = bookingsService;
        this.noticeService = noticeService;
        this.personalEventService = personalEventService;
        this.eventService = eventService;
        this.systemUserService = systemUserService;
    }

    @GetMapping({"/student/dashboard", "/user/dashboard", "/dashboard"})
    public String showStudentDashboard(Model model, Principal principal) {
        SystemUser currentUser = resolveCurrentUser(principal);
        Long userId = (currentUser != null) ? currentUser.getUserId() : null;
        String studentName = (currentUser != null && currentUser.getFirstName() != null) 
                ? currentUser.getFirstName() : "Student";

        // 1. Announcements
        List<Notice> notices = noticeService.getAllNotices();

        // 2. Active Tickets
        List<Ticket> myTickets = (userId != null) ? ticketService.getTicketsByUserId(userId) : List.of();
        long activeTicketCount = myTickets.stream()
                .filter(t -> !"COMPLETED".equalsIgnoreCase(t.getStatus()) && !"CANCELLED".equalsIgnoreCase(t.getStatus()))
                .count();

        // 3. Resource Bookings
        List<Bookings> myBookings = (userId != null) ? bookingsService.getBookingsByUserId(userId) : List.of();
        long confirmedBookingCount = myBookings.stream()
                .filter(b -> "CONFIRMED".equalsIgnoreCase(b.getApprovalStatus()) || "APPROVED".equalsIgnoreCase(b.getApprovalStatus()))
                .count();

        // 4. Schedule
        List<PersonalEvent> personalEvents = (userId != null) ? personalEventService.getPersonalEventsByUser(userId) : List.of();
        List<Event> campusEvents = eventService.getAllEvents();

        model.addAttribute("userName", studentName);
        model.addAttribute("notices", notices);
        model.addAttribute("tickets", myTickets);
        model.addAttribute("activeTicketCount", activeTicketCount);
        model.addAttribute("bookings", myBookings);
        model.addAttribute("confirmedBookingCount", confirmedBookingCount);
        model.addAttribute("personalEvents", personalEvents);
        model.addAttribute("campusEvents", campusEvents);

        return "dashboard/student-dashboard";
    }

    private SystemUser resolveCurrentUser(Principal principal) {
        if (principal == null) return null;
        String identifier = principal.getName();
        return systemUserService.getUserByEmail(identifier)
                .or(() -> systemUserService.getUserByUsername(identifier))
                .orElse(null);
    }
}
