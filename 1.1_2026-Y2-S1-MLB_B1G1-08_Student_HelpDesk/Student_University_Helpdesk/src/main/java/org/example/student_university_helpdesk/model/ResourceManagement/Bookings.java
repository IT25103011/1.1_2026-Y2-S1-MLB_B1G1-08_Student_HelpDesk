package org.example.student_university_helpdesk.model.ResourceManagement;

import jakarta.persistence.*;
import org.example.student_university_helpdesk.model.Users.User;

import java.time.LocalDateTime;

@Entity
@Table(name="bookings")
public class Bookings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="booking_id")
    private Long bookingId;
    @Column(name="Approval_Status",nullable = false)
    private String approvalStatus;
    @Column(name="Start_Time",nullable = false)
    private LocalDateTime startTime;
    @Column(name="End_Time",nullable = false)
    private LocalDateTime endTime;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="resource_Id",nullable = false)
    private Resources resource;

    public Bookings() {
    }

    public Bookings(Long id, String approvalStatus, LocalDateTime startTime, LocalDateTime endTime, User user, Resources resource) {
        this.bookingId = id;
        this.approvalStatus = approvalStatus;
        this.startTime = startTime;
        this.endTime = endTime;
        this.user = user;
        this.resource = resource;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Resources getResource() {
        return resource;
    }

    public void setResource(Resources resource) {
        this.resource = resource;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
