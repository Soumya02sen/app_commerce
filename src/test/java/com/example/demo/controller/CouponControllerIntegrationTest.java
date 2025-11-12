package com.example.demo.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.example.demo.DemoApplication;
import com.example.demo.dto.BxGyCouponRequest;
import com.example.demo.dto.Cart;
import com.example.demo.dto.CartItem;
import com.example.demo.dto.CartWiseCouponRequest;
import com.example.demo.dto.ProductWiseCouponRequest;
import com.example.demo.model.BxGyProductDetail;
import com.example.demo.service.CouponService;

@SpringBootTest(classes = DemoApplication.class)
@AutoConfigureMockMvc
class CouponControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CouponService couponService; // To clear state between tests

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Clear all coupons before each test to ensure a clean state
        couponService.getAllCoupons().forEach(coupon -> couponService.deleteCoupon(coupon.getId()));
        objectMapper.registerModule(new JavaTimeModule()); // Register JavaTimeModule for LocalDate serialization
    }

    @Test
    void createCartWiseCoupon_shouldReturnCreatedCoupon() throws Exception {
        CartWiseCouponRequest request = CartWiseCouponRequest.builder()
                .code("CART10")
                .description("10% off cart")
                .expirationDate(LocalDate.now().plusDays(10))
                .threshold(BigDecimal.valueOf(100))
                .discountPercentage(BigDecimal.valueOf(10))
                .build();

        mockMvc.perform(post("/api/v1/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.code", is("CART10")))
                .andExpect(jsonPath("$.type", is("CART_WISE")));
    }

    @Test
    void createProductWiseCoupon_shouldReturnCreatedCoupon() throws Exception {
        ProductWiseCouponRequest request = ProductWiseCouponRequest.builder()
                .code("PROD20")
                .description("20% off product 1")
                .expirationDate(LocalDate.now().plusDays(10))
                .productId(1L)
                .discountPercentage(BigDecimal.valueOf(20))
                .build();

        mockMvc.perform(post("/api/v1/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.code", is("PROD20")))
                .andExpect(jsonPath("$.type", is("PRODUCT_WISE")));
    }

    @Test
    void createBxGyCoupon_shouldReturnCreatedCoupon() throws Exception {
        BxGyCouponRequest request = BxGyCouponRequest.builder()
                .code("BXGY1")
                .description("Buy X Get Y")
                .expirationDate(LocalDate.now().plusDays(10))
                .buyProducts(Collections.singletonList(BxGyProductDetail.builder().productId(1L).quantity(2).build()))
                .getProducts(Collections.singletonList(BxGyProductDetail.builder().productId(2L).quantity(1).build()))
                .repetitionLimit(1)
                .build();

        mockMvc.perform(post("/api/v1/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.code", is("BXGY1")))
                .andExpect(jsonPath("$.type", is("BXGY")));
    }

    @Test
    void getAllCoupons_shouldReturnAllCoupons() throws Exception {
        couponService.createCoupon(CartWiseCouponRequest.builder().code("C1").expirationDate(LocalDate.now().plusDays(1)).threshold(BigDecimal.ONE).discountPercentage(BigDecimal.ONE).build());
        couponService.createCoupon(ProductWiseCouponRequest.builder().code("P1").expirationDate(LocalDate.now().plusDays(1)).productId(1L).discountPercentage(BigDecimal.ONE).build());

        mockMvc.perform(get("/api/v1/coupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getCouponById_shouldReturnCoupon() throws Exception {
        couponService.createCoupon(CartWiseCouponRequest.builder().code("C1").expirationDate(LocalDate.now().plusDays(1)).threshold(BigDecimal.ONE).discountPercentage(BigDecimal.ONE).build());

        mockMvc.perform(get("/api/v1/coupons/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.code", is("C1")));
    }

    @Test
    void getCouponById_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/coupons/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCoupon_shouldUpdateExistingCoupon() throws Exception {
        couponService.createCoupon(CartWiseCouponRequest.builder().code("C1").description("Old Desc").expirationDate(LocalDate.now().plusDays(1)).threshold(BigDecimal.ONE).discountPercentage(BigDecimal.ONE).build());

        CartWiseCouponRequest updateRequest = CartWiseCouponRequest.builder()
                .code("C1-Updated")
                .description("New Desc")
                .expirationDate(LocalDate.now().plusDays(5))
                .threshold(BigDecimal.valueOf(10))
                .discountPercentage(BigDecimal.valueOf(5))
                .build();

        mockMvc.perform(put("/api/v1/coupons/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.code", is("C1-Updated")))
                .andExpect(jsonPath("$.description", is("New Desc")));
    }

    @Test
    void deleteCoupon_shouldDeleteCoupon() throws Exception {
        couponService.createCoupon(CartWiseCouponRequest.builder().code("C1").expirationDate(LocalDate.now().plusDays(1)).threshold(BigDecimal.ONE).discountPercentage(BigDecimal.ONE).build());

        mockMvc.perform(delete("/api/v1/coupons/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/coupons/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getApplicableCoupons_shouldReturnApplicableCoupons() throws Exception {
        couponService.createCoupon(CartWiseCouponRequest.builder().code("C1").expirationDate(LocalDate.now().plusDays(1)).threshold(BigDecimal.valueOf(50)).discountPercentage(BigDecimal.valueOf(10)).build());
        couponService.createCoupon(ProductWiseCouponRequest.builder().code("P1").expirationDate(LocalDate.now().plusDays(1)).productId(1L).discountPercentage(BigDecimal.valueOf(20)).build());

        CartItem item1 = CartItem.builder().productId(1L).quantity(1).price(BigDecimal.valueOf(100)).build();
        Cart cart = Cart.builder().items(Collections.singletonList(item1)).build();

        mockMvc.perform(post("/api/v1/coupons/applicable-coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cart)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].couponId").isNumber())
                .andExpect(jsonPath("$[0].type").isString())
                .andExpect(jsonPath("$[0].discountAmount").isNumber());
    }

    @Test
    void applyCoupon_shouldApplyDiscountAndReturnUpdatedCart() throws Exception {
        couponService.createCoupon(CartWiseCouponRequest.builder().code("C1").expirationDate(LocalDate.now().plusDays(1)).threshold(BigDecimal.valueOf(50)).discountPercentage(BigDecimal.valueOf(10)).build());

        CartItem item1 = CartItem.builder().productId(1L).quantity(1).price(BigDecimal.valueOf(100)).build();
        Cart cart = Cart.builder().items(Collections.singletonList(item1)).build();

        mockMvc.perform(post("/api/v1/coupons/apply-coupon/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cart)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDiscount", is(10.0)))
                .andExpect(jsonPath("$.finalPrice", is(90.0)));
    }

    @Test
    void applyCoupon_shouldReturnBadRequest_whenCouponNotApplicable() throws Exception {
        couponService.createCoupon(CartWiseCouponRequest.builder().code("C1").expirationDate(LocalDate.now().plusDays(1)).threshold(BigDecimal.valueOf(150)).discountPercentage(BigDecimal.valueOf(10)).build());

        CartItem item1 = CartItem.builder().productId(1L).quantity(1).price(BigDecimal.valueOf(100)).build();
        Cart cart = Cart.builder().items(Collections.singletonList(item1)).build();

        mockMvc.perform(post("/api/v1/coupons/apply-coupon/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cart)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Coupon with id 1 is not applicable to the cart")));
    }
}
