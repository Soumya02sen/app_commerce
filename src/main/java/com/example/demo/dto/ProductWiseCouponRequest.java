package com.example.demo.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductWiseCouponRequest implements CouponRequestDTO {
    private Long productId;
    private BigDecimal discountPercentage;
    private BigDecimal fixedDiscountAmount;
    private Integer minQuantity;
}
