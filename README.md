# Coupon Management API

This project implements a simplified Coupon Management API using Spring Boot. It supports various coupon types and provides endpoints for managing coupons and applying them to a shopping cart.

## Implemented Coupon Types

Based on the `implementation.md` document, the following coupon types have been implemented:

1.  **Cart-Wise Coupon (`CART_WISE`)**: Applies a discount to the entire cart if the cart's total original price meets a specified threshold.
    *   **Fields**: `threshold`, `discountPercentage`, `maxDiscountAmount` (optional cap).
2.  **Product-Wise Coupon (`PRODUCT_WISE`)**: Applies a discount to a specific product in the cart.
    *   **Fields**: `productId`, `discountPercentage` or `fixedDiscountAmount`, `minQuantity` (optional).
3.  **Buy X Get Y Coupon (`BXGY`)**: Offers free products when a certain quantity of other products is purchased.
    *   **Fields**: `buyProducts` (list of `BxGyProductDetail`), `getProducts` (list of `BxGyProductDetail`), `repetitionLimit`.

## Potential Future Coupon Types (Not Implemented Due to Time Constraints)

The scope of coupon types can be significantly expanded. Here are some examples of other coupon types that could be implemented:

*   **Category-Wise Coupon**: Discount applies to all products within a specific category.
*   **Shipping Discount Coupon**: Offers a discount on shipping costs (e.g., free shipping, percentage off shipping).
*   **First-Time Customer Coupon**: Exclusive discount for new users.
*   **Loyalty Point Redemption**: Allows users to redeem loyalty points for discounts.
*   **Minimum Purchase Quantity for Specific Product**: Discount applies if a minimum quantity of a specific product is purchased (similar to product-wise but with more complex rules).
*   **Bundle Discount**: Discount applies when a specific combination of products is purchased together.
*   **Referral Coupon**: Discount for both referrer and referee.
*   **Seasonal/Event-Based Coupons**: Coupons valid only during specific holidays or events.

## How to Run the Application

1.  **Prerequisites**:
    *   Java 17 LTS (OpenJDK recommended)
    *   Maven 3.8.9 or higher

2.  **Build the project**:
    ```bash
    mvn clean install
    ```

3.  **Run the application**:
    ```bash
    mvn spring-boot:run
    ```
    The application will start on `http://localhost:8080`.

## How to Test the Code

### Running Unit and Integration Tests

To run all tests (unit and integration), execute the following Maven command:

```bash
mvn test
```

This will execute all tests located in `src/test/java`.

### API Endpoints

You can use tools like Postman, Insomnia, or `curl` to interact with the API.

#### 1. Create a Coupon (Cart-Wise Example)

**Endpoint**: `POST /api/v1/coupons`
**Content-Type**: `application/json`

**Request Body**:
```json
{
  "code": "CART10OFF",
  "description": "10% off on orders over $50",
  "expirationDate": "2025-12-31",
  "threshold": 50.00,
  "discountPercentage": 10.0,
  "type": "CART_WISE"
}
```

#### 2. Create a Coupon (Product-Wise Example)

**Endpoint**: `POST /api/v1/coupons`
**Content-Type**: `application/json`

**Request Body**:
```json
{
  "code": "PROD20OFF",
  "description": "20% off Product ID 1",
  "expirationDate": "2025-12-31",
  "productId": 1,
  "discountPercentage": 20.0,
  "type": "PRODUCT_WISE"
}
```

#### 3. Create a Coupon (BxGy Example)

**Endpoint**: `POST /api/v1/coupons`
**Content-Type**: `application/json`

**Request Body**:
```json
{
  "code": "BUY2GET1",
  "description": "Buy 2 of Product 1, Get 1 of Product 2 Free",
  "expirationDate": "2025-12-31",
  "buyProducts": [
    {
      "productId": 1,
      "quantity": 2
    }
  ],
  "getProducts": [
    {
      "productId": 2,
      "quantity": 1
    }
  ],
  "repetitionLimit": 1,
  "type": "BXGY"
}
```

#### 4. Get All Coupons

**Endpoint**: `GET /api/v1/coupons`

#### 5. Get Coupon by ID

**Endpoint**: `GET /api/v1/coupons/{id}`

#### 6. Update a Coupon

**Endpoint**: `PUT /api/v1/coupons/{id}`
**Content-Type**: `application/json`

**Request Body (Example for Cart-Wise Update)**:
```json
{
  "code": "CART15OFF",
  "description": "15% off on orders over $75",
  "expirationDate": "2025-12-31",
  "threshold": 75.00,
  "discountPercentage": 15.0,
  "type": "CART_WISE"
}
```

