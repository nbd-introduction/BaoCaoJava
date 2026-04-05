package com.coffeeshop.backend.repository;
import com.coffeeshop.backend.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByJobId(Long jobId);
    long countByStatus(JobApplication.Status status);
}