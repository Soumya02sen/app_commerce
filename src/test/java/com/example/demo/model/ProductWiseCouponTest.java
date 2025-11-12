package com.example.demo.model;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class ProductWiseCouponTest {

    @Test
    void testProductWiseCouponCreationUsingBuilder() {
        // Arrange & Act
        LocalDate expirationDate = LocalDate.of(2025, 12, 31);
        ProductWiseCoupon coupon = ProductWiseCoupon.builder()
            .id(1L)
            .type(CouponType.PRODUCT_WISE)
            .code("PROD10")
            .description("10% discount on products")
            .expirationDate(expirationDate)
            .productId(100L)
            .discountPercentage(BigDecimal.valueOf(10))
            .fixedDiscountAmount(BigDecimal.valueOf(50))
            .minQuantity(5)
            .build();

        // Assert
        assertEquals(1L, coupon.getId());
        assertEquals(CouponType.PRODUCT_WISE, coupon.getType());
        assertEquals("PROD10", coupon.getCode());
        assertEquals(expirationDate, coupon.getExpirationDate());
        assertEquals(100L, coupon.getProductId());
        assertEquals(0, BigDecimal.valueOf(10).compareTo(coupon.getDiscountPercentage()));
        assertEquals(0, BigDecimal.valueOf(50).compareTo(coupon.getFixedDiscountAmount()));
        assertEquals(Integer.valueOf(5), coupon.getMinQuantity());
    }

    @Test
    void testProductWiseCouponGetterSetters() {
        // Arrange
        ProductWiseCoupon coupon = ProductWiseCoupon.builder()
            .productId(100L)
            .discountPercentage(BigDecimal.valueOf(10))
            .build();

        // Act
        coupon.setId(2L);
        coupon.setProductId(200L);
        coupon.setDiscountPercentage(BigDecimal.valueOf(15));
        coupon.setFixedDiscountAmount(BigDecimal.valueOf(75));
        coupon.setMinQuantity(10);

        // Assert
        assertEquals(2L, coupon.getId());
        assertEquals(200L, coupon.getProductId());
        assertEquals(0, BigDecimal.valueOf(15).compareTo(coupon.getDiscountPercentage()));
        assertEquals(0, BigDecimal.valueOf(75).compareTo(coupon.getFixedDiscountAmount()));
        assertEquals(Integer.valueOf(10), coupon.getMinQuantity());
    }

    @Test
    void testProductWiseCouponWithNullOptionalFields() {
        // Arrange & Act
        ProductWiseCoupon coupon = ProductWiseCoupon.builder()
            .type(CouponType.PRODUCT_WISE)
            .code("PROD")
            .productId(100L)
            .discountPercentage(BigDecimal.valueOf(10))
            .build();

        // Assert
        assertEquals(CouponType.PRODUCT_WISE, coupon.getType());
        assertNull(coupon.getFixedDiscountAmount());
        assertNull(coupon.getMinQuantity());
        assertNull(coupon.getId());
    }

    @Test
    void testProductWiseCouponEquality() {
        // Arrange
        ProductWiseCoupon coupon1 = ProductWiseCoupon.builder()
            .id(1L)
            .type(CouponType.PRODUCT_WISE)
            .code("CODE1")
            .productId(100L)
            .discountPercentage(BigDecimal.valueOf(10))
            .build();

        ProductWiseCoupon coupon2 = ProductWiseCoupon.builder()
            .id(1L)
            .type(CouponType.PRODUCT_WISE)
            .code("CODE1")
            .productId(100L)
            .discountPercentage(BigDecimal.valueOf(10))
            .build();

        // Assert
        assertEquals(coupon1, coupon2);
    }

    @Test
    void testProductWiseCouponInequality() {
        // Arrange
        ProductWiseCoupon coupon1 = ProductWiseCoupon.builder()
            .id(1L)
            .productId(100L)
            .discountPercentage(BigDecimal.valueOf(10))
            .build();

        ProductWiseCoupon coupon2 = ProductWiseCoupon.builder()
            .id(2L)
            .productId(200L)
            .discountPercentage(BigDecimal.valueOf(15))
            .build();

        // Assert
        assertNotEquals(coupon1, coupon2);
    }

    @Test
    void testProductWiseCouponHashCode() {
        // Arrange & Act
        ProductWiseCoupon coupon1 = ProductWiseCoupon.builder()
            .id(1L)
            .productId(100L)
            .discountPercentage(BigDecimal.valueOf(10))
            .build();

        ProductWiseCoupon coupon2 = ProductWiseCoupon.builder()
            .id(1L)
            .productId(100L)
            .discountPercentage(BigDecimal.valueOf(10))
            .build();

        // Assert
        assertEquals(coupon1.hashCode(), coupon2.hashCode());
    }

    @Test
    void testProductWiseCouponDefaultValues() {
        // Arrange & Act
        ProductWiseCoupon coupon = new ProductWiseCoupon();

        // Assert
        assertNull(coupon.getId());
        assertNull(coupon.getType());
        assertNull(coupon.getCode());
        assertNull(coupon.getProductId());
        assertNull(coupon.getDiscountPercentage());
        assertNull(coupon.getFixedDiscountAmount());
        assertNull(coupon.getMinQuantity());
    }

    @Test
    void testProductWiseCouponToString() {
        // Arrange & Act
        ProductWiseCoupon coupon = ProductWiseCoupon.builder()
            .id(1L)
            .code("PROD10")
            .productId(100L)
            .discountPercentage(BigDecimal.valueOf(10))
            .build();

        String toString = coupon.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("ProductWiseCoupon"));
    }
}
