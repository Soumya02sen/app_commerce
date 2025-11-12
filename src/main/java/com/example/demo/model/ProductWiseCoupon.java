package com.example.demo.model;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class ProductWiseCoupon extends Coupon {
    @NotNull
    private Long productId;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal discountPercentage;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal fixedDiscountAmount;

    @Min(value = 1)
    private Integer minQuantity;
}
