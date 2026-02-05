package org.example.ecomerce.module.product.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 200, message = "Product name must be between 3 and 200 characters")
    private String name;

    @Size(max = 5000, message = "Description too long (max 5000 chars)")
    private String description;

    @Size(max = 500, message = "Short description too long (max 500 chars)")
    private String shortDescription;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal price;

    @DecimalMin(value = "0.0", message = "Original price must be positive")
    private BigDecimal originalPrice;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    @Size(max = 50, message = "SKU must be at most 50 characters")
    private String sku;

    @NotNull(message = "Category is required")
    private Long categoryId;

    private Long brandId;

    // Nhận List ảnh từ FE, Service sẽ lo việc convert sang JSON String
    private List<String> images;

    private String thumbnail;

    private Boolean featured;
}