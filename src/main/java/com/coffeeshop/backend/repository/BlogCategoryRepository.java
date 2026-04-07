package com.coffeeshop.backend.repository;
import com.coffeeshop.backend.entity.BlogCategory;
import org.springframework.data.jpa.repository.JpaRepository;
public interface BlogCategoryRepository extends JpaRepository<BlogCategory, Integer> {}