package com.coffeeshop.backend.repository;
import com.coffeeshop.backend.entity.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    List<BlogPost> findAllByOrderByCreatedAtDesc();
}