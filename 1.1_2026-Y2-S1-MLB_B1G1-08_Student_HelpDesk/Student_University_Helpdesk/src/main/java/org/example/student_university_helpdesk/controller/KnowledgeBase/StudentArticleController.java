package org.example.student_university_helpdesk.controller.KnowledgeBase;

import org.example.student_university_helpdesk.model.Knowledge_Base.Article;
import org.example.student_university_helpdesk.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/knowledgebase")
public class StudentArticleController {

    private final ArticleService articleService;

    @Autowired
    public StudentArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    // GET: Search & FAQs view
    @GetMapping({"", "/student-articles"})
    public String showKnowledgeBase(Model model) {
        List<Article> articles = articleService.getAllArticles();
        model.addAttribute("articles", articles);
        return "knowledgebase/student-articles";
    }

    // GET: Search articles by keyword or title
    @GetMapping("/search")
    public String searchArticles(@RequestParam(required = false) String keyword, Model model) {
        List<Article> results;
        if (keyword != null && !keyword.trim().isEmpty()) {
            results = articleService.searchArticlesByTitle(keyword.trim());
            if (results.isEmpty()) {
                results = articleService.searchArticlesByKeyword(keyword.trim());
            }
        } else {
            results = articleService.getAllArticles();
        }
        model.addAttribute("articles", results);
        model.addAttribute("searchKeyword", keyword);
        return "knowledgebase/student-articles";
    }
}
