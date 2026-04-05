package com.coffeeshop.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "job_positions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class JobPosition {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @Column(columnDefinition = "TEXT")
    private String benefits;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type")
    @Builder.Default
    private JobType jobType = JobType.FULL_TIME;

    @Column(name = "salary_range", length = 100)
    private String salaryRange;

    @Builder.Default
    private Integer slots = 1;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.OPEN;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<JobApplication> applications;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum JobType { FULL_TIME, PART_TIME, INTERN }
    public enum Status { OPEN, CLOSED, PAUSED }
}