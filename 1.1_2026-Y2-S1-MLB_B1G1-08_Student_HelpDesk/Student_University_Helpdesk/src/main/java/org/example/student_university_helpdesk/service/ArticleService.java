package org.example.student_university_helpdesk.service;

import org.example.student_university_helpdesk.model.Knowledge_Base.Article;
import org.example.student_university_helpdesk.model.Users.Admin;
import org.example.student_university_helpdesk.repository.AdminRepository;
import org.example.student_university_helpdesk.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public ArticleService(ArticleRepository articleRepository, AdminRepository adminRepository) {
        this.articleRepository = articleRepository;
        this.adminRepository = adminRepository;
    }

    // ==========================================
    // ADMIN / MANAGEMENT OPERATIONS
    // ==========================================

    @Transactional
    public Article createArticle(String title, String content, String category, String keywords, Long adminId) {
        Admin admin = (adminId != null ? adminRepository.findById(adminId) : java.util.Optional.<Admin>empty())
                .or(() -> adminRepository.findAll().stream().findFirst())
                .orElseThrow(() -> new RuntimeException("No admin account found to publish article."));

        Article article = new Article(title, content, category, keywords, admin);
        return articleRepository.save(article);
    }

    @Transactional
    public Article updateArticle(Long articleId, String title, String content, String category, String keywords) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found with ID: " + articleId));

        article.setTitle(title);
        article.setContent(content);
        article.setCategory(category);
        article.setKeywords(keywords);

        return articleRepository.save(article);
    }

    @Transactional
    public void deleteArticle(Long articleId) {
        if (!articleRepository.existsById(articleId)) {
            throw new RuntimeException("Article not found with ID: " + articleId);
        }
        articleRepository.deleteById(articleId);
    }

    // ==========================================
    // USER / SEARCH OPERATIONS
    // ==========================================

    @Transactional(readOnly = true)
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Article getArticleById(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found with ID: " + articleId));
    }

    @Transactional(readOnly = true)
    public List<Article> getArticlesByCategory(String category) {
        return articleRepository.findByCategory(category);
    }

    @Transactional(readOnly = true)
    public List<Article> searchArticlesByTitle(String titleQuery) {
        return articleRepository.findByTitleContainingIgnoreCase(titleQuery);
    }

    @Transactional(readOnly = true)
    public List<Article> searchArticlesByKeyword(String keyword) {
        return articleRepository.findByKeywordsContaining(keyword);
    }

    @Transactional(readOnly = true)
    public List<Article> getArticlesByAdmin(Long adminId) {
        return articleRepository.findByCreatedBy_UserId(adminId);
    }
}