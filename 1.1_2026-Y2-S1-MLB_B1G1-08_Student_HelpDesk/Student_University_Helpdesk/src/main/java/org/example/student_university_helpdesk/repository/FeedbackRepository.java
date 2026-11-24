package org.example.student_university_helpdesk.repository;

import org.example.student_university_helpdesk.model.Feedback.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    //Looks for specific ticket
    Optional<Feedback> findByTicketTicketId(Long ticketId);
    //Find all feedback matching rating
    List<Feedback> findByRatingScore(Integer ratingScore);
    //Find feedback with rating scores greater than or equal to
    List<Feedback> findByRatingScoreGreaterThanEqual(Integer ratingScore);
    //Find if ticket already exists from the Id
    boolean existsByTicketTicketId(Long ticketId);

    // Fixed JPQL path: f.ticket -> t.submittedBy
    @Query("SELECT f FROM Feedback f LEFT JOIN FETCH f.ticket t LEFT JOIN FETCH t.submittedBy")
    List<Feedback> findAllWithTickets();
}