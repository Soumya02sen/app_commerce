package com.example.demo.dto;

import java.time.LocalDate;
import com.example.demo.model.CouponType;

public interface CouponResponseDTO {
    Long getId();
    CouponType getType();
    String getCode();
    String getDescription();
    LocalDate getExpirationDate();
}
