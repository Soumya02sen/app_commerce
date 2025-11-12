package com.example.demo.strategy;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.demo.dto.Cart;
import com.example.demo.dto.CartItem;
import com.example.demo.model.BxGyCoupon;
import com.example.demo.model.BxGyProductDetail;
import com.example.demo.model.Coupon;
import com.example.demo.model.CouponType;

@Component
public class BxGyCouponStrategy implements CouponStrategy {

    @Override
    public CouponType getCouponType() {
        return CouponType.BXGY;
    }

    @Override
    public boolean isApplicable(Cart cart, Coupon coupon) {
        BxGyCoupon bxGyCoupon = (BxGyCoupon) coupon;
        Map<Long, Integer> cartProductQuantities = cart.getItems().stream()
                .collect(Collectors.toMap(CartItem::getProductId, CartItem::getQuantity));

        for (BxGyProductDetail buyProduct : bxGyCoupon.getBuyProducts()) {
            if (!cartProductQuantities.containsKey(buyProduct.getProductId()) ||
                    cartProductQuantities.get(buyProduct.getProductId()) < buyProduct.getQuantity()) {
                return false;
            }
        }
        for (BxGyProductDetail getProduct : bxGyCoupon.getGetProducts()) {
            if (!cartProductQuantities.containsKey(getProduct.getProductId()) ||
                    cartProductQuantities.get(getProduct.getProductId()) < getProduct.getQuantity()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public BigDecimal calculateDiscount(Cart cart, Coupon coupon) {
        if (!isApplicable(cart, coupon)) {
            return BigDecimal.ZERO;
        }

        BxGyCoupon bxGyCoupon = (BxGyCoupon) coupon;
        Map<Long, CartItem> cartItemsMap = cart.getItems().stream()
                .collect(Collectors.toMap(CartItem::getProductId, Function.identity()));

        int maxRepetitions = bxGyCoupon.getRepetitionLimit();

        for (BxGyProductDetail buyProduct : bxGyCoupon.getBuyProducts()) {
            int repetitions = cartItemsMap.get(buyProduct.getProductId()).getQuantity() / buyProduct.getQuantity();
            maxRepetitions = Math.min(maxRepetitions, repetitions);
        }

        BigDecimal totalDiscount = BigDecimal.ZERO;
        for (int i = 0; i < maxRepetitions; i++) {
            for (BxGyProductDetail getProduct : bxGyCoupon.getGetProducts()) {
                CartItem item = cartItemsMap.get(getProduct.getProductId());
                totalDiscount = totalDiscount.add(item.getPrice().multiply(BigDecimal.valueOf(getProduct.getQuantity())));
            }
        }

        return totalDiscount;
    }

    @Override
    public Cart applyDiscount(Cart cart, Coupon coupon) {
        if (!isApplicable(cart, coupon)) {
            return cart;
        }

        BxGyCoupon bxGyCoupon = (BxGyCoupon) coupon;
        Map<Long, CartItem> cartItemsMap = cart.getItems().stream()
                .collect(Collectors.toMap(CartItem::getProductId, Function.identity()));

        int maxRepetitions = bxGyCoupon.getRepetitionLimit();

        for (BxGyProductDetail buyProduct : bxGyCoupon.getBuyProducts()) {
            int repetitions = cartItemsMap.get(buyProduct.getProductId()).getQuantity() / buyProduct.getQuantity();
            maxRepetitions = Math.min(maxRepetitions, repetitions);
        }

        BigDecimal totalDiscount = BigDecimal.ZERO;
        for (int i = 0; i < maxRepetitions; i++) {
            for (BxGyProductDetail getProduct : bxGyCoupon.getGetProducts()) {
                CartItem item = cartItemsMap.get(getProduct.getProductId());
                BigDecimal discount = item.getPrice().multiply(BigDecimal.valueOf(getProduct.getQuantity()));
                item.setTotalDiscount(item.getTotalDiscount().add(discount));
                item.setFinalPrice(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())).subtract(item.getTotalDiscount()));
                totalDiscount = totalDiscount.add(discount);
            }
        }

        for (CartItem item : cart.getItems()) {
            if (item.getFinalPrice() == null) {
                item.setFinalPrice(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }

        cart.setTotalDiscount(totalDiscount);
        cart.setFinalPrice(cart.getTotalOriginalPrice().subtract(totalDiscount));
        return cart;
    }
}
