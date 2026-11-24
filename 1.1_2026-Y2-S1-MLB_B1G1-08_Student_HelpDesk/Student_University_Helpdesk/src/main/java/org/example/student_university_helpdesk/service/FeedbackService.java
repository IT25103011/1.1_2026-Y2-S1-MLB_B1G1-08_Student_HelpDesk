package org.example.student_university_helpdesk.service;

import org.example.student_university_helpdesk.model.Feedback.Feedback;
import org.example.student_university_helpdesk.model.Ticket.Ticket;
import org.example.student_university_helpdesk.repository.FeedbackRepository;
import org.example.student_university_helpdesk.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final TicketRepository ticketRepository;

    @Autowired
    public FeedbackService(FeedbackRepository feedbackRepository, TicketRepository ticketRepository) {
        this.feedbackRepository = feedbackRepository;
        this.ticketRepository = ticketRepository;
    }

    // USER SIDE OPERATIONS

    @Transactional
    public Feedback submitFeedback(Long ticketId, Integer ratingScore, String comments) {
        // 1. Validate rating boundaries (1 to 5 scale)
        if (ratingScore == null || ratingScore < 1 || ratingScore > 5) {
            throw new IllegalArgumentException("Rating score must be between 1 and 5.");
        }

        // 2. Prevent duplicate submissions on the same ticket (enforces @OneToOne constraint)
        if (feedbackRepository.existsByTicketTicketId(ticketId)) {
            throw new IllegalStateException("Feedback has already been submitted for ticket ID: " + ticketId);
        }

        // 3. Look up target ticket
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));

        // 4. Build and save feedback
        Feedback feedback = new Feedback(ratingScore, comments, ticket);
        feedback.setSubmittedDate(LocalDateTime.now());

        return feedbackRepository.save(feedback);
    }

    @Transactional
    public Feedback updateFeedback(Long feedbackId, Integer ratingScore, String comments) {
        // 1. Validate rating boundaries
        if (ratingScore == null || ratingScore < 1 || ratingScore > 5) {
            throw new IllegalArgumentException("Rating score must be between 1 and 5.");
        }

        // 2. Retrieve existing feedback entry
        Feedback existingFeedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found with ID: " + feedbackId));

        // 3. Update editable properties
        existingFeedback.setRatingScore(ratingScore);
        existingFeedback.setComments(comments);

        // 4. Save and return updated entity
        return feedbackRepository.save(existingFeedback);
    }

    @Transactional(readOnly = true)
    public Feedback getFeedbackByTicketId(Long ticketId) {
        return feedbackRepository.findByTicketTicketId(ticketId)
                .orElseThrow(() -> new RuntimeException("No feedback found for ticket ID: " + ticketId));
    }

    // ADMIN / ANALYTICS OPERATIONS

    @Transactional(readOnly = true)
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAllWithTickets();
    }

    @Transactional(readOnly = true)
    public List<Feedback> getFeedbackByRating(Integer ratingScore) {
        return feedbackRepository.findByRatingScore(ratingScore);
    }

    @Transactional(readOnly = true)
    public List<Feedback> getHighRatedFeedback(Integer minimumRating) {
        return feedbackRepository.findByRatingScoreGreaterThanEqual(minimumRating);
    }

    @Transactional(readOnly = true)
    public double getAverageRatingScore() {
        List<Feedback> allFeedback = feedbackRepository.findAll();
        if (allFeedback.isEmpty()) {
            return 0.0;
        }

        return allFeedback.stream()
                .mapToInt(Feedback::getRatingScore)
                .average()
                .orElse(0.0);
    }

    @Transactional
    public Feedback getFeedbackById(Long id) {
        return feedbackRepository.findById(id).orElse(null);
    }
}