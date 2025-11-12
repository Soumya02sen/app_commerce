package com.example.demo.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CartWiseCouponRequest implements CouponRequestDTO {
    private BigDecimal threshold;
    private BigDecimal discountPercentage;
    private BigDecimal maxDiscountAmount;
}
