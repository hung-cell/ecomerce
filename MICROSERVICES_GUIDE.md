# 🚀 Hướng Dẫn Xây Dựng Microservices: E-Commerce + Order Service

## 📋 Mục Lục
1. [Tổng Quan Kiến Trúc](#1-tổng-quan-kiến-trúc)
2. [Tạo Order Service Project](#2-tạo-order-service-project)
3. [Entity & Database Schema](#3-entity--database-schema)
4. [Giao Tiếp Giữa 2 Services](#4-giao-tiếp-giữa-2-services)
5. [Docker Compose Setup](#5-docker-compose-setup)
6. [API Documentation](#6-api-documentation)
7. [Testing](#7-testing)

---

## 1. Tổng Quan Kiến Trúc

### 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                           API GATEWAY                                │
│                        (Optional - Port 8000)                        │
└─────────────────────────────────────────────────────────────────────┘
                    │                           │
                    ▼                           ▼
┌─────────────────────────────┐   ┌─────────────────────────────┐
│      ECOMMERCE-SERVICE      │   │       ORDER-SERVICE         │
│        (Port 8080)          │   │        (Port 8081)          │
├─────────────────────────────┤   ├─────────────────────────────┤
│ • Product Management        │   │ • Order Management          │
│ • Category Management       │   │ • Order Items               │
│ • User Management           │   │ • Payment Processing        │
│ • Inventory Control         │   │ • Order History             │
└─────────────────────────────┘   └─────────────────────────────┘
            │                               │
            │    ◄── REST API / Feign ──►   │
            │                               │
            ▼                               ▼
    ┌───────────────┐               ┌───────────────┐
    │    MySQL      │               │    MySQL      │
    │ ecommerce_db  │               │   order_db    │
    └───────────────┘               └───────────────┘
```

### 📊 Luồng Nghiệp Vụ Đặt Hàng

```
┌──────┐      ┌──────────────┐      ┌─────────────────┐      ┌──────────────┐
│ User │ ──►  │ Order Service│ ──►  │ Ecommerce Service│ ──► │   Database   │
└──────┘      └──────────────┘      └─────────────────┘      └──────────────┘
   │                 │                       │                      │
   │  1. Tạo Order   │                       │                      │
   │ ───────────────►│                       │                      │
   │                 │  2. Check Product     │                      │
   │                 │ ─────────────────────►│                      │
   │                 │                       │  3. Query Product    │
   │                 │                       │ ────────────────────►│
   │                 │                       │◄────────────────────│
   │                 │  4. Product Info      │                      │
   │                 │◄─────────────────────│                      │
   │                 │                       │                      │
   │                 │  5. Reduce Stock      │                      │
   │                 │ ─────────────────────►│                      │
   │                 │                       │  6. Update Stock     │
   │                 │                       │ ────────────────────►│
   │                 │◄─────────────────────│◄────────────────────│
   │  7. Order Created                       │                      │
   │◄────────────────│                       │                      │
```

---

## 2. Tạo Order Service Project

### 📁 Cấu Trúc Thư Mục

```
order-service/
├── src/
│   ├── main/
│   │   ├── java/org/example/orderservice/
│   │   │   ├── OrderServiceApplication.java
│   │   │   ├── config/
│   │   │   │   ├── FeignConfig.java
│   │   │   │   └── WebClientConfig.java
│   │   │   ├── client/
│   │   │   │   └── ProductClient.java          # Feign Client
│   │   │   ├── module/order/
│   │   │   │   ├── entity/
│   │   │   │   │   ├── Order.java
│   │   │   │   │   └── OrderItem.java
│   │   │   │   ├── dto/
│   │   │   │   │   ├── OrderRequest.java
│   │   │   │   │   ├── OrderResponse.java
│   │   │   │   │   └── OrderItemRequest.java
│   │   │   │   ├── enums/
│   │   │   │   │   ├── OrderStatus.java
│   │   │   │   │   └── PaymentStatus.java
│   │   │   │   ├── repository/
│   │   │   │   │   ├── OrderRepository.java
│   │   │   │   │   └── OrderItemRepository.java
│   │   │   │   ├── service/
│   │   │   │   │   └── OrderService.java
│   │   │   │   └── controller/
│   │   │   │       └── OrderController.java
│   │   │   └── common/
│   │   │       ├── exception/
│   │   │       └── response/
│   │   └── resources/
│   │       └── application.yml
│   └── test/
├── pom.xml
└── Dockerfile
```

### 📦 pom.xml cho Order Service

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>

    <groupId>org.example</groupId>
    <artifactId>order-service</artifactId>
    <version>1.0.0</version>
    <name>order-service</name>

    <properties>
        <java.version>21</java.version>
        <spring-cloud.version>2023.0.0</spring-cloud.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- OpenFeign - Gọi REST API giữa các services -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>

        <!-- Database -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Swagger/OpenAPI Documentation -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>2.3.0</version>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

### ⚙️ application.yml

```yaml
server:
  port: 8081

spring:
  application:
    name: order-service

  datasource:
    url: jdbc:mysql://localhost:3306/order_db?createDatabaseIfNotExist=true
    username: root
    password: root

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

# Feign Client Config - Địa chỉ Ecommerce Service
ecommerce-service:
  url: http://localhost:8080

# Swagger UI
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
```

---

## 3. Entity & Database Schema

### 📊 Database Schema

```sql
-- order_db

CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(15,2) NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING',
    payment_status ENUM('PENDING', 'PAID', 'FAILED', 'REFUNDED') DEFAULT 'PENDING',
    shipping_address TEXT,
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(15,2) NOT NULL,
    total_price DECIMAL(15,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id)
);
```

### 🏷️ Order Entity

```java
package org.example.orderservice.module.order.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.orderservice.module.order.enums.OrderStatus;
import org.example.orderservice.module.order.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", unique = true, nullable = false, length = 50)
    private String orderNumber;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "shipping_address", columnDefinition = "TEXT")
    private String shippingAddress;

    @Column(columnDefinition = "TEXT")
    private String note;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper method để thêm item
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
}
```

### 🏷️ OrderItem Entity

```java
package org.example.orderservice.module.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalPrice;

    @PrePersist
    @PreUpdate
    protected void calculateTotalPrice() {
        if (unitPrice != null && quantity != null) {
            totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
}
```

### 🏷️ Enums

```java
// OrderStatus.java
package org.example.orderservice.module.order.enums;

public enum OrderStatus {
    PENDING,      // Chờ xác nhận
    CONFIRMED,    // Đã xác nhận
    PROCESSING,   // Đang xử lý
    SHIPPED,      // Đang giao
    DELIVERED,    // Đã giao
    CANCELLED     // Đã hủy
}

// PaymentStatus.java
package org.example.orderservice.module.order.enums;

public enum PaymentStatus {
    PENDING,   // Chờ thanh toán
    PAID,      // Đã thanh toán
    FAILED,    // Thanh toán thất bại
    REFUNDED   // Đã hoàn tiền
}
```

---

## 4. Giao Tiếp Giữa 2 Services

### 🔗 Có 3 cách phổ biến:

| Cách | Ưu điểm | Nhược điểm |
|------|---------|------------|
| **OpenFeign** | Đơn giản, declarative | Cần Spring Cloud |
| **RestTemplate** | Có sẵn trong Spring | Deprecated, blocking |
| **WebClient** | Non-blocking, reactive | Phức tạp hơn |

### ✅ Cách 1: OpenFeign (Khuyến nghị)

#### Bước 1: Enable Feign trong Main Application

```java
package org.example.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients  // Enable Feign
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
```

#### Bước 2: Tạo Feign Client Interface

```java
package org.example.orderservice.client;

import org.example.orderservice.client.dto.ProductResponse;
import org.example.orderservice.client.dto.StockUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
    name = "ecommerce-service",
    url = "${ecommerce-service.url}"  // http://localhost:8080
)
public interface ProductClient {

    // Lấy thông tin Product từ Ecommerce Service
    @GetMapping("/api/v1/products/{id}")
    ProductResponse getProductById(@PathVariable("id") Long id);

    // Kiểm tra Product có tồn tại không
    @GetMapping("/api/v1/products/{id}")
    ProductResponse checkProductExists(@PathVariable("id") Long id);

    // Cập nhật stock sau khi đặt hàng
    @PatchMapping("/api/v1/products/{id}/stock")
    ProductResponse updateStock(
        @PathVariable("id") Long id,
        @RequestParam("quantity") Integer quantity
    );
}
```

#### Bước 3: DTO cho Feign Client

```java
package org.example.orderservice.client.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String slug;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private String status;
    private Boolean featured;
}
```

#### Bước 4: Sử dụng trong OrderService

```java
package org.example.orderservice.module.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.orderservice.client.ProductClient;
import org.example.orderservice.client.dto.ProductResponse;
import org.example.orderservice.module.order.dto.*;
import org.example.orderservice.module.order.entity.*;
import org.example.orderservice.module.order.enums.*;
import org.example.orderservice.module.order.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;  // Inject Feign Client

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        log.info("Creating order for user: {}", request.getUserId());

        // 1. Tạo Order mới
        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .userId(request.getUserId())
                .shippingAddress(request.getShippingAddress())
                .note(request.getNote())
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        // 2. Xử lý từng item trong order
        for (OrderItemRequest itemRequest : request.getItems()) {

            // 2.1. Gọi Ecommerce Service để lấy thông tin Product
            ProductResponse product = productClient.getProductById(itemRequest.getProductId());

            // 2.2. Validate: Kiểm tra còn hàng không
            if (product.getStock() < itemRequest.getQuantity()) {
                throw new RuntimeException(
                    "Product " + product.getName() + " không đủ hàng. Còn: " + product.getStock()
                );
            }

            // 2.3. Tạo OrderItem
            OrderItem orderItem = OrderItem.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(product.getPrice())
                    .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())))
                    .build();

            order.addItem(orderItem);
            totalAmount = totalAmount.add(orderItem.getTotalPrice());

            // 2.4. Gọi Ecommerce Service để trừ stock
            int newStock = product.getStock() - itemRequest.getQuantity();
            productClient.updateStock(product.getId(), newStock);
            log.info("Updated stock for product {}: {} -> {}", product.getId(), product.getStock(), newStock);
        }

        // 3. Cập nhật tổng tiền và lưu
        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        log.info("Order created successfully: {}", savedOrder.getOrderNumber());
        return mapToResponse(savedOrder);
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private OrderResponse mapToResponse(Order order) {
        // Map entity to response DTO
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
```

### ✅ Cách 2: WebClient (Non-blocking)

```java
package org.example.orderservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${ecommerce-service.url}")
    private String ecommerceServiceUrl;

    @Bean
    public WebClient ecommerceWebClient() {
        return WebClient.builder()
                .baseUrl(ecommerceServiceUrl)
                .build();
    }
}
```

```java
// Sử dụng WebClient trong Service
@Service
@RequiredArgsConstructor
public class OrderServiceWithWebClient {

