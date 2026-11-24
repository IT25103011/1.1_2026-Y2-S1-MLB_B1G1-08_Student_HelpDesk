package org.example.student_university_helpdesk.model.ScheduleManagement;

import jakarta.persistence.*;
import org.example.student_university_helpdesk.model.Users.Admin;
import org.example.student_university_helpdesk.model.Users.SystemUser;

@Entity
@Table(name = "personal_events")
@PrimaryKeyJoinColumn(name = "event_id")
public class PersonalEvent extends Event {

    // Relationship: Schedules (SystemUSER 1 : N Personal_Event)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private SystemUser user;

    public PersonalEvent() {
        super();
    }

    public PersonalEvent(String title, java.time.LocalDate date, String location, Integer duration,
                         java.time.LocalTime endTime, Integer setDeleteTimer,
                         Admin scheduledBy, SystemUser user) {
        super(title, date, location, duration, endTime, setDeleteTimer, scheduledBy);
        this.user = user;
    }

    public SystemUser getUser() { return user; }
    public void setUser(SystemUser user) { this.user = user; }
}
