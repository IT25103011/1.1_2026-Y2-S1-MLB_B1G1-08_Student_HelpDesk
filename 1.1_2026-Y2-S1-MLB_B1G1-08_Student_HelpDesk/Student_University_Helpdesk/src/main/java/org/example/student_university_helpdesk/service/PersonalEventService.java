package org.example.student_university_helpdesk.service;

import org.example.student_university_helpdesk.model.ScheduleManagement.PersonalEvent;
import org.example.student_university_helpdesk.model.Users.Admin;
import org.example.student_university_helpdesk.model.Users.SystemUser;
import org.example.student_university_helpdesk.repository.AdminRepository;
import org.example.student_university_helpdesk.repository.PersonalEventRepository;
import org.example.student_university_helpdesk.repository.SystemUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class PersonalEventService {

    private final PersonalEventRepository personalEventRepository;
    private final SystemUserRepository systemUserRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public PersonalEventService(PersonalEventRepository personalEventRepository,
                                SystemUserRepository systemUserRepository,
                                AdminRepository adminRepository) {
        this.personalEventRepository = personalEventRepository;
        this.systemUserRepository = systemUserRepository;
        this.adminRepository = adminRepository;
    }

    // USER PERSONAL EVENT OPERATIONS

    @Transactional
    public PersonalEvent createPersonalEvent(Long userId, String title, LocalDate date, String location,
                                             Integer duration, LocalTime endTime, Integer setDeleteTimer,
                                             Long adminId) {
        SystemUser user = systemUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Admin admin = null;
        if (adminId != null) {
            admin = adminRepository.findById(adminId).orElse(null);
        }

        PersonalEvent personalEvent = new PersonalEvent(title, date, location, duration, endTime, setDeleteTimer, admin, user);
        return personalEventRepository.save(personalEvent);
    }

    @Transactional
    public PersonalEvent updatePersonalEvent(Long eventId, Long userId, String title, LocalDate date,
                                             String location, Integer duration, LocalTime endTime,
                                             Integer setDeleteTimer) {
        PersonalEvent personalEvent = personalEventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Personal event not found with ID: " + eventId));

        if (!personalEvent.getUser().getUserId().equals(userId)) {
            throw new SecurityException("Unauthorized: You can only edit your own personal events.");
        }

        personalEvent.setTitle(title);
        personalEvent.setDate(date);
        personalEvent.setLocation(location);
        personalEvent.setDuration(duration);
        personalEvent.setEndTime(endTime);
        personalEvent.setSetDeleteTimer(setDeleteTimer);

        return personalEventRepository.save(personalEvent);
    }

    @Transactional
    public void deletePersonalEvent(Long eventId, Long userId) {
        PersonalEvent personalEvent = personalEventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Personal event not found with ID: " + eventId));

        if (!personalEvent.getUser().getUserId().equals(userId)) {
            throw new SecurityException("Unauthorized: You can only delete your own personal events.");
        }

        personalEventRepository.delete(personalEvent);
    }

    // QUERY OPERATIONS

    @Transactional(readOnly = true)
    public List<PersonalEvent> getPersonalEventsByUser(Long userId) {
        return personalEventRepository.findByUser_UserId(userId);
    }

    @Transactional(readOnly = true)
    public List<PersonalEvent> getPersonalEventsByUserAndDate(Long userId, LocalDate date) {
        return personalEventRepository.findByUser_UserIdAndDate(userId, date);
    }

    @Transactional(readOnly = true)
    public List<PersonalEvent> getPersonalEventsByUserAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return personalEventRepository.findByUser_UserIdAndDateBetween(userId, startDate, endDate);
    }
}