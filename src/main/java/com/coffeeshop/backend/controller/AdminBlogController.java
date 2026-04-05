package com.coffeeshop.backend.controller;

import com.coffeeshop.backend.entity.*;
import com.coffeeshop.backend.repository.UserRepository;
import com.coffeeshop.backend.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/blog")
@RequiredArgsConstructor
public class AdminBlogController {
    private final BlogService blogService;
    private final UserRepository userRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("posts", blogService.findAll());
        return "admin/blog";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("post", new BlogPost());
        model.addAttribute("categories", blogService.findAllCategories());
        model.addAttribute("branches", blogService.findAllBranches());
        model.addAttribute("types", BlogPost.Type.values());
        return "admin/blog-form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("post", blogService.findById(id).orElseThrow());
        model.addAttribute("categories", blogService.findAllCategories());
        model.addAttribute("branches", blogService.findAllBranches());
        model.addAttribute("types", BlogPost.Type.values());
        return "admin/blog-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute BlogPost post, Authentication auth) {
        userRepository.findByEmail(auth.getName()).ifPresent(post::setAuthor);
        if (post.getSlug() == null || post.getSlug().isEmpty()) {
            post.setSlug("post-" + System.currentTimeMillis());
        }
        blogService.save(post);
        return "redirect:/admin/blog";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        blogService.deleteById(id);
        return "redirect:/admin/blog";
    }

    @GetMapping("/publish/{id}")
    public String publish(@PathVariable Long id) {
        blogService.findById(id).ifPresent(post -> {
            post.setStatus(BlogPost.Status.PUBLISHED);
            post.setPublishedAt(LocalDateTime.now());
            blogService.save(post);
        });
        return "redirect:/admin/blog";
    }
}