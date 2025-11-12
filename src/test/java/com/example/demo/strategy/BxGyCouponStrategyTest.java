package com.example.demo.strategy;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.demo.dto.Cart;
import com.example.demo.dto.CartItem;
import com.example.demo.model.BxGyCoupon;
import com.example.demo.model.BxGyProductDetail;
import com.example.demo.model.CouponType;

class BxGyCouponStrategyTest {

    private BxGyCouponStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new BxGyCouponStrategy();
    }

    @Test
    void getCouponType_shouldReturnBxGy() {
        assertEquals(CouponType.BXGY, strategy.getCouponType());
    }

    @Test
    void isApplicable_shouldReturnTrue_whenCartMeetsBuyAndGetRequirements() {
        CartItem item1 = CartItem.builder().productId(1L).quantity(2).price(BigDecimal.valueOf(50)).build();
        CartItem item2 = CartItem.builder().productId(2L).quantity(1).price(BigDecimal.valueOf(100)).build();
        Cart cart = Cart.builder().items(Arrays.asList(item1, item2)).build();

        BxGyProductDetail buy1 = BxGyProductDetail.builder().productId(1L).quantity(2).build();
        BxGyProductDetail get1 = BxGyProductDetail.builder().productId(2L).quantity(1).build();
        BxGyCoupon coupon = BxGyCoupon.builder()
                .buyProducts(Collections.singletonList(buy1))
                .getProducts(Collections.singletonList(get1))
                .repetitionLimit(1)
                .build();
        assertTrue(strategy.isApplicable(cart, coupon));
    }

    @Test
    void isApplicable_shouldReturnFalse_whenCartDoesNotMeetBuyRequirements() {
        CartItem item1 = CartItem.builder().productId(1L).quantity(1).price(BigDecimal.valueOf(50)).build();
        CartItem item2 = CartItem.builder().productId(2L).quantity(1).price(BigDecimal.valueOf(100)).build();
        Cart cart = Cart.builder().items(Arrays.asList(item1, item2)).build();

        BxGyProductDetail buy1 = BxGyProductDetail.builder().productId(1L).quantity(2).build();
        BxGyProductDetail get1 = BxGyProductDetail.builder().productId(2L).quantity(1).build();
        BxGyCoupon coupon = BxGyCoupon.builder()
                .buyProducts(Collections.singletonList(buy1))
                .getProducts(Collections.singletonList(get1))
                .repetitionLimit(1)
                .build();
        assertFalse(strategy.isApplicable(cart, coupon));
    }

    @Test
    void isApplicable_shouldReturnFalse_whenCartDoesNotMeetGetRequirements() {
        CartItem item1 = CartItem.builder().productId(1L).quantity(2).price(BigDecimal.valueOf(50)).build();
        CartItem item2 = CartItem.builder().productId(2L).quantity(0).price(BigDecimal.valueOf(100)).build(); // Quantity 0
        Cart cart = Cart.builder().items(Arrays.asList(item1, item2)).build();

        BxGyProductDetail buy1 = BxGyProductDetail.builder().productId(1L).quantity(2).build();
        BxGyProductDetail get1 = BxGyProductDetail.builder().productId(2L).quantity(1).build();
        BxGyCoupon coupon = BxGyCoupon.builder()
                .buyProducts(Collections.singletonList(buy1))
                .getProducts(Collections.singletonList(get1))
                .repetitionLimit(1)
                .build();
        assertFalse(strategy.isApplicable(cart, coupon));
    }

    @Test
    void calculateDiscount_shouldReturnCorrectDiscount_singleRepetition() {
        CartItem item1 = CartItem.builder().productId(1L).quantity(2).price(BigDecimal.valueOf(50)).build(); // Buy 2 of product 1
        CartItem item2 = CartItem.builder().productId(2L).quantity(1).price(BigDecimal.valueOf(100)).build(); // Get 1 of product 2
        Cart cart = Cart.builder().items(Arrays.asList(item1, item2)).build();

        BxGyProductDetail buy1 = BxGyProductDetail.builder().productId(1L).quantity(2).build();
        BxGyProductDetail get1 = BxGyProductDetail.builder().productId(2L).quantity(1).build();
        BxGyCoupon coupon = BxGyCoupon.builder()
                .buyProducts(Collections.singletonList(buy1))
                .getProducts(Collections.singletonList(get1))
                .repetitionLimit(1)
                .build();
        assertEquals(0, BigDecimal.valueOf(100).compareTo(strategy.calculateDiscount(cart, coupon)));
    }

    @Test
    void calculateDiscount_shouldReturnCorrectDiscount_multipleRepetitions() {
        CartItem item1 = CartItem.builder().productId(1L).quantity(4).price(BigDecimal.valueOf(50)).build(); // Buy 2 of product 1 (twice)
        CartItem item2 = CartItem.builder().productId(2L).quantity(2).price(BigDecimal.valueOf(100)).build(); // Get 1 of product 2 (twice)
        Cart cart = Cart.builder().items(Arrays.asList(item1, item2)).build();

        BxGyProductDetail buy1 = BxGyProductDetail.builder().productId(1L).quantity(2).build();
        BxGyProductDetail get1 = BxGyProductDetail.builder().productId(2L).quantity(1).build();
        BxGyCoupon coupon = BxGyCoupon.builder()
                .buyProducts(Collections.singletonList(buy1))
                .getProducts(Collections.singletonList(get1))
                .repetitionLimit(2)
                .build();
        assertEquals(0, BigDecimal.valueOf(200).compareTo(strategy.calculateDiscount(cart, coupon))); // 2 * 100
    }

    @Test
    void calculateDiscount_shouldRespectRepetitionLimit() {
        CartItem item1 = CartItem.builder().productId(1L).quantity(6).price(BigDecimal.valueOf(50)).build(); // Buy 2 of product 1 (three times)
        CartItem item2 = CartItem.builder().productId(2L).quantity(3).price(BigDecimal.valueOf(100)).build(); // Get 1 of product 2 (three times)
        Cart cart = Cart.builder().items(Arrays.asList(item1, item2)).build();

        BxGyProductDetail buy1 = BxGyProductDetail.builder().productId(1L).quantity(2).build();
        BxGyProductDetail get1 = BxGyProductDetail.builder().productId(2L).quantity(1).build();
        BxGyCoupon coupon = BxGyCoupon.builder()
                .buyProducts(Collections.singletonList(buy1))
                .getProducts(Collections.singletonList(get1))
                .repetitionLimit(2) // Limit to 2 repetitions
                .build();
        assertEquals(0, BigDecimal.valueOf(200).compareTo(strategy.calculateDiscount(cart, coupon))); // 2 * 100
    }

    @Test
    void applyDiscount_shouldUpdateCartItemsAndTotals() {
        CartItem item1 = CartItem.builder().productId(1L).quantity(2).price(BigDecimal.valueOf(50)).totalDiscount(BigDecimal.ZERO).finalPrice(BigDecimal.valueOf(100)).build();
        CartItem item2 = CartItem.builder().productId(2L).quantity(1).price(BigDecimal.valueOf(100)).totalDiscount(BigDecimal.ZERO).finalPrice(BigDecimal.valueOf(100)).build();
        List<CartItem> items = Arrays.asList(item1, item2);
        Cart cart = Cart.builder()
                .items(items)
                .totalOriginalPrice(BigDecimal.valueOf(200))
                .totalDiscount(BigDecimal.ZERO)
                .finalPrice(BigDecimal.valueOf(200))
                .build();

        BxGyProductDetail buy1 = BxGyProductDetail.builder().productId(1L).quantity(2).build();
        BxGyProductDetail get1 = BxGyProductDetail.builder().productId(2L).quantity(1).build();
        BxGyCoupon coupon = BxGyCoupon.builder()
                .buyProducts(Collections.singletonList(buy1))
                .getProducts(Collections.singletonList(get1))
                .repetitionLimit(1)
                .build();

        Cart updatedCart = strategy.applyDiscount(cart, coupon);

        assertEquals(0, BigDecimal.valueOf(100).compareTo(updatedCart.getTotalDiscount()));
        assertEquals(0, BigDecimal.valueOf(100).compareTo(updatedCart.getFinalPrice()));
        assertEquals(BigDecimal.ZERO, updatedCart.getItems().get(0).getTotalDiscount()); // Buy product not discounted
        assertEquals(0, BigDecimal.valueOf(100).compareTo(updatedCart.getItems().get(0).getFinalPrice()));
        assertEquals(0, BigDecimal.valueOf(100).compareTo(updatedCart.getItems().get(1).getTotalDiscount())); // Get product discounted
        assertEquals(BigDecimal.ZERO, updatedCart.getItems().get(1).getFinalPrice());
    }
}
