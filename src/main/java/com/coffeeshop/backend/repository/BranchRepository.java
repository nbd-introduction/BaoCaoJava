package com.coffeeshop.backend.repository;
import com.coffeeshop.backend.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface BranchRepository extends JpaRepository<Branch, Integer> {
    List<Branch> findByIsActiveTrue();
}