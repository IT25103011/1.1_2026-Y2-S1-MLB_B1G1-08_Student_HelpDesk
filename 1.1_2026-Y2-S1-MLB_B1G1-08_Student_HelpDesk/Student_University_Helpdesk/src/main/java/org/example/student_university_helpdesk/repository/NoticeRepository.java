package org.example.student_university_helpdesk.repository;

import org.example.student_university_helpdesk.model.NoticeBoard.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    //Find all active notices where the expiration date is today or in the future
    List<Notice> findByExpirationDateGreaterThanEqual(LocalDate date);

    //Find active notices that haven't expired yet or have no expiration date set
    List<Notice> findByExpirationDateAfterOrExpirationDateIsNull(LocalDate date);

    // Find all notices published by a specific Admin (navigates publishedBy -> primary key)
    List<Notice> findByPublishedBy_UserId(Long userId);

    //Search notices containing a specific keyword in the title
    List<Notice> findByTitleContainingIgnoreCase(String title);
}