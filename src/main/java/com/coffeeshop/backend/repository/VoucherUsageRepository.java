package com.coffeeshop.backend.repository;

import com.coffeeshop.backend.entity.VoucherUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherUsageRepository extends JpaRepository<VoucherUsage, Long> {
    boolean existsByVoucherIdAndUserId(Long voucherId, Long userId);
}
