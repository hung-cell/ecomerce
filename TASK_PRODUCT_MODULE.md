# 🛍️ TASK: IMPLEMENT PRODUCT MODULE

## 🎯 MỤC TIÊU

Xây dựng module Product với đầy đủ CRUD operations, search, filter và pagination theo đúng kiến trúc của base project.

---

## 📋 YÊU CẦU CHỨC NĂNG

### Core Features:
- ✅ CRUD Product (Create, Read, Update, Delete)
- ✅ Get all products với pagination và sorting
- ✅ Search product theo tên
- ✅ Filter theo category, price range, status
- ✅ Upload và quản lý product images (multiple images)
- ✅ Quản lý inventory (stock quantity)
- ✅ Product status management (ACTIVE, INACTIVE, OUT_OF_STOCK)

### Business Rules:
- Product name phải unique
- Price phải > 0
- Stock quantity phải >= 0
- Khi stock = 0, tự động chuyển status sang OUT_OF_STOCK
- Mỗi product có thể có nhiều images
- Product phải thuộc 1 category

---

## 📁 CẤU TRÚC FILES CẦN TẠO

```
src/main/java/org/example/ecomerce/module/product/
├── entity/
│   └── Product.java                    # Entity chính
│
├── dto/
│   ├── ProductRequest.java             # DTO cho Create/Update
│   ├── ProductResponse.java            # DTO cho Response
│   └── ProductSearchRequest.java       # DTO cho Search/Filter
│
├── repository/
│   └── ProductRepository.java          # JPA Repository
│
├── service/
│   └── ProductService.java             # Business Logic
│
└── controller/
    └── ProductController.java          # REST API Endpoints
```

---

## 🔨 CHI TIẾT IMPLEMENTATION

### TASK 1: TẠO ENTITY

**File:** `src/main/java/org/example/ecomerce/module/product/entity/Product.java`

**Yêu cầu:**
- [ ] Extend `BaseEntity` để có sẵn id, createdAt, updatedAt, etc.
- [ ] Sử dụng `@Entity` và `@Table(name = "products")`
- [ ] Sử dụng Lombok annotations: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`

**Fields cần có:**

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `name` | String | NOT NULL, UNIQUE, max 200 chars | Tên sản phẩm |
| `slug` | String | UNIQUE, max 250 chars | URL-friendly name |
| `description` | String | TEXT, max 5000 chars | Mô tả chi tiết |
| `shortDescription` | String | max 500 chars | Mô tả ngắn |
| `price` | BigDecimal | NOT NULL, min 0 | Giá bán |
| `originalPrice` | BigDecimal | min 0 | Giá gốc (để tính discount) |
| `stock` | Integer | NOT NULL, default 0 | Số lượng tồn kho |
| `sku` | String | UNIQUE, max 50 chars | Mã SKU |
| `categoryId` | Long | NOT NULL | ID của category |
| `brandId` | Long | | ID của brand (optional) |
| `images` | String | TEXT | JSON array of image URLs |
| `thumbnail` | String | max 500 chars | Main image URL |
| `status` | ProductStatus | NOT NULL, default ACTIVE | Trạng thái sản phẩm |
| `featured` | Boolean | default false | Sản phẩm nổi bật |
| `viewCount` | Long | default 0 | Số lượt xem |
| `soldCount` | Long | default 0 | Số lượng đã bán |

**Enum ProductStatus:**
- ACTIVE - Đang bán
- INACTIVE - Tạm ngưng
- OUT_OF_STOCK - Hết hàng
- DISCONTINUED - Ngừng kinh doanh

**Lưu ý:**
- Dùng `@Column` annotation để define constraints
- Dùng `@Enumerated(EnumType.STRING)` cho enum
- Dùng `BigDecimal` cho price (không dùng double/float)

---

### TASK 2: TẠO DTOs

#### 2.1. ProductRequest DTO

**File:** `src/main/java/org/example/ecomerce/module/product/dto/ProductRequest.java`

**Yêu cầu:**
- [ ] Dùng Lombok: `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
- [ ] Add validation annotations từ `jakarta.validation.constraints.*`

