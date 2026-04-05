package com.coffeeshop.backend.service;

import com.coffeeshop.backend.entity.Banner;
import com.coffeeshop.backend.repository.BannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BannerService {
    private final BannerRepository bannerRepository;

    public List<Banner> findAll() { return bannerRepository.findAll(); }
    public Optional<Banner> findById(Integer id) { return bannerRepository.findById(id); }
    public Banner save(Banner banner) { return bannerRepository.save(banner); }
    public void deleteById(Integer id) { bannerRepository.deleteById(id); }
}