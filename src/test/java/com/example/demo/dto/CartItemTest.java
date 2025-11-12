package com.example.demo.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class CartItemTest {

    @Test
    void testCartItemBuilderCreation() {
        // Arrange & Act
        CartItem cartItem = CartItem.builder()
            .productId(1L)
            .quantity(5)
            .price(BigDecimal.valueOf(100))
            .build();

        // Assert
        assertEquals(1L, cartItem.getProductId());
        assertEquals(5, cartItem.getQuantity());
        assertEquals(0, BigDecimal.valueOf(100).compareTo(cartItem.getPrice()));
    }

    @Test
    void testCartItemDefaultQuantity() {
        // Arrange & Act
        CartItem cartItem = CartItem.builder()
            .productId(1L)
            .price(BigDecimal.valueOf(100))
            .quantity(1)  // Setting default quantity
            .build();

        // Assert
        assertNotNull(cartItem.getQuantity());
        assertEquals(1, cartItem.getQuantity());
    }

    @Test
    void testCartItemSettersAndGetters() {
        // Arrange
        CartItem cartItem = new CartItem();

        // Act
        cartItem.setProductId(2L);
        cartItem.setQuantity(10);
        cartItem.setPrice(BigDecimal.valueOf(250));

        // Assert
        assertEquals(2L, cartItem.getProductId());
        assertEquals(10, cartItem.getQuantity());
        assertEquals(0, BigDecimal.valueOf(250).compareTo(cartItem.getPrice()));
    }

    @Test
    void testCartItemEquality() {
        // Arrange & Act
        CartItem item1 = CartItem.builder()
            .productId(1L)
            .quantity(5)
            .price(BigDecimal.valueOf(100))
            .build();

        CartItem item2 = CartItem.builder()
            .productId(1L)
            .quantity(5)
            .price(BigDecimal.valueOf(100))
            .build();

        // Assert
        assertEquals(item1, item2);
    }

    @Test
    void testCartItemToString() {
        // Arrange & Act
        CartItem cartItem = CartItem.builder()
            .productId(1L)
            .quantity(5)
            .price(BigDecimal.valueOf(100))
            .build();

        String toString = cartItem.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("CartItem"));
    }
}
