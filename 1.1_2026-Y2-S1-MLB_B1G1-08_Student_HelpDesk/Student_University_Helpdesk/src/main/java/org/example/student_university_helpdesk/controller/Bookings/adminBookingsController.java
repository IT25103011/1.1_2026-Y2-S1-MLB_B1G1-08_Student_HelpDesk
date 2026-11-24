package org.example.student_university_helpdesk.controller.Bookings;

import org.example.student_university_helpdesk.model.ResourceManagement.Bookings;
import org.example.student_university_helpdesk.model.ResourceManagement.Resources;
import org.example.student_university_helpdesk.service.BookingsService;
import org.example.student_university_helpdesk.service.ResourcesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/bookings")
public class adminBookingsController {
    private final ResourcesService resourcesServices;
    private final BookingsService bookingsService;

    public adminBookingsController(ResourcesService resourcesServices, BookingsService bookingsService) {
        this.resourcesServices = resourcesServices;
        this.bookingsService = bookingsService;
    }

    @GetMapping("")
    public String showResourcesPage(Model model){
        List<Bookings> allBookings = bookingsService.getAllBookings();

        List<Resources> allResources = resourcesServices.getAllResources();

        model.addAttribute("bookings",allBookings);
        model.addAttribute("resources",allResources);
        return "bookings/admin-bookings";
    }

    @PostMapping("/resources/update")
    public String updateAvailability(@RequestParam Long resourceId, @RequestParam boolean availability,
                                     @RequestParam(required = false) String resourceType, @RequestParam(required = false) String location,
                                     RedirectAttributes redirectAttributes){
        resourcesServices.setResourceAvailability(resourceId,availability);
        redirectAttributes.addFlashAttribute("successMessage","Facility availability updated successfully.");
        return "redirect:/bookings/admin";

    }

    @PostMapping("/bookings/update-status")
    public String updateStatus(@RequestParam Long bookingId, @RequestParam String status,RedirectAttributes redirectAttributes){
        bookingsService.updateApprovalStatus(bookingId,status);
        redirectAttributes.addFlashAttribute("successMessage","Booking updated successfully.");
        return "redirect:/bookings/admin";
    }

}
