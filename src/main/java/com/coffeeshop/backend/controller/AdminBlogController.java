package com.coffeeshop.backend.controller;

import com.coffeeshop.backend.entity.*;
import com.coffeeshop.backend.repository.UserRepository;
import com.coffeeshop.backend.service.BlogService;
import com.coffeeshop.backend.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/blog")
@RequiredArgsConstructor
public class AdminBlogController {

    private final BlogService blogService;
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;

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
    public String save(@ModelAttribute BlogPost post,
                       @RequestParam(required = false) MultipartFile thumbnailFile,
                       Authentication auth) {
        try {
            // ✅ Upload thumbnail nếu có file mới
            if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                if (post.getThumbnailUrl() != null) {
                    fileUploadService.deleteFile(post.getThumbnailUrl());
                }
                String thumbnailUrl = fileUploadService.uploadImage(thumbnailFile);
                post.setThumbnailUrl(thumbnailUrl);
            }

            userRepository.findByEmail(auth.getName()).ifPresent(post::setAuthor);

            if (post.getSlug() == null || post.getSlug().isEmpty()) {
                post.setSlug("post-" + System.currentTimeMillis());
            }

            blogService.save(post);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/blog";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        blogService.findById(id).ifPresent(post -> {
            fileUploadService.deleteFile(post.getThumbnailUrl());
            blogService.deleteById(id);
        });
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
