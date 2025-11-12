package com.example.demo.dto;

import java.util.List;

import com.example.demo.model.BxGyProductDetail;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@SuperBuilder
public class BxGyCouponRequest extends CouponRequestDTO {
    private List<BxGyProductDetail> buyProducts;
    private List<BxGyProductDetail> getProducts;
    private Integer repetitionLimit;
}
