package com.coffeeshop.backend.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CartItem {
    private Long productSizeId;
    private Long productId;
    private String productName;
    private String productImage;
    private String size;
    private BigDecimal price;
    private Integer quantity;

    public BigDecimal getSubtotal() {
        if (price == null || quantity == null) return BigDecimal.ZERO;
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
