package org.example.student_university_helpdesk.controller.Schedule;

import org.example.student_university_helpdesk.model.ScheduleManagement.Event;
import org.example.student_university_helpdesk.model.ScheduleManagement.PersonalEvent;
import org.example.student_university_helpdesk.model.Users.SystemUser;
import org.example.student_university_helpdesk.service.EventService;
import org.example.student_university_helpdesk.service.PersonalEventService;
import org.example.student_university_helpdesk.service.SystemUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/schedule")
public class StudentScheduleController {

    private final EventService eventService;
    private final PersonalEventService personalEventService;
    private final SystemUserService systemUserService;

    @Autowired
    public StudentScheduleController(EventService eventService,
                                     PersonalEventService personalEventService,
                                     SystemUserService systemUserService) {
        this.eventService = eventService;
        this.personalEventService = personalEventService;
        this.systemUserService = systemUserService;
    }

    // GET: Side-by-side calendar dashboard
    @GetMapping({"", "/student-schedule"})
    public String showStudentSchedule(Model model, Principal principal) {
        Long currentUserId = resolveUserId(principal);
        List<Event> campusEvents = eventService.getAllEvents().stream()
                .filter(e -> !(e instanceof PersonalEvent))
                .collect(Collectors.toList());
        List<PersonalEvent> personalEvents = personalEventService.getPersonalEventsByUser(currentUserId);

        List<Map<String, Object>> campusList = campusEvents.stream().map(e -> {
            Map<String, Object> m = new HashMap<>();
            m.put("eventId", e.getEventId());
            m.put("title", e.getTitle());
            m.put("date", e.getDate() != null ? e.getDate().toString() : "");
            m.put("duration", e.getDuration());
            m.put("location", e.getLocation());
            m.put("type", "academic");
            return m;
        }).collect(Collectors.toList());

        List<Map<String, Object>> personalList = personalEvents.stream().map(e -> {
            Map<String, Object> m = new HashMap<>();
            m.put("eventId", e.getEventId());
            m.put("title", e.getTitle());
            m.put("date", e.getDate() != null ? e.getDate().toString() : "");
            m.put("duration", e.getDuration());
            m.put("type", "personal");
            return m;
        }).collect(Collectors.toList());

        model.addAttribute("campusEvents", campusList);
        model.addAttribute("personalEvents", personalList);
        model.addAttribute("currentUserId", currentUserId);

        return "schedule/student-schedule";
    }

    // POST: Add personal event
    @PostMapping("/personal-events/create")
    public String createPersonalEvent(@RequestParam String title,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                      @RequestParam(required = false, defaultValue = "120") Integer duration,
                                      @RequestParam(required = false, defaultValue = "7") Integer setDeleteTimer,
                                      Principal principal,
                                      RedirectAttributes redirectAttributes) {
        Long currentUserId = resolveUserId(principal);
        LocalTime endTime = LocalTime.now().plusMinutes(duration != null ? duration : 120);

        personalEventService.createPersonalEvent(currentUserId, title, date, "Personal Study", duration, endTime, setDeleteTimer, null);
        redirectAttributes.addFlashAttribute("message", "Personal event scheduled successfully.");

        return "redirect:/schedule/student-schedule";
    }

    private Long resolveUserId(Principal principal) {
        if (principal == null) return 1L;
        String identifier = principal.getName();
        return systemUserService.getUserByEmail(identifier)
                .or(() -> systemUserService.getUserByUsername(identifier))
                .map(SystemUser::getUserId)
                .orElse(1L);
    }
}
