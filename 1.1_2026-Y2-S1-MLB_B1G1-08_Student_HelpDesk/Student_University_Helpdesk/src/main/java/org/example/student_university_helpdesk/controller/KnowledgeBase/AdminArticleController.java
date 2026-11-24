package org.example.student_university_helpdesk.controller.KnowledgeBase;

import org.example.student_university_helpdesk.model.Knowledge_Base.Article;
import org.example.student_university_helpdesk.model.Users.Admin;
import org.example.student_university_helpdesk.repository.AdminRepository;
import org.example.student_university_helpdesk.service.ArticleService;
import org.example.student_university_helpdesk.service.SystemUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping({"/admin/articles", "/knowledgebase/admin-articles"})
public class AdminArticleController {

    private final ArticleService articleService;
    private final SystemUserService systemUserService;
    private final AdminRepository adminRepository;

    @Autowired
    public AdminArticleController(ArticleService articleService,
                                  SystemUserService systemUserService,
                                  AdminRepository adminRepository) {
        this.articleService = articleService;
        this.systemUserService = systemUserService;
        this.adminRepository = adminRepository;
    }

    // GET: Admin article & policy management view
    @GetMapping
    public String showAdminArticles(@RequestParam(required = false) Long editArticleId, Model model) {
        List<Article> articles = articleService.getAllArticles();
        model.addAttribute("articles", articles);
        if (editArticleId != null) {
            try {
                Article editArticle = articleService.getArticleById(editArticleId);
                model.addAttribute("editArticle", editArticle);
            } catch (Exception ignored) {}
        }
        return "knowledgebase/admin-articles";
    }

    // POST: Create and publish article
    @PostMapping("/create")
    public String createArticle(@RequestParam String title,
                                @RequestParam String content,
                                @RequestParam String category,
                                @RequestParam(required = false, defaultValue = "campus,policy,faq") String keywords,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        try {
            Long adminId = resolveAdminId(principal);
            articleService.createArticle(title.trim(), content.trim(), category.trim(), keywords.trim(), adminId);
            redirectAttributes.addFlashAttribute("successMessage", "Article '" + title.trim() + "' published successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error publishing article: " + e.getMessage());
        }
        return "redirect:/admin/articles";
    }

    // POST: Update existing article
    @PostMapping("/update")
    public String updateArticle(@RequestParam Long articleId,
                                @RequestParam String title,
                                @RequestParam String content,
                                @RequestParam String category,
                                @RequestParam(required = false, defaultValue = "campus,policy,faq") String keywords,
                                RedirectAttributes redirectAttributes) {
        try {
            articleService.updateArticle(articleId, title.trim(), content.trim(), category.trim(), keywords.trim());
            redirectAttributes.addFlashAttribute("successMessage", "Article '" + title.trim() + "' updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating article: " + e.getMessage());
        }
        return "redirect:/admin/articles";
    }

    // POST: Delete or archive article
    @PostMapping("/delete")
    public String deleteArticle(@RequestParam Long articleId, RedirectAttributes redirectAttributes) {
        try {
            articleService.deleteArticle(articleId);
            redirectAttributes.addFlashAttribute("successMessage", "Article removed successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error removing article: " + e.getMessage());
        }
        return "redirect:/admin/articles";
    }

    private Long resolveAdminId(Principal principal) {
        if (principal != null) {
            String identifier = principal.getName();
            Optional<Admin> adminOpt = adminRepository.findByUsername(identifier)
                    .or(() -> adminRepository.findByEmail(identifier));
            if (adminOpt.isPresent()) {
                return adminOpt.get().getUserId();
            }
        }
        return adminRepository.findAll().stream()
                .findFirst()
                .map(Admin::getUserId)
                .orElse(1L);
    }
}
