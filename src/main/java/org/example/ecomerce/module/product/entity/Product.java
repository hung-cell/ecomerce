package org.example.ecomerce.module.product.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.example.ecomerce.common.entity.BaseEntity;
import org.example.ecomerce.module.product.enums.ProductStatus;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "products")
// 1. Khi gọi repository.deleteById(id) -> Hibernate sẽ tự biến thành câu SQL UPDATE này
@SQLDelete(sql = "UPDATE products SET deleted = true WHERE id = ?")
// 2. Tự động thêm điều kiện "AND deleted = false" vào TẤT CẢ các câu query (Select, FindAll...)
@SQLRestriction("deleted = false")
public class Product extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 200)
    private String name;

    @Column(name = "slug", unique = true, length = 250)
    private String slug;

    @Column(name = "short_description", length = 500)
    private String shortDescription;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false)
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private BigDecimal price;

    @Column(name = "original_price")
    @Min(value = 0, message = "Original price must be greater than or equal to 0")
    private BigDecimal originalPrice;

    @Column(name = "stock", nullable = false)
    @Builder.Default
    private Integer stock = 0;

    @Column(name = "sku", unique = true, length = 50)
    private String sku;

    // ==========================================
    // 👇 PHẦN ĐÃ SỬA ĐỔI (QUAN TRỌNG NHẤT)
    // ==========================================

    // Thay vì lưu Long categoryId, ta lưu Object để tạo Foreign Key
    @ManyToOne(fetch = FetchType.LAZY) // Lazy: Chỉ lấy thông tin Category khi gọi getter (Tối ưu)
    @JoinColumn(name = "category_id", nullable = false) // Tên cột trong DB là category_id
    private Category category;

    // ==========================================

    @Column(name = "brand_id")
    private Long brandId;
    // Note: Sau này nên sửa thành @ManyToOne Brand brand tương tự Category

    @Column(name = "images", columnDefinition = "TEXT")
    private String images;

    @Column(name = "thumbnail", length = 500)
    private String thumbnail;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ProductStatus status = ProductStatus.ACTIVE;

    @Column(name = "featured")
    @Builder.Default
    private Boolean featured = false;

    @Column(name = "view_count")
    @Builder.Default
    private Long viewCount = 0L;

    @Column(name = "sold_count")
    @Builder.Default
    private Long soldCount = 0L;

    @Column(name = "deleted")
    @Builder.Default
    private boolean deleted = Boolean.FALSE;
}