package org.example.ecomerce.module.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ecomerce.module.product.dto.ProductRequest;
import org.example.ecomerce.module.product.dto.ProductResponse;
import org.example.ecomerce.module.product.dto.ProductSearchRequest;
import org.example.ecomerce.module.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // --- 1. GET ALL (with Pagination & Sort) ---
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    // --- 2. SEARCH & FILTER (Advanced) ---
    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean featured,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        // Build Search Request từ Query Params
        // Lưu ý: Đảm bảo bạn đã có class ProductSearchRequest với @Builder nhé
        ProductSearchRequest searchRequest = ProductSearchRequest.builder()
                .keyword(keyword)
                .categoryId(categoryId)
                .brandId(brandId)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .featured(featured)
                .inStock(inStock)
                .build();

        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        return ResponseEntity.ok(productService.searchProducts(searchRequest, pageable));
    }

    // --- 3. GET BY ID ---
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // --- 4. GET BY SLUG ---
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ProductResponse> getProductBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(productService.getProductBySlug(slug));
    }

    // --- 5. CREATE ---
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(request));
    }

    // --- 6. UPDATE ---
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request
    ) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    // --- 7. UPDATE STOCK ---
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductResponse> updateStock(
            @PathVariable Long id,
            @RequestParam Integer quantity
    ) {
        return ResponseEntity.ok(productService.updateStock(id, quantity));
    }

    // --- 8. DELETE ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // --- 9. UPLOAD IMAGE ---
    // Endpoint: POST http://localhost:8088/api/v1/products/uploads/{id}
    @PostMapping(value = "uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(
            @PathVariable("id") Long productId,
            // Sửa thành @RequestParam để rõ ràng hơn
            @RequestParam("file") MultipartFile file
    ) {
        try {
            ProductResponse response = productService.uploadImage(productId, file);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Đây là cách bắt lỗi nhanh, sau này nên dùng GlobalExceptionHandler
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // ==================== STATISTICS API ====================

    // GET /api/v1/products/statistics/count
    @GetMapping("/statistics/count")
    public ResponseEntity<Long> countProducts() {
        return ResponseEntity.ok(productService.countAllProducts());
    }

    // GET /api/v1/products/statistics/out-of-stock
    @GetMapping("/statistics/out-of-stock")
    public ResponseEntity<Long> countOutOfStock() {
        return ResponseEntity.ok(productService.countOutOfStock());
    }

    // GET /api/v1/products/statistics/total-value
    @GetMapping("/statistics/total-value")
    public ResponseEntity<BigDecimal> getTotalInventoryValue() {
        return ResponseEntity.ok(productService.getTotalInventoryValue());
    }
}