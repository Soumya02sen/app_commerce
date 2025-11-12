# Implementation Plan: Coupon Management API

This document outlines a simplified implementation plan for the Coupon Management API, focusing on core requirements and extensibility while keeping the technology stack minimal.

## Architecture Overview

```mermaid
graph TD
    User -->|HTTP Request| Controller
    Controller -->|Calls| Service
    Service -->|Manages| In-Memory Collection
    Service -->|Uses| CouponStrategyFactory
    CouponStrategyFactory -->|Provides| CouponStrategy
    CouponStrategy -->|Applies Logic| Cart (DTO)
    Controller --x |Handles| GlobalExceptionHandler
```

## 1. Project Setup and Configuration

*   **`pom.xml`:**
    *   **Dependencies:**
        *   `spring-boot-starter-web`: For building RESTful APIs.
        *   `lombok`: To reduce boilerplate code (getters, setters, constructors).
        *   `spring-boot-starter-validation`: For JSR 380 Bean Validation.
    *   **Java Version:** 17 (as per initial `pom.xml`).


## 2. Data Model (Entities and Enums)

### 2.1 `CouponType.java` (Enum)
Defines the types of coupons.
```java
public enum CouponType {
    CART_WISE,
    PRODUCT_WISE,
    BXGY
}
```

### 2.2 `Coupon.java` (Abstract Base Class for In-Memory)
This abstract class will serve as the base for all coupon types. IDs will be managed in-memory.
```java
@Data // Lombok
@NoArgsConstructor // Lombok
@SuperBuilder // Lombok
public abstract class Coupon {
    private Long id; // Managed in-memory

    @NotNull
    private CouponType type;

    @NotNull
    private String code; // Unique identifier for the coupon

    private String description;

    @FutureOrPresent // Bonus: expiration date (validation still applies)
    private LocalDate expirationDate;
}
```

### 2.3 Concrete Coupon Entities (Extending `Coupon`)

*   **`CartWiseCoupon.java`**
    ```java
    @Data @EqualsAndHashCode(callSuper = true) @NoArgsConstructor @SuperBuilder
    public class CartWiseCoupon extends Coupon {
        @NotNull @DecimalMin(value = "0.0", inclusive = false)
        private BigDecimal threshold; // Minimum cart total for applicability
        @NotNull @DecimalMin(value = "0.0", inclusive = false)
        private BigDecimal discountPercentage; // e.g., 10 for 10%
        private BigDecimal maxDiscountAmount; // Optional cap on discount
    }
    ```

*   **`ProductWiseCoupon.java`**
    ```java
    @Data @EqualsAndHashCode(callSuper = true) @NoArgsConstructor @SuperBuilder
    public class ProductWiseCoupon extends Coupon {
        @NotNull
        private Long productId; // ID of the product to which the discount applies
        @DecimalMin(value = "0.0", inclusive = false)
        private BigDecimal discountPercentage; // e.g., 20 for 20%
        @DecimalMin(value = "0.0", inclusive = false)
        private BigDecimal fixedDiscountAmount; // Optional: fixed amount off per product
        @Min(value = 1)
        private Integer minQuantity; // Optional: minimum quantity of product for discount
    }
    ```

*   **`BxGyProductDetail.java`**
    Helper class for `BxGyCoupon`.
    ```java
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public class BxGyProductDetail {
        @NotNull
        private Long productId;
        @NotNull @Min(value = 1)
        private Integer quantity;
    }
    ```

*   **`BxGyCoupon.java`**
    ```java
    @Data @EqualsAndHashCode(callSuper = true) @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public class BxGyCoupon extends Coupon {
        private List<BxGyProductDetail> buyProducts; // Stored as part of the serialized object
        private List<BxGyProductDetail> getProducts; // Stored as part of the serialized object
        @NotNull @Min(value = 1)
        private Integer repetitionLimit;
    }
    ```

## 3. Data Transfer Objects (DTOs)

These DTOs will be used for API request and response payloads. They are simple POJOs.

*   **`CartItem.java`**
    ```java
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public class CartItem {
        private Long productId;
        private Integer quantity;
        private BigDecimal price; // Unit price
        private BigDecimal totalDiscount = BigDecimal.ZERO; // Output only
        private BigDecimal finalPrice; // Output only
    }
    ```

*   **`Cart.java`**
    ```java
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public class Cart {
        @Valid @NotNull @Size(min = 1)
        private List<CartItem> items;

        // Calculated fields for response
        private BigDecimal totalOriginalPrice;
        private BigDecimal totalDiscount;
        private BigDecimal finalPrice;
    }
    ```

