package com.coffeeshop.backend.service;
import com.coffeeshop.backend.entity.Branch;
import com.coffeeshop.backend.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BranchService {
    private final BranchRepository branchRepository;

    public List<Branch> findAll() { return branchRepository.findAll(); }
    public Optional<Branch> findById(Integer id) { return branchRepository.findById(id); }
    public Branch save(Branch branch) { return branchRepository.save(branch); }
    public void deleteById(Integer id) { branchRepository.deleteById(id); }
}