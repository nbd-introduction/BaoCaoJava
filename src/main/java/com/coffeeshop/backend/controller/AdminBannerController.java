package com.coffeeshop.backend.controller;

import com.coffeeshop.backend.entity.Banner;
import com.coffeeshop.backend.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/banners")
@RequiredArgsConstructor
public class AdminBannerController {
    private final BannerService bannerService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("banners", bannerService.findAll());
        model.addAttribute("activePage", "banners");
        return "admin/banners";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("banner", new Banner());
        model.addAttribute("activePage", "banners");
        return "admin/banner-form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        model.addAttribute("banner", bannerService.findById(id).orElseThrow());
        model.addAttribute("activePage", "banners");
        return "admin/banner-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Banner banner) {
        bannerService.save(banner);
        return "redirect:/admin/banners";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        bannerService.deleteById(id);
        return "redirect:/admin/banners";
    }

    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Integer id) {
        bannerService.findById(id).ifPresent(b -> {
            b.setIsActive(!b.getIsActive());
            bannerService.save(b);
        });
        return "redirect:/admin/banners";
    }
}