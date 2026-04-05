package com.coffeeshop.backend.repository;
import com.coffeeshop.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByIsActiveTrueOrderBySortOrder();
}