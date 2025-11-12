package com.example.demo.dto;

import java.math.BigDecimal;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@SuperBuilder
public class CartWiseCouponRequest extends CouponRequestDTO {
    private BigDecimal threshold;
    private BigDecimal discountPercentage;
    private BigDecimal maxDiscountAmount;
}