#### 7. Delete a Coupon

**Endpoint**: `DELETE /api/v1/coupons/{id}`

#### 8. Get Applicable Coupons for a Cart

**Endpoint**: `POST /api/v1/coupons/applicable-coupons`
**Content-Type**: `application/json`

**Request Body**:
```json
{
  "items": [
    {
      "productId": 1,
      "quantity": 3,
      "price": 25.00
    },
    {
      "productId": 2,
      "quantity": 1,
      "price": 50.00
    }
  ]
}
```

#### 9. Apply a Coupon to a Cart

**Endpoint**: `POST /api/v1/coupons/apply-coupon/{id}`
**Content-Type**: `application/json`

**Request Body**:
```json
{
  "items": [
    {
      "productId": 1,
      "quantity": 3,
      "price": 25.00
    },
    {
      "productId": 2,
      "quantity": 1,
      "price": 50.00
    }
  ]
}
```
Replace `{id}` with the actual coupon ID.

## Test Coverage

The project includes comprehensive test coverage with **49 tests** across multiple test suites:

### Test Results (Java 17 LTS)
```
✅ Total Tests: 49
✅ Passed: 49
✅ Failed: 0
✅ Errors: 0
```

### Test Breakdown
- **11 Integration Tests** - Controller-level tests using MockMvc
- **15 Service Tests** - CouponService business logic tests
- **9 Product-Wise Strategy Tests** - Strategy pattern implementation tests
- **8 BxGy Strategy Tests** - Buy X Get Y coupon logic tests
- **6 Cart-Wise Strategy Tests** - Cart-level discount logic tests

### Running Tests

**Run all tests**:
```bash
mvn clean test
```

**Run specific test class**:
```bash
mvn test -Dtest=CouponServiceTest
```

**Run with coverage report** (if plugin configured):
```bash
mvn clean test jacoco:report
```

## Java Version Compatibility

### Current Version
- **Java 17 LTS** - Fully compatible and tested
- **Spring Boot 3.3.0** - Verified with Java 17
- **Lombok 1.18.20** - Compatible with Java 17

### Upgrade Notes
The project has been upgraded from Java 25 to Java 17 LTS. All code has been validated to work correctly with Java 17, including:
- ✅ All 49 tests passing
- ✅ Full compilation without errors
- ✅ Spring Boot packaging successful
- ✅ Polymorphic JSON deserialization working correctly

See `implementation.md` for detailed upgrade information.

## Project Structure

```
app_commerce/
├── src/
│   ├── main/
│   │   └── java/com/example/demo/
│   │       ├── controller/      # REST API endpoints
│   │       ├── service/         # Business logic
│   │       ├── strategy/        # Coupon strategy implementations
│   │       ├── model/           # Entity classes
│   │       ├── dto/             # Data transfer objects
│   │       └── exception/       # Custom exceptions
│   └── test/
│       └── java/com/example/demo/
│           ├── controller/      # Integration tests
│           ├── service/         # Unit tests
│           └── strategy/        # Strategy pattern tests
├── pom.xml                      # Maven configuration
├── README.md                    # This file
└── implementation.md            # Detailed implementation notes
```

## Dependencies

Key dependencies (from Spring Boot 3.3.0 BOM):
- **spring-boot-starter-web** - Web framework
- **spring-boot-starter-validation** - Bean validation
- **spring-boot-starter-actuator** - Monitoring and metrics
- **spring-boot-starter-test** - Testing framework (JUnit 5, Mockito)
- **lombok** - Boilerplate reduction
- **jackson-datatype-jsr310** - Java Time API support

## Known Limitations

1. **In-Memory Storage**: Coupons are stored in-memory using a `ConcurrentHashMap` and are lost when the application restarts. For production use, implement persistent storage (database).

2. **No Authentication/Authorization**: The API does not include user authentication or role-based access control. Implement Spring Security for production deployments.

3. **Limited Product Information**: Product details (price, availability) are not persistent. In a real application, integrate with a Product Service or database.

4. **No Async Processing**: The API is fully synchronous. Consider adding async/reactive APIs for high-traffic scenarios.

5. **No Rate Limiting**: API endpoints are not rate-limited. Implement rate limiting for production environments.

## Future Enhancements

- Database persistence (JPA/Hibernate)
- User authentication and authorization (Spring Security)
- API documentation (Swagger/Springdoc OpenAPI)
- Async endpoints using WebFlux
- Caching layer (Redis/Caffeine)
- Rate limiting and throttling
- Additional coupon types (Category-wise, Shipping, Loyalty, etc.)
- Batch coupon operations
- Coupon usage analytics and reporting

