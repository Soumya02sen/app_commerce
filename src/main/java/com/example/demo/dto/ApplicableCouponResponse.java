package com.example.demo.dto;

import java.math.BigDecimal;

import com.example.demo.model.CouponType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApplicableCouponResponse {
    private Long couponId;
    private CouponType type;
    private BigDecimal discountAmount;
}
