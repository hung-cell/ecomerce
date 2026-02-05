package org.example.ecomerce.module.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecomerce.module.product.enums.ProductStatus; // Nhớ import Enum vừa tạo

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchRequest {

    // Tìm kiếm theo tên hoặc mô tả
    private String keyword;

    // Lọc theo danh mục hoặc thương hiệu
    private Long categoryId;
    private Long brandId;

    // Lọc theo khoảng giá (Từ ... Đến ...)
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    // Lọc trạng thái cụ thể (VD: Chỉ lấy hàng đang ACTIVE)
    private ProductStatus status;

    // Lọc sản phẩm nổi bật
    private Boolean featured;

    // Lọc sản phẩm còn hàng (stock > 0)
    private Boolean inStock;
}