package com.example.demo.strategy;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.example.demo.dto.Cart;
import com.example.demo.model.CartWiseCoupon;
import com.example.demo.model.Coupon;
import com.example.demo.model.CouponType;

@Component
public class CartWiseCouponStrategy implements CouponStrategy {

    @Override
    public CouponType getCouponType() {
        return CouponType.CART_WISE;
    }

    @Override
    public boolean isApplicable(Cart cart, Coupon coupon) {
        CartWiseCoupon cartWiseCoupon = (CartWiseCoupon) coupon;
        return cart.getTotalOriginalPrice().compareTo(cartWiseCoupon.getThreshold()) >= 0;
    }

    @Override
    public BigDecimal calculateDiscount(Cart cart, Coupon coupon) {
        CartWiseCoupon cartWiseCoupon = (CartWiseCoupon) coupon;
        BigDecimal discount = cart.getTotalOriginalPrice()
                .multiply(cartWiseCoupon.getDiscountPercentage())
                .divide(BigDecimal.valueOf(100));
        if (cartWiseCoupon.getMaxDiscountAmount() != null && discount.compareTo(cartWiseCoupon.getMaxDiscountAmount()) > 0) {
            discount = cartWiseCoupon.getMaxDiscountAmount();
        }
        return discount;
    }

    @Override
    public Cart applyDiscount(Cart cart, Coupon coupon) {
        BigDecimal discount = calculateDiscount(cart, coupon);
        cart.setTotalDiscount(discount);
        cart.setFinalPrice(cart.getTotalOriginalPrice().subtract(discount));
        return cart;
    }
}
