package com.example.demo.model;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class CartWiseCouponTest {

    @Test
    void testCartWiseCouponCreationUsingBuilder() {
        // Arrange & Act
        LocalDate expirationDate = LocalDate.of(2025, 12, 31);
        CartWiseCoupon coupon = CartWiseCoupon.builder()
            .id(1L)
            .type(CouponType.CART_WISE)
            .code("CART10")
            .description("10% discount on cart")
            .expirationDate(expirationDate)
            .threshold(BigDecimal.valueOf(1000))
            .discountPercentage(BigDecimal.valueOf(10))
            .maxDiscountAmount(BigDecimal.valueOf(500))
            .build();

        // Assert
        assertEquals(1L, coupon.getId());
        assertEquals(CouponType.CART_WISE, coupon.getType());
        assertEquals("CART10", coupon.getCode());
        assertEquals("10% discount on cart", coupon.getDescription());
        assertEquals(expirationDate, coupon.getExpirationDate());
        assertEquals(0, BigDecimal.valueOf(1000).compareTo(coupon.getThreshold()));
        assertEquals(0, BigDecimal.valueOf(10).compareTo(coupon.getDiscountPercentage()));
        assertEquals(0, BigDecimal.valueOf(500).compareTo(coupon.getMaxDiscountAmount()));
    }

    @Test
    void testCartWiseCouponGetterSetters() {
        // Arrange
        CartWiseCoupon coupon = CartWiseCoupon.builder()
            .threshold(BigDecimal.valueOf(1000))
            .discountPercentage(BigDecimal.valueOf(10))
            .maxDiscountAmount(BigDecimal.valueOf(500))
            .build();

        // Act
        coupon.setId(2L);
        coupon.setThreshold(BigDecimal.valueOf(2000));
        coupon.setDiscountPercentage(BigDecimal.valueOf(15));
        coupon.setMaxDiscountAmount(BigDecimal.valueOf(750));
        coupon.setType(CouponType.CART_WISE);

        // Assert
        assertEquals(2L, coupon.getId());
        assertEquals(0, BigDecimal.valueOf(2000).compareTo(coupon.getThreshold()));
        assertEquals(0, BigDecimal.valueOf(15).compareTo(coupon.getDiscountPercentage()));
        assertEquals(0, BigDecimal.valueOf(750).compareTo(coupon.getMaxDiscountAmount()));
        assertEquals(CouponType.CART_WISE, coupon.getType());
    }

    @Test
    void testCartWiseCouponWithMinimalFields() {
        // Arrange & Act
        CartWiseCoupon coupon = CartWiseCoupon.builder()
            .type(CouponType.CART_WISE)
            .code("TEST")
            .threshold(BigDecimal.valueOf(500))
            .discountPercentage(BigDecimal.valueOf(5))
            .build();

        // Assert
        assertEquals(CouponType.CART_WISE, coupon.getType());
        assertEquals("TEST", coupon.getCode());
        assertNull(coupon.getId());
        assertNull(coupon.getDescription());
        assertNull(coupon.getExpirationDate());
    }

    @Test
    void testCartWiseCouponEquality() {
        // Arrange
        LocalDate date = LocalDate.now();
        CartWiseCoupon coupon1 = CartWiseCoupon.builder()
            .id(1L)
            .type(CouponType.CART_WISE)
            .code("CODE1")
            .threshold(BigDecimal.valueOf(1000))
            .discountPercentage(BigDecimal.valueOf(10))
            .expirationDate(date)
            .build();

        CartWiseCoupon coupon2 = CartWiseCoupon.builder()
            .id(1L)
            .type(CouponType.CART_WISE)
            .code("CODE1")
            .threshold(BigDecimal.valueOf(1000))
            .discountPercentage(BigDecimal.valueOf(10))
            .expirationDate(date)
            .build();

        // Assert
        assertEquals(coupon1, coupon2);
    }

    @Test
    void testCartWiseCouponToString() {
        // Arrange & Act
        CartWiseCoupon coupon = CartWiseCoupon.builder()
            .id(1L)
            .code("CART10")
            .threshold(BigDecimal.valueOf(1000))
            .discountPercentage(BigDecimal.valueOf(10))
            .build();

        String toString = coupon.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("CartWiseCoupon"));
    }

    @Test
    void testCartWiseCouponHashCode() {
        // Arrange & Act
        CartWiseCoupon coupon1 = CartWiseCoupon.builder()
            .id(1L)
            .code("HASH1")
            .threshold(BigDecimal.valueOf(1000))
            .discountPercentage(BigDecimal.valueOf(10))
            .build();

        CartWiseCoupon coupon2 = CartWiseCoupon.builder()
            .id(1L)
            .code("HASH1")
            .threshold(BigDecimal.valueOf(1000))
            .discountPercentage(BigDecimal.valueOf(10))
            .build();

        // Assert - Equal objects should have equal hash codes
        assertEquals(coupon1.hashCode(), coupon2.hashCode());
    }

    @Test
    void testCartWiseCouponDefaultValues() {
        // Arrange & Act
        CartWiseCoupon coupon = new CartWiseCoupon();

        // Assert
        assertNull(coupon.getId());
        assertNull(coupon.getType());
        assertNull(coupon.getCode());
        assertNull(coupon.getThreshold());
        assertNull(coupon.getDiscountPercentage());
        assertNull(coupon.getMaxDiscountAmount());
    }
}
