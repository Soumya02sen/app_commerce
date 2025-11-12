package com.example.demo.model;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BxGyCoupon extends Coupon {
    private List<BxGyProductDetail> buyProducts;

    private List<BxGyProductDetail> getProducts;

    @NotNull
    @Min(value = 1)
    private Integer repetitionLimit;
}
