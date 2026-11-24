package org.example.student_university_helpdesk.service;

import org.example.student_university_helpdesk.model.ScheduleManagement.Event;
import org.example.student_university_helpdesk.model.Users.Admin;
import org.example.student_university_helpdesk.repository.AdminRepository;
import org.example.student_university_helpdesk.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public EventService(EventRepository eventRepository, AdminRepository adminRepository) {
        this.eventRepository = eventRepository;
        this.adminRepository = adminRepository;
    }

    // ADMIN OPERATIONS

    @Transactional
    public Event createEvent(String title, LocalDate date, String location, Integer duration,
                             LocalTime endTime, Integer setDeleteTimer, Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found with ID: " + adminId));

        Event event = new Event(title, date, location, duration, endTime, setDeleteTimer, admin);
        return eventRepository.save(event);
    }

    @Transactional
    public Event updateEvent(Long eventId, String title, LocalDate date, String location,
                             Integer duration, LocalTime endTime, Integer setDeleteTimer) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));

        event.setTitle(title);
        event.setDate(date);
        event.setLocation(location);
        event.setDuration(duration);
        event.setEndTime(endTime);
        event.setSetDeleteTimer(setDeleteTimer);

        return eventRepository.save(event);
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new RuntimeException("Event not found with ID: " + eventId);
        }
        eventRepository.deleteById(eventId);
    }

    // QUERY OPERATIONS
    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));
    }

    @Transactional(readOnly = true)
    public List<Event> getEventsByDate(LocalDate date) {
        return eventRepository.findByDate(date);
    }

    @Transactional(readOnly = true)
    public List<Event> getEventsBetweenDates(LocalDate startDate, LocalDate endDate) {
        return eventRepository.findByDateBetween(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<Event> getEventsByLocation(String location) {
        return eventRepository.findByLocation(location);
    }

    @Transactional(readOnly = true)
    public List<Event> getEventsByAdmin(Long adminId) {
        return eventRepository.findByScheduledBy_UserId(adminId);
    }
}