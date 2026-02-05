package org.example.ecomerce.module.product.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.ecomerce.common.entity.BaseEntity;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "categories")
public class Category extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "slug", unique = true, length = 150)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // QUAN HỆ 1-N (Một danh mục có nhiều sản phẩm)
    // mappedBy = "category": Trỏ tới biến 'category' bên file Product
    // FetchType.LAZY: Khi lấy Category, KHÔNG tự động lấy list Product (Tránh làm nặng ram)
    // CascadeType.ALL: (Tùy chọn) Nếu xóa Category thì xóa luôn Product (Cẩn thận khi dùng)
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Product> products;
}