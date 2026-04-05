package com.coffeeshop.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "blog_categories")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BlogCategory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, length = 120)
    private String slug;

    @Column(length = 100)
    private String icon;
}