package org.example.student_university_helpdesk.model.Ticket;

import jakarta.persistence.*;
import org.example.student_university_helpdesk.model.Users.User;
import org.example.student_university_helpdesk.model.Users.HelpdeskOfficer;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long ticketId;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User submittedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "officer_id",nullable = true) // Set it to true bc records can be created without officer at first
    private HelpdeskOfficer handledBy;

    public Ticket() {
        this.createdAt = LocalDateTime.now();
    }

    public Ticket(String subject, String description, String status, User submittedBy) {
        this.subject = subject;
        this.description = description;
        this.status = status;
        this.submittedBy = (User) submittedBy;
        this.createdAt = LocalDateTime.now();
    }

    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getSubmittedBy() { return submittedBy; }
    public void setSubmittedBy(User submittedBy) { this.submittedBy = submittedBy; }

    public HelpdeskOfficer getHandledBy() { return handledBy; }
    public void setHandledBy(HelpdeskOfficer handledBy) { this.handledBy = handledBy; }
}