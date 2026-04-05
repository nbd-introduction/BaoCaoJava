package com.coffeeshop.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product_sizes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductSize {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Size size;

    @Column(nullable = false, precision = 10, scale = 0)
    private BigDecimal price;

    @Builder.Default
    private Integer stock = 0;

    public enum Size { S, M, L }
}