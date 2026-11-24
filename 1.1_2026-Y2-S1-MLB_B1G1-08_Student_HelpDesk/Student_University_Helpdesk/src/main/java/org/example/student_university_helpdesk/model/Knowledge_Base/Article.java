package org.example.student_university_helpdesk.model.Knowledge_Base;

import jakarta.persistence.*;
import org.example.student_university_helpdesk.model.Users.Admin;

@Entity
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long articleId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "category")
    private String category;

    @Column(name = "keywords")
    private String keywords;

    // Relationship: Creates (Administrator 1 : N Article)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_admin_id", nullable = false)
    private Admin createdBy;

    public Article() {}

    public Article(String title, String content, String category, String keywords, Admin createdBy) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.keywords = keywords;
        this.createdBy = createdBy;
    }

    public Long getArticleId() { return articleId; }
    public void setArticleId(Long articleId) { this.articleId = articleId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }

    public Admin getCreatedBy() { return createdBy; }
    public void setCreatedBy(Admin createdBy) { this.createdBy = createdBy; }
}