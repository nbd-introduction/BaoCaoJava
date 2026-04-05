package com.coffeeshop.backend.controller;
import com.coffeeshop.backend.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/staff")
@RequiredArgsConstructor
public class AdminStaffController {
    private final CustomerService customerService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("staff", customerService.findAllStaff());
        return "admin/staff";
    }
}