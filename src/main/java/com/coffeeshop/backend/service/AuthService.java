package com.coffeeshop.backend.service;

import com.coffeeshop.backend.dto.request.RegisterRequest;
import com.coffeeshop.backend.entity.User;
import com.coffeeshop.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại!");
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail().toLowerCase())
                .password(request.getPassword())  // ✅ Sửa passwordHash → password
                .phone(request.getPhone())
                .role(User.Role.CUSTOMER)
                .build();
        userRepository.save(user);
    }
}