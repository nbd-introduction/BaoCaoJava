package com.coffeeshop.backend.repository;
import com.coffeeshop.backend.entity.JobPosition;
import org.springframework.data.jpa.repository.JpaRepository;
public interface JobPositionRepository extends JpaRepository<JobPosition, Long> {}