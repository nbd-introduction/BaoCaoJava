package com.coffeeshop.backend.controller;

import com.coffeeshop.backend.entity.User;
import com.coffeeshop.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*") // Cho phép Frontend gọi API
@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // --- CHỨC NĂNG 1: ĐĂNG KÝ ---
    @GetMapping("/auth/register")
    public String register(@RequestParam String username, @RequestParam String password,
                           @RequestParam String email, @RequestParam String name) {
        if (userRepository.findByUsername(username) != null) {
            return "Lỗi: Tên đăng nhập đã tồn tại!";
        }

        // 2. Kiểm tra trùng Email (Thêm đoạn này vào)
        if (userRepository.findByEmail(email) != null) {
            return "Lỗi: Email này đã được sử dụng!";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setName(name);

        userRepository.save(user);
        return "Đăng ký thành công cho: " + username;
    }

    // --- CHỨC NĂNG 2: ĐĂNG NHẬP ---
    @GetMapping("/auth/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) return "Đăng nhập thành công!";
        return "Lỗi: Sai tài khoản hoặc mật khẩu!";
    }

    // --- CHỨC NĂNG 3: ĐẶT HÀNG ---
    @PostMapping("/orders/create")
    public String placeOrder(@RequestParam String username, @RequestParam String productName) {
        return "Xác nhận: " + username + " đã đặt món " + productName;
    }
}