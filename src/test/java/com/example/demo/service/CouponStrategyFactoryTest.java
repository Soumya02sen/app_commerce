package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.demo.model.CouponType;
import com.example.demo.strategy.BxGyCouponStrategy;
import com.example.demo.strategy.CartWiseCouponStrategy;
import com.example.demo.strategy.CouponStrategy;
import com.example.demo.strategy.ProductWiseCouponStrategy;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CouponStrategyFactoryTest {

    @Autowired
    private CouponStrategyFactory couponStrategyFactory;

    @Test
    void testGetStrategyForCartWiseCoupon() {
        // Arrange
        CouponType couponType = CouponType.CART_WISE;

        // Act
        CouponStrategy strategy = couponStrategyFactory.getStrategy(couponType);

        // Assert
        assertNotNull(strategy);
        assertTrue(strategy instanceof CartWiseCouponStrategy);
    }

    @Test
    void testGetStrategyForProductWiseCoupon() {
        // Arrange
        CouponType couponType = CouponType.PRODUCT_WISE;

        // Act
        CouponStrategy strategy = couponStrategyFactory.getStrategy(couponType);

        // Assert
        assertNotNull(strategy);
        assertTrue(strategy instanceof ProductWiseCouponStrategy);
    }

    @Test
    void testGetStrategyForBxGyCoupon() {
        // Arrange
        CouponType couponType = CouponType.BXGY;

        // Act
        CouponStrategy strategy = couponStrategyFactory.getStrategy(couponType);

        // Assert
        assertNotNull(strategy);
        assertTrue(strategy instanceof BxGyCouponStrategy);
    }

    @Test
    void testGetStrategyNeverReturnsNull() {
        // Test all coupon types return non-null strategies
        for (CouponType couponType : CouponType.values()) {
            CouponStrategy strategy = couponStrategyFactory.getStrategy(couponType);
            assertNotNull(strategy, "Strategy should not be null for CouponType: " + couponType);
        }
    }

    @Test
    void testGetStrategyConsistency() {
        // Arrange & Act
        CouponStrategy strategy1 = couponStrategyFactory.getStrategy(CouponType.CART_WISE);
        CouponStrategy strategy2 = couponStrategyFactory.getStrategy(CouponType.CART_WISE);

        // Assert - both calls should return the same type
        assertEquals(strategy1.getClass(), strategy2.getClass());
    }
}
