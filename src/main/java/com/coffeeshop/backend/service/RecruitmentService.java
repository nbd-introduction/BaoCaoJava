package com.coffeeshop.backend.service;
import com.coffeeshop.backend.entity.*;
import com.coffeeshop.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecruitmentService {
    private final JobPositionRepository jobPositionRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final BranchRepository branchRepository;

    public List<JobPosition> findAll() { return jobPositionRepository.findAll(); }
    public Optional<JobPosition> findById(Long id) { return jobPositionRepository.findById(id); }
    public JobPosition save(JobPosition job) { return jobPositionRepository.save(job); }
    public void deleteById(Long id) { jobPositionRepository.deleteById(id); }
    public List<JobApplication> findApplicationsByJob(Long jobId) { return jobApplicationRepository.findByJobId(jobId); }
    public Optional<JobApplication> findApplicationById(Long id) { return jobApplicationRepository.findById(id); }
    public JobApplication saveApplication(JobApplication app) { return jobApplicationRepository.save(app); }
    public List<Branch> findAllBranches() { return branchRepository.findAll(); }
    public long countPendingApplications() { return jobApplicationRepository.countByStatus(JobApplication.Status.PENDING); }
}