*   **`CouponRequestDTO.java` (Interface/Marker)**
    A marker interface or abstract class for all coupon creation/update requests.
    Concrete DTOs for each type: `CartWiseCouponRequest`, `ProductWiseCouponRequest`, `BxGyCouponRequest`. These will mirror the fields of their respective entities.

*   **`CouponResponseDTO.java` (Interface/Marker)**
    A marker interface or abstract class for all coupon responses.
    Concrete DTOs for each type: `CartWiseCouponResponse`, `ProductWiseCouponResponse`, `BxGyCouponResponse`. These will mirror the fields of their respective entities.

*   **`ApplicableCouponResponse.java`**
    ```java
    @Data @Builder
    public class ApplicableCouponResponse {
        private Long couponId;
        private CouponType type;
        private BigDecimal discountAmount; // Total discount this coupon would provide
    }
    ```



## 5. Service Layer - Core Logic

### 5.1 Coupon Strategy Interface and Implementations

*   **`CouponStrategy.java` (Interface)**
    ```java
    public interface CouponStrategy {
        CouponType getCouponType();
        boolean isApplicable(Cart cart, Coupon coupon);
        BigDecimal calculateDiscount(Cart cart, Coupon coupon);
        Cart applyDiscount(Cart cart, Coupon coupon);
    }
    ```

*   **Concrete Strategy Implementations:**
    *   `CartWiseCouponStrategy.java`
    *   `ProductWiseCouponStrategy.java`
    *   `BxGyCouponStrategy.java`
    Each will implement the `CouponStrategy` interface, injecting necessary dependencies (e.g., `ProductRepository` if product details were external).

### 5.2 `CouponStrategyFactory.java` (Component)
Manages and provides the correct `CouponStrategy` based on `CouponType`.
```java
@Component
public class CouponStrategyFactory {
    private final Map<CouponType, CouponStrategy> strategies;

    public CouponStrategyFactory(List<CouponStrategy> couponStrategies) {
        strategies = couponStrategies.stream()
            .collect(Collectors.toMap(CouponStrategy::getCouponType, Function.identity()));
    }

    public CouponStrategy getStrategy(CouponType couponType) {
        return Optional.ofNullable(strategies.get(couponType))
            .orElseThrow(() -> new IllegalArgumentException("Unknown coupon type: " + couponType));
    }
}
```

### 5.3 `CouponService.java` (Service)
Handles business logic, managing coupons in-memory and orchestrating strategy calls.

*   **CRUD Operations:**
    *   `createCoupon(CouponRequestDTO request)`: Maps DTO to entity, assigns a new ID, and stores in-memory.
    *   `getCouponById(Long id)`: Retrieves entity from in-memory storage.
    *   `getAllCoupons()`: Retrieves all coupons from in-memory storage.
    *   `updateCoupon(Long id, CouponRequestDTO request)`: Finds, updates fields, and stores in-memory.
    *   `deleteCoupon(Long id)`: Deletes by ID from in-memory storage.
    *   **Note:** IDs for new coupons will be generated using an `AtomicLong` to ensure uniqueness in-memory.*   **`getApplicableCoupons(Cart cart)`:**
    1.  Calculate `cart.totalOriginalPrice`.
    2.  Fetch *all* coupons from in-memory storage.
    3.  **Filter active coupons in memory:** Iterate through fetched coupons and filter out expired ones (`coupon.getExpirationDate() == null || coupon.getExpirationDate().isAfter(LocalDate.now())`).
    4.  For each active coupon:
        *   Get `CouponStrategy` from `CouponStrategyFactory`.
        *   Check `isApplicable`.
        *   If applicable, `calculateDiscount`.
        *   Add to `List<ApplicableCouponResponse>`.
    5.  Return the list.

*   **`applyCoupon(Long couponId, Cart cart)`:**
    1.  Retrieve `Coupon` by `couponId` from in-memory storage. Throw `CouponNotFoundException` if not found.
    2.  Check `coupon.expirationDate`. Throw `CouponExpiredException` if expired.
    3.  Get `CouponStrategy` from `CouponStrategyFactory`.
    4.  Check `isApplicable`. Throw `CouponNotApplicableException` if false.
    5.  Call `strategy.applyDiscount(cart, coupon)` to modify `cart.items`.
    6.  Recalculate `cart.totalOriginalPrice`, `cart.totalDiscount`, `cart.finalPrice`.
    7.  Return `cart` (which is now `UpdatedCartResponse`).

## 6. Controller Layer

