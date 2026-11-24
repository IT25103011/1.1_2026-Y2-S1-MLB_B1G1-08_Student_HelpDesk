package org.example.student_university_helpdesk.controller.Bookings;

import org.example.student_university_helpdesk.model.ResourceManagement.Bookings;
import org.example.student_university_helpdesk.model.ResourceManagement.Resources;
import org.example.student_university_helpdesk.model.Users.SystemUser;
import org.example.student_university_helpdesk.repository.ResourcesRepository;
import org.example.student_university_helpdesk.service.BookingsService;
import org.example.student_university_helpdesk.service.ResourcesService;
import org.example.student_university_helpdesk.service.SystemUserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user/bookings")
public class userBookingsController {
    private final BookingsService bookingsService;
    private final ResourcesService resourcesService;
    private final SystemUserService systemUserService;

    public userBookingsController(BookingsService bookingsService, ResourcesService resourcesService, SystemUserService systemUserService) {
        this.bookingsService = bookingsService;
        this.resourcesService = resourcesService;
        this.systemUserService = systemUserService;
    }

    @GetMapping("")
    public String showBookingsPage(Model model, Principal principal) {
        String username = (principal != null) ? principal.getName() : "";
        SystemUser currentUser = systemUserService.getUserByEmail(username)
                .or(() -> systemUserService.getUserByUsername(username))
                .orElse(null);
        Long currentUserId = (currentUser != null) ? currentUser.getUserId() : 3L;
        List<Bookings> allBookings = bookingsService.getBookingsByUserId(currentUserId);
        List<Resources> allResources = resourcesService.getAllResources();

        // Approved bookings across the campus for slot availability check
        List<Map<String, Object>> approvedList = bookingsService.getAllBookings().stream()
                .filter(b -> "CONFIRMED".equalsIgnoreCase(b.getApprovalStatus()) || "APPROVED".equalsIgnoreCase(b.getApprovalStatus()))
                .map(b -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("bookingId", b.getBookingId());
                    m.put("resourceId", b.getResource() != null ? b.getResource().getResourceId() : null);
                    m.put("startTime", b.getStartTime() != null ? b.getStartTime().toString() : "");
                    m.put("endTime", b.getEndTime() != null ? b.getEndTime().toString() : "");
                    return m;
                })
                .collect(Collectors.toList());

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userId", currentUserId);
        model.addAttribute("bookings", allBookings);
        model.addAttribute("resources", allResources);
        model.addAttribute("approvedBookings", approvedList);
        return "bookings/student-bookings";
    }

    @PostMapping("/create")
    public String createBooking(@RequestParam(required = false) Long userId,
                                @RequestParam Long resourceId,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        if (userId == null) {
            String username = (principal != null) ? principal.getName() : "";
            SystemUser currentUser = systemUserService.getUserByEmail(username)
                    .or(() -> systemUserService.getUserByUsername(username))
                    .orElse(null);
            userId = (currentUser != null) ? currentUser.getUserId() : 3L;
        }

        // Check if slot is already taken by an approved/confirmed booking for the same resource
        boolean isSlotTaken = bookingsService.getAllBookings().stream()
                .anyMatch(b -> b.getResource() != null
                        && b.getResource().getResourceId().equals(resourceId)
                        && ("CONFIRMED".equalsIgnoreCase(b.getApprovalStatus()) || "APPROVED".equalsIgnoreCase(b.getApprovalStatus()))
                        && b.getStartTime().isBefore(endTime) && b.getEndTime().isAfter(startTime));

        if (isSlotTaken) {
            redirectAttributes.addFlashAttribute("errorMessage", "This time slot is already booked and approved. Please pick another slot.");
            return "redirect:/user/bookings";
        }

        bookingsService.createBooking(userId, resourceId, startTime, endTime);
        redirectAttributes.addFlashAttribute("message", "Booking Created");
        return "redirect:/user/bookings";
    }

    @PostMapping("/reschedule")
    public String rescheduleBooking(@RequestParam Long bookingId,
                                    @RequestParam String newDate,
                                    @RequestParam String timeSlot,
                                    RedirectAttributes redirectAttributes) {
        Bookings oldBooking = bookingsService.getBookingById(bookingId);
        if (oldBooking != null) {
            Long userId = oldBooking.getUser() != null ? oldBooking.getUser().getUserId() : 3L;
            Long resourceId = oldBooking.getResource() != null ? oldBooking.getResource().getResourceId() : 1L;

            String[] parts = timeSlot.split("-");
            LocalDateTime newStart = LocalDateTime.parse(newDate + "T" + parts[0]);
            LocalDateTime newEnd = LocalDateTime.parse(newDate + "T" + parts[1]);

            // Check if new slot is already taken by an approved booking for this resource
            boolean isSlotTaken = bookingsService.getAllBookings().stream()
                    .anyMatch(b -> b.getResource() != null
                            && b.getResource().getResourceId().equals(resourceId)
                            && ("CONFIRMED".equalsIgnoreCase(b.getApprovalStatus()) || "APPROVED".equalsIgnoreCase(b.getApprovalStatus()))
                            && !b.getBookingId().equals(bookingId)
                            && b.getStartTime().isBefore(newEnd) && b.getEndTime().isAfter(newStart));

            if (isSlotTaken) {
                redirectAttributes.addFlashAttribute("errorMessage", "This time slot is already booked and approved. Please pick another slot.");
                return "redirect:/user/bookings";
            }

            // Delete old booking and create new booking request (status defaults to PENDING)
            bookingsService.deleteBooking(bookingId);
            bookingsService.createBooking(userId, resourceId, newStart, newEnd);
            redirectAttributes.addFlashAttribute("message", "Reschedule submitted. New request sent for admin approval.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Booking not found.");
        }
        return "redirect:/user/bookings";
    }

    @PostMapping("/cancel")
    public String deleteBooking(@RequestParam Long bookingId, RedirectAttributes redirectAttributes){
        bookingsService.deleteBooking(bookingId);
        redirectAttributes.addFlashAttribute("message","Booking Deleted");
        return "redirect:/user/bookings";
    }
}