**Validations cần có:**

```
name:
  - @NotBlank(message = "Product name is required")
  - @Size(min = 3, max = 200)

description:
  - @Size(max = 5000, message = "Description too long")

price:
  - @NotNull(message = "Price is required")
  - @DecimalMin(value = "0.0", message = "Price must be positive")

stock:
  - @NotNull(message = "Stock is required")
  - @Min(value = 0, message = "Stock cannot be negative")

categoryId:
  - @NotNull(message = "Category is required")

sku:
  - @Size(max = 50)
```

**Fields cần có:**
- name, description, shortDescription
- price, originalPrice
- stock, sku
- categoryId, brandId
- images (List<String>), thumbnail
- featured

---

#### 2.2. ProductResponse DTO

**File:** `src/main/java/org/example/ecomerce/module/product/dto/ProductResponse.java`

**Yêu cầu:**
- [ ] Dùng `@JsonInclude(JsonInclude.Include.NON_NULL)`
- [ ] Bao gồm tất cả fields của Product
- [ ] Thêm các computed fields như: discountPercent, isInStock

**Fields cần có:**
- Tất cả fields từ Product entity
- `discountPercent` (Integer) - % discount nếu có originalPrice
- `isInStock` (Boolean) - true nếu stock > 0
- createdAt, updatedAt

---

#### 2.3. ProductSearchRequest DTO

**File:** `src/main/java/org/example/ecomerce/module/product/dto/ProductSearchRequest.java`

**Yêu cầu:**
- [ ] DTO cho search và filter parameters

**Fields:**
- `keyword` (String) - Search trong name và description
- `categoryId` (Long) - Filter by category
- `brandId` (Long) - Filter by brand
- `minPrice` (BigDecimal) - Minimum price
- `maxPrice` (BigDecimal) - Maximum price
- `status` (ProductStatus) - Filter by status
- `featured` (Boolean) - Filter featured products only
- `inStock` (Boolean) - Filter products in stock only

---

### TASK 3: TẠO REPOSITORY

**File:** `src/main/java/org/example/ecomerce/module/product/repository/ProductRepository.java`

**Yêu cầu:**
- [ ] Extend `JpaRepository<Product, Long>`
- [ ] Extend `JpaSpecificationExecutor<Product>` (cho dynamic query)
- [ ] Annotate với `@Repository`

**Custom Query Methods cần có:**

```java
// Find by slug
Optional<Product> findBySlug(String slug);

// Check exists by name
boolean existsByName(String name);

// Check exists by SKU
boolean existsBySku(String sku);

// Find by category
Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

// Find by status
Page<Product> findByStatus(Product.ProductStatus status, Pageable pageable);

// Find featured products
Page<Product> findByFeaturedTrue(Pageable pageable);

// Search by name (contains, ignore case)
Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

// Find by price range
Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

// Custom query - Find products in stock
@Query("SELECT p FROM Product p WHERE p.stock > 0")
Page<Product> findInStockProducts(Pageable pageable);
```

**Lưu ý:**
- Method name sẽ tự động tạo query (Spring Data JPA magic)
- Dùng `@Query` cho complex queries

---

### TASK 4: TẠO SERVICE

**File:** `src/main/java/org/example/ecomerce/module/product/service/ProductService.java`

**Yêu cầu:**
- [ ] Annotate: `@Service`, `@RequiredArgsConstructor`, `@Slf4j`
- [ ] Inject: `ProductRepository`, `ModelMapper`
- [ ] Implement tất cả business logic

**Methods cần implement:**

#### 4.1. Get All Products
```
PageResponse<ProductResponse> getAllProducts(Pageable pageable)
```
- Lấy tất cả products với pagination
- Map entity sang DTO
- Return PageResponse

