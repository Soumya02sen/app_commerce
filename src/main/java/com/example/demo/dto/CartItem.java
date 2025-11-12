package com.example.demo.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalDiscount = BigDecimal.ZERO;
    private BigDecimal finalPrice;
}
