package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.dto.ApplicableCouponResponse;
import com.example.demo.dto.BxGyCouponRequest;
import com.example.demo.dto.Cart;
import com.example.demo.dto.CartItem;
import com.example.demo.dto.CartWiseCouponRequest;
import com.example.demo.dto.CouponResponseDTO;
import com.example.demo.dto.ProductWiseCouponRequest;
import com.example.demo.exception.CouponExpiredException;
import com.example.demo.exception.CouponNotApplicableException;
import com.example.demo.exception.CouponNotFoundException;
import com.example.demo.exception.InvalidCouponRequestException;
import com.example.demo.model.BxGyCoupon;
import com.example.demo.model.BxGyProductDetail;
import com.example.demo.model.CartWiseCoupon;
import com.example.demo.model.Coupon;
import com.example.demo.model.CouponType;
import com.example.demo.model.ProductWiseCoupon;
import com.example.demo.strategy.CouponStrategy;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponStrategyFactory couponStrategyFactory;

    @Mock
    private CouponStrategy cartWiseCouponStrategy;
    @Mock
    private CouponStrategy productWiseCouponStrategy;
    @Mock
    private CouponStrategy bxGyCouponStrategy;

    @InjectMocks
    private CouponService couponService;

    @BeforeEach
    void setUp() {
        // Reset the internal state of couponService for each test
        // This is a workaround for in-memory storage in a singleton service
        try {
            java.lang.reflect.Field couponsField = CouponService.class.getDeclaredField("coupons");
            couponsField.setAccessible(true);
            Map<?, ?> coupons = (Map<?, ?>) couponsField.get(couponService);
            coupons.clear();

            java.lang.reflect.Field idCounterField = CouponService.class.getDeclaredField("idCounter");
            idCounterField.setAccessible(true);
            AtomicLong idCounter = (AtomicLong) idCounterField.get(couponService);
            idCounter.set(0);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        when(cartWiseCouponStrategy.getCouponType()).thenReturn(CouponType.CART_WISE);
        when(productWiseCouponStrategy.getCouponType()).thenReturn(CouponType.PRODUCT_WISE);
        when(bxGyCouponStrategy.getCouponType()).thenReturn(CouponType.BXGY);

        when(couponStrategyFactory.getStrategy(CouponType.CART_WISE)).thenReturn(cartWiseCouponStrategy);
        when(couponStrategyFactory.getStrategy(CouponType.PRODUCT_WISE)).thenReturn(productWiseCouponStrategy);
        when(couponStrategyFactory.getStrategy(CouponType.BXGY)).thenReturn(bxGyCouponStrategy);
    }

    @Test
    void createCoupon_cartWise_shouldReturnCreatedCoupon() {
        CartWiseCouponRequest request = CartWiseCouponRequest.builder()
                .code("CART10")
                .description("10% off cart")
                .expirationDate(LocalDate.now().plusDays(10))
                .threshold(BigDecimal.valueOf(100))
                .discountPercentage(BigDecimal.valueOf(10))
                .build();

        CouponResponseDTO response = couponService.createCoupon(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(CouponType.CART_WISE, response.getType());
        assertEquals("CART10", response.getCode());
        assertTrue(couponService.getAllCoupons().size() == 1);
    }

    @Test
    void createCoupon_productWise_shouldReturnCreatedCoupon() {
        ProductWiseCouponRequest request = ProductWiseCouponRequest.builder()
                .code("PROD20")
                .description("20% off product 1")
                .expirationDate(LocalDate.now().plusDays(10))
                .productId(1L)
                .discountPercentage(BigDecimal.valueOf(20))
                .build();

        CouponResponseDTO response = couponService.createCoupon(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(CouponType.PRODUCT_WISE, response.getType());
        assertEquals("PROD20", response.getCode());
    }

    @Test
    void createCoupon_bxGy_shouldReturnCreatedCoupon() {
        BxGyCouponRequest request = BxGyCouponRequest.builder()
                .code("BXGY1")
                .description("Buy X Get Y")
                .expirationDate(LocalDate.now().plusDays(10))
                .buyProducts(Collections.singletonList(BxGyProductDetail.builder().productId(1L).quantity(2).build()))
                .getProducts(Collections.singletonList(BxGyProductDetail.builder().productId(2L).quantity(1).build()))
                .repetitionLimit(1)
                .build();

        CouponResponseDTO response = couponService.createCoupon(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(CouponType.BXGY, response.getType());
        assertEquals("BXGY1", response.getCode());
    }

    @Test
    void getCouponById_shouldReturnCoupon_whenFound() {
        CartWiseCouponRequest request = CartWiseCouponRequest.builder()
                .code("CART10")
                .description("10% off cart")
                .expirationDate(LocalDate.now().plusDays(10))
                .threshold(BigDecimal.valueOf(100))
                .discountPercentage(BigDecimal.valueOf(10))
                .build();
        couponService.createCoupon(request);

        CouponResponseDTO response = couponService.getCouponById(1L);
        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getCouponById_shouldThrowException_whenNotFound() {
        assertThrows(CouponNotFoundException.class, () -> couponService.getCouponById(99L));
    }

    @Test
    void getAllCoupons_shouldReturnAllCoupons() {
        couponService.createCoupon(CartWiseCouponRequest.builder().code("C1").expirationDate(LocalDate.now().plusDays(1)).threshold(BigDecimal.ONE).discountPercentage(BigDecimal.ONE).build());
        couponService.createCoupon(ProductWiseCouponRequest.builder().code("P1").expirationDate(LocalDate.now().plusDays(1)).productId(1L).discountPercentage(BigDecimal.ONE).build());

        List<CouponResponseDTO> coupons = couponService.getAllCoupons();
        assertEquals(2, coupons.size());
    }

    @Test
    void updateCoupon_shouldUpdateExistingCoupon() {
        CartWiseCouponRequest createRequest = CartWiseCouponRequest.builder()
                .code("CART10")
                .description("10% off cart")
                .expirationDate(LocalDate.now().plusDays(10))
                .threshold(BigDecimal.valueOf(100))
                .discountPercentage(BigDecimal.valueOf(10))
                .build();
        couponService.createCoupon(createRequest);

        CartWiseCouponRequest updateRequest = CartWiseCouponRequest.builder()
                .code("CART15")
                .description("15% off cart")
                .expirationDate(LocalDate.now().plusDays(15))
                .threshold(BigDecimal.valueOf(150))
                .discountPercentage(BigDecimal.valueOf(15))
                .build();

        CouponResponseDTO updatedCoupon = couponService.updateCoupon(1L, updateRequest);

        assertNotNull(updatedCoupon);
        assertEquals(1L, updatedCoupon.getId());
        assertEquals("CART15", updatedCoupon.getCode());
        assertEquals("15% off cart", updatedCoupon.getDescription());
    }

    @Test
    void updateCoupon_shouldThrowException_whenNotFound() {
        CartWiseCouponRequest updateRequest = CartWiseCouponRequest.builder()
                .code("CART15")
                .description("15% off cart")
                .expirationDate(LocalDate.now().plusDays(15))
                .threshold(BigDecimal.valueOf(150))
                .discountPercentage(BigDecimal.valueOf(15))
                .build();
        assertThrows(CouponNotFoundException.class, () -> couponService.updateCoupon(99L, updateRequest));
    }

    @Test
    void deleteCoupon_shouldRemoveCoupon() {
        couponService.createCoupon(CartWiseCouponRequest.builder().code("C1").expirationDate(LocalDate.now().plusDays(1)).threshold(BigDecimal.ONE).discountPercentage(BigDecimal.ONE).build());
        couponService.deleteCoupon(1L);
        assertTrue(couponService.getAllCoupons().isEmpty());
    }

    @Test
    void deleteCoupon_shouldThrowException_whenNotFound() {
        assertThrows(CouponNotFoundException.class, () -> couponService.deleteCoupon(99L));
    }

    @Test
    void getApplicableCoupons_shouldReturnApplicableCoupons() {
        // Setup cart
        CartItem item1 = CartItem.builder().productId(1L).quantity(1).price(BigDecimal.valueOf(100)).build();
        Cart cart = Cart.builder().items(Collections.singletonList(item1)).build();

        // Setup coupons
        CartWiseCouponRequest cartWiseRequest = CartWiseCouponRequest.builder()
                .code("CART10")
                .description("10% off cart")
                .expirationDate(LocalDate.now().plusDays(10))
                .threshold(BigDecimal.valueOf(50))
                .discountPercentage(BigDecimal.valueOf(10))
                .build();
        couponService.createCoupon(cartWiseRequest); // ID 1

        ProductWiseCouponRequest productWiseRequest = ProductWiseCouponRequest.builder()
                .code("PROD20")
                .description("20% off product 1")
                .expirationDate(LocalDate.now().plusDays(10))
                .productId(1L)
                .discountPercentage(BigDecimal.valueOf(20))
                .build();
        couponService.createCoupon(productWiseRequest); // ID 2

        // Mock strategy behavior
        when(cartWiseCouponStrategy.isApplicable(any(Cart.class), any(CartWiseCoupon.class))).thenReturn(true);
        when(cartWiseCouponStrategy.calculateDiscount(any(Cart.class), any(CartWiseCoupon.class))).thenReturn(BigDecimal.valueOf(10));
        when(productWiseCouponStrategy.isApplicable(any(Cart.class), any(ProductWiseCoupon.class))).thenReturn(true);
        when(productWiseCouponStrategy.calculateDiscount(any(Cart.class), any(ProductWiseCoupon.class))).thenReturn(BigDecimal.valueOf(20));

        List<ApplicableCouponResponse> applicableCoupons = couponService.getApplicableCoupons(cart);

        assertNotNull(applicableCoupons);
        assertEquals(2, applicableCoupons.size());
        assertTrue(applicableCoupons.stream().anyMatch(c -> c.getCouponId() == 1L && c.getDiscountAmount().compareTo(BigDecimal.valueOf(10)) == 0));
        assertTrue(applicableCoupons.stream().anyMatch(c -> c.getCouponId() == 2L && c.getDiscountAmount().compareTo(BigDecimal.valueOf(20)) == 0));
    }

    @Test
    void applyCoupon_shouldApplyDiscountAndReturnUpdatedCart() {
        // Setup cart
        CartItem item1 = CartItem.builder().productId(1L).quantity(1).price(BigDecimal.valueOf(100)).build();
        Cart cart = Cart.builder().items(Collections.singletonList(item1)).build();

        // Setup coupon
        CartWiseCouponRequest cartWiseRequest = CartWiseCouponRequest.builder()
                .code("CART10")
                .description("10% off cart")
                .expirationDate(LocalDate.now().plusDays(10))
                .threshold(BigDecimal.valueOf(50))
                .discountPercentage(BigDecimal.valueOf(10))
                .build();
        couponService.createCoupon(cartWiseRequest); // ID 1

        // Mock strategy behavior
        when(cartWiseCouponStrategy.isApplicable(any(Cart.class), any(CartWiseCoupon.class))).thenReturn(true);
        Cart updatedCartMock = Cart.builder()
                .items(Collections.singletonList(item1))
                .totalOriginalPrice(BigDecimal.valueOf(100))
                .totalDiscount(BigDecimal.valueOf(10))
                .finalPrice(BigDecimal.valueOf(90))
                .build();
        when(cartWiseCouponStrategy.applyDiscount(any(Cart.class), any(CartWiseCoupon.class))).thenReturn(updatedCartMock);

        Cart resultCart = couponService.applyCoupon(1L, cart);

        assertNotNull(resultCart);
        assertEquals(BigDecimal.valueOf(10), resultCart.getTotalDiscount());
        assertEquals(BigDecimal.valueOf(90), resultCart.getFinalPrice());
    }

    @Test
    void applyCoupon_shouldThrowException_whenCouponNotFound() {
        Cart cart = Cart.builder().items(Collections.singletonList(CartItem.builder().productId(1L).quantity(1).price(BigDecimal.valueOf(100)).build())).build();
        assertThrows(CouponNotFoundException.class, () -> couponService.applyCoupon(99L, cart));
    }

    @Test
    void applyCoupon_shouldThrowException_whenCouponExpired() {
        CartWiseCouponRequest cartWiseRequest = CartWiseCouponRequest.builder()
                .code("CART10")
                .description("10% off cart")
                .expirationDate(LocalDate.now().minusDays(1)) // Expired
                .threshold(BigDecimal.valueOf(50))
                .discountPercentage(BigDecimal.valueOf(10))
                .build();
        couponService.createCoupon(cartWiseRequest); // ID 1

        Cart cart = Cart.builder().items(Collections.singletonList(CartItem.builder().productId(1L).quantity(1).price(BigDecimal.valueOf(100)).build())).build();
        assertThrows(CouponExpiredException.class, () -> couponService.applyCoupon(1L, cart));
    }

    @Test
    void applyCoupon_shouldThrowException_whenCouponNotApplicable() {
        CartWiseCouponRequest cartWiseRequest = CartWiseCouponRequest.builder()
                .code("CART10")
                .description("10% off cart")
                .expirationDate(LocalDate.now().plusDays(10))
                .threshold(BigDecimal.valueOf(50))
                .discountPercentage(BigDecimal.valueOf(10))
                .build();
        couponService.createCoupon(cartWiseRequest); // ID 1

        Cart cart = Cart.builder().items(Collections.singletonList(CartItem.builder().productId(1L).quantity(1).price(BigDecimal.valueOf(100)).build())).build();

        when(cartWiseCouponStrategy.isApplicable(any(Cart.class), any(CartWiseCoupon.class))).thenReturn(false);

        assertThrows(CouponNotApplicableException.class, () -> couponService.applyCoupon(1L, cart));
    }
}
