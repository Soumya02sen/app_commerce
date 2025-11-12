package com.example.demo.strategy;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.demo.dto.Cart;
import com.example.demo.dto.CartItem;
import com.example.demo.model.CartWiseCoupon;
import com.example.demo.model.CouponType;

class CartWiseCouponStrategyTest {

    private CartWiseCouponStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new CartWiseCouponStrategy();
    }

    @Test
    void getCouponType_shouldReturnCartWise() {
        assertEquals(CouponType.CART_WISE, strategy.getCouponType());
    }

    @Test
    void isApplicable_shouldReturnTrue_whenCartTotalMeetsThreshold() {
        Cart cart = Cart.builder()
                .items(Collections.singletonList(CartItem.builder().productId(1L).quantity(1).price(BigDecimal.valueOf(100)).build()))
                .totalOriginalPrice(BigDecimal.valueOf(100))
                .build();
        CartWiseCoupon coupon = CartWiseCoupon.builder()
                .threshold(BigDecimal.valueOf(50))
                .build();
        assertTrue(strategy.isApplicable(cart, coupon));
    }

    @Test
    void isApplicable_shouldReturnFalse_whenCartTotalBelowThreshold() {
        Cart cart = Cart.builder()
                .items(Collections.singletonList(CartItem.builder().productId(1L).quantity(1).price(BigDecimal.valueOf(40)).build()))
                .totalOriginalPrice(BigDecimal.valueOf(40))
                .build();
        CartWiseCoupon coupon = CartWiseCoupon.builder()
                .threshold(BigDecimal.valueOf(50))
                .build();
        assertFalse(strategy.isApplicable(cart, coupon));
    }

    @Test
    void calculateDiscount_shouldReturnCorrectDiscount_withoutMaxAmount() {
        Cart cart = Cart.builder()
                .items(Collections.singletonList(CartItem.builder().productId(1L).quantity(1).price(BigDecimal.valueOf(100)).build()))
                .totalOriginalPrice(BigDecimal.valueOf(100))
                .build();
        CartWiseCoupon coupon = CartWiseCoupon.builder()
                .threshold(BigDecimal.valueOf(50))
                .discountPercentage(BigDecimal.valueOf(10))
                .build();
        assertEquals(0, BigDecimal.valueOf(10).compareTo(strategy.calculateDiscount(cart, coupon)));
    }

    @Test
    void calculateDiscount_shouldReturnMaxDiscount_whenDiscountExceedsMaxAmount() {
        Cart cart = Cart.builder()
                .items(Collections.singletonList(CartItem.builder().productId(1L).quantity(1).price(BigDecimal.valueOf(200)).build()))
                .totalOriginalPrice(BigDecimal.valueOf(200))
                .build();
        CartWiseCoupon coupon = CartWiseCoupon.builder()
                .threshold(BigDecimal.valueOf(50))
                .discountPercentage(BigDecimal.valueOf(10))
                .maxDiscountAmount(BigDecimal.valueOf(15))
                .build();
        assertEquals(0, BigDecimal.valueOf(15).compareTo(strategy.calculateDiscount(cart, coupon)));
    }

    @Test
    void applyDiscount_shouldUpdateCartCorrectly() {
        Cart cart = Cart.builder()
                .items(Collections.singletonList(CartItem.builder().productId(1L).quantity(1).price(BigDecimal.valueOf(100)).build()))
                .totalOriginalPrice(BigDecimal.valueOf(100))
                .totalDiscount(BigDecimal.ZERO)
                .finalPrice(BigDecimal.valueOf(100))
                .build();
        CartWiseCoupon coupon = CartWiseCoupon.builder()
                .threshold(BigDecimal.valueOf(50))
                .discountPercentage(BigDecimal.valueOf(10))
                .build();

        Cart updatedCart = strategy.applyDiscount(cart, coupon);

        assertEquals(0, BigDecimal.valueOf(10).compareTo(updatedCart.getTotalDiscount()));
        assertEquals(0, BigDecimal.valueOf(90).compareTo(updatedCart.getFinalPrice()));
    }
}
