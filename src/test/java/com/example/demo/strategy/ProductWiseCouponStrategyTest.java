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
import com.example.demo.model.CouponType;
import com.example.demo.model.ProductWiseCoupon;

class ProductWiseCouponStrategyTest {

    private ProductWiseCouponStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new ProductWiseCouponStrategy();
    }

    @Test
    void getCouponType_shouldReturnProductWise() {
        assertEquals(CouponType.PRODUCT_WISE, strategy.getCouponType());
    }

    @Test
    void isApplicable_shouldReturnTrue_whenProductInCart() {
        CartItem item1 = CartItem.builder().productId(1L).quantity(2).price(BigDecimal.valueOf(50)).build();
        CartItem item2 = CartItem.builder().productId(2L).quantity(1).price(BigDecimal.valueOf(100)).build();
        Cart cart = Cart.builder().items(Arrays.asList(item1, item2)).build();
        ProductWiseCoupon coupon = ProductWiseCoupon.builder().productId(1L).build();
        assertTrue(strategy.isApplicable(cart, coupon));
    }

    @Test
    void isApplicable_shouldReturnFalse_whenProductNotInCart() {
        CartItem item1 = CartItem.builder().productId(1L).quantity(2).price(BigDecimal.valueOf(50)).build();
        Cart cart = Cart.builder().items(Collections.singletonList(item1)).build();
        ProductWiseCoupon coupon = ProductWiseCoupon.builder().productId(3L).build();
        assertFalse(strategy.isApplicable(cart, coupon));
    }

    @Test
    void isApplicable_shouldReturnTrue_whenProductInCartAndMeetsMinQuantity() {
        CartItem item1 = CartItem.builder().productId(1L).quantity(3).price(BigDecimal.valueOf(50)).build();
        Cart cart = Cart.builder().items(Collections.singletonList(item1)).build();
        ProductWiseCoupon coupon = ProductWiseCoupon.builder().productId(1L).minQuantity(3).build();
        assertTrue(strategy.isApplicable(cart, coupon));
    }

    @Test
    void isApplicable_shouldReturnFalse_whenProductInCartButBelowMinQuantity() {
        CartItem item1 = CartItem.builder().productId(1L).quantity(2).price(BigDecimal.valueOf(50)).build();
        Cart cart = Cart.builder().items(Collections.singletonList(item1)).build();
        ProductWiseCoupon coupon = ProductWiseCoupon.builder().productId(1L).minQuantity(3).build();
        assertFalse(strategy.isApplicable(cart, coupon));
    }

    @Test
    void calculateDiscount_shouldReturnCorrectPercentageDiscount() {
        CartItem item1 = CartItem.builder().productId(1L).quantity(2).price(BigDecimal.valueOf(50)).build(); // Total 100
        CartItem item2 = CartItem.builder().productId(2L).quantity(1).price(BigDecimal.valueOf(100)).build();
        Cart cart = Cart.builder().items(Arrays.asList(item1, item2)).build();
        ProductWiseCoupon coupon = ProductWiseCoupon.builder().productId(1L).discountPercentage(BigDecimal.valueOf(10)).build(); // 10% off 100 = 10
        assertEquals(0, BigDecimal.valueOf(10).compareTo(strategy.calculateDiscount(cart, coupon)));
    }

    @Test
    void calculateDiscount_shouldReturnCorrectFixedDiscount() {
        CartItem item1 = CartItem.builder().productId(1L).quantity(2).price(BigDecimal.valueOf(50)).build(); // Total 100
        CartItem item2 = CartItem.builder().productId(2L).quantity(1).price(BigDecimal.valueOf(100)).build();
        Cart cart = Cart.builder().items(Arrays.asList(item1, item2)).build();
        ProductWiseCoupon coupon = ProductWiseCoupon.builder().productId(1L).fixedDiscountAmount(BigDecimal.valueOf(5)).build(); // 5 per item * 2 items = 10
        assertEquals(0, BigDecimal.valueOf(10).compareTo(strategy.calculateDiscount(cart, coupon)));
    }

    @Test
    void applyDiscount_shouldUpdateCartItemsAndTotals_percentage() {
        CartItem item1 = CartItem.builder().productId(1L).quantity(2).price(BigDecimal.valueOf(50)).totalDiscount(BigDecimal.ZERO).finalPrice(BigDecimal.valueOf(100)).build();
        CartItem item2 = CartItem.builder().productId(2L).quantity(1).price(BigDecimal.valueOf(100)).totalDiscount(BigDecimal.ZERO).finalPrice(BigDecimal.valueOf(100)).build();
        List<CartItem> items = Arrays.asList(item1, item2);
        Cart cart = Cart.builder()
                .items(items)
                .totalOriginalPrice(BigDecimal.valueOf(200))
                .totalDiscount(BigDecimal.ZERO)
                .finalPrice(BigDecimal.valueOf(200))
                .build();
        ProductWiseCoupon coupon = ProductWiseCoupon.builder().productId(1L).discountPercentage(BigDecimal.valueOf(10)).build(); // 10% off 100 = 10

        Cart updatedCart = strategy.applyDiscount(cart, coupon);

        assertEquals(0, BigDecimal.valueOf(10).compareTo(updatedCart.getTotalDiscount()));
        assertEquals(0, BigDecimal.valueOf(190).compareTo(updatedCart.getFinalPrice()));
        assertEquals(0, BigDecimal.valueOf(10).compareTo(updatedCart.getItems().get(0).getTotalDiscount()));
        assertEquals(0, BigDecimal.valueOf(90).compareTo(updatedCart.getItems().get(0).getFinalPrice()));
        assertEquals(BigDecimal.ZERO, updatedCart.getItems().get(1).getTotalDiscount());
        assertEquals(0, BigDecimal.valueOf(100).compareTo(updatedCart.getItems().get(1).getFinalPrice()));
    }

    @Test
    void applyDiscount_shouldUpdateCartItemsAndTotals_fixed() {
        CartItem item1 = CartItem.builder().productId(1L).quantity(2).price(BigDecimal.valueOf(50)).totalDiscount(BigDecimal.ZERO).finalPrice(BigDecimal.valueOf(100)).build();
        CartItem item2 = CartItem.builder().productId(2L).quantity(1).price(BigDecimal.valueOf(100)).totalDiscount(BigDecimal.ZERO).finalPrice(BigDecimal.valueOf(100)).build();
        List<CartItem> items = Arrays.asList(item1, item2);
        Cart cart = Cart.builder()
                .items(items)
                .totalOriginalPrice(BigDecimal.valueOf(200))
                .totalDiscount(BigDecimal.ZERO)
                .finalPrice(BigDecimal.valueOf(200))
                .build();
        ProductWiseCoupon coupon = ProductWiseCoupon.builder().productId(1L).fixedDiscountAmount(BigDecimal.valueOf(5)).build(); // 5 per item * 2 items = 10

        Cart updatedCart = strategy.applyDiscount(cart, coupon);

        assertEquals(0, BigDecimal.valueOf(10).compareTo(updatedCart.getTotalDiscount()));
        assertEquals(0, BigDecimal.valueOf(190).compareTo(updatedCart.getFinalPrice()));
        assertEquals(0, BigDecimal.valueOf(10).compareTo(updatedCart.getItems().get(0).getTotalDiscount()));
        assertEquals(0, BigDecimal.valueOf(90).compareTo(updatedCart.getItems().get(0).getFinalPrice()));
        assertEquals(BigDecimal.ZERO, updatedCart.getItems().get(1).getTotalDiscount());
        assertEquals(0, BigDecimal.valueOf(100).compareTo(updatedCart.getItems().get(1).getFinalPrice()));
    }
}
