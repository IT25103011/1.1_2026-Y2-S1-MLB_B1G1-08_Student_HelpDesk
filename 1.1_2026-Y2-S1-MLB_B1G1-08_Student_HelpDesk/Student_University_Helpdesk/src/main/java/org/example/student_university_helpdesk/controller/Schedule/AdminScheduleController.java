package org.example.student_university_helpdesk.controller.Schedule;

import org.example.student_university_helpdesk.model.ScheduleManagement.Event;
import org.example.student_university_helpdesk.model.ScheduleManagement.PersonalEvent;
import org.example.student_university_helpdesk.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping({"/admin/schedule", "/schedule/admin-schedule"})
public class AdminScheduleController {

    private final EventService eventService;

    @Autowired
    public AdminScheduleController(EventService eventService) {
        this.eventService = eventService;
    }

    // GET: Admin event publishing & management view
    @GetMapping
    public String showAdminSchedule(Model model) {
        List<Event> events = eventService.getAllEvents().stream()
                .filter(e -> !(e instanceof PersonalEvent))
                .collect(Collectors.toList());
        model.addAttribute("events", events);
        return "schedule/admin-schedule";
    }

    // POST: Publish new campus-wide event
    @PostMapping("/create")
    public String createCampusEvent(@RequestParam String title,
                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                    @RequestParam(required = false, defaultValue = "All Students & Faculty") String location,
                                    @RequestParam(required = false, defaultValue = "480") Integer duration,
                                    RedirectAttributes redirectAttributes) {
        LocalTime endTime = LocalTime.of(17, 0);
        eventService.createEvent(title, date, location, duration, endTime, 30, 1L);
        redirectAttributes.addFlashAttribute("successMessage", "Campus-wide event published successfully.");
        return "redirect:/admin/schedule";
    }

    // POST: Update existing event
    @PostMapping("/update")
    public String updateCampusEvent(@RequestParam Long eventId,
                                    @RequestParam String title,
                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                    @RequestParam(required = false, defaultValue = "Campus Facility") String location,
                                    @RequestParam(required = false, defaultValue = "120") Integer duration,
                                    RedirectAttributes redirectAttributes) {
        LocalTime endTime = LocalTime.of(18, 0);
        eventService.updateEvent(eventId, title, date, location, duration, endTime, 30);
        redirectAttributes.addFlashAttribute("successMessage", "Event details updated successfully.");
        return "redirect:/admin/schedule";
    }

    // POST: Delete campus event
    @PostMapping("/delete")
    public String deleteCampusEvent(@RequestParam Long eventId, RedirectAttributes redirectAttributes) {
        eventService.deleteEvent(eventId);
        redirectAttributes.addFlashAttribute("successMessage", "Event deleted successfully.");
        return "redirect:/admin/schedule";
    }
}