#### 4.2. Get Product by ID
```
ProductResponse getProductById(Long id)
```
- Find product by ID
- Throw `ResourceNotFoundException` nếu không tìm thấy
- Map sang DTO và return

#### 4.3. Get Product by Slug
```
ProductResponse getProductBySlug(String slug)
```
- Find product by slug
- Tăng view count lên 1
- Throw exception nếu không tìm thấy

#### 4.4. Search Products
```
PageResponse<ProductResponse> searchProducts(ProductSearchRequest searchRequest, Pageable pageable)
```
- Implement search với multiple filters
- Dùng Specification để build dynamic query
- Support: keyword, category, brand, price range, status, featured, inStock

#### 4.5. Create Product
```
ProductResponse createProduct(ProductRequest request)
```
**Business logic:**
- Validate product name unique
- Validate SKU unique (nếu có)
- Generate slug from name (dùng StringUtil.toSlug)
- Set status = ACTIVE mặc định
- Nếu stock = 0, set status = OUT_OF_STOCK
- Save và return DTO

#### 4.6. Update Product
```
ProductResponse updateProduct(Long id, ProductRequest request)
```
**Business logic:**
- Find product by ID
- Validate name unique (trừ product hiện tại)
- Validate SKU unique (trừ product hiện tại)
- Update all fields
- Nếu stock = 0, auto set status = OUT_OF_STOCK
- Nếu stock > 0 và status = OUT_OF_STOCK, set ACTIVE
- Save và return DTO

#### 4.7. Delete Product
```
void deleteProduct(Long id)
```
- Soft delete: set deleted = true
- Hoặc hard delete: repository.delete()

#### 4.8. Update Stock
```
ProductResponse updateStock(Long id, Integer quantity)
```
- Cập nhật stock quantity
- Auto update status based on stock
- Validate quantity >= 0

#### 4.9. Get Featured Products
```
PageResponse<ProductResponse> getFeaturedProducts(Pageable pageable)
```
- Lấy products có featured = true

#### 4.10. Get Products by Category
```
PageResponse<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable)
```
- Lấy products theo category

**Business Rules trong Service:**
1. Tất cả read operations dùng `@Transactional(readOnly = true)`
2. Write operations dùng `@Transactional`
3. Log mọi operations: `log.info("...")`
4. Throw exception với clear message
5. Validate business rules trước khi save

---

### TASK 5: TẠO CONTROLLER

**File:** `src/main/java/org/example/ecomerce/module/product/controller/ProductController.java`

**Yêu cầu:**
- [ ] Annotate: `@RestController`, `@RequestMapping("/api/v1/products")`, `@RequiredArgsConstructor`
- [ ] Inject: `ProductService`
- [ ] Return `ResponseEntity<ApiResponse<...>>`

**Endpoints cần implement:**

#### 5.1. Get All Products
```
GET /api/v1/products
Query params: page, size, sortBy, sortDir
Response: ApiResponse<PageResponse<ProductResponse>>
```

#### 5.2. Search Products
```
GET /api/v1/products/search
Query params: keyword, categoryId, minPrice, maxPrice, status, featured, inStock
Response: ApiResponse<PageResponse<ProductResponse>>
```

#### 5.3. Get Product by ID
```
GET /api/v1/products/{id}
Response: ApiResponse<ProductResponse>
```

#### 5.4. Get Product by Slug
```
GET /api/v1/products/slug/{slug}
Response: ApiResponse<ProductResponse>
```

#### 5.5. Get Featured Products
```
GET /api/v1/products/featured
Response: ApiResponse<PageResponse<ProductResponse>>
```

#### 5.6. Get Products by Category
```
GET /api/v1/products/category/{categoryId}
Response: ApiResponse<PageResponse<ProductResponse>>
```

#### 5.7. Create Product
```
POST /api/v1/products
Body: ProductRequest (với @Valid)
Response: ApiResponse<ProductResponse>
Status: 201 CREATED
```