*   **`CouponController.java` (REST Controller)**
    Exposes API endpoints, handles request/response mapping, and delegates to `CouponService`.
    ```java
    @RestController
    @RequestMapping("/api/v1/coupons")
    public class CouponController {
        private final CouponService couponService;

        // Constructor injection

        @PostMapping
        public ResponseEntity<CouponResponseDTO> createCoupon(@Valid @RequestBody CouponRequestDTO request) { /* ... */ }

        @GetMapping
        public ResponseEntity<List<CouponResponseDTO>> getAllCoupons() { /* ... */ }

        @GetMapping("/{id}")
        public ResponseEntity<CouponResponseDTO> getCouponById(@PathVariable Long id) { /* ... */ }

        @PutMapping("/{id}")
        public ResponseEntity<CouponResponseDTO> updateCoupon(@PathVariable Long id, @Valid @RequestBody CouponRequestDTO request) { /* ... */ }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) { /* ... */ }

        @PostMapping("/applicable-coupons")
        public ResponseEntity<List<ApplicableCouponResponse>> getApplicableCoupons(@Valid @RequestBody Cart cart) { /* ... */ }

        @PostMapping("/apply-coupon/{id}")
        public ResponseEntity<Cart> applyCoupon(@PathVariable Long id, @Valid @RequestBody Cart cart) { /* ... */ }
    }
    ```

## 7. Exception Handling

*   **Custom Exceptions:**
    *   `CouponNotFoundException` (404 NOT FOUND)
    *   `CouponExpiredException` (400 BAD REQUEST)
    *   `CouponNotApplicableException` (400 BAD REQUEST)
    *   `InvalidCouponRequestException` (400 BAD REQUEST)
*   **`GlobalExceptionHandler.java` (`@ControllerAdvice`)**
    Handles custom exceptions and standard Spring validation errors (`MethodArgumentNotValidException`), returning consistent `ErrorResponse` DTOs.

## 8. Testing

*   **Unit Tests:** For `CouponService` and each `CouponStrategy` implementation, using JUnit 5 and Mockito.
*   **Integration Tests:** For `CouponController` using `@SpringBootTest` and `MockMvc`. Since persistence is in-memory, these tests will directly interact with the in-memory state managed by the service.

## 9. Documentation

*   **`README.md`:** Will contain implemented/unimplemented cases, limitations, and assumptions.
*   **No Springdoc OpenAPI:** As per "DO NOT use any unnecessary libs" and "KEEP IT as SIMPLE as POSSIBLE", explicit API documentation generation (like Swagger UI) will be omitted to keep the dependency footprint minimal. The API endpoints are clearly defined in the controller.

## 10. Mapper (Optional but Recommended for Cleanliness)

*   A simple mapper (e.g., using plain methods or MapStruct if allowed) to convert between Entities and DTOs. This keeps the service layer clean of mapping logic. For simplicity, direct field copying can be done in service methods initially.

## Simplified BxGy Logic Details

The `BxGyCouponStrategy` will implement the following:

1.  **Identify Buy/Get Products in Cart:**
    *   Create maps of `productId -> quantity` for `buyProducts` and `getProducts` from the coupon definition.
    *   Create a map of `productId -> CartItem` from the input `Cart` for quick lookup.

2.  **Determine Max Repetitions:**
    *   Calculate `maxBuyRepetitions`: For each `buyProduct` in the coupon, determine how many times its required quantity can be met by the cart's contents. The minimum of these values across all `buyProducts` is `maxBuyRepetitions`.
    *   Calculate `maxGetRepetitions`: For each `getProduct` in the coupon, determine how many times its required quantity can be met by the cart's contents. The minimum of these values across all `getProducts` is `maxGetRepetitions`.
    *   The actual repetitions will be `min(coupon.repetitionLimit, maxBuyRepetitions, maxGetRepetitions)`.

3.  **Apply Discount (`applyDiscount` method):**
    *   For each determined repetition:
        *   Iterate through the `getProducts` defined in the coupon.
        *   For each `getProduct`, find the corresponding `CartItem` in the cart.
        *   Mark the required `quantity` of that `CartItem` as free (by adjusting `totalDiscount` and `finalPrice`). Prioritize making the most expensive `getProducts` free if multiple options exist and the coupon allows choice (though current spec implies specific products).
        *   Crucially, ensure that the `quantity` of `getProducts` made free does not exceed the actual `quantity` available in the cart for that product.
        *   Also, decrement the available `buyProducts` quantities in the cart (conceptually) to prevent them from being used for further repetitions within the same coupon application.

This plan prioritizes a clear, maintainable structure with minimal external dependencies, directly addressing the core requirements of the task.
