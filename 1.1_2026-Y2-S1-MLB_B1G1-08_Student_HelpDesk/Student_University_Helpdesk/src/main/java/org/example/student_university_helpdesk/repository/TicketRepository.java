package org.example.student_university_helpdesk.repository;

import org.example.student_university_helpdesk.model.Ticket.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findBySubmittedBy_UserId(Long userId);

    List<Ticket> findByHandledBy_UserId(Long userId);

    List<Ticket> findByStatus(String status);

    List<Ticket> findBySubmittedBy_UserIdAndStatus(Long userId, String status);

    List<Ticket> findByHandledBy_UserIdAndStatus(Long officerId, String status);
}