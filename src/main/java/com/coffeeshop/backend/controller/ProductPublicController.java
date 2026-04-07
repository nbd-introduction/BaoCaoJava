package com.coffeeshop.backend.controller;

import com.coffeeshop.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductPublicController {

    private final ProductService productService;

    @GetMapping
    public String list(@RequestParam(required = false) Integer category, Model model) {
        model.addAttribute("categories", productService.findActiveCategories());
        model.addAttribute("selectedCategory", category);
        if (category != null) {
            model.addAttribute("products", productService.findAll().stream()
                    .filter(p -> Boolean.TRUE.equals(p.getIsAvailable())
                            && p.getCategory() != null
                            && p.getCategory().getId().equals(category)).toList());
        } else {
            model.addAttribute("products", productService.findAll().stream()
                    .filter(p -> Boolean.TRUE.equals(p.getIsAvailable())).toList());
        }
        return "user/products";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.findById(id).orElseThrow());
        model.addAttribute("related", productService.findAll().stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsAvailable())).limit(4).toList());
        return "user/product-detail";
    }
}