    private final WebClient ecommerceWebClient;

    public ProductResponse getProduct(Long productId) {
        return ecommerceWebClient.get()
                .uri("/api/v1/products/{id}", productId)
                .retrieve()
                .bodyToMono(ProductResponse.class)
                .block();  // Blocking call (hoặc dùng reactive)
    }
}
```

### ✅ Cách 3: RestTemplate (Legacy)

```java
package org.example.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

```java
// Sử dụng RestTemplate trong Service
@Service
@RequiredArgsConstructor
public class OrderServiceWithRestTemplate {

    private final RestTemplate restTemplate;

    @Value("${ecommerce-service.url}")
    private String ecommerceServiceUrl;

    public ProductResponse getProduct(Long productId) {
        String url = ecommerceServiceUrl + "/api/v1/products/" + productId;
        return restTemplate.getForObject(url, ProductResponse.class);
    }
}
```

---

## 5. Docker Compose Setup

### 🐳 docker-compose.yml (Chạy cả 2 services)

```yaml
version: '3.8'

services:
  # ========== DATABASES ==========
  mysql-ecommerce:
    image: mysql:8.0
    container_name: mysql-ecommerce
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: ecommerce_db
    ports:
      - "3306:3306"
    volumes:
      - mysql_ecommerce_data:/var/lib/mysql
    networks:
      - microservices-network

  mysql-order:
    image: mysql:8.0
    container_name: mysql-order
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: order_db
    ports:
      - "3307:3306"
    volumes:
      - mysql_order_data:/var/lib/mysql
    networks:
      - microservices-network

  # ========== SERVICES ==========
  ecommerce-service:
    build:
      context: ./ecomerce
      dockerfile: Dockerfile
    container_name: ecommerce-service
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql-ecommerce:3306/ecommerce_db
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root
    depends_on:
      - mysql-ecommerce
    networks:
      - microservices-network

  order-service:
    build:
      context: ./order-service
      dockerfile: Dockerfile
    container_name: order-service
    ports:
      - "8081:8081"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql-order:3306/order_db
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root
      ECOMMERCE_SERVICE_URL: http://ecommerce-service:8080
    depends_on:
      - mysql-order
      - ecommerce-service
    networks:
      - microservices-network

volumes:
  mysql_ecommerce_data:
  mysql_order_data:

networks:
  microservices-network:
    driver: bridge
```

