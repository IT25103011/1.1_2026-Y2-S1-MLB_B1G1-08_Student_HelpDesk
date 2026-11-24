package org.example.student_university_helpdesk.controller.NoticeBoard;

import org.example.student_university_helpdesk.model.NoticeBoard.Notice;
import org.example.student_university_helpdesk.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/notices")
public class StudentNoticeController {

    private final NoticeService noticeService;

    @Autowired
    public StudentNoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    // GET: Student notice feed
    @GetMapping({"", "/student-notices"})
    public String showNoticeFeed(Model model) {
        List<Notice> notices = noticeService.getAllNotices();
        model.addAttribute("notices", notices);
        return "notices/student-notices";
    }
}
