package com.coffeeshop.backend.controller;

import com.coffeeshop.backend.entity.Banner;
import com.coffeeshop.backend.service.BannerService;
import com.coffeeshop.backend.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/banners")
@RequiredArgsConstructor
public class AdminBannerController {

    private final BannerService bannerService;
    private final FileUploadService fileUploadService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("banners", bannerService.findAll());
        return "admin/banners";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("banner", new Banner());
        return "admin/banner-form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        model.addAttribute("banner", bannerService.findById(id).orElseThrow());
        return "admin/banner-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Banner banner,
                       @RequestParam(required = false) MultipartFile imageFile) {
        try {
            // ✅ Upload ảnh nếu có file mới
            if (imageFile != null && !imageFile.isEmpty()) {
                if (banner.getImageUrl() != null) {
                    fileUploadService.deleteFile(banner.getImageUrl());
                }
                String imageUrl = fileUploadService.uploadImage(imageFile);
                banner.setImageUrl(imageUrl);
            }
            bannerService.save(banner);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/banners";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        bannerService.findById(id).ifPresent(b -> {
            fileUploadService.deleteFile(b.getImageUrl());
            bannerService.deleteById(id);
        });
        return "redirect:/admin/banners";
    }

    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Integer id) {
        bannerService.findById(id).ifPresent(b -> {
            b.setIsActive(!Boolean.TRUE.equals(b.getIsActive()));
            bannerService.save(b);
        });
        return "redirect:/admin/banners";
    }
}
