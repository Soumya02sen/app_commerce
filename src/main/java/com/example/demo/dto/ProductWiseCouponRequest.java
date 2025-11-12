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
public class ProductWiseCouponRequest extends CouponRequestDTO {
    private Long productId;
    private BigDecimal discountPercentage;
    private BigDecimal fixedDiscountAmount;
    private Integer minQuantity;
}
