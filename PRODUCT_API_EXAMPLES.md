PHẦN 1: TẠO FILE PRODUCT_API_EXAMPLES.md
Cách làm:

Tại thư mục gốc của dự án (cùng cấp với pom.xml), bạn chuột phải -> New File -> Đặt tên PRODUCT_API_EXAMPLES.md.

Copy toàn bộ nội dung bên dưới dán vào. File này dùng định dạng Markdown nên hiển thị trên Github/Gitlab rất đẹp.

Markdown

# Product API Documentation

Tài liệu hướng dẫn sử dụng API cho module Product.
**Base URL:** `http://localhost:8080/api/v1/products`

---

## 1. Quản lý sản phẩm (Basic CRUD)

### 1.1 Tạo sản phẩm mới (Create)
**Method:** `POST`
**URL:** `/`
```bash
curl --location 'http://localhost:8080/api/v1/products' \
--header 'Content-Type: application/json' \
--data '{
    "name": "iPhone 15 Pro Max Titanium",
    "description": "Phiên bản titan cao cấp nhất",
    "shortDescription": "Flagship 2024",
    "price": 30000000,
    "originalPrice": 35000000,
    "stock": 100,
    "sku": "IP15-TITAN",
    "categoryId": 1,
    "brandId": 1,
    "thumbnail": "thumb.jpg",
    "images": ["img1.jpg", "img2.jpg"],
    "featured": true
}'
```
1.2 Lấy danh sách (Pagination)
Method: GET URL: /?page=0&size=10

Bash
```
curl --location 'http://localhost:8080/api/v1/products?page=0&size=10&sortBy=createdAt&sortDir=desc'1.3 Xem chi tiết theo ID (Admin/Edit)
```
1.3 Xem chi tiết theo ID (Admin/Edit)
Method: GET URL: /{id}
```
Bash

curl --location 'http://localhost:8080/api/v1/products/1'
```

1.4 Xem chi tiết theo Slug (Client/SEO)
Method: GET URL: /slug/{slug}

Bash
```
curl --location 'http://localhost:8080/api/v1/products/slug/iphone-15-pro-max-titanium'
```

1.5 Cập nhật thông tin (Update Info)
Method: PUT URL: /{id}

Bash
```
curl --location --request PUT 'http://localhost:8080/api/v1/products/1' \
--header 'Content-Type: application/json' \
--data '{
    "name": "iPhone 15 Pro Max (Đã Giảm Giá)",
    "description": "Xả kho đón Tết",
    "shortDescription": "Giá cực sốc",
    "price": 25000000,
    "originalPrice": 35000000,
    "stock": 100,
    "sku": "IP15-TITAN",
    "categoryId": 1,
    "brandId": 1,
    "thumbnail": "thumb_new.jpg",
    "images": ["img_new.jpg"],
    "featured": true
}'
```
1.6 Xóa sản phẩm (Soft Delete/Hard Delete)
Method: DELETE URL: /{id}

Bash
```
curl --location --request DELETE 'http://localhost:8080/api/v1/products/1'
```

2. Nghiệp vụ đặc biệt (Business Operations)
2.1 Cập nhật tồn kho (Update Stock)
Logic: Nếu stock = 0, hệ thống tự động chuyển status sang OUT_OF_STOCK. Method: PATCH URL: /{id}/stock

Bash
```
curl --location --request PATCH 'http://localhost:8080/api/v1/products/1/stock?quantity=50'
```

3. Tìm kiếm & Bộ lọc (Search & Filter)
3.1 Tìm theo tên (Keyword)
Bash
```
curl --location 'http://localhost:8080/api/v1/products/search?keyword=iphone'
```
3.2 Bộ lọc nâng cao (Giá, Danh mục, Tình trạng)
Bash
```
curl --location 'http://localhost:8080/api/v1/products/search?minPrice=10000000&maxPrice=50000000&categoryId=1&inStock=true'
```

3.3 Lấy sản phẩm nổi bật (Featured)
Bash
```
curl --location 'http://localhost:8080/api/v1/products/search?featured=true'
```
---

### PHẦN 2: COMMENT CODE (BUSINESS LOGIC)

Bạn mở file `ProductService.java`. Đừng comment kiểu "dòng này gán biến a = 1". Hãy comment **TẠI SAO** bạn làm thế (Why) và **LOGIC** phức tạp hoạt động thế nào.

Dưới đây là các vị trí "đắt giá" cần comment:

#### 1. Tại logic tự động đổi trạng thái (Automation)
```java

// Logic tự động cập nhật trạng thái dựa trên tồn kho (Business Rule)
// 1. Nếu kho về 0 -> Chuyển ngay sang HẾT HÀNG để chặn người mua.
// 2. Nếu kho > 0 và đang HẾT HÀNG -> Mở lại ACTIVE để bán tiếp.
private void updateStatusBasedOnStock(Product product) {
    if (product.getStock() == 0) {
        product.setStatus(ProductStatus.OUT_OF_STOCK);
    } else if (product.getStock() > 0 && product.getStatus() == ProductStatus.OUT_OF_STOCK) {
        product.setStatus(ProductStatus.ACTIVE);
    }
}
2. Tại logic tìm kiếm động (Specification)
Java

public Page<ProductResponse> searchProducts(ProductSearchRequest request, Pageable pageable) {
    // Sử dụng Specification để tạo Dynamic Query (Truy vấn động)
    // Giúp kết hợp linh hoạt nhiều điều kiện lọc mà không cần viết nhiều hàm Repository
    Specification<Product> spec = (root, query, cb) -> {
        List<Predicate> predicates = new ArrayList<>();

        // 1. Tìm theo từ khóa (Tên hoặc Mô tả) - Không phân biệt hoa thường
        if (StringUtils.hasText(request.getKeyword())) {
            String likePattern = "%" + request.getKeyword().toLowerCase() + "%";
            predicates.add(cb.or(
                cb.like(cb.lower(root.get("name")), likePattern),
                cb.like(cb.lower(root.get("description")), likePattern)
            ));
        }

        // 2. Lọc theo khoảng giá (Range)
        if (request.getMinPrice() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("price"), request.getMinPrice()));
        }
        
        // ... (các điều kiện khác)

        return cb.and(predicates.toArray(new Predicate[0]));
    };

    return productRepository.findAll(spec, pageable).map(this::mapToResponse);
}
3. Tại hàm tạo Slug (SEO)
Java

// Tạo Slug chuẩn SEO từ tên sản phẩm
// Ví dụ: "iPhone 15 Pro Max" -> "iphone-15-pro-max"
// Dùng thư viện Normalizer để loại bỏ dấu tiếng Việt
private String generateSlug(String name) {
    // ... code của bạn ...
}
4. Tại đầu hàm Create/Update (Transactional)
Java

// @Transactional: Đảm bảo tính toàn vẹn dữ liệu (ACID).
// Nếu có lỗi xảy ra ở bất kỳ bước nào (ví dụ: lỗi lưu ảnh), 
// toàn bộ dữ liệu đã lưu trước đó sẽ bị Rollback (hoàn tác).
@Transactional
public ProductResponse createProduct(ProductRequest request) {
   // ...
}