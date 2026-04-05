package com.coffeeshop.backend.controller;

import com.coffeeshop.backend.entity.Orders;
import com.coffeeshop.backend.repository.OrderRepository;
import com.coffeeshop.backend.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final OrderService orderService;
    private final CustomerService customerService;
    private final ProductService productService;
    private final RecruitmentService recruitmentService;
    private final OrderRepository orderRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Orders> allOrders = orderService.findAll();

        // STATS
        long totalOrders = allOrders.size();
        long pendingOrders = allOrders.stream()
                .filter(o -> o.getStatus() == Orders.Status.PENDING).count();
        long totalCustomers = customerService.countCustomers();
        long totalProducts = productService.findAll().size();

        // DOANH THU HÔM NAY
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        BigDecimal revenueToday = allOrders.stream()
                .filter(o -> o.getStatus() == Orders.Status.DONE)
                .filter(o -> o.getCreatedAt() != null && o.getCreatedAt().isAfter(startOfDay))
                .map(Orders::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // TỔNG DOANH THU
        BigDecimal totalRevenue = allOrders.stream()
                .filter(o -> o.getStatus() == Orders.Status.DONE)
                .map(Orders::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // DOANH THU 7 NGÀY (cho biểu đồ)
        List<String> chartLabels = new ArrayList<>();
        List<BigDecimal> chartData = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");

        for (int i = 6; i >= 0; i--) {
            LocalDateTime dayStart = LocalDateTime.now().minusDays(i).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime dayEnd = dayStart.plusDays(1);
            String label = dayStart.format(fmt);

            BigDecimal dayRevenue = allOrders.stream()
                    .filter(o -> o.getStatus() == Orders.Status.DONE)
                    .filter(o -> o.getCreatedAt() != null
                            && o.getCreatedAt().isAfter(dayStart)
                            && o.getCreatedAt().isBefore(dayEnd))
                    .map(Orders::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            chartLabels.add(label);
            chartData.add(dayRevenue);
        }

        // ĐƠN HÀNG GẦN NHẤT (10 cái)
        List<Orders> recentOrders = allOrders.stream().limit(10).collect(Collectors.toList());

        // ĐẾM THEO TRẠNG THÁI
        Map<String, Long> orderStats = new LinkedHashMap<>();
        orderStats.put("PENDING", allOrders.stream().filter(o -> o.getStatus() == Orders.Status.PENDING).count());
        orderStats.put("CONFIRMED", allOrders.stream().filter(o -> o.getStatus() == Orders.Status.CONFIRMED).count());
        orderStats.put("PREPARING", allOrders.stream().filter(o -> o.getStatus() == Orders.Status.PREPARING).count());
        orderStats.put("DELIVERING", allOrders.stream().filter(o -> o.getStatus() == Orders.Status.DELIVERING).count());
        orderStats.put("DONE", allOrders.stream().filter(o -> o.getStatus() == Orders.Status.DONE).count());
        orderStats.put("CANCELLED", allOrders.stream().filter(o -> o.getStatus() == Orders.Status.CANCELLED).count());

        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("revenueToday", revenueToday);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("recentOrders", recentOrders);
        model.addAttribute("chartLabels", chartLabels);
        model.addAttribute("chartData", chartData);
        model.addAttribute("orderStats", orderStats);
        model.addAttribute("pendingApplications", recruitmentService.countPendingApplications());
        model.addAttribute("activePage", "dashboard");

        return "dashboard";
    }
}