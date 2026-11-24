package org.example.student_university_helpdesk.repository;

import org.example.student_university_helpdesk.model.Knowledge_Base.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    //Filter articles by category
    List<Article> findByCategory(String category);

    //Search articles matching keywords
    List<Article> findByKeywordsContaining(String keyword);

    //Search articles by title (case-insensitive search for knowledge base queries)
    List<Article> findByTitleContainingIgnoreCase(String title);

    // Find all articles published by a specific Admin (navigates createdBy.adminId)
    List<Article> findByCreatedBy_UserId(Long adminId);
}