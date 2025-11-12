package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.demo.model.CouponType;

import lombok.Data;

@Data
public class CartWiseCouponResponse implements CouponResponseDTO {
    private Long id;
    private CouponType type;
    private String code;
    private String description;
    private LocalDate expirationDate;
    private BigDecimal threshold;
    private BigDecimal discountPercentage;
    private BigDecimal maxDiscountAmount;
}