#### 5.8. Update Product
```
PUT /api/v1/products/{id}
Body: ProductRequest (với @Valid)
Response: ApiResponse<ProductResponse>
```

#### 5.9. Update Stock
```
PATCH /api/v1/products/{id}/stock
Body: { "quantity": 100 }
Response: ApiResponse<ProductResponse>
```

#### 5.10. Delete Product
```
DELETE /api/v1/products/{id}
Response: ApiResponse<Void>
```

**Lưu ý Controller:**
- Dùng `@Valid` cho request body
- Dùng `@RequestParam` cho query parameters với default values
- Dùng `@PathVariable` cho URL parameters
- Return proper HTTP status codes
- Wrap response trong `ApiResponse.success()`

---

## 🧪 TESTING CHECKLIST

### API Testing (dùng cURL hoặc Postman):

#### Create Product
```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15 Pro Max",
    "description": "Latest iPhone with A17 Pro chip",
    "shortDescription": "Premium smartphone",
    "price": 29990000,
    "originalPrice": 32990000,
    "stock": 50,
    "sku": "IP15PM-256-BLK",
    "categoryId": 1,
    "thumbnail": "https://example.com/iphone15.jpg",
    "images": ["https://example.com/img1.jpg", "https://example.com/img2.jpg"],
    "featured": true
  }'
```

#### Get All Products
```bash
curl "http://localhost:8080/api/v1/products?page=0&size=10&sortBy=createdAt&sortDir=desc"
```

#### Search Products
```bash
curl "http://localhost:8080/api/v1/products/search?keyword=iphone&minPrice=20000000&maxPrice=35000000&inStock=true"
```

#### Get Product by ID
```bash
curl http://localhost:8080/api/v1/products/1
```

#### Get Product by Slug
```bash
curl http://localhost:8080/api/v1/products/slug/iphone-15-pro-max
```

#### Update Product
```bash
curl -X PUT http://localhost:8080/api/v1/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15 Pro Max Updated",
    "description": "Updated description",
    "price": 28990000,
    "stock": 45,
    "categoryId": 1
  }'
```

#### Update Stock
```bash
curl -X PATCH http://localhost:8080/api/v1/products/1/stock \
  -H "Content-Type: application/json" \
  -d '{"quantity": 100}'
```

#### Delete Product
```bash
curl -X DELETE http://localhost:8080/api/v1/products/1
```

### Test Cases cần verify:

**Validation Tests:**
- [ ] Create product với name rỗng → 400 Bad Request
- [ ] Create product với price < 0 → 400 Bad Request
- [ ] Create product với stock < 0 → 400 Bad Request
- [ ] Create product với duplicate name → 400 Bad Request
- [ ] Create product với duplicate SKU → 400 Bad Request

**Business Logic Tests:**
- [ ] Create product với stock = 0 → status tự động = OUT_OF_STOCK
- [ ] Update product, set stock từ 0 → 10 → status tự động = ACTIVE
- [ ] Update product với duplicate name → 400 Bad Request
- [ ] Get product không tồn tại → 404 Not Found

**Search & Filter Tests:**
- [ ] Search với keyword → trả về products matching
- [ ] Filter by price range → trả về products trong khoảng giá
- [ ] Filter by category → trả về products của category đó
- [ ] Filter inStock=true → chỉ trả về products có stock > 0
- [ ] Filter featured=true → chỉ trả về featured products

**Pagination Tests:**
- [ ] Get all với page=0, size=5 → trả về 5 items
- [ ] Verify totalElements, totalPages, last, first fields
- [ ] Sort by price ascending → products sắp xếp theo giá tăng dần
- [ ] Sort by createdAt descending → products mới nhất trước

**Database Tests:**
- [ ] Check data trong MySQL qua phpMyAdmin
- [ ] Verify JPA Auditing fields (createdAt, updatedAt, createdBy)
- [ ] Verify slug được generate đúng (URL-friendly)
- [ ] Verify images được lưu dưới dạng JSON string

