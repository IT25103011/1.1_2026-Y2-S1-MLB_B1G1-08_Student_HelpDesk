package org.example.student_university_helpdesk.service;

import org.example.student_university_helpdesk.model.NoticeBoard.Notice;
import org.example.student_university_helpdesk.model.Users.Admin;
import org.example.student_university_helpdesk.repository.AdminRepository;
import org.example.student_university_helpdesk.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public NoticeService(NoticeRepository noticeRepository, AdminRepository adminRepository) {
        this.noticeRepository = noticeRepository;
        this.adminRepository = adminRepository;
    }

    // ADMIN OPERATIONS

    @Transactional
    public Notice createNotice(String title, String message, LocalDate expirationDate, Long adminId) {
        if (expirationDate != null && expirationDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Expiration date cannot be in the past.");
        }

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found with ID: " + adminId));

        Notice notice = new Notice(title, message, expirationDate, admin);
        return noticeRepository.save(notice);
    }

    @Transactional
    public Notice updateNotice(Long noticeId, String title, String message, LocalDate expirationDate) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("Notice not found with ID: " + noticeId));

        if (expirationDate != null && expirationDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Expiration date cannot be in the past.");
        }

        notice.setTitle(title);
        notice.setMessage(message);
        notice.setExpirationDate(expirationDate);

        return noticeRepository.save(notice);
    }

    @Transactional
    public void deleteNotice(Long noticeId) {
        if (!noticeRepository.existsById(noticeId)) {
            throw new RuntimeException("Notice not found with ID: " + noticeId);
        }
        noticeRepository.deleteById(noticeId);
    }

    // USER / BOARD OPERATIONS

    @Transactional(readOnly = true)
    public List<Notice> getAllNotices() {
        return noticeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Notice getNoticeById(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("Notice not found with ID: " + noticeId));
    }

    @Transactional(readOnly = true)
    public List<Notice> getActiveNotices() {
        // Retrieves notices expiring today/future OR notices with no set expiration date
        return noticeRepository.findByExpirationDateAfterOrExpirationDateIsNull(LocalDate.now().minusDays(1));
    }

    @Transactional(readOnly = true)
    public List<Notice> searchNoticesByTitle(String titleQuery) {
        return noticeRepository.findByTitleContainingIgnoreCase(titleQuery);
    }

    @Transactional(readOnly = true)
    public List<Notice> getNoticesByAdmin(Long adminId) {
        return noticeRepository.findByPublishedBy_UserId(adminId);
    }
}