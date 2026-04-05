package com.coffeeshop.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders order;

    @ManyToOne
    @JoinColumn(name = "product_size_id")
    private ProductSize productSize;

    @Column(name = "product_name", length = 200)
    private String productName;

    @Enumerated(EnumType.STRING)
    private ProductSize.Size size;

    @Column(nullable = false, precision = 10, scale = 0)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 12, scale = 0)
    private BigDecimal subtotal;
}