package com.coffeeshop.backend.repository;
import com.coffeeshop.backend.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface OrderRepository extends JpaRepository<Orders, Long> {
    List<Orders> findAllByOrderByCreatedAtDesc();
    List<Orders> findByStatus(Orders.Status status);
    Optional<Orders> findByOrderCode(String orderCode);
    long countByStatus(Orders.Status status);
}