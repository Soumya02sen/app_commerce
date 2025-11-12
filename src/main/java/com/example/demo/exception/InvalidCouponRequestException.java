package com.example.demo.exception;

public class InvalidCouponRequestException extends RuntimeException {
    public InvalidCouponRequestException(String message) {
        super(message);
    }
}
