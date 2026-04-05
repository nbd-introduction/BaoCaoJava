package com.coffeeshop.backend.repository;

import com.coffeeshop.backend.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Integer> {
    List<Banner> findByIsActiveTrueOrderBySortOrder();
}