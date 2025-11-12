package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.dto.ApplicableCouponResponse;
import com.example.demo.dto.BxGyCouponRequest;
import com.example.demo.dto.BxGyCouponResponse;
import com.example.demo.dto.Cart;
import com.example.demo.dto.CartItem;
import com.example.demo.dto.CartWiseCouponRequest;
import com.example.demo.dto.CartWiseCouponResponse;
import com.example.demo.dto.CouponRequestDTO;
import com.example.demo.dto.CouponResponseDTO;
import com.example.demo.dto.ProductWiseCouponRequest;
import com.example.demo.dto.ProductWiseCouponResponse;
import com.example.demo.exception.CouponExpiredException;
import com.example.demo.exception.CouponNotApplicableException;
import com.example.demo.exception.CouponNotFoundException;
import com.example.demo.exception.InvalidCouponRequestException;
import com.example.demo.model.BxGyCoupon;
import com.example.demo.model.CartWiseCoupon;
import com.example.demo.model.Coupon;
import com.example.demo.model.CouponType;
import com.example.demo.model.ProductWiseCoupon;
import com.example.demo.strategy.CouponStrategy;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final Map<Long, Coupon> coupons = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong();
    private final CouponStrategyFactory couponStrategyFactory;

    public CouponResponseDTO createCoupon(CouponRequestDTO request) {
        Coupon coupon;
        Long newId = idCounter.incrementAndGet();

        if (request instanceof CartWiseCouponRequest cartWiseRequest) {
            coupon = CartWiseCoupon.builder()
                    .id(newId)
                    .type(CouponType.CART_WISE)
                    .code(cartWiseRequest.getCode())
                    .description(cartWiseRequest.getDescription())
                    .expirationDate(cartWiseRequest.getExpirationDate())
                    .threshold(cartWiseRequest.getThreshold())
                    .discountPercentage(cartWiseRequest.getDiscountPercentage())
                    .maxDiscountAmount(cartWiseRequest.getMaxDiscountAmount())
                    .build();
        } else if (request instanceof ProductWiseCouponRequest productWiseRequest) {
            coupon = ProductWiseCoupon.builder()
                    .id(newId)
                    .type(CouponType.PRODUCT_WISE)
                    .code(productWiseRequest.getCode())
                    .description(productWiseRequest.getDescription())
                    .expirationDate(productWiseRequest.getExpirationDate())
                    .productId(productWiseRequest.getProductId())
                    .discountPercentage(productWiseRequest.getDiscountPercentage())
                    .fixedDiscountAmount(productWiseRequest.getFixedDiscountAmount())
                    .minQuantity(productWiseRequest.getMinQuantity())
                    .build();
        } else if (request instanceof BxGyCouponRequest bxGyRequest) {
            coupon = BxGyCoupon.builder()
                    .id(newId)
                    .type(CouponType.BXGY)
                    .code(bxGyRequest.getCode())
                    .description(bxGyRequest.getDescription())
                    .expirationDate(bxGyRequest.getExpirationDate())
                    .buyProducts(bxGyRequest.getBuyProducts())
                    .getProducts(bxGyRequest.getGetProducts())
                    .repetitionLimit(bxGyRequest.getRepetitionLimit())
                    .build();
        } else {
            throw new InvalidCouponRequestException("Unknown coupon request type");
        }


        coupons.put(newId, coupon);
        return convertToDto(coupon);
    }

    public CouponResponseDTO getCouponById(Long id) {
        return Optional.ofNullable(coupons.get(id))
                .map(this::convertToDto)
                .orElseThrow(() -> new CouponNotFoundException("Coupon with id " + id + " not found"));
    }

    public List<CouponResponseDTO> getAllCoupons() {
        return coupons.values().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public CouponResponseDTO updateCoupon(Long id, CouponRequestDTO request) {
        Coupon existingCoupon = coupons.get(id);
        if (existingCoupon == null) {
            throw new CouponNotFoundException("Coupon with id " + id + " not found");
        }

        // Update common fields
        existingCoupon.setCode(request.getCode());
        existingCoupon.setDescription(request.getDescription());
        existingCoupon.setExpirationDate(request.getExpirationDate());

        if (request instanceof CartWiseCouponRequest cartWiseRequest && existingCoupon instanceof CartWiseCoupon) {
            CartWiseCoupon coupon = (CartWiseCoupon) existingCoupon;
            coupon.setThreshold(cartWiseRequest.getThreshold());
            coupon.setDiscountPercentage(cartWiseRequest.getDiscountPercentage());
            coupon.setMaxDiscountAmount(cartWiseRequest.getMaxDiscountAmount());
        } else if (request instanceof ProductWiseCouponRequest productWiseRequest && existingCoupon instanceof ProductWiseCoupon) {
            ProductWiseCoupon coupon = (ProductWiseCoupon) existingCoupon;
            coupon.setProductId(productWiseRequest.getProductId());
            coupon.setDiscountPercentage(productWiseRequest.getDiscountPercentage());
            coupon.setFixedDiscountAmount(productWiseRequest.getFixedDiscountAmount());
            coupon.setMinQuantity(productWiseRequest.getMinQuantity());
        } else if (request instanceof BxGyCouponRequest bxGyRequest && existingCoupon instanceof BxGyCoupon) {
            BxGyCoupon coupon = (BxGyCoupon) existingCoupon;
            coupon.setBuyProducts(bxGyRequest.getBuyProducts());
            coupon.setGetProducts(bxGyRequest.getGetProducts());
            coupon.setRepetitionLimit(bxGyRequest.getRepetitionLimit());
        } else {
            throw new InvalidCouponRequestException("Mismatched coupon type for update or unknown request type");
        }

        coupons.put(id, existingCoupon); // Re-put to ensure thread safety if ConcurrentHashMap is used
        return convertToDto(existingCoupon);
    }

    public void deleteCoupon(Long id) {
        if (!coupons.containsKey(id)) {
            throw new CouponNotFoundException("Coupon with id " + id + " not found");
        }
        coupons.remove(id);
    }

    public List<ApplicableCouponResponse> getApplicableCoupons(Cart cart) {
        // Calculate totalOriginalPrice for the cart
        cart.setTotalOriginalPrice(cart.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        List<ApplicableCouponResponse> applicableCoupons = new ArrayList<>();
        for (Coupon coupon : coupons.values()) {
            if (coupon.getExpirationDate() != null && coupon.getExpirationDate().isBefore(LocalDate.now())) {
                continue; // Skip expired coupons
            }

            CouponStrategy strategy = couponStrategyFactory.getStrategy(coupon.getType());
            if (strategy.isApplicable(cart, coupon)) {
                BigDecimal discountAmount = strategy.calculateDiscount(cart, coupon);
                applicableCoupons.add(ApplicableCouponResponse.builder()
                        .couponId(coupon.getId())
                        .type(coupon.getType())
                        .discountAmount(discountAmount)
                        .build());
            }
        }
        return applicableCoupons;
    }

    public Cart applyCoupon(Long couponId, Cart cart) {
        Coupon coupon = coupons.get(couponId);
        if (coupon == null) {
            throw new CouponNotFoundException("Coupon with id " + couponId + " not found");
        }
        if (coupon.getExpirationDate() != null && coupon.getExpirationDate().isBefore(LocalDate.now())) {
            throw new CouponExpiredException("Coupon with id " + couponId + " has expired");
        }

        // Calculate totalOriginalPrice for the cart
        cart.setTotalOriginalPrice(cart.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        CouponStrategy strategy = couponStrategyFactory.getStrategy(coupon.getType());
        if (!strategy.isApplicable(cart, coupon)) {
            throw new CouponNotApplicableException("Coupon with id " + couponId + " is not applicable to the cart");
        }

        // Apply discount and update cart totals
        Cart updatedCart = strategy.applyDiscount(cart, coupon);

        // Ensure finalPrice and totalDiscount are set even if strategy doesn't fully set them
        if (updatedCart.getTotalDiscount() == null) {
            updatedCart.setTotalDiscount(BigDecimal.ZERO);
        }
        if (updatedCart.getFinalPrice() == null) {
            updatedCart.setFinalPrice(updatedCart.getTotalOriginalPrice().subtract(updatedCart.getTotalDiscount()));
        }

        return updatedCart;
    }

    private CouponResponseDTO convertToDto(Coupon coupon) {
        if (coupon instanceof CartWiseCoupon cartWiseCoupon) {
            CartWiseCouponResponse dto = new CartWiseCouponResponse();
            dto.setId(cartWiseCoupon.getId());
            dto.setType(cartWiseCoupon.getType());
            dto.setCode(cartWiseCoupon.getCode());
            dto.setDescription(cartWiseCoupon.getDescription());
            dto.setExpirationDate(cartWiseCoupon.getExpirationDate());
            dto.setThreshold(cartWiseCoupon.getThreshold());
            dto.setDiscountPercentage(cartWiseCoupon.getDiscountPercentage());
            dto.setMaxDiscountAmount(cartWiseCoupon.getMaxDiscountAmount());
            return dto;
        } else if (coupon instanceof ProductWiseCoupon productWiseCoupon) {
            ProductWiseCouponResponse dto = new ProductWiseCouponResponse();
            dto.setId(productWiseCoupon.getId());
            dto.setType(productWiseCoupon.getType());
            dto.setCode(productWiseCoupon.getCode());
            dto.setDescription(productWiseCoupon.getDescription());
            dto.setExpirationDate(productWiseCoupon.getExpirationDate());
            dto.setProductId(productWiseCoupon.getProductId());
            dto.setDiscountPercentage(productWiseCoupon.getDiscountPercentage());
            dto.setFixedDiscountAmount(productWiseCoupon.getFixedDiscountAmount());
            dto.setMinQuantity(productWiseCoupon.getMinQuantity());
            return dto;
        } else if (coupon instanceof BxGyCoupon bxGyCoupon) {
            BxGyCouponResponse dto = new BxGyCouponResponse();
            dto.setId(bxGyCoupon.getId());
            dto.setType(bxGyCoupon.getType());
            dto.setCode(bxGyCoupon.getCode());
            dto.setDescription(bxGyCoupon.getDescription());
            dto.setExpirationDate(bxGyCoupon.getExpirationDate());
            dto.setBuyProducts(bxGyCoupon.getBuyProducts());
            dto.setGetProducts(bxGyCoupon.getGetProducts());
            dto.setRepetitionLimit(bxGyCoupon.getRepetitionLimit());
            return dto;
        }
        throw new InvalidCouponRequestException("Unknown coupon type for DTO conversion");
    }
}
