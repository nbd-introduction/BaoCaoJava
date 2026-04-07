package com.coffeeshop.backend.service;
import com.coffeeshop.backend.entity.*;
import com.coffeeshop.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogService {
    private final BlogPostRepository blogPostRepository;
    private final BlogCategoryRepository blogCategoryRepository;
    private final BranchRepository branchRepository;

    public List<BlogPost> findAll() { return blogPostRepository.findAllByOrderByCreatedAtDesc(); }
    public Optional<BlogPost> findById(Long id) { return blogPostRepository.findById(id); }
    public BlogPost save(BlogPost post) { return blogPostRepository.save(post); }
    public void deleteById(Long id) { blogPostRepository.deleteById(id); }
    public List<BlogCategory> findAllCategories() { return blogCategoryRepository.findAll(); }
    public List<Branch> findAllBranches() { return branchRepository.findAll(); }
}