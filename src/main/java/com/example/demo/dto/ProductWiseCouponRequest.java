package com.example.demo.dto;

import java.math.BigDecimal;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@SuperBuilder
public class ProductWiseCouponRequest extends CouponRequestDTO {
    private Long productId;
    private BigDecimal discountPercentage;
    private BigDecimal fixedDiscountAmount;
    private Integer minQuantity;
}
