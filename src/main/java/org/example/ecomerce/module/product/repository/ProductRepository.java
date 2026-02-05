package org.example.ecomerce.module.product.repository;

import org.example.ecomerce.module.product.entity.Product;
import org.example.ecomerce.module.product.enums.ProductStatus; // Import Enum chúng ta vừa tạo
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    // --- 1. Basic Finders ---

    // Tìm theo Slug (cho trang chi tiết sản phẩm chuẩn SEO)
    Optional<Product> findBySlug(String slug);

    // Kiểm tra tồn tại (để Validate duplicate khi Create/Update)
    boolean existsByName(String name);
    boolean existsBySku(String sku);

    // --- 2. Filter Methods (Query Method Name Strategy) ---
    // Spring Data JPA sẽ tự dịch tên hàm thành câu SQL tương ứng

    // Lọc theo Category
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    // Lọc theo Status (ACTIVE, INACTIVE...)
    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    // Lọc sản phẩm nổi bật (Featured = true)
    Page<Product> findByFeaturedTrue(Pageable pageable);

    // Tìm kiếm theo tên (Có chứa keyword, không phân biệt hoa thường)
    // SQL: ... WHERE lower(name) LIKE lower(%keyword%)
    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    // Tìm theo khoảng giá
    // SQL: ... WHERE price BETWEEN minPrice AND maxPrice
    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    // --- 3. Custom Query (JPQL) ---

    // Tìm sản phẩm còn hàng (stock > 0)
    // @Query cho phép bạn viết câu truy vấn tùy chỉnh trên Entity
    @Query("SELECT p FROM Product p WHERE p.stock > 0")
    Page<Product> findInStockProducts(Pageable pageable);

    // 1. Đếm số lượng sản phẩm Out of Stock (Stock = 0)
    // Hibernate sẽ tự động thêm "AND deleted = false" nhờ @SQLRestriction
    long countByStock(int stock);

    // 2. Tính tổng giá trị tồn kho (Giá * Số lượng tồn)
    // COALESCE để xử lý trường hợp bảng rỗng thì trả về 0 thay vì null
    @Query("SELECT COALESCE(SUM(p.price * p.stock), 0) FROM Product p")
    BigDecimal getTotalInventoryValue();
}