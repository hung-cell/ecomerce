package org.example.ecomerce.module.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecomerce.module.product.enums.ProductStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Ẩn các trường null để API gọn nhẹ hơn
public class ProductResponse {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private String shortDescription;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private String sku;
    private Long categoryId;
    private Long brandId;
    private String thumbnail;

    // Lưu ý: Ở Entity là String (JSON), nhưng trả về FE thì đổi thành List cho dễ dùng
    private List<String> images;

    private ProductStatus status;
    private Boolean featured;
    private Long viewCount;
    private Long soldCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // --- COMPUTED FIELDS (Các "công thức" tính toán) ---

    // 1. Tự động tính % giảm giá
    public Integer getDiscountPercent() {
        if (originalPrice == null || price == null || originalPrice.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        // Công thức: (1 - price/originalPrice) * 100
        BigDecimal discount = BigDecimal.ONE.subtract(price.divide(originalPrice, 2, RoundingMode.HALF_UP));
        return discount.multiply(new BigDecimal("100")).intValue();
    }

    // 2. Tự động kiểm tra còn hàng hay không
    public Boolean getIsInStock() {
        return stock != null && stock > 0;
    }
}