### 📦 Dockerfile cho mỗi service

```dockerfile
# Dockerfile
FROM eclipse-temurin:21-jdk-alpine as build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 🚀 Chạy Docker Compose

```bash
# Build và chạy tất cả services
docker-compose up -d --build

# Xem logs
docker-compose logs -f

# Dừng tất cả
docker-compose down

# Dừng và xóa volumes
docker-compose down -v
```

---

## 6. API Documentation

### 📚 Order Service APIs

#### Base URL: `http://localhost:8081/api/v1/orders`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/` | Tạo đơn hàng mới |
| GET | `/` | Lấy danh sách đơn hàng |
| GET | `/{id}` | Lấy chi tiết đơn hàng |
| GET | `/number/{orderNumber}` | Tìm theo mã đơn hàng |
| GET | `/user/{userId}` | Lấy đơn hàng của user |
| PATCH | `/{id}/status` | Cập nhật trạng thái |
| PATCH | `/{id}/payment` | Cập nhật thanh toán |
| DELETE | `/{id}` | Hủy đơn hàng |

### 📝 Request/Response Examples

#### POST /api/v1/orders - Tạo đơn hàng

**Request:**
```json
{
  "userId": 1,
  "shippingAddress": "123 Nguyễn Văn A, Q.1, TP.HCM",
  "note": "Giao giờ hành chính",
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 3,
      "quantity": 1
    }
  ]
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Order created successfully",
  "data": {
    "id": 1,
    "orderNumber": "ORD-A1B2C3D4",
    "userId": 1,
    "totalAmount": 15500000.00,
    "status": "PENDING",
    "paymentStatus": "PENDING",
    "shippingAddress": "123 Nguyễn Văn A, Q.1, TP.HCM",
    "items": [
      {
        "productId": 1,
        "productName": "iPhone 15 Pro",
        "quantity": 2,
        "unitPrice": 5000000.00,
        "totalPrice": 10000000.00
      },
      {
        "productId": 3,
        "productName": "AirPods Pro",
        "quantity": 1,
        "unitPrice": 5500000.00,
        "totalPrice": 5500000.00
      }
    ],
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

#### GET /api/v1/orders/{id} - Chi tiết đơn hàng

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "orderNumber": "ORD-A1B2C3D4",
    "userId": 1,
    "totalAmount": 15500000.00,
    "status": "CONFIRMED",
    "paymentStatus": "PAID",
    "shippingAddress": "123 Nguyễn Văn A, Q.1, TP.HCM",
    "note": "Giao giờ hành chính",
    "items": [...],
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T11:00:00"
  }
}
```

