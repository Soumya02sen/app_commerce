package com.example.demo.strategy;

import java.math.BigDecimal;

import com.example.demo.dto.Cart;
import com.example.demo.model.Coupon;
import com.example.demo.model.CouponType;

public interface CouponStrategy {
    CouponType getCouponType();
    boolean isApplicable(Cart cart, Coupon coupon);
    BigDecimal calculateDiscount(Cart cart, Coupon coupon);
    Cart applyDiscount(Cart cart, Coupon coupon);
}
