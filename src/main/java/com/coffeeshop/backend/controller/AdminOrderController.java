package com.coffeeshop.backend.controller;

import com.coffeeshop.backend.entity.Orders;
import com.coffeeshop.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {
    private final OrderService orderService;

    @GetMapping
    public String list(Model model, @RequestParam(required = false) String status) {
        List<Orders> allOrders = orderService.findAll();
        List<Orders> filteredOrders = allOrders;

        if (status != null && !status.isEmpty()) {
            filteredOrders = allOrders.stream()
                    .filter(o -> o.getStatus().name().equals(status))
                    .collect(Collectors.toList());
        }

        model.addAttribute("orders", filteredOrders);
        model.addAttribute("statuses", Orders.Status.values());
        model.addAttribute("totalCount", allOrders.size());
        model.addAttribute("pendingCount", allOrders.stream()
                .filter(o -> o.getStatus() == Orders.Status.PENDING).count());
        model.addAttribute("deliveringCount", allOrders.stream()
                .filter(o -> o.getStatus() == Orders.Status.DELIVERING).count());
        model.addAttribute("doneCount", allOrders.stream()
                .filter(o -> o.getStatus() == Orders.Status.DONE).count());
        return "admin/orders";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderService.findById(id).orElseThrow());
        model.addAttribute("statuses", Orders.Status.values());
        return "admin/order-detail";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam String status) {
        orderService.updateStatus(id, Orders.Status.valueOf(status));
        return "redirect:/admin/orders/" + id;
    }
}