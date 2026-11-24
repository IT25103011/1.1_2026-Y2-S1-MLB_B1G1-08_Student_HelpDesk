package org.example.student_university_helpdesk.repository;

import org.example.student_university_helpdesk.model.ScheduleManagement.PersonalEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PersonalEventRepository extends JpaRepository<PersonalEvent, Long> {

    List<PersonalEvent> findByUser_UserId(Long userId);

    List<PersonalEvent> findByUser_UserIdAndDate(Long userId, LocalDate date);

    List<PersonalEvent> findByUser_UserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}