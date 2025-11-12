package com.example.demo.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@SuperBuilder
public class CartWiseCouponRequest extends CouponRequestDTO {
    private BigDecimal threshold;
    private BigDecimal discountPercentage;
    private BigDecimal maxDiscountAmount;
}
