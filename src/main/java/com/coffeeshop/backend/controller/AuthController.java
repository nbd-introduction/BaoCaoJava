package com.coffeeshop.backend.controller;

import com.coffeeshop.backend.dto.request.RegisterRequest;
import com.coffeeshop.backend.entity.User;
import com.coffeeshop.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    // =================== LOGIN ===================
    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        if (error != null) model.addAttribute("error", "Email hoặc mật khẩu không đúng!");
        if (logout != null) model.addAttribute("logout", true);
        return "auth/login";
    }

    // =================== REGISTER ===================
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("formData", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           @RequestParam(required = false) String phone,
                           Model model) {
        // Kiểm tra mật khẩu khớp
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp!");
            RegisterRequest formData = new RegisterRequest();
            formData.setName(name);
            formData.setEmail(email);
            formData.setPhone(phone);
            model.addAttribute("formData", formData);
            return "auth/register";
        }

        // Kiểm tra email đã tồn tại
        if (userRepository.existsByEmail(email.toLowerCase())) {
            model.addAttribute("error", "Email này đã được đăng ký! Vui lòng dùng email khác.");
            RegisterRequest formData = new RegisterRequest();
            formData.setName(name);
            formData.setEmail(email);
            formData.setPhone(phone);
            model.addAttribute("formData", formData);
            return "auth/register";
        }

        // Kiểm tra độ dài mật khẩu
        if (password.length() < 6) {
            model.addAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự!");
            RegisterRequest formData = new RegisterRequest();
            formData.setName(name);
            formData.setEmail(email);
            formData.setPhone(phone);
            model.addAttribute("formData", formData);
            return "auth/register";
        }

        // Tạo user mới
        User newUser = User.builder()
                .name(name.trim())
                .email(email.toLowerCase().trim())
                .password(password)
                .phone(phone)
                .role(User.Role.CUSTOMER)
                .loyaltyPoints(0)
                .isActive(true)
                .build();

        userRepository.save(newUser);

        model.addAttribute("success", true);
        return "auth/register";
    }
}