#### PATCH /api/v1/orders/{id}/status - Cập nhật trạng thái

**Request:**
```json
{
  "status": "SHIPPED"
}
```

### 🔗 Swagger UI

Sau khi chạy Order Service, truy cập:
- **Swagger UI:** http://localhost:8081/swagger-ui.html
- **API Docs (JSON):** http://localhost:8081/api-docs

---

## 7. Testing

### 🧪 Test Flow Hoàn Chỉnh

```bash
# 1. Tạo Category (Ecommerce Service)
curl -X POST http://localhost:8080/api/v1/categories \
  -H "Content-Type: application/json" \
  -d '{"name": "Điện thoại", "description": "Smartphone"}'

# 2. Tạo Product (Ecommerce Service)
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15 Pro",
    "price": 25000000,
    "stock": 100,
    "categoryId": 1
  }'

# 3. Tạo Order (Order Service) - Sẽ gọi sang Ecommerce Service
curl -X POST http://localhost:8081/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "shippingAddress": "123 ABC Street",
    "items": [
      {"productId": 1, "quantity": 2}
    ]
  }'

# 4. Kiểm tra stock đã giảm (Ecommerce Service)
curl http://localhost:8080/api/v1/products/1
# stock: 100 -> 98
```

### 🧪 Unit Test với MockServer

```java
@SpringBootTest
@AutoConfigureMockMvc
class OrderServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductClient productClient;

    @Test
    void createOrder_Success() throws Exception {
        // Mock Feign Client response
        ProductResponse mockProduct = new ProductResponse();
        mockProduct.setId(1L);
        mockProduct.setName("Test Product");
        mockProduct.setPrice(new BigDecimal("100000"));
        mockProduct.setStock(50);

        when(productClient.getProductById(1L)).thenReturn(mockProduct);
        when(productClient.updateStock(eq(1L), anyInt())).thenReturn(mockProduct);

        // Test create order
        String requestBody = """
            {
              "userId": 1,
              "shippingAddress": "Test Address",
              "items": [{"productId": 1, "quantity": 2}]
            }
            """;

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.orderNumber").exists());
    }
}
```

---

## 📌 Tổng Kết

### Checklist triển khai Microservices:

- [ ] Tạo Order Service project với Spring Boot
- [ ] Cấu hình OpenFeign để gọi Ecommerce Service
- [ ] Tạo Entity: Order, OrderItem
- [ ] Implement OrderService với business logic
- [ ] Tạo OrderController với REST APIs
- [ ] Cấu hình Docker Compose cho cả 2 services
- [ ] Thêm Swagger documentation
- [ ] Viết Unit Tests
- [ ] Test integration giữa 2 services

### 🔗 Useful Links

- [Spring Cloud OpenFeign Docs](https://spring.io/projects/spring-cloud-openfeign)
- [Docker Compose Docs](https://docs.docker.com/compose/)
- [SpringDoc OpenAPI](https://springdoc.org/)

---

**Author:** Generated for E-Commerce Project
**Version:** 1.0
**Last Updated:** 2024

