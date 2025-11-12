package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {
    @Valid
    @NotNull
    @Size(min = 1)
    private List<CartItem> items;

    private BigDecimal totalOriginalPrice;
    private BigDecimal totalDiscount;
    private BigDecimal finalPrice;
}
