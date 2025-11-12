package com.example.demo.dto;

import java.util.List;

import com.example.demo.model.BxGyProductDetail;

import lombok.Data;

@Data
public class BxGyCouponRequest implements CouponRequestDTO {
    private List<BxGyProductDetail> buyProducts;
    private List<BxGyProductDetail> getProducts;
    private Integer repetitionLimit;
}
