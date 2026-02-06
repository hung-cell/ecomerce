package org.example.ecomerce.module.product.repository;

import org.example.ecomerce.module.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Hàm này để check xem tên danh mục đã tồn tại chưa (Validate)
    boolean existsByName(String name);
}