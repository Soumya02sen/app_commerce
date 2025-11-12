# Unit Tests Documentation

## Overview
Comprehensive unit test suite for the E-Commerce Coupon Management System using **JUnit 5** and **Mockito**.

**Test Framework:** JUnit 5 (Jupiter)  
**Mocking Framework:** Mockito  
**Total Tests:** 91 (All Passing ✅)

---

## Test Files Created

### 1. **GlobalExceptionHandlerTest** 
**Location:** `src/test/java/com/example/demo/exception/GlobalExceptionHandlerTest.java`  
**Tests:** 9

Tests for REST API global exception handling and error responses.

**Test Cases:**
- `testHandleCouponNotFoundException()` - Verifies 404 response for missing coupon
- `testHandleCouponExpiredException()` - Verifies 400 response for expired coupon
- `testHandleCouponNotApplicableException()` - Verifies invalid coupon scenario
- `testHandleInvalidCouponRequestException()` - Verifies malformed request handling
- `testHandleIllegalArgumentException()` - Verifies illegal argument exception
- `testHandleMethodArgumentNotValidException()` - Verifies validation error response
- `testHandleAllUncaughtException()` - Verifies 500 error for unexpected exceptions
- `testResponseBodyStructure()` - Validates response body structure
- `testMultipleExceptionsWithBadRequestHandler()` - Tests multiple exception types

**Key Assertions:**
- HTTP status codes (200, 400, 404, 500)
- Response body structure (timestamp, message, errors)
- Exception messages propagation

---

### 2. **CouponStrategyFactoryTest**
**Location:** `src/test/java/com/example/demo/service/CouponStrategyFactoryTest.java`  
**Tests:** 5

Tests the Strategy Pattern factory for coupon type selection.

**Test Cases:**
- `testGetStrategyForCartWiseCoupon()` - Verifies CartWiseCouponStrategy selection
- `testGetStrategyForProductWiseCoupon()` - Verifies ProductWiseCouponStrategy selection
- `testGetStrategyForBxGyCoupon()` - Verifies BxGyCouponStrategy selection
- `testGetStrategyNeverReturnsNull()` - Ensures all coupon types have strategies
- `testGetStrategyConsistency()` - Verifies consistent strategy type selection

**Key Features:**
- Uses `@SpringBootTest` for Spring context loading
- Tests all CouponType enum values
- Validates strategy instance types

---

### 3. **CartWiseCouponTest**
**Location:** `src/test/java/com/example/demo/model/CartWiseCouponTest.java`  
**Tests:** 7

Unit tests for CartWiseCoupon model using Lombok builders.

**Test Cases:**
- `testCartWiseCouponCreationUsingBuilder()` - Builder pattern construction
- `testCartWiseCouponGetterSetters()` - Property accessors
- `testCartWiseCouponWithMinimalFields()` - Minimal field requirement
- `testCartWiseCouponEquality()` - Object equality comparison
- `testCartWiseCouponToString()` - String representation
- `testCartWiseCouponHashCode()` - Hash code consistency
- `testCartWiseCouponDefaultValues()` - Default values validation

**BigDecimal Handling:**
- Uses `.compareTo()` for BigDecimal assertions instead of `equals()`
- Handles scale differences properly

---

### 4. **ProductWiseCouponTest**
**Location:** `src/test/java/com/example/demo/model/ProductWiseCouponTest.java`  
**Tests:** 8

Unit tests for ProductWiseCoupon model including Integer and BigDecimal fields.

**Test Cases:**
- `testProductWiseCouponCreationUsingBuilder()` - Builder creation
- `testProductWiseCouponGetterSetters()` - Property accessors
- `testProductWiseCouponWithNullOptionalFields()` - Optional field handling
- `testProductWiseCouponEquality()` - Equality testing
- `testProductWiseCouponInequality()` - Inequality testing
- `testProductWiseCouponHashCode()` - Hash consistency
- `testProductWiseCouponDefaultValues()` - Default values
- `testProductWiseCouponToString()` - String representation

**Field Type Coverage:**
- Long: productId
- BigDecimal: discountPercentage, fixedDiscountAmount
- Integer: minQuantity

---

### 5. **CartItemTest**
**Location:** `src/test/java/com/example/demo/dto/CartItemTest.java`  
**Tests:** 5

Tests for CartItem data transfer object.

**Test Cases:**
- `testCartItemBuilderCreation()` - Builder pattern construction
- `testCartItemDefaultQuantity()` - Default quantity handling
- `testCartItemSettersAndGetters()` - Property accessors
- `testCartItemEquality()` - Object equality
- `testCartItemToString()` - String representation

**Coverage:**
- productId (Long)
- quantity (Integer)
- price (BigDecimal)
- Lombok @Builder and @Data annotations

---

### 6. **CartTest**
**Location:** `src/test/java/com/example/demo/dto/CartTest.java`  
**Tests:** 8

Comprehensive tests for Cart data transfer object.

**Test Cases:**
- `testCartBuilderCreation()` - Builder pattern with items
- `testCartWithEmptyItems()` - Empty cart handling
- `testCartSettersAndGetters()` - Property accessors
- `testCartAddingItemsDynamically()` - Dynamic item addition
- `testCartEquality()` - Object equality
- `testCartToString()` - String representation
- `testCartWithNullFields()` - Null field handling
- `testCartConstructor()` - AllArgsConstructor testing

