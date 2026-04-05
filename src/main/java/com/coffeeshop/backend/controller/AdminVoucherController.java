package com.coffeeshop.backend.controller;
import com.coffeeshop.backend.entity.Voucher;
import com.coffeeshop.backend.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/vouchers")
@RequiredArgsConstructor
public class AdminVoucherController {
    private final VoucherService voucherService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("vouchers", voucherService.findAll());
        return "admin/vouchers";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("voucher", new Voucher());
        model.addAttribute("discountTypes", Voucher.DiscountType.values());
        return "admin/voucher-form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("voucher", voucherService.findById(id).orElseThrow());
        model.addAttribute("discountTypes", Voucher.DiscountType.values());
        return "admin/voucher-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Voucher voucher) {
        voucherService.save(voucher);
        return "redirect:/admin/vouchers";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        voucherService.deleteById(id);
        return "redirect:/admin/vouchers";
    }
}