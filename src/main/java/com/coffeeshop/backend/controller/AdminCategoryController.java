package com.coffeeshop.backend.controller;

import com.coffeeshop.backend.entity.Category;
import com.coffeeshop.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final ProductService productService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", productService.findAllCategories());
        model.addAttribute("activePage", "categories");
        return "admin/categories";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("activePage", "categories");
        return "admin/category-form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        model.addAttribute("category", productService.findAllCategories().stream()
                .filter(c -> c.getId().equals(id)).findFirst().orElseThrow());
        model.addAttribute("activePage", "categories");
        return "admin/category-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Category category) {
        productService.saveCategory(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        productService.deleteCategoryById(id);
        return "redirect:/admin/categories";
    }
}