package com.coffeeshop.backend.controller;

import com.coffeeshop.backend.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/blog")
@RequiredArgsConstructor
public class BlogPublicController {

    private final BlogService blogService;

    @GetMapping
    public String list(@RequestParam(required = false) String type, Model model) {
        model.addAttribute("categories", blogService.findAllCategories());
        model.addAttribute("selectedType", type);
        if (type != null && !type.isEmpty()) {
            model.addAttribute("posts", blogService.findAll().stream()
                    .filter(p -> p.getStatus().name().equals("PUBLISHED")
                            && p.getType().name().equals(type)).toList());
        } else {
            model.addAttribute("posts", blogService.findAll().stream()
                    .filter(p -> p.getStatus().name().equals("PUBLISHED")).toList());
        }
        return "user/blog";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("post", blogService.findById(id).orElseThrow());
        model.addAttribute("relatedPosts", blogService.findAll().stream()
                .filter(p -> p.getStatus().name().equals("PUBLISHED"))
                .limit(3).toList());
        return "user/blog-detail";
    }
}