---

## 📊 ACCEPTANCE CRITERIA

### Hoàn thành khi:

1. **Code Structure:**
   - [ ] Tất cả files được tạo đúng cấu trúc
   - [ ] Follow naming conventions của project
   - [ ] Dùng đúng annotations (Lombok, JPA, Validation)

2. **Functionality:**
   - [ ] Tất cả 10 endpoints hoạt động đúng
   - [ ] Validation hoạt động đúng
   - [ ] Business rules được enforce
   - [ ] Exception handling đúng (dùng GlobalExceptionHandler)

3. **Testing:**
   - [ ] Test thành công tất cả endpoints
   - [ ] Test tất cả validation cases
   - [ ] Test search & filter với nhiều combinations
   - [ ] Verify data trong database

4. **Code Quality:**
   - [ ] Code clean, dễ đọc
   - [ ] Có logging đầy đủ
   - [ ] Không có duplicate code
   - [ ] Follow DRY principle

5. **Documentation:**
   - [ ] Tạo file `PRODUCT_API_EXAMPLES.md` với tất cả cURL examples
   - [ ] Comment cho các business logic phức tạp

---

## 💡 TIPS & BEST PRACTICES

### 1. Slug Generation
Dùng `StringUtil.toSlug()` để tạo slug từ product name:
```java
String slug = StringUtil.toSlug(product.getName());
```

### 2. Price với BigDecimal
```java
// Luôn dùng BigDecimal cho tiền
BigDecimal price = new BigDecimal("29990000");

// So sánh
if (price.compareTo(BigDecimal.ZERO) < 0) {
    throw new BadRequestException("Price must be positive");
}
```

### 3. Auto Update Status
```java
if (product.getStock() == 0) {
    product.setStatus(Product.ProductStatus.OUT_OF_STOCK);
} else if (product.getStock() > 0 && 
           product.getStatus() == Product.ProductStatus.OUT_OF_STOCK) {
    product.setStatus(Product.ProductStatus.ACTIVE);
}
```

### 4. Search với Specification (Advanced)
Nếu muốn implement search phức tạp, dùng JPA Specification:
```java
// Tham khảo: Spring Data JPA Specification
// Cho phép build dynamic query based on search criteria
```

### 5. Images Handling
Lưu images dưới dạng JSON string trong database:
```java
// Convert List<String> to JSON string
String imagesJson = objectMapper.writeValueAsString(images);

// Convert JSON string to List<String>
List<String> images = objectMapper.readValue(imagesJson, 
    new TypeReference<List<String>>(){});
```

### 6. Testing Strategy
- Test validation trước
- Test CRUD operations
- Test business logic cuối cùng
- Dùng phpMyAdmin để verify data

---


## 🎯 BONUS TASKS (Optional)

Nếu hoàn thành sớm:

1. **Implement Category Module:**
   - Category entity với relationship OneToMany với Product
   - CRUD operations cho Category

2. **Add Product Images Upload:**
   - Endpoint upload ảnh lên server
   - Save file vào thư mục uploads/
   - Return image URL

3. **Add Statistics Endpoints:**
   - GET /api/v1/products/statistics/count
   - GET /api/v1/products/statistics/total-value
   - GET /api/v1/products/statistics/out-of-stock

4. **Implement Soft Delete:**
   - Override delete để set deleted = true thay vì xóa
   - Filter out deleted products trong queries

---

## ✅ SUBMIT CHECKLIST

Trước khi báo hoàn thành:

- [ ] Code compile thành công (mvn clean install)
- [ ] Application chạy không lỗi
- [ ] Tất cả endpoints test thành công
- [ ] Data hiển thị đúng trong MySQL
- [ ] Không có TODO/FIXME trong code
- [ ] Code đã được format
- [ ] Commit với message rõ ràng: "feat: Implement Product module with CRUD operations"

---

