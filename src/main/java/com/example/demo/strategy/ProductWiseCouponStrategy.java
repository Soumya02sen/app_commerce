package com.example.demo.strategy;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.example.demo.dto.Cart;
import com.example.demo.dto.CartItem;
import com.example.demo.model.Coupon;
import com.example.demo.model.CouponType;
import com.example.demo.model.ProductWiseCoupon;

@Component
public class ProductWiseCouponStrategy implements CouponStrategy {

    @Override
    public CouponType getCouponType() {
        return CouponType.PRODUCT_WISE;
    }

    @Override
    public boolean isApplicable(Cart cart, Coupon coupon) {
        ProductWiseCoupon productWiseCoupon = (ProductWiseCoupon) coupon;
        return cart.getItems().stream()
                .anyMatch(item -> item.getProductId().equals(productWiseCoupon.getProductId()) &&
                        (productWiseCoupon.getMinQuantity() == null || item.getQuantity() >= productWiseCoupon.getMinQuantity()));
    }

    @Override
    public BigDecimal calculateDiscount(Cart cart, Coupon coupon) {
        ProductWiseCoupon productWiseCoupon = (ProductWiseCoupon) coupon;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        for (CartItem item : cart.getItems()) {
            if (item.getProductId().equals(productWiseCoupon.getProductId())) {
                if (productWiseCoupon.getDiscountPercentage() != null) {
                    totalDiscount = totalDiscount.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                            .multiply(productWiseCoupon.getDiscountPercentage()).divide(BigDecimal.valueOf(100)));
                } else if (productWiseCoupon.getFixedDiscountAmount() != null) {
                    totalDiscount = totalDiscount.add(productWiseCoupon.getFixedDiscountAmount().multiply(BigDecimal.valueOf(item.getQuantity())));
                }
            }
        }
        return totalDiscount;
    }

    @Override
    public Cart applyDiscount(Cart cart, Coupon coupon) {
        ProductWiseCoupon productWiseCoupon = (ProductWiseCoupon) coupon;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        for (CartItem item : cart.getItems()) {
            if (item.getProductId().equals(productWiseCoupon.getProductId())) {
                BigDecimal discount = BigDecimal.ZERO;
                if (productWiseCoupon.getDiscountPercentage() != null) {
                    discount = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                            .multiply(productWiseCoupon.getDiscountPercentage()).divide(BigDecimal.valueOf(100));
                } else if (productWiseCoupon.getFixedDiscountAmount() != null) {
                    discount = productWiseCoupon.getFixedDiscountAmount().multiply(BigDecimal.valueOf(item.getQuantity()));
                }
                item.setTotalDiscount(discount);
                item.setFinalPrice(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())).subtract(discount));
                totalDiscount = totalDiscount.add(discount);
            } else {
                item.setFinalPrice(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }
        cart.setTotalDiscount(totalDiscount);
        cart.setFinalPrice(cart.getTotalOriginalPrice().subtract(totalDiscount));
        return cart;
    }
}
