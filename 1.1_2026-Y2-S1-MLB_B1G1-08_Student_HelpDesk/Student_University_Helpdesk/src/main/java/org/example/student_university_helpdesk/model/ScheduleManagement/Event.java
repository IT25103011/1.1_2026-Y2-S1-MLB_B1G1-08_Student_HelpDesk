package org.example.student_university_helpdesk.model.ScheduleManagement;

import jakarta.persistence.*;
import org.example.student_university_helpdesk.model.Users.Admin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "events")
@Inheritance(strategy = InheritanceType.JOINED)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "location")
    private String location;

    @Column(name = "duration")
    private Integer duration; // Duration in minutes

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "set_delete_timer")
    private Integer setDeleteTimer; // Timer in days or hours for auto-cleanup

    // Relationship: Schedules (Administrator 1 : N Event)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin scheduledBy;

    public Event() {
        this.createdAt = LocalDateTime.now();
    }

    public Event(String title, LocalDate date, String location, Integer duration,
                 LocalTime endTime, Integer setDeleteTimer, Admin scheduledBy) {
        this.title = title;
        this.date = date;
        this.location = location;
        this.duration = duration;
        this.createdAt = LocalDateTime.now();
        this.endTime = endTime;
        this.setDeleteTimer = setDeleteTimer;
        this.scheduledBy = scheduledBy;
    }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Integer getSetDeleteTimer() { return setDeleteTimer; }
    public void setSetDeleteTimer(Integer setDeleteTimer) { this.setDeleteTimer = setDeleteTimer; }

    public Admin getScheduledBy() { return scheduledBy; }
    public void setScheduledBy(Admin scheduledBy) { this.scheduledBy = scheduledBy; }
}
