package com.example.demo.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class CartTest {

    @Test
    void testCartBuilderCreation() {
        // Arrange
        List<CartItem> items = Arrays.asList(
            CartItem.builder().productId(1L).quantity(5).price(BigDecimal.valueOf(100)).build(),
            CartItem.builder().productId(2L).quantity(3).price(BigDecimal.valueOf(200)).build()
        );

        // Act
        Cart cart = Cart.builder()
            .items(items)
            .totalOriginalPrice(BigDecimal.valueOf(1100))
            .totalDiscount(BigDecimal.valueOf(100))
            .finalPrice(BigDecimal.valueOf(1000))
            .build();

        // Assert
        assertEquals(2, cart.getItems().size());
        assertEquals(0, BigDecimal.valueOf(1100).compareTo(cart.getTotalOriginalPrice()));
        assertEquals(0, BigDecimal.valueOf(100).compareTo(cart.getTotalDiscount()));
        assertEquals(0, BigDecimal.valueOf(1000).compareTo(cart.getFinalPrice()));
    }

    @Test
    void testCartWithEmptyItems() {
        // Arrange & Act
        Cart cart = Cart.builder()
            .items(new ArrayList<>())
            .totalOriginalPrice(BigDecimal.ZERO)
            .totalDiscount(BigDecimal.ZERO)
            .finalPrice(BigDecimal.ZERO)
            .build();

        // Assert
        assertEquals(0, cart.getItems().size());
        assertEquals(0, BigDecimal.ZERO.compareTo(cart.getTotalOriginalPrice()));
        assertEquals(0, BigDecimal.ZERO.compareTo(cart.getTotalDiscount()));
        assertEquals(0, BigDecimal.ZERO.compareTo(cart.getFinalPrice()));
    }

    @Test
    void testCartSettersAndGetters() {
        // Arrange
        Cart cart = new Cart();
        List<CartItem> items = new ArrayList<>();

        // Act
        cart.setItems(items);
        cart.setTotalOriginalPrice(BigDecimal.valueOf(500));
        cart.setTotalDiscount(BigDecimal.valueOf(50));
        cart.setFinalPrice(BigDecimal.valueOf(450));

        // Assert
        assertEquals(items, cart.getItems());
        assertEquals(0, BigDecimal.valueOf(500).compareTo(cart.getTotalOriginalPrice()));
        assertEquals(0, BigDecimal.valueOf(50).compareTo(cart.getTotalDiscount()));
        assertEquals(0, BigDecimal.valueOf(450).compareTo(cart.getFinalPrice()));
    }

    @Test
    void testCartAddingItemsDynamically() {
        // Arrange
        Cart cart = Cart.builder()
            .items(new ArrayList<>())
            .totalOriginalPrice(BigDecimal.ZERO)
            .totalDiscount(BigDecimal.ZERO)
            .finalPrice(BigDecimal.ZERO)
            .build();

        CartItem item = CartItem.builder()
            .productId(1L)
            .quantity(2)
            .price(BigDecimal.valueOf(150))
            .build();

        // Act
        cart.getItems().add(item);
        cart.setTotalOriginalPrice(BigDecimal.valueOf(300));
        cart.setFinalPrice(BigDecimal.valueOf(300));

        // Assert
        assertEquals(1, cart.getItems().size());
        assertTrue(cart.getItems().contains(item));
        assertEquals(0, BigDecimal.valueOf(300).compareTo(cart.getTotalOriginalPrice()));
    }

    @Test
    void testCartEquality() {
        // Arrange
        List<CartItem> items = Arrays.asList(
            CartItem.builder().productId(1L).quantity(5).price(BigDecimal.valueOf(100)).build()
        );

        Cart cart1 = Cart.builder()
            .items(items)
            .totalOriginalPrice(BigDecimal.valueOf(500))
            .totalDiscount(BigDecimal.valueOf(50))
            .finalPrice(BigDecimal.valueOf(450))
            .build();

        Cart cart2 = Cart.builder()
            .items(items)
            .totalOriginalPrice(BigDecimal.valueOf(500))
            .totalDiscount(BigDecimal.valueOf(50))
            .finalPrice(BigDecimal.valueOf(450))
            .build();

        // Assert
        assertEquals(cart1, cart2);
    }

    @Test
    void testCartToString() {
        // Arrange & Act
        Cart cart = Cart.builder()
            .items(new ArrayList<>())
            .totalOriginalPrice(BigDecimal.valueOf(500))
            .finalPrice(BigDecimal.valueOf(500))
            .build();

        String toString = cart.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("Cart"));
    }

    @Test
    void testCartWithNullFields() {
        // Arrange & Act
        Cart cart = new Cart();

        // Assert
        assertNull(cart.getItems());
        assertNull(cart.getTotalOriginalPrice());
        assertNull(cart.getTotalDiscount());
        assertNull(cart.getFinalPrice());
    }

    @Test
    void testCartConstructor() {
        // Arrange
        List<CartItem> items = Arrays.asList(
            CartItem.builder().productId(1L).quantity(2).price(BigDecimal.valueOf(100)).build()
        );

        // Act
        Cart cart = new Cart(items, BigDecimal.valueOf(200), BigDecimal.valueOf(20), BigDecimal.valueOf(180));

        // Assert
        assertEquals(items, cart.getItems());
        assertEquals(0, BigDecimal.valueOf(200).compareTo(cart.getTotalOriginalPrice()));
        assertEquals(0, BigDecimal.valueOf(20).compareTo(cart.getTotalDiscount()));
        assertEquals(0, BigDecimal.valueOf(180).compareTo(cart.getFinalPrice()));
    }
}
