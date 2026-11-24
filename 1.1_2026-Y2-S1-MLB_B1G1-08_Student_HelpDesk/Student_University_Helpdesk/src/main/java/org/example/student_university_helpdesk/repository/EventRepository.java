package org.example.student_university_helpdesk.repository;

import org.example.student_university_helpdesk.model.ScheduleManagement.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByScheduledBy_UserId(Long adminId);

    List<Event> findByDate(LocalDate date);

    List<Event> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<Event> findByLocation(String location);
}