package com.coffeeshop.backend.service;
import com.coffeeshop.backend.entity.User;
import com.coffeeshop.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final UserRepository userRepository;

    public List<User> findAllCustomers() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == User.Role.CUSTOMER)
                .collect(Collectors.toList());
    }

    public List<User> findAllStaff() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == User.Role.STAFF)
                .collect(Collectors.toList());
    }

    public long countCustomers() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == User.Role.CUSTOMER).count();
    }

    public Optional<User> findById(Long id) { return userRepository.findById(id); }
    public void deleteById(Long id) { userRepository.deleteById(id); }
}