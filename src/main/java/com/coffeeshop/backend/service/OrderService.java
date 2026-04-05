package com.coffeeshop.backend.service;
import com.coffeeshop.backend.entity.Orders;
import com.coffeeshop.backend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public List<Orders> findAll() { return orderRepository.findAllByOrderByCreatedAtDesc(); }
    public Optional<Orders> findById(Long id) { return orderRepository.findById(id); }
    public long countPending() { return orderRepository.countByStatus(Orders.Status.PENDING); }

    public void updateStatus(Long id, Orders.Status status) {
        orderRepository.findById(id).ifPresent(o -> {
            o.setStatus(status);
            orderRepository.save(o);
        });
    }
}