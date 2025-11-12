package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;

import com.example.demo.model.BxGyProductDetail;
import com.example.demo.model.CouponType;

import lombok.Data;

@Data
public class BxGyCouponResponse implements CouponResponseDTO {
    private Long id;
    private CouponType type;
    private String code;
    private String description;
    private LocalDate expirationDate;
    private List<BxGyProductDetail> buyProducts;
    private List<BxGyProductDetail> getProducts;
    private Integer repetitionLimit;
}
