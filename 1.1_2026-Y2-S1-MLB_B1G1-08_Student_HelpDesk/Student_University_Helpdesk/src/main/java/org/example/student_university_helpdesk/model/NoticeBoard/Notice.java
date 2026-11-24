package org.example.student_university_helpdesk.model.NoticeBoard;

import jakarta.persistence.*;
import org.example.student_university_helpdesk.model.Users.Admin;

import java.time.LocalDate;

@Entity
@Table(name = "notices")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long noticeId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(name = "publish_date", nullable = false)
    private LocalDate publishDate;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    // Relationship: Publishes (Administrator 1 : N Notice)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", nullable = false)
    private Admin publishedBy;

    public Notice() {
        this.publishDate = LocalDate.now();
    }

    public Notice(String title, String message, LocalDate expirationDate, Admin publishedBy) {
        this.title = title;
        this.message = message;
        this.publishDate = LocalDate.now();
        this.expirationDate = expirationDate;
        this.publishedBy = publishedBy;
    }

    public Long getNoticeId() { return noticeId; }
    public void setNoticeId(Long noticeId) { this.noticeId = noticeId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDate getPublishDate() { return publishDate; }
    public void setPublishDate(LocalDate publishDate) { this.publishDate = publishDate; }

    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }

    public Admin getPublishedBy() { return publishedBy; }
    public void setPublishedBy(Admin publishedBy) { this.publishedBy = publishedBy; }
}
