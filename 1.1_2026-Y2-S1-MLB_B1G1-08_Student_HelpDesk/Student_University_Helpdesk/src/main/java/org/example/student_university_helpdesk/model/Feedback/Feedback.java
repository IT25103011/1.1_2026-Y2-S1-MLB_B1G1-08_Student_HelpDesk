package org.example.student_university_helpdesk.model.Feedback;

import jakarta.persistence.*;
import org.example.student_university_helpdesk.model.Ticket.Ticket;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long feedbackId;

    @Column(name = "rating_score", nullable = false)
    private Integer ratingScore;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "submitted_date", nullable = false)
    private LocalDateTime submittedDate;

    @OneToOne
    @JoinColumn(name = "ticket_id", referencedColumnName = "ticket_id", unique = true, nullable = false)
    private Ticket ticket;

    public Feedback() {
        this.submittedDate = LocalDateTime.now();
    }

    public Feedback(Integer ratingScore, String comments, Ticket ticket) {
        this.ratingScore = ratingScore;
        this.comments = comments;
        this.ticket = ticket;
        this.submittedDate = LocalDateTime.now();
    }

    public Long getFeedbackId() { return feedbackId; }
    public void setFeedbackId(Long feedbackId) { this.feedbackId = feedbackId; }

    public Integer getRatingScore() { return ratingScore; }
    public void setRatingScore(Integer ratingScore) { this.ratingScore = ratingScore; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public LocalDateTime getSubmittedDate() { return submittedDate; }
    public void setSubmittedDate(LocalDateTime submittedDate) { this.submittedDate = submittedDate; }

    public Ticket getTicket() { return ticket; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }
}