**Price Calculations:**
- totalOriginalPrice
- totalDiscount
- finalPrice

---

## Existing Test Files (Already in Project)

### Service Tests
- **CouponServiceTest** (15 tests) - Service layer business logic
- **CouponControllerIntegrationTest** (11 tests) - REST API integration

### Strategy Tests
- **ProductWiseCouponStrategyTest** (9 tests) - Product discount logic
- **BxGyCouponStrategyTest** (8 tests) - Buy X Get Y logic
- **CartWiseCouponStrategyTest** (6 tests) - Cart-level discount logic

---

## Test Execution Summary

### Command to Run Tests

**All Tests:**
```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
mvn clean test
```

**Specific Test Class:**
```bash
mvn test -Dtest=GlobalExceptionHandlerTest
```

**Specific Test Method:**
```bash
mvn test -Dtest=GlobalExceptionHandlerTest#testHandleCouponNotFoundException
```

### Test Results
```
Tests run: 91
Failures: 0
Errors: 0
Skipped: 0
Time elapsed: ~2.9 seconds
```

---

## Testing Best Practices Used

### 1. **Arrange-Act-Assert Pattern**
Every test follows the AAA pattern:
```java
// Arrange - Setup test data
CartWiseCoupon coupon = CartWiseCoupon.builder()
    .id(1L)
    .type(CouponType.CART_WISE)
    .threshold(BigDecimal.valueOf(1000))
    .build();

// Act - Execute the code being tested
String result = coupon.toString();

// Assert - Verify results
assertNotNull(result);
assertTrue(result.contains("CartWiseCoupon"));
```

### 2. **Mockito Best Practices**
- Uses `@ExtendWith(MockitoExtension.class)` for JUnit 5 integration
- Wraps mock setup with `lenient()` for optional stubbing
- Uses `@Mock` and `@InjectMocks` for dependency injection

### 3. **BigDecimal Comparison**
Always use `.compareTo()` instead of `equals()` for BigDecimal:
```java
// Correct
assertEquals(0, BigDecimal.valueOf(10).compareTo(actual));

// Incorrect - fails due to scale differences
assertEquals(BigDecimal.valueOf(10), actual);
```

### 4. **Builder Pattern Testing**
Tests Lombok `@Builder` and `@SuperBuilder` annotations:
```java
CartWiseCoupon coupon = CartWiseCoupon.builder()
    .id(1L)
    .type(CouponType.CART_WISE)
    .code("CART10")
    .threshold(BigDecimal.valueOf(1000))
    .discountPercentage(BigDecimal.valueOf(10))
    .maxDiscountAmount(BigDecimal.valueOf(500))
    .build();
```

### 5. **Spring Boot Test Context**
Uses `@SpringBootTest` for integration tests requiring Spring:
```java
@ExtendWith(SpringExtension.class)
@SpringBootTest
class CouponStrategyFactoryTest {
    @Autowired
    private CouponStrategyFactory couponStrategyFactory;
}
```

---

## Key Testing Features

### Exception Handling
- Custom exceptions tested: CouponNotFoundException, CouponExpiredException, etc.
- Response body structure validation
- HTTP status code verification

### Object Equality & Hashing
- `equals()` and `hashCode()` consistency
- Lombok `@Data` and `@EqualsAndHashCode` integration

### Type Safety
- Generics testing with `@SuppressWarnings("unchecked")`
- Instance type verification with `instanceof`

### Null Safety
- Null field handling
- Default value testing
- Optional field scenarios

---

## Code Coverage Improvements

### New Test Coverage
- **Exception Handling:** 9 tests covering all exception handlers
- **Factory Pattern:** 5 tests for coupon strategy selection
- **Model Classes:** 15 tests for Coupon DTOs and Models
- **Data Classes:** 13 tests for Cart and CartItem DTOs

### Total Coverage
- **91 Tests** in comprehensive test suite
- **All critical paths** covered
- **Edge cases** included
- **100% build success rate** with Java 17 LTS

---

## Integration with CI/CD

These tests are designed to:
1. **Run automatically** during Maven build (`mvn clean test`)
2. **Provide fast feedback** (~3 seconds for full suite)
3. **Ensure quality gates** before deployment
4. **Enable refactoring** with confidence

---

## Troubleshooting

### Common Issues & Solutions

**Issue:** Tests fail with Java 25
```bash
# Solution: Set JAVA_HOME to Java 17
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
```

**Issue:** BigDecimal assertion failures
```java
// Use compareTo() instead of equals()
assertEquals(0, BigDecimal.valueOf(10).compareTo(actual));
```

**Issue:** Mockito "Unnecessary stubbing" errors
```java
// Wrap setup with lenient()
lenient().when(mock.method()).thenReturn(value);
```

---

## Future Enhancements

- Add performance benchmarking tests
- Implement property-based testing with QuickCheck
- Add test data builders (TestDataBuilder pattern)
- Implement mutation testing for quality metrics
- Add integration tests with test containers (H2 database, Redis)

---

**Last Updated:** November 12, 2025  
**Project:** E-Commerce Coupon Management System  
**Java Version:** 17 LTS  
**Test Framework:** JUnit 5 + Mockito
