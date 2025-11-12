package com.example.demo.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = CartWiseCouponRequest.class, name = "CART_WISE"),
    @JsonSubTypes.Type(value = ProductWiseCouponRequest.class, name = "PRODUCT_WISE"),
    @JsonSubTypes.Type(value = BxGyCouponRequest.class, name = "BXGY")
})
public abstract class CouponRequestDTO {
    @NotNull
    private String code;
    private String description;
    @FutureOrPresent
    private LocalDate expirationDate;
}
