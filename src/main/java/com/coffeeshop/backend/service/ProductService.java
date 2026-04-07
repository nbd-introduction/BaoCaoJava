package com.coffeeshop.backend.service;
import com.coffeeshop.backend.entity.*;
import com.coffeeshop.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductSizeRepository productSizeRepository;

    public List<Product> findAll() { return productRepository.findAll(); }
    public Optional<Product> findById(Long id) { return productRepository.findById(id); }
    public Product save(Product product) { return productRepository.save(product); }
    public void deleteById(Long id) { productRepository.deleteById(id); }
    public List<Category> findAllCategories() { return categoryRepository.findAll(); }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    public void deleteCategoryById(Integer id) {
        categoryRepository.deleteById(id);
    }
    public List<Category> findActiveCategories() {
        return categoryRepository.findByIsActiveTrueOrderBySortOrder();
    }
}