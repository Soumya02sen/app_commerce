package com.example.demo.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
public abstract class CouponRequestDTO {
    @NotNull
    private String code;
    private String description;
    @FutureOrPresent
    private LocalDate expirationDate;
}
