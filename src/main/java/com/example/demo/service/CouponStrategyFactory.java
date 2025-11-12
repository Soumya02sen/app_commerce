package com.example.demo.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.demo.model.CouponType;
import com.example.demo.strategy.CouponStrategy;

@Component
public class CouponStrategyFactory {
    private final Map<CouponType, CouponStrategy> strategies;

    public CouponStrategyFactory(List<CouponStrategy> couponStrategies) {
        strategies = couponStrategies.stream()
                .collect(Collectors.toMap(CouponStrategy::getCouponType, Function.identity()));
    }

    public CouponStrategy getStrategy(CouponType couponType) {
        return Optional.ofNullable(strategies.get(couponType))
                .orElseThrow(() -> new IllegalArgumentException("Unknown coupon type: " + couponType));
    }
}
