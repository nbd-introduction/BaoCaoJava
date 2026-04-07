package com.coffeeshop.backend.controller;

import com.coffeeshop.backend.entity.Voucher;
import com.coffeeshop.backend.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;
    private final BlogService blogService;
    private final BranchService branchService;
    private final BannerService bannerService;
    private final VoucherService voucherService;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        // Banners
        model.addAttribute("banners", bannerService.findAll().stream()
                .filter(b -> Boolean.TRUE.equals(b.getIsActive())).toList());

        // Categories
        model.addAttribute("categories", productService.findActiveCategories());

        // Featured products
        model.addAttribute("featuredProducts", productService.findAll().stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsAvailable())).limit(8).toList());

        // Latest blog posts
        model.addAttribute("latestPosts", blogService.findAll().stream()
                .filter(p -> p.getStatus().name().equals("PUBLISHED")).limit(3).toList());

        // Branches
        model.addAttribute("branches", branchService.findActive());

        // ✅ Vouchers đang hoạt động - hiển thị ở trang chủ
        List<Voucher> activeVouchers = voucherService.findAll().stream()
                .filter(v -> Boolean.TRUE.equals(v.getIsActive()))
                .filter(v -> v.getUsedCount() < v.getMaxUses())
                .filter(v -> v.getExpiresAt() == null
                        || v.getExpiresAt().isAfter(LocalDateTime.now()))
                .toList();
        model.addAttribute("vouchers", activeVouchers);

        return "user/home";
    }
}
