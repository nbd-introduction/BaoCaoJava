package com.coffeeshop.backend.service;
import com.coffeeshop.backend.entity.Voucher;
import com.coffeeshop.backend.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoucherService {
    private final VoucherRepository voucherRepository;

    public List<Voucher> findAll() { return voucherRepository.findAll(); }
    public Optional<Voucher> findById(Long id) { return voucherRepository.findById(id); }
    public Voucher save(Voucher voucher) { return voucherRepository.save(voucher); }
    public void deleteById(Long id) { voucherRepository.deleteById(id); }
}