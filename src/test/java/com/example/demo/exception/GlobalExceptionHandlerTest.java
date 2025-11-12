package com.example.demo.exception;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void testHandleCouponNotFoundException() {
        // Arrange
        String exceptionMessage = "Coupon not found with id: 999";
        CouponNotFoundException exception = new CouponNotFoundException(exceptionMessage);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleCouponNotFoundException(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(exceptionMessage, body.get("message"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void testHandleCouponExpiredException() {
        // Arrange
        String exceptionMessage = "Coupon has expired";
        CouponExpiredException exception = new CouponExpiredException(exceptionMessage);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleBadRequestExceptions(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(exceptionMessage, body.get("message"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void testHandleCouponNotApplicableException() {
        // Arrange
        String exceptionMessage = "Coupon is not applicable for this cart";
        CouponNotApplicableException exception = new CouponNotApplicableException(exceptionMessage);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleBadRequestExceptions(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(exceptionMessage, body.get("message"));
    }

    @Test
    void testHandleInvalidCouponRequestException() {
        // Arrange
        String exceptionMessage = "Invalid coupon request";
        InvalidCouponRequestException exception = new InvalidCouponRequestException(exceptionMessage);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleBadRequestExceptions(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(exceptionMessage, body.get("message"));
    }

    @Test
    void testHandleIllegalArgumentException() {
        // Arrange
        String exceptionMessage = "Illegal argument provided";
        IllegalArgumentException exception = new IllegalArgumentException(exceptionMessage);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleBadRequestExceptions(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(exceptionMessage, body.get("message"));
    }

    @Test
    void testHandleMethodArgumentNotValidException() throws Exception {
        // Arrange
        @SuppressWarnings("unchecked")
        MethodParameter parameter = mock(MethodParameter.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        ObjectError error1 = new ObjectError("field1", "Field 1 is required");
        ObjectError error2 = new ObjectError("field2", "Field 2 must be a valid email");
        
        when(bindingResult.getAllErrors()).thenReturn(java.util.Arrays.asList(error1, error2));
        
        @SuppressWarnings("null")
        MethodArgumentNotValidException exception = 
            new MethodArgumentNotValidException(parameter, bindingResult);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleMethodArgumentNotValid(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Validation Error", body.get("message"));
        assertNotNull(body.get("errors"));
        assertTrue(body.containsKey("timestamp"));
    }

    @Test
    void testHandleAllUncaughtException() {
        // Arrange
        String exceptionMessage = "Something went wrong";
        Exception exception = new Exception(exceptionMessage);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleAllUncaughtException(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        String message = body.get("message").toString();
        assertTrue(message.contains("An unexpected error occurred"));
        assertTrue(message.contains(exceptionMessage));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void testResponseBodyStructure() {
        // Arrange
        String exceptionMessage = "Test exception";
        CouponNotFoundException exception = new CouponNotFoundException(exceptionMessage);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleCouponNotFoundException(exception);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();

        // Assert
        assertTrue(body.containsKey("timestamp"));
        assertTrue(body.containsKey("message"));
        assertTrue(body.get("timestamp") instanceof LocalDateTime);
        assertTrue(body.get("message") instanceof String);
    }

    @Test
    void testMultipleExceptionsWithBadRequestHandler() {
        // Test that handler works with different exception types
        Exception[] exceptions = {
            new CouponExpiredException("Expired"),
            new CouponNotApplicableException("Not applicable"),
            new InvalidCouponRequestException("Invalid request"),
            new IllegalArgumentException("Illegal arg")
        };

        for (Exception ex : exceptions) {
            ResponseEntity<Object> response = 
                globalExceptionHandler.handleBadRequestExceptions((RuntimeException) ex);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
        }
    }
}
