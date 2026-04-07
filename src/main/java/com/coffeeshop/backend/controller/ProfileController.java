package com.coffeeshop.backend.controller;

import com.coffeeshop.backend.entity.Orders;
import com.coffeeshop.backend.repository.UserRepository;
import com.coffeeshop.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;
    private final OrderService orderService;

    @GetMapping
    public String profile(Authentication auth, Model model) {
        var user = userRepository.findByEmail(auth.getName()).orElseThrow();

        List<Orders> orders = orderService.findAll().stream()
                .filter(o -> o.getUser() != null
                        && o.getUser().getId().equals(user.getId()))
                .toList();

        // ✅ Tính sẵn stats - KHÔNG dùng stream() trong Thymeleaf
        long totalOrders = orders.size();
        long doneOrders = orders.stream()
                .filter(o -> o.getStatus() == Orders.Status.DONE).count();
        long cancelledOrders = orders.stream()
                .filter(o -> o.getStatus() == Orders.Status.CANCELLED).count();

        model.addAttribute("user", user);
        model.addAttribute("orders", orders);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("doneOrders", doneOrders);
        model.addAttribute("cancelledOrders", cancelledOrders);
        return "user/profile";
    }

    @GetMapping("/orders")
    public String orders(Authentication auth, Model model) {
        return profile(auth, model);
    }
}
