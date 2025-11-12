package com.example.demo.model;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class CartWiseCoupon extends Coupon {
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal threshold;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal discountPercentage;

    private BigDecimal maxDiscountAmount;
}
