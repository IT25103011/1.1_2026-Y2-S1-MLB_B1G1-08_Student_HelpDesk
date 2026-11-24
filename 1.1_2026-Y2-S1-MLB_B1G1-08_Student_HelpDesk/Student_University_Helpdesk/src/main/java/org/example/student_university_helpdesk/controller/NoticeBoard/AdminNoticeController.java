package org.example.student_university_helpdesk.controller.NoticeBoard;

import org.example.student_university_helpdesk.model.NoticeBoard.Notice;
import org.example.student_university_helpdesk.model.Users.SystemUser;
import org.example.student_university_helpdesk.service.NoticeService;
import org.example.student_university_helpdesk.service.SystemUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping({"/admin/notices", "/notices/admin-notices"})
public class AdminNoticeController {

    private final NoticeService noticeService;
    private final SystemUserService systemUserService;

    @Autowired
    public AdminNoticeController(NoticeService noticeService, SystemUserService systemUserService) {
        this.noticeService = noticeService;
        this.systemUserService = systemUserService;
    }

    // GET: Admin notice management view
    @GetMapping
    public String showAdminNotices(@RequestParam(required = false) Long editNoticeId, Model model) {
        List<Notice> notices = noticeService.getAllNotices();
        model.addAttribute("notices", notices);
        if (editNoticeId != null) {
            try {
                Notice editNotice = noticeService.getNoticeById(editNoticeId);
                model.addAttribute("editNotice", editNotice);
            } catch (Exception ignored) {}
        }
        return "notices/admin-notices";
    }

    // POST: Create and publish notice
    @PostMapping("/create")
    public String createNotice(@RequestParam String title,
                               @RequestParam String message,
                               @RequestParam(required = false) String colorCategory,
                               @RequestParam(required = false) String expirationDate,
                               @RequestParam(required = false) String expirationTimer,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        LocalDate expDate = computeExpirationDate(expirationDate, expirationTimer);
        String finalTitle = applyColorToTitle(title, colorCategory);

        Long adminId = resolveAdminId(principal);
        noticeService.createNotice(finalTitle, message, expDate, adminId);
        redirectAttributes.addFlashAttribute("successMessage", "Campus announcement broadcasted successfully.");
        return "redirect:/admin/notices";
    }

    // POST: Modify existing notice
    @PostMapping("/update")
    public String updateNotice(@RequestParam Long noticeId,
                               @RequestParam String title,
                               @RequestParam String message,
                               @RequestParam(required = false) String colorCategory,
                               @RequestParam(required = false) String expirationDate,
                               @RequestParam(required = false) String expirationTimer,
                               RedirectAttributes redirectAttributes) {
        LocalDate expDate = computeExpirationDate(expirationDate, expirationTimer);
        String finalTitle = applyColorToTitle(title, colorCategory);

        noticeService.updateNotice(noticeId, finalTitle, message, expDate);
        redirectAttributes.addFlashAttribute("successMessage", "Announcement updated successfully.");
        return "redirect:/admin/notices";
    }

    private String applyColorToTitle(String title, String colorCategory) {
        if (title == null) {
            return "";
        }
        String cleanTitle = title.trim();
        if ("RED".equalsIgnoreCase(colorCategory)) {
            if (!cleanTitle.toLowerCase().contains("urgent")) {
                cleanTitle = "[URGENT] " + cleanTitle;
            }
        } else if ("GREY".equalsIgnoreCase(colorCategory)) {
            cleanTitle = cleanTitle.replaceAll("(?i)\\[urgent\\]\\s*", "").trim();
            cleanTitle = cleanTitle.replaceAll("(?i)urgent:\\s*", "").trim();
            cleanTitle = cleanTitle.replaceAll("(?i)\\burgent\\b\\s*", "").trim();
        }
        return cleanTitle;
    }

    private LocalDate computeExpirationDate(String expirationDate, String expirationTimer) {
        if (expirationDate != null && !expirationDate.trim().isEmpty()) {
            try {
                return LocalDate.parse(expirationDate.trim());
            } catch (Exception ignored) {}
        }
        if (expirationTimer != null) {
            String lower = expirationTimer.toLowerCase();
            if (lower.contains("6 hour") || lower.contains("12 hour") || lower.contains("24 hour")) {
                return LocalDate.now().plusDays(1);
            } else if (lower.contains("48 hour")) {
                return LocalDate.now().plusDays(2);
            } else if (lower.contains("5 day")) {
                return LocalDate.now().plusDays(5);
            } else if (lower.contains("14 day")) {
                return LocalDate.now().plusDays(14);
            }
        }
        return LocalDate.now().plusDays(7);
    }

    // POST: Delete announcement
    @PostMapping("/delete")
    public String deleteNotice(@RequestParam Long noticeId, RedirectAttributes redirectAttributes) {
        noticeService.deleteNotice(noticeId);
        redirectAttributes.addFlashAttribute("successMessage", "Announcement removed successfully.");
        return "redirect:/admin/notices";
    }

    private Long resolveAdminId(Principal principal) {
        if (principal != null) {
            String identifier = principal.getName();
            return systemUserService.getUserByUsername(identifier)
                    .or(() -> systemUserService.getUserByEmail(identifier))
                    .map(SystemUser::getUserId)
                    .orElse(1L);
        }
        return 1L;
    